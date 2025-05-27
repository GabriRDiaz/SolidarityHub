package com.upv.solidarityHub.ui.donaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.upv.solidarityHub.R
import com.upv.solidarityHub.databinding.FragmentSistemaDeDonacionesBinding

class SistemaDeDonaciones : Fragment() {

    private var _binding: FragmentSistemaDeDonacionesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SistemaDeDonacionesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSistemaDeDonacionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()
        setupPaymentMethods()
    }

    private fun setupObservers() {
        viewModel.donationAmount.observe(viewLifecycleOwner) { amount ->
            binding.tvAmount.text = getString(R.string.amount_format, amount)
        }
    }

    private fun setupListeners() {
        binding.btnIncrease.setOnClickListener {
            viewModel.increaseAmount()
        }

        binding.btnDecrease.setOnClickListener {
            viewModel.decreaseAmount()
        }

        binding.btnDonate.setOnClickListener {
            navigateToPayment()
        }

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupPaymentMethods() {
        binding.radioCard.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.selectPaymentMethod(getString(R.string.card_payment))
        }

        binding.radioBizum.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.selectPaymentMethod(getString(R.string.bizum_payment))
        }

        binding.radioPaypal.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) viewModel.selectPaymentMethod(getString(R.string.paypal_payment))
        }
    }

    private fun navigateToPayment() {
        if(viewModel.donationAmount.value!=0){
            val bundle = bundleOf(
                "amount" to (viewModel.donationAmount.value ?: 10),
                "paymentMethod" to (viewModel.selectedPaymentMethod.value ?: getString(R.string.card_payment))
            )

            findNavController().navigate(
                R.id.action_sistemaDeDonaciones_to_pasarelaPago,
                bundle
            )
        }else{
            Toast.makeText(requireContext(), "Cantidad inválida, no puedes donar 0€", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}