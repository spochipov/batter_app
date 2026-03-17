package com.batterapp.matrixcode

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.batterapp.matrixcode.databinding.ActivityEditCodeBinding

class EditCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditCodeBinding
    private lateinit var repository: SettingsRepository
    private var editIndex: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = SettingsRepository(this)
        editIndex = intent.getIntExtra(EXTRA_INDEX, -1)

        val list = repository.getCodePhraseList().toMutableList()
        if (editIndex in list.indices) {
            val item = list[editIndex]
            binding.editCode.setText(item.code)
            binding.editPhrase.setText(item.phrase)
            binding.switchCodeActive.isChecked = item.isActive
        } else {
            binding.switchCodeActive.isChecked = true
        }

        binding.editCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val digits = s?.filter { it.isDigit() }?.take(6) ?: ""
                if (digits != s.toString()) {
                    binding.editCode.setText(digits)
                    binding.editCode.setSelection(digits.length)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.editPhrase.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePhraseCounter()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        updatePhraseCounter()

        binding.btnSaveEdit.setOnClickListener { save() }
        binding.btnBackEdit.setOnClickListener { finish() }
    }

    private fun updatePhraseCounter() {
        val len = binding.editPhrase.text.length
        binding.editPhraseCounter.text = getString(R.string.settings_phrase_min_max).let { _ ->
            "$len / 10000 (мин. 3)"
        }
    }

    private fun save() {
        val code = binding.editCode.text.toString().trim()
        val phrase = binding.editPhrase.text.toString()

        if (code.length != 6) {
            Toast.makeText(this, R.string.settings_error_codes, Toast.LENGTH_SHORT).show()
            return
        }
        if (phrase.length < 3) {
            Toast.makeText(this, R.string.settings_phrase_min_max, Toast.LENGTH_SHORT).show()
            return
        }
        if (phrase.length > 10000) {
            Toast.makeText(this, R.string.settings_phrase_min_max, Toast.LENGTH_SHORT).show()
            return
        }

        var list = repository.getCodePhraseList().toMutableList()
        val isActive = binding.switchCodeActive.isChecked
        val item = CodePhraseItem(code = code, phrase = phrase, isActive = isActive)

        val targetIndex = if (editIndex in list.indices) {
            list[editIndex] = item
            editIndex
        } else {
            list.add(item)
            list.lastIndex
        }

        if (isActive) {
            list = list.mapIndexed { index, old ->
                if (index != targetIndex && old.code == code) old.copy(isActive = false) else old
            }.toMutableList()
        }

        repository.saveCodePhraseList(list)
        Toast.makeText(this, R.string.settings_code_updated, Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        const val EXTRA_INDEX = "edit_index"
    }
}
