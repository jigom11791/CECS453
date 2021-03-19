package com.jose_gomez08.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

private const val JG_ARG_DATE = "date"

class JGDatePickerFragment : DialogFragment() {

    interface Callbacks {
        fun jgOnDateSelected(date: Date)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val jgDateListener = DatePickerDialog.OnDateSetListener {
            _: DatePicker, year: Int, month: Int, day: Int->

            val jgResultDate: Date = GregorianCalendar(year, month, day).time

            targetFragment?.let { fragment->
                (fragment as Callbacks).jgOnDateSelected(jgResultDate)
            }
        }

        val jgDate = arguments?.getSerializable(JG_ARG_DATE) as Date
        val jgCalendar = Calendar.getInstance()
        jgCalendar.time = jgDate
        val jgInitialYear = jgCalendar.get(Calendar.YEAR)
        val jgInitialMonth = jgCalendar.get(Calendar.MONTH)
        val jgInitialDay = jgCalendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            jgDateListener,
            jgInitialYear,
            jgInitialMonth,
            jgInitialDay
        )
    }

    companion object {
        fun jgNewInstance(date: Date): JGDatePickerFragment {
            val jgargs = Bundle().apply {
                putSerializable(JG_ARG_DATE, date)
            }
            return JGDatePickerFragment().apply {
                arguments = jgargs
            }
        }
    }
}