package com.upv.solidarityHub.ui.registro

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.upv.solidarityHub.persistence.database.DatabaseAPI
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.utils.strategy.EmailValidator
import com.upv.solidarityHub.utils.strategy.FormField
import com.upv.solidarityHub.utils.strategy.NameValidator
import com.upv.solidarityHub.utils.strategy.PasswordValidator
import kotlinx.coroutines.runBlocking

class RegistroModel {
    val _correo = MutableLiveData<String>()
    val _nombre = MutableLiveData<String>()
    val _apellidos = MutableLiveData<String>()
    val _contrasena = MutableLiveData<String>()
    val _repContrasena = MutableLiveData<String>()
    val _fechaNacimiento = MutableLiveData<String>("")
    val _municipio = MutableLiveData<String>()
    val _municipios = MutableLiveData<Array<String?>>()

    val _correoIsValid = MutableLiveData<Boolean>(false)
    val _nombreIsValid = MutableLiveData<Boolean>(true)
    val _apellidosIsValid = MutableLiveData<Boolean>(true)
    val _contrasenaIsValid = MutableLiveData<Boolean>(false)
    val _repContrasenaIsValid = MutableLiveData<Boolean>(false)
    val _municipioIsValid = MutableLiveData<Boolean>(false)
    val _showToast = MutableLiveData<Boolean>(false)
    val _toastMessage = MutableLiveData<String>("")
    val _registryFinalized = MutableLiveData<Boolean>(false)


    val _fechaNacimientoIsValid = MutableLiveData<Boolean>(false)
    val _allIsValid = MutableLiveData<Boolean>(false)

    val db: DatabaseAPI = SupabaseAPI()

    fun updateCorreo(newCorreo: String) {
        _correo.value = newCorreo
        _correoIsValid.value = checkCorreoIsValid()
        checkAllValid()
    }

    fun updateNombre(newNombre: String) {
        _nombre.value = newNombre
        _nombreIsValid.value = checkNombreIsValid()
        checkAllValid()
    }

    fun updateApellidos(newApellidos: String) {
        _apellidos.value = newApellidos
        _apellidosIsValid.value = checkApellidosIsValid()
        checkAllValid()
    }

    fun updateContrasena(newContrasena: String) {
        _contrasena.value = newContrasena
        _contrasenaIsValid.value = checkContrasenaIsValid()
        checkAllValid()
    }

    fun updateRepContrasena(newRepContrasena: String) {
        _repContrasena.value = newRepContrasena
        _repContrasenaIsValid.value = checkRepContrasenaIsValid()
        checkAllValid()
    }

    fun updateFechaNacimiento(newFechaNacimiento: String) {
        _fechaNacimiento.value = newFechaNacimiento
        checkFechaNacimientoIsValid()
        checkAllValid()
    }

    fun updateMunicipio(newMunicipio: String) {
        _municipio.value = newMunicipio
        checkMunicipioIsValid()
        checkAllValid()
    }

    fun updateMunicipiosList(newMunicipio: Array<String?>) {
        _municipios.value = newMunicipio
    }

    fun checkCorreoIsValid(): Boolean {
        val correo = _correo.value;
        if(correo != null){
            var validate = FormField("email",correo, EmailValidator());
            Log.d("LOG DEBUG", validate.isValid().toString())
            return validate.isValid();
        }
        return false;
    }

    fun checkNombreIsValid(): Boolean {
        val nombre = _nombre.value
        if(nombre!=null){
            var validate = FormField("nombre",nombre, NameValidator());
            return validate.isValid();
        }
        return false;
    }

    fun checkApellidosIsValid(): Boolean {
        val apellidos = _apellidos.value
        if(apellidos!=null){
            var validate = FormField("apellidos",apellidos, NameValidator());
            return validate.isValid()
        }
        return false;
    }

    fun checkContrasenaIsValid(): Boolean {
        val password = _contrasena.value
        if(password!=null){
            var validate = FormField("password",password, PasswordValidator());
            return validate.isValid()
        }
        return false;
    }

    fun checkRepContrasenaIsValid(): Boolean {
        val res = _contrasena.value == _repContrasena.value
        _repContrasenaIsValid.value = res
        return res
    }

    fun checkFechaNacimientoIsValid(): Boolean {
        val res = _fechaNacimiento.value != ""
        _fechaNacimientoIsValid.value = res
        return res
    }

    fun checkMunicipioIsValid():Boolean {
        val res = _municipios.value!!.contains(_municipio.value)
        _municipioIsValid.value = res
        return res
    }

    fun checkAllValid(): Boolean {
        val res = _nombreIsValid.value!! && _correoIsValid.value!! && _apellidosIsValid.value!! && _contrasenaIsValid.value!! && _repContrasenaIsValid.value!! && _fechaNacimientoIsValid.value!! && _municipioIsValid.value!!
        _allIsValid.value = res
        return res
    }

    fun registrarse() {
        var successfullRegistry = false
        var foundExistingUser = true

        try{
            runBlocking {
                db.getUsuarioByCorreo(_correo.value!!) != null
            }
        } catch (e: NoSuchElementException){
            foundExistingUser = false
            runBlocking {
                successfullRegistry = db.registerUsuario(_correo.value!!, _nombre.value!!, _apellidos.value!!, _contrasena.value!!, _fechaNacimiento.value!!,_municipio.value!!)
            }
            if (successfullRegistry) {
                _registryFinalized.value = true
            }

        }

        if(foundExistingUser) throw Exception("User already registered")


    }
}