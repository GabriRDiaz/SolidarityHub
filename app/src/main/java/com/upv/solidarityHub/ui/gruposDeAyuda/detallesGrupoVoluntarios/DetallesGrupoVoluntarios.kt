package com.upv.solidarityHub.ui.gruposDeAyuda.detallesGrupoVoluntarios

import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
//import android.util.Log
//import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.navigateUp
//import androidx.navigation.ui.setupActionBarWithNavController
//import com.upv.solidarityHub.databinding.ActivityRegistroGrupoVoluntariosBinding
import com.upv.solidarityHub.databinding.ContentDetallesGrupoVoluntariosBinding
import com.upv.solidarityHub.persistence.database.SupabaseAPI


class DetallesGrupoVoluntarios : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ContentDetallesGrupoVoluntariosBinding

    private val db: SupabaseAPI = SupabaseAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ContentDetallesGrupoVoluntariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbar)
        val grupoId = intent.getIntExtra("grupoId", -1)
        if (grupoId == -1) {
            Toast.makeText(this, "ID del grupo no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        lifecycleScope.launch {
            try{
            val grupo = db.getGrupoById(grupoId)
            if (grupo == null) {
                Toast.makeText(this@DetallesGrupoVoluntarios, "Grupo no encontrado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                binding.textoNombreGrupo.text = "Grupo ${grupo.id}"
                binding.textView11.text = "Ubicación: ${grupo.ubicacion}"
                binding.textViewSesion.text = grupo.sesion
                binding.textViewTamanyo.text = "${grupo.tamanyo} personas"
                binding.textViewFecha.text = grupo.fecha_creacion
                binding.textViewDescripcion.text = grupo.descripcion
            }
        } catch (e: Exception) {
            Toast.makeText(this@DetallesGrupoVoluntarios, "Error al cargar el grupo: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
        }

        binding.botonVolver.setOnClickListener {
            finish()
        }
    }
}