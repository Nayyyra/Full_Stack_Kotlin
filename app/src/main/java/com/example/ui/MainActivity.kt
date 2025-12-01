package com.example.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.google.android.gms.location.*
import com.example.data.WeatherRepository
import com.example.data.local.WeatherDatabase
import com.example.data.remote.RetrofitInstance
import com.example.wheatherapp_full_stack.BuildConfig
import com.example.wheatherapp_full_stack.databinding.ActivityMainBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //Definimos las variables de latitud y longitud y las inicializamos
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    //Con binding podemos acceder a los elementos de la UI de forma segura sin usar findViewById
    private lateinit var binding: ActivityMainBinding

    //Fused es el cliente de Google Location Services que nos permite obtener la ubicación del dispositivo
    private lateinit var fused: FusedLocationProviderClient

    //vm (Viewmodel) gestiona los datos del clima y comunica la UI con el repositorio
    private lateinit var vm: WeatherViewModel

    //Si es true, indica que hemos abierto esta pantalla desde una ciudad favorita
    private var openedFromFavorite: Boolean = false

    // FUNCIÓN PARA CREAR EL CANAL DE NOTIFICACIONES
    private fun createNotificationChannel() {
        //Verificamos si la versión de Android permite enviar notificaciones
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //Nombre para el canal de notificaciones
            val name = "Alertas Meteo"
            //Descripción del canal de notificaciones
            val descriptionText = "Notificaciones de alertas meteorológicas"
            //Definimos que la importancia de las notificaciones será alta, por lo que:
            //Tendrán sonido, aparecerán como alerta y se consideran de alta prioridad
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            //Creamos el canal de notificaciones
            val channel = android.app.NotificationChannel("ALERTA_METEO", name, importance).apply {
                //Asignamos la descripción al declarada previamente al canal de notificaciones
                description = descriptionText
            }
            //Registramos el canal de notifiacioknes en el sistema Android con notificatioManager
            //que se encarga de gestionar las notificaciones en el sistema
            val notificationManager: android.app.NotificationManager =
                getSystemService(android.app.NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // FUNCIÓN PARA MOSTRAR UNA NOTIFICACIÓN LOCAL
    //Recibimos por parámetro el título y el contenido de la notificación
    private fun mostrarAlertaMeteo(titulo: String, texto: String) {
        //Comprobamos los permisos para Android 13+ antes de mostrar notificaciones
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            //Comprobamos si el permiso de las notificaciones se ha concedido
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                //Si no tienes el permiso, no lanzas la notificación
                return
            }
        }
        //Con el NotificationCompat.Builder construimos la notificación
        //Con this usamos el contexto y "ALERTA-METEO" es el canal para la notificación
        val builder = androidx.core.app.NotificationCompat.Builder(this, "ALERTA_METEO")
            //Icono para las notificaciones
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            //Titulo de la notificación
            .setContentTitle(titulo)
            //Texto de la notificación
            .setContentText(texto)
            //Prioridad alta para la notificación
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            //La notificación desaparece automáticamente cuando el ususario la toca
            .setAutoCancel(true)

        //Obtenemos un NotificationManagerCompact para poder enviar la notifiación
        //de forma compatible con la versión de Android
        val notificationManager = androidx.core.app.NotificationManagerCompat.from(this)
        //Enviamos la notificación con un ID único para ella y construyendola con builder.build
        notificationManager.notify(1001, builder.build())
    }

    //Configuración de la página
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Creamos el canal de notificación
        createNotificationChannel()
        //Solicitamos permiso de notificación si es Android 13 o superior
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            //Pedimos permiso al usuario para enviar notificaciones
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 0)
        }

        //Inicializamos binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializamos fused
        fused = LocationServices.getFusedLocationProviderClient(this)


        //BOTÓN (ICONO) PARA RECARGAR A LA UBICACIÓN ACTUAL

        //Cuando pulsemos en el icono de "Mi ubicación" volvemos a usar el GPS
        binding.btnUseCurrentLocation.setOnClickListener {
            //Dejamos de usar ciudad favorita fija
            openedFromFavorite = false
            //Reiniciamos las coordenadas
            latitude = 0.0
            longitude = 0.0
            //Volvemos a pedir permiso / ubicación
            requestLocationPermission()
            //Mostramos un mensaje usando toast indicando que se está buscando la ubicación actual
            android.widget.Toast.makeText(
                this,
                "Buscando tu ubicación actual...",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }


        // CAMBIAR A PANTALLA DE MÁS DETALLES

        //Cuando pulsemos en la CardView de más detalles
        binding.cardForecast.setOnClickListener {
            //Solo abrimos detalles si ya tenemos una ubicación válida
            if (latitude != 0.0 && longitude != 0.0) {
                //Creamos un intent que nos lleva a la página de los detalles del tiempo
                val intent : Intent = Intent(this, Main_Activity2::class.java)
                //Pasamos la latitud y longitud para poder sacar el pronóstico
                //de 5 días en la siguiente pantalla
                intent.putExtra("lat", latitude)
                intent.putExtra("lon", longitude)
                //Abrimos la página de los detalles del tiempo
                startActivity(intent)
            } else {
                //Si aún no tenemos las coordenadas de la ubicación actual (suelen tardar en cargar)
                //Mostramos este toast que será un mensaje que aparecerá en pequeñito
                android.widget.Toast.makeText(
                    //Y nos informará que se está cargando aún la ubicación
                    this,
                    "Espera a que se cargue la ubicación.",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }

        //INICIALIZAR BBDD Y REPOSITORIO

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
            //dao para la tabla de ciudades favoritas
            favoriteDao = db.favoriteCityDao(),
            //LLamada a la api y uso de apikey
            api = RetrofitInstance.api,
            apiKey = BuildConfig.OPENWEATHER_API_KEY
        )

        //Creamos un factory para crear el viewmodel
        val factory = WeatherViewModelFactory(repo)
        //Se crea tu viewModel con el que puedes acceder a la bbdd y a la API
        vm = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        //Recogemos datos si vienen de una ciudad guardada en favoritos
        val cityName = intent.getStringExtra("city_name")
        val cityLat = intent.getDoubleExtra("city_lat", 0.0)
        val cityLon = intent.getDoubleExtra("city_lon", 0.0)
        if (cityName != null && cityLat != 0.0 && cityLon != 0.0) {
            //Si la actividad se abre desde una ciudad guardada en favoritos
            openedFromFavorite = true
            //Cargamos estos datos en la UI
            latitude = cityLat
            longitude = cityLon
            //Cargamos el clima para esa ciudad
            vm.load(cityLat, cityLon)
        }

        //Llamamos a la función que observa cambios en los datos del clima y actualiza la UI
        setupObservers()
        //Llamamos a la función que solicita permisos de GPS al usuario
        //y solo pedimos ubicación si no venimos de una ciudad favorita
        if (!openedFromFavorite) {
            requestLocationPermission()
        }
    }


    // AQUI GESTIONAMOS LOS VALORES DE TEMPERATURA, NOMBRE DE LA CIUDAD AND ESTADO DEL TIEMPO
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
                val descBonita = data.description.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale("es", "ES")) else it.toString()
                }
                binding.textDesc.text = descBonita
                //Mostramos la sensación térmica
                binding.textFeelsLike.text = "${data.feelsLike.toInt()} °C"
                //Mostramos la presión
                binding.textActualPressure.text = "${data.pressure} hPa"
                //Mostramos la humedad
                binding.textHumidity.text = "${data.humidity} %"

                //Variable para usar después en imagen y animación
                val weather = data.description.lowercase()

                // SEGUN EL TIEMPO QUE HAGA PONEMOS UN ICONO DISTINTO
                when {
                    weather.contains("pocas nubes") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.parcialmente_nublado)
                    weather.contains("nubes dispersas") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.parcialmente_nublado)
                    weather.contains("nublado") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("nubes") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("muy nuboso") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("niebla") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("neblina") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("humo") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("calima") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("arena") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("polvo") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("ceniza") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("vendaval") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("tornado") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nube)
                    weather.contains("lluvia") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.lluvia)
                    weather.contains("chubasco") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.lluvia)
                    weather.contains("llovizna") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.lluvia)
                    weather.contains("tormenta") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.tormenta)
                    weather.contains("nieve") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nieve)
                    weather.contains("nevada") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nieve)
                    weather.contains("nevadas") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nieve)
                    weather.contains("ventisca") -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.nieve)
                    else -> binding.fotoTiempo.setImageResource(com.example.wheatherapp_full_stack.R.drawable.sol)
                }

                //Animación Lottie según clima
                val animView = binding.weatherAnimation
                when {
                            weather.contains("sol") ||
                            weather.contains("despejado") -> {
                        animView.setAnimation(com.example.wheatherapp_full_stack.R.raw.sunny)
                    }
                            weather.contains("lluvia") ||
                            weather.contains("llovizna")||
                            weather.contains("chubasco") -> {
                        animView.setAnimation(com.example.wheatherapp_full_stack.R.raw.rainy)
                    }
                            weather.contains("nubes") ||
                            weather.contains("nublado") ||
                            weather.contains("nuboso") ||
                            weather.contains("niebla") ||
                            weather.contains("neblina") ||
                            weather.contains("humo") ||
                            weather.contains("calima") ||
                            weather.contains("arena") ||
                            weather.contains("polvo") ||
                            weather.contains("ceniza") ||
                            weather.contains("vendaval")-> {
                        animView.setAnimation(com.example.wheatherapp_full_stack.R.raw.cloudy)
                    }
                        weather.contains("tormenta")||
                        weather.contains("tornado") -> {
                        animView.setAnimation(com.example.wheatherapp_full_stack.R.raw.storm)
                    }
                        weather.contains("nieve") ||
                        weather.contains("nevada") ||
                        weather.contains("nevadas") ||
                        weather.contains("ventisca") -> {
                        animView.setAnimation(com.example.wheatherapp_full_stack.R.raw.snow)
                    }
                    else -> {
                        animView.setAnimation(com.example.wheatherapp_full_stack.R.raw.sunny)
                    }
                }
                animView.playAnimation()

                // ALERTA LOCAL SI HAY TORMENTA, LLUVIA FUERTE O NUBES
                if (weather.contains("tormenta")) {
                    mostrarAlertaMeteo("¡Alerta meteorológica!", "Tormenta detectada en ${data.cityName}")
                }
                if (weather.contains("lluvia")) {
                    mostrarAlertaMeteo("¡Alerta meteorológica!", "Lluvias en ${data.cityName}")
                }

                if (weather.contains("nubes")) {
                    mostrarAlertaMeteo("¡Alerta meteorológica!", "Cielo nublado en ${data.cityName}")
                }
            }
        }
    }

    // PERMISOS DE UBICACIÓN

    //Creamos un permissionLauncher para que solicite permisos de ubicación
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            //Si se aceptan los permisos
            if (granted) {
                //Se llama a fetchLocation() para obtener la ubicación
                fetchLocation()
            } else {
                //Si el permiso no se concede, comprobamos si el usuario marcó "No preguntar de nuevo"
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Si está denegado permanentemente te sugiere abrir ajustes para que el usuario lo habilite manualmente
                    showPermissionDeniedPermanentlyDialog()
                } else {
                    //Si el usuario lo denegó sin seleccionar “no volver a preguntar”, mostramos un mensaje Toast
                    android.widget.Toast.makeText(this, "Permiso de ubicación denegado.", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun requestLocationPermission() {
        //Comprobamos si ya hay permisos de ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            //Si ya está concedido, usamos directamente la ubicación
            fetchLocation()
        } else {
            //Si no está concedido
            //Mostramos un rationale explicando por qué necesitamos el permiso
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Mostramos diálogo explicativo
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Permiso de ubicación")
                    .setMessage("La app necesita acceso a la ubicación para mostrar el clima local. ¿Deseas permitirlo?")
                    .setPositiveButton("Permitir") { _, _ ->
                        //Si el usuario acepta, lanzamos el request de permiso
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    //Si le da a cancelar, no se pide el permiso
                    .setNegativeButton("Cancelar", null)
                    .show()
            } else {
                //No hay que mostrar rationale (primera vez o denegado permanentemente) y lanzamos el request directamente
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    //Si el permiso de ubicación se deniega permanentemente
    private fun showPermissionDeniedPermanentlyDialog() {
        //Mostramos un diálogo que sugiere abrir los ajustes de la app
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Permiso necesario")
            .setMessage("Has denegado el permiso de ubicación permanentemente. Ve a los ajustes de la aplicación para activarlo.")
            .setPositiveButton("Abrir ajustes")
            //Abrimos la pantalla de ajustes al pulsar el botón
            { _, _ -> openAppSettings() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    //Abrir la pantalla de ajustes de la app
    private fun openAppSettings() {
        //Creamos un intent que abre los ajustes de la app
        val intent = android.content.Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        //Le pasamos el paquete de la app para que abra sus ajustes concretamente
        val uri = android.net.Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    //OBTENER UBICACIÓN
    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        //Si venimos de una ciudad favorita, no actualizamos por GPS
        if (openedFromFavorite) return

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
                    //Asignamos valor a las variables de la ubicación
                    latitude = location.latitude
                    longitude = location.longitude
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