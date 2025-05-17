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

        val amount = arguments?.getInt("amount") ?: 10
        val paymentMethod = arguments?.getString("paymentMethod") ?: getString(R.string.card_payment)

        binding.textCantidad.text = "Cantidad: ${amount}€"
        binding.textMetodoPago.text = "Metodo de pago: ${paymentMethod}"

        //setupUI()
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnPay.setOnClickListener {
            if (validateForm()) {
                viewModel.processPayment(
                    amount = args.amount,
                    cardholder = binding.inputCardholder.text.toString(),
                    cardNumber = binding.inputCardNumber.text.toString(),
                    expiryDate = binding.inputExpiry.text.toString(),
                    cvv = binding.inputCvv.text.toString()
                )
            }
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
            Toast.makeText(requireContext(), "Error: ${message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun validateForm(): Boolean {
        return when {
            binding.inputCardholder.text.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Error: ${getString(R.string.error_cardholder)}", Toast.LENGTH_LONG).show()

                false
            }
            binding.inputCardNumber.text?.length != 16 -> {
                Toast.makeText(requireContext(), "Error: ${getString(R.string.error_card_number)}", Toast.LENGTH_LONG).show()


                false
            }
            !isValidExpiryDate(binding.inputExpiry.text.toString()) -> {
                Toast.makeText(requireContext(), "Error: ${getString(R.string.error_expiry_date)}", Toast.LENGTH_LONG).show()

                false
            }
            binding.inputCvv.text?.length !in 3..4 -> {
                Toast.makeText(requireContext(), "Error: ${getString(R.string.error_cvv)}", Toast.LENGTH_LONG).show()

                false
            }
            else -> true
        }
    }

    private fun isValidExpiryDate(expiry: String): Boolean {
        val parts = expiry.split("/").takeIf { it.size == 2 } ?: return false
        val (month, year) = parts
        val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

        return year.toIntOrNull()?.let { y ->
            month.toIntOrNull()?.let { m ->
                m in 1..12 && (y > currentYear || (y == currentYear && m >= currentMonth))
            }
        } ?: false
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