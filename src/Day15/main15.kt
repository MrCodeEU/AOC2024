package Day15

import java.io.File

val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day15/$fileName").readLines()
    val grid = mutableListOf<MutableList<Char>>()
    val moves = mutableListOf<Char>()
    var robot = Pair(0, 0)
    var counter = 0
    while (input[counter].isNotEmpty()) {
        grid.add(input[counter].toMutableList())
        counter++
    }
    counter++
    while (counter < input.size) {
        input[counter].forEach { moves.add(it) }
        counter++
    }

    grid.forEach { row ->
        row.forEach {
            if (it == '@') {
                robot = Pair(row.indexOf(it), grid.indexOf(row))
                grid[robot.second][robot.first] = '.'
            }
        }
    }

    // printGrid(grid)
    // println(moves)
    // println(robot)

    var DEBUG_COUNTER = 0

    moves.forEach { move ->
        val newRobot = when (move) {
            '^' -> Pair(robot.first, robot.second - 1)
            'v' -> Pair(robot.first, robot.second + 1)
            '<' -> Pair(robot.first - 1, robot.second)
            '>' -> Pair(robot.first + 1, robot.second)
            else -> robot
        }

        if (grid[newRobot.second][newRobot.first] == '.') {
            grid[robot.second][robot.first] = '.'
            grid[newRobot.second][newRobot.first] = '@'
            robot = newRobot
        } else if (grid[newRobot.second][newRobot.first] == 'O') {
            val canPush = when (move) {
                '^' -> canPushBoxes(grid, robot, -1, 0)
                'v' -> canPushBoxes(grid, robot, 1, 0)
                '<' -> canPushBoxes(grid, robot, 0, -1)
                '>' -> canPushBoxes(grid, robot, 0, 1)
                else -> false
            }

            if (canPush) {
                when (move) {
                    '^' -> pushBoxes(grid, robot.second, robot.first, -1, 0)
                    'v' -> pushBoxes(grid, robot.second, robot.first, 1, 0)
                    '<' -> pushBoxes(grid, robot.second, robot.first, 0, -1)
                    '>' -> pushBoxes(grid, robot.second, robot.first, 0, 1)
                }
                grid[robot.second][robot.first] = '.'
                grid[newRobot.second][newRobot.first] = '@'
                robot = newRobot
            }
        }
        if (!realInput){
            println(move)
            printGrid(grid)
            println()
        }

        DEBUG_COUNTER--
        if (DEBUG_COUNTER == 0) return
    }

    // Calculate final GPS sum
    println("Final GPS sum: ${calculateGPSSum(grid)}")
}

fun canPushBoxes(grid: List<List<Char>>, start: Pair<Int, Int>, dy: Int, dx: Int): Boolean {
    var y = start.second + dy
    var x = start.first + dx
    var boxCount = 0

    while (grid[y][x] == 'O') {
        boxCount++
        y += dy
        x += dx
    }

    return grid[y][x] == '.'
}

fun pushBoxes(grid: MutableList<MutableList<Char>>, startY: Int, startX: Int, dy: Int, dx: Int) {
    var y = startY + dy
    var x = startX + dx
    val boxes = mutableListOf<Pair<Int, Int>>()

    while (grid[y][x] == 'O') {
        boxes.add(Pair(y, x))
        y += dy
        x += dx
    }

    for (box in boxes) {
        grid[box.first][box.second] = '.'
    }

    y = startY + dy
    x = startX + dx
    for (i in boxes.indices) {
        y += dy
        x += dx
    }

    for (box in boxes) {
        grid[y][x] = 'O'
        y -= dy
        x -= dx
    }
}

fun calculateGPSSum(grid: List<List<Char>>): Int {
    var sum = 0
    for (y in grid.indices) {
        for (x in grid[y].indices) {
            if (grid[y][x] == 'O') {
                sum += (y * 100 + x)
            }
        }
    }
    return sum
}

fun printGrid(grid: List<List<Char>>) {
    grid.forEach { row ->
        row.forEach { print(it) }
        println()
    }
}