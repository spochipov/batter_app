package com.batterapp.matrixcode

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.batterapp.matrixcode.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
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

    private fun submitCode() {
        if (codeBuffer.length != 6) return
        val code = codeBuffer.toString()
        val phraseResId = Codes.getPhraseResIdForCode(code)
        if (phraseResId != null) {
            showMatrixScreen(phraseResId)
        } else {
            codeBuffer.clear()
            updateCellsDisplay()
            Toast.makeText(this, "Неверный код", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showMatrixScreen(phraseResId: Int) {
        binding.screenCodeInput.visibility = View.GONE
        binding.screenMatrix.visibility = View.VISIBLE
        binding.matrixPhrase.visibility = View.GONE
        binding.matrixPhrase.text = getString(phraseResId)

        binding.matrixPhrase.postDelayed({
            binding.matrixPhrase.visibility = View.VISIBLE
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
    }
}
