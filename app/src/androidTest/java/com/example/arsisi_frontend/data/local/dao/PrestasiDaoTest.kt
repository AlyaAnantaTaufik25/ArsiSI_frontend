package com.example.arsisi_frontend.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.arsisi_frontend.data.local.AppDatabase
import com.example.arsisi_frontend.data.model.Prestasi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Instrumented test untuk PrestasiDao
 * Test ini akan dijalankan di Android device/emulator
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
class PrestasiDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var prestasiDao: PrestasiDao

    @Before
    fun setup() {
        // Membuat in-memory database untuk testing
        // Data akan hilang setelah test selesai
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries() // Untuk testing, kita izinkan main thread
            .build()

        prestasiDao = database.prestasiDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertPrestasi_dan_getById_berhasil() = runBlocking {
        // Given - Buat prestasi baru
        val prestasi = Prestasi(
            id = 0, // Auto-generate
            userId = 1,
            nama = "Juara 1 Lomba Programming",
            jenis = "PRESTASI",
            tingkat = "Nasional",
            tahun = 2024,
            penyelenggara = "Kemendikbud",
            deskripsi = "Menjadi juara 1 dalam lomba programming nasional",
            filePath = "/path/to/certificate.pdf",
            tanggal = "2024-01-15"
        )

        // When - Insert prestasi
        val insertedId = prestasiDao.insertPrestasi(prestasi)

        // Then - Verifikasi ID yang dikembalikan
        assertTrue("ID harus lebih besar dari 0", insertedId > 0)

        // Get by ID
        val retrievedPrestasi = prestasiDao.getPrestasiById(insertedId.toInt()).first()
        
        // Verifikasi data
        assertNotNull("Prestasi harus ditemukan", retrievedPrestasi)
        assertEquals("Nama harus sama", prestasi.nama, retrievedPrestasi?.nama)
        assertEquals("Jenis harus sama", prestasi.jenis, retrievedPrestasi?.jenis)
        assertEquals("Tahun harus sama", prestasi.tahun, retrievedPrestasi?.tahun)
    }

    @Test
    fun insertAllPrestasi_dan_getAll_berhasil() = runBlocking {
        // Given - Buat beberapa prestasi
        val prestasiList = listOf(
            Prestasi(
                id = 0,
                userId = 1,
                nama = "Prestasi 1",
                jenis = "PRESTASI",
                tingkat = "Lokal",
                tahun = 2024,
                penyelenggara = "Organisasi A",
                deskripsi = "Deskripsi 1",
                tanggal = "2024-01-15"
            ),
            Prestasi(
                id = 0,
                userId = 1,
                nama = "Sertifikat 1",
                jenis = "SERTIFIKAT",
                tingkat = "Nasional",
                tahun = 2024,
                penyelenggara = "Organisasi B",
                deskripsi = "Deskripsi 2",
                tanggal = "2024-02-20"
            ),
            Prestasi(
                id = 0,
                userId = 1,
                nama = "Organisasi 1",
                jenis = "ORGANISASI",
                tingkat = "Internasional",
                tahun = 2024,
                penyelenggara = "Organisasi C",
                deskripsi = "Deskripsi 3",
                tanggal = "2024-03-25"
            )
        )

        // When - Insert semua prestasi
        prestasiDao.insertAllPrestasi(prestasiList)

        // Then - Get all prestasi
        val allPrestasi = prestasiDao.getAllPrestasi().first()
        
        // Verifikasi
        assertEquals("Harus ada 3 prestasi", 3, allPrestasi.size)
        // Verifikasi urutan (harus terurut berdasarkan tanggal DESC)
        assertTrue("Prestasi pertama harus yang tanggal terbaru", 
            allPrestasi[0].tanggal == "2024-03-25")
    }

    @Test
    fun updatePrestasi_berhasil() = runBlocking {
        // Given - Insert prestasi
        val prestasi = Prestasi(
            id = 0,
            userId = 1,
            nama = "Prestasi Awal",
            jenis = "PRESTASI",
            tingkat = "Lokal",
            tahun = 2024,
            penyelenggara = "Org A",
            deskripsi = "Deskripsi awal",
            tanggal = "2024-01-15"
        )
        val insertedId = prestasiDao.insertPrestasi(prestasi)

        // When - Update prestasi
        val updatedPrestasi = prestasi.copy(
            id = insertedId.toInt(),
            nama = "Prestasi Diupdate",
            deskripsi = "Deskripsi diupdate"
        )
        prestasiDao.updatePrestasi(updatedPrestasi)

        // Then - Verifikasi update
        val retrieved = prestasiDao.getPrestasiById(insertedId.toInt()).first()
        assertNotNull(retrieved)
        assertEquals("Nama harus terupdate", "Prestasi Diupdate", retrieved?.nama)
        assertEquals("Deskripsi harus terupdate", "Deskripsi diupdate", retrieved?.deskripsi)
    }

    @Test
    fun deletePrestasi_berhasil() = runBlocking {
        // Given - Insert prestasi
        val prestasi = Prestasi(
            id = 0,
            userId = 1,
            nama = "Prestasi untuk dihapus",
            jenis = "PRESTASI",
            tingkat = "Lokal",
            tahun = 2024,
            penyelenggara = "Org A",
            deskripsi = "Deskripsi",
            tanggal = "2024-01-15"
        )
        val insertedId = prestasiDao.insertPrestasi(prestasi)

        // When - Delete prestasi
        prestasiDao.deletePrestasiById(insertedId.toInt())

        // Then - Verifikasi sudah terhapus
        val retrieved = prestasiDao.getPrestasiById(insertedId.toInt()).first()
        assertNull("Prestasi harus sudah terhapus", retrieved)
    }

    @Test
    fun getPrestasiByJenis_berhasil() = runBlocking {
        // Given - Insert prestasi dengan jenis berbeda
        prestasiDao.insertAllPrestasi(listOf(
            Prestasi(0, 1, "Prestasi 1", "PRESTASI", "Lokal", 2024, "Org A", "Desc 1", tanggal = "2024-01-15"),
            Prestasi(0, 1, "Prestasi 2", "PRESTASI", "Nasional", 2024, "Org B", "Desc 2", tanggal = "2024-02-20"),
            Prestasi(0, 1, "Sertifikat 1", "SERTIFIKAT", "Lokal", 2024, "Org C", "Desc 3", tanggal = "2024-03-25")
        ))

        // When - Get prestasi by jenis
        val prestasiList = prestasiDao.getPrestasiByJenis("PRESTASI").first()

        // Then - Verifikasi
        assertEquals("Harus ada 2 prestasi dengan jenis PRESTASI", 2, prestasiList.size)
        assertTrue("Semua harus jenis PRESTASI", prestasiList.all { it.jenis == "PRESTASI" })
    }

    @Test
    fun searchPrestasi_berhasil() = runBlocking {
        // Given - Insert prestasi
        prestasiDao.insertAllPrestasi(listOf(
            Prestasi(0, 1, "Juara Programming", "PRESTASI", "Nasional", 2024, "Org A", "Lomba programming nasional", tanggal = "2024-01-15"),
            Prestasi(0, 1, "Sertifikat Android", "SERTIFIKAT", "Lokal", 2024, "Org B", "Sertifikat development Android", tanggal = "2024-02-20"),
            Prestasi(0, 1, "Organisasi Himpunan", "ORGANISASI", "Lokal", 2024, "Org C", "Anggota aktif himpunan", tanggal = "2024-03-25")
        ))

        // When - Search dengan keyword "Programming"
        val results = prestasiDao.searchPrestasi("Programming").first()

        // Then - Verifikasi
        assertEquals("Harus menemukan 1 hasil", 1, results.size)
        assertTrue("Hasil harus mengandung 'Programming'", 
            results[0].nama.contains("Programming", ignoreCase = true) ||
            results[0].deskripsi.contains("Programming", ignoreCase = true))
    }

    @Test
    fun getPrestasiCount_berhasil() = runBlocking {
        // Given - Insert beberapa prestasi
        prestasiDao.insertAllPrestasi(listOf(
            Prestasi(0, 1, "P1", "PRESTASI", "Lokal", 2024, "Org", "Desc", tanggal = "2024-01-15"),
            Prestasi(0, 1, "P2", "PRESTASI", "Lokal", 2024, "Org", "Desc", tanggal = "2024-02-20"),
            Prestasi(0, 1, "S1", "SERTIFIKAT", "Lokal", 2024, "Org", "Desc", tanggal = "2024-03-25")
        ))

        // When - Get count
        val totalCount = prestasiDao.getPrestasiCount().first()
        val prestasiCount = prestasiDao.getPrestasiCountByJenis("PRESTASI").first()

        // Then - Verifikasi
        assertEquals("Total harus 3", 3, totalCount)
        assertEquals("Prestasi harus 2", 2, prestasiCount)
    }

    @Test
    fun deleteAllPrestasi_berhasil() = runBlocking {
        // Given - Insert beberapa prestasi
        prestasiDao.insertAllPrestasi(listOf(
            Prestasi(0, 1, "P1", "PRESTASI", "Lokal", 2024, "Org", "Desc", tanggal = "2024-01-15"),
            Prestasi(0, 1, "P2", "PRESTASI", "Lokal", 2024, "Org", "Desc", tanggal = "2024-02-20")
        ))

        // When - Delete all
        prestasiDao.deleteAllPrestasi()

        // Then - Verifikasi semua terhapus
        val allPrestasi = prestasiDao.getAllPrestasi().first()
        assertEquals("Harus kosong", 0, allPrestasi.size)
    }
}

