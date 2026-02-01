package com.oolestudio.tamashi.viewmodel

import com.oolestudio.tamashi.data.AuthRepository
import com.oolestudio.tamashi.data.UserState
import com.oolestudio.tamashi.util.isPasswordValid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de la lógica de autenticación y gestión de usuarios.
 * Actúa como intermediario entre la UI (pantallas de Login/Registro/Perfil) y el Repositorio de Datos.
 */
class AuthViewModel(private val repository: AuthRepository) {

    // Scope para lanzar corrutinas en el hilo principal (UI).
    private val viewModelScope = CoroutineScope(Dispatchers.Main)

    // Estado observable de la autenticación (Logueado/Deslogueado).
    // stateIn convierte el Flow frío del repositorio en un StateFlow caliente que la UI puede observar eficientemente.
    val authState: StateFlow<UserState> = repository.getAuthState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // Mantiene la suscripción activa 5s después de que la UI deje de observar (útil para rotaciones de pantalla).
            initialValue = UserState.LoggedOut
        )

    // Estados para manejar errores y mensajes en la UI.
    // Usamos MutableStateFlow internamente (para modificar) y StateFlow público (solo lectura).
    
    private val _loginError = MutableStateFlow<String?>(null)
    val loginError = _loginError.asStateFlow()
    
    private val _registrationError = MutableStateFlow<String?>(null)
    val registrationError = _registrationError.asStateFlow()
    
    private val _updateError = MutableStateFlow<String?>(null)
    val updateError = _updateError.asStateFlow()

    private val _updateSuccessMessage = MutableStateFlow<String?>(null)
    val updateSuccessMessage = _updateSuccessMessage.asStateFlow()

    /**
     * Intenta registrar un nuevo usuario.
     * Valida la contraseña antes de llamar al repositorio.
     */
    fun register(email: String, password: String, username: String) {
        if (!isPasswordValid(password)) {
            _registrationError.value = "La contraseña debe contener mínimo 6 caracteres, que incluyan mayúscula, minúscula, y número."
            return
        }
        viewModelScope.launch {
            repository.registerUser(email, password, username)
        }
    }

    /**
     * Inicia sesión con correo y contraseña.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            if (result.isFailure) {
                _loginError.value = "No se encontró el correo o contraseña. Intente de nuevo."
            }
        }
    }
    
    // Funciones para limpiar los mensajes de error/éxito una vez mostrados.
    fun clearLoginError() { _loginError.value = null }
    fun clearRegistrationError() { _registrationError.value = null }
    fun clearUpdateError() { _updateError.value = null }
    fun clearUpdateSuccessMessage() { _updateSuccessMessage.value = null }

    /**
     * Inicia sesión usando Google Sign-In.
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            repository.signInWithGoogle(idToken)
        }
    }

    /**
     * Cierra la sesión actual.
     */
    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    /**
     * Re-autentica al usuario (necesario para operaciones sensibles).
     * Ejecuta onSuccess si la contraseña es correcta, u onError si falla.
     */
    fun reauthenticate(password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.reauthenticate(password)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError("La contraseña es incorrecta. Intente de nuevo.")
            }
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            val result = repository.updateUsername(newUsername)
            if (result.isSuccess) {
                _updateSuccessMessage.value = "Nombre de usuario guardado exitosamente"
            } else {
                _updateError.value = "No se pudo guardar el nombre de usuario."
            }
        }
    }

    fun updateUserEmail(newEmail: String) {
        viewModelScope.launch {
            val result = repository.updateUserEmail(newEmail)
            if (result.isSuccess) {
                _updateSuccessMessage.value = "¡Correo actualizado! Revisa tu bandeja de entrada para verificar la nueva dirección."
            } else {
                _updateError.value = "No se pudo cambiar el correo. Es posible que ya esté en uso o no sea válido."
            }
        }
    }

    fun updateUserPassword(newPassword: String) {
        if (!isPasswordValid(newPassword)) {
            _updateError.value = "La contraseña debe contener mínimo 6 caracteres, que incluyan mayúscula, minúscula, y número."
            return
        }
        viewModelScope.launch {
            val result = repository.updateUserPassword(newPassword)
            if (result.isSuccess) {
                _updateSuccessMessage.value = "Contraseña guardada exitosamente"
            } else {
                _updateError.value = "Ocurrió un error al cambiar la contraseña."
            }
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            repository.deleteUser()
        }
    }
}