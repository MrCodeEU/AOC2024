package Day9

import java.io.File

val realInput = true

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day9/$fileName").readLines()
    val diskMap = input[0] // input is only one line

    val diskId =  mutableListOf<Int>()
    var idCounter = 0
    diskMap.toCharArray().forEachIndexed { index, c ->
        if (index % 2 == 0) // even indexes (id points)
        {
            for(i in 0..<(c.toString().toInt())) {
                diskId.add(idCounter)
            }
            idCounter++
        } else { // odd indexes (free points)
            for(i in 0..<(c.toString().toInt())) {
                diskId.add(-1)
            }
        }
    }
    val diskId2 = diskId.toMutableList()
    // take last element and add to first -1 elements until no -1 elements left
    while (diskId.contains(-1)) {
        val last = diskId.last()
        val first = diskId.indexOf(-1)
        diskId[first] = last
        diskId.removeLast()
    }
    println(diskId)

    // calculate checksum
    var checksum = 0L
    diskId.forEachIndexed { index, i ->
        checksum += index * i
    }

    println("Part 1:")
    println("Checksum: $checksum")

    // Part 2
    // check every "file" (all elements with same id) and check if free space (id = -1) from the front if large enough
    // if yes, move file to free space
    // if no, leave the file where it is
    for (i in idCounter-1 downTo 1) {
        val file = diskId2.filter { it == i }
        val filePosition = diskId2.indexOf(i)
        // for every block check if -1 if yes check every concurrent block if until no -1 that = free space
        // check if free space is large enough then move file to free space
        for ((index, c) in diskId2.withIndex()) {
            if(c == -1 && index < filePosition) {
                var freeSpace = 1
                while (diskId2[index + freeSpace] == -1) {
                    freeSpace++
                }
                if (freeSpace >= file.size) {
                    for (j in file.indices) {
                        // move file to free space
                        diskId2[index + j] = i
                        // remove file from old location
                        diskId2[filePosition + j] = -1
                    }
                    break
                }
            }
        }
    }
    println(diskId2)
    checksum = 0L
    diskId2.forEachIndexed { index, i ->
        if (i != -1) {
            checksum += index * i
        }
    }

    println("Part 2:")
    println("Checksum: $checksum")
}