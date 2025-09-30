/**
 * Room database holder for the app.
 * Connects EventDao and applies type converters if needed.
 */
package com.example.eventplanner.data

import android.content.Context
import androidx.room.*
import com.example.eventplanner.data.Converters

@Database(entities = [EventEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class EventDatabase : RoomDatabase() {
    // provides EventDao
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        // single instance for app
        private var INSTANCE: EventDatabase? = null
        // get database instance, create if null
        fun getDatabase(context: Context): EventDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    "event_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
