package com.example.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.android.gms.location.*
import com.example.data.WeatherRepository
import com.example.data.local.WeatherDatabase
import com.example.data.remote.RetrofitInstance
import com.example.wheatherapp_full_stack.BuildConfig
import com.example.wheatherapp_full_stack.R
import com.example.wheatherapp_full_stack.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fused: FusedLocationProviderClient
    private lateinit var vm: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fused = LocationServices.getFusedLocationProviderClient(this)


        // CAMBIAR A PANTALLA DE MÁS DETALLES

        binding.masDetallesBtn.setOnClickListener {
            val intent : Intent = Intent(this, Main_Activity2::class.java)
            startActivity(intent)
        }

        // Inicializar BD y repositorio
        val db = Room.databaseBuilder(
            applicationContext,
            WeatherDatabase::class.java,
            "weather.db"
        )
            .fallbackToDestructiveMigration() // Esto evita crashes si cambiaste la estructura de la BD
            .build()

        val repo = WeatherRepository(
            dao = db.dao(),
            api = RetrofitInstance.api,
            apiKey = BuildConfig.OPENWEATHER_API_KEY
        )

        val factory = WeatherViewModelFactory(repo)
        vm = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        setupObservers()
        requestLocationPermission()
    }



    // AQUI GESTIONAMOS LOS VALORES DE TEMPERATURA, NOMBRE DE LA CIUDAD Y ESTADO DEL TIEMPO
    private fun setupObservers() {
        vm.weather.observe(this) { data ->
            if (data != null) {

                // PONER FECHA
                val hoy = LocalDate.now()

                val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
                val fechaFormateada = hoy.format(formatter)
                binding.diaHoy.text = fechaFormateada


                binding.textCity.text = data.cityName
                binding.textTemp.text = "${data.temperature.toInt()} °C"
                binding.textDesc.text = data.description
                //binding.textFeelsLike.text = "${data.feelslike} °C"

                // SEGUN EL TIEMPO QUE HAGA UN ICONO DISTINTO
                val weather = data.description.lowercase()

                when {
                    weather.contains("pocas nubes") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.parcialmente_nublado)
                    weather.contains("nubes dispersas") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.parcialmente_nublado)
                    weather.contains("nublado") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("nubes") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("muy nuboso") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("niebla") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("neblina") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("lluvia") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.lluvia)
                    weather.contains("llovizna") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.lluvia)
                    weather.contains("tormenta") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.tormenta)
                    weather.contains("nieve") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nieve)
                    weather.contains("nevadas") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nieve)
                    weather.contains("ventisca") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nieve)
                    else -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.sol)
                }

            }

        }
    }


    /** PERMISOS DE UBICACIÓN */

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) fetchLocation()
        }

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                if (location != null) {
                    vm.load(location.latitude, location.longitude)
                    // Dejamos de recibir actualizaciones después de obtener la primera ubicación
                    fused.removeLocationUpdates(this)
                }
            }
        }

        fused.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

}