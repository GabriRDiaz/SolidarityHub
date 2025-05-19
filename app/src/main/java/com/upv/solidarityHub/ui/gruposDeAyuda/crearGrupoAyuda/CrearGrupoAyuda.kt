package com.upv.solidarityHub.ui.gruposDeAyuda.crearGrupoAyuda

import android.R
import android.os.Bundle
import android.widget.ArrayAdapter
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
        val opcionesHorario = listOf(
            "Mañana Temprana (6:00 - 9:00)",
            "Mañana (9:00 - 12:00)",
            "Mediodía (12:00 - 15:00)",
            "Tarde (15:00 - 18:00)",
            "Noche Temprana (18:00 - 21:00)",
            "Noche (21:00 - 00:00)",
            "Madrugada (00:00 - 6:00)"
        )
        val adapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, opcionesHorario)
        binding.dropdownHorario.setAdapter(adapter)

        binding.botonAceptar.setOnClickListener {
            val descripcion = binding.textoDescripcion.text?.toString()?.trim() ?: ""
            val ubicacion = binding.textoMunicipio.text?.toString()?.trim() ?: ""
            val sesion = binding.dropdownHorario.text?.toString()?.trim() ?: ""
            val tamanyo = 1

            if (descripcion.isEmpty() || ubicacion.isEmpty() || sesion.isEmpty() || sesion !in opcionesHorario) {
                Toast.makeText(this, "Rellena todos los campos con opciones validas", Toast.LENGTH_SHORT).show()
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