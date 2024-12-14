package Day10

import java.io.File

val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day10/$fileName").readLines()
    val topography = input.map { s -> s.toCharArray().map { it.toString().toInt() } }
    val trailHeads = topography.mapIndexed {
        y, points -> points.mapIndexed { x, value -> if (value == 0) Pair(y, x) else null }
    }.flatten().filterNotNull()
    val trailHeadsMap = trailHeads.mapIndexed { _, pair -> pair to Pair(mutableSetOf<Pair<Int, Int>>(), 0) }.toMap().toMutableMap()
    // from every trailhead walk up, down ,left, right if typography only increases by 1 until we reach 9 than increase the trailhead
    trailHeadsMap.forEach{
        walk(it.key, it.key, topography, trailHeadsMap)
    }

    trailHeadsMap.forEach{
        println(it)
    }

    println("Part 1:")
    println("Sum of trailheads: ${trailHeadsMap.values.sumOf { it.first.size }}")

    println("Part 2:")
    println("Trailhead with most trailheads: ${trailHeadsMap.values.sumOf { it.second }}")

}

fun walk(start: Pair<Int, Int>, currentPosition: Pair<Int, Int>, typography:  List<List<Int>>, trailHeadsMap: MutableMap<Pair<Int, Int>, Pair<MutableSet<Pair<Int, Int>>, Int>>){
    val x = currentPosition.first
    val y = currentPosition.second
    val currentHeight = typography[x][y]
    if (currentHeight == 9){
        trailHeadsMap[start]?.first?.add(Pair(x, y))
        trailHeadsMap[start] = Pair(trailHeadsMap[start]!!.first, trailHeadsMap[start]!!.second + 1)
    }
    if (x > 0 && currentHeight == typography[x-1][y]-1){
        walk(start, Pair(x - 1, y), typography, trailHeadsMap)
    }
    if (x < typography.size -1 && currentHeight == typography[x+1][y]-1){
        walk(start, Pair(x + 1, y), typography, trailHeadsMap)
    }
    if (y > 0 && currentHeight == typography[x][y-1]-1){
        walk(start, Pair(x, y - 1), typography, trailHeadsMap)
    }
    if (y < typography[0].size -1 && currentHeight == typography[x][y+1]-1){
        walk(start, Pair(x, y + 1), typography, trailHeadsMap)
    }
}