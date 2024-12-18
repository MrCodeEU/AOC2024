package Day17

import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day17/$fileName").readLines()
    var registerA = input[0].split(" ")[2].toLong()
    var registerB = input[1].split(" ")[2].toLong()
    var registerC = input[2].split(" ")[2].toLong()
    val program = input[4].split(" ")[1].split(",").map { it.toLong() }

    // Part 1
    val output = runProgram(program, registerA, registerB, registerC)
    println("Part 1: ${output.joinToString(", ")}")

    // Part 2
    // run program from Register A = 0 until we get the output that's content is equal to the content of the program

    runBlocking {
        val found = AtomicBoolean(false)
        val result = AtomicLong(0)
        val targetOutput = program.joinToString(",")
        val numberOfJobs = (Runtime.getRuntime().availableProcessors()-1).toLong()
        val batchSize = 1_000_000_000_000_000L
        
        var currentBatchStart = 140_000_000_000_000_000L
        while (!found.get()) {
        println("Processing batch starting at ${"%,d".format(currentBatchStart)}")
            coroutineScope {
                val rangePerJob = batchSize / numberOfJobs
                for (jobId in 0L..<numberOfJobs) {
                    launch(Dispatchers.Default) {
                        val startA = currentBatchStart + (jobId * rangePerJob)
                        val endA = startA + rangePerJob
                        
                        for (a in startA..<endA) {
                            if (found.get()) return@launch
                            
                            val output2 = runProgram(program, a, registerB, registerC)
                            if (output2.joinToString(",") == targetOutput) {
                                found.set(true)
                                result.set(a)
                                return@launch
                            }
                        }
                    }
                }
            }
            
            if (!found.get()) {
                currentBatchStart += batchSize
            }
        }
        
        println("Part 2: ${result.get()}")
    }

}

private fun runProgram(
    program: List<Long>,
    registerA: Long,
    registerB: Long,
    registerC: Long
): List<Long> {
    var registerA1 = registerA
    var registerB1 = registerB
    var registerC1 = registerC
    var instructionPointer = 0
    val output = mutableListOf<Long>()

    while (instructionPointer < program.size) {
        val literalOperand = program[instructionPointer + 1]  // operand is always at next position
        val comboOperand = when (program[instructionPointer + 1]) {
            0L, 1L, 2L, 3L -> program[instructionPointer + 1]
            4L -> registerA1
            5L -> registerB1
            6L -> registerC1
            else -> {
                println("Invalid program!")
                -1
            }
        }
        when (program[instructionPointer]) {  // instruction is at current position
            0L -> { // adv performs division. Numerator reg A and denominator 2^comboOperand and store in reg A
                registerA1 /= (1 shl comboOperand.toInt())
                instructionPointer += 2
            }

            1L -> { // bxl calculates the bitwise XOR of reg B and the literal operand and store in reg B
                registerB1 = registerB1 xor literalOperand
                instructionPointer += 2
            }

            2L -> { // bst calculates combo operand modulo 8 and stores it in reg B
                registerB1 = comboOperand % 8
                instructionPointer += 2
            }

            3L -> { // jnz does nothing if reg A is zero. Else it jumps by setting the instruction pointer
                // to the value of the literal operand. Also, if we jump the instruction pointer is not incremented
                if (registerA1 != 0L) {
                    instructionPointer = literalOperand.toInt()
                } else {
                    instructionPointer += 2
                }
            }

            4L -> { // bxc calculates the bitwise XOR of reg B and reg C and stores it in reg B
                registerB1 = registerB1 xor registerC1
                instructionPointer += 2

            }

            5L -> { // out calculates the values of the combo operand modulo 8 and outputs it (multiple outputs are seperated by commas)
                output.add(comboOperand % 8)
                instructionPointer += 2

            }

            6L -> {// bdv works like adv (read reg A) but stores the result in reg B
                registerB1 = registerA1 / (1 shl comboOperand.toInt())
                instructionPointer += 2
            }

            7L -> { // cdv works like adv (read reg A) but stores the result in reg C
                registerC1 = registerA1 / (1 shl comboOperand.toInt())
                instructionPointer += 2

            }
        }
    }
    return output
}