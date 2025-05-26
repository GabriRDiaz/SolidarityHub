package com.upv.solidarityHub

import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.factory.habilidad.HabilidadFactoryProvider
import com.upv.solidarityHub.persistence.model.Habilidad
import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BdUnitTest {
    private val numTestUsuarios = 9
    private var testUsuarios = registrarTestUsuarios()

    data class UsuarioYHabilidad(val usuario: Usuario, val habilidades: List<Habilidad>)

    @Test
    fun testUsuariosRegistradosCorrectamente() {
        eliminarTestUsuariosRegistrados()

        testUsuarios = registrarTestUsuarios()
        kotlin.test.assertTrue(usuariosEnBD(testUsuarios))

        eliminarTestUsuariosRegistrados()
    }

    @Test
    fun usuariosModificadosCorrectamente() {
        eliminarTestUsuariosRegistrados()

        testUsuarios = registrarTestUsuarios()
        var usuariosModificados = mutableListOf<UsuarioYHabilidad>()
        for(tuplaUsuario in testUsuarios) {
            val usuario = tuplaUsuario.usuario
            val usuarioModificado = Usuario(
                tuplaUsuario.usuario.correo,
                usuario.nombre + "Modificado",
                usuario.apellidos + "Modificado",
                usuario.password + "Modificado",
                "2-2-2004",
                "Pedreguer")

            val factory = HabilidadFactoryProvider.getFactory()

            var habilidadesModificadas = mutableListOf<Habilidad>()
            habilidadesModificadas.add(factory.createHabilidad("Veterinaria", 2, 2))
            habilidadesModificadas.add(factory.createHabilidad("Medicina", 2, 2))
            habilidadesModificadas.add(factory.createHabilidad("Conducción", 2, 2))

            usuariosModificados.add(UsuarioYHabilidad(usuarioModificado, habilidadesModificadas))

            SupabaseAPI().updateUsuario(usuarioModificado, habilidadesModificadas)

        }
        assertTrue(usuariosEnBD(usuariosModificados))
        assertTrue(usuariosNOEnBD(testUsuarios))

        eliminarTestUsuariosRegistrados()
    }



    fun usuariosEnBD(tuplasUsuarios: List<UsuarioYHabilidad>): Boolean {
        val usuariosEnBD = mutableListOf<UsuarioYHabilidad>()

        for(tuplaUsuario in tuplasUsuarios) {
            var usuarioBD: Usuario?
            var habilidades: List<Habilidad>

            try {
                runBlocking {
                    usuarioBD = SupabaseAPI().getUsuarioByCorreo(tuplaUsuario.usuario.correo)
                    habilidades = SupabaseAPI().getHabilidadesOfUser(tuplaUsuario.usuario.correo)!!
                }

                usuariosEnBD.add(UsuarioYHabilidad(usuarioBD!!,habilidades))

            } catch(e: Exception) {return false}
        }
        return usuariosEnBD == tuplasUsuarios
    }

    fun usuariosNOEnBD(tuplasUsuarios: List<UsuarioYHabilidad>): Boolean {

        for(tuplaUsuario in tuplasUsuarios) {
            var usuarioBD: Usuario?
            var habilidades: List<Habilidad>

            try {
                runBlocking {
                    usuarioBD = SupabaseAPI().getUsuarioByCorreo(tuplaUsuario.usuario.correo)
                    habilidades = SupabaseAPI().getHabilidadesOfUser(tuplaUsuario.usuario.correo)!!
                }

                val usuarioEnBD = UsuarioYHabilidad(usuarioBD!!,habilidades)
                if(tuplaUsuario != usuarioEnBD) return true

            } catch(e: Exception) {return true}
        }
        return false
    }

    private fun registrarTestUsuarios(): List<UsuarioYHabilidad> {
        var res = mutableListOf<UsuarioYHabilidad>()

        for(i in 0..<numTestUsuarios) {
            var numUsuario = i + 1

            var correo = "correoTest" + numUsuario + "gmail.com"
            var nombre = "nombreTest" + numUsuario
            var apellidos = "apellidosTest" + numUsuario
            var password = "contraseñaTest." + numUsuario
            var nacimiento = "1-1-2004"
            var municipio = "Alaquas"

            var usuario = Usuario(correo,nombre, apellidos, password, nacimiento, municipio)

            val factory = HabilidadFactoryProvider.getFactory()
            var habilidades = mutableListOf<Habilidad>()
            habilidades.add(factory.createHabilidad("Veterinaria", 1, 1))
            habilidades.add(factory.createHabilidad("Medicina", 1, 1))
            habilidades.add(factory.createHabilidad("Conducción", 1, 1))

            SupabaseAPI().eliminarUsuario(correo)
            SupabaseAPI().registerUsuario(usuario)

            runBlocking {
                SupabaseAPI().registrarHabilidades(habilidades,usuario)
            }

            res.add(UsuarioYHabilidad(usuario,habilidades))
        }
        return res
    }

    private fun eliminarTestUsuariosRegistrados() {
        for(usuario in testUsuarios) {
            SupabaseAPI().eliminarUsuario(usuario.usuario.correo)
        }
    }

}