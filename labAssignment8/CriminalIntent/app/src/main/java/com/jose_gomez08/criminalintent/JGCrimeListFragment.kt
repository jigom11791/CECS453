package com.jose_gomez08.criminalintent

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

private const val TAG = "CrimeListFragment"

class JGCrimeListFragment : Fragment() {

    private val jgCrimesListViewModel : JGCrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(JGCrimeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes: ${jgCrimesListViewModel.jgCrimes.size}")
    }

    companion object {
        fun newInstance(): JGCrimeListFragment {
            return JGCrimeListFragment()
        }
    }
}