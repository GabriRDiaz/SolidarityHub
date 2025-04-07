package com.upv.solidarityHub

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = "register") {
        composable("register") {
            context.startActivity(Intent(context, Registro::class.java))
        }
        composable("skills_form") {
            HabilidadesForm()
        }

    }
}
