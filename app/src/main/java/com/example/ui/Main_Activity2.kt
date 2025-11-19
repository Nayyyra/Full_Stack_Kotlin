package com.example.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ui.MainActivity3
import com.example.wheatherapp_full_stack.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class Main_Activity2 : AppCompatActivity() {

    //Configuración de la pantalla
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // VOLVER A LA PÁGINA DE INICIO

        //Buscamos el boton de inicio dentro de activity_main2
        var botonInicio: ImageView = findViewById<ImageView>(R.id.botonInicio)

        //Cuando hacemos click sobre el boton de inicio
        botonInicio.setOnClickListener {
            //Creamos un intent que nos lleva de vuelta a la pantalla principal
            val intent: Intent = Intent(this, MainActivity::class.java)

            //Abrimos la pantalla principal
            startActivity(intent)
        }


        // IR A LA PÁGINA DE CIUDADES FAVORITAS

        //Buscamos la cardview de las ciudades favoritas dentro de activity_main2
        var cardViewCiudades : CardView = findViewById<CardView>(R.id.cardFavCities)

        //Al pulsar sobre la cardView
        cardViewCiudades.setOnClickListener {
            //Creamos un intent que nos lleva a la página de las ciudades favoritas
            val intent : Intent = Intent(this, MainActivity3::class.java)

            //Abrimos la página de ciudades favoritas
            startActivity(intent)
        }



    }
}