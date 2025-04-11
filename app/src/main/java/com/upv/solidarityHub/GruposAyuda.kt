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
//import io.github.jan.supabase.SupabaseClient
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.ArrayAdapter
import com.upv.solidarityHub.persistence.GrupoDeAyuda
import android.content.Intent
import io.github.jan.supabase.auth.auth

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

        binding.botonVerGrupos.setOnClickListener {
            mostrarGruposInscritos()
        }

        binding.listaGruposAyuda.setOnItemClickListener { _, _, position, _ ->
            grupoSeleccionado = listaGrupos.getOrNull(position)
        }
    }

    private fun obtenerGrupos() {
        lifecycleScope.launch {
            try {
                val grupos = db.getAllGrupos()
                if (grupos.isNullOrEmpty()) {
                    Toast.makeText(this@GruposAyuda, "No hay grupos disponibles", Toast.LENGTH_SHORT).show()
                } else {
                    listaGrupos = grupos
                    val nombresGrupos = grupos.map { "Grupo ${it.id} - ${it.sesion}" }
                    val adapter = ArrayAdapter(this@GruposAyuda, android.R.layout.simple_list_item_1, nombresGrupos)
                    binding.listaGruposAyuda.adapter = adapter
                }
            } catch (e: Exception) {
                Toast.makeText(this@GruposAyuda, "Error al obtener grupos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Función para mostrar los grupos en los que el usuario está inscrito
    private fun mostrarGruposInscritos() {
        lifecycleScope.launch {
            try {
                db.initializeDatabase()
                val usuarioId = db.supabase?.auth?.currentUserOrNull()?.id
                if (usuarioId != null) {
                    val gruposInscritos = db.getGruposusuario(usuarioId)
                    if (gruposInscritos.isNullOrEmpty()) {
                        Toast.makeText(this@GruposAyuda, "No estás inscrito en ningún grupo", Toast.LENGTH_SHORT).show()
                    } else {
                        listaGrupos = gruposInscritos
                        val nombres = gruposInscritos.map { "Grupo ${it.id} - ${it.sesion}" }
                        val adapter = ArrayAdapter(this@GruposAyuda, android.R.layout.simple_list_item_1, nombres)
                        binding.listaGruposAyuda.adapter = adapter
                    }
                } else {
                    Toast.makeText(this@GruposAyuda, "No hay usuario logueado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@GruposAyuda, "Error al cargar tus grupos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        }
}