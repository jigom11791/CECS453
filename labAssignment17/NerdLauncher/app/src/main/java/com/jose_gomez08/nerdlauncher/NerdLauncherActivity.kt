package com.jose_gomez08.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.RecursiveAction

private const val TAG = "NerdLauncherActivity"

class NerdLauncherActivity : AppCompatActivity() {

    private lateinit var jgRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher)

        jgRecyclerView = findViewById(R.id.jg_app_recycler_view)
        jgRecyclerView.layoutManager = LinearLayoutManager(this)

        jgSetupAdapter()
    }

    private fun jgSetupAdapter() {
        val jgStartupIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val jgPackageManager = packageManager
        val jgActivities = jgPackageManager.queryIntentActivities(jgStartupIntent, 0)
        jgActivities.sortWith(Comparator { a, b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(jgPackageManager).toString(),
                b.loadLabel(jgPackageManager).toString()
            )
        })

        Log.i(TAG, "Found ${jgActivities.size} activities")
        jgRecyclerView.adapter = JGActivityAdapter(jgActivities)
    }

    private class JGActivityHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val jgNameTextView = itemView as TextView
        private lateinit var jgResolveInfo: ResolveInfo

        init {
            jgNameTextView.setOnClickListener(this)
        }

        fun bindActivity(resolveInfo: ResolveInfo) {
            this.jgResolveInfo = resolveInfo
            val jgPackageManager = itemView.context.packageManager
            val jgAppName = resolveInfo.loadLabel(jgPackageManager).toString()
            jgNameTextView.text = jgAppName
        }

        override fun onClick(view: View) {
            val jgActivityInfo = jgResolveInfo.activityInfo

            val jgIntent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(jgActivityInfo.applicationInfo.packageName,
                    jgActivityInfo.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val jgContext = view.context
            jgContext.startActivity(jgIntent)
        }
    }

    private class JGActivityAdapter(val jgActivities: List<ResolveInfo>) :
        RecyclerView.Adapter<JGActivityHolder>() {

        override fun onCreateViewHolder(container: ViewGroup, viewType: Int):
                JGActivityHolder {
            val jgLayoutInflater = LayoutInflater.from(container.context)
            val jgView = jgLayoutInflater
                .inflate(android.R.layout.simple_list_item_1, container, false)
            return JGActivityHolder(jgView)
        }

        override fun onBindViewHolder(holder: JGActivityHolder, position: Int) {
            val jgResolveInfo = jgActivities[position]
            holder.bindActivity(jgResolveInfo)
        }

        override fun getItemCount(): Int {
            return jgActivities.size
        }
    }
}
