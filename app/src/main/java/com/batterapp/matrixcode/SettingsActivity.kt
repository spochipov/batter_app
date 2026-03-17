package com.batterapp.matrixcode

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.batterapp.matrixcode.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMasterPassword.setOnClickListener {
            startActivity(Intent(this, MasterPasswordActivity::class.java))
        }
        binding.btnCodesPhrases.setOnClickListener {
            startActivity(Intent(this, CodesListActivity::class.java))
        }
        binding.btnBackSettings.setOnClickListener { finish() }
    }
}
