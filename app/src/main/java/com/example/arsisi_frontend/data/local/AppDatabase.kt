package com.example.arsisi_frontend.data.local
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.arsisi_frontend.data.local.converter.AgendaConverter
import com.example.arsisi_frontend.data.local.dao.AgendaDao
import com.example.arsisi_frontend.data.local.entity.AgendaEntity

@Database(
    entities = [
        AgendaEntity::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(AgendaConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun agendaDao(): AgendaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DATABASE_NAME = "arsisi_database"

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}