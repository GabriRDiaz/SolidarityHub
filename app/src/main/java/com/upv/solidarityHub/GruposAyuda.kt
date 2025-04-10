package com.upv.solidarityHub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.navigateUp
//import androidx.navigation.ui.setupActionBarWithNavController
import com.upv.solidarityHub.databinding.ContentGruposAyuda2Binding
//import com.upv.solidarityHub.databinding.ActivityGruposAyuda2Binding
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.ArrayAdapter
import com.upv.solidarityHub.persistence.GrupoDeAyuda
import android.content.Intent

class GruposAyuda : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ContentGruposAyuda2Binding
    private val db: SupabaseAPI = SupabaseAPI()
    private var listaGrupos: List<GrupoDeAyuda> = listOf()
    private var grupoSeleccionado: GrupoDeAyuda? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ContentGruposAyuda2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbar)
        obtenerGrupos()

        binding.botonVerDetalles2.setOnClickListener {
            grupoSeleccionado?.let {
                val intent = Intent(this, DetallesGrupoVoluntarios::class.java)
                intent.putExtra("idGrupo", it.id)
                startActivity(intent)
            } ?: Toast.makeText(this, "Selecciona un grupo primero", Toast.LENGTH_SHORT).show()
        }

        binding.botonUnirse2.setOnClickListener {
            grupoSeleccionado?.let {
                Toast.makeText(this, "Te has unido al grupo ${it.id}", Toast.LENGTH_SHORT).show()
                // Aquí podrías llamar a tu función en Supabase para unirte de verdad
            } ?: Toast.makeText(this, "Selecciona un grupo primero", Toast.LENGTH_SHORT).show()
        }

        binding.botonCrearGrupo.setOnClickListener {
            val intent = Intent(this, CrearGrupoAyuda::class.java)
            startActivity(intent)
        }
    }

    private fun obtenerGrupos() {
        lifecycleScope.launch {
            val grupos = db.getAllGrupos() // Método que obtiene todos los grupos de la base de datos
            if (grupos.isNullOrEmpty()) {
                Toast.makeText(this@GruposAyuda, "No hay grupos disponibles", Toast.LENGTH_SHORT).show()
            } else {
                // Crear una lista con solo los nombres de los grupos para mostrar
                val nombresGrupos = grupos.map { "Grupo ${it.id} - ${it.sesion}" }

                // Crear un ArrayAdapter para mostrar los nombres de los grupos
                val adapter = ArrayAdapter(
                    this@GruposAyuda, // Contexto
                    android.R.layout.simple_list_item_1, // Layout simple de una línea
                    nombresGrupos // Lista de nombres de los grupos
                )

                // Asignar el adaptador al ListView
                binding.listaGruposAyuda.adapter = adapter
            }
        }
    }
}