package com.example.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.WeatherRepository
import com.example.data.local.WeatherDatabase
import com.example.data.remote.ForecastItem
import com.example.data.remote.RetrofitInstance
import com.example.wheatherapp_full_stack.BuildConfig
import com.example.wheatherapp_full_stack.R
import com.example.wheatherapp_full_stack.databinding.ActivityMain2Binding
import java.text.SimpleDateFormat
import java.util.Locale
import android.widget.SearchView
import com.example.data.local.FavoriteCity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class Main_Activity2 : AppCompatActivity() {
    //Declaramos las variables binding(para acceder a elementos del layout sin findViewById)
    //y vm (ViewModel que mantiene la lógica separada de la UI)
    //como lateinit para inicializarlas más tarde
    private lateinit var binding: ActivityMain2Binding
    private lateinit var vm: WeatherViewModel

    //Repositorio para poder guardar ciudades favoritas desde esta pantalla
    private lateinit var repo: WeatherRepository

    //Configuración de la página
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Construye (infla) el xml y asocia todas las vistas para poder acceder a ellas
        binding = ActivityMain2Binding.inflate(layoutInflater)
        //Muestra el xml construido(inflado) en pantalla
        setContentView(binding.root)

        //Inicializa la BD y el repositorio igual que en MainActivity
        val db = Room.databaseBuilder(
            applicationContext,
            WeatherDatabase::class.java,
            "weather.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        //Creamos el repositorio que maneja datos locales y remotos
        repo = WeatherRepository(
            //dao nos permite leer y escribir en la bbdd local
            dao = db.dao(),
            //dao para la tabla de ciudades favoritas
            favoriteDao = db.favoriteCityDao(),
            //api para acceder a OpenWeatherMap
            api = RetrofitInstance.api,
            apiKey = BuildConfig.OPENWEATHER_API_KEY
        )

        //Creamos un factory para crear el viewmodel
        val factory = WeatherViewModelFactory(repo)
        //Se crea tu viewModelProvider con el que puedes gestionar el ciclo de vida del ViewModel
        vm = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        //Recupera la latitud y longitud del intent del MainActivity
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lon = intent.getDoubleExtra("lon", 0.0)

        //Pide el pronóstico de 5 días según ubicación
        vm.loadForecast(lat, lon)

        //Observa y muestra los datos en la UI
        setupForecastObservers()

        //Configuración para la searchView de ciudades
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            //Función para cuando se utiliza la barra de buscar
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Guardamos el texto que ha escrito el usuario y le quitamos espacios si hay
                val ciudad = query?.trim()
                //Si la variable de ciudad no está vacía y no es nula
                if (!ciudad.isNullOrEmpty()) {
                    //Quitamos el foco lo antes posible para evitar dobles envíos
                    binding.searchView.clearFocus()
                    //Iniciamos una tarea en segundo plano para no bloquear la app
                    lifecycleScope.launch {
                        try {
                            //Llamamos a la API buscando por nombre de ciudad para conseguir lat/lon reales
                            val response = RetrofitInstance.api.getCurrentWeatherByCity(
                                //Nombre de la ciudad
                                ciudad,
                                //APIKEY
                                BuildConfig.OPENWEATHER_API_KEY
                            )
                            //Si la respuesta es correcta y contiene datos
                            if (response.isSuccessful && response.body() != null) {
                                val body = response.body()!!
                                //Guardas la latitud y la longitud
                                val cityLat = body.coord.lat
                                val cityLon = body.coord.lon

                                //Comprobamos si la ciudad ya está en favoritos
                                val exists = repo.isFavoriteCity(body.city)
                                //Si no existe
                                if (!exists) {
                                    //Guardamos en la bbdd de Room la ciudad con su nombre y coordenadas correctas
                                    repo.saveFavoriteCity(
                                        FavoriteCity(
                                            name = body.city,
                                            lat = cityLat,
                                            lon = cityLon
                                        )
                                    )
                                    //Mostramos mensaje de que la ciudad se ha guardado
                                    android.widget.Toast.makeText(
                                        this@Main_Activity2,
                                        "Ciudad guardada: ${body.city}",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // Mensaje si la ciudad ya está guardada
                                    android.widget.Toast.makeText(
                                        this@Main_Activity2,
                                        "Esa ciudad ya está en favoritas.",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                                //Limpia barra de búsqueda
                                binding.searchView.setQuery("", false)
                                //Quita el foco (la cierras) para nuevas búsquedas
                                binding.searchView.clearFocus()
                            } else {
                                //Si la api no devuelve datos (la ciudad no existe)
                                //Se manda un mensaje avisando de que la ciudad no existe
                                android.widget.Toast.makeText(
                                    this@Main_Activity2,
                                    "No se ha encontrado esa ciudad.",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            //Si ocurre algún error (por ejemplo de conexión)
                            //También lanzamos un mensaje informando
                            android.widget.Toast.makeText(
                                this@Main_Activity2,
                                "Error de red o ciudad no encontrada.",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    //Hemos gestionado la búsqueda correctamente
                    return true
                } else {
                    //Si el usuario no escribe nada y pulsa buscar, cierras la busqueda
                    binding.searchView.clearFocus()
                    //No había nada útil que procesar
                    return false
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //Si quisiera que la searchView reaccionara mientras se escribe, se haría aquí
                return false
            }
        })

        //Volver a la página de inicio al pulsar el botón de inicio
        binding.botonInicio.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            //Si quieres volver usando la misma ciudad del forecast actual:
            //Nombre de la ciudad
            intent.putExtra("city_name", "")
            //Coordenadas
            intent.putExtra("city_lat", lat)
            intent.putExtra("city_lon", lon)
            //Abrimos la pantalla
            startActivity(intent)
        }

        //Ir a la página de ciudades favoritas al pulsar en la card de las ciudades favoritas
        binding.cardFavCities.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }
    }

    //Cada vez que hay datos nuevos del forecast se ejecuta este método:
    private fun setupForecastObservers() {
        //Observa los datos en vivo del forecast y los transforma para mostrarlos por días
        vm.forecast.observe(this) { forecasts ->
            //dailySummaries tendrá un resumen por día
            val dailySummaries: List<DaySummary> = forecasts
                //Agrupa todos los emelentos del pronóstico por día
                //Y con substring le quitas la hora a la fecha
                .groupBy { it.dateTime.substring(0, 10) }
                //Recorre la lista obteniendo los datos del pronóstico de cada día
                .map { (_, items) ->
                    //Busca la máxima y mínima temperatura por cada día
                    val tempMax = items.maxOf { it.main.temp }
                    val tempMin = items.minOf { it.main.temp }
                    //Recoge el primer pronóstico del día
                    val first = items[0]
                    //Crea un nuevo objeto que contiene el pronóstico del día junto a sus máximas y mínimas
                    DaySummary(first, tempMax, tempMin)
                }

            //Si tenemos los 5 días de pronóstico
            if (dailySummaries.size >= 5) {
                //Guardamos los datos del pronóstico de cada día en una variable
                val d1 = dailySummaries[0]
                val d2 = dailySummaries[1]
                val d3 = dailySummaries[2]
                val d4 = dailySummaries[3]
                val d5 = dailySummaries[4]

                //Actualizamos los textos de la UI con los datos del pronóstico de cada día
                binding.textDia1.text = resumenDiaMaxMin(d1)
                binding.textDia2.text = resumenDiaMaxMin(d2)
                binding.textDia3.text = resumenDiaMaxMin(d3)
                binding.textDia4.text = resumenDiaMaxMin(d4)
                binding.textDia5.text = resumenDiaMaxMin(d5)

                //Añadimos un icono según cada descripción del tiempo de cada día
                binding.imageDia1.setImageResource(iconResId(d1.item.weather.firstOrNull()?.description ?: ""))
                binding.imageDia2.setImageResource(iconResId(d2.item.weather.firstOrNull()?.description ?: ""))
                binding.imageDia3.setImageResource(iconResId(d3.item.weather.firstOrNull()?.description ?: ""))
                binding.imageDia4.setImageResource(iconResId(d4.item.weather.firstOrNull()?.description ?: ""))
                binding.imageDia5.setImageResource(iconResId(d5.item.weather.firstOrNull()?.description ?: ""))
            }
        }
    }

    //Función para generar el texto que se muestra en el pronóstico cada día
    private fun resumenDiaMaxMin(summary: DaySummary): String {
        //Parsea la fecha para que sea formato día y número
        //según el idioma del sistema operativo (Locale.getDefault)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        //Convierte la fecha en un objeto date
        val date = dateFormat.parse(summary.item.dateTime)
        //dayFormat traduce el objeto date a un texto como "lunes 20" en español debido a Locale
        val dayFormat = SimpleDateFormat("EEEE d", Locale("es", "ES"))
        //Si se pudo formatear la fecha, lo hace, sino le pone "Día"
        val diaTexto = if (date != null) dayFormat.format(date) else "Día"
        //Saca las temperaturas mínimas y máximas diarias
        val max = summary.tempMax.toInt()
        val min = summary.tempMin.toInt()
        //Obtiene la descripción del clima, si no encuetra nada pone "N/A"
        val descRaw = summary.item.weather.firstOrNull()?.description ?: "N/A"
        //Esto es meramente estético, pone la primera letra de la descripción del clima en mayúsculas
        val descBonita = descRaw.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale("es","ES")) else it.toString()
        }
        //Devuelve el texto que aparecerá en los TextView del pronóstico
        return "$diaTexto: $max°/$min° $descBonita"
    }

    //Data class para guardar los datos del resumen de cada día
    data class DaySummary(
        //Obtenemos la información del día
        val item: ForecastItem,
        //Obtenemos las temperaturas máximas y mínimas
        val tempMax: Double,
        val tempMin: Double
    )

    //Usa las mismas imágenes que en la pantalla principal
    private fun iconResId(description: String): Int {
        //Según la descripción del clima
        val desc = description.lowercase()
        //Devuelve una imagen
        return when {
            desc.contains("pocas nubes") -> R.drawable.parcialmente_nublado
            desc.contains("nubes dispersas") -> R.drawable.parcialmente_nublado
            desc.contains("muy nuboso") -> R.drawable.nube
            desc.contains("nublado") -> R.drawable.nube
            desc.contains("nubes") -> R.drawable.nube
            desc.contains("niebla") -> R.drawable.nube
            desc.contains("neblina") -> R.drawable.nube
            desc.contains("humo") -> R.drawable.nube
            desc.contains("calima") -> R.drawable.nube
            desc.contains("arena") -> R.drawable.nube
            desc.contains("polvo") -> R.drawable.nube
            desc.contains("ceniza") -> R.drawable.nube
            desc.contains("vendaval") -> R.drawable.nube
            desc.contains("tornado") -> R.drawable.nube
            desc.contains("lluvia") -> R.drawable.lluvia
            desc.contains("chubasco") -> R.drawable.lluvia
            desc.contains("llovizna") -> R.drawable.lluvia
            desc.contains("tormenta") -> R.drawable.tormenta
            desc.contains("nieve") -> R.drawable.nieve
            desc.contains("nevada") -> R.drawable.nieve
            desc.contains("nevadas") -> R.drawable.nieve
            desc.contains("ventisca") -> R.drawable.nieve
            else -> R.drawable.sol
        }
    }
}