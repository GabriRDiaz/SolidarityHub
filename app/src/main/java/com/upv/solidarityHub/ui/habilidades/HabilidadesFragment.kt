package com.upv.solidarityHub.ui.habilidades

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.upv.solidarityHub.persistence.Baliza
import com.upv.solidarityHub.persistence.Usuario
import com.upv.solidarityHub.persistence.database.DatabaseAPI
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import com.upv.solidarityHub.persistence.model.Habilidad
import com.upv.solidarityHub.ui.main.Main

class HabilidadesFragment(prevHabilidades: List<Habilidad>) : DialogFragment() {

    val habilidades = prevHabilidades

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                HabilidadesFragmentForm(habilidades, this@HabilidadesFragment)
            }
        }
    }

    fun closeDialog() {
        dismiss()
    }

    fun inputHabilidades(habilidades: List<Habilidad>) {
        (activity as HabilidadesListener).onHabilidadesInput(habilidades)
    }

    interface HabilidadesListener {
        fun onHabilidadesInput(habilidades: List<Habilidad>)
    }
}