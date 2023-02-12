package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.domain.PictureOfDay
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()

interface AsteroidApiService {
    @GET("/neo/rest/v1/feed")
    suspend fun getAsteroids(
        // Fetch asteroids from next seven days, Start Date = TODAY + 1, End Data = TODAY + 8,
        // So it only fetches asteroids from the next 7 days, excluding today's asteroids
        @Query("start_date") startDate: String = getTomorrowDate(),
        @Query("end_date") endDate: String = getNextEightDays(),
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): Response<String>

    @GET("/planetary/apod")
    suspend fun getPicture(
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): PictureOfDay
}

object AsteroidApi {
    val retrofitService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}
