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


    //Con binding podemos acceder a los elementos de la UI de forma segura sin usar findViewById
    private lateinit var binding: ActivityMainBinding

    //Fused es el cliente de Google Location Services que nos permite obtener la ubicación del dispositivo
    private lateinit var fused: FusedLocationProviderClient

    //vm (Viewmodel) gestiona los datos del clima y comunica la UI con el repositorio
    private lateinit var vm: WeatherViewModel

    //Configuración de la página
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inicializamos binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializamos fused
        fused = LocationServices.getFusedLocationProviderClient(this)


        // CAMBIAR A PANTALLA DE MÁS DETALLES
        //Cuando pulsemos en el boton de más detalles
        binding.masDetallesBtn.setOnClickListener {
            //Creamos un intent que nos lleva a la página de los detalles del tiempo
            val intent : Intent = Intent(this, Main_Activity2::class.java)
            //Abrimos la página de los detalles del tiempo
            startActivity(intent)
        }

        // Inicializar BD y repositorio
        //Creamos la bbdd local con Room
        val db = Room.databaseBuilder(
            //La bbdd se llamará weather.db
            applicationContext,
            WeatherDatabase::class.java,
            "weather.db"
        )
            //Esto evita crashes si cambias la estructura de la BD, elimina la bbdd vieja y crea una nueva
            .fallbackToDestructiveMigration()
            //Construimos la bbdd
            .build()

        //Creamos el repositorio que maneja datos locales y remotos
        val repo = WeatherRepository(
            //dao nos permite leer y escribir en la bbdd local
            dao = db.dao(),
            //LLamada a la api y uso de apikey
            api = RetrofitInstance.api,
            apiKey = BuildConfig.OPENWEATHER_API_KEY
        )

        //Creamos un factory para crear el viewmodel
        val factory = WeatherViewModelFactory(repo)
        //Se crea tu viewModel con el que puedes acceder a la bbdd y a la API
        vm = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        //Llamamos a la función que observa cambios en los datos del clima y actualiza la UI
        setupObservers()
        //Llamamos a la función que solicita permisos de GPS al usuario
        requestLocationPermission()
    }


    // AQUI GESTIONAMOS LOS VALORES DE TEMPERATURA, NOMBRE DE LA CIUDAD Y ESTADO DEL TIEMPO
    private fun setupObservers() {
        //Observa los datos en vivo, se ejecuta cada vez que cambian los datos
        vm.weather.observe(this) { data ->
            if (data != null) {

                // PONER FECHA

                //Obtenemos la fecha
                val hoy = LocalDate.now()

                //La formateamos con el formato de fecha que queremos
                val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("es", "ES"))
                val fechaFormateada = hoy.format(formatter)

                //Muestra la fecha en el textView de la interfaz
                binding.diaHoy.text = fechaFormateada


                //Mostramos la ciudad en la UI
                binding.textCity.text = data.cityName
                //Mostramos la temperatura
                binding.textTemp.text = "${data.temperature.toInt()} °C"
                //Mostramos la descripción del clima
                binding.textDesc.text = data.description
                //Mostramos la sensación térmica
                binding.textFeelsLike.text = "${data.feelsLike.toInt()} °C"

                // SEGUN EL TIEMPO QUE HAGA UN ICONO DISTINTO
                val weather = data.description.lowercase()

                //Seleccionamos una imágen según la descripción del clima
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

    //Creamos un permissionLauncher para que solicite permisos de ubicación
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            //Si se aceptan los permisos se llama a fetchLocation()
            if (granted) fetchLocation()
        }

    private fun requestLocationPermission() {
        //Comprobamos si ya hay permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        } else {
            //Sino los solicita
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    //Obtener ubicación
    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        //Vamos actualizando y almacenando la ubicación cada segundo
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()

        //Recibimos la ubicación del GPS cuando esté lista
        val locationCallback = object : LocationCallback() {
            //Definimos que hacemos cuando tenemos la ubicación
            override fun onLocationResult(result: LocationResult) {
                //Guardamos la localización
                val location = result.lastLocation
                //Si hay ubicación
                if (location != null) {
                    //Actualizamos el clima según las coordenadas
                    vm.load(location.latitude, location.longitude)
                    //Dejamos de recibir actualizaciones después de obtener la primera ubicación
                    fused.removeLocationUpdates(this)
                }
            }
        }

        //Comenzamos a recibir actualizaciones de la ubicación
        fused.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

}