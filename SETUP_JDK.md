# 🔧 Panduan Setup JDK di Android Studio

## Masalah: "Project JDK is not defined"

Jika Anda melihat error "Project JDK is not defined" di Android Studio, ikuti langkah-langkah berikut:

## ✅ Solusi 1: Setup JDK melalui Android Studio UI

1. **Klik tombol "Setup SDK"** yang muncul di banner error (warna biru)
   - Atau buka: **File → Project Structure** (atau tekan `Ctrl+Alt+Shift+S`)

2. **Di tab "Project":**
   - **SDK:** Pilih **Android API 34** (atau versi terbaru yang terinstall)
   - **JDK:** Pilih **JDK 17** (atau **Embedded JDK** yang sudah include di Android Studio)
   - Jika tidak ada JDK 17, klik **Download JDK** dan pilih **JDK 17**

3. **Klik "Apply"** lalu **"OK"**

4. **Sync Project dengan Gradle:**
   - Klik ikon **elephant (Gradle)** di sidebar kanan
   - Atau: **File → Sync Project with Gradle Files**
   - Atau tekan: `Ctrl+Shift+O` (untuk sync)

## ✅ Solusi 2: Setup JDK Manual (jika Solusi 1 tidak bekerja)

1. **Buka File → Settings** (atau `Ctrl+Alt+S`)

2. **Navigasi ke:**
   - **Build, Execution, Deployment → Build Tools → Gradle**

3. **Di bagian "Gradle JDK":**
   - Pilih **JDK 17** atau **Embedded JDK**
   - Jika tidak ada, klik **Download JDK** → pilih **17**

4. **Klik "Apply"** lalu **"OK"**

5. **Sync Project dengan Gradle Files**

## ✅ Solusi 3: Verifikasi JDK sudah terinstall

1. **Cek apakah JDK sudah terinstall:**
   - Buka **File → Project Structure → SDK Location**
   - Pastikan **JDK location** menunjuk ke folder JDK yang benar
   - Contoh: `C:\Program Files\Android\Android Studio\jbr` (Embedded JDK)

2. **Jika JDK belum terinstall:**
   - Download JDK 17 dari: https://adoptium.net/ (pilih Java 17 LTS)
   - Install JDK
   - Set path di Android Studio

## ✅ Solusi 4: Invalidate Caches (jika masih error)

1. **File → Invalidate Caches...**
2. Pilih **"Invalidate and Restart"**
3. Tunggu Android Studio restart
4. Sync project lagi

## 📝 Catatan Penting

- Project ini menggunakan **Java 17** (sudah dikonfigurasi di `build.gradle.kts`)
- Android Studio biasanya sudah include **Embedded JDK** yang bisa langsung digunakan
- Pastikan **Gradle sync** berhasil setelah setup JDK

## 🔍 Verifikasi Setup Berhasil

Setelah setup, cek:
1. Banner error "Project JDK is not defined" **hilang**
2. Tidak ada error merah di file Kotlin
3. Build project berhasil (tidak ada error kompilasi)

---

**Jika masih ada masalah, coba:**
- Restart Android Studio
- Hapus folder `.idea` dan `.gradle` (jika perlu)
- Re-import project



