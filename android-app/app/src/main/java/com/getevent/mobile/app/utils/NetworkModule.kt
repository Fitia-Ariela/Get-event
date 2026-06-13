package com.getevent.mobile.app.utils

import com.getevent.mobile.BuildConfig
import com.getevent.mobile.app.api.AuthApi
import com.getevent.mobile.app.api.EventApi
import com.getevent.mobile.app.api.ReservationApi
import com.getevent.mobile.app.api.TicketApi
import com.getevent.mobile.app.api.UserApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    // Émulateur : 10.0.2.2 = localhost du PC. Appareil physique : api.base.url dans local.properties.
    private val BASE_URL = BuildConfig.API_BASE_URL

    private val authInterceptor = Interceptor { chain ->
        val token = SessionManager.token.value
        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrBlank()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()
        chain.proceed(request)
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val eventApi: EventApi by lazy { retrofit.create(EventApi::class.java) }
    val reservationApi: ReservationApi by lazy { retrofit.create(ReservationApi::class.java) }
    val ticketApi: TicketApi by lazy { retrofit.create(TicketApi::class.java) }
    val userApi: UserApi by lazy { retrofit.create(UserApi::class.java) }
}
