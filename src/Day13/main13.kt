package Day13

import java.io.File

val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day13/$fileName").readLines()
    var buttonAsXs = mutableListOf<Int>()
    var buttonBsXs = mutableListOf<Int>()
    var buttonAsYs = mutableListOf<Int>()
    var buttonBsYs = mutableListOf<Int>()
    var Xs = mutableListOf<Int>()
    var Ys = mutableListOf<Int>()
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
    var filteredResult = As.zip(Bs).filter { it.first < 100 && it.second < 100 }

    println("Part 1: ")
    println("Tokens: ${filteredResult.sumOf { it.first * 3 + it.second }}")

    var buttonAsXsLong = mutableListOf<Long>()
    var buttonBsXsLong = mutableListOf<Long>()
    var buttonAsYsLong = mutableListOf<Long>()
    var buttonBsYsLong = mutableListOf<Long>()
    var XsLong = mutableListOf<Long>()
    var YsLong = mutableListOf<Long>()
    counter = 0
    input.forEachIndexed { _, s ->
        when (counter){
            0 -> {
                buttonAsXsLong.add(s.split(" ")[2].substring(2).replace(",", "").toLong())
                buttonAsYsLong.add(s.split(" ")[3].substring(2).toLong())
            }
            1 -> {
                buttonBsXsLong.add(s.split(" ")[2].substring(2).replace(",", "").toLong())
                buttonBsYsLong.add(s.split(" ")[3].substring(2).toLong())
            }
            2 -> {
                XsLong.add(s.split(" ")[1].substring(2).replace(",", "").toLong()+10000000000000)
                YsLong.add(s.split(" ")[2].substring(2).toLong()+10000000000000)
            }
            3 ->{} // empty line ignore
        }

        counter++
        if (counter == 4) counter = 0
    }
    val AsLong = mutableListOf<Long>()
    val BsLong = mutableListOf<Long>()
    for (i in 0..<Xs.size){
        val x = XsLong[i]
        val y = YsLong[i]
        val aX = buttonAsXsLong[i]
        val aY = buttonAsYsLong[i]
        val bX = buttonBsXsLong[i]
        val bY = buttonBsYsLong[i]

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

        AsLong.add(a)
        BsLong.add(b)
    }
    // filter results where a or b is higher than 100
    //val filteredResultLong = AsLong.zip(BsLong).filter { it.first < 100 && it.second < 100 }
    val filteredResultLong = AsLong.zip(BsLong)
    println("Part 2: ")
    println("Tokens: ${filteredResultLong.sumOf { it.first * 3 + it.second }}")
}