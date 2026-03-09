package com.example.altintakipandroid.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.altintakipandroid.domain.UserAsset
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "altintakip_prefs")

/**
 * Local preferences (aligned with iOS LocalStorageService keys).
 */
class PreferencesManager(private val context: Context) {

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val API_KEY = stringPreferencesKey("api_key")
        val APP_UI_CONFIG = stringPreferencesKey("app_ui_config")
        val APP_INFORMATION_CACHE = stringPreferencesKey("app_information_cache")
        val USER_FAVORITES = stringPreferencesKey("user_favorites")
        val DEVICE_REGISTERED = booleanPreferencesKey("device_registered")
        val USER_ASSETS = stringPreferencesKey("user_assets")
        val PORTAL_USERNAME = stringPreferencesKey("portal_username")
        val PORTAL_PASSWORD = stringPreferencesKey("portal_password")
    }

    private val gson = Gson()
    private val userAssetsType = object : TypeToken<List<UserAsset>>() {}.type
    private val intListType = object : TypeToken<List<Int>>() {}.type

    val onboardingDone: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_DONE] ?: false
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = done }
    }

    suspend fun isOnboardingDone(): Boolean = context.dataStore.data.map { it[Keys.ONBOARDING_DONE] ?: false }.first()

    val apiKey: Flow<String?> = context.dataStore.data.map { prefs -> prefs[Keys.API_KEY] }

    suspend fun getApiKey(): String? = context.dataStore.data.map { it[Keys.API_KEY] }.first()

    suspend fun saveApiKey(key: String) {
        context.dataStore.edit { it[Keys.API_KEY] = key }
    }

    suspend fun clearApiKey() {
        context.dataStore.edit { it.remove(Keys.API_KEY) }
    }

    suspend fun savePortalCredentials(username: String, pass: String) {
        context.dataStore.edit { 
            it[Keys.PORTAL_USERNAME] = username
            it[Keys.PORTAL_PASSWORD] = pass 
        }
    }

    suspend fun getPortalCredentials(): Pair<String, String>? {
        val user = context.dataStore.data.map { it[Keys.PORTAL_USERNAME] }.first()
        val pass = context.dataStore.data.map { it[Keys.PORTAL_PASSWORD] }.first()
        return if (!user.isNullOrBlank() && !pass.isNullOrBlank()) Pair(user, pass) else null
    }

    suspend fun clearPortalCredentials() {
        context.dataStore.edit {
            it.remove(Keys.PORTAL_USERNAME)
            it.remove(Keys.PORTAL_PASSWORD)
        }
    }

    suspend fun saveUserAssets(assets: List<UserAsset>) {
        context.dataStore.edit { it[Keys.USER_ASSETS] = gson.toJson(assets) }
    }

    suspend fun getUserAssets(): List<UserAsset> {
        val json = context.dataStore.data.map { it[Keys.USER_ASSETS] }.first() ?: return emptyList()
        return (gson.fromJson(json, userAssetsType) as? List<UserAsset>) ?: emptyList()
    }

    suspend fun saveUserFavorites(rateIds: List<Int>) {
        context.dataStore.edit { it[Keys.USER_FAVORITES] = gson.toJson(rateIds) }
    }

    suspend fun getUserFavorites(): List<Int> {
        val json = context.dataStore.data.map { it[Keys.USER_FAVORITES] }.first() ?: return emptyList()
        return (gson.fromJson(json, intListType) as? List<Int>) ?: emptyList()
    }

    // UIConfig and AppInformation cached as JSON (simplified; use proper serialization in production)
    // For Faz 0 we only need onboarding_done and api_key. Config cache can be added when we integrate API.
}
