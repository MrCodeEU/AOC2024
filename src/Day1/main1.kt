package Day1

import java.io.File
import kotlin.math.abs

var realInput = true

fun main() {
    var fileName = "input.txt"
    if (!realInput) {
        fileName = "sample.txt"
    }
    val input = File("src/Day1/$fileName").readLines()
    val left = mutableListOf<Int>()
    val right = mutableListOf<Int>()
    input.forEach {
        left.add(it.split("   ")[0].toInt())
        right.add(it.split("   ")[1].toInt())
    }
    left.sort()
    right.sort()
    val distances = mutableListOf<Int>()
    left.forEachIndexed { index, s ->
        val distance = abs(right[index] - s)
        distances.add(distance)
    }
    var sum = distances.sum()
    println("Part 1:")
    println("Sum: $sum")
    val similarityScores = mutableListOf<Int>()
    left.forEach{ l ->
        similarityScores.add(right.filter { it == l }.size * l)
    }
    sum = similarityScores.sum()
    println("Part 2:")
    println("Sum: $sum")
}