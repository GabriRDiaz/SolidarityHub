package com.upv.solidarityHub

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.content.ContextCompat
import com.upv.solidarityHub.persistence.Usuario

class HabilidadesActivity() : ComponentActivity() {
    private lateinit var usr:Usuario
    override fun onCreate(savedInstanceState: Bundle?) {
        usr = intent.getParcelableExtra<Usuario>("usuario")!!
        super.onCreate(savedInstanceState)
        setContent {
                HabilidadesForm(usr,this)
        }
    }

    fun goToStopGap() {
        val intent = Intent(this, StopGap::class.java)
        startActivity(intent)
    }
}