package com.upv.solidarityHub.ui.modificarPerfil

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.utils.strategy.ApellidosValidator
import com.upv.solidarityHub.utils.strategy.EmailValidator
import com.upv.solidarityHub.utils.strategy.FormField
import com.upv.solidarityHub.utils.strategy.NameValidator
import com.upv.solidarityHub.utils.strategy.PasswordValidator

class ModificarPerfilModel {
        val max_char: Int = 50
        val max_altura: Int = 300
        val max_edad: Int = 120

        val _nombre: MutableLiveData<String> = MutableLiveData<String>("")
        val _apellidos: MutableLiveData<String> = MutableLiveData<String>("")
        val _contrasena: MutableLiveData<String> = MutableLiveData<String>("")
        val _municipio: MutableLiveData<String> = MutableLiveData<String>("")
        val _oldContrasena: MutableLiveData<String> = MutableLiveData<String>("")
        val _fechaNacimiento: MutableLiveData<String> = MutableLiveData<String>("")

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
            checkEdadIsValid()
            checkAllValid()
        }

        fun updateMunicipio(newMunicipio: String) {
            _municipio.value = newMunicipio
            checkAlturaIsValid()
            checkAllValid()
        }

        fun updateOldContrasena(newOldContrasena: String) {
            _oldContrasena.value = newOldContrasena
            checkComplexionIsValid()
            checkAllValid()
        }

        fun updateFechaNacimiento(newFechaNacimiento: String) {
            _fechaNacimiento.value = newFechaNacimiento
            checkSexoIsValid()
            checkAllValid()
        }

        fun checkNombreIsValid(): Boolean {
            val nombre = _nombre.value;
            if(nombre != null){
                var validate = FormField("nombre",nombre, NameValidator());
                return validate.isValid();
            }
            return false;
        }


        fun checkMunicipioIsValid(): Boolean {
            val municipio = _municipio.value;
            if(contrasena != null){
                val validate = FormField("contrasena",contrasena, PasswordValidator());
                return validate.isValid();
            }
            return false;
        }

        fun checkEdadIsValid(): Boolean {
            val edad = _edad.value
            val res = edad != null && edad >= 0 && edad <= max_edad
            _edadIsValid.value = res
            return res
        }

        fun checkAlturaIsValid(): Boolean {
            val altura = _altura.value!!
            val res = altura in 0.0..300.0
            _alturaIsValid.value = res
            return res
        }

        fun checkComplexionIsValid(): Boolean {
            val complexion = _complexion.value
            val res = complexion != -1
            _complexionIsValid.value = res
            return res
        }

        fun checkSexoIsValid(): Boolean {
            val sexo = _sexo.value
            val res = sexo != -1
            _sexoIsValid.value = res
            return res
        }

    }
