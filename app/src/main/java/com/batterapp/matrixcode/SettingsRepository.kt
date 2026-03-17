package com.batterapp.matrixcode

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject

data class CodePhraseItem(
    val code: String,
    val phrase: String,
    val isActive: Boolean = true
)

class SettingsRepository(context: Context) {

    private val appContext = context.applicationContext
    private val prefs: SharedPreferences = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        migrateFromLegacyIfNeeded()
        if (!prefs.contains(KEY_CODES_JSON)) {
            initDefaults()
        }
    }

    fun getMasterPassword(): String =
        prefs.getString(KEY_MASTER_PASSWORD, DEFAULT_MASTER_PASSWORD)!!

    fun saveMasterPassword(password: String) {
        prefs.edit { putString(KEY_MASTER_PASSWORD, password) }
    }

    fun getCodePhraseList(): List<CodePhraseItem> {
        val json = prefs.getString(KEY_CODES_JSON, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            List(arr.length()) { i ->
                val obj = arr.getJSONObject(i)
                CodePhraseItem(
                    code = obj.optString("code", ""),
                    phrase = obj.optString("phrase", ""),
                    isActive = obj.optBoolean("active", true)
                )
            }.sortedWith(compareBy({ !it.isActive }, { it.code })) // active first
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getPhraseForCode(code: String): String? =
        getCodePhraseList().find { it.isActive && it.code == code }?.phrase

    fun saveCodePhraseList(list: List<CodePhraseItem>) {
        val arr = JSONArray()
        list.forEach { item ->
            arr.put(JSONObject().apply {
                put("code", item.code)
                put("phrase", item.phrase)
                put("active", item.isActive)
            })
        }
        prefs.edit { putString(KEY_CODES_JSON, arr.toString()) }
    }

    private fun migrateFromLegacyIfNeeded() {
        if (prefs.contains(KEY_CODES_JSON)) return
        if (!prefs.contains("${KEY_CODE_PREFIX}1")) return
        val list = (1..4).map { i ->
            val code = prefs.getString("$KEY_CODE_PREFIX$i", "") ?: ""
            val phrase = prefs.getString("$KEY_PHRASE_PREFIX$i", "") ?: ""
            CodePhraseItem(code = code, phrase = phrase, isActive = true)
        }
        saveCodePhraseList(list)
        prefs.edit {
            remove("${KEY_CODE_PREFIX}1"); remove("${KEY_CODE_PREFIX}2")
            remove("${KEY_CODE_PREFIX}3"); remove("${KEY_CODE_PREFIX}4")
            remove("${KEY_PHRASE_PREFIX}1"); remove("${KEY_PHRASE_PREFIX}2")
            remove("${KEY_PHRASE_PREFIX}3"); remove("${KEY_PHRASE_PREFIX}4")
        }
    }

    private fun initDefaults() {
        prefs.edit { putString(KEY_MASTER_PASSWORD, DEFAULT_MASTER_PASSWORD) }
        val list = listOf(
            CodePhraseItem("123456", appContext.getString(R.string.matrix_phrase_1), true),
            CodePhraseItem("654321", appContext.getString(R.string.matrix_phrase_2), true),
            CodePhraseItem("111222", appContext.getString(R.string.matrix_phrase_3), true),
            CodePhraseItem("999888", appContext.getString(R.string.matrix_phrase_4), true)
        )
        saveCodePhraseList(list)
    }

    companion object {
        private const val PREFS_NAME = "batter_code_settings"
        private const val KEY_MASTER_PASSWORD = "master_password"
        private const val KEY_CODES_JSON = "codes_phrases_json"
        private const val KEY_CODE_PREFIX = "code_"
        private const val KEY_PHRASE_PREFIX = "phrase_"
        private const val DEFAULT_MASTER_PASSWORD = "000000"
    }
}
