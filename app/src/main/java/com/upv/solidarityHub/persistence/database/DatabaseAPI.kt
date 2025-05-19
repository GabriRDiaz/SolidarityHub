package com.upv.solidarityHub.persistence.database

import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.GrupoDeAyuda
import com.upv.solidarityHub.persistence.SolicitudAyuda
import com.upv.solidarityHub.persistence.tieneAsignado
import com.upv.solidarityHub.persistence.database.SupabaseAPI.reqDB
import com.upv.solidarityHub.persistence.database.SupabaseAPI.taskDB
import com.upv.solidarityHub.persistence.model.Desaparecido
import com.upv.solidarityHub.persistence.model.Habilidad
import com.upv.solidarityHub.persistence.taskReq
import java.util.Date

interface DatabaseAPI {

    fun initializeDatabase()

    public suspend fun getUsuarioByCorreo(correo: String): Usuario?
    public suspend fun getBalizaByName(name: String): Baliza?
    public suspend fun deleteBaliza(name: String): Boolean
    public suspend fun getAllBalizas(): List<Baliza>?
    public suspend fun addBaliza(id: Int, latitud: Double,longitud: Double, nombre:String,tipo: String,descripcion:String): Boolean
    public suspend fun registerUsuario(correo: String, nombre: String, apellidos: String, password: String, nacimiento:String, municipio: String):Boolean
    public suspend fun getGrupoById(id:Int): GrupoDeAyuda?
    public suspend fun registrarGrupo(id:Int, descripcion: String, ubicacion: String, fecha_creacion: String, sesion: String, tamanyo: Int): Boolean
    public suspend fun getAllGrupos(): List<GrupoDeAyuda>?
    public suspend fun getGruposusuario(usuario: String): List<GrupoDeAyuda>?
    public suspend fun loginUsuario(correo: String, contrasena: String): Usuario?
    public suspend fun registrarHabilidades(habilidades:List<Habilidad>, usuario:Usuario): Boolean
    public suspend fun registrarReq(req : SolicitudAyuda): Boolean
    public suspend fun getLastId(table: String): Int?
    public suspend fun getHelpReqs(task: taskReq) : List<SupabaseAPI.reqDB>?
    public suspend fun registrarTask(task : taskReq, req: SupabaseAPI.reqDB): SupabaseAPI.taskDB?
    public suspend fun helpReqsToTasks(task: taskReq): List<SupabaseAPI.taskDB>?
    public suspend fun getTaskOGReqs(): List<Int>?
    public suspend fun getAllUsers(): List<Usuario>?
    public suspend fun createIsAssigned(idTask: Int, user: Usuario)
    public suspend fun getTaskById(id: Int): taskDB?
    public suspend fun getHelpReqById(id: Int): reqDB?
    public suspend fun registerDesaparecido(desaparecido: Desaparecido, ultimaUbi: Baliza?)
    public suspend fun unirseAGrupo(usuario: String, grupoId: Int): Boolean
    public suspend fun getAsignacionesUsuario(userId: String): List<tieneAsignado>?
    public suspend fun eliminarAsignacion(id: Int): Boolean
    public suspend fun aceptarTarea(asignacionId: Int): Boolean
    public suspend fun salirDelGrupo(usuario: String, grupo: Int): Boolean
    public fun getHabilidadesOfUser(correo: String): List<Habilidad>?
    public fun updateUsuario(usuario: Usuario, habilidades: List<Habilidad>?): Boolean
}