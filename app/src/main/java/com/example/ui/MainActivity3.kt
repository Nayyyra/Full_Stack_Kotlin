package com.example.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wheatherapp_full_stack.R

class MainActivity3 : AppCompatActivity() {
    //Configuración de la página
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // VOLVER HACIA ATRÁS (PÁGINA 2)

        //Buscamos en activity_main3 el botón de volver a la página de detalles
        var botonVolver: ImageView = findViewById<ImageView>(R.id.fotoVolver)

        //Al pulsar en el botón de volver
        botonVolver.setOnClickListener {
            //Creamos un intent que nos lleva a la página de detalles
            val intent: Intent = Intent(this, Main_Activity2::class.java)
            //Abrimos la pantalla de detalles
            startActivity(intent)
        }



    }
}