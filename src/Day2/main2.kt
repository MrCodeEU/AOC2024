package Day2

import java.io.File

var realInput = true

fun main() {
    var fileName = "input.txt"
    if (!realInput) {
        fileName = "sample.txt"
    }
    val input = File("src/Day2/$fileName").readLines()
    val safeList = mutableListOf<Boolean>()

    input.forEach { line ->
        val levels = line.split(" ").map { it.toInt() }
        if (isSequenceSafe(levels)) {
            safeList.add(true)
            return@forEach
        }

        // Try removing one number at a time
        for (i in levels.indices) {
            val newLevels = levels.toMutableList()
            newLevels.removeAt(i)
            if (isSequenceSafe(newLevels)) {
                safeList.add(true)
                return@forEach
            }
        }
        safeList.add(false)
    }

    val result = safeList.count { it }
    println("Part2: $result")
}

fun isSequenceSafe(levels: List<Int>): Boolean {
    if (levels.size < 2) return true
    val ascending = levels[0] < levels[1]

    for (i in 0 until levels.size - 1) {
        val diff = levels[i + 1] - levels[i]
        if (ascending && (diff <= 0 || diff > 3) || !ascending && (diff >= 0 || diff < -3)) {
            return false
        }
    }
    return true
}