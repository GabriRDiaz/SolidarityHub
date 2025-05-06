package com.upv.solidarityHub

import android.content.Intent
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.upv.solidarityHub.ui.login.Login
import com.upv.solidarityHub.ui.registro.Registro

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = "register") {
        composable("register") {
            context.startActivity(Intent(context, Registro::class.java))
        }
//        composable("skills_form") {
//            HabilidadesForm(
//                Usuario(
//                "test@mail.com",
//                "test",
//                "test",
//                "abc123.",
//                "04122001",
//                "Viveiro"
//            )
//            )
//        }

        composable("login") {
            context.startActivity(Intent(context, Login::class.java))
        }

        composable("SolAyuda"){
            AndroidView(factory = { context ->
                val view = LayoutInflater.from(context).
                inflate(R.layout.fragment_host_sol_ayuda,null,false)

                val fragment = SolAyudaFragment()
                (context as? AppCompatActivity)?.supportFragmentManager?.
                        beginTransaction()?.replace(R.id.fragment_container_view, fragment)?.addToBackStack(null)?.commit()
                view
            })
        }

        composable("MapaGenerico") {
            AndroidView(factory = { context ->
                val view = LayoutInflater.from(context).
                inflate(R.layout.fragment_host_mapa_generico,null,false)

                val fragment = SolAyudaFragment()
                (context as? AppCompatActivity)?.supportFragmentManager?.
                beginTransaction()?.replace(R.id.fragment_container_view, fragment)?.addToBackStack(null)?.commit()
                view
            })
        }

        composable("CrearGrupoAyuda") {
            AndroidView(factory = { context ->
                val view = LayoutInflater.from(context).
                inflate(R.layout.fragment_crear_grupo_ayuda,null,false)

                val fragment = CrearGrupoAyudaFragment()
                (context as? AppCompatActivity)?.supportFragmentManager?.
                beginTransaction()?.replace(R.id.fragment_container_view, fragment)?.addToBackStack(null)?.commit()
                view
            })
        }

        composable("DetallesGrupoVoluntarios") {
            AndroidView(factory = { context ->
                val view = LayoutInflater.from(context).
                inflate(R.layout.fragment_detalles_grupo_voluntarios,null,false)

                val fragment = DetallesGrupoVoluntariosFragment()
                (context as? AppCompatActivity)?.supportFragmentManager?.
                beginTransaction()?.replace(R.id.fragment_container_view, fragment)?.addToBackStack(null)?.commit()
                view
            })
        }

        composable("GruposAyuda") {
            AndroidView(factory = { context ->
                val view = LayoutInflater.from(context).
                inflate(R.layout.fragment_grupos_ayuda2,null,false)

                val fragment = GruposAyudaFragment()
                (context as? AppCompatActivity)?.supportFragmentManager?.
                beginTransaction()?.replace(R.id.fragment_container_view, fragment)?.addToBackStack(null)?.commit()
                view
            })
        }

        composable("GruposAyuda") {
            AndroidView(factory = { context ->
                val view = LayoutInflater.from(context).
                inflate(R.layout.fragment_grupos_ayuda2,null,false)

                val fragment = GruposAyudaFragment()
                (context as? AppCompatActivity)?.supportFragmentManager?.
                beginTransaction()?.replace(R.id.fragment_container_view, fragment)?.addToBackStack(null)?.commit()
                view
            })
        }

        composable("tempScreen") {
            AndroidView(factory = { context ->
                val view = LayoutInflater.from(context).
                inflate(R.layout.fragment_temp_task,null,false)

                val fragment = tempTaskFragment()
                (context as? AppCompatActivity)?.supportFragmentManager?.
                beginTransaction()?.replace(R.id.fragment_container_view, fragment)?.addToBackStack(null)?.commit()
                view
            })
        }
    }
}
