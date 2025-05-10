package com.upv.solidarityHub.ui.registro

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.DatabaseAPI
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.model.Desaparecido
import com.upv.solidarityHub.ui.components.DatePicker.DatePickerFragment
import com.upv.solidarityHub.ui.components.DatePicker.DatePickerHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import com.upv.solidarityHub.utils.strategy.*

class RegistroViewModel : ViewModel() {

    private val _correo = MutableLiveData<String>()
    private val _nombre = MutableLiveData<String>()
    private val _apellidos = MutableLiveData<String>()
    private val _contrasena = MutableLiveData<String>()
    private val _repContrasena = MutableLiveData<String>()
    private val _fechaNacimiento = MutableLiveData<String>("")
    private val _municipio = MutableLiveData<String>()
    private val _municipios = MutableLiveData<Array<String?>>()

    private val _correoIsValid = MutableLiveData<Boolean>(false)
    private val _nombreIsValid = MutableLiveData<Boolean>(true)
    private val _apellidosIsValid = MutableLiveData<Boolean>(true)
    private val _contrasenaIsValid = MutableLiveData<Boolean>(false)
    private val _repContrasenaIsValid = MutableLiveData<Boolean>(false)
    private val _municipioIsValid = MutableLiveData<Boolean>(false)
    private val _showToast = MutableLiveData<Boolean>(false)
    private val _toastMessage = MutableLiveData<String>("")
    private val _registryFinalized = MutableLiveData<Boolean>(false)


    private val _fechaNacimientoIsValid = MutableLiveData<Boolean>(false)
    private val _allIsValid = MutableLiveData<Boolean>(false)

    val correo: LiveData<String> get() = _correo
    val nombre: LiveData<String> get() = _nombre
    val apellidos: LiveData<String> get() = _apellidos
    val contrasena: LiveData<String> get() = _apellidos
    val repContrasena: LiveData<String> get() = _contrasena
    val fechaNacimiento: LiveData<String> get() = _fechaNacimiento
    val municipio: LiveData<String> get() = _municipio
    val municipios: LiveData<Array<String?>> get() = _municipios
    val toastMessage: LiveData<String> get() = _toastMessage

    val correoIsValid: LiveData<Boolean> get() = _correoIsValid
    val nombreIsValid: LiveData<Boolean> get() = _nombreIsValid
    val apellidosIsValid: LiveData<Boolean> get() = _apellidosIsValid
    val contrasenaIsValid: LiveData<Boolean> get() = _contrasenaIsValid
    val repContrasenaIsValid: LiveData<Boolean> get() = _repContrasenaIsValid
    val municipioIsValid: LiveData<Boolean> get() = _municipioIsValid
    val fechaNacimientoIsValid: LiveData<Boolean> get() = _fechaNacimientoIsValid
    val allIsValid: LiveData<Boolean> get() = _allIsValid
    val showToast: LiveData<Boolean> get() = _showToast
    val registryFinalized: LiveData<Boolean> get() = _registryFinalized


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
            var validate = FormField("email",correo,EmailValidator());
            Log.d("LOG DEBUG", validate.isValid().toString())
            return validate.isValid();
        }
        return false;
    }

    fun checkNombreIsValid(): Boolean {
        val nombre = _nombre.value
        if(nombre!=null){
            var validate = FormField("nombre",nombre,NameValidator());
            return validate.isValid();
        }
        return false;
    }

    fun checkApellidosIsValid(): Boolean {
        val apellidos = _apellidos.value
        if(apellidos!=null){
            var validate = FormField("apellidos",apellidos,NameValidator());
            return validate.isValid()
        }
        return false;
    }

    fun checkContrasenaIsValid(): Boolean {
        val password = _contrasena.value
        if(password!=null){
            var validate = FormField("password",password,PasswordValidator());
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

    suspend fun registrarse() {
        var successfullRegistry = false
        var foundExistingUser = true

        try{
            db.getUsuarioByCorreo(_correo.value!!) != null
        } catch (e: NoSuchElementException){
            foundExistingUser = false
            successfullRegistry = db.registerUsuario(_correo.value!!, _nombre.value!!, _apellidos.value!!, _contrasena.value!!, _fechaNacimiento.value!!,_municipio.value!!)
            if (successfullRegistry) {
                _registryFinalized.value = true
            }

        }

        if(foundExistingUser) throw Exception("User already registered")


    }

}
