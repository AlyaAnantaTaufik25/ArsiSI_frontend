package com.example.arsisi_frontend.data.remote
import com.example.arsisi_frontend.utils.PreferencesManager
import okhttp3.Interceptor
import okhttp3.Response
class AuthInterceptor(
    private val prefsManager: PreferencesManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = prefsManager.getAuthToken()
        val newRequest = if (token.isNullOrEmpty()) {
            originalRequest.newBuilder().build()
        } else {
            originalRequest.newBuilder()
                .header("Authorization", token)
                .build()
        }
        return chain.proceed(newRequest)
    }
}