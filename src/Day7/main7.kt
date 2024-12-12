package Day7

import java.io.File
import kotlin.math.pow

const val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day7/$fileName").readLines()
    val equations = mutableListOf<Pair<Long, List<Int>>>()  // Changed to list of pairs

    input.forEach { s ->
        val equation = s.split(":")
        val key = equation[0].trim()
        val value = equation[1].trim().split(" ").filter { it != "" }.map { it.toInt() }
        equations.add(Pair(key.toLong(), value))  // Add as pair to list
    }

    var part1Sum = 0L
    var part2Sum = 0L

    // Part 1
    part1Sum = calculateAllPossibleValues(equations, 2)

    println("Part 1:")
    println("Sum: $part1Sum")

    // Part 2
    part2Sum = calculateAllPossibleValues(equations, 3)

    println("Part 2:")
    println("Sum: $part2Sum")
}

fun intToBase3String(int: Int, numberOfBits: Int): String {
    return int.toString(3).padStart(numberOfBits, '0')
}

fun intToBinaryString(int: Int, numberOfBits: Int): String {
    return int.toString(2).padStart(numberOfBits, '0')
}

fun calculateAllPossibleValues(equations: MutableList<Pair<Long, List<Int>>>, numberOfDifferentOperators: Int): Long {
    var sum = 0L
    equations.forEach { (targetSum, values) ->
        val numberOfOperators = values.size - 1

        // Generate all possible operator combinations (0 = +, 1 = *)
        for (i in 0 until numberOfDifferentOperators.toFloat().pow(numberOfOperators).toInt()) {
            val operators = when (numberOfDifferentOperators) {
                2 -> intToBinaryString(i, numberOfOperators)
                3 -> intToBase3String(i, numberOfOperators)
                else -> throw IllegalArgumentException("Unsupported number of operators: $numberOfOperators")
            }
            var result = values[0].toLong()

            // Evaluate left-to-right
            for (j in 0 until numberOfOperators) {
                when (operators[j]) {
                    '0' -> result += values[j + 1]
                    '1' -> result *= values[j + 1]
                    '2' -> {
                        result = "${result}${values[j + 1]}".toLong()
                    }
                }
            }

            if (result == targetSum) {
                sum += targetSum
                // Debug output
                val debugStr = buildString {
                    append(values[0])
                    for (j in 0 until numberOfOperators) {
                        append(if (operators[j] == '0') " + " else if (operators[j] == '1') " * " else " || ")
                        append(values[j + 1])
                    }
                    append(" = ")
                    append(result)
                }
                println("Found valid equation: $debugStr")
                break
            }
        }
    }
    return sum
}