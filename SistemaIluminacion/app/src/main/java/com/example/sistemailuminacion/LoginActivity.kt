package com.example.sistemailuminacion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        val etCorreoLogin: EditText = findViewById(R.id.etCorreoLogin)
        val etPasswordLogin: EditText = findViewById(R.id.etPasswordLogin)
        val btnIngresar: Button = findViewById(R.id.btnIngresar)

        if (auth.currentUser != null) {
            val ventana = Intent(this, MainActivity::class.java)
            startActivity(ventana)
            finish()
            return
        }

        btnIngresar.setOnClickListener {
            val correo = etCorreoLogin.text.toString().trim()
            val password = etPasswordLogin.text.toString().trim()

            if (correo.isBlank() || password.isBlank()) {
                Toast.makeText(
                    this,
                    "Error: completa correo y contrasena",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(correo, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Sesion iniciada correctamente",
                            Toast.LENGTH_LONG
                        ).show()
                        val ventana = Intent(this, MainActivity::class.java)
                        startActivity(ventana)
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "Error al iniciar sesion",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}