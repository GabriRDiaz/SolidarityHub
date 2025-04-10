package com.upv.solidarityHub

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
import java.text.SimpleDateFormat
//import io.github.jan.supabase.SupabaseClient
//import io.github.jan.supabase.SupabaseClientBuilder
//import java.util.Date

class DetallesGrupoVoluntarios : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ContentDetallesGrupoVoluntariosBinding

    private val db: SupabaseAPI = SupabaseAPI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ContentDetallesGrupoVoluntariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbar)
        //var bd= SupabaseAPI()
        //val grupo = intent.getParcelableExtra<GrupoDeAyuda>("GrupoDeAyuda") ?: return
        val grupoId = intent.getIntExtra("grupoId", -1)
        if (grupoId == -1) {
            Toast.makeText(this, "ID del grupo no válido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        // Cargar el grupo desde Supabase
        lifecycleScope.launch {
            val grupo = db.getGrupoById(grupoId)
            if (grupo == null) {
                Toast.makeText(
                    this@DetallesGrupoVoluntarios,
                    "Grupo no encontrado",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                return@launch
            }
            // Mostrar datos del grupo en la UI
            binding.textoNombreGrupo.text = "Grupo ${grupo.id}"
            binding.textView11.text = "Ubicación: ${grupo.ubicacion}"
            binding.textViewSesion.text = grupo.sesion
            binding.textViewTamanyo.text = "${grupo.tamanyo} personas"
            // Formatear la fecha y mostrarla en el TextView
            //val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            //val formattedDate = dateFormat.format(grupo.fecha_creacion)
            //binding.textViewFecha.text = formattedDate
            binding.textViewFecha.text= grupo.fecha_creacion

            binding.textViewDescripcion.text = grupo.descripcion
        }

        binding.botonVolver.setOnClickListener {
            finish()
        }
    }
}