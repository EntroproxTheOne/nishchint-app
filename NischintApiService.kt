package com.nischint.app.data.api

import com.nischint.app.data.models.*
import retrofit2.http.*

interface NischintApiService {
    
    @GET("api/user/dashboard")
    suspend fun getDashboard(): DashboardResponse
    
    @POST("api/sms/simulate")
    suspend fun simulateSms(): SmsResponse
    
    @POST("api/onboarding/answer")
    suspend fun submitAnswer(@Body answer: AnswerRequest): QuestionResponse
    
    @GET("api/onboarding/horoscope")
    suspend fun getHoroscope(): HoroscopeResponse
    
    @GET("api/onboarding/questions")
    suspend fun getQuestions(): List<OnboardingQuestion>
}
