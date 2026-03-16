package com.batterapp.matrixcode

/**
 * Каждый код однозначно привязан к своей финальной фразе.
 */
object Codes {

    data class CodePhrase(val code: String, val phraseResId: Int)

    val codePhrases = listOf(
        CodePhrase("123456", R.string.matrix_phrase_1),
        CodePhrase("654321", R.string.matrix_phrase_2),
        CodePhrase("111222", R.string.matrix_phrase_3),
        CodePhrase("999888", R.string.matrix_phrase_4),
    )

    /**
     * @return ресурс строки фразы для данного кода или null, если код неверный
     */
    fun getPhraseResIdForCode(code: String): Int? =
        codePhrases.find { it.code == code }?.phraseResId
}
