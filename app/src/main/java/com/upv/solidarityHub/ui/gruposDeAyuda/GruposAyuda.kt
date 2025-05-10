package com.upv.solidarityHub.ui.gruposDeAyuda

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
import android.util.Log
import android.widget.ListView
import com.upv.solidarityHub.R
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.ui.gruposDeAyuda.crearGrupoAyuda.CrearGrupoAyuda
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.lang.Integer.parseInt

class GruposAyuda() : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ContentGruposAyuda2Binding
    private val db: SupabaseAPI = SupabaseAPI()
    private var listaGrupos: List<GrupoDeAyuda> = listOf()
    private var grupoSeleccionado: GrupoDeAyuda? = null
    private lateinit var usuario: Usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        usuario = intent.getParcelableExtra<Usuario>("usuario")!!

        super.onCreate(savedInstanceState)

        binding = ContentGruposAyuda2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbar)
        obtenerGrupos()

        binding.botonVerDetalles2.setOnClickListener {
            grupoSeleccionado?.let {
                lifecycleScope.launch {
                    val resultado = db.unirseAGrupo(usuario.correo, it.id)
                    if (resultado) {
                        Toast.makeText(this@GruposAyuda, "Te has unido al grupo ${it.id}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@GruposAyuda, "Error al unirte al grupo", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: Toast.makeText(this, "Selecciona un grupo primero", Toast.LENGTH_SHORT).show()
        }

        binding.botonUnirse2.setOnClickListener {
            grupoSeleccionado?.let {
                Toast.makeText(this, "Te has unido al grupo ${grupoSeleccionado!!.id}", Toast.LENGTH_SHORT).show()
            } ?: Toast.makeText(this, "Selecciona un grupo primero", Toast.LENGTH_SHORT).show()
        }

        binding.botonCrearGrupo.setOnClickListener {
            val intent = Intent(this, CrearGrupoAyuda::class.java)
            startActivity(intent)
        }

        binding.botonVerGrupos.setOnClickListener {
            mostrarGruposInscritos()
        }

        findViewById<ListView>(R.id.listaGruposAyuda).setOnItemClickListener { _, _, position, _ ->
            //TODO: SOLUCIÓN MUY ESTÚPIDA PARA SALIR DEL PASO POR AHORA, REFACTORIZAR!!!!
            var grupo = binding.listaGruposAyuda.adapter.getItem(position)
            runBlocking {
                val deferred1 = async {
                    Log.d("DEBUG",parseInt((grupo as String).substring(6,7)).toString())
                    grupoSeleccionado = db.getGrupoById(parseInt((grupo as String).substring(6,7)))

                }
                deferred1.await()
            }

        }

    }

    private fun obtenerGrupos() {
        lifecycleScope.launch {
            val grupos = db.getAllGrupos() // Método que obtiene todos los grupos de la base de datos
            if (grupos.isNullOrEmpty()) {
                Toast.makeText(this@GruposAyuda, "No hay grupos disponibles", Toast.LENGTH_SHORT).show()
            } else {
                val nombresGrupos = grupos.map { "Grupo ${it.id} - ${it.sesion}" }

                val adapter = ArrayAdapter(
                    this@GruposAyuda, // Contexto
                    android.R.layout.simple_list_item_1, // Layout simple de una línea
                    nombresGrupos // Lista de nombres de los grupos
                )

                binding.listaGruposAyuda.adapter = adapter
            }
        }
    }

    private fun mostrarGruposInscritos() {
        lifecycleScope.launch {
            db.initializeDatabase()
            val usuarioId = usuario.correo
            if (usuarioId != null) {
                val gruposInscritos = db.getGruposusuario(usuarioId)
                if (gruposInscritos.isNullOrEmpty()) {
                    Toast.makeText(this@GruposAyuda, "No estás inscrito en ningún grupo", Toast.LENGTH_SHORT).show()
                } else {
                    val nombresGruposInscritos =
                        gruposInscritos.map { "Grupo ${it.id} - ${it.sesion}" }

                    val adapter = ArrayAdapter(
                        this@GruposAyuda,
                        android.R.layout.simple_list_item_1,
                        nombresGruposInscritos
                    )

                    binding.listaGruposAyuda.adapter = adapter
                }
            } else {
                Toast.makeText(this@GruposAyuda, "No hay usuario logueado", Toast.LENGTH_SHORT).show()
            }
        }
        }
}