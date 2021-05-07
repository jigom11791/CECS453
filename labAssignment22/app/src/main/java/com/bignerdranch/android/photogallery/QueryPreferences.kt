import android.content.Context
import android.preference.PreferenceManager
import android.provider.Settings.Global.putString
import androidx.core.content.edit

private const val JG_PREF_SEARCH_QUERY = "searchQuery"
private const val PREF_LAST_RESULT_ID = "LastResultId"
private const val PREF_IS_POLLING = "isPolling"
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

    fun jgGetLastResultId(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PREF_LAST_RESULT_ID, "")!!
    }
    fun jgSetLastResultId(context: Context, lastResultId: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(PREF_LAST_RESULT_ID, lastResultId)
        }
    }

    fun jgIsPolling(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PREF_IS_POLLING, false)
    }
    fun jgSetPolling(context: Context, isOn: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(PREF_IS_POLLING, isOn)
        }
    }
}