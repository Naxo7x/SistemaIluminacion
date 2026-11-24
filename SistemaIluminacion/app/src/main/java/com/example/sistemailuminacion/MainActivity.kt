package com.example.sistemailuminacion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val tvUsuarioActual: TextView = findViewById(R.id.tvUsuarioActual)
        val btnIrControlIot: Button = findViewById(R.id.btnIrControlIot)
        val btnIrCrudRegistros: Button = findViewById(R.id.btnIrCrudRegistros)
        val btnCerrarSesion: Button = findViewById(R.id.btnCerrarSesion)

        val correoActual = auth.currentUser?.email ?: "Usuario conectado"
        tvUsuarioActual.text = "Usuario conectado:\n$correoActual"

        btnIrControlIot.setOnClickListener {
            val ventana = Intent(this, ControlActivity::class.java)
            startActivity(ventana)
        }

        btnIrCrudRegistros.setOnClickListener {
            val ventana = Intent(this, RegistroActivity::class.java)
            startActivity(ventana)
        }

        btnCerrarSesion.setOnClickListener {
            auth.signOut()
            val ventana = Intent(this, LoginActivity::class.java)
            startActivity(ventana)
            finish()
        }
    }
}