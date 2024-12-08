package Day8

import java.io.File

const val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day8/$fileName").readLines()
    val grid = input.map { it.toCharArray() }.toTypedArray()

    // Part 1
    // 1. Get all Antennas
    val antennas = mutableListOf<Antenna>()
    grid.forEachIndexed { i, row ->
        row.forEachIndexed { j, cell ->
            if (cell != '.') {
                println("Antenna with frequency $cell at: $i, $j")
                antennas.add(Antenna(cell, i, j))
            }
        }
    }
    // 2. Get all Pair of Antennas with the same frequency
    val antennaPairs = mutableListOf<Pair<Antenna, Antenna>>()
    antennas.forEachIndexed { i, antenna1 ->
        antennas.forEachIndexed { j, antenna2 ->
            if (i != j && antenna1.frequency == antenna2.frequency) {
                antennaPairs.add(Pair(antenna1, antenna2))
            }
        }
    }

    // 2.1 Remove duplicate pairs (antenna1, antenna2) and (antenna2, antenna1)
    val uniqueAntennaPairs = mutableListOf<Pair<Antenna, Antenna>>()
    antennaPairs.forEach { (antenna1, antenna2) ->
        if (!uniqueAntennaPairs.contains(Pair(antenna2, antenna1))) {
            uniqueAntennaPairs.add(Pair(antenna1, antenna2))
        }
    }

    // DEBUG: Print all antenna pairs
    uniqueAntennaPairs.forEach { (antenna1, antenna2) ->
        println("Antenna pair with frequency ${antenna1.frequency} at: ${antenna1.x}, ${antenna1.y} and ${antenna2.x}, ${antenna2.y}")
    }
    // 4. Calculate the two spots where each spot is in line with the pair of antennas and
    // twice the distance from one of the antennas and once the distance from the other antenna
    // make sure the spot is within the grid else ignore it
    val spots = mutableSetOf<Pair<Int, Int>>()
    uniqueAntennaPairs.forEach { (antenna1, antenna2) ->
        val direction = antenna1.directionTo(antenna2)
        val spot1 = Pair(antenna1.x + 2 * direction.first, antenna1.y + 2 * direction.second)
        val spot2 = Pair(antenna2.x + 2 * -direction.first, antenna2.y + 2 * -direction.second)
        if (spot1.first in 0 until grid.size && spot1.second in 0 until grid[0].size) {
            spots.add(spot1)
        }
        if (spot2.first in 0 until grid.size && spot2.second in 0 until grid[0].size) {
            spots.add(spot2)
        }
    }

    // DEBUG: Print all spots and Antennas in the grid
    grid.forEachIndexed { i, row ->
        row.forEachIndexed { j, cell ->
            if (spots.contains(Pair(i, j))) {
                print("#")
            } else {
                print(cell)
            }
        }
        println()
    }

    println("Part 1:")
    println("Number of spots: ${spots.size}")

    // Part 2
    // Same as before but instead of just 2*distance we need to use all multiples of the distance that are within the grid
    val spots2 = mutableSetOf<Pair<Int, Int>>()
    uniqueAntennaPairs.forEach { (antenna1, antenna2) ->
        val direction = antenna1.directionTo(antenna2)
        val maxDistance = maxOf(grid.size, grid[0].size)
        for (i in 1 until maxDistance) {
            val spot1 = Pair(antenna1.x + i * direction.first, antenna1.y + i * direction.second)
            val spot2 = Pair(antenna2.x + i * -direction.first, antenna2.y + i * -direction.second)
            if (spot1.first in 0 until grid.size && spot1.second in 0 until grid[0].size) {
                spots2.add(spot1)
            }
            if (spot2.first in 0 until grid.size && spot2.second in 0 until grid[0].size) {
                spots2.add(spot2)
            }
        }
    }

    // DEBUG: Print all spots and Antennas in the grid
    grid.forEachIndexed { i, row ->
        row.forEachIndexed { j, cell ->
            if (spots2.contains(Pair(i, j))) {
                print("#")
            } else {
                print(cell)
            }
        }
        println()
    }

    println("Part 2:")
    println("Number of spots: ${spots2.size}")

}

class Antenna(val frequency: Char, val x: Int, val y: Int) {
    fun directionTo(other: Antenna): Pair<Int, Int> {
        val dx = other.x - x
        val dy = other.y - y
        return Pair(dx, dy)
    }
}