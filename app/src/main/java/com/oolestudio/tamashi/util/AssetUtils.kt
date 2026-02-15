package com.oolestudio.tamashi.util

import androidx.annotation.RawRes
import com.oolestudio.tamashi.R

object AssetUtils {
    /**
     * Devuelve el ID del recurso raw para la animación Lottie del Tamashi.
     * Esta función es necesaria para la pantalla de selección de Tamashi.
     */
    @RawRes
    fun getTamashiRawRes(assetName: String): Int {
        return when (assetName.lowercase()) {
            "asset_tamashi_bublu", "ajolote" -> R.raw.ajolote
            else -> R.raw.ajolote // Recurso por defecto para evitar errores.
        }
    }
}
