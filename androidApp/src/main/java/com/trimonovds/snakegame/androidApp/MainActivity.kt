package com.trimonovds.snakegame.androidApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.trimonovds.snakegame.shared.*

class MainActivity : AppCompatActivity(), GameView {
    private val presenter = GamePresenter()
    private var _delegate: GameViewDelegate? = null

    override var delegate: GameViewDelegate?
        get() = _delegate
        set(value) {
            _delegate = value
        }

    override fun render(state: GameState) {
        when (state) {
            is GameState.GameOver -> println("Game over")
            is GameState.Playing -> debugPrint(state.cells)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = "Snake game"

        presenter.onAttach(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.onDetach()
    }
}

private fun debugPrint(gameCells: List<List<GameCell>>) {
    println("=== Game field ===")
    for (rowElement in gameCells.withIndex()) {
        println("Row: ${rowElement.index}: " + rowElement.value.joinToString(", "))
    }
}
