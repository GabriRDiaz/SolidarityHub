package com.upv.solidarityHub.ui.gruposDeAyuda.detallesGrupoVoluntarios

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.upv.solidarityHub.databinding.ContentDetallesGrupoVoluntariosBinding
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.Usuario
import kotlinx.coroutines.launch

class DetallesGrupoVoluntarios : AppCompatActivity() {

    private lateinit var binding: ContentDetallesGrupoVoluntariosBinding
    private val db: SupabaseAPI = SupabaseAPI()
    private var grupoId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ContentDetallesGrupoVoluntariosBinding.inflate(layoutInflater)
        setContentView(binding.root)


        grupoId = intent.getIntExtra("grupoId", -1).takeIf { it != -1 }
            ?: run {
                Toast.makeText(this, "ID del grupo no válido", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        cargarDatosGrupo(grupoId)
        configurarBotones()
    }

    private fun cargarDatosGrupo(grupoId: Int) {
        lifecycleScope.launch {
            try {
                val grupo = db.getGrupoById(grupoId) ?: run {
                    Toast.makeText(this@DetallesGrupoVoluntarios,
                        "Grupo no encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                    return@launch
                }

                with(binding) {
                    textoNombreGrupo.text = "Grupo ${grupo.id}"
                    textViewUbicacion.text = grupo.ubicacion
                    textViewSesion.text = grupo.sesion
                    textViewTamanyo.text = "${grupo.tamanyo} personas"
                    textViewFecha.text = grupo.fecha_creacion
                    textViewDescripcion.text = grupo.descripcion
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@DetallesGrupoVoluntarios,
                    "Error al cargar el grupo: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
        }
    }

    private fun configurarBotones() {
        binding.botonVolver.setOnClickListener { finish() }

        binding.botonUnirse.setOnClickListener {
            lifecycleScope.launch {

            }
        }
    }
}