import android.content.Context
import android.preference.PreferenceManager
import android.provider.Settings.Global.putString
import androidx.core.content.edit

private const val JG_PREF_SEARCH_QUERY = "searchQuery"
object QueryPreferences {
    fun jgGetStoredQuery(context: Context): String {
        val jgPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        return jgPrefs.getString(JG_PREF_SEARCH_QUERY, "")!!
    }
    fun jgSetStoredQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(JG_PREF_SEARCH_QUERY, query)
            }
    }
}