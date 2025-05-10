package com.upv.solidarityHub.ui.gruposDeAyuda.crearGrupoAyuda

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
            val descripcion = binding.textoDescripcion.text?.toString()?.trim() ?: ""
            val ubicacion = binding.textoMunicipio.text?.toString()?.trim() ?: ""
            val sesion = binding.textoSesion.text?.toString()?.trim() ?: ""
            val tamanyo = 1

            if (descripcion.isEmpty() || ubicacion.isEmpty() || sesion.isEmpty()) {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val id = Random.nextInt(0, 9999)
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
                        finish()
                    } else {
                        Toast.makeText(this@CrearGrupoAyuda, "Error al crear el grupo", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@CrearGrupoAyuda, "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        binding.botonCancelar.setOnClickListener {
            finish()
        }

    }

}