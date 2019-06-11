package br.org.cesar.weatherapp.api

import br.org.cesar.weatherapp.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitManager {

    // Cria uma instância do Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    // Cria a implementação da interface WeateherServices
    fun weatherService() = retrofit.create(WeatherServices::class.java)

}