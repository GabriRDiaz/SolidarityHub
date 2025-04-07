package com.upv.solidarityHub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
//import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
//import androidx.navigation.ui.navigateUp
//import androidx.navigation.ui.setupActionBarWithNavController
import com.upv.solidarityHub.databinding.ContentGruposAyuda2Binding
//import com.upv.solidarityHub.databinding.ActivityGruposAyuda2Binding

class GruposAyuda : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ContentGruposAyuda2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ContentGruposAyuda2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        //setSupportActionBar(binding.toolbar)
    }
}