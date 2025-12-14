package com.samadtch.inspired.data.datasources.local.preferences

import android.content.SharedPreferences
import org.koin.dsl.module
import androidx.core.content.edit

class TokenPreferencesDataSourceAndroid(
    private val sharedPrefs: SharedPreferences
) : TokenPreferencesDataSource {

    override fun saveToken(accessToken: String, refreshToken: String, expiresAt: Int) {
        sharedPrefs.edit {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            putInt("expires_at", expiresAt)
        }
    }

    override fun resetTokens() {
        sharedPrefs.edit {
            remove("access_token")
            remove("refresh_token")
            remove("expires_at")
        }
    }

    override fun getAccessToken(): String? = sharedPrefs.getString("access_token", null)

    override fun getRefreshToken(): String? = sharedPrefs.getString("refresh_token", null)

    override fun expiresAt(): Int = sharedPrefs.getInt("expires_at", -1)

}

actual fun getTokenPreferencesDataSource() = module {
    single<TokenPreferencesDataSource> { TokenPreferencesDataSourceAndroid(get()) }
}