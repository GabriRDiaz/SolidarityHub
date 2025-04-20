package com.upv.solidarityHub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.ui.AppBarConfiguration
import com.upv.solidarityHub.databinding.ActivityLoginBinding
import com.upv.solidarityHub.databinding.ActivityRegistroBinding
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class Login : AppCompatActivity() {

    private lateinit var inputCorreo: EditText
    private lateinit var inputContrasena: EditText
    private lateinit var showPasswordButton:Button
    private lateinit var irHaciaRegistroButton:Button
    private lateinit var logearseButton:Button
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLoginBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeFields()
        initializeButtons()
        initializeListeners()


    }

    private fun initializeFields() {
        inputCorreo = findViewById(R.id.loginInputCorreo)
        inputContrasena = findViewById(R.id.loginInputContrasena)
    }

    private fun initializeButtons() {
        showPasswordButton = findViewById(R.id.loginShowPassButton)
        logearseButton = findViewById(R.id.logearseButton)
        irHaciaRegistroButton = findViewById(R.id.botonIrRegistrarse)
    }

    private fun initializeListeners() {
        inputCorreo.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (checkCorreoValidity()) {
            }
            checkAllFields()
            false
        })

        inputCorreo.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (!hasFocus) {
                if (!checkCorreoValidity()) {
                }
            }
            checkAllFields()
        }

        inputContrasena.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (checkContrasenaValidity()) {
            }
            checkAllFields()
            false
        })

        inputContrasena.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (!hasFocus) {
                if (!checkContrasenaValidity()) {
                }
            }
            checkAllFields()
        }

        showPasswordButton.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    inputContrasena.inputType =
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    true
                }

                MotionEvent.ACTION_DOWN -> {
                    inputContrasena.inputType =
                        android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    true
                }

                else -> false
            }
        }

        irHaciaRegistroButton.setOnClickListener {
            goToRegister()
        }

        logearseButton.setOnClickListener {
            var usuario: Usuario? = null
            var db = SupabaseAPI()
            try {
                runBlocking {
                    val deferred1 = async {
                        usuario = db.loginUsuario(inputCorreo.text.toString(), inputContrasena.text.toString())
                    }
                    deferred1.await()
                }

                if(usuario != null) {Toast.makeText(getApplicationContext(), usuario!!.correo.toString() + "  " + usuario!!.nombre.toString(), Toast.LENGTH_SHORT).show()
                    goToMain(usuario!!)
                } else {Toast.makeText(getApplicationContext(), "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()}

            } catch (e:NoSuchElementException) {
                Toast.makeText(getApplicationContext(), "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            } catch (e:Exception) {
                Toast.makeText(getApplicationContext(),"Hubo un error, porfavor inténtelo más tarde", Toast.LENGTH_SHORT).show()
            }


        }


    }

    private fun checkCorreoValidity():Boolean {
     return !inputCorreo.text.toString().equals("")
    }

    private fun checkContrasenaValidity():Boolean {
        return !inputContrasena.text.toString().equals("")
    }

    private fun checkAllFields():Boolean {
        var allGood = checkCorreoValidity() && checkContrasenaValidity()
        if(allGood) {
            logearseButton.isEnabled = true; return true
        }  else logearseButton.isEnabled = false; return false
    }

    fun goToRegister() {
        val intent = Intent(this, Registro::class.java)
        startActivity(intent)
    }

    fun goToStopGap(usr:Usuario) {
        val intent = Intent(this, StopGap::class.java)
        intent.putExtra("usuario", usr)
        startActivity(intent)
    }

    fun goToMain(usr:Usuario) {
        val intent = Intent(this, Main::class.java)
        intent.putExtra("usuario", usr)
        startActivity(intent)
    }

    fun goToMainPage(usr:Usuario) {
        val intent = Intent(this, MainPage::class.java)
        intent.putExtra("usuario", usr)
        startActivity(intent)
    }

    fun goToHabilidades(usuario:Usuario) {
        val intent = Intent(this, HabilidadesActivity()::class.java)
        intent.putExtra("usuario", usuario)

        startActivity(intent)
    }
}