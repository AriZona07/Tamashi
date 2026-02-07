package com.oolestudio.tamashi.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Clase principal de la base de datos Room para la aplicación Tamashi.
 *
 * Define las entidades que componen la base de datos y proporciona acceso a los DAOs.
 * Utiliza el patrón Singleton para asegurar que solo exista una instancia de la base de datos.
 */
@Database(
    entities = [PlaylistEntity::class, ObjectiveEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TamashiDatabase : RoomDatabase() {

    /**
     * Proporciona acceso al DAO de Playlists.
     */
    abstract fun playlistDao(): PlaylistDao

    /**
     * Proporciona acceso al DAO de Objetivos.
     */
    abstract fun objectiveDao(): ObjectiveDao

    companion object {
        @Volatile
        private var INSTANCE: TamashiDatabase? = null

        /**
         * Obtiene la instancia única de la base de datos.
         * Si no existe, la crea de forma thread-safe.
         *
         * @param context El contexto de la aplicación.
         * @return La instancia de [TamashiDatabase].
         */
        fun getDatabase(context: Context): TamashiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TamashiDatabase::class.java,
                    "tamashi_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true) // En desarrollo, recrea la BD si hay cambios de esquema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

