package Day4

import java.io.File

var realInput = true

fun main() {
    var fileName = "input.txt"
    if (!realInput) {
        fileName = "sample.txt"
    }
    val input = File("src/Day4/$fileName").readLines()

    val grid = input.map { it.toCharArray() }.toTypedArray()

    var foundNumbers = 0
    grid.forEachIndexed { i, row ->
        row.forEachIndexed { j, c ->
            if (c == 'X'){
                // ---- Horizontal ----
                //check if we find XMAS in bounds in direction right
                if (j < (row.size-3) && row[j+1] == 'M' && row[j+2] == 'A' && row[j+3] == 'S'){
                    foundNumbers++
                }
                //check if we find XMAS in bounds in direction left
                if (j > 2 && row[j-1] == 'M' && row[j-2] == 'A' && row[j-3] == 'S'){
                    foundNumbers++
                }
                // ---- Vertical ----
                //check if we find XMAS in bounds in direction down
                if (i < (grid.size-3) && grid[i+1][j] == 'M' && grid[i+2][j] == 'A' && grid[i+3][j] == 'S'){
                    foundNumbers++
                }
                //check if we find XMAS in bounds in direction up
                if (i > 2 && grid[i-1][j] == 'M' && grid[i-2][j] == 'A' && grid[i-3][j] == 'S'){
                    foundNumbers++
                }
                // ---- Diagonal ----
                //check if we find XMAS in bounds in direction down right
                if(i < (grid.size-3) && j < (row.size-3) && grid[i+1][j+1] == 'M' && grid[i+2][j+2] == 'A' && grid[i+3][j+3] == 'S'){
                    foundNumbers++
                }
                //check if we find XMAS in bounds in direction down left
                if(i < (grid.size-3) && j > 2 && grid[i+1][j-1] == 'M' && grid[i+2][j-2] == 'A' && grid[i+3][j-3] == 'S'){
                    foundNumbers++
                }
                //check if we find XMAS in bounds in direction up right
                if(i > 2 && j < (row.size-3) && grid[i-1][j+1] == 'M' && grid[i-2][j+2] == 'A' && grid[i-3][j+3] == 'S'){
                    foundNumbers++
                }
                //check if we find XMAS in bounds in direction up left
                if(i > 2 && j > 2 && grid[i-1][j-1] == 'M' && grid[i-2][j-2] == 'A' && grid[i-3][j-3] == 'S'){
                    foundNumbers++
                }
            }
        }
    }
    println("Part 1:")
    println("Number of XMAS's: $foundNumbers")


    var foundMASs = 0

    grid.forEachIndexed { i, row ->
        row.forEachIndexed { j, c ->
            if (c == 'A' && i > 0 && j > 0 && i < grid.size-1 && j < row.size-1){ // bounds check 1 left, right, up, down
                // M top S bottom
                if(grid[i-1][j-1] == 'M' && grid[i-1][j+1] == 'M' && grid[i+1][j+1] == 'S' && grid[i+1][j-1] == 'S'){
                    foundMASs++
                }
                // M bottom S top
                if(grid[i-1][j-1] == 'S' && grid[i-1][j+1] == 'S' && grid[i+1][j+1] == 'M' && grid[i+1][j-1] == 'M'){
                    foundMASs++
                }
                // M left S right
                if(grid[i-1][j-1] == 'M' && grid[i-1][j+1] == 'S' && grid[i+1][j+1] == 'S' && grid[i+1][j-1] == 'M'){
                    foundMASs++
                }
                // M right S left
                if(grid[i-1][j-1] == 'S' && grid[i-1][j+1] == 'M' && grid[i+1][j+1] == 'M' && grid[i+1][j-1] == 'S'){
                    foundMASs++
                }
            }
        }
    }

    println("Part 2:")
    println("Number of XMAS's: $foundMASs")
}