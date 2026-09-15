package com.example.data.supabase

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig

object SupabaseConfig {
    private const val PREFS_NAME = "supabase_config"
    private const val KEY_URL = "custom_supabase_url"
    private const val KEY_KEY = "custom_supabase_anon_key"

    const val DEFAULT_SUPABASE_URL = "https://vatekggpsbyedxelaouu.supabase.co"
    const val DEFAULT_SUPABASE_ANON_KEY = "sb_publishable_TM-pC1iH2yb8v8EiRIgAmw_0_8QVMuk"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    private fun getBuildConfigField(fieldName: String): String {
        return try {
            val field = BuildConfig::class.java.getField(fieldName)
            field.get(null)?.toString()?.trim() ?: ""
        } catch (e: Throwable) {
            ""
        }
    }

    fun getSupabaseUrl(): String {
        val saved = prefs?.getString(KEY_URL, "")?.trim().orEmpty()
        if (saved.isNotBlank()) return saved

        val fromBuildConfig = getBuildConfigField("SUPABASE_URL")
        if (fromBuildConfig.isNotBlank() && !fromBuildConfig.contains("your-project")) return fromBuildConfig

        val fromExpoBuildConfig = getBuildConfigField("EXPO_PUBLIC_SUPABASE_URL")
        if (fromExpoBuildConfig.isNotBlank() && !fromExpoBuildConfig.contains("your-project")) return fromExpoBuildConfig

        return DEFAULT_SUPABASE_URL
    }

    fun getSupabaseAnonKey(): String {
        val saved = prefs?.getString(KEY_KEY, "")?.trim().orEmpty()
        if (saved.isNotBlank()) return saved

        val fromBuildConfig = getBuildConfigField("SUPABASE_ANON_KEY")
        if (fromBuildConfig.isNotBlank() && !fromBuildConfig.contains("your-anon-key")) return fromBuildConfig

        val fromExpoBuildConfig = getBuildConfigField("EXPO_PUBLIC_SUPABASE_ANON_KEY")
        if (fromExpoBuildConfig.isNotBlank() && !fromExpoBuildConfig.contains("your-anon-key")) return fromExpoBuildConfig

        return DEFAULT_SUPABASE_ANON_KEY
    }

    fun saveCustomConfig(url: String, key: String) {
        prefs?.edit()
            ?.putString(KEY_URL, url.trim())
            ?.putString(KEY_KEY, key.trim())
            ?.apply()
    }

    fun isConfigured(): Boolean {
        val url = getSupabaseUrl()
        val key = getSupabaseAnonKey()
        val hasValidUrl = (url.startsWith("http://") || url.startsWith("https://")) && !url.contains("your-project")
        val hasValidKey = key.length > 20 && !key.contains("your-anon-key")
        return hasValidUrl && hasValidKey
    }

    fun getRealtimeWebSocketUrl(): String {
        val rawBase = getSupabaseUrl().trim().trimEnd('/')
        if (rawBase.isBlank()) return ""
        val baseUrl = if (!rawBase.startsWith("http://") && !rawBase.startsWith("https://")) {
            "https://$rawBase"
        } else {
            rawBase
        }
        val wsUrl = if (baseUrl.startsWith("https://")) {
            baseUrl.replace("https://", "wss://")
        } else {
            baseUrl.replace("http://", "ws://")
        }
        val key = getSupabaseAnonKey().trim()
        return "$wsUrl/realtime/v1/websocket?apikey=$key&vsn=1.0.0"
    }
}
