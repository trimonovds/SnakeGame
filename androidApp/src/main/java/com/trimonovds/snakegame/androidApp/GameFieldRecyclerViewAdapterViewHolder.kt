package com.trimonovds.snakegame.androidApp

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GameFieldRecyclerViewAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val textView: TextView = itemView.findViewById(R.id.info_text)
}