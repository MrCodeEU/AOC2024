package Day20

import java.io.File

data class quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day20/$fileName").readLines()
    val maze = input.map { it.toCharArray() }
    var start = Pair(0, 0)
    var end = Pair(0, 0)

    for (i in 0..<maze.size) {
        for (j in 0..<maze[i].size) {
            if (maze[i][j] == 'S') {
                start = Pair(i, j)
            } else if (maze[i][j] == 'E') {
                end = Pair(i, j)
            }
        }
    }

    val directions = listOf(Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0))


    // First get normal path length
    val normalLength = findShortestPathLength(start, end, maze)
    println("Normal path length: $normalLength")
    
    // Replace shortcuts set with a set of path details
    val shortcuts = mutableSetOf<quadruple<Pair<Int, Int>, Pair<Int, Int>, Pair<Int, Int>, Int>>()  // start, mid, end, savings
    var processed = 0
    val total = maze.size * maze[0].size
    
    // For each position
    for (i in maze.indices) {
        for (j in maze[0].indices) {
            processed++
            if (processed % 100 == 0) {
                println("Progress: ${(processed * 100) / total}%")
            }
            
            val pos = Pair(i, j)
            
            // Try each direction for first move
            for (dir in directions) {
                val mid = Pair(pos.first + dir.first, pos.second + dir.second)
                val jump = Pair(mid.first + dir.first, mid.second + dir.second)
                
                if (!isTrack(pos, maze) || !isTrack(jump, maze)) continue
                if (!isWallPassage(pos, mid, jump, maze)) continue
                
                val startToCurrent = findShortestPathLength(start, pos, maze)
                if (startToCurrent == Int.MAX_VALUE) continue
                
                val afterJumpToEnd = findShortestPathLength(jump, end, maze)
                if (afterJumpToEnd == Int.MAX_VALUE) continue
                
                val pathWithShortcut = startToCurrent + 2 + afterJumpToEnd
                val savings = normalLength - pathWithShortcut
                
                if (savings > 0) {
                    shortcuts.add(quadruple(pos, mid, jump, savings))
                }
            }
        }
    }

    println("\nFound ${shortcuts.size} unique shortcuts")
    println("Shortcuts by savings: ${shortcuts.map { it.fourth }.sorted()}")
    println("Unique paths saving 100 or more picoseconds: ${shortcuts.count { it.fourth >= 100 }}")

    // Part 2
    println("\nPart 2 - Cheats up to 20 steps:")
    var shortcutsCount = 0
    processed = 0
    
    // For each position
    for (i in maze.indices) {
        for (j in maze[0].indices) {
            processed++
            if (processed % 100 == 0) {
                println("Progress: ${(processed * 100) / total}%")
            }
            
            val pos = Pair(i, j)
            if (!isTrack(pos, maze)) continue
            
            val startToCurrent = findShortestPathLength(start, pos, maze)
            if (startToCurrent == Int.MAX_VALUE) continue
            
            // Find all possible endpoints reachable within 20 steps
            val destinations = findAllEndpoints(pos, maze, 20)
            
            for ((endpoint, pathLength) in destinations) {
                val afterJumpToEnd = findShortestPathLength(endpoint, end, maze)
                if (afterJumpToEnd == Int.MAX_VALUE) continue
                
                val pathWithShortcut = startToCurrent + pathLength + afterJumpToEnd
                val savings = normalLength - pathWithShortcut
                
                if (savings >= 100) {
                    shortcutsCount++
                }
            }
        }
    }

    println("\nFound $shortcutsCount shortcuts saving 100 or more picoseconds")
}

private fun findAllEndpoints(
    start: Pair<Int, Int>,
    maze: List<CharArray>,
    maxSteps: Int
): Map<Pair<Int, Int>, Int> {
    val endpoints = mutableMapOf<Pair<Int, Int>, Int>()
    val visited = mutableSetOf<Pair<Int, Int>>()
    val queue = ArrayDeque<Triple<Pair<Int, Int>, Int, Boolean>>() // pos, steps, hasPassedWall
    queue.add(Triple(start, 0, false))
    
    while (queue.isNotEmpty()) {
        val (pos, steps, hasPassedWall) = queue.removeFirst()
        
        if (steps > maxSteps) continue
        if (pos in visited) continue
        visited.add(pos)
        
        // If we've passed a wall and this is a valid track, it's an endpoint
        if (hasPassedWall && isTrack(pos, maze) && steps > 0) {
            endpoints[pos] = steps
        }
        
        if (steps == maxSteps) continue
        
        // Try all directions
        val directions = listOf(Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0))
        for (dir in directions) {
            val next = Pair(pos.first + dir.first, pos.second + dir.second)
            if (!isInBounds(next, maze)) continue
            val newHasPassedWall = hasPassedWall || maze[next.first][next.second] == '#'
            queue.add(Triple(next, steps + 1, newHasPassedWall))
        }
    }
    
    return endpoints
}

private fun findPossibleJumps(
    start: Pair<Int, Int>,
    maze: List<CharArray>,
    maxSteps: Int
): Map<Pair<Int, Int>, Int> {
    val result = mutableMapOf<Pair<Int, Int>, Int>()
    // Track visited states as position + hasPassedWall
    val visited = mutableSetOf<Triple<Int, Int, Boolean>>()
    val queue = ArrayDeque<Triple<Pair<Int, Int>, Int, Boolean>>() // position, steps, hasPassedWall
    queue.add(Triple(start, 0, false))
    
    while (queue.isNotEmpty()) {
        val (current, steps, hasPassedWall) = queue.removeFirst()
        val visitState = Triple(current.first, current.second, hasPassedWall)
        
        if (steps > maxSteps) continue
        if (visitState in visited) continue
        visited.add(visitState)
        
        // If we've passed through a wall and we're on a track, record this endpoint
        if (hasPassedWall && isTrack(current, maze) && steps > 0) {
            if (result[current] == null || steps < result[current]!!) {
                result[current] = steps
            }
        }
        
        // Try all directions
        val directions = listOf(Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0))
        for (dir in directions) {
            val next = Pair(current.first + dir.first, current.second + dir.second)
            if (!isInBounds(next, maze)) continue
            
            // Check if we're passing through a wall
            val newHasPassedWall = hasPassedWall || maze[next.first][next.second] == '#'
            queue.add(Triple(next, steps + 1, newHasPassedWall))
        }
    }
    
    return result
}

private fun findNormalPath(
    start: Pair<Int, Int>,
    end: Pair<Int, Int>,
    maze: List<CharArray>
): List<Pair<Int, Int>> {
    val visited = mutableSetOf<Pair<Int, Int>>()
    val queue = ArrayDeque<List<Pair<Int, Int>>>()
    val directions = listOf(Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0))

    queue.add(listOf(start))
    
    while (queue.isNotEmpty()) {
        val path = queue.removeFirst()
        val current = path.last()
        
        if (current == end) return path
        if (current in visited) continue
        
        visited.add(current)
        
        for (dir in directions) {
            val next = Pair(current.first + dir.first, current.second + dir.second)
            if (isValidMove(next, maze)) {
                queue.add(path + next)
            }
        }
    }
    return emptyList()
}

private fun findShortestPathLength(
    start: Pair<Int, Int>,
    end: Pair<Int, Int>,
    maze: List<CharArray>
): Int {
    val visited = mutableSetOf<Pair<Int, Int>>()
    val queue = ArrayDeque<Pair<Pair<Int, Int>, Int>>() // position, distance
    queue.add(Pair(start, 0))
    
    while (queue.isNotEmpty()) {
        val (pos, dist) = queue.removeFirst()
        
        if (pos == end) return dist
        if (pos in visited) continue
        visited.add(pos)
        
        val directions = listOf(Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0))
        for (dir in directions) {
            val next = Pair(pos.first + dir.first, pos.second + dir.second)
            if (isValidMove(next, maze)) {
                queue.add(Pair(next, dist + 1))
            }
        }
    }
    return Int.MAX_VALUE
}

private fun isValidMove(pos: Pair<Int, Int>, maze: List<CharArray>): Boolean {
    return pos.first >= 0 && pos.first < maze.size && 
           pos.second >= 0 && pos.second < maze[pos.first].size && 
           maze[pos.first][pos.second] != '#'
}

private fun isTrack(pos: Pair<Int, Int>, maze: List<CharArray>): Boolean {
    return pos.first >= 0 && pos.first < maze.size && 
           pos.second >= 0 && pos.second < maze[pos.first].size && 
           maze[pos.first][pos.second] != '#'
}

private fun isWallPassage(start: Pair<Int, Int>, mid: Pair<Int, Int>, end: Pair<Int, Int>, maze: List<CharArray>): Boolean {
    // Check if the points are in bounds
    if (!isInBounds(mid, maze)) return false
    if (!isInBounds(end, maze)) return false
    
    // At least one position (mid or both mid and end) must be a wall
    return maze[mid.first][mid.second] == '#' ||
           (maze[mid.first][mid.second] == '#' && maze[end.first][end.second] == '#')
}

private fun isInBounds(pos: Pair<Int, Int>, maze: List<CharArray>): Boolean {
    return pos.first >= 0 && pos.first < maze.size && 
           pos.second >= 0 && pos.second < maze[pos.first].size
}

