package com.trimonovds.snakegame.androidApp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trimonovds.snakegame.shared.GamePresenter
import com.trimonovds.snakegame.shared.GameState
import com.trimonovds.snakegame.shared.GameView
import com.trimonovds.snakegame.shared.GameViewDelegate

class MainActivity : AppCompatActivity(), GameView {

    companion object {
        const val numberOfColumns: Int = 10
    }

    private val presenter = GamePresenter(numberOfColumns)
    private val adapter: GameFieldRecyclerViewAdapter by lazy {
        GameFieldRecyclerViewAdapter(this)
    }

    override var delegate: GameViewDelegate? = null

    override fun render(state: GameState) {
        val cells = when (state) {
            is GameState.GameOver -> emptyList()
            is GameState.Playing -> state.cells.flatten()
        }
        adapter.updateCells(cells)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.rvGameField)
        recyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        recyclerView.adapter = adapter

        val upButton = findViewById<Button>(R.id.button_up)
        val downButton = findViewById<Button>(R.id.button_down)
        val leftButton = findViewById<Button>(R.id.button_left)
        val rightButton = findViewById<Button>(R.id.button_right)
        upButton.setOnClickListener {
            delegate?.onDidTapButton(GameViewDelegate.GameViewButton.TOP)
        }
        downButton.setOnClickListener {
            delegate?.onDidTapButton(GameViewDelegate.GameViewButton.BOTTOM)
        }
        leftButton.setOnClickListener {
            delegate?.onDidTapButton(GameViewDelegate.GameViewButton.LEFT)
        }
        rightButton.setOnClickListener {
            delegate?.onDidTapButton(GameViewDelegate.GameViewButton.RIGHT)
        }

        presenter.onAttach(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onDetach()
    }

}