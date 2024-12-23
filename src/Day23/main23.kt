package Day23

import java.io.File
import kotlinx.coroutines.*

val realInput = true

fun main() = runBlocking {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day23/$fileName").readLines()
    
    // Create adjacency map
    val connections = mutableMapOf<String, MutableSet<String>>()
    input.forEach { line ->
        val (a, b) = line.split("-")
        connections.getOrPut(a) { mutableSetOf() }.add(b)
        connections.getOrPut(b) { mutableSetOf() }.add(a)
    }

    // Find all sets of three interconnected computers
    val triplets = mutableSetOf<Set<String>>()
    for (comp1 in connections.keys) {
        val neighbors = connections[comp1] ?: continue
        for (comp2 in neighbors) {
            val comp2Neighbors = connections[comp2] ?: continue
            for (comp3 in comp2Neighbors) {
                if (comp3 != comp1 && 
                    connections[comp3]?.contains(comp1) == true) {
                    triplets.add(setOf(comp1, comp2, comp3))
                }
            }
        }
    }

    // Filter for sets containing computers starting with 't'
    val tSets = triplets.count { set ->
        set.any { it.startsWith('t') }
    }

    println("Part 1:")
    println("Total triplets: ${triplets.size}")
    println("Triplets with 't': $tSets")

    // Part 2: Find the largest clique using Bron-Kerbosch algorithm
    var maxCliqueSize = 0
    var largestClique = setOf<String>()

    fun findMaximalClique(
        potential: Set<String>,
        candidates: Set<String>,
        excluded: Set<String>,
        connections: Map<String, Set<String>>
    ) {
        if (candidates.isEmpty() && excluded.isEmpty()) {
            if (potential.size > maxCliqueSize) {
                maxCliqueSize = potential.size
                largestClique = potential
                println("New largest clique found! Size: $maxCliqueSize")
            }
            return
        }

        if (candidates.isEmpty()) return

        // Choose pivot
        val pivot = candidates.maxByOrNull { connections[it]!!.size } ?: return
        val pivotConnections = connections[pivot]!!

        val verticesToProcess = candidates - pivotConnections
        for (vertex in verticesToProcess) {
            val vertexConnections = connections[vertex]!!
            findMaximalClique(
                potential + vertex,
                candidates intersect vertexConnections - setOf(vertex),
                excluded intersect vertexConnections,
                connections
            )
            findMaximalClique(
                potential,
                candidates - setOf(vertex),
                excluded + vertex,
                connections
            )
            return  // Only need first iteration due to algorithm's nature
        }
    }

    println("Starting clique search...")
    findMaximalClique(
        potential = emptySet(),
        candidates = connections.keys,
        excluded = emptySet(),
        connections = connections
    )

    val password = largestClique.sorted().joinToString(",")
    
    println("\nPart 2:")
    println("Largest clique size: ${largestClique.size}")
    println("Password: $password")
}