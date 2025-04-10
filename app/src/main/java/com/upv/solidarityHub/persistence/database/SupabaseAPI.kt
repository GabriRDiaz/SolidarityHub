package com.upv.solidarityHub.persistence.database

import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.persistence.GrupoDeAyuda
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.FormaParte
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.Date


public var supabase: SupabaseClient? = null;
private const val supabaseUrl = "https://jjmkaouvmwcakqusbabw.supabase.co"
private const val supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImpqbWthb3V2bXdjYWtxdXNiYWJ3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDA0Nzg0MTIsImV4cCI6MjA1NjA1NDQxMn0.FwI-6QCb12bth5DnIJUARbZ74LDbxZ7g7Hls7D9xJcE"

class SupabaseAPI : DatabaseAPI {



    public override fun initializeDatabase() {

        if ( supabase == null) {
            supabase = createSupabaseClient(supabaseUrl, supabaseKey) {install(Postgrest)}
        }

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

    public override suspend fun registrarGrupo(id: Int, descripcion: String, ubicacion: String, fecha_creacion: Date, sesion: String, tamanyo: String): Boolean {
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

        // Obtener las relaciones entre el usuario y los grupos
        val relaciones = supabase?.from("FormaParte")
            ?.select()
            { filter { eq("usuario_id", usuario) } }
            ?.decodeList<FormaParte>() ?: return emptyList()

        // Obtener los ids de los grupos
        val idsGrupo = relaciones.map { it.grupo_id }

        // Obtener los grupos utilizando los ids de los grupos encontrados
        val grupos = supabase?.from("GrupoDeAyuda")
            ?.select()
            {
                filter {
                    // Usamos 'or' para aplicar el filtro a cada id
                    idsGrupo.forEach { grupoId ->
                        or { eq("id", grupoId) }
                    }
                }
            }
            ?.decodeList<GrupoDeAyuda>()

        return grupos
    }
    }