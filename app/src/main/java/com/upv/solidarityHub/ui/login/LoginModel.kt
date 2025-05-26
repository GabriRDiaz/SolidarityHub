package com.upv.solidarityHub.ui.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.runBlocking

class LoginModel {
    val _correo = MutableLiveData<String>()
    val _contrasena = MutableLiveData<String>()

    val _correoIsValid = MutableLiveData<Boolean>(true)
    val _contrasenaIsValid = MutableLiveData<Boolean>(true)
    val _allIsValid = MutableLiveData<Boolean>(false)

    val _goToMain = MutableLiveData<Boolean>(false)

    private lateinit var context: Context

    fun setContext(cont: Context) {
        context = cont

    }

    fun updateCorreo(newCorreo: String) {
        _correo.value = newCorreo
        checkCorreoIsValid()
        checkAllValid()
    }

    fun updateContrasena(newContrasena: String) {
        _contrasena.value = newContrasena
        checkContrasenaIsValid()
        checkAllValid()
    }

    fun checkCorreoIsValid(): Boolean {
        var res = _correo.value != null && _correo.value != ""
        _correoIsValid.value = res
        return res
    }

    fun checkContrasenaIsValid(): Boolean {
        var res = _contrasena.value != null && _contrasena.value != ""
        _contrasenaIsValid.value = res
        return res
    }

    fun checkAllValid(): Boolean {
        val res = _correoIsValid.value!! && _contrasenaIsValid.value!!
        _allIsValid.value = res
        return res
    }

    fun makeToast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun login() {
        var usuario: Usuario? = null
        var db = SupabaseAPI()
        try {
            runBlocking {
                usuario = db.loginUsuario(_correo.value!!, _contrasena.value!!)
            }

            if (usuario != null) {
                db.setLogedUserCorreo(_correo.value!!)
                usuario = db.getLogedUser()
                _goToMain.value = true
                makeToast("Bienvenido, " + usuario!!.nombre)
            } else {
                makeToast("Correo o contraseña incorrectos")}
            } catch (e:NoSuchElementException) {
                makeToast("Correo o contraseña incorrectos")
            } catch (e:Exception) {
                makeToast("Hubo un error, porfavor inténtelo más tarde")
            }
        }
    }
