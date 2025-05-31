package com.upv.solidarityHub

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.factory.habilidad.HabilidadFactoryProvider
import com.upv.solidarityHub.persistence.model.Habilidad
import com.upv.solidarityHub.ui.modificarPerfil.ModificarPerfilViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BdUnitTest {
    @get:Rule var rule: TestRule = InstantTaskExecutorRule()
    private val numTestUsuarios = 9
    private var testUsuarios = registrarTestUsuarios()

    data class UsuarioYHabilidad(val usuario: Usuario, val habilidades: List<Habilidad>)


    @Test
    fun testUsuariosRegistradosCorrectamente() {
        eliminarTestUsuariosRegistrados()

        testUsuarios = registrarTestUsuarios()
        kotlin.test.assertEquals(testUsuarios.size, numTestUsuarios)
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

            val usuarioViewModel = ModificarPerfilViewModel()
            usuarioViewModel.setOriginalUsuario(usuario)
            usuarioViewModel.setOriginalUserValues()
            usuarioViewModel.updateMunicipiosList(arrayOf("Pedreguer", "Alaquas"))

            usuarioViewModel.updateHabilidades(habilidadesModificadas)
            usuarioViewModel.updateNombre(usuarioModificado.nombre)
            usuarioViewModel.updateApellidos(usuarioModificado.apellidos)
            usuarioViewModel.updateOldContrasena(usuario.password)
            usuarioViewModel.updateContrasena(usuarioModificado.password)
            usuarioViewModel.updateFechaNacimiento(usuarioModificado.nacimiento)
            usuarioViewModel.updateMunicipio(usuarioModificado.municipio)

            if(!usuarioViewModel.confirmar()) throw Exception(
                "El ViewModel ha rechazado la modificación, probablemente hay campos que considera no válidos")
        }

        assertTrue(usuariosEnBD(usuariosModificados))
        assertTrue(usuariosNOEnBD(testUsuarios))

        eliminarTestUsuariosRegistrados()
    }
    @Test
    fun usuariosNoValidosNoModificados() {
        eliminarTestUsuariosRegistrados()

        testUsuarios = registrarTestUsuarios()
        var usuariosModificados = mutableListOf<UsuarioYHabilidad>()
        var encontradoUnoQueNoFalla = false

        var orden = 0
        for(tuplaUsuario in testUsuarios) {
            val usuario = tuplaUsuario.usuario

            var correo = tuplaUsuario.usuario.correo

            var nombre = usuario.nombre + "Modificado"
            if(orden == 0) nombre += "."

            var apellidos = usuario.apellidos + "Modificado"
            if(orden == 1) apellidos += "6"

            var contrasena = usuario.password + "Modificado"
            if(orden == 2) contrasena = "contrasenaenminusculas"

            var oldContrasena = usuario.password
            if(orden ==3) oldContrasena += "string que la hace inválida"


            orden++
            orden %= 4

            var fecha = "2-2-2004"
            var municipio = "Pedreguer"
            val usuarioModificado = Usuario(correo,nombre,apellidos,contrasena,fecha,municipio)

            val factory = HabilidadFactoryProvider.getFactory()

            var habilidadesModificadas = mutableListOf<Habilidad>()
            habilidadesModificadas.add(factory.createHabilidad("Veterinaria", 2, 2))
            habilidadesModificadas.add(factory.createHabilidad("Medicina", 2, 2))
            habilidadesModificadas.add(factory.createHabilidad("Conducción", 2, 2))

            usuariosModificados.add(UsuarioYHabilidad(usuarioModificado, habilidadesModificadas))

            val usuarioViewModel = ModificarPerfilViewModel()
            usuarioViewModel.setOriginalUsuario(usuario)
            usuarioViewModel.setOriginalUserValues()
            usuarioViewModel.updateMunicipiosList(arrayOf("Pedreguer", "Alaquas"))

            usuarioViewModel.updateHabilidades(habilidadesModificadas)
            usuarioViewModel.updateNombre(usuarioModificado.nombre)
            usuarioViewModel.updateApellidos(usuarioModificado.apellidos)
            usuarioViewModel.updateOldContrasena(oldContrasena)
            usuarioViewModel.updateContrasena(usuarioModificado.password)
            usuarioViewModel.updateFechaNacimiento(usuarioModificado.nacimiento)
            usuarioViewModel.updateMunicipio(usuarioModificado.municipio)

            encontradoUnoQueNoFalla = encontradoUnoQueNoFalla || usuarioViewModel.confirmar()
        }

        assertFalse(encontradoUnoQueNoFalla)
        assertTrue(usuariosEnBD(testUsuarios))
        assertTrue(usuariosNOEnBD(usuariosModificados))

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
        var numUsuario = ""
        for(i in 0..<numTestUsuarios) {
            numUsuario += "I"

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