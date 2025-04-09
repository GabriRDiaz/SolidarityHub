package com.upv.solidarityHub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.navigateUp
//import androidx.navigation.ui.setupActionBarWithNavController
//import com.upv.solidarityHub.databinding.ActivityCrearGrupoAyudaBinding
import com.upv.solidarityHub.databinding.ContentCrearGrupoAyudaBinding

class CrearGrupoAyuda : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ContentCrearGrupoAyudaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ContentCrearGrupoAyudaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbar)
    }
}