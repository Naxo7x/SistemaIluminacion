package com.example.sistemailuminacion

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ControlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_iot)


        val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("iluminacion")


        val tvLuzActual: TextView = findViewById(R.id.tvLuzActual)
        val tvEstadoLed: TextView = findViewById(R.id.tvEstadoLed)
        val tvModoOperacion: TextView = findViewById(R.id.tvModoOperacion)
        val btnCambiarModo: Button = findViewById(R.id.btnCambiarModo)
        val btnToggleLed: Button = findViewById(R.id.btnToggleLed)
        val etUmbralEncender: EditText = findViewById(R.id.etUmbralEncender)
        val etUmbralApagar: EditText = findViewById(R.id.etUmbralApagar)
        val btnGuardarUmbrales: Button = findViewById(R.id.btnGuardarUmbrales)

        var modoActual: String = "automatico"
        var ledEncendido: Boolean = false


        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val luz = snapshot.child("luz_actual").getValue(Int::class.java)
                    ?: snapshot.child("luz_actual").getValue(Long::class.java)?.toInt()
                    ?: 0

                ledEncendido = snapshot.child("estado_led").getValue(Boolean::class.java) ?: false
                modoActual = snapshot.child("modo_operacion").getValue(String::class.java) ?: "automatico"

                val textoLed = if (ledEncendido) "ENCENDIDO" else "APAGADO"
                tvLuzActual.text = "Luz actual: $luz"
                tvEstadoLed.text = "Estado LED: $textoLed"
                tvModoOperacion.text = "Modo: $modoActual"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ControlActivity,
                    "Error al leer datos de Firebase",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        btnCambiarModo.setOnClickListener {
            val nuevoModo = if (modoActual.equals("manual", ignoreCase = true)) {
                "automatico"
            } else {
                "manual"
            }
            dbRef.child("modo_operacion").setValue(nuevoModo)
        }


        btnToggleLed.setOnClickListener {
            val nuevoEstado = !ledEncendido
            dbRef.child("modo_operacion").setValue("manual")
            dbRef.child("estado_led").setValue(nuevoEstado)
        }

        btnGuardarUmbrales.setOnClickListener {
            val encenderTexto = etUmbralEncender.text.toString().trim()
            val apagarTexto = etUmbralApagar.text.toString().trim()

            if (encenderTexto.isBlank() || apagarTexto.isBlank()) {
                Toast.makeText(
                    this,
                    "Error: ingresa ambos umbrales",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val umbralEncender = encenderTexto.toIntOrNull()
            val umbralApagar = apagarTexto.toIntOrNull()

            if (umbralEncender == null || umbralEncender <= 0) {
                etUmbralEncender.error = "Ingresa un numero mayor a 0"
                etUmbralEncender.requestFocus()
                return@setOnClickListener
            }

            if (umbralApagar == null || umbralApagar <= 0) {
                etUmbralApagar.error = "Ingresa un numero mayor a 0"
                etUmbralApagar.requestFocus()
                return@setOnClickListener
            }

            if (umbralEncender >= umbralApagar) {
                etUmbralEncender.error = "Debe ser menor que el umbral de apagar"
                etUmbralEncender.requestFocus()
                return@setOnClickListener
            }

            dbRef.child("umbral_encender").setValue(umbralEncender)
            dbRef.child("umbral_apagar").setValue(umbralApagar)

            Toast.makeText(
                this,
                "Umbrales guardados correctamente",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}