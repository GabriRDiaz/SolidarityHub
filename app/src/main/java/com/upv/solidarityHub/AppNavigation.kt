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
    NavHost(navController = navController, startDestination = "SolAyuda") {
        composable("register") {
            context.startActivity(Intent(context, Registro::class.java))
        }
        //composable("skills_form") {
        //    HabilidadesForm()
        //}

        composable("login") {
            context.startActivity(Intent(context, Login::class.java))
        }

        composable("SolAyuda"){
            context.startActivity(Intent(context, SolAyuda::class.java))
        }

        composable("MapaGenerico") {
            context.startActivity(Intent(context, MapaGenerico::class.java))
        }

        composable("CrearGrupoAyuda") {
            context.startActivity(Intent(context, CrearGrupoAyuda::class.java))
        }

        composable("DetallesGrupoVoluntarios") {
            context.startActivity(Intent(context, DetallesGrupoVoluntarios::class.java))
        }

        composable("GruposAyuda") {
            context.startActivity(Intent(context, GruposAyuda::class.java))
        }
    }
}
