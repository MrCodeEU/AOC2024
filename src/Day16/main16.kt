package Day16

import java.io.File

val realInput = false

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day16/$fileName").readLines()
    // Idea: recursive through whole maze. Bounds check (if next move would be # than skip)
    // TO get the best path we need to memorize the direction we were going before
    // rotation lef tor right adds 1000 score to the current path.
    // Also, no going backwards (visited) and no going to the same point twice
    // after all directions are checked we take the one with the lowest path
    // and return that path + its score
    // find all paths with their score and use the one with the lowest score
    val grid = mutableListOf<MutableList<Char>>()
    // create maze as grid
    input.forEach { row ->
        grid.add(row.toMutableList())
    }

    // find the starting point
    var start = Pair(0, 0)
    grid.forEach { row ->
        row.forEach {
            if (it == 'S') {
                start = Pair(row.indexOf(it), grid.indexOf(row))
                grid[start.second][start.first] = '.'
            }
        }
    }
    // find the end point
    var end = Pair(0, 0)
    grid.forEach { row ->
        row.forEach {
            if (it == 'E') {
                end = Pair(row.indexOf(it), grid.indexOf(row))
                grid[end.second][end.first] = '.'
            }
        }
    }

    val (bestScore, allBestPaths) = findAllBestPaths(grid, start, end)
    
    if (allBestPaths.isEmpty()) {
        println("No valid paths found!")
        return
    }
    
    // Part 1: Print one of the best paths
    val pathForDisplay = allBestPaths.first()
    val gridCopy = grid.map { it.toMutableList() }.toMutableList()
    pathForDisplay.forEach { (x, y) ->
        gridCopy[y][x] = 'X'
    }
    
    println("Part 1:")
    gridCopy.forEach { row ->
        row.forEach { print(it) }
        println()
    }
    println("Shortest path length: $bestScore")

    // Part 2: Mark all tiles that are part of any best path
    val uniqueTiles = allBestPaths.flatten().toSet()
    val gridCopy2 = grid.map { it.toMutableList() }.toMutableList()
    uniqueTiles.forEach { (x, y) ->
        gridCopy2[y][x] = 'O'
    }

    println("\nPart 2:")
    gridCopy2.forEach { row ->
        row.forEach { print(it) }
        println()
    }
    println("Number of tiles in best paths: ${uniqueTiles.size}")
}

data class PathState(
    val position: Pair<Int, Int>,
    val direction: Direction,
    val score: Int,
    val path: List<Pair<Int, Int>>
)

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

fun findAllBestPaths(
    grid: List<List<Char>>,
    start: Pair<Int, Int>,
    end: Pair<Int, Int>
): Pair<Int, List<List<Pair<Int, Int>>>> {
    val bestScores = mutableMapOf<Triple<Int, Int, Direction>, Int>()
    val queue = ArrayDeque<PathState>()
    val completePaths = mutableListOf<PathState>()
    
    // Start from all directions
    Direction.values().forEach { dir ->
        queue.add(PathState(start, dir, 0, listOf(start)))
    }
    
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        val (x, y) = current.position
        val state = Triple(x, y, current.direction)
        
        // Skip if we've found a better path to this position+direction
        val existingScore = bestScores[state]
        if (existingScore != null && existingScore <= current.score) {
            continue
        }
        bestScores[state] = current.score
        
        // Found end
        if (current.position == end) {
            completePaths.add(current)
            continue
        }
        
        // Try moving forward
        val nextPos = when (current.direction) {
            Direction.UP -> x to y - 1
            Direction.DOWN -> x to y + 1
            Direction.LEFT -> x - 1 to y
            Direction.RIGHT -> x + 1 to y
        }
        
        if (isValidMove(nextPos, grid) && nextPos !in current.path) {
            queue.add(PathState(
                nextPos,
                current.direction,
                current.score + 1,
                current.path + nextPos
            ))
        }
        
        // Try rotations
        listOf(rotateLeft(current.direction), rotateRight(current.direction)).forEach { newDir ->
            queue.add(PathState(
                current.position,
                newDir,
                current.score + 1000,
                current.path
            ))
        }
    }
    
    if (completePaths.isEmpty()) return Int.MAX_VALUE to emptyList()
    
    val bestScore = completePaths.minOf { it.score }
    val bestPaths = completePaths
        .filter { it.score == bestScore }
        .map { it.path }
    
    return bestScore to bestPaths
}

fun rotateLeft(direction: Direction) = when (direction) {
    Direction.UP -> Direction.LEFT
    Direction.LEFT -> Direction.DOWN
    Direction.DOWN -> Direction.RIGHT
    Direction.RIGHT -> Direction.UP
}

fun rotateRight(direction: Direction) = when (direction) {
    Direction.UP -> Direction.RIGHT
    Direction.RIGHT -> Direction.DOWN
    Direction.DOWN -> Direction.LEFT
    Direction.LEFT -> Direction.UP
}

fun isValidMove(pos: Pair<Int, Int>, grid: List<List<Char>>): Boolean {
    val (x, y) = pos
    return y in grid.indices && x in grid[0].indices && grid[y][x] != '#'
}
