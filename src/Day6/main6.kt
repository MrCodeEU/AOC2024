package Day6

import java.io.File

var realInput = true

fun main() {
    var fileName = "input.txt"
    if (!realInput) {
        fileName = "sample.txt"
    }
    val input = File("src/Day6/$fileName").readLines()
    val grid = input.map { it.toCharArray() }
    val visited = Array(input.size) { Array(grid[0].size) {false} }

    var guardDirection = 0 // 0 up 1 right 2 down 3 left
    var guardPosition = Pair(0,0)

    grid.forEachIndexed{ i, it ->
        if (it.contains('^')){
            guardPosition = Pair(i, it.indexOf('^'))
        }
    }

    // let guard walk until out of bounds
    while (guardPosition.first > 0 && guardPosition.second > 0 && guardPosition.first < grid.size-1 && guardPosition.second < grid[0].size-1 ){
        // Add to visited
        visited[guardPosition.first][guardPosition.second] = true
        //check collision
        when (guardDirection) {
            0 -> {
                //check collision
                if (grid[guardPosition.first-1][guardPosition.second] == '#'){
                    //rotate 90°
                    guardDirection = 1
                }
            }
            1 -> {
                //check collision
                if (grid[guardPosition.first][guardPosition.second+1] == '#'){
                    //rotate 90°
                    guardDirection = 2
                }
            }
            2 -> {
                //check collision
                if (grid[guardPosition.first+1][guardPosition.second] == '#'){
                    //rotate 90°
                    guardDirection = 3
                }
            }
            3 -> {
                //check collision
                if (grid[guardPosition.first][guardPosition.second-1] == '#'){
                    //rotate 90°
                    guardDirection = 0
                }
            }
        }
        // Move Guard
        when (guardDirection) {
            0 -> {
                guardPosition = guardPosition.copy(first = guardPosition.first - 1)
            }
            1 -> {
                guardPosition = guardPosition.copy(second = guardPosition.second + 1)
            }
            2 -> {
                guardPosition = guardPosition.copy(first = guardPosition.first + 1)
            }
            3 -> {
                guardPosition = guardPosition.copy(second = guardPosition.second - 1)
            }
        }
    }

    // add last point before leave
    visited[guardPosition.first][guardPosition.second] = true
    var visitedPoints = 0
    visited.forEach{
        it.forEach { b ->
            if(b) visitedPoints++
        }
    }

    println("Part 1")
    println("Visited points: $visitedPoints")


    grid.forEachIndexed{ i, it ->
        if (it.contains('^')){
            guardPosition = Pair(i, it.indexOf('^'))
        }
    }

    // For each empty space, try placing an obstacle and check if it creates a loop
    val loopPositions = mutableListOf<Pair<Int,Int>>()

    for (i in grid.indices) {
        for (j in grid[0].indices) {
            if (grid[i][j] == '.' && Pair(i, j) != guardPosition) {
                // Create a copy of the grid with new obstacle
                val testGrid = grid.map { it.clone() }.toTypedArray()
                testGrid[i][j] = '#'

                if (createsLoop(testGrid, guardPosition)) {
                    loopPositions.add(Pair(i, j))
                }
            }
        }
    }

    println("Part 2")
    println("Number of possible positions: ${loopPositions.size}")
}


fun createsLoop(grid: Array<CharArray>, startPos: Pair<Int,Int>): Boolean {
    val visited = mutableSetOf<Pair<Pair<Int, Int>, Int>>()
    var guardPos = startPos
    var direction = 0
    var stepCount = 0
    val maxSteps = grid.size * grid[0].size * 4 * 2 // Allow enough steps for loop validation

    // Track positions for potential loop
    val positionSequence = mutableListOf<Pair<Pair<Int, Int>, Int>>()

    while (stepCount < maxSteps) {
        stepCount++

        // Out of bounds check
        if (guardPos.first <= 0 || guardPos.second <= 0 ||
            guardPos.first >= grid.size - 1 || guardPos.second >= grid[0].size - 1) {
            return false
        }

        val currentState = Pair(guardPos, direction)
        positionSequence.add(currentState)

        // Check for loop - need at least 4 moves to form a valid loop
        if (currentState in visited && positionSequence.size > 4) {
            val loopStartIndex = positionSequence.indexOf(currentState)
            val loopLength = positionSequence.size - loopStartIndex

            // Verify loop properties:
            // 1. Loop must be confined (all positions within boundaries)
            // 2. Must repeat the same sequence
            var isValidLoop = true
            val loopPositions = positionSequence.subList(loopStartIndex, positionSequence.size)

            // Check if all positions in loop are at least 1 space from border
            for (pos in loopPositions) {
                val (p, _) = pos
                if (p.first <= 1 || p.second <= 1 ||
                    p.first >= grid.size - 2 || p.second >= grid[0].size - 2) {
                    isValidLoop = false
                    break
                }
            }

            if (isValidLoop) return true
        }

        visited.add(currentState)

        // Update direction and position
        direction = when (direction) {
            0 -> if (grid[guardPos.first - 1][guardPos.second] == '#') 1 else 0
            1 -> if (grid[guardPos.first][guardPos.second + 1] == '#') 2 else 1
            2 -> if (grid[guardPos.first + 1][guardPos.second] == '#') 3 else 2
            3 -> if (grid[guardPos.first][guardPos.second - 1] == '#') 0 else 3
            else -> direction
        }

        guardPos = when (direction) {
            0 -> guardPos.copy(first = guardPos.first - 1)
            1 -> guardPos.copy(second = guardPos.second + 1)
            2 -> guardPos.copy(first = guardPos.first + 1)
            3 -> guardPos.copy(second = guardPos.second - 1)
            else -> guardPos
        }
    }
    return false
}