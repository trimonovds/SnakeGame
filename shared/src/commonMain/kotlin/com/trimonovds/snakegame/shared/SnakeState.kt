package com.trimonovds.snakegame.shared

data class SnakeState(val points: Array<Point>, val direction: Direction) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SnakeState

        if (!points.contentEquals(other.points)) return false
        if (direction != other.direction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = points.contentHashCode()
        result = 31 * result + direction.hashCode()
        return result
    }
}
