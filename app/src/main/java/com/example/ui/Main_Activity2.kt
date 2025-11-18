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

    lateinit var mapView: MapView
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

        var botonInicio: ImageView = findViewById<ImageView>(R.id.botonInicio)

        botonInicio.setOnClickListener {
            val intent: Intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }

        // IR A LA PÁGINA DE CIUDADES FAVORITAS

        var cardViewCiudades : CardView = findViewById<CardView>(R.id.cardFavCities)

        cardViewCiudades.setOnClickListener {
            val intent : Intent = Intent(this, MainActivity3::class.java)

            startActivity(intent)
        }



    }
}