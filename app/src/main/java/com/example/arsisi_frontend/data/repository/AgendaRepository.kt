package com.example.arsisi_frontend.data.repository
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.arsisi_frontend.data.model.Agenda as BackendAgenda
import com.example.arsisi_frontend.data.remote.ApiClient
import com.example.arsisi_frontend.data.local.AppDatabase
import com.example.arsisi_frontend.data.local.converter.AgendaConverter
import com.example.arsisi_frontend.utils.PreferencesManager
import kotlinx.coroutines.flow.first
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
data class Agenda(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val category: String,
    val priority: String,
    val filePath: String? = null
)
interface AgendaRepository {
    fun getAgendas(): Flow<List<Agenda>>
    suspend fun addAgenda(
        title: String,
        description: String,
        date: String,
        time: String,
        category: String,
        priority: String,
        reminderSetting: String? = null,
        fileUri: Uri? = null
    )
    suspend fun updateAgenda(
        agendaId: String,
        title: String,
        description: String,
        date: String,
        time: String,
        category: String,
        priority: String,
        reminderSetting: String? = null,
        fileUri: Uri? = null
    )
    suspend fun deleteAgenda(agendaId: String)
}
class RemoteAgendaRepositoryImpl(private val context: Context) : AgendaRepository {
    private val apiService = ApiClient.getApiService(context)
    private val preferencesManager = PreferencesManager(context)
    private val _agendas = MutableStateFlow<List<Agenda>>(emptyList())
    private val scope = CoroutineScope(Dispatchers.IO)
    private val database = AppDatabase.getDatabase(context)
    private val agendaDao = database.agendaDao()
    private suspend fun getAuthToken(): String {
        val token = preferencesManager.getToken() ?: throw Exception("Token tidak ditemukan, silakan login ulang")
        return "Bearer $token"
    }
    override fun getAgendas(): Flow<List<Agenda>> {
        scope.launch {
            loadAgendas()
        }
        return _agendas.asStateFlow()
    }
    suspend fun loadAgendas() {
        try {
            val token = getAuthToken()
            val response = apiService.getAllAgenda(token)
            if (response.isSuccessful) {
                val backendAgendas = response.body() ?: emptyList()
                val uiAgendas = backendAgendas.map { convertToUiAgenda(it) }
                _agendas.value = uiAgendas
                try {
                    val entities = AgendaConverter.toEntityList(backendAgendas)
                    agendaDao.insertAllAgendas(entities)
                    android.util.Log.d("AgendaRepository", "✅ Cached ${entities.size} agendas to Room Database")
                } catch (e: Exception) {
                    android.util.Log.e("AgendaRepository", "Error caching to Room: ${e.message}", e)
                }
                android.util.Log.d("AgendaRepository", "Loaded ${uiAgendas.size} agendas from backend")
            } else {
                android.util.Log.e("AgendaRepository", "Error loading agendas: ${response.message()}")
                try {
                    val cachedEntities = agendaDao.getAllAgendas()
                    android.util.Log.d("AgendaRepository", "Trying to load from Room Database (offline mode)")
                } catch (e: Exception) {
                    android.util.Log.e("AgendaRepository", "Error loading from Room: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("AgendaRepository", "Exception loading agendas: ${e.message}", e)
            e.printStackTrace()
        }
    }
    override suspend fun addAgenda(
        title: String,
        description: String,
        date: String,
        time: String,
        category: String,
        priority: String,
        reminderSetting: String?,
        fileUri: Uri?
    ) {
        try {
            val formattedDate = convertDateToBackendFormat(date)
            val formattedTime = convertTimeToBackendFormat(time)
            val formattedPriority = convertPriorityToBackendFormat(priority)
            val judulBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val deskripsiBody = description.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val tanggalBody = formattedDate.toRequestBody("text/plain".toMediaTypeOrNull())
            val waktuBody = formattedTime?.toRequestBody("text/plain".toMediaTypeOrNull())
            val kategoriBody = category.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val prioritasBody = formattedPriority.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val reminderBody = reminderSetting?.toRequestBody("text/plain".toMediaTypeOrNull())
            var filePart: MultipartBody.Part? = null
            if (fileUri != null) {
                try {
                    val file = uriToFile(context, fileUri)
                    val requestFile = file.asRequestBody(
                        context.contentResolver.getType(fileUri)?.toMediaTypeOrNull()
                            ?: "application/octet-stream".toMediaTypeOrNull()
                    )
                    filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                } catch (e: Exception) {
                    android.util.Log.e("AgendaRepository", "Error converting URI to file: ${e.message}", e)
                }
            }
            val token = getAuthToken()
            val response = apiService.createAgenda(
                token = token,
                judul = judulBody,
                deskripsi = deskripsiBody,
                tanggal = tanggalBody,
                waktu = waktuBody,
                kategori = kategoriBody,
                prioritas = prioritasBody,
                reminderSetting = reminderBody,
                file = filePart
            )
            if (response.isSuccessful) {
                android.util.Log.d("AgendaRepository", "Agenda berhasil ditambahkan ke backend")
                loadAgendas()
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("AgendaRepository", "Error response: $errorBody")
                throw Exception("Gagal menambahkan agenda (${response.code()}): ${errorBody ?: response.message()}")
            }
        } catch (e: Exception) {
            android.util.Log.e("AgendaRepository", "Exception saat menambahkan agenda: ${e.message}", e)
            throw e
        }
    }
    override suspend fun updateAgenda(
        agendaId: String,
        title: String,
        description: String,
        date: String,
        time: String,
        category: String,
        priority: String,
        reminderSetting: String?,
        fileUri: Uri?
    ) {
        try {
            val id = agendaId.toIntOrNull()
            if (id == null) {
                throw Exception("Invalid agenda ID: $agendaId")
            }
            val formattedDate = convertDateToBackendFormat(date)
            val formattedTime = convertTimeToBackendFormat(time)
            val formattedPriority = convertPriorityToBackendFormat(priority)
            val judulBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val deskripsiBody = description.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val tanggalBody = formattedDate.toRequestBody("text/plain".toMediaTypeOrNull())
            val waktuBody = formattedTime?.toRequestBody("text/plain".toMediaTypeOrNull())
            val kategoriBody = category.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val prioritasBody = formattedPriority.ifEmpty { null }?.toRequestBody("text/plain".toMediaTypeOrNull())
            val reminderBody = reminderSetting?.toRequestBody("text/plain".toMediaTypeOrNull())
            var filePart: MultipartBody.Part? = null
            if (fileUri != null) {
                try {
                    val file = uriToFile(context, fileUri)
                    val requestFile = file.asRequestBody(
                        context.contentResolver.getType(fileUri)?.toMediaTypeOrNull()
                            ?: "application/octet-stream".toMediaTypeOrNull()
                    )
                    filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
                } catch (e: Exception) {
                    android.util.Log.e("AgendaRepository", "Error converting URI to file: ${e.message}", e)
                }
            }
            val token = getAuthToken()
            val response = apiService.updateAgenda(
                token = token,
                id = id,
                judul = judulBody,
                deskripsi = deskripsiBody,
                tanggal = tanggalBody,
                waktu = waktuBody,
                kategori = kategoriBody,
                prioritas = prioritasBody,
                reminderSetting = reminderBody,
                file = filePart
            )
            if (response.isSuccessful) {
                android.util.Log.d("AgendaRepository", "Agenda berhasil diupdate di backend")
                loadAgendas()
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("AgendaRepository", "Error updating agenda: $errorBody")
                throw Exception("Gagal mengupdate agenda: ${errorBody ?: response.message()}")
            }
        } catch (e: Exception) {
            android.util.Log.e("AgendaRepository", "Exception saat mengupdate agenda: ${e.message}", e)
            throw e
        }
    }
    override suspend fun deleteAgenda(agendaId: String) {
        try {
            val id = agendaId.toIntOrNull()
            if (id != null) {
                val token = getAuthToken()
                val response = apiService.deleteAgenda(token, id)
                if (response.isSuccessful) {
                    android.util.Log.d("AgendaRepository", "Agenda berhasil dihapus dari backend")
                    try {
                        agendaDao.deleteAgendaById(id)
                        android.util.Log.d("AgendaRepository", "✅ Deleted agenda from Room Database")
                    } catch (e: Exception) {
                        android.util.Log.e("AgendaRepository", "Error deleting from Room: ${e.message}", e)
                    }
                    loadAgendas()
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("AgendaRepository", "Error deleting agenda: $errorBody")
                    throw Exception("Gagal menghapus agenda: ${errorBody ?: response.message()}")
                }
            } else {
                throw Exception("Invalid agenda ID: $agendaId")
            }
        } catch (e: Exception) {
            android.util.Log.e("AgendaRepository", "Exception saat menghapus agenda: ${e.message}", e)
            throw e
        }
    }
    private fun convertToUiAgenda(backendAgenda: BackendAgenda): Agenda {
        return Agenda(
            id = backendAgenda.id.toString(),
            title = backendAgenda.judul,
            description = backendAgenda.deskripsi ?: "",
            date = convertDateToUiFormat(backendAgenda.tanggal),
            time = convertTimeToUiFormat(backendAgenda.waktu),
            category = backendAgenda.kategori ?: "",
            priority = convertPriorityToUiFormat(backendAgenda.prioritas),
            filePath = backendAgenda.filePath
        )
    }
    private fun convertDateToBackendFormat(date: String): String {
        return try {
            android.util.Log.d("AgendaRepository", "Converting date to backend format: '$date'")
            val formats = listOf(
                SimpleDateFormat("d MMM yyyy", Locale.ENGLISH),
                SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH),
                SimpleDateFormat("d MMM", Locale.ENGLISH),
                SimpleDateFormat("dd MMM", Locale.ENGLISH),
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            )
            var parsedDate: Date? = null
            var usedFormat: SimpleDateFormat? = null
            for (format in formats) {
                try {
                    parsedDate = format.parse(date.trim())
                    if (parsedDate != null) {
                        usedFormat = format
                        break
                    }
                } catch (e: Exception) {
                    continue
                }
            }
            if (parsedDate != null) {
                val calendar = Calendar.getInstance()
                calendar.time = parsedDate
                if (usedFormat?.toPattern()?.contains("yyyy") != true) {
                    calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
                }
                val year = calendar.get(Calendar.YEAR)
                val month = String.format("%02d", calendar.get(Calendar.MONTH) + 1)
                val day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH))
                val result = "$year-$month-$day"
                android.util.Log.d("AgendaRepository", "Date converted to backend format: $date -> $result")
                result
            } else {
                android.util.Log.w("AgendaRepository", "Could not parse date: $date")
                date
            }
        } catch (e: Exception) {
            android.util.Log.e("AgendaRepository", "Error converting date: ${e.message}")
            date
        }
    }
    private fun convertDateToUiFormat(date: String): String {
        return try {
            android.util.Log.d("AgendaRepository", "Converting date from backend: '$date'")
            if (date.contains("T")) {
                val datePart = date.substringBefore("T")
                val parts = datePart.split("-")
                if (parts.size == 3) {
                    val year = parts[0].toIntOrNull() ?: return date
                    val month = parts[1].toIntOrNull() ?: return date
                    val day = parts[2].toIntOrNull() ?: return date
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month - 1)
                        set(Calendar.DAY_OF_MONTH, day)
                        set(Calendar.HOUR_OF_DAY, 12)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val outputFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
                    val result = outputFormat.format(calendar.time)
                    android.util.Log.d("AgendaRepository", "ISO Date conversion: $date -> $result (day=$day, month=$month, year=$year)")
                    result
                } else {
                    android.util.Log.w("AgendaRepository", "Invalid ISO date format: $date")
                    date
                }
            }
            else if (date.contains("-") && date.count { it == '-' } == 2) {
                val parts = date.split("-")
                if (parts.size == 3) {
                    val year = parts[0].toIntOrNull() ?: return date
                    val month = parts[1].toIntOrNull() ?: return date
                    val day = parts[2].toIntOrNull() ?: return date
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month - 1)
                        set(Calendar.DAY_OF_MONTH, day)
                        set(Calendar.HOUR_OF_DAY, 12)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val outputFormat = SimpleDateFormat("d MMM yyyy", Locale.ENGLISH)
                    val result = outputFormat.format(calendar.time)
                    android.util.Log.d("AgendaRepository", "Date conversion: $date -> $result (day=$day, month=$month, year=$year)")
                    result
                } else {
                    android.util.Log.w("AgendaRepository", "Invalid date format: $date")
                    date
                }
            } else {
                android.util.Log.w("AgendaRepository", "Unknown date format: $date")
                date
            }
        } catch (e: Exception) {
            android.util.Log.e("AgendaRepository", "Error converting date to UI format: ${e.message}")
            date
        }
    }
    private fun convertTimeToBackendFormat(time: String?): String? {
        return time?.let {
            try {
                if (it.contains(":")) {
                    val parts = it.split(":")
                    if (parts.size >= 2) {
                        "${parts[0].padStart(2, '0')}:${parts[1].padStart(2, '0')}"
                    } else {
                        it
                    }
                } else {
                    it
                }
            } catch (e: Exception) {
                it
            }
        }
    }
    private fun convertTimeToUiFormat(time: String?): String {
        return time ?: ""
    }
    private fun convertPriorityToBackendFormat(priority: String): String {
        return priority.trim()
    }
    private fun convertPriorityToUiFormat(priority: String?): String {
        return priority?.trim() ?: "Rendah"
    }
    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, getFileName(context, uri))
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return file
    }
    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex >= 0) {
                        result = it.getString(nameIndex)
                    }
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result ?: "file_${System.currentTimeMillis()}"
    }
}