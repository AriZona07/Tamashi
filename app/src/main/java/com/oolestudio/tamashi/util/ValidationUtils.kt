package com.oolestudio.tamashi.util

/**
 * Valida si una contraseña cumple con los requisitos de seguridad.
 *
 * Requisitos:
 * - Longitud mínima de 6 caracteres.
 * - Al menos una letra mayúscula.
 * - Al menos una letra minúscula.
 * - Al menos un número (dígito).
 *
 * @param password La contraseña a validar.
 * @return `true` si la contraseña es válida, `false` en caso contrario.
 */
fun isPasswordValid(password: String): Boolean {
    val hasMinLength = password.length >= 6
    val hasUppercase = password.any { it.isUpperCase() }
    val hasLowercase = password.any { it.isLowerCase() }
    val hasDigit = password.any { it.isDigit() }
    return hasMinLength && hasUppercase && hasLowercase && hasDigit
}