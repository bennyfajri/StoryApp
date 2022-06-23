package com.drsync.storyapp.models

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.drsync.storyapp.util.Constant.KEY_NAME
import com.drsync.storyapp.util.Constant.KEY_TOKEN
import com.drsync.storyapp.util.Constant.KEY_USERID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class UserPreference (
    private val context: Context
) {
    private val userDataStore = context.dataStore

    fun getUser(): Flow<User> {
        return userDataStore.data.map { preferences ->
            User(
                preferences[KEY_USERID] ?: "",
                preferences[KEY_NAME] ?: "",
                preferences[KEY_TOKEN] ?: ""
            )
        }
    }

    suspend fun saveUser(user: User) {
        userDataStore.edit { preferences ->
            preferences[KEY_USERID] = user.userId
            preferences[KEY_NAME] = user.name
            preferences[KEY_TOKEN] = user.token
        }
    }

    suspend fun deleteUser(user: User){
        userDataStore.edit { preferences ->
            preferences[KEY_USERID] = ""
            preferences[KEY_NAME] = ""
            preferences[KEY_TOKEN] = ""
        }
    }
}