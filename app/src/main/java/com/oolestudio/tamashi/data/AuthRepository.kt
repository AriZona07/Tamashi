package com.oolestudio.tamashi.data

import kotlinx.coroutines.flow.Flow

/**
 * Representa los posibles estados de autenticación de un usuario.
 * Usamos una "sealed class" para asegurarnos de que solo puedan existir estos estados y ningún otro.
 */
sealed class UserState {
    /** El usuario no ha iniciado sesión. */
    object LoggedOut : UserState()

    /**
     * El usuario ha iniciado sesión.
     * @param userId El identificador único del usuario en Firebase.
     * @param providerIds La lista de métodos con los que el usuario puede iniciar sesión (ej. "password", "google.com").
     */
    data class LoggedIn(val userId: String, val providerIds: List<String>) : UserState()
}

/**
 * Esta es la interfaz de nuestro Repositorio de Autenticación.
 * Funciona como un "contrato" que define QUÉ operaciones se pueden hacer relacionadas con la autenticación.
 * No dice CÓMO se hacen, solo QUÉ se puede hacer. La implementación real (con Firebase) estará en otro archivo.
 * Esto nos permite tener una implementación "falsa" para previews y una "real" para la app.
 */
interface AuthRepository {

    /**
     * Obtiene el estado de autenticación del usuario en tiempo real.
     * Devuelve un Flow, que es como una "tubería" que emite un nuevo valor (UserState) cada vez que el estado cambia (login/logout).
     */
    fun getAuthState(): Flow<UserState>

    /**
     * Re-autentica al usuario usando su contraseña actual.
     * Es necesario para operaciones sensibles como cambiar la contraseña o el correo.
     */
    suspend fun reauthenticate(password: String): Result<Unit>

    /** Registra un nuevo usuario con correo, contraseña y nombre de usuario. */
    suspend fun registerUser(email: String, password: String, username: String): Result<Unit>

    /** Inicia sesión con correo y contraseña. */
    suspend fun login(email: String, password: String): Result<Unit>

    /** Inicia sesión usando un token de ID de Google. */
    suspend fun signInWithGoogle(idToken: String): Result<Unit>

    /** Cierra la sesión del usuario actual. */
    suspend fun logout()

    // --- Funciones para actualizar datos del usuario ---

    /** Actualiza el nombre de usuario en Firestore. */
    suspend fun updateUsername(newUsername: String): Result<Unit>

    /** Actualiza el correo del usuario en Firebase Authentication. */
    suspend fun updateUserEmail(newEmail: String): Result<Unit>

    /** Actualiza la contraseña del usuario en Firebase Authentication. */
    suspend fun updateUserPassword(newPassword: String): Result<Unit>

    /** Elimina la cuenta del usuario de Firestore y de Firebase Authentication. */
    suspend fun deleteUser(): Result<Unit>
}