package com.upv.solidarityHub.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.upv.solidarityHub.ui.main.Main
import com.upv.solidarityHub.R
import com.upv.solidarityHub.databinding.ActivityLoginBinding
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.ui.registro.Registro
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class Login : AppCompatActivity() {

    private lateinit var inputCorreo: TextInputLayout
    private lateinit var inputContrasena: TextInputLayout
    private lateinit var showPasswordButton: MaterialButton
    private lateinit var irHaciaRegistroButton:Button
    private lateinit var logearseButton:Button
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityLoginBinding

    private lateinit var viewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        initializeFields()
        initializeButtons()
        initializeListeners()
        initializeObservers()
        viewModel.setContext(applicationContext)
    }

    private fun initializeFields() {
        inputCorreo = findViewById(R.id.loginInputCorreo)
        inputContrasena = findViewById(R.id.loginInputContrasena)
        inputContrasena.editText!!.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

    }

    private fun initializeButtons() {
        showPasswordButton = findViewById(R.id.loginShowPassButton)
        showPasswordButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_menu_view,0,0,0)
        logearseButton = findViewById(R.id.logearseButton)
        irHaciaRegistroButton = findViewById(R.id.botonIrRegistrarse)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeListeners() {
        inputCorreo.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateCorreo(inputCorreo.editText!!.text.toString())
            }
        })

        inputCorreo.editText!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                if(!viewModel.correoIsValid.value!!)  {
                    inputCorreo.isErrorEnabled = true
                    inputCorreo.editText!!.error = "Las contraseñas no coinciden"
                }
            }
        }

        inputContrasena.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.updateContrasena(inputContrasena.editText!!.text.toString())
            }
        })

        inputContrasena.editText!!.setOnFocusChangeListener { v, hasFocus ->
            if(!hasFocus) {
                if(!viewModel.contrasenaIsValid.value!!)  {
                    inputContrasena.isErrorEnabled = true
                    inputContrasena.editText!!.error = "Las contraseñas no coinciden"
                }
            }
        }

        showPasswordButton.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> {
                    inputContrasena.editText!!.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    true
                }

                MotionEvent.ACTION_DOWN -> {
                    inputContrasena.editText!!.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    true
                }
                else -> false
            }
        }

        irHaciaRegistroButton.setOnClickListener {
            goToRegister()
        }
        logearseButton.setOnClickListener {
            viewModel.login()

        }
    }

    fun initializeObservers() {
        viewModel.correoIsValid.observe(this, Observer { newCorreoIsValid ->
            if(newCorreoIsValid) {
                inputCorreo.isErrorEnabled = false
                inputCorreo.editText!!.error = null
            }
        })

        viewModel.contrasenaIsValid.observe(this, Observer { newContrasenaIsValid ->
            if(newContrasenaIsValid) {
                inputContrasena.isErrorEnabled = false
                inputContrasena.editText!!.error = null
            }
        })

        viewModel.allIsValid.observe(this, Observer { newAllIsValid ->
            logearseButton.isEnabled = newAllIsValid
        })

        viewModel.goToMain.observe(this, Observer { newGoToMain ->
            if(newGoToMain) {
                goToMain()
            }
        })
    }


    fun goToRegister() {
        val intent = Intent(this, Registro::class.java)
        startActivity(intent)
    }

    fun goToMain() {
        val intent = Intent(this, Main::class.java)
        startActivity(intent)
    }

}