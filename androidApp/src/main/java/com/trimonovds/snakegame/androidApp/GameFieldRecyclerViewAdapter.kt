package com.trimonovds.snakegame.androidApp

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.trimonovds.snakegame.shared.GameCell

class GameFieldRecyclerViewAdapter(private val context: Context): RecyclerView.Adapter<GameFieldRecyclerViewAdapterViewHolder>() {

    private var cells: List<GameCell> = listOf()

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    fun updateCells(newCells: List<GameCell>) {
        cells = newCells
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameFieldRecyclerViewAdapterViewHolder {
        val view = mInflater.inflate(R.layout.recyclerview_item, parent, false)
        return GameFieldRecyclerViewAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameFieldRecyclerViewAdapterViewHolder, position: Int) {
        val color: Int = when(cells[position]) {
            GameCell.FOOD -> Color.RED
            GameCell.SNAKE_BODY -> Color.BLACK
            GameCell.SNAKE_HEAD -> Color.BLACK
            GameCell.EMPTY -> Color.GRAY
        }
        holder.textView.setBackgroundColor(color);
    }

    override fun getItemCount(): Int {
        return cells.size
    }

}