package com.upv.solidarityHub.ui.donaciones

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.upv.solidarityHub.R
import com.upv.solidarityHub.databinding.FragmentDonacionExitosaBinding
import androidx.navigation.fragment.findNavController


class DonacionExitosa : Fragment() {
    private var _binding: FragmentDonacionExitosaBinding? = null
    private val binding get() = _binding!!
    private val args: DonacionExitosaArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDonacionExitosaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mostrar datos de la transacción
        binding.txtDetalleMonto.text = "Monto: ${args.amount}€"
        binding.txtMetodoPago.text = "Método: ${args.paymentMethod}"

        binding.btnVolverInicio.setOnClickListener {
            // Navegar al inicio lógico de la app (no al login)
            findNavController().navigate(R.id.action_donacionExitosa_to_volunteermenu)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}