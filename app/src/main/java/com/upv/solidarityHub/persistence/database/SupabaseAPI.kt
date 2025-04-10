package com.upv.solidarityHub.persistence.database

import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.persistence.Usuario
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

    public override suspend fun getBalizaById(id: String): Baliza? {
        initializeDatabase()
        val baliza =
            supabase?.from("Balizas")?.select() {
                filter{
                    eq("id", id)
                } }?.decodeSingle<Baliza>()

        return baliza;
    }

    public override suspend fun getAllBalizas(): List<Baliza>? {
        initializeDatabase()
        val balizas =
            supabase?.from("Balizas")?.select {  }?.decodeList<Baliza>()

        return balizas;
    }

    public override suspend fun registerUsuario(correo: String, nombre: String, apellidos: String, password: String, nacimiento: String, municipio: String): Boolean {
        initializeDatabase()
        try{
            val Usuario = Usuario(correo, nombre, apellidos, password, nacimiento, municipio)
            supabase?.from("Usuario")?.insert(Usuario)
            return true
         } catch(e:Exception) {return false}
    }



}