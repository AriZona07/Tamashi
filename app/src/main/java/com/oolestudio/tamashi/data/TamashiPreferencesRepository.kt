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
private const val KEY_TAMASHI_XP = "tamashi_xp"
private const val KEY_TAMASHI_HATCHED = "tamashi_hatched"
private const val KEY_SEEN_OBJECTIVE_TUTORIAL = "seen_objective_tutorial"
private const val KEY_THEME_SETTING = "theme_setting"

/**
 * Opciones de tema disponibles en la app.
 */
enum class ThemeSetting {
    SYSTEM,
    LIGHT,
    DARK
}

/**
 * Representa los niveles de evolución del Tamashi
 */
enum class TamashiLevel(val levelNumber: Int, val displayName: String, val requiredXp: Int, val animationRes: String) {
    BABY(1, "Bebé", 0, "ajolote"),           // Recién nacido
    CHILD(2, "Niño", 10, "ajolote_child"),    // Primera evolución
    YOUNG(3, "Joven", 30, "ajolote_young"),   // Segunda evolución
    ADULT(4, "Adulto", 60, "ajolote_adult"),  // Tercera evolución
    MASTER(5, "Maestro", 100, "ajolote_master"); // Forma final

    companion object {
        /**
         * Obtiene el nivel correspondiente a una cantidad de XP
         */
        fun fromXp(xp: Int): TamashiLevel {
            return entries.lastOrNull { xp >= it.requiredXp } ?: BABY
        }

        /**
         * Obtiene el siguiente nivel (o null si ya está en el máximo)
         */
        fun getNextLevel(currentLevel: TamashiLevel): TamashiLevel? {
            val currentIndex = entries.indexOf(currentLevel)
            return if (currentIndex < entries.size - 1) entries[currentIndex + 1] else null
        }
    }
}

/**
 * Datos del estado actual del Tamashi
 */
data class TamashiStats(
    val xp: Int,
    val level: TamashiLevel,
    val xpForNextLevel: Int?, // XP necesario para el siguiente nivel (null si es máximo)
    val xpProgress: Float // Progreso hacia el siguiente nivel (0.0 - 1.0)
)

data class TamashiProfile(
    val name: String,
    val assetName: String
)

class TamashiPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val selectedFlow = MutableStateFlow(readSelectedTamashi())
    private val chosenFlow = MutableStateFlow(isTamashiChosen())
    private val xpFlow = MutableStateFlow(readXp())
    private val hatchedFlow = MutableStateFlow(isHatched())
    private val themeSettingFlow = MutableStateFlow(readThemeSetting())

    fun flowSelectedTamashi(): Flow<TamashiProfile?> = selectedFlow.asStateFlow()
    fun flowIsTamashiChosen(): Flow<Boolean> = chosenFlow.asStateFlow()
    fun flowXp(): Flow<Int> = xpFlow.asStateFlow()
    fun flowIsHatched(): Flow<Boolean> = hatchedFlow.asStateFlow()
    fun flowThemeSetting(): Flow<ThemeSetting> = themeSettingFlow.asStateFlow()

    /**
     * Obtiene el estado actual del Tamashi con su nivel y progreso
     */
    fun getTamashiStats(): TamashiStats {
        val xp = xpFlow.value
        val level = TamashiLevel.fromXp(xp)
        val nextLevel = TamashiLevel.getNextLevel(level)

        val xpProgress = if (nextLevel != null) {
            val xpInCurrentLevel = xp - level.requiredXp
            val xpNeededForNext = nextLevel.requiredXp - level.requiredXp
            (xpInCurrentLevel.toFloat() / xpNeededForNext.toFloat()).coerceIn(0f, 1f)
        } else {
            1f // Nivel máximo alcanzado
        }

        return TamashiStats(
            xp = xp,
            level = level,
            xpForNextLevel = nextLevel?.requiredXp,
            xpProgress = xpProgress
        )
    }

    private fun readSelectedTamashi(): TamashiProfile? {
        val name = prefs.getString(KEY_TAMASHI_NAME, null)
        val asset = prefs.getString(KEY_TAMASHI_ASSET, null)
        return if (name != null && asset != null) TamashiProfile(name, asset) else null
    }

    private fun isTamashiChosen(): Boolean = prefs.getBoolean(KEY_TAMASHI_CHOSEN, false)

    private fun readXp(): Int = prefs.getInt(KEY_TAMASHI_XP, 0)

    private fun isHatched(): Boolean = prefs.getBoolean(KEY_TAMASHI_HATCHED, false)

    private fun readThemeSetting(): ThemeSetting {
        val themeName = prefs.getString(KEY_THEME_SETTING, ThemeSetting.SYSTEM.name)
        return try {
            ThemeSetting.valueOf(themeName ?: ThemeSetting.SYSTEM.name)
        } catch (e: IllegalArgumentException) {
            ThemeSetting.SYSTEM
        }
    }

    fun hasSeenObjectiveTutorial(): Boolean = prefs.getBoolean(KEY_SEEN_OBJECTIVE_TUTORIAL, false)

    suspend fun setHasSeenObjectiveTutorial(seen: Boolean) {
        prefs.edit().putBoolean(KEY_SEEN_OBJECTIVE_TUTORIAL, seen).apply()
    }

    suspend fun setThemeSetting(theme: ThemeSetting) {
        prefs.edit().putString(KEY_THEME_SETTING, theme.name).apply()
        themeSettingFlow.value = theme
    }

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

    /**
     * Agrega XP al Tamashi (al completar un objetivo)
     * @return true si el Tamashi subió de nivel
     */
    suspend fun addXp(amount: Int = 1): Boolean {
        val previousLevel = TamashiLevel.fromXp(xpFlow.value)
        val newXp = xpFlow.value + amount
        prefs.edit().putInt(KEY_TAMASHI_XP, newXp).apply()
        xpFlow.value = newXp
        val newLevel = TamashiLevel.fromXp(newXp)
        return newLevel != previousLevel
    }

    /**
     * Resta XP al Tamashi (al descompletar un objetivo)
     * El XP no puede bajar de 0
     */
    suspend fun removeXp(amount: Int = 1) {
        val newXp = (xpFlow.value - amount).coerceAtLeast(0)
        prefs.edit().putInt(KEY_TAMASHI_XP, newXp).apply()
        xpFlow.value = newXp
    }

    /**
     * Marca que el Tamashi ha nacido
     */
    suspend fun setHatched(hatched: Boolean) {
        prefs.edit().putBoolean(KEY_TAMASHI_HATCHED, hatched).apply()
        hatchedFlow.value = hatched
    }
}
