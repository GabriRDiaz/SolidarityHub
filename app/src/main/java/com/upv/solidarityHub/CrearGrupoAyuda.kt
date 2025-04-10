package com.upv.solidarityHub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.navigateUp
//import androidx.navigation.ui.setupActionBarWithNavController
//import com.upv.solidarityHub.databinding.ActivityCrearGrupoAyudaBinding
import com.upv.solidarityHub.databinding.ContentCrearGrupoAyudaBinding
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlin.random.Random
import java.util.Date

class CrearGrupoAyuda : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ContentCrearGrupoAyudaBinding
    private val db: SupabaseAPI = SupabaseAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ContentCrearGrupoAyudaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbar)
        binding.botonAceptar.setOnClickListener {
            val descripcion = binding.root.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.textoDescripcion)?.text.toString()
            val ubicacion = binding.root.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.textoMunicipio)?.text.toString()
            val sesion = binding.root.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.textoSesion)?.text.toString()
            val tamanyo = 1 // Por defecto

            if (descripcion.isNotEmpty() && ubicacion.isNotEmpty() && sesion.isNotEmpty()) {
                lifecycleScope.launch {
                    val id = Random.nextInt(0, 9999) // Id aleatorio
                    val success = db.registrarGrupo(
                        id = id,
                        descripcion = descripcion,
                        ubicacion = ubicacion,
                        fecha_creacion = Date().toString(),
                        sesion = sesion,
                        tamanyo = tamanyo
                    )
                    if (success) {
                        Toast.makeText(this@CrearGrupoAyuda, "Grupo creado correctamente", Toast.LENGTH_LONG).show()
                        finish() // cerrar la actividad
                    } else {
                        Toast.makeText(this@CrearGrupoAyuda, "Error al crear el grupo", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        binding.botonCancelar.setOnClickListener {
            finish()
        }

    }

}