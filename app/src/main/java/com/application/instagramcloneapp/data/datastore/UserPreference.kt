package com.application.instagramcloneapp.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun saveLogin(uid: String) {
        context.dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.IS_LOGGED_IN] = true
            prefs[UserPreferencesKeys.USER_ID] = uid
        }
    }

    suspend fun clearLogin() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    val isLoggedInFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            prefs[UserPreferencesKeys.IS_LOGGED_IN] ?: false
        }
}
