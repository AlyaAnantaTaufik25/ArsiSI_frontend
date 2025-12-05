pluginManagement {
    // Tempat Gradle mencari Plugin (seperti Android Gradle Plugin)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

// Konfigurasi ini memastikan semua modul (termasuk :app) menggunakan repository yang sama
dependencyResolutionManagement {

    // **Baris penyebab peringatan @Incubating telah dihapus.**
    // Repositori yang didefinisikan di sini akan digunakan oleh semua proyek.

    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ArsiSI_frontend"
include(":app")