package jp.co.tai.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "riches_prefs")
object SnowboundStore {
    private val KEY_SAVED_STRING = stringPreferencesKey("saved_string")

    suspend fun saveStringOnce(context: Context, value: String) {
        val dataStore = context.dataStore

        val existing = dataStore.data.first()[KEY_SAVED_STRING]
        if (existing != null) {
            throw IllegalStateException("Value already saved: $existing")
        }

//        Log.d("MYTAG", "Save url = $value")
        dataStore.edit { prefs ->
            prefs[KEY_SAVED_STRING] = value
        }
    }

    fun getStringFlow(context: Context): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[KEY_SAVED_STRING]
        }
    }
}