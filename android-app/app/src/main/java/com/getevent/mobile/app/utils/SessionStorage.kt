package com.getevent.mobile.app.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.sessionDataStore by preferencesDataStore(name = "session")

class SessionStorage(private val context: Context) {
    private object Keys {
        val TOKEN: Preferences.Key<String> = stringPreferencesKey("token")
        val USER_JSON: Preferences.Key<String> = stringPreferencesKey("user_json")
    }

    suspend fun readToken(): String? = context.sessionDataStore.data.first()[Keys.TOKEN]
    suspend fun readUserJson(): String? = context.sessionDataStore.data.first()[Keys.USER_JSON]

    suspend fun save(token: String?, userJson: String?) {
        context.sessionDataStore.edit { prefs ->
            if (token == null) prefs.remove(Keys.TOKEN) else prefs[Keys.TOKEN] = token
            if (userJson == null) prefs.remove(Keys.USER_JSON) else prefs[Keys.USER_JSON] = userJson
        }
    }

    suspend fun clear() = save(null, null)
}

