package com.upv.solidarityHub.ui.components.DatePicker

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.upv.solidarityHub.ui.registro.Registro
import java.util.Calendar

class DatePickerFragment() : DialogFragment(), DatePickerDialog.OnDateSetListener {
    lateinit var parent: DatePickerHandler

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        parent = this.activity as DatePickerHandler
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it.
        return DatePickerDialog(requireContext(), this, year, month, day)

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        parent.handleDate("$year-$month-$day")
    }
}