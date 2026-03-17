package com.batterapp.matrixcode

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.batterapp.matrixcode.databinding.ActivityMasterPasswordBinding

class MasterPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMasterPasswordBinding
    private lateinit var repository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMasterPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = SettingsRepository(this)

        binding.editMasterPassword.setText(repository.getMasterPassword())
        binding.btnSaveMaster.setOnClickListener { save() }
        binding.btnBackMaster.setOnClickListener { finish() }
    }

    private fun save() {
        val master = binding.editMasterPassword.text.toString().trim()
        if (master.length != 6 || !master.all { it.isDigit() }) {
            Toast.makeText(this, R.string.settings_error_master, Toast.LENGTH_SHORT).show()
            return
        }
        repository.saveMasterPassword(master)
        Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show()
        finish()
    }
}
