package com.batterapp.matrixcode

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.batterapp.matrixcode.databinding.ActivityCodesListBinding

class CodesListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCodesListBinding
    private lateinit var repository: SettingsRepository
    private lateinit var adapter: CodesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCodesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = SettingsRepository(this)

        adapter = CodesAdapter(
            items = repository.getCodePhraseList().toMutableList(),
            onItemClick = { index -> openEdit(index) },
            onDeleteClick = { index -> confirmDelete(index) },
            onToggleActiveClick = { index -> toggleActive(index) }
        )
        binding.recyclerCodes.layoutManager = LinearLayoutManager(this)
        binding.recyclerCodes.adapter = adapter

        val touchHelper = ItemTouchHelper(
            SwipeCallback(
                onSwipeLeft = { position -> confirmDelete(position) },
                onSwipeRight = { position -> toggleActive(position) }
            )
        )
        touchHelper.attachToRecyclerView(binding.recyclerCodes)

        binding.btnBackCodesList.setOnClickListener { finish() }
        binding.btnAddCode.setOnClickListener { openEdit(-1) }
    }

    override fun onResume() {
        super.onResume()
        adapter.updateList(repository.getCodePhraseList().toMutableList())
    }

    private fun openEdit(index: Int) {
        startActivity(Intent(this, EditCodeActivity::class.java).putExtra(EditCodeActivity.EXTRA_INDEX, index))
    }

    private fun confirmDelete(index: Int) {
        val item = adapter.getList().getOrNull(index) ?: return
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.settings_delete_confirm, item.code))
            .setPositiveButton(R.string.settings_delete_yes) { _, _ ->
                deleteItemConfirmed(index)
            }
            .setNegativeButton(R.string.settings_delete_no) { _, _ ->
                adapter.updateList(repository.getCodePhraseList().toMutableList())
            }
            .setCancelable(true)
            .show()
    }

    private fun deleteItemConfirmed(index: Int) {
        if (index !in adapter.getList().indices) return
        adapter.removeAt(index)
        repository.saveCodePhraseList(adapter.getList())
        Toast.makeText(this, R.string.settings_code_deleted, Toast.LENGTH_SHORT).show()
    }

    private fun toggleActive(index: Int) {
        val current = adapter.getList().toMutableList()
        if (index !in current.indices) return
        val item = current[index]
        val newActive = !item.isActive
        current[index] = item.copy(isActive = newActive)

        // если включаем — выключаем все другие совпадающие коды
        if (newActive) {
            for (i in current.indices) {
                if (i != index && current[i].code == item.code) {
                    current[i] = current[i].copy(isActive = false)
                }
            }
        }

        repository.saveCodePhraseList(current)
        adapter.updateList(repository.getCodePhraseList().toMutableList())
    }
}
