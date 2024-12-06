package Day5

import java.io.File

var realInput = true

fun main() {
    var fileName = "input.txt"
    if (!realInput) {
        fileName = "sample.txt"
    }
    val input = File("src/Day5/$fileName").readLines()
    val rules = mutableListOf<Pair<Int,Int>>()
    val updates = mutableListOf<List<Int>>()
    var readRules = true
    // read rules and updates
    for (line in input) {
        if (line.isEmpty()) {
            readRules = false
            continue
        }
        if (readRules) {
            rules.add(Pair(line.split("|")[0].toInt(), line.split("|")[1].toInt()))
        } else {
            updates.add(line.split(",").map { it.toInt() })
        }
    }

    val correctUpdates = mutableListOf<List<Int>>()
    val wrongUpdates = mutableListOf<MutableList<Int>>()
    // check update if rules are contained
    updates.forEach { u ->
        var correct = true
        correct = isCorrect(u, rules)
        if (correct){
            correctUpdates.add(u)
        } else{
            wrongUpdates.add(u.toMutableList())
        }
    }

    var sum = 0
    correctUpdates.forEach {
        // get the middle page number
        sum += it[it.size/2]
    }

    println("Part 1:")
    println("Sum: $sum")

    // Fix Page update order
    wrongUpdates.forEach { u ->
        while (!isCorrect(u, rules)) {
            rules.forEach { r ->
                if(u.contains(r.first) && u.contains(r.second)) {
                    // check again (maybe not to smart) what rules do not apply
                    if (u.indexOf(r.first) > u.indexOf(r.second)) {
                        // change update to comply with rule (swap them?)
                        val helper = u[u.indexOf(r.first)]
                        u[u.indexOf(r.first)] = u[u.indexOf(r.second)]
                        u[u.indexOf(r.second)] = helper
                    }
                }
            }
        }
    }
    sum = 0
    wrongUpdates.forEach {
        // get the middle page number
        sum += it[it.size/2]
    }

    println("Part 2:")
    println("Sum: $sum")
}

fun isCorrect(u: List<Int>, rules: MutableList<Pair<Int, Int>>): Boolean {
    rules.forEach{ r ->
        if(u.contains(r.first) && u.contains(r.second)) {
            // check if all rules apply correctly on update
            if (u.indexOf(r.first) > u.indexOf(r.second)) {
                return false
            }
        }
    }
    return true
}
