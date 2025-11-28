package com.nischint.app.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    
    // Change this based on testing environment
    private const val USE_MOCK = true  // Set to false when backend is ready
    
    // For Android Emulator connecting to localhost
    private const val EMULATOR_URL = "http://10.0.2.2:8000/"
    
    // For real device - replace with your ngrok URL
    private const val DEVICE_URL = "https://your-ngrok-url.ngrok.io/"
    
    // Current base URL
    private const val BASE_URL = EMULATOR_URL
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: NischintApiService = retrofit.create(NischintApiService::class.java)
    
    // Helper to check if we should use mock data
    fun shouldUseMock(): Boolean = USE_MOCK
}
