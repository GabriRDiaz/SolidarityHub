package com.upv.solidarityHub.ui.main


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.upv.solidarityHub.R
import com.upv.solidarityHub.databinding.ActivityMainBinding
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.ui.modificarPerfil.ModificarPerfilActivity


class Main : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val hView = navigationView.getHeaderView(0)
        val nav_modButton = hView.findViewById<View>(R.id.modPerfilButton) as TextView
        val nav_userWelcomeText = hView.findViewById<View>(R.id.userWelcomeText) as TextView

        nav_modButton.text = "Modificar Perfil"
        nav_userWelcomeText.text = "Bienvenido, " + SupabaseAPI().getLogedUser()!!.nombre


        nav_modButton.setOnClickListener {
            intent = Intent(this, ModificarPerfilActivity()::class.java)
            startActivity(intent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}