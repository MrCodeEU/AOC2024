package Day14

import java.io.File

val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day14/$fileName").readLines()
    val robots = mutableListOf<Robot>()
    val initialPositions = mutableListOf<Pair<Int, Int>>()

    val width = if (realInput) 101 else 11
    val height = if (realInput) 103 else 7
    val seconds = 100

    input.forEach { line ->
        val p = line.substring(2).split(" ")[0]
        val v = line.split(" ")[1].substring(2)
        val pos = Pair(p.split(",")[0].toInt(), p.split(",")[1].toInt())
        initialPositions.add(pos)
        robots.add(Robot(pos, Pair(v.split(",")[0].toInt(), v.split(",")[1].toInt())))
    }

    // Simulate the robots movement
    // when width or height larger then value teleport by
    // subtracting the values of width / height
    // if smaller than 0 add width / height

    for (i in 1..100){
        robots.forEach { robot ->
            var x = robot.position.first + robot.velocity.first
            var y = robot.position.second + robot.velocity.second

            if(x >= width) x -= width
            if(y >= height) y -= height
            if(x < 0) x += width
            if(y < 0) y += height
            robot.position = Pair(x, y)
        }
    }

    // print roboter positions as grid with number of robots at each position in width * height grid
    val grid = Array(height) { Array(width) { 0 } }
    robots.forEach { robot ->
        grid[robot.position.second][robot.position.first]++
    }

    grid.forEach { row ->
        row.forEach { cell ->
            print(if (cell > 0) "$cell" else ".")
        }
        println()
    }

    // seperate the grid into 4 quadrants (ignoring the center lines)
    // and sum the number of robots in each quadrant
    val topLeft = grid.slice(0 until height / 2).map { it.slice(0 until width / 2).sum() }.sum()
    val topRight = grid.slice(0 until height / 2).map { it.slice((width / 2 + 1) until width).sum() }.sum()
    val bottomLeft = grid.slice((height / 2 + 1) until height).map { it.slice(0 until width / 2).sum() }.sum()
    val bottomRight = grid.slice((height / 2 + 1) until height).map { it.slice((width / 2 + 1) until width).sum() }.sum()
    

    println("Top left: $topLeft")
    println("Top right: $topRight")
    println("Bottom left: $bottomLeft")
    println("Bottom right: $bottomRight")

    println("Part 1:")
    println("Safety factor: ${topLeft * topRight * bottomLeft * bottomRight}")

    // Part 2: The robots will eventually form a Christmas tree shape
    robots.forEachIndexed { index, robot ->
        robot.position = initialPositions[index]
    }

    var consecutiveTreePatterns = 0
    var currentSecond = 0
    val requiredConsecutive = 3

    while (currentSecond < 1000000 && consecutiveTreePatterns < requiredConsecutive) {
        currentSecond++
        
        // Move robots
        robots.forEach { robot ->
            var x = robot.position.first + robot.velocity.first
            var y = robot.position.second + robot.velocity.second

            if(x >= width) x -= width
            if(y >= height) y -= height
            if(x < 0) x += width
            if(y < 0) y += height
            robot.position = Pair(x, y)
        }

        // Check for Christmas tree pattern
        val positions = robots.map { it.position }.toSet()
        val isTreePattern = positions.any { (x, y) ->
            // Check for specific Christmas tree shape:
            //     *        (x, y-3)
            //    ***      (x-1,y-2),(x,y-2),(x+1,y-2)
            //   *****     (x-2,y-1),(x-1,y-1),(x,y-1),(x+1,y-1),(x+2,y-1)
            //     |       (x,y)
            
            positions.contains(Pair(x, y-3)) &&  // top star
            positions.contains(Pair(x-1, y-2)) && positions.contains(Pair(x, y-2)) && positions.contains(Pair(x+1, y-2)) &&  // second row
            positions.contains(Pair(x-2, y-1)) && positions.contains(Pair(x-1, y-1)) && positions.contains(Pair(x, y-1)) && 
            positions.contains(Pair(x+1, y-1)) && positions.contains(Pair(x+2, y-1)) &&  // third row
            positions.contains(Pair(x, y))  // trunk
        }

        if (isTreePattern) {
            consecutiveTreePatterns++
            if (consecutiveTreePatterns == 1) {
                println("Part 2:")
                println("Christmas tree pattern detected at second: $currentSecond")
                
                // Visualize the pattern when found
                val minX = positions.minOf { it.first } - 1
                val maxX = positions.maxOf { it.first } + 1
                val minY = positions.minOf { it.second } - 1
                val maxY = positions.maxOf { it.second } + 1
                
                for (py in minY..maxY) {
                    for (px in minX..maxX) {
                        print(if (positions.contains(Pair(px, py))) "#" else ".")
                    }
                    println()
                }
            }
            break
        } else {
            consecutiveTreePatterns = 0
        }
    }
}

class Robot(var position: Pair<Int,Int>, val velocity: Pair<Int, Int>)