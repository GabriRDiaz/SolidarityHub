package com.upv.solidarityHub.ui.habilidades

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.ui.main.Main

class HabilidadesActivity() : ComponentActivity() {
    private lateinit var usr:Usuario
    override fun onCreate(savedInstanceState: Bundle?) {
        usr = SupabaseAPI().getLogedUser()!!
        super.onCreate(savedInstanceState)
        setContent {
                HabilidadesForm(usr,this)
        }
    }


    fun goToMain() {
        val intent = Intent(this, Main::class.java)
        intent.putExtra("usuario", usr)
        startActivity(intent)
    }
}