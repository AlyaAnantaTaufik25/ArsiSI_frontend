package com.example.arsisi_frontend.utils
object UserSession {
    private var mahasiswaId: Int = 1
    private var mahasiswaNama: String = "User Test"
    fun setUserData(id: Int, nama: String) {
        mahasiswaId = id
        mahasiswaNama = nama
    }
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