package com.trimonovds.snakegame.androidApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.trimonovds.snakegame.shared.GameEngine
import com.trimonovds.snakegame.shared.GameSettings
import com.trimonovds.snakegame.shared.Size

class MainActivity : AppCompatActivity() {
    private val gameEngine: GameEngine = GameEngine(GameSettings(Size(10, 10)))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tv: TextView = findViewById(R.id.text_view)
        tv.text = "Snake game"

        gameEngine.run()
    }
}
