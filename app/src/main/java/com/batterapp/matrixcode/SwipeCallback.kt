package com.batterapp.matrixcode

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Простой свайп: действие выполняется сразу.
 * - Свайп влево  -> delete
 * - Свайп вправо -> toggle active
 */
class SwipeCallback(
    private val onSwipeLeft: (Int) -> Unit,
    private val onSwipeRight: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (pos == RecyclerView.NO_POSITION) return
        when (direction) {
            ItemTouchHelper.LEFT  -> onSwipeLeft(pos)
            ItemTouchHelper.RIGHT -> onSwipeRight(pos)
        }
    }
}
