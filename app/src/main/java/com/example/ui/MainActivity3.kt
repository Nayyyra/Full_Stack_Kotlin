package com.example.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.data.WeatherRepository
import com.example.data.local.WeatherDatabase
import com.example.data.remote.RetrofitInstance
import com.example.wheatherapp_full_stack.BuildConfig
import com.example.wheatherapp_full_stack.R
import com.example.wheatherapp_full_stack.databinding.ActivityMain3Binding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class MainActivity3 : AppCompatActivity() {

    //ViewBinding para acceder a los elementos del layout sin findViewById
    private lateinit var binding: ActivityMain3Binding

    //ViewModel que gestionará la lista de ciudades favoritas
    private lateinit var vmFav: FavoriteCitiesViewModel

    //Adapter del RecyclerView que mostrará las ciudades favoritas
    private lateinit var favAdapter: FavoriteCitiesAdapter

    //Configuración de la página
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Inflamos (construimos) el layout con ViewBinding
        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Inicializamos la BD y el repositorio igual que en las otras pantallas
        val db = Room.databaseBuilder(
            applicationContext,
            WeatherDatabase::class.java,
            "weather.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        val repo = WeatherRepository(
            dao = db.dao(),
            favoriteDao = db.favoriteCityDao(),
            api = RetrofitInstance.api,
            apiKey = BuildConfig.OPENWEATHER_API_KEY
        )

        //Creamos el ViewModel específico para ciudades favoritas
        val factory = FavoriteCitiesViewModelFactory(repo)
        vmFav = ViewModelProvider(this, factory)[FavoriteCitiesViewModel::class.java]

        //Configuramos el RecyclerView con su adapter y 2 callbacks
        favAdapter = FavoriteCitiesAdapter(
            onDeleteClicked = { ciudad ->
                //Cuando pulsamos la papelera, borramos la ciudad
                lifecycleScope.launch {
                    repo.deleteFavoriteCity(ciudad)
                }
            },
            onItemClicked = { ciudad ->
                //Cuando se pulsa la fila, ir a pantalla principal y pasar datos de la ciudad
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("city_name", ciudad.name)
                intent.putExtra("city_lat", ciudad.lat)
                intent.putExtra("city_lon", ciudad.lon)
                startActivity(intent)
            }
        )
        binding.recyclerFavCities.apply {
            //Mostamos la lista de forma vertical
            layoutManager = LinearLayoutManager(this@MainActivity3)
            //Le asociamos su adaptador para que sepa como mostrar los datos
            adapter = favAdapter
        }

        //Observamos la lista de ciudades favoritas
        vmFav.favoriteCities.observe(this) { cities ->
            //Actualizamos la UI
            favAdapter.submitList(cities)
        }

        // VOLVER HACIA ATRÁS (PÁGINA 2)

        //Buscamos en activity_main3 el botón de volver a la página de detalles
        val botonVolver: ImageView = findViewById(R.id.fotoVolver)

        //Al pulsar en el botón de volver
        botonVolver.setOnClickListener {
            //Creamos un intent que nos lleva a la página de detalles
            val intent: Intent = Intent(this, Main_Activity2::class.java)
            //Abrimos la pantalla de detalles
            startActivity(intent)
        }
    }
}