package Day7

import java.io.File
import kotlin.math.pow

const val realInput = false

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

    equations.forEach { (targetSum, values) ->
        val numberOfOperators = values.size - 1

        // Generate all possible operator combinations (0 = +, 1 = *)
        for (i in 0 until 2.0.pow(numberOfOperators).toInt()) {
            val operators = intToBinaryString(i, numberOfOperators)
            var result = values[0].toLong()

            // Evaluate left-to-right
            for (j in 0 until numberOfOperators) {
                when (operators[j]) {
                    '0' -> result += values[j + 1]
                    '1' -> result *= values[j + 1]
                }
            }

            if (result == targetSum) {
                part1Sum += targetSum
                // Debug output
                val debugStr = buildString {
                    append(values[0])
                    for (j in 0 until numberOfOperators) {
                        append(if (operators[j] == '0') " + " else " * ")
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

    println("Part 1:")
    println("Sum: $part1Sum")

    // Part 2
    var part2Sum = 0L

    equations.forEach { (targetSum, values) ->
        val numberOfOperators = values.size - 1

        // Generate all possible operator combinations (0 = +, 1 = *)
        for (i in 0 until 3.0.pow(numberOfOperators).toInt()) {
            val operators = intToBase3String(i, numberOfOperators)
            var result = values[0].toLong()

            // first generate a new list of values with the concatenation where operand is 2 (concat meaning string concatenation)
            val newValues = mutableListOf<Int>()
            val newOperators = operators.toCharArray().filter { it != '2' }.toMutableList()

            for (j in 0 until values.size) {
                if (j < numberOfOperators && operators[j] == '2') {
                    newValues.add("${values[j]}${values[j + 1]}".toInt())
                } else {
                    newValues.add(values[j])
                }
            }

                    // Evaluate left-to-right with new values and operators
            for (j in 0 until newOperators.size-1) {
                when (newOperators[j]) {
                    '0' -> result += newValues[j + 1]
                    '1' -> result *= newValues[j + 1]
                }
            }

            if (result == targetSum) {
                part2Sum += targetSum
                // Debug output
                val debugStr = buildString {
                    append(values[0])
                    for (j in 0 until numberOfOperators) {
                        append(if (operators[j] == '0') " + " else " * ")
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

    println("Part 2:")
    println("Sum: $part2Sum")
}

fun intToBase3String(int: Int, numberOfBits: Int): String {
    return int.toString(3).padStart(numberOfBits, '0')
}

fun intToBinaryString(int: Int, numberOfBits: Int): String {
    return int.toString(2).padStart(numberOfBits, '0')
}