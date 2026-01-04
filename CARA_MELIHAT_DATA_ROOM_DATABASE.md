# Cara Melihat Data Room Database

Ada beberapa cara untuk melihat data yang tersimpan di Room database:

## 1. Menggunakan Database Inspector di Android Studio (Paling Mudah)

### Langkah-langkah:

1. **Jalankan aplikasi di emulator atau device**
   - Pastikan aplikasi sudah berjalan dan database sudah dibuat

2. **Buka Database Inspector**
   - Di Android Studio, klik menu **View** → **Tool Windows** → **App Inspection**
   - Atau klik tab **App Inspection** di bagian bawah Android Studio
   - Pilih tab **Database Inspector**

3. **Pilih Database**
   - Di panel kiri, pilih device/emulator yang sedang berjalan
   - Pilih aplikasi Anda (com.example.arsisi_frontend)
   - Klik pada database **arsisi_database**

4. **Lihat Tabel**
   - Klik pada tabel **prestasi** untuk melihat semua data
   - Anda bisa melihat, mengedit, dan menghapus data langsung dari sini
   - Anda juga bisa menjalankan query SQL secara langsung

### Keuntungan Database Inspector:
- ✅ Visual interface yang mudah digunakan
- ✅ Bisa melihat data real-time
- ✅ Bisa mengedit data langsung
- ✅ Bisa menjalankan query SQL custom
- ✅ Tidak perlu menambahkan kode tambahan

---

## 2. Melihat Data melalui Logcat (Menggunakan Tombol di UI)

### Cara Menggunakan:

1. **Buka aplikasi di PrestasiScreen**
2. **Klik tombol ikon database** (ikon storage/box) di sebelah tombol "+"
   - Tombol ini berada di header orange, di sebelah kanan search bar
3. **Buka Logcat di Android Studio**
   - Klik tab **Logcat** di bagian bawah
   - Filter dengan tag: **RoomDB**
4. **Lihat output log**
   - Semua data akan ditampilkan dengan format yang rapi
   - Termasuk statistik per kategori

### Contoh Output Log:
```
═══════════════════════════════════════════════════════
📊 DATA ROOM DATABASE - PRESTASI
═══════════════════════════════════════════════════════
Total Data: 5
═══════════════════════════════════════════════════════

📄 Data #1:
   ID: 1
   User ID: 123
   Nama: Juara 1 Lomba Programming
   Jenis: PRESTASI
   Tingkat: Nasional
   Tahun: 2024
   ...
```

---

## 3. Menggunakan ADB Shell (Advanced)

Jika Anda ingin melihat database file secara langsung:

1. **Buka Terminal/Command Prompt**
2. **Jalankan perintah:**
   ```bash
   adb shell
   ```
3. **Masuk ke direktori database:**
   ```bash
   cd /data/data/com.example.arsisi_frontend/databases
   ```
4. **Lihat file database:**
   ```bash
   ls -la
   ```
5. **Jalankan SQLite:**
   ```bash
   sqlite3 arsisi_database
   ```
6. **Jalankan query:**
   ```sql
   SELECT * FROM prestasi;
   ```

---

## 4. Membuat Screen Khusus untuk Debug (Opsional)

Jika Anda ingin membuat screen khusus untuk melihat data, Anda bisa:

1. Buat composable baru yang menampilkan data dari Room database
2. Tambahkan navigasi ke screen tersebut
3. Tampilkan data dalam bentuk list atau card

Contoh kode:
```kotlin
@Composable
fun RoomDatabaseDebugScreen() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val prestasiDao = remember { database.prestasiDao() }
    
    val prestasiList by prestasiDao.getAllPrestasi()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    
    LazyColumn {
        items(prestasiList) { prestasi ->
            Card {
                Text("ID: ${prestasi.id}")
                Text("Nama: ${prestasi.nama}")
                Text("Jenis: ${prestasi.jenis}")
            }
        }
    }
}
```

---

## Tips:

- **Database Inspector** adalah cara termudah dan paling direkomendasikan
- **Logcat** berguna untuk debugging cepat tanpa membuka tool window
- Pastikan aplikasi sudah berjalan sebelum menggunakan Database Inspector
- Data akan muncul setelah ada operasi insert ke database

---

## Troubleshooting:

**Q: Database Inspector tidak muncul?**
A: Pastikan Anda menggunakan Android Studio versi terbaru (Arctic Fox atau lebih baru)

**Q: Database kosong padahal sudah insert data?**
A: Pastikan Anda sudah memanggil `prestasiDao.insertPrestasi()` dan menunggu operasi selesai

**Q: Tidak bisa melihat database di Database Inspector?**
A: Pastikan aplikasi sudah berjalan dan database sudah dibuat. Coba restart aplikasi.

