package Day18

import java.io.File

val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day18/$fileName").readLines()
    val width = if(realInput) 70 else 6
    val height = if(realInput) 70 else 6

    var position = Pair(0,0)
    val goal = Pair(width,height)

    val bytes = mutableListOf<Pair<Int,Int>>()
    input.forEach {
        val bytePosition = it.split(",")
        bytes.add(Pair(bytePosition[0].toInt(), bytePosition[1].toInt()))
    }

    val bytesToSimulate = if (realInput) 1024 else 12

    val fallenBytes = bytes.slice(0..<bytesToSimulate)
    val maze = Array(width+1) { CharArray(height+1) }
    for(i in 0..width) {
        for(j in 0..height) {
            if(fallenBytes.contains(Pair(j,i))){
                maze[i][j] = '#'
            } else {
                maze[i][j] = '.'
            }
        }
    }

    val path = bfs(maze, position, goal)
    // print maze with path added as O
    for(i in 0..width) {
        for(j in 0..height) {
            if(path.contains(Pair(j,i))) {
                print("O")
            } else {
                print(maze[i][j])
            }
        }
        println()
    }

    println("Part 1:")
    println("Shortest path: ${path.size-1}")

    // Part 2
    // We add bytes from the bytes list at the position of the pair in the list and check if we can still find a path
    // If we can't find a path, we print the byte position.
    for(i in bytesToSimulate until bytes.size) {
        println("Simulating byte $i")
        val byte = bytes[i]
        maze[byte.second][byte.first] = '#'
        val path = bfs(maze, position, goal)
        if(path.isEmpty()) {
            println("Part 2:")
            println("Byte $i at position $byte is unreachable")
            break
        }
    }
}

fun bfs(maze: Array<CharArray>, position: Pair<Int, Int>, goal: Pair<Int, Int>): List<Pair<Int, Int>> {
    val queue = mutableListOf<Pair<Pair<Int, Int>, List<Pair<Int, Int>>>>()
    queue.add(Pair(position, listOf(position)))
    val visited = mutableSetOf<Pair<Int, Int>>()
    visited.add(position)

    while(queue.isNotEmpty()) {
        val (current, path) = queue.removeAt(0)
        if(current == goal) {
            return path
        }

        val (x, y) = current
        val neighbors = listOf(Pair(x+1, y), Pair(x-1, y), Pair(x, y+1), Pair(x, y-1))
        for(neighbor in neighbors) {
            val (nx, ny) = neighbor
            if(nx < 0 || nx >= maze.size || ny < 0 || ny >= maze[0].size) {
                continue
            }
            if(maze[nx][ny] == '#' || visited.contains(neighbor)) {
                continue
            }
            visited.add(neighbor)
            queue.add(Pair(neighbor, path + neighbor))
        }
    }
    return emptyList<Pair<Int, Int>>()
}
