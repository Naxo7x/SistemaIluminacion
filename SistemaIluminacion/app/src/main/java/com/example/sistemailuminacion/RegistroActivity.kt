package com.example.sistemailuminacion

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegistroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registros)

        val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("registros")

        val etTituloRegistro: EditText = findViewById(R.id.etTituloRegistro)
        val etDescripcionRegistro: EditText = findViewById(R.id.etDescripcionRegistro)
        val btnGuardarRegistro: Button = findViewById(R.id.btnGuardarRegistro)
        val btnEliminarRegistro: Button = findViewById(R.id.btnEliminarRegistro)
        val lvRegistros: ListView = findViewById(R.id.lvRegistros)

        val listaRegistros = mutableListOf<Registro>()
        val listaTexto = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaTexto)
        lvRegistros.adapter = adapter

        var registroSeleccionado: Registro? = null

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaRegistros.clear()
                listaTexto.clear()

                for (hijo in snapshot.children) {
                    try {
                        val clave = hijo.key ?: continue
                        val titulo = hijo.child("titulo").getValue(String::class.java)?.trim() ?: ""
                        val descripcion = hijo.child("descripcion").getValue(String::class.java)?.trim() ?: ""
                        val idGuardado = hijo.child("id").getValue(String::class.java)?.trim()


                        if (titulo.isBlank() && descripcion.isBlank()) {
                            continue
                        }

                        val idFinal = if (!idGuardado.isNullOrBlank()) idGuardado else clave
                        val reg = Registro(
                            id = idFinal,
                            titulo = titulo,
                            descripcion = descripcion
                        )

                        listaRegistros.add(reg)
                        listaTexto.add("Lugar: ${reg.titulo}\nDescripcion: ${reg.descripcion}")
                    } catch (_: Exception) {

                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@RegistroActivity,
                    "Error al cargar registros",
                    Toast.LENGTH_LONG
                ).show()
            }
        })

        btnGuardarRegistro.setOnClickListener {
            val titulo = etTituloRegistro.text.toString().trim()
            val descripcion = etDescripcionRegistro.text.toString().trim()

            if (titulo.isBlank() || descripcion.isBlank()) {
                Toast.makeText(
                    this,
                    "Error: completa el lugar y la descripcion",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val id = registroSeleccionado?.id ?: dbRef.push().key
            if (id == null) {
                Toast.makeText(
                    this,
                    "Error al generar ID del registro",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val registro = Registro(
                id = id,
                titulo = titulo,
                descripcion = descripcion
            )

            dbRef.child(id).setValue(registro).addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Registro guardado correctamente",
                    Toast.LENGTH_LONG
                ).show()
                registroSeleccionado = null
                etTituloRegistro.setText("")
                etDescripcionRegistro.setText("")
            }
        }


        lvRegistros.setOnItemClickListener { _, _, position, _ ->
            if (position in listaRegistros.indices) {
                val item = listaRegistros[position]
                registroSeleccionado = item
                etTituloRegistro.setText(item.titulo)
                etDescripcionRegistro.setText(item.descripcion)
            }
        }


        btnEliminarRegistro.setOnClickListener {
            val seleccionado = registroSeleccionado
            if (seleccionado == null) {
                Toast.makeText(
                    this,
                    "Selecciona un registro de la lista primero",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Confirmar eliminacion")
                .setMessage("¿Seguro que deseas eliminar \"${seleccionado.titulo}\"?")
                .setPositiveButton("Eliminar") { _, _ ->
                    dbRef.child(seleccionado.id).removeValue().addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Registro eliminado",
                            Toast.LENGTH_LONG
                        ).show()
                        registroSeleccionado = null
                        etTituloRegistro.setText("")
                        etDescripcionRegistro.setText("")
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}