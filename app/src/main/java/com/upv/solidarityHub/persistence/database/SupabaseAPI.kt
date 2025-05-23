package com.upv.solidarityHub.persistence.database

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.persistence.FormaParte
import com.upv.solidarityHub.persistence.GrupoDeAyuda
import com.upv.solidarityHub.persistence.SolicitudAyuda
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.factory.habilidad.HabilidadFactory
import com.upv.solidarityHub.persistence.factory.habilidad.HabilidadFactoryImpl
import com.upv.solidarityHub.persistence.tieneAsignado
import com.upv.solidarityHub.persistence.model.DatabaseHabilidad
import com.upv.solidarityHub.persistence.model.Desaparecido
import com.upv.solidarityHub.persistence.model.Habilidad
import com.upv.solidarityHub.persistence.taskReq
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.ktor.http.cio.Request
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar


//public var supabase: SupabaseClient? = null;
private const val supabaseUrl = "https://jjmkaouvmwcakqusbabw.supabase.co"
private const val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImpqbWthb3V2bXdjYWtxdXNiYWJ3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDA0Nzg0MTIsImV4cCI6MjA1NjA1NDQxMn0.FwI-6QCb12bth5DnIJUARbZ74LDbxZ7g7Hls7D9xJcE"
private lateinit var logedUserCorreo: String

class SupabaseAPI : DatabaseAPI {

    public var supabase: SupabaseClient? = null;

    @Serializable
    data class reqDB(
        val id: Int?,
        val created_at: String? = null,
        val titulo : String,
        val descripcion : String,
        val categoria : String,
        val ubicacion : String,
        val generado_por: String? = null,
        val horario : String,
        val envergadura: String,
        val urgencia: String
    )

    @Serializable
    data class taskDB(
        val id: Int?,
        val created_at: String? = null,
        val og_req : Int?,
        val latitud: Double,
        val longitud : Double,
        val fecha_inicial : String,
        val fecha_final : String,
        val hora_inicio : String? = null,
    )

    @Serializable
    data class assignedDB(
        val id: Int?,
        val created_at: String? = null,
        val id_user : String?,
        val id_task: Int?,
        val estado: String
    )

    public override fun initializeDatabase() {

        if (supabase == null) {
            supabase = createSupabaseClient(supabaseUrl, supabaseKey) {
                install(Postgrest)
                install(Auth)
            }
        }

    }

    public fun setLogedUserCorreo(usr: String) {
        logedUserCorreo = usr
    }

    public fun getLogedUser(): Usuario {
        var res:Usuario
        runBlocking{
            res = getUsuarioByCorreo(logedUserCorreo)!!
        }
        return res
    }

    public override suspend fun getUsuarioByCorreo(correo: String): Usuario? {
        initializeDatabase()
        val usuario =
            supabase?.from("Usuario")?.select() {
            filter{
                eq("correo", correo)
            } }?.decodeSingle<Usuario>()

        return usuario;
    }

    public override suspend fun getBalizaByName(name: String): Baliza? {
        initializeDatabase()
        val baliza =
            supabase?.from("Baliza")?.select() {
                filter{
                    eq("nombre", name)
                } }?.decodeSingle<Baliza>()

        return baliza;
    }

    public override suspend fun deleteBaliza(name: String): Boolean {
        initializeDatabase()
        supabase?.from("Baliza")?.delete {
            filter {
                eq("nombre", name)
            }
        }
        return true
    }
    public override suspend fun getAllBalizas(): List<Baliza>? {
        initializeDatabase()
        val baliza = supabase?.from("Baliza")?.select(Columns.ALL)?.decodeList<Baliza>()
        return baliza;
    }

    public override suspend fun addBaliza(id: Int, latitud: Double, longitud: Double, nombre: String, tipo: String, descripcion: String): Boolean {
        initializeDatabase()
        try{
            val baliza =  Baliza(id,latitud,longitud,nombre,tipo,descripcion)
            supabase?.from("Baliza")?.insert(baliza)
            return true
        } catch(e:Exception) {return false}
    }
    public override suspend fun registerUsuario(correo: String, nombre: String, apellidos: String, password: String, nacimiento: String, municipio: String): Boolean {
        initializeDatabase()
        try{
            val Usuario = Usuario(correo, nombre, apellidos, password, nacimiento, municipio)
            supabase?.from("Usuario")?.insert(Usuario)
            return true
         } catch(e:Exception) {return false}
    }

    public override suspend fun getGrupoById(id: Int): GrupoDeAyuda? {
        initializeDatabase()
        val grupo =
            supabase?.from("GrupoDeAyuda")?.select() {
                filter{
                    eq("id", id)
                } }?.decodeSingle<GrupoDeAyuda>()

        return grupo;
    }

    public override suspend fun registrarGrupo(id: Int, descripcion: String, ubicacion: String, fecha_creacion: String, sesion: String, tamanyo: Int): Boolean {
        initializeDatabase()
        try{
            val grupo = GrupoDeAyuda(id, descripcion, ubicacion, fecha_creacion, sesion, tamanyo)
            supabase?.from("GrupoDeAyuda")?.insert(grupo)
            return true
        } catch(e:Exception) {return false}
    }

    public override suspend fun getAllGrupos(): List<GrupoDeAyuda>? {
        initializeDatabase()
        val grupo = supabase?.from("GrupoDeAyuda")?.select(Columns.ALL)?.decodeList<GrupoDeAyuda>()
        return grupo;
    }

    public override suspend fun getGruposusuario(usuario: String): List<GrupoDeAyuda>? {
        initializeDatabase()

        val relaciones = supabase?.from("FormaParte")
            ?.select()
            { filter { eq("user", usuario) } }
            ?.decodeList<FormaParte>() ?: return emptyList()

        val idsGrupo = relaciones.map { it.grupo }

        if (idsGrupo.isEmpty()) return emptyList()

        val grupos = mutableListOf<GrupoDeAyuda>()

        idsGrupo.forEach { grupoId ->
            supabase?.from("GrupoDeAyuda")
                ?.select()
                { filter { eq("id", grupoId) } }
                ?.decodeSingle<GrupoDeAyuda>()
                ?.let { grupos.add(it) }
        }

        return grupos
    }

    public override suspend fun registrarReq(req : SolicitudAyuda): Boolean {
        initializeDatabase()
        try{
            val reqDB =reqDB(getLastId("Solicituddeayuda")?.plus(1),null,req.titulo,req.desc,req.categoria,req.ubicacion,null,req.horario,req.tamanyo, req.urgencia)
            supabase?.from("Solicituddeayuda")?.insert(reqDB)
            System.out.println("Todo bien")
            return true
        } catch(e:Exception) {
            Log.d("DEBUG",e.toString())
            return false
        }
    }

    public override suspend fun getLastId(table: String): Int? {
        initializeDatabase()
        return try {
            supabase
                ?.from(table)
                ?.select(Columns.list("id")){
                    order(column = "id", order = Order.DESCENDING)
                    limit(1)
                }
                ?.decodeSingle<JsonObject>()  // Decode as single object
                ?.get("id")?.jsonPrimitive?.int
        } catch (e: Exception) {
            return -1  // Return null if there's any error or no records
        }
    }



    public override suspend fun loginUsuario(correo: String, contrasena: String): Usuario? {


        initializeDatabase()


        var usuario = getUsuarioByCorreo(correo)


        if (usuario != null) {


            if (usuario.password.equals(contrasena)) return usuario


        }
        return null
    }
        public override suspend fun registrarHabilidades(habilidades:List<Habilidad>, usuario:Usuario): Boolean {



            initializeDatabase()


            try{


                for( habilidad in habilidades) {


                    var dbHabilidad = DatabaseHabilidad(habilidad.nombre, usuario.correo, habilidad.competencia,habilidad.preferencia)


                    supabase?.from("Habilidad")?.insert(dbHabilidad)


                }








                return true


            } catch(e:Exception) {


                Log.d("DEBUG",e.toString()); return false}








    }

    public override suspend fun getHelpReqs(task: taskReq): List<reqDB>? {
        try{
            initializeDatabase()
            val currentTasks = getTaskOGReqs()
            val helpReqs = supabase?.from("Solicituddeayuda")?.select(Columns.ALL){
                filter{
                    if(task.cat != null) run {
                        reqDB::categoria eq task.cat
                    }

                    if(task.town != null) run {
                        reqDB::ubicacion eq task.town
                    }

                    if(task.priority != null) run {
                        reqDB::urgencia eq task.priority
                    }

                    if(task.schedule != null) run{
                        reqDB::horario eq task.schedule
                    }

                    if(task.size != null) run{
                        reqDB::envergadura eq task.size
                    }

                    if (currentTasks != null && currentTasks.isNotEmpty()) {
                            and {
                                currentTasks.forEach { taskOG ->
                                    reqDB::id neq taskOG
                                }
                            }
                    }



                }


            }

            if (helpReqs != null) {
                return helpReqs.decodeList<reqDB>()
            }
            return null
        }
        catch(e: Exception){
            Log.d("DEBUG",e.toString())
            return null
        }

    }

    public override suspend fun registrarTask(task : taskReq, req : reqDB): taskDB? {
        initializeDatabase()
        try{
            val date = task.calendarToDateString(task.date)
            val taskDB =taskDB(getLastId("Task")?.plus(1),null,req.id,task.lat,task.long,date, date, "10:00")

            try{
                supabase?.from("Task")?.insert(taskDB)
            }
            catch(e:Exception){
                Log.d("DEBUG", "Error during operation: ${e.localizedMessage}")
            }
            System.out.println("Todo bien")
            return taskDB
        } catch(e:Exception) {
            Log.d("DEBUG", "Error during operation: ${e.localizedMessage}")
            e.printStackTrace()
            return null
        }
    }

    public override suspend fun helpReqsToTasks(task: taskReq): List<taskDB>?{

        val matchedReqs = getHelpReqs(task)
        val taskList = mutableListOf<taskDB>()
        if (matchedReqs != null && matchedReqs.isNotEmpty()) {
            matchedReqs.forEach { reqDB ->
                val t = registrarTask(task,reqDB)
                if (t != null) {
                    taskList.add(t)
                }
            }
            return taskList
        }

        return null
    }

    public override suspend fun getTaskOGReqs(): List<Int>?{
        initializeDatabase()
        val result = supabase?.from("Task")
            ?.select(Columns.list("og_req"))
            ?.decodeList<Map<String, Int>>()
        return result?.mapNotNull { it["og_req"] }
    }

    public override suspend fun getAllUsers(): List<Usuario>?{
        initializeDatabase()
        val users = supabase?.from("Usuario")?.select(Columns.ALL)?.decodeList<Usuario>()
        return users;
    }

    public override suspend fun createIsAssigned(idTask: Int, user: Usuario){
        val res = assignedDB(getLastId("tieneAsignado")?.plus(1),null,user.correo,idTask,"pendiente")
        supabase?.from("tieneAsignado")?.insert(res)
    }

    public override suspend fun getTaskById(id: Int): taskDB? {
        initializeDatabase()
        val task =
            supabase?.from("Task")?.select() {
                filter{
                    eq("id", id)
                } }?.decodeSingle<taskDB>()

        return task;
    }

    public override suspend fun getHelpReqById(id: Int): reqDB? {
        initializeDatabase()
        val task =
            supabase?.from("Solicituddeayuda")?.select() {
                filter{
                    eq("id", id)
                } }?.decodeSingle<reqDB>()

        return task;
    }

    public override suspend fun registerDesaparecido(desaparecido: Desaparecido, ultimaUbi: Baliza?) {
        initializeDatabase()
        //TODO: Un poco tinkie winkie que desaparecido pille id y luego aquí pasar la ultimaUbi por separado, veré como lo pongo mas bonito más adelante
        if(ultimaUbi != null) {
            addBaliza(ultimaUbi.id,ultimaUbi.latitud,ultimaUbi.longitud,ultimaUbi.nombre,ultimaUbi.tipo,ultimaUbi.descripcion)
        }
        supabase?.from("Desaparecido")?.insert(desaparecido)

    }

    public override suspend fun unirseAGrupo(usuario:String , grupoId:Int): Boolean {
        return try {
            val fechaActual = java.time.LocalDate.now().toString()
            val relacion = FormaParte(user = usuario, grupo = grupoId, fecha = fechaActual)

            supabase?.from("FormaParte")?.insert(relacion)
            true
        } catch (e: Exception) {
            Log.e("SupabaseAPI", "Error al unirse al grupo", e)
            false
        }
    }

    public override suspend fun getAsignacionesUsuario(userId: String): List<tieneAsignado>? {
        initializeDatabase()
        val response = supabase?.from("tieneAsignado")?.select(){
            filter{
                eq("id_user", userId)
            }
        }?.decodeList<tieneAsignado>()
        Log.d("SupabaseAPI", "Asignaciones para $userId: ${response?.size}")
        return response
    }

    public override suspend fun eliminarAsignacion(id: Int): Boolean {
        return try {
            initializeDatabase()
            supabase?.from("tieneAsignado")?.delete {
                filter { eq("id", id) }
            }
            true
        } catch (e: Exception) {
            Log.e("Supabase", "Error rechazando tarea", e)
            false
        }
    }

    public override suspend fun aceptarTarea(asignacionId: Int): Boolean {
        return try {
            initializeDatabase()
            supabase?.from("tieneAsignado")?.update({
                set("estado", "aceptada")  // Asegúrate que este campo existe en tu tabla
            }) {
                filter { eq("id", asignacionId) }
            }
            true
        } catch (e: Exception) {
            Log.e("Supabase", "Error aceptando tarea", e)
            false
        }
    }

    public override suspend fun salirDelGrupo(usuario: String, grupo: Int) : Boolean{
        return try {
            initializeDatabase()
            supabase?.from("FormaParte")?.delete {
                filter {
                    eq("user", usuario)
                    eq("grupo", grupo)
                }
            }
            true
        } catch (e: Exception) {
            Log.e("Supabase", "Error saliendo del grupo", e)
            false
        }
    }


    public override fun getHabilidadesOfUser(correo: String): List<Habilidad>? {
        initializeDatabase()
        var response: List<DatabaseHabilidad>?
        var result: MutableList<Habilidad> = mutableListOf()
        runBlocking {
            response = supabase?.from("Habilidad")?.select(){
                filter{
                    eq("correo_usuario", correo)
                }
            }?.decodeList<DatabaseHabilidad>()
        }

        val factory: HabilidadFactory = HabilidadFactoryImpl()
        for (habilidad in response!!) {
            result.add(factory.createHabilidad(habilidad.nombre_habilidad, habilidad.competencia, habilidad.preferencia))
        }
        return result
    }


    public override fun updateUsuario(usuario: Usuario, habilidades: List<Habilidad>?): Boolean {
        initializeDatabase()
        var error = false
        try {
            runBlocking {
                supabase?.from("Usuario")?.update({
                    set("nombre", usuario.nombre)
                    set("apellidos", usuario.apellidos)
                    set("password", usuario.password)
                    set("municipio", usuario.municipio)
                    set("nacimiento", usuario.nacimiento)
                }) {
                    filter { eq("correo", usuario.correo) }
                }

                if(habilidades != null) {
                    supabase?.from("Habilidad")?.delete {
                        filter {
                            eq("correo_usuario", usuario.correo)
                        }
                    }
                    registrarHabilidades(habilidades, usuario)
                }
            }
        } catch (e: Exception) {error = true}

        return !error
    }

    public override suspend fun getAllTareas(): List<taskDB>? {
        initializeDatabase()
        val tareas = supabase?.from("Task")?.select(Columns.ALL)?.decodeList<taskDB>()
        return tareas;
    }

    public suspend override fun eliminarTarea(id: Int): Boolean {
        return try {
            initializeDatabase()
            supabase?.from("Task")?.delete {
                filter { eq("id", id) }
            }
            true
        } catch (e: Exception) {
            Log.e("Supabase", "Error eliminando tarea", e)
            false
        }
    }

    public override suspend fun getAllSolicitudes(): List<reqDB>? {
        initializeDatabase()
        val solicitudes = supabase?.from("Solicituddeayuda")?.select(Columns.ALL)?.decodeList<reqDB>()
        return solicitudes;
    }

}