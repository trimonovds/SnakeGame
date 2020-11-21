package com.trimonovds.snakegame.androidApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.trimonovds.snakegame.shared.GameEngine
import com.trimonovds.snakegame.shared.GameSettings
import com.trimonovds.snakegame.shared.Size
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val gameEngine: GameEngine = GameEngine(GameSettings(Size(5, 5)))
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = "Snake game"

        scope.launch {
            gameEngine.run()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
