package br.org.cesar.weatherapp.api

import br.org.cesar.weatherapp.entity.FindResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServices {

    @GET("find")
    fun find(
        @Query("q")
        cityName: String,

        @Query("appid")
        appKey: String,

        @Query("units")
        units: String,

        @Query("lang")
        lang: String): Call<FindResult>



}