package Day21

import java.io.File
import java.util.PriorityQueue
import kotlinx.coroutines.*

val realInput = false
val runPart1 = false // Make part 1 skippable

fun main() = runBlocking {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day21/$fileName").readLines()

    val numericKeypad = arrayOf(
        arrayOf('7', '8', '9'),
        arrayOf('4', '5', '6'),
        arrayOf('1', '2', '3'),
        arrayOf(' ', '0', 'A')
    )
    val dirKeypad = arrayOf(
        arrayOf(' ', '^', 'A'),
        arrayOf('<', 'v', '>')
    )

    if (runPart1) {
        println("Processing Part 1...")
        val finalCommands = input.mapIndexed { index, code ->
            async { processPart1Code(code, index, input.size, numericKeypad, dirKeypad) }
        }.awaitAll()

        var sum = 0
        finalCommands.forEachIndexed { i, commands -> 
            println("${input[i]}: ${commands.length}")
            sum += commands.length * input[i].replace("A", "").toInt()
        }
        println("Part 1:")
        println("Complexity: $sum")
    }

    // Part 2
    println("\nProcessing Part 2...")
    val part2Results = input.mapIndexed { index, code ->
        async { processPart2Code(code, index, input.size) }
    }.awaitAll()
    
    val sumPart2 = part2Results.zip(input) { length, code ->
        length * code.replace("A", "").toInt()
    }.sum()
    
    println("Part 2:")
    println("Complexity: $sumPart2")
}

private suspend fun processPart1Code(
    targetCode: String,
    codeIndex: Int,
    totalCodes: Int,
    numericKeypad: Array<Array<Char>>,
    dirKeypad: Array<Array<Char>>
): String {
    println("Processing code ${codeIndex + 1}/$totalCodes: $targetCode")
    
    val robot1Paths = generateAllPaths(targetCode, numericKeypad, Pair(3, 2))
    println("  Found ${robot1Paths.size} paths for Robot1")
    
    var shortestTotal = Int.MAX_VALUE
    var shortestHumanCommand = ""
    var pathsProcessed = 0
    val totalPaths = robot1Paths.size
    
    robot1Paths.forEach { robot1Commands ->
        pathsProcessed++
        if (pathsProcessed % 100 == 0) {
            println("  Progress: $pathsProcessed/$totalPaths paths processed")
        }
        // For each Robot1 path, get all possible Robot2 paths
        val robot2Paths = generateAllPaths(robot1Commands, dirKeypad, Pair(0, 2))
        
        // For each Robot2 path, get all possible Human paths
        robot2Paths.forEach { robot2Commands ->
            val humanPaths = generateAllPaths(robot2Commands, dirKeypad, Pair(0, 2))
            
            // Find shortest human path
            humanPaths.forEach { humanCommands ->
                if (humanCommands.length < shortestTotal) {
                    shortestTotal = humanCommands.length
                    shortestHumanCommand = humanCommands
                }
            }
        }
    }
    
    return shortestHumanCommand
}

private suspend fun processPart2Code(
    targetCode: String,
    codeIndex: Int,
    totalCodes: Int
): Int {
    println("Processing code ${codeIndex + 1}/$totalCodes: $targetCode")
    
    val memo = mutableMapOf<String, Int>()
    val minLength = calculateMinChainLength(targetCode, 0, memo) // Start at 0 and work up to 25
    println("${targetCode}: $minLength (Cache size: ${memo.size})")
    return minLength
}

private fun calculateMinChainLength(
    targetCode: String,
    chainLength: Int,
    memo: MutableMap<String, Int>
): Int {
    val key = "$chainLength:$targetCode"
    memo[key]?.let { return it }

    if (chainLength == 25) {
        // Base case: numeric keypad (last in the chain)
        val length = calculateSequenceLength(targetCode, false) // Use numeric keypad
        memo[key] = length
        return length
    }

    // Get the current directional keypad length
    val currentLength = calculateSequenceLength(targetCode, true)
    
    // Get the next chain result (moving towards the numeric keypad)
    val nextLength = calculateMinChainLength(targetCode, chainLength + 1, memo)
    
    val result = currentLength + nextLength
    memo[key] = result
    return result
}

private fun calculateSequenceLength(
    sequence: String,
    isDirectional: Boolean
): Int {
    var totalLength = 0
    val keypad = if (isDirectional) {
        arrayOf(
            arrayOf(' ', '^', 'A'),
            arrayOf('<', 'v', '>')
        )
    } else {
        arrayOf(
            arrayOf('7', '8', '9'),
            arrayOf('4', '5', '6'),
            arrayOf('1', '2', '3'),
            arrayOf(' ', '0', 'A')
        )
    }
    
    var pos = if (isDirectional) {
        Pair(0, 2)  // 'A' position in directional keypad
    } else {
        Pair(3, 2)  // 'A' position in numeric keypad
    }

    sequence.forEach { target ->
        val pathLength = findPathLength(pos, target, keypad)
        totalLength += pathLength + 1  // +1 for the 'A' press
        pos = findButtonPosition(target, keypad)
    }
    
    return totalLength
}

private fun findPathLength(
    from: Pair<Int, Int>,
    to: Char,
    keypad: Array<Array<Char>>
): Int {
    if (keypad[from.first][from.second] == to) return 0

    val distances = Array(keypad.size) { Array(keypad[0].size) { Int.MAX_VALUE } }
    val queue = ArrayDeque<Pair<Int, Int>>()
    
    distances[from.first][from.second] = 0
    queue.add(from)
    
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        
        for ((dx, dy) in listOf(Pair(-1, 0), Pair(1, 0), Pair(0, -1), Pair(0, 1))) {
            val next = Pair(current.first + dx, current.second + dy)
            if (isValidMove(next, keypad)) {
                val newDist = distances[current.first][current.second] + 1
                if (newDist < distances[next.first][next.second]) {
                    distances[next.first][next.second] = newDist
                    queue.add(next)
                    
                    if (keypad[next.first][next.second] == to) {
                        return newDist
                    }
                }
            }
        }
    }
    
    throw IllegalStateException("No path found from $from to $to")
}

private fun findMinimumLengthForChain(
    targetCode: String,
    numericKeypad: Array<Array<Char>>,
    dirKeypad: Array<Array<Char>>,
    chainLength: Int
): Int {
    // Map to store minimum lengths for each stage and target
    val memo = mutableMapOf<String, Int>()
    
    fun processStage(remainingChain: Int, currentTarget: String): Int {
        if (currentTarget.isEmpty()) return 0
        
        val key = "$remainingChain:$currentTarget"
        memo[key]?.let { return it }

        if (remainingChain == 0) {
            // Final numeric keypad stage
            return findMinimumLength(currentTarget, numericKeypad, Pair(3, 2))
        }

        // For directional keypad
        val minLength = findMinimumLength(currentTarget, dirKeypad, Pair(0, 2))
        val result = minLength + processStage(remainingChain - 1, currentTarget)
        
        memo[key] = result
        return result
    }
    
    return processStage(chainLength, targetCode)
}

private fun findMinimumLength(
    targetSequence: String,
    keypad: Array<Array<Char>>,
    startPos: Pair<Int, Int>
): Int {
    var totalLength = 0
    var currentPos = startPos
    
    targetSequence.forEach { target ->
        val length = findShortestLength(currentPos, target, keypad)
        totalLength += length + 1  // +1 for the 'A' press
        currentPos = findButtonPosition(target, keypad)
    }
    
    return totalLength
}

private fun findShortestLength(
    from: Pair<Int, Int>,
    to: Char,
    keypad: Array<Array<Char>>
): Int {
    if (keypad[from.first][from.second] == to) return 0

    val distances = Array(keypad.size) { Array(keypad[0].size) { Int.MAX_VALUE } }
    val queue = ArrayDeque<Pair<Int, Int>>()
    
    distances[from.first][from.second] = 0
    queue.add(from)
    
    while (queue.isNotEmpty()) {
        val pos = queue.removeFirst()
        
        if (keypad[pos.first][pos.second] == to) {
            return distances[pos.first][pos.second]
        }
        
        for ((dx, dy) in listOf(
            Pair(-1, 0), Pair(1, 0), 
            Pair(0, -1), Pair(0, 1)
        )) {
            val newPos = Pair(pos.first + dx, pos.second + dy)
            if (isValidMove(newPos, keypad)) {
                val newDist = distances[pos.first][pos.second] + 1
                if (newDist < distances[newPos.first][newPos.second]) {
                    distances[newPos.first][newPos.second] = newDist
                    queue.add(newPos)
                }
            }
        }
    }
    
    throw IllegalStateException("No path found")
}

private fun processDirectionalChain(
    targetCommands: String,
    remainingKeyPads: Int,
    cacheMemo: MutableMap<String, Int>
): Int {
    // Base case: last keypad (human input)
    if (remainingKeyPads == 0) {
        return targetCommands.length
    }

    // Check cache
    val cacheKey = "$remainingKeyPads:$targetCommands"
    cacheMemo[cacheKey]?.let { return it }

    // Get shortest path for current keypad
    val dirKeypad = arrayOf(
        arrayOf(' ', '^', 'A'),
        arrayOf('<', 'v', '>')
    )
    
    val paths = generateAllPaths(targetCommands, dirKeypad, Pair(0, 2))
    var shortestLength = Int.MAX_VALUE

    paths.forEach { path ->
        val length = processDirectionalChain(path, remainingKeyPads - 1, cacheMemo)
        if (length < shortestLength) {
            shortestLength = length
        }
    }

    // Cache result
    cacheMemo[cacheKey] = shortestLength
    return shortestLength
}

private fun findAllShortestPaths(from: Pair<Int, Int>, to: Char, keypad: Array<Array<Char>>): Set<String> {
    if (keypad[from.first][from.second] == to) return setOf("")

    val paths = mutableSetOf<String>()
    var shortestLength = Int.MAX_VALUE
    
    // Queue contains position, path pairs
    val queue = ArrayDeque<Pair<Pair<Int, Int>, String>>()
    queue.add(Pair(from, ""))
    
    val visited = mutableMapOf<Pair<Int, Int>, Int>() // position to shortest length
    visited[from] = 0

    while (queue.isNotEmpty()) {
        val (pos, path) = queue.removeFirst()
        
        // Skip if we already found shorter paths
        if (path.length > shortestLength) continue
        
        if (keypad[pos.first][pos.second] == to) {
            if (path.length <= shortestLength) {
                if (path.length < shortestLength) {
                    shortestLength = path.length
                    paths.clear()
                }
                paths.add(path)
            }
            continue
        }

        for ((dx, dy, dir) in listOf(
            Triple(-1, 0, "^"),
            Triple(1, 0, "v"),
            Triple(0, -1, "<"),
            Triple(0, 1, ">")
        )) {
            val newPos = Pair(pos.first + dx, pos.second + dy)
            val newLength = path.length + 1
            if (isValidMove(newPos, keypad) && 
                (!visited.containsKey(newPos) || visited[newPos]!! >= newLength)) {
                visited[newPos] = newLength
                queue.add(Pair(newPos, path + dir))
            }
        }
    }
    return paths
}

private fun generateAllPaths(targetCode: String, keypad: Array<Array<Char>>, startPos: Pair<Int, Int>): Set<String> {
    var currentPaths = setOf("")
    var pos = startPos

    targetCode.forEach { target ->
        val newPaths = mutableSetOf<String>()
        currentPaths.forEach { currentPath ->
            val paths = findAllShortestPaths(pos, target, keypad)
            paths.forEach { path ->
                newPaths.add(currentPath + path + "A")
            }
        }
        pos = findButtonPosition(target, keypad)
        currentPaths = newPaths
    }
    return currentPaths
}

private fun getCommandsForTarget(targetSequence: String, keypad: Array<Array<Char>>): String {
    var pos = if (keypad[0].size == 3) Pair(0, 2) else Pair(3, 2) // Start at A
    return buildString {
        targetSequence.forEach { target ->
            val path = findShortestPath(pos, target, keypad)
            append(path)
            append('A')
            pos = findButtonPosition(target, keypad)
        }
    }
}

private fun findShortestPath(from: Pair<Int, Int>, to: Char, keypad: Array<Array<Char>>): String {
    if (keypad[from.first][from.second] == to) return ""

    // Use priority queue sorted by path length to ensure shortest path
    val queue = PriorityQueue<Pair<Pair<Int, Int>, String>>(compareBy { it.second.length })
    queue.add(Pair(from, ""))

    val visited = mutableSetOf<Pair<Int, Int>>()
    visited.add(from)

    var shortestFound: String? = null
    
    while (queue.isNotEmpty()) {
        val (pos, path) = queue.poll()
        
        // If we found a path and current path is longer, we can stop
        if (shortestFound != null && path.length >= shortestFound.length) continue
        
        if (keypad[pos.first][pos.second] == to) {
            if (shortestFound == null || path.length < shortestFound.length) {
                shortestFound = path
            }
            continue
        }

        for ((dx, dy, dir) in listOf(
            Triple(-1, 0, "^"),
            Triple(1, 0, "v"),
            Triple(0, -1, "<"),
            Triple(0, 1, ">")
        )) {
            val newPos = Pair(pos.first + dx, pos.second + dy)
            if (isValidMove(newPos, keypad) && newPos !in visited) {
                visited.add(newPos)
                queue.add(Pair(newPos, path + dir))
            }
        }
    }

    return shortestFound ?: throw IllegalStateException("No path found from ${keypad[from.first][from.second]} to $to")
}

private fun isValidMove(pos: Pair<Int, Int>, keypad: Array<Array<Char>>): Boolean {
    return pos.first in keypad.indices && 
           pos.second in keypad[0].indices && 
           keypad[pos.first][pos.second] != ' '
}

private fun findButtonPosition(button: Char, keypad: Array<Array<Char>>): Pair<Int, Int> {
    keypad.forEachIndexed { i, row ->
        row.forEachIndexed { j, c ->
            if (c == button) return Pair(i, j)
        }
    }
    throw IllegalStateException("Button $button not found")
}
