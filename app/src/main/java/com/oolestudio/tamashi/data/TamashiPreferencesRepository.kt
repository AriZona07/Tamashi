package com.oolestudio.tamashi.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val PREFS_NAME = "tamashi_prefs"
private const val KEY_TAMASHI_NAME = "selected_tamashi_name"
private const val KEY_TAMASHI_ASSET = "selected_tamashi_asset"
private const val KEY_TAMASHI_CHOSEN = "is_tamashi_chosen"

data class TamashiProfile(
    val name: String,
    val assetName: String
)

class TamashiPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val selectedFlow = MutableStateFlow(readSelectedTamashi())
    private val chosenFlow = MutableStateFlow(isTamashiChosen())

    fun flowSelectedTamashi(): Flow<TamashiProfile?> = selectedFlow.asStateFlow()
    fun flowIsTamashiChosen(): Flow<Boolean> = chosenFlow.asStateFlow()

    private fun readSelectedTamashi(): TamashiProfile? {
        val name = prefs.getString(KEY_TAMASHI_NAME, null)
        val asset = prefs.getString(KEY_TAMASHI_ASSET, null)
        return if (name != null && asset != null) TamashiProfile(name, asset) else null
    }

    private fun isTamashiChosen(): Boolean = prefs.getBoolean(KEY_TAMASHI_CHOSEN, false)

    suspend fun setTamashi(profile: TamashiProfile) {
        prefs.edit()
            .putString(KEY_TAMASHI_NAME, profile.name)
            .putString(KEY_TAMASHI_ASSET, profile.assetName)
            .apply()
        selectedFlow.value = profile
    }

    suspend fun setIsTamashiChosen(chosen: Boolean) {
        prefs.edit().putBoolean(KEY_TAMASHI_CHOSEN, chosen).apply()
        chosenFlow.value = chosen
    }
}
