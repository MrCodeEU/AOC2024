package Day22

import java.io.File
import kotlinx.coroutines.*

val realInput = true
const val SEQUENCE_LENGTH = 2000 // Make sequence length configurable

fun main() = runBlocking {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day22/$fileName").readLines()
    val secretNumbers = input.map { it.toLong() }.toLongArray()
    val numbersBuyer = Array(secretNumbers.size){ Array(SEQUENCE_LENGTH + 1){ 0L } }
    
    for(i in 0..<SEQUENCE_LENGTH){
        println("Iteration $i/$SEQUENCE_LENGTH")
        for (j in secretNumbers.indices) {
            if (i == 0) {
                numbersBuyer[j][0] = secretNumbers[j]
            }
            var sectretNumber = secretNumbers[j]
            // 1. multiply by 64 and then mix  and prune
            var number = sectretNumber * 64
            sectretNumber = mix(sectretNumber, number)
            sectretNumber = prune(sectretNumber)
            // 2. divide secret number by 32 round down and then mix and prune
            number = sectretNumber / 32
            sectretNumber = mix(sectretNumber, number)
            sectretNumber = prune(sectretNumber)
            // 3. multiply by 2048 and then mix and prune
            number = sectretNumber * 2048
            sectretNumber = mix(sectretNumber, number)
            sectretNumber = prune(sectretNumber)
            // finally save the secret number
            secretNumbers[j] = sectretNumber
            numbersBuyer[j][i+1] = sectretNumber
        }
    }
    
    println("Part 1:")
    println("Sum of secret numbers: ${secretNumbers.sum()}")
    
    // Part 2
    println("Calculating price changes...")
    val startTime = System.currentTimeMillis()
    val prices = numbersBuyer.map { longs -> longs.map { it.toString().last().digitToInt() }.toIntArray() }
    
    // Pre-calculate all price changes as Int arrays
    val priceChanges = prices.map { buyer ->
        IntArray(buyer.size - 1) { i -> buyer[i + 1] - buyer[i] }
    }
    
    // Pre-generate all possible patterns
    val patternRange = -9..9
    val allPatterns = mutableListOf<IntArray>()
    for (a in patternRange) {
        for (b in patternRange) {
            for (c in patternRange) {
                for (d in patternRange) {
                    allPatterns.add(intArrayOf(a, b, c, d))
                }
            }
        }
    }
    
    val totalCombinations = allPatterns.size.toLong()
    var processedCombinations = 0L
    var lastUpdateTime = startTime
    var lastProcessedCombinations = 0L
    
    println("Searching through $totalCombinations patterns using ${Runtime.getRuntime().availableProcessors()} cores...")
    
    // Split patterns into chunks for parallel processing
    val chunkSize = 1000
    val results = allPatterns.chunked(chunkSize).map { patterns ->
        async {
            patterns.map { pattern ->
                var totalProfit = 0L
                
                for ((buyerIdx, buyerChanges) in priceChanges.withIndex()) {
                    // Direct array comparison
                    outer@ for (i in 0..buyerChanges.size-4) {
                        var matches = true
                        for (j in 0..3) {
                            if (buyerChanges[i+j] != pattern[j]) {
                                matches = false
                                break
                            }
                        }
                        if (matches) {
                            totalProfit += prices[buyerIdx][i+4]
                            break@outer
                        }
                    }
                }
                
                synchronized(this) {
                    processedCombinations += 1
                    if (processedCombinations % 10000 == 0L) {
                        val currentTime = System.currentTimeMillis()
                        val elapsed = (currentTime - startTime) / 1000.0
                        val progress = (processedCombinations.toDouble() / totalCombinations * 100)
                        val timeSinceLastUpdate = (currentTime - lastUpdateTime) / 1000.0
                        val combinationsSinceLastUpdate = processedCombinations - lastProcessedCombinations
                        val speed = combinationsSinceLastUpdate / timeSinceLastUpdate
                        val remainingCombinations = totalCombinations - processedCombinations
                        val etaSeconds = remainingCombinations / speed
                        val etaMinutes = (etaSeconds / 60).toInt()
                        
                        println("Progress: ${String.format("%.2f", progress)}% " +
                              "(${processedCombinations}/$totalCombinations) - " +
                              "${String.format("%.1f", elapsed)}s elapsed - " +
                              "Speed: ${String.format("%.0f", speed)}/s - " +
                              "ETA: ${etaMinutes / 60}h ${etaMinutes % 60}m")
                        
                        lastUpdateTime = currentTime
                        lastProcessedCombinations = processedCombinations
                    }
                }
                
                pattern to totalProfit
            }
        }
    }.awaitAll().flatten()
    
    val bestResult = results.maxByOrNull { it.second }!!
    val bestPattern = bestResult.first
    val maxProfit = bestResult.second
    
    val totalTime = (System.currentTimeMillis() - startTime) / 1000.0
    
    println("\nPart 2:")
    println("Best pattern: ${bestPattern.joinToString(",")}")
    println("Maximum bananas: $maxProfit")
    println("Total time: ${String.format("%.2f", totalTime)}s")
}

fun mix (secretNUmber: Long, number: Long): Long {
    return secretNUmber xor number
}

fun prune (secretNumber: Long): Long {
    return secretNumber % 16777216
}