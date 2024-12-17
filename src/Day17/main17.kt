package Day17

import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day17/$fileName").readLines()
    var registerA = input[0].split(" ")[2].toInt()
    var registerB = input[1].split(" ")[2].toInt()
    var registerC = input[2].split(" ")[2].toInt()
    val program = input[4].split(" ")[1].split(",").map { it.toInt() }

    // Part 1
    val output = runProgram(program, registerA, registerB, registerC)
    println("Part 1: ${output.joinToString(", ")}")

    // Part 2
    // run program from Register A = 0 until we get the output that's content is equal to the content of the program

    runBlocking {
        val found = AtomicBoolean(false)
        val result = AtomicInteger(0)
        val targetOutput = program.joinToString(",")
        
        // Use number of CPU cores for parallelization
        val numberOfJobs = Runtime.getRuntime().availableProcessors()*2-1
        val batchSize = 1000 // Smaller batch size for more frequent yields
        
        coroutineScope {
            for (jobId in 0 until numberOfJobs) {
                launch {
                    var a = jobId
                    while (!found.get()) { // Add upper bound of 10000
                        if (a % batchSize == 0) {
                            yield() // Allow other coroutines to run
                            if (a % 100_000 == 0) { // Print less frequently
                                println("Job $jobId trying A = $a")
                            }
                        }
                        
                        val output2 = runProgram(program, a, registerB, registerC)
                        if (output2.joinToString(",") == targetOutput) {
                            found.set(true)
                            result.set(a)
                            return@launch
                        }
                        a += numberOfJobs
                    }
                }
            }
        }
        
        println("Part 2: ${result.get()}")
    }

}

private fun runProgram(
    program: List<Int>,
    registerA: Int,
    registerB: Int,
    registerC: Int
): List<Int> {
    var registerA1 = registerA
    var registerB1 = registerB
    var registerC1 = registerC
    var instructionPointer = 0
    val output = mutableListOf<Int>()

    while (instructionPointer < program.size) {
        val literalOperand = program[instructionPointer + 1]  // operand is always at next position
        val comboOperand = when (program[instructionPointer + 1]) {
            0, 1, 2, 3 -> program[instructionPointer + 1]
            4 -> registerA1
            5 -> registerB1
            6 -> registerC1
            else -> {
                println("Invalid program!")
                -1
            }
        }
        when (program[instructionPointer]) {  // instruction is at current position
            0 -> { // adv performs division. Numerator reg A and denominator 2^comboOperand and store in reg A
                registerA1 /= (1 shl comboOperand)
                instructionPointer += 2
            }

            1 -> { // bxl calculates the bitwise XOR of reg B and the literal operand and store in reg B
                registerB1 = registerB1 xor literalOperand
                instructionPointer += 2
            }

            2 -> { // bst calculates combo operand modulo 8 and stores it in reg B
                registerB1 = comboOperand % 8
                instructionPointer += 2
            }

            3 -> { // jnz does nothing if reg A is zero. Else it jumps by setting the instruction pointer
                // to the value of the literal operand. Also, if we jump the instruction pointer is not incremented
                if (registerA1 != 0) {
                    instructionPointer = literalOperand
                } else {
                    instructionPointer += 2
                }
            }

            4 -> { // bxc calculates the bitwise XOR of reg B and reg C and stores it in reg B
                registerB1 = registerB1 xor registerC1
                instructionPointer += 2

            }

            5 -> { // out calculates the values of the combo operand modulo 8 and outputs it (multiple outputs are seperated by commas)
                output.add(comboOperand % 8)
                instructionPointer += 2

            }

            6 -> {// bdv works like adv (read reg A) but stores the result in reg B
                registerB1 = registerA1 / (1 shl comboOperand)
                instructionPointer += 2
            }

            7 -> { // cdv works like adv (read reg A) but stores the result in reg C
                registerC1 = registerA1 / (1 shl comboOperand)
                instructionPointer += 2

            }
        }
    }
    return output
}