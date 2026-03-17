package com.batterapp.matrixcode

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.batterapp.matrixcode.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsRepository: SettingsRepository
    private val cells: List<TextView> by lazy {
        listOf(
            binding.cell0, binding.cell1, binding.cell2,
            binding.cell3, binding.cell4, binding.cell5
        )
    }
    private val digitButtons: List<Button> by lazy {
        listOf(
            binding.btn0, binding.btn1, binding.btn2, binding.btn3, binding.btn4,
            binding.btn5, binding.btn6, binding.btn7, binding.btn8, binding.btn9
        )
    }

    private var codeBuffer = StringBuilder(6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        settingsRepository = SettingsRepository(this)

        digitButtons.forEachIndexed { index, btn ->
            btn.setOnClickListener { appendDigit(index.toString()) }
        }
        binding.btnBack.setOnClickListener { backspace() }
        binding.btnEnter.setOnClickListener { submitCode() }

        binding.btnCloseMatrix.setOnClickListener { closeMatrixScreen() }

        updateCellsDisplay()
    }

    private fun appendDigit(d: String) {
        if (codeBuffer.length >= 6) return
        codeBuffer.append(d)
        updateCellsDisplay()
    }

    private fun backspace() {
        if (codeBuffer.isEmpty()) return
        codeBuffer.deleteAt(codeBuffer.lastIndex)
        updateCellsDisplay()
    }

    private fun updateCellsDisplay() {
        cells.forEachIndexed { index, tv ->
            tv.text = if (index < codeBuffer.length) codeBuffer[index].toString() else ""
        }
    }

    private fun setCellsBackground(drawableResId: Int) {
        cells.forEach { it.setBackgroundResource(drawableResId) }
    }

    private fun showCodeError() {
        binding.codeInputLoading.visibility = View.VISIBLE
        binding.codeInputLoading.postDelayed({
            if (!isFinishing) {
                binding.codeInputLoading.visibility = View.GONE
                setCellsBackground(R.drawable.code_cell_bg_error)
                AlertDialog.Builder(this)
                    .setMessage(R.string.error_wrong_code)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        setCellsBackground(R.drawable.code_cell_bg)
                        codeBuffer.clear()
                        updateCellsDisplay()
                    }
                    .setCancelable(false)
                    .show()
            }
        }, LOADING_DURATION_MS)
    }

    private fun submitCode() {
        if (codeBuffer.length != 6) return
        val code = codeBuffer.toString()
        if (code == settingsRepository.getMasterPassword()) {
            codeBuffer.clear()
            updateCellsDisplay()
            startActivity(Intent(this, SettingsActivity::class.java))
            return
        }
        val phrase = settingsRepository.getPhraseForCode(code)
        if (phrase != null) {
            showMatrixScreen(phrase)
        } else {
            showCodeError()
        }
    }

    private fun showMatrixScreen(phrase: String) {
        binding.screenCodeInput.visibility = View.GONE
        binding.screenMatrix.visibility = View.VISIBLE
        binding.matrixPhrase.visibility = View.GONE
        binding.matrixPhrase.alpha = 0f
        binding.matrixPhrase.text = phrase

        binding.matrixPhrase.postDelayed({
            binding.matrixPhrase.visibility = View.VISIBLE
            binding.matrixPhrase.animate()
                .alpha(1f)
                .setDuration(PHRASE_FADE_DURATION_MS)
                .start()
        }, PHRASE_DELAY_MS)
    }

    private fun closeMatrixScreen() {
        binding.screenMatrix.visibility = View.GONE
        binding.screenCodeInput.visibility = View.VISIBLE
        codeBuffer.clear()
        updateCellsDisplay()
    }

    companion object {
        private const val PHRASE_DELAY_MS = 3000L
        private const val PHRASE_FADE_DURATION_MS = 2000L
        private const val LOADING_DURATION_MS = 2000L
    }
}
