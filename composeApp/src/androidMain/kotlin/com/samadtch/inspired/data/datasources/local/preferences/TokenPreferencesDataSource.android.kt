package com.samadtch.inspired.data.datasources.local.preferences

import android.content.SharedPreferences
import org.koin.dsl.module

class TokenPreferencesDataSourceAndroid(
    private val sharedPrefs: SharedPreferences
) : TokenPreferencesDataSource {

    override fun saveToken(accessToken: String, refreshToken: String) {
        val editor = sharedPrefs.edit()
        editor.putString("access_token", accessToken)
        editor.putString("refresh_token", refreshToken)
        editor.apply()
    }

    override fun resetTokens() {
        val editor = sharedPrefs.edit()
        editor.remove("access_token")
        editor.remove("refresh_token")
        editor.apply()
    }

    override fun getAccessToken(): String? = sharedPrefs.getString("access_token", null)

    override fun getRefreshToken(): String? = sharedPrefs.getString("refresh_token", null)

}

actual fun getTokenPreferencesDataSource() = module {
    single<TokenPreferencesDataSource> { TokenPreferencesDataSourceAndroid(get()) }
}