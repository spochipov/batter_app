package com.batterapp.matrixcode

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.batterapp.matrixcode.databinding.ItemCodePhraseBinding

class CodesAdapter(
    private var items: MutableList<CodePhraseItem>,
    private val onItemClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onToggleActiveClick: (Int) -> Unit
) : RecyclerView.Adapter<CodesAdapter.ViewHolder>() {

    fun updateList(newList: MutableList<CodePhraseItem>) {
        items = newList
        notifyDataSetChanged()
    }

    fun getList(): List<CodePhraseItem> = items.toList()

    fun removeAt(position: Int) {
        if (position !in items.indices) return
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCodePhraseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemCodePhraseBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.itemForeground.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) onItemClick(pos)
            }
        }

        fun bind(item: CodePhraseItem) {
            binding.itemCode.text = item.code
            binding.itemStatus.text = if (item.isActive) binding.root.context.getString(R.string.settings_status_active)
            else binding.root.context.getString(R.string.settings_status_inactive)
            binding.itemPhrasePreview.text = item.phrase.take(80).let { if (item.phrase.length > 80) "$it…" else it }
        }
    }
}
