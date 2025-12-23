package com.example.arsisi_frontend.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.arsisi_frontend.data.local.dao.MataKuliahDao
import com.example.arsisi_frontend.data.local.dao.TugasDao
import com.example.arsisi_frontend.data.local.entity.MataKuliahEntity
import com.example.arsisi_frontend.data.local.entity.TugasEntity

@Database(
    entities = [
        TugasEntity::class,
        MataKuliahEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun tugasDao(): TugasDao
    abstract fun mataKuliahDao(): MataKuliahDao

    companion object {

        // Contoh migration sederhana (sesuaikan dengan perubahanmu)
        // Kalau sebelumnya version 3, dan di 4 kamu nambah kolom updated_at TEXT di tugas_cache.
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Sesuaikan perintah SQL dengan perubahan schema yang kamu buat
                database.execSQL(
                    "ALTER TABLE tugas_cache ADD COLUMN updated_at TEXT"
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "arsisi_database"
                )
                    // Jangan reset database lagi setiap ganti version
                    // .fallbackToDestructiveMigration()   // ❌ hapus

                    // Tambahkan migration (kalau perlu, bisa lebih dari satu)
                    .addMigrations(MIGRATION_3_4)        // ✅ contoh

                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
