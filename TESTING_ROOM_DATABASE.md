# Panduan Testing Room Database dan PrestasiDao

## 📋 Daftar Isi
1. [Cara Menjalankan Test](#cara-menjalankan-test)
2. [Jenis Test yang Tersedia](#jenis-test-yang-tersedia)
3. [Contoh Penggunaan di Kode](#contoh-penggunaan-di-kode)
4. [Manual Testing](#manual-testing)

---

## 🚀 Cara Menjalankan Test

### Opsi 1: Melalui Android Studio (Recommended)

1. **Buka Android Studio**
2. **Buka file test**: `app/src/androidTest/java/com/example/arsisi_frontend/data/local/dao/PrestasiDaoTest.kt`
3. **Klik kanan** pada class `PrestasiDaoTest` atau method test tertentu
4. **Pilih "Run 'PrestasiDaoTest'"** atau "Run 'methodName'"
5. **Pilih device/emulator** untuk menjalankan test
6. **Tunggu hasil test** di panel "Run"

### Opsi 2: Melalui Terminal/Command Line

```bash
# Test semua di androidTest
./gradlew connectedAndroidTest

# Test PrestasiDaoTest saja
./gradlew connectedAndroidTest --tests "com.example.arsisi_frontend.data.local.dao.PrestasiDaoTest"

# Test method tertentu
./gradlew connectedAndroidTest --tests "com.example.arsisi_frontend.data.local.dao.PrestasiDaoTest.insertPrestasi_dan_getById_berhasil"
```

**Catatan**: Pastikan device/emulator sudah terhubung sebelum menjalankan test.

---

## 📝 Jenis Test yang Tersedia

File `PrestasiDaoTest.kt` berisi test untuk:

### ✅ Test CRUD Operations
- ✅ `insertPrestasi_dan_getById_berhasil` - Test insert dan get by ID
- ✅ `insertAllPrestasi_dan_getAll_berhasil` - Test insert multiple dan get all
- ✅ `updatePrestasi_berhasil` - Test update prestasi
- ✅ `deletePrestasi_berhasil` - Test delete prestasi

### ✅ Test Query Operations
- ✅ `getPrestasiByJenis_berhasil` - Test filter by kategori/jenis
- ✅ `searchPrestasi_berhasil` - Test search functionality
- ✅ `getPrestasiCount_berhasil` - Test count operations
- ✅ `deleteAllPrestasi_berhasil` - Test delete all

---

## 💻 Contoh Penggunaan di Kode

### 1. Menggunakan PrestasiDao di ViewModel

```kotlin
class PrestasiViewModel(application: Application) : AndroidViewModel(application) {
    
    // Inisialisasi database dan DAO
    private val database = AppDatabase.getDatabase(application)
    private val prestasiDao = database.prestasiDao()
    
    // State untuk data dari database
    val localPrestasiList: StateFlow<List<Prestasi>> = prestasiDao
        .getAllPrestasi()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Fungsi untuk menyimpan prestasi ke database lokal
    fun savePrestasiToLocal(prestasi: Prestasi) {
        viewModelScope.launch {
            try {
                prestasiDao.insertPrestasi(prestasi)
                Log.d("PrestasiVM", "✅ Prestasi tersimpan ke database lokal")
            } catch (e: Exception) {
                Log.e("PrestasiVM", "❌ Error menyimpan: ${e.message}")
            }
        }
    }
    
    // Fungsi untuk sync data dari API ke database lokal
    fun syncPrestasiFromAPI() {
        viewModelScope.launch {
            try {
                // Ambil data dari API
                val response = apiService.getArsip("Bearer $token")
                if (response.isSuccessful) {
                    val arsipList = response.body()?.data ?: emptyList()
                    
                    // Convert Arsip ke Prestasi
                    val prestasiList = arsipList.map { arsip ->
                        Prestasi(
                            id = arsip.arsipId,
                            userId = arsip.mahasiswaId,
                            nama = arsip.judul,
                            jenis = arsip.kategori,
                            tingkat = "Lokal",
                            tahun = arsip.tanggal.take(4).toIntOrNull() ?: 2024,
                            penyelenggara = "Penyelenggara",
                            deskripsi = arsip.deskripsi,
                            filePath = arsip.filePath,
                            tanggal = arsip.tanggal
                        )
                    }
                    
                    // Simpan ke database lokal
                    prestasiDao.insertAllPrestasi(prestasiList)
                    Log.d("PrestasiVM", "✅ ${prestasiList.size} prestasi tersimpan ke database lokal")
                }
            } catch (e: Exception) {
                Log.e("PrestasiVM", "❌ Error sync: ${e.message}")
            }
        }
    }
    
    // Fungsi untuk mencari prestasi di database lokal
    fun searchLocalPrestasi(query: String) {
        viewModelScope.launch {
            prestasiDao.searchPrestasi(query)
                .collect { results ->
                    // Update UI dengan hasil pencarian
                    _searchResults.value = results
                }
        }
    }
}
```

### 2. Menggunakan PrestasiDao di Composable

```kotlin
@Composable
fun PrestasiListScreen() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val prestasiDao = remember { database.prestasiDao() }
    
    // Observe data dari database
    val prestasiList by prestasiDao.getAllPrestasi()
        .collectAsState(initial = emptyList())
    
    // Tampilkan list
    LazyColumn {
        items(prestasiList) { prestasi ->
            PrestasiItemCard(prestasi = prestasi)
        }
    }
}
```

---

## 🧪 Manual Testing

### Test 1: Insert dan Read Data

1. **Buka aplikasi** di device/emulator
2. **Buka Logcat** di Android Studio
3. **Filter log** dengan tag "PrestasiVM" atau "PrestasiDao"
4. **Jalankan fungsi** yang menggunakan database (misalnya sync data)
5. **Cek log** untuk melihat apakah data berhasil disimpan

### Test 2: Cek Database dengan Database Inspector

1. **Buka Android Studio**
2. **Menu**: View → Tool Windows → App Inspection
3. **Pilih device** yang sedang running aplikasi
4. **Pilih proses** aplikasi Anda
5. **Buka tab "Database Inspector"**
6. **Cari database**: `arsisi_database`
7. **Buka table**: `prestasi`
8. **Lihat data** yang tersimpan

### Test 3: Test Offline Mode

1. **Sync data** saat online (pastikan ada data di database)
2. **Matikan internet** (Airplane mode)
3. **Buka aplikasi** dan cek apakah data masih muncul
4. **Cari prestasi** dan cek apakah search masih bekerja
5. **Cek filter** berdasarkan kategori

---

## 📊 Hasil Test yang Diharapkan

Semua test harus **PASS** dengan hasil:

```
✅ insertPrestasi_dan_getById_berhasil - PASSED
✅ insertAllPrestasi_dan_getAll_berhasil - PASSED
✅ updatePrestasi_berhasil - PASSED
✅ deletePrestasi_berhasil - PASSED
✅ getPrestasiByJenis_berhasil - PASSED
✅ searchPrestasi_berhasil - PASSED
✅ getPrestasiCount_berhasil - PASSED
✅ deleteAllPrestasi_berhasil - PASSED
```

---

## 🔧 Troubleshooting

### Error: "cannot find required type"
- **Solusi**: Pastikan versi Room compiler sesuai dengan runtime (2.6.1)

### Error: "unexpected jvm signature V"
- **Solusi**: Pastikan semua method suspend memiliki return type yang jelas

### Test tidak berjalan
- **Solusi**: Pastikan device/emulator sudah terhubung dan aplikasi sudah di-install

### Database tidak muncul di Database Inspector
- **Solusi**: Pastikan aplikasi sudah di-debug (bukan release build)

---

## 📚 Referensi

- [Room Database Documentation](https://developer.android.com/training/data-storage/room)
- [Testing Room Database](https://developer.android.com/training/data-storage/room/testing-db)
- [Android Testing Guide](https://developer.android.com/training/testing)


