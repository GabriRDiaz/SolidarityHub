package com.upv.solidarityHub.ui.donaciones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.upv.solidarityHub.R
import com.upv.solidarityHub.databinding.FragmentPasarelaPagoBinding
import java.util.Calendar
import androidx.navigation.fragment.findNavController
import android.widget.Toast

class PasarelaPago : Fragment() {

    private var _binding: FragmentPasarelaPagoBinding? = null
    private val binding get() = _binding!!
    private val args: PasarelaPagoArgs by navArgs()
    private val viewModel: PasarelaPagoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPasarelaPagoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textCantidad.text = "Cantidad: ${args.amount}€"
        binding.textMetodoPago.text = "Método de pago: ${args.paymentMethod}"

        setupPaymentUI() // Nueva función
        setupListeners()
        setupObservers()
    }

    private fun setupPaymentUI() {
        when (args.paymentMethod) {
            getString(R.string.card_payment) -> {
                binding.containerTarjeta.visibility = View.VISIBLE
            }
            getString(R.string.bizum_payment) -> {
                binding.containerBizum.visibility = View.VISIBLE
            }
            getString(R.string.paypal_payment) -> {
                binding.containerPaypal.visibility = View.VISIBLE
            }
        }
    }

    private fun setupListeners() {
        binding.btnPay.setOnClickListener {
            val cardData = if (args.paymentMethod == getString(R.string.card_payment)) {
                CardData(
                    binding.inputCardholder.text.toString(),
                    binding.inputCardNumber.text.toString(),
                    binding.inputExpiry.text.toString(),
                    binding.inputCvv.text.toString()
                )
            } else null

            viewModel.processPayment(
                amount = args.amount,
                paymentMethod = args.paymentMethod,
                cardData = cardData,
                phone = binding.inputPhone.text?.toString(),
                email = binding.inputEmail.text?.toString()
            )
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }


        private fun setupObservers() {
        viewModel.paymentResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                showSuccessAndFinish()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    private fun showSuccessAndFinish() {
        val directions = PasarelaPagoDirections.actionPasarelaPagoToDonacionExitosa(
            amount = args.amount,
            paymentMethod = args.paymentMethod
        )
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}