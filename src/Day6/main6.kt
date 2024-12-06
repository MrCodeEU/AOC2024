package Day6

import java.io.File

val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day6/$fileName").readLines()
    val grid = input.map { it.toCharArray() }.toTypedArray()

    println("Part 1: ${countVisitedPositions(grid)}")
    println("Part 2: ${findPossibleObstacles(grid)}")
}

data class Position(val row: Int, val col: Int)

// Part 1: Simply follow the guard's path until out of bounds
fun countVisitedPositions(grid: Array<CharArray>): Int {
    val visited = mutableSetOf<Position>()
    var pos = findGuardStart(grid)
    var direction = 0 // 0=up, 1=right, 2=down, 3=left

    while (true) {
        visited.add(pos)
        val nextPos = getNextPosition(pos, direction)

        if (!isInBounds(nextPos, grid)) break

        if (grid[nextPos.row][nextPos.col] == '#') {
            direction = (direction + 1) % 4
        } else {
            pos = nextPos
        }
    }

    return visited.size
}

// Part 2: Check positions along the guard's path
fun findPossibleObstacles(grid: Array<CharArray>): Int {
    var pos = findGuardStart(grid)
    var direction = 0
    val possiblePositions = mutableSetOf<Position>()
    val checkedPositions = mutableSetOf<Position>()

    while (true) {
        val nextPos = getNextPosition(pos, direction)
        if (!isInBounds(nextPos, grid)) break

        if (grid[nextPos.row][nextPos.col] == '#') {
            direction = (direction + 1) % 4
            continue
        }

        // Check if placing an obstacle here would create a cycle
        if (nextPos !in checkedPositions && wouldCreateCycle(grid, pos, nextPos, direction)) {
            possiblePositions.add(nextPos)
        }

        checkedPositions.add(nextPos)
        pos = nextPos
    }

    return possiblePositions.size
}

fun wouldCreateCycle(grid: Array<CharArray>, currentPos: Position, obstaclePos: Position, initialDir: Int): Boolean {
    val visited = mutableMapOf<Position, MutableSet<Int>>() // position -> set of directions it was visited from
    var pos = currentPos
    var direction = initialDir

    // Create a temporary grid with the obstacle
    val tempGrid = grid.map { it.clone() }.toTypedArray()
    tempGrid[obstaclePos.row][obstaclePos.col] = '#'

    while (true) {
        // Add current position and direction to visited
        visited.getOrPut(pos) { mutableSetOf() }.add(direction)

        val nextPos = getNextPosition(pos, direction)

        // Check if we're going out of bounds
        if (!isInBounds(nextPos, grid)) return false

        // If we hit an obstacle, turn right
        if (tempGrid[nextPos.row][nextPos.col] == '#') {
            direction = (direction + 1) % 4
            // If we've been here before in this direction, we found a cycle
            if (direction in visited.getOrDefault(pos, mutableSetOf())) {
                return true
            }
        } else {
            pos = nextPos
            // If we've been here before in this direction, we found a cycle
            if (direction in visited.getOrDefault(pos, mutableSetOf())) {
                return true
            }
        }
    }
}

fun findGuardStart(grid: Array<CharArray>): Position {
    grid.forEachIndexed { row, chars ->
        chars.forEachIndexed { col, char ->
            if (char == '^') return Position(row, col)
        }
    }
    error("Guard not found")
}

fun getNextPosition(pos: Position, direction: Int): Position = when(direction) {
    0 -> Position(pos.row - 1, pos.col) // up
    1 -> Position(pos.row, pos.col + 1) // right
    2 -> Position(pos.row + 1, pos.col) // down
    else -> Position(pos.row, pos.col - 1) // left
}

fun isInBounds(pos: Position, grid: Array<CharArray>): Boolean =
    pos.row in grid.indices && pos.col in grid[0].indices