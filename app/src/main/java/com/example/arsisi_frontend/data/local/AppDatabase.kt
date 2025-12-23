package com.example.arsisi_frontend.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.arsisi_frontend.data.local.converter.AttachmentListConverter
import com.example.arsisi_frontend.data.local.dao.AkademikDao
import com.example.arsisi_frontend.data.local.entity.AkademikDocumentEntity

@Database(
    entities = [AkademikDocumentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(AttachmentListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun akademikDao(): AkademikDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "arsisi_database"
                )
                    .fallbackToDestructiveMigration() // Hapus data saat migration (untuk development)
                    .allowMainThreadQueries() // Untuk development/debugging (hapus di production)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
