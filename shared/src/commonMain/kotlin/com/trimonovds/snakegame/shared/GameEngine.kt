package com.trimonovds.snakegame.shared


class GameEngine(private val settings: GameSettings) {

    private var snakeState: SnakeState = SnakeState(arrayOf(Point(0, 0)), Direction.RIGHT)
    private var userDirection: Direction? = null

    fun run() {
        println("Let the game begin!!!")
        var finished = false
        while (!finished) {
            println("Snake: ${snakeState.points.joinToString(", ")}")
            snakeState = snakeState.nextState(userDirection)
            finished = !snakeState.isValid(settings.fieldSize)
        }
        println("You lose")
    }
}

private fun SnakeState.nextState(userDirection: Direction?): SnakeState {
    val nextStateDirection: Direction = nextStateDirection(userDirection)
    val newFirstPoint = points.first().nextByDirection(nextStateDirection)
    val newPoints = points.toMutableList()
    newPoints.add(0, newFirstPoint)
    newPoints.removeLast()
    return this.copy(points = newPoints.toTypedArray())
}

private fun SnakeState.nextStateDirection(userDirection: Direction?): Direction {
    return if (userDirection != null) {
        if (userDirection.isOpposite(this.direction) || userDirection == direction) {
            this.direction
        } else {
            userDirection
        }
    } else {
        this.direction
    }
}

private fun Direction.isOpposite(direction: Direction): Boolean {
    return when (direction) {
        Direction.TOP -> this == Direction.BOTTOM
        Direction.BOTTOM -> this == Direction.TOP
        Direction.LEFT -> this == Direction.RIGHT
        Direction.RIGHT -> this == Direction.LEFT
    }
}

private fun SnakeState.isValid(fieldSize: Size): Boolean {
    val firstPoint = this.points.first()
    val isFirstPointInField =
        firstPoint.x >= 0 && firstPoint.x <= fieldSize.width && firstPoint.y >= 0 && firstPoint.y <= fieldSize.height
    val noIntersectionWithOwnPoints = this.points.toSet().size == this.points.size
    return isFirstPointInField && noIntersectionWithOwnPoints
}

private fun Point.nextByDirection(direction: Direction): Point {
    return when (direction) {
        Direction.TOP -> Point(x, y - 1)
        Direction.LEFT -> Point(x - 1, y)
        Direction.BOTTOM -> Point(x, y + 1)
        Direction.RIGHT -> Point(x + 1, y)
    }
}