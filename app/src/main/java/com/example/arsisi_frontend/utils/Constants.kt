package com.example.arsisi_frontend.utils

object Constants {
    // API Configuration
    const val BASE_URL = "http://10.0.2.2:5000/api/" // Untuk emulator
    // const val BASE_URL = "http://192.168.1.100:3000/api/" // Untuk device fisik, ganti dengan IP komputer

    // SharedPreferences Keys
    const val PREFS_NAME = "arsisi_prefs"
    const val KEY_TOKEN = "token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_NIM = "user_nim"
    const val KEY_USER_NAME = "user_name"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_ANGKATAN = "user_angkatan"
    const val KEY_USER_JURUSAN = "user_jurusan"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    const val KEY_IS_FIRST_TIME = "is_first_time"


    // API Endpoints
    const val ENDPOINT_LOGIN = "auth/login"
    const val ENDPOINT_REGISTER = "auth/register"
    const val ENDPOINT_ARSIP = "arsip"
    const val ENDPOINT_ARSIP_BY_MAHASISWA = "arsip/mahasiswa/{mahasiswaId}"
    const val ENDPOINT_ARSIP_DETAIL = "arsip/{arsipId}"
    const val ENDPOINT_UPLOAD_FILE = "upload"

    // File Upload
    const val MAX_FILE_SIZE = 5 * 1024 * 1024 // 5 MB
    const val ALLOWED_IMAGE_TYPES = "image/jpeg,image/jpg,image/png"
    const val ALLOWED_DOC_TYPES = "application/pdf"

    // Database
    const val DATABASE_NAME = "arsisi_database"
    const val DATABASE_VERSION = 1

    // Date Formats
    const val DATE_FORMAT_DISPLAY = "dd MMMM yyyy"
    const val DATE_FORMAT_API = "yyyy-MM-dd"
    const val DATE_FORMAT_PICKER = "dd-MM-yyyy"

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "arsisi_channel"
    const val NOTIFICATION_CHANNEL_NAME = "ArsiSI Notifications"

    // Request Codes
    const val REQUEST_CAMERA_PERMISSION = 100
    const val REQUEST_STORAGE_PERMISSION = 101
    const val REQUEST_NOTIFICATION_PERMISSION = 102

    // Agenda Categories
    val AGENDA_CATEGORIES = listOf("Tugas", "Ujian", "Meeting", "Event", "Lainnya")

    // Agenda Priorities
    val AGENDA_PRIORITIES = listOf("Rendah", "Sedang", "Tinggi")

    // Prestasi Types
    val PRESTASI_TYPES = listOf("Akademik", "Non-Akademik", "Organisasi", "Lainnya")

    // Prestasi Levels
    val PRESTASI_LEVELS = listOf("Lokal", "Nasional", "Internasional")

    // Dokumen Types
    val DOKUMEN_TYPES = listOf("KHS", "Transkrip", "Sertifikat", "Surat", "Lainnya")

    // Kategori Arsip
    val KATEGORI_LIST = listOf("PRESTASI", "SERTIFIKAT", "ORGANISASI")

    // Search Delay
    const val SEARCH_DELAY_MS = 500L

    // Animation Duration
    const val ANIMATION_DURATION = 300

    // Error Messages
    const val ERROR_NETWORK = "Koneksi internet bermasalah"
    const val ERROR_SERVER = "Server sedang bermasalah"
    const val ERROR_UNKNOWN = "Terjadi kesalahan"
    const val ERROR_FILE_TOO_LARGE = "Ukuran file terlalu besar (max 5MB)"
    const val ERROR_INVALID_FILE_TYPE = "Tipe file tidak didukung"
    const val ERROR_EMPTY_FIELD = "Harap isi semua field"
    const val ERROR_GENERIC = "Terjadi kesalahan yang tidak diketahui."

    // Success Messages
    const val SUCCESS_CREATE = "Arsip berhasil ditambahkan"
    const val SUCCESS_UPDATE = "Arsip berhasil diperbarui"
    const val SUCCESS_DELETE = "Arsip berhasil dihapus"
    const val SUCCESS_DOWNLOAD = "File berhasil diunduh"


}