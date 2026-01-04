# 📸 Cara Menambahkan Foto ke Emulator Android Studio

Ada beberapa cara untuk menambahkan foto ke emulator Android Studio agar bisa di-test upload functionality:

## 🎯 **CARA 1: Drag & Drop (Paling Mudah)**

1. **Buka emulator** yang sedang berjalan
2. **Buka aplikasi "Files" atau "Gallery"** di emulator
3. **Drag & drop foto** dari komputer Anda langsung ke emulator
4. Foto akan otomatis tersimpan di folder Downloads atau Pictures

## 🎯 **CARA 2: Menggunakan ADB Push (Via Terminal)**

1. **Buka terminal/command prompt** di komputer Anda
2. **Pastikan emulator sedang berjalan**
3. **Jalankan perintah:**

```bash
# Push foto ke folder Downloads
adb push "C:\path\to\foto.jpg" /sdcard/Download/

# Atau push ke folder Pictures
adb push "C:\path\to\foto.jpg" /sdcard/Pictures/
```

**Contoh:**
```bash
adb push "C:\Users\ACER\Pictures\test.jpg" /sdcard/Download/
```

4. **Buka aplikasi Gallery atau Files** di emulator untuk melihat foto

## 🎯 **CARA 3: Menggunakan Camera Emulator**

1. **Buka aplikasi "Camera"** di emulator
2. **Ambil foto** menggunakan kamera virtual emulator
3. Foto akan otomatis tersimpan di folder Pictures

## 🎯 **CARA 4: Download dari Internet**

1. **Buka browser** di emulator (Chrome)
2. **Cari gambar** di Google Images
3. **Download gambar** tersebut
4. Gambar akan tersimpan di folder Downloads

## 🎯 **CARA 5: Menggunakan File Manager**

1. **Buka aplikasi "Files"** di emulator
2. **Pergi ke folder Downloads**
3. **Gunakan fitur "New" atau "Create"** untuk membuat file baru (jika tersedia)

## ✅ **Cek Foto Sudah Masuk**

Setelah menambahkan foto, cek dengan:
1. Buka aplikasi **Gallery** atau **Photos** di emulator
2. Atau buka aplikasi **Files** → **Downloads** atau **Pictures**
3. Foto seharusnya sudah muncul di sana

## 📝 **Catatan Penting**

- Foto yang di-upload harus dalam format: **JPG, PNG, atau PDF**
- Ukuran maksimal: **5 MB**
- Pastikan emulator memiliki storage yang cukup

---

**Tips:** Cara paling mudah adalah **Drag & Drop** langsung ke emulator! 🚀

