package Day19

import java.io.File

val realInput = true

data class TowelInfo(val pattern: String, val length: Int)

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day19/$fileName").readLines()

    val towels = input[0].split(", ")
    val patterns = input.slice(2..<input.size)

    // Preprocess towels for faster lookup
    val towelsByFirstColor = towels.groupBy { it.first() }
    val sortedTowels = towels.map { TowelInfo(it, it.length) }
        .sortedByDescending { it.length }

    // Part 1
    var possibleCount = 0L
    patterns.forEach { pattern ->
        println(pattern)
        for(i in towels.indices) {
            if(checkNextTowl(i, mutableListOf(), towels, pattern)){
                possibleCount++
                break
            }
        }
    }

    println("Part 1:")
    println("Possible Combinations: $possibleCount")

    // Part 2 with new optimization
    possibleCount = 0L
    val cache = mutableMapOf<String, Long>()
    
    patterns.forEach { pattern ->
        println(pattern)
        val count = countCombinations(
            pattern,
            0,
            towels.toSet(),
            cache
        )
        possibleCount += count
        println("\tFound $count combinations")
    }
    
    println("Part 2:")
    println("Possible Combinations: $possibleCount")
}

// take Towel and check it if it works add to list that is returned
fun checkNextTowl(currentTowelId: Int, foundTowels: MutableList<String>, towels: List<String>, pattern: String): Boolean {
    var towelID = currentTowelId
    val newTowel = if(foundTowels.isEmpty()) towels[towelID] else foundTowels.reduce {acc, s -> acc+s } + towels[towelID]
    if(newTowel.length > pattern.length) return false
    //println("\t" + newTowel)
    foundTowels.add(towels[towelID])
    if(pattern == newTowel) {
        return true
    }
    if (pattern.startsWith(newTowel)){
        var found = false
        for (i in towels.indices){
            if (checkNextTowl(i, foundTowels.toMutableList(), towels, pattern)) return true
        }
    }
    return false
}

fun countCombinations(
    pattern: String,
    pos: Int,
    towels: Set<String>,
    cache: MutableMap<String, Long>
): Long {
    if (pos == pattern.length) return 1L
    
    // Create cache key from current state and remaining pattern
    val remainingPattern = pattern.substring(pos)
    val cacheKey = remainingPattern
    cache[cacheKey]?.let { return it }
    
    var count = 0L
    // Only check towels that start with the correct color and fit in remaining pattern
    val possibleTowels = towels.filter { 
        it[0] == pattern[pos] && pos + it.length <= pattern.length
    }
    
    for (towel in possibleTowels) {
        if (pattern.regionMatches(pos, towel, 0, towel.length)) {
            count += countCombinations(
                pattern,
                pos + towel.length,
                towels,
                cache
            )
        }
    }
    
    cache[cacheKey] = count
    return count
}