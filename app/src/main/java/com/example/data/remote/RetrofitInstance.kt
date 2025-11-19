package com.example.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
//Esta clase es un singletone (solo existe una instancia de esta clase)
object RetrofitInstance {

    //Definimos la url base de la API, a la que se harán las peticiones
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    //Esto configura el adaptador JSON con moshi que puede leer
    //data class, valores nulos y tipos específicos
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    //Instanciamos Retrofit que sirve para hacer todas las peticiones a la API
    //Con lazy hacemos que Retrofit solo se cree cuando se usa por primera vez, no antes
    val api: WeatherApiService by lazy {
        //Construimos retrofit
        Retrofit.Builder()
            //Le añadimos la url base de la API
            .baseUrl(BASE_URL)
            //Le añadimos un convertidor JSON (moshi)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            //Lo construimos
            .build()
            //Retrofit crea la clase real(WheatherApiService)
            //que hace las peticiones definidas en WeatherApiService
            .create(WeatherApiService::class.java)
    }
}
