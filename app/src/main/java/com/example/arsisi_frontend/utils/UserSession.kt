package com.example.arsisi_frontend.utils

/**
 * UserSession - Helper untuk manage data user yang sedang login
 * 
 * Untuk saat ini menggunakan dummy value.
 * Nanti akan diisi dari modul login teman.
 */
object UserSession {
    private var mahasiswaId: Int = 1  // Dummy value untuk testing
    private var mahasiswaNama: String = "User Test"
    
    /**
     * Set data user setelah login berhasil
     * NANTI dipanggil dari modul login teman
     */
    fun setUserData(id: Int, nama: String) {
        mahasiswaId = id
        mahasiswaNama = nama
    }
    
    /**
     * Get mahasiswa_id untuk digunakan saat create/update dokumen
     */
    fun getMahasiswaId(): Int {
        return mahasiswaId
    }
    
    fun getMahasiswaNama(): String {
        return mahasiswaNama
    }
    
    fun isLoggedIn(): Boolean {
        return mahasiswaId != 0
    }
    
    fun clearSession() {
        mahasiswaId = 0
        mahasiswaNama = ""
    }
}
