package Day11

import java.io.File
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

val realInput = true

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day11/$fileName").readLines()

    // Process both parts
    processWithCache(input[0], 25, "Part 1")
    processWithCache(input[0], 75, "Part 2")
}


// cache teh results of blicking patterns instead of saving all the results
// with this look up table we can save on a ton of calculations as patterns tend to repeat a lot
// as we often start in a small number like 1.
fun processWithCache(input: String, iterations: Int, partName: String) {
    val initialStones = input.split(" ").map { it.toLong() }
    val numberOfThreads = initialStones.size
    val cache = mutableMapOf<String, Long>()
    var totalStones = 0L
    val time = measureTimeMillis {
        runBlocking {
            val dispatcher = Dispatchers.Default.limitedParallelism(numberOfThreads)

            val deferredResults = initialStones.mapIndexed { index, stone ->
                async(dispatcher) {
                    println("Processing stone ${index + 1}/${initialStones.size}")
                    getStoneSizeAfterBlinks(stone, iterations, cache)
                }
            }
            totalStones = deferredResults.awaitAll().sum()
        }
    }
    println("$partName:")
    println("Total processing time: ${time}ms")
    println("Number of Stones: $totalStones")
}

fun blink(stone: Long): List<Long> {
    val result = mutableListOf<Long>()
    val stoneStr = stone.toString()

    when {
        stone == 0L -> result.add(1)
        stoneStr.length % 2 == 0 -> {
            val mid = stoneStr.length / 2
            result.add(stoneStr.substring(0, mid).toLong())
            result.add(stoneStr.substring(mid).toLong())
        }
        else -> result.add(stone * 2024)
    }

    return result
}

fun getStoneSizeAfterBlinks(stone: Long, blinks: Int, cache: MutableMap<String, Long>): Long {
    if (blinks <= 0) return 1

    val key = "$stone:$blinks"
    cache[key]?.let { return it }

    val result = blink(stone)
        .sumOf { nextStone -> getStoneSizeAfterBlinks(nextStone, blinks - 1, cache) }

    cache[key] = result
    return result
}