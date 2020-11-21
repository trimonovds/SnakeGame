package com.trimonovds.snakegame.shared

import kotlin.random.Random

data class Point(val x: Int, val y: Int) {
    companion object {
        fun rand(size: Size): Point {
            val randX = Random.nextInt(0, size.width)
            val randY = Random.nextInt(0, size.height)
            return Point(randX, randY)
        }
    }
}
