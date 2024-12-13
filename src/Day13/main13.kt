package Day13

import java.io.File

val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day13/$fileName").readLines()
    val buttonAsXs = mutableListOf<Int>()
    val buttonBsXs = mutableListOf<Int>()
    val buttonAsYs = mutableListOf<Int>()
    val buttonBsYs = mutableListOf<Int>()
    val Xs = mutableListOf<Int>()
    val Ys = mutableListOf<Int>()
    var counter = 0
    input.forEachIndexed { _, s ->
        when (counter){
            0 -> {
                buttonAsXs.add(s.split(" ")[2].substring(2).replace(",", "").toInt())
                buttonAsYs.add(s.split(" ")[3].substring(2).toInt())
            }
            1 -> {
                buttonBsXs.add(s.split(" ")[2].substring(2).replace(",", "").toInt())
                buttonBsYs.add(s.split(" ")[3].substring(2).toInt())
            }
            2 -> {
                Xs.add(s.split(" ")[1].substring(2).replace(",", "").toInt())
                Ys.add(s.split(" ")[2].substring(2).toInt())
            }
            3 ->{} // empty line ignore
        }

        counter++
        if (counter == 4) counter = 0
    }
    val As = mutableListOf<Int>()
    val Bs = mutableListOf<Int>()
    for (i in 0..<Xs.size){
        val x = Xs[i]
        val y = Ys[i]
        val aX = buttonAsXs[i]
        val aY = buttonAsYs[i]
        val bX = buttonBsXs[i]
        val bY = buttonBsYs[i]

        // use formula to calculate a and b times Button presses
        val determinant = bY.toLong() * aX.toLong() - aY.toLong() * bX.toLong()
        if (determinant == 0L) continue  // no solution

        // Use long arithmetic to prevent overflow
        val numeratorA = bY.toLong() * x.toLong() - bX.toLong() * y.toLong()
        val a = numeratorA / determinant

        // Check if a is a clean integer
        if (numeratorA % determinant != 0L) continue

        val numeratorB = x.toLong() - aX.toLong() * a
        val b = numeratorB / bX.toLong()

        // Check if b is a clean integer
        if (numeratorB % bX != 0L) continue

        val aInt = a.toInt()
        val bInt = b.toInt()
        As.add(aInt)
        Bs.add(bInt)
    }
    // filter results where a or b is higher than 100
    val filteredResult = As.zip(Bs).filter { it.first < 100 && it.second < 100 }

    println("Part 1: ")
    println("Tokens: ${filteredResult.sumOf { it.first * 3 + it.second }}")
}