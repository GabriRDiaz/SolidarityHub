package com.upv.solidarityHub.ui.gruposDeAyuda

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.upv.solidarityHub.databinding.FragmentMisGruposBinding
import com.upv.solidarityHub.persistence.database.SupabaseAPI
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.upv.solidarityHub.persistence.Usuario
import androidx.navigation.fragment.findNavController


class MisGruposFragment : Fragment() {
    private var _binding: FragmentMisGruposBinding? = null
    private val binding get() = _binding!!
    //private lateinit var usuario: Usuario
    private val db = SupabaseAPI()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisGruposBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //usuario = requireArguments().getParcelable("usuario")!!
        //usuario = requireActivity().intent.getParcelableExtra("usuario")!!
        //usuario = arguments?.getParcelable("usuario")
        val sharedPref = requireActivity().getSharedPreferences("usuario", AppCompatActivity.MODE_PRIVATE)
        val usuario = sharedPref.getString("usuarioCorreo", null)

        lifecycleScope.launch {
            if(usuario!=null){
                val gruposInscritos = db.getGruposusuario(usuario)
                if (gruposInscritos.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "No estás inscrito en ningún grupo", Toast.LENGTH_SHORT).show()
                } else {
                    val nombresGrupos = gruposInscritos.map { "Grupo ${it.id} - ${it.sesion}" }
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        nombresGrupos
                    )
                    binding.listaMisGrupos.adapter = adapter
                }
            }else{
                Toast.makeText(requireContext(), "Error al buscar el usuario", Toast.LENGTH_SHORT).show()
            }
        }

        binding.botonVolver.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}