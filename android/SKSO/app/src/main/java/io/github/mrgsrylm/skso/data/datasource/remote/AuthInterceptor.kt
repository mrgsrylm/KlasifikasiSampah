package io.github.mrgsrylm.skso.data.datasource.remote

import android.util.Log
import io.github.mrgsrylm.skso.common.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()

        if (tokenManager.isTokenValid()) {
            val token = tokenManager.getToken()
            request.addHeader("Authorization", "Bearer $token")
        } else {
            Log.d("AUTH INTERCEPTOR", "Token is not valid")
            tokenManager.deleteToken()
        }

        return chain.proceed(request.build())
    }
}