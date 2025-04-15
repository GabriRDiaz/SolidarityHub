package com.upv.solidarityHub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class MapaGenericoFragment : Fragment() {
    override fun onCreateView(
        inflater : LayoutInflater,
        containter: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mapa_generico,containter,false)
    }

}