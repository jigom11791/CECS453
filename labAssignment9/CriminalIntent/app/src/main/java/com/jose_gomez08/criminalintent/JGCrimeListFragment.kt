package com.jose_gomez08.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.awt.font.TextAttribute

private const val TAG = "CrimeListFragment"

class JGCrimeListFragment : Fragment() {

    private lateinit var jgCrimeRecyclerView: RecyclerView
    private var jgAdapter: JGCrimeAdapter? = null

    private val jgCrimesListViewModel : JGCrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(JGCrimeListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes: ${jgCrimesListViewModel.jgCrimes.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val jgView = inflater.inflate(R.layout.fragment_crime_list, container, false)

        jgCrimeRecyclerView = jgView.findViewById(R.id.crime_recycler_view) as RecyclerView
        jgCrimeRecyclerView.layoutManager = LinearLayoutManager(context)

        jgUpdateUI()
        return jgView
    }

    private fun jgUpdateUI() {
        val jgCrimes = jgCrimesListViewModel.jgCrimes
        jgAdapter = JGCrimeAdapter(jgCrimes)
        jgCrimeRecyclerView.adapter = jgAdapter
    }

    private inner class JGCrimeHolder(view: View)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var jgCrime: JGCrime

        private val jgTitleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val jgDateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val jgSolvedImageView: ImageView = itemView.findViewById(R.id.jg_crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(jgCrime: JGCrime) {
            this.jgCrime = jgCrime
            jgTitleTextView.text = this.jgCrime.jgTitle
            jgDateTextView.text = this.jgCrime.jgDate.toString()
            jgSolvedImageView.visibility = if(jgCrime.jgIsSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v: View) {
            Toast.makeText(context, "${jgCrime.jgTitle} pressed!", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class JGCrimeAdapter(var jgCrimes: List<JGCrime>)
        : RecyclerView.Adapter<JGCrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JGCrimeHolder {
            val jgView = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return JGCrimeHolder(jgView)
        }

        override fun getItemCount() = jgCrimes.size

        override fun onBindViewHolder(holder: JGCrimeHolder, position: Int) {
            val jgCrime = jgCrimes[position]
            holder.bind(jgCrime)
        }
    }

    companion object {
        fun newInstance(): JGCrimeListFragment {
            return JGCrimeListFragment()
        }
    }
}