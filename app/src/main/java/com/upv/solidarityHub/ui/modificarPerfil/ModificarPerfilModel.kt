package com.upv.solidarityHub.ui.modificarPerfil

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.model.Habilidad
import com.upv.solidarityHub.utils.strategy.ApellidosValidator
import com.upv.solidarityHub.utils.strategy.FormField
import com.upv.solidarityHub.utils.strategy.NameValidator
import com.upv.solidarityHub.utils.strategy.PasswordValidator
import com.upv.solidarityHub.persistence.database.SupabaseAPI

class ModificarPerfilModel {
        val usuario: Usuario = SupabaseAPI().getLogedUser()
        val _nombre: MutableLiveData<String> = MutableLiveData<String>("")
        val _apellidos: MutableLiveData<String> = MutableLiveData<String>("")
        val _contrasena: MutableLiveData<String> = MutableLiveData<String>("")
        val _municipio: MutableLiveData<String> = MutableLiveData<String>("")
        val _oldContrasena: MutableLiveData<String> = MutableLiveData<String>("")
        val _fechaNacimiento: MutableLiveData<String> = MutableLiveData<String>("")
        lateinit var _habilidades: MutableLiveData<List<Habilidad>>

        val _municipios = MutableLiveData<Array<String?>>()

        val _nombreIsValid: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
        val _apellidosIsValid: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
        val _contrasenaIsValid: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
        val _municipioIsValid: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
        val _oldContrasenaIsValid: MutableLiveData<Boolean> = MutableLiveData<Boolean>(true)
        val _fechaNacimientoIsValid: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
        val _allIsValid: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)


        fun updateNombre(newNombre: String) {
            _nombre.value = newNombre
            checkNombreIsValid()
            checkAllValid()
        }

        fun updateApellidos(newApellidos: String) {
            _apellidos.value = newApellidos
            checkApellidosIsValid()
            checkAllValid()
        }

        fun updateContrasena(newContrasena: String) {
            _contrasena.value = newContrasena
            checkContrasenaIsValid()
            checkAllValid()
        }

        fun updateMunicipio(newMunicipio: String) {
            _municipio.value = newMunicipio
            checkMunicipioIsValid()
            checkAllValid()
        }

        fun updateOldContrasena(newOldContrasena: String) {
            _oldContrasena.value = newOldContrasena
            checkOldContrasenaIsValid()
            checkAllValid()
        }

        fun updateFechaNacimiento(newFechaNacimiento: String) {
            _fechaNacimiento.value = newFechaNacimiento
            checkFechaNacimientoIsValid()
            checkAllValid()
        }

        private fun checkNombreIsValid(): Boolean {
            val nombre = _nombre.value
            if(nombre != null){
                val validate = FormField("nombre",nombre, NameValidator())
                _nombreIsValid.value = validate.isValid()
                return validate.isValid()
            }
            return false
        }

        private fun checkMunicipioIsValid(): Boolean {
            val municipio = _municipio.value
            if(municipio != null){
                val found = _municipios.value!!.contains(municipio)
                _municipioIsValid.value = found
                return found
            }
            return false
        }

        private fun checkApellidosIsValid(): Boolean {
            val apellidos = _apellidos.value
            if(apellidos != null){
                val validate = FormField("apellidos",apellidos, ApellidosValidator())
                _apellidosIsValid.value = validate.isValid()
                return validate.isValid()
            }
            return false
        }

        private fun checkContrasenaIsValid(): Boolean {
            val contrasena = _contrasena.value
            if(contrasena != null){
                val validate = FormField("password",contrasena, PasswordValidator())
                _contrasenaIsValid.value = validate.isValid()
                return validate.isValid()
            }
            return false
        }

        private fun checkFechaNacimientoIsValid(): Boolean {
            val fecha = _fechaNacimiento.value
            if(fecha != null){
                return fecha != ""
            }
            return false
        }

        private fun checkOldContrasenaIsValid(): Boolean {
            val contrasena = _oldContrasena.value
            if(contrasena != null) {
                return contrasena != ""
            }
            return false
        }

        private fun checkAllValid(): Boolean {
            val allGood = _nombreIsValid.value!! && _apellidosIsValid.value!! && _contrasenaIsValid.value!! && _oldContrasenaIsValid.value!! && _fechaNacimientoIsValid.value!! && _municipioIsValid.value!!
            _allIsValid.value = allGood
            return allGood
        }

        private fun getOriginalHabilidades(): MutableLiveData<List<Habilidad>> {
            val habilidades = SupabaseAPI().getHabilidadesOfUser(SupabaseAPI().getLogedUser().correo)
            if(habilidades != null) {
            return MutableLiveData<List<Habilidad>>(habilidades)
            } else return MutableLiveData<List<Habilidad>>(listOf<Habilidad>())

        }

        public fun confirmarModificacion(): Boolean {
            val db = SupabaseAPI()

            val nuevoUser: Usuario = Usuario(usuario.correo, _nombre.value!!, _apellidos.value!!, _contrasena.value!!, _fechaNacimiento.value!!, _municipio.value!!)
            if (usuario.password == _oldContrasena.value) {
                    if (!db.updateUsuario(nuevoUser, _habilidades.value)) throw Exception("unknown error")
            }   else return false

            return true
        }

        public fun setOriginalUsuarioValues() {
            _nombre.value = usuario.nombre
            _apellidos.value = usuario.apellidos
            _municipio.value = usuario.municipio
            _fechaNacimiento.value = usuario.nacimiento
            _contrasena.value = usuario.password
            _habilidades = getOriginalHabilidades()
        }

}
