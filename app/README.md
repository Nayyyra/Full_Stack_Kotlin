# WheatherApp Full Stack - SkyMood 🌦️

### Breve descripción del proyecto
Aplicación Android en Kotlin que muestra el tiempo actual y el pronóstico de 5 días utilizando la API pública de OpenWeatherMap.  
Permite usar la ubicación actual del dispositivo y gestionar una lista de ciudades favoritas almacenadas en local con Room.

-----------------------------------------------------------------------------------------------------------------------

### Requisitos
- Android Studio (versión reciente, Giraffe o superior).
- JDK 17 (incluido normalmente con Android Studio).
- Dispositivo Android o emulador con:
    - Android 8.0 (API 26) o superior.
    - Servicios de Google Play (para ubicación).
- Conexión a Internet (para consultar la API del tiempo).
- Una API Key gratuita de OpenWeatherMap.

-----------------------------------------------------------------------------------------------------------------------

### Estructura del proyecto

app/
└── src/
└── main/
├── AndroidManifest.xml
├── kotlin+java
│   └── com/
│       └── example/
│           └── ├── data/
│               │   ├── local/
│               │   │   ├── CachedWeather.kt
│               │   │   ├── FavoriteCity.kt
│               │   │   ├── FavoriteCityDao.kt
│               │   │   ├── WeatherDao.kt
│               │   │   └── WeatherDatabase.kt
│               │   ├── remote/
│               │   │   ├── ForecastResponse.kt
│               │   │   ├── WeatherResponse.kt
│               │   │   ├── WeatherApiService.kt
│               │   │   └── RetrofitInstance.kt
│               │   └── WeatherRepository.kt
│               └── ui/
│                   ├── MainActivity.kt
│                   ├── Main_Activity2.kt
│                   ├── MainActivity3.kt
│                   ├── WeatherViewModel.kt
│                   ├── WeatherViewModelFactory.kt
│                   ├── FavoriteCitiesViewModel.kt
│                   ├── FavoriteCitiesViewModelFactory.kt
│                   └── FavoriteCitiesAdapter.kt
└── res/
    ├── layout/
    │     ├── activity_main.xml
    │     ├── activity_main2.xml  
    │     ├── activity_main3.xml
    │     └── item_favorite_city.xml
    ├── drawable/           (iconos, fondos, etc.)
    ├── values/
    │   ├── colors.xml
    │   ├── strings.xml
    │   └── themes.xml  
    ├── mipmap/             
    │      └── ic_launcher.xml (icono de la app)
    └── raw/ (animaciones Lottie)
         ├── cloudy.json
         ├── rainy.json
         ├── snow.json
         ├── storm.json
         └── sunny.json
-----------------------------------------------------------------------------------------------------------------------

### Como ejecutar la app en local:

## 1.Clona el repositorio:
 - git clone https://github.com/Nayyyra/Full_Stack_Kotlin.git
 - cd Full_Stack_Kotlin

## Configuración de la API Key

1. Crea una cuenta en OpenWeatherMap:
    - https://openweathermap.org/api

2. Genera una API Key (puede tardar unos minutos en activarse).

3. En Android Studio, abre el archivo `app/build.gradle.kts` (o `app/build.gradle`).

4. Dentro de `defaultConfig` localiza esta línea:
   buildConfigField("String", "OPENWEATHER_API_KEY", ""TU_API_KEY_AQUI"")

5. Sustituye `TU_API_KEY_AQUI` por tu propia clave de OpenWeatherMap, por ejemplo:
   buildConfigField("String", "OPENWEATHER_API_KEY", ""1234567890abcdef"")

6. Sincroniza el proyecto (Gradle Sync) y ejecuta la app.  
   El código utiliza `BuildConfig.OPENWEATHER_API_KEY` para hacer las peticiones a la API.

## Abre el proyecto en Android Studio:
1. File > Open… y selecciona la carpeta del proyecto.
2. Configura la API Key como se indica en el apartado anterior.
3. Sincroniza el proyecto (Gradle Sync).
4. Elige un dispositivo:
   - Emulador de Android Studio
   - Dispositivo físico con modo desarrollador y depuración USB activados
   - Pulsa el botón Run ▶ en Android Studio y selecciona el dispositivo
   
-----------------------------------------------------------------------------------------------------------------------

### Permisos necesarios
La app usa:
- Ubicación precisa: para obtener la localización actual del dispositivo
- Notificaciones (Android 13+): para mostrar alertas meteorológicas locales

Android solicitará estos permisos en tiempo de ejecución la primera vez que hagan falta.
Si por un casual la app no solicitase la ubicación, sería necesario entrar en ajustes 
de la aplicación y permitir que se acceda a la ubicación siempre que se use la app.

-----------------------------------------------------------------------------------------------------------------------

### Uso de la Aplicación

## Al abrir la app

- Si **concedes el permiso de ubicación**, se obtiene el **tiempo actual de tu ubicación (GPS)**.
- Si entras desde una **ciudad favorita**, se mostrará el tiempo de esa ciudad **sin usar el GPS**.

-----------------------------------------------------------------------------------------------------------------------

## Pantalla principal (`MainActivity`)

# Muestra:
- Nombre de la ciudad.
- Temperatura actual y sensación térmica.
- Descripción del tiempo.
- Humedad y presión.
- Icono y animación según el clima.

# Desde aquí puedes:
- Ir al **pronóstico de 5 días**.
- Ir a **buscar ciudades**.
- Ir a la **lista de ciudades favoritas**.

-----------------------------------------------------------------------------------------------------------------------

## Pantalla de pronóstico (`Main_Activity2`)
Muestra el **pronóstico de 5 días** para las coordenadas recibidas.

# Cada día incluye:
- Fecha (día de la semana).
- Temperaturas máxima y mínima.
- Descripción e icono del clima.

# Barra de búsqueda
- Escribe el nombre de una ciudad para buscarla por API.
- Si la ciudad existe, se **guarda como favorita** usando **Room**.

# Botones disponibles
- **Volver a inicio** usando la ciudad actual del pronóstico.
- **Ir a la lista de ciudades favoritas**.

-----------------------------------------------------------------------------------------------------------------------

## Pantalla de favoritos (`MainActivity3`)
Esta pantalla muestra una lista con todas las **ciudades guardadas como favoritas** en la base de datos **Room**.

# Funciones:
- **Listar ciudades favoritas**  
  Todas las ciudades almacenadas aparecen en un RecyclerView.

- **Tocar una ciudad**  
  Al seleccionar una ciudad, la app vuelve a la **pantalla principal** mostrando el **tiempo actual de esa ciudad**.

- **Eliminar ciudad (icono de papelera)**  
  Al pulsar la papelera, la ciudad se **elimina de favoritos** de Room y desaparece de la lista.

-----------------------------------------------------------------------------------------------------------------------

### Relación entre las distintas capas
- La **UI** nunca pregunta directamente a la BD o la API, siempre habla con el ViewModel.
- El **ViewModel** nunca toca la BD ni la API, solo habla con el Repository.
- El **Repository** es el único que sabe usar DAOs, Room, Retrofit, etc.

-----------------------------------------------------------------------------------------------------------------------

### Funcionamiento de la aplicación
    - La aplicación sigue una arquitectura por capas (UI, lógica y datos) usando el patrón MVVM para separar
    interfaz, lógica y acceso a datos.

    - Al abrirse, la pantalla principal (MainActivity) comprueba si llega una ciudad fija (por ejemplo, desde favoritos);
    si no es así, solicita permiso de ubicación y obtiene las coordenadas actuales con FusedLocationProviderClient
    para cargar el tiempo de esa ubicación.

    - MainActivity crea la base de datos Room, el WeatherRepository y el WeatherViewModel, observa el estado del tiempo
    mediante LiveData y actualiza textos, iconos, animaciones y notificaciones en función de la información meteorológica recibida.

    - Cuando el usuario pulsa la tarjeta de “Más detalles”, MainActivity abre Main_Activity2 enviando la latitud y longitud 
    actuales por un Intent para mostrar el pronóstico de esa ubicación.

    - Main_Activity2 utiliza el mismo WeatherViewModel para pedir al repositorio el pronóstico de 5 días, 
    agrupa las predicciones por día, calcula temperaturas máximas y mínimas y muestra un resumen diario con texto e iconos.

    - Desde la pantalla de pronóstico, el usuario puede buscar una ciudad por nombre. Se llama a la API de OpenWeather con el cliente
    Retrofit, se obtienen sus coordenadas reales y se guarda como ciudad favorita en la tabla FavoriteCity de Room si aún no existía.

    - Esta misma pantalla permite volver a la pantalla principal pasando de nuevo las coordenadas actuales, de modo que
    MainActivity trate esa ciudad como fija y deje de usar el GPS mientras esa ciudad esté seleccionada.

    - La pantalla de favoritas (MainActivity3) observa el listado de FavoriteCity a través de FavoriteCitiesViewModel, 
    muestra la lista en un RecyclerView y permite borrar ciudades de Room o abrir una ciudad concreta en MainActivity con 
    sus coordenadas.

    - En toda la app, las Activities y adapters solo muestran datos y reaccionan a los clics del usuario, mientras que la lógica 
    de acceso a red y base de datos se concentra en WeatherRepository y los ViewModels son quienes se comunican con 
    el repositorio y exponen datos listos para la interfaz.

-----------------------------------------------------------------------------------------------------------------------

### Tecnologías y Arquitectura

## Lenguaje
- **Kotlin**

## Arquitectura
- **MVVM (Model–View–ViewModel) simplificada**

-----------------------------------------------------------------------------------------------------------------------
### Capas del proyecto

## UI
- `Activities`
- `ViewModels`
- `Adapters`

## data.local
- **Room**
    - Entidades
    - DAOs
    - Base de datos

## data.remote
- **Retrofit + Moshi**
    - Consumo de la API de OpenWeather
    - Conversión automática JSON → Kotlin

## data
- `WeatherRepository`
    - Actúa como puente entre **local** (Room) y **remoto** (API).

-----------------------------------------------------------------------------------------------------------------------

### Persistencia
- **Room** para almacenamiento local.
- **Flow / LiveData** para datos reactivos y caché interna.

-----------------------------------------------------------------------------------------------------------------------

### Red
- **Retrofit + Moshi** para:
    - Llamadas HTTP
    - Deserialización de JSON

-----------------------------------------------------------------------------------------------------------------------

### Ubicación
- **FusedLocationProviderClient**  
  (de Google Play Services) para obtener la ubicación del usuario.

-----------------------------------------------------------------------------------------------------------------------

### Notificaciones
- Uso de **canales de notificación** (NotificationChannel)
- Alertas según el clima:
    - Lluvia
    - Tormenta
    - Nubes

-----------------------------------------------------------------------------------------------------------------------

### Animaciones
- **Lottie** para animaciones según la condición meteorológica.

-----------------------------------------------------------------------------------------------------------------------

### Gestión de claves
Se ha implementado como mejora extra la gestión de la API key mediante BuildConfig y local.properties, por lo que:
- La clave no se sube al repositorio, se guarda en local.properties
- En tiempo de compilación se inyecta en **`BuildConfig.OPENWEATHER_API_KEY`**  con buildConfigField
- La clave queda hardcodeada en el APK

-----------------------------------------------------------------------------------------------------------------------

### Personalización de la Configuración

Si deseas adaptar el proyecto a tu propio entorno, puedes modificar los siguientes elementos:

## Configuración modificable

- **API Base URL**  
  Puedes cambiar la URL base por otra API de clima en la clase donde se crea el cliente de Retrofit.

- **Clave API**  
  La clave se configura en app/build.gradle dentro de defaultConfig, en la línea 
  buildConfigField("String", "OPENWEATHER_API_KEY", "...").
  Para usar la app con otra cuenta de OpenWeatherMap solo hay que sustituir ese valor 
  por la nueva API Key y sincronizar el proyecto.

- **Intervalo de actualización de ubicación**  
  Ajustable modificando los parámetros del `LocationRequest`.

-----------------------------------------------------------------------------------------------------------------------

## Desactivar características

- **Notificaciones**  
  Si no deseas solicitar el permiso `POST_NOTIFICATIONS` o no quieres alertas meteorológicas:  
  → Puedes **comentar o eliminar** toda la sección relacionada con notificaciones.

- **Favoritos**  
  Si solo deseas usar la ubicación actual y no guardar ciudades:  
  → Puedes **eliminar o simplificar** la lógica de Room y la pantalla de favoritos.

-----------------------------------------------------------------------------------------------------------------------

### Posibles problemas

## La aplicación no muestra datos
- Comprueba tu conexión a Internet
- Verifica que la API key sea válida
- Revisa en ajustes del dispositivo que has concedido permisos de ubicación

## BuildConfig en rojo
- Sincroniza el proyecto con Gradle: **File > Sync Project with Gradle Files**
- Limpia y reconstruye: **Build > Clean Project** y luego **Build > Rebuild Project**
- Si estas opciones no funcionaron, finaliza la tarea de Android Studio desde el
  administrador de tareas y vuelve a abrir Android Studio
- Si sigue saliendo BuildConfig en rojo prueba a runnear la app 
  (a veces aparece como si hubiese error pero luego la app funciona porque no hay un error realmente)

-----------------------------------------------------------------------------------------------------------------------

### Licencia

    Este proyecto fue desarrollado como trabajo para la asignatura de 2º de DAM
    Programación Multimedia y Dispositivos Móviles durante el transcurso del 1er Trimestre.

    Su propósito es estrictamente académico y está pensado para uso educativo
    y demostración dentro del entorno del módulo.

-----------------------------------------------------------------------------------------------------------------------

### Componentes del equipo
- Nayra Muñiz
- Ana Margarita
- Ainhoa Melgarejo 
- Shihui Chen

