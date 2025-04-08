package com.upv.solidarityHub

import android.os.Bundle
//import android.util.Log
import android.widget.TextView
//import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.navigateUp
//import androidx.navigation.ui.setupActionBarWithNavController
//import com.upv.solidarityHub.databinding.ActivityRegistroGrupoVoluntariosBinding
import com.upv.solidarityHub.databinding.ContentDetallesGrupoVoluntariosBinding
//import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.model.GrupoDeAyuda
import java.text.SimpleDateFormat
//import io.github.jan.supabase.SupabaseClient
//import io.github.jan.supabase.SupabaseClientBuilder
//import java.util.Date

class DetallesGrupoVoluntarios : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ContentDetallesGrupoVoluntariosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ContentDetallesGrupoVoluntariosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbar)
        //var bd= SupabaseAPI();
        //Log.d("DEBUG", "Trying to access database" +bd.toString())
        val grupo = intent.getParcelableExtra<GrupoDeAyuda>("GrupoDeAyuda") ?: return

        findViewById<TextView>(R.id.textoNombreGrupo).text = "Grupo ${grupo.id}"
        findViewById<TextView>(R.id.textView11).text = "Ubicación: ${grupo.ubicacion}"
        findViewById<TextView>(R.id.textView19).text = "${grupo.sesion}"
        findViewById<TextView>(R.id.textView20).text = "Tamaño: ${grupo.tamanyo}"
        // Formatear la fecha y mostrarla en el TextView
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = dateFormat.format(grupo.fecha_creacion)
        findViewById<TextView>(R.id.editTextDate).text = "$formattedDate"

        findViewById<TextView>(R.id.textView21).text = grupo.descripcion
    }
}