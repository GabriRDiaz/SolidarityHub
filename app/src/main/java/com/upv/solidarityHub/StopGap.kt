package com.upv.solidarityHub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.upv.solidarityHub.persistence.Usuario

class StopGap : AppCompatActivity() {
    private lateinit var usr: Usuario
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_stop_gap)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usr = intent.getParcelableExtra<Usuario>("usuario")!!

        findViewById<Button>(R.id.stgMapButton).setOnClickListener {
            val intent = Intent(this, MapaGenerico::class.java)
            intent.putExtra("usuario", usr)
            startActivity(intent)
        }

        findViewById<Button>(R.id.stgSolAyudaButton).setOnClickListener {
            val intent = Intent(this, SolAyuda::class.java)
            intent.putExtra("usuario", usr)
            startActivity(intent)
        }

        findViewById<Button>(R.id.stgGruposButton).setOnClickListener {
            val intent = Intent(this, GruposAyuda::class.java)
            intent.putExtra("usuario", usr)
            startActivity(intent)
        }
    }
}