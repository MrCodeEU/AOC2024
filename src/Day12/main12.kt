package Day12

import java.io.File

val realInput = false   

data class Point(val x: Int, val y: Int)
data class Shape(val points: Set<Point>) {
    fun findCorners(): List<Point> {
        val corners = mutableListOf<Point>()
        for (point in points) {
            var neighborCount = 0
            var diagonalCount = 0
            // Check all 8 surrounding positions
            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (dx == 0 && dy == 0) continue
                    val neighbor = Point(point.x + dx, point.y + dy)
                    if (points.contains(neighbor)) {
                        if (dx == 0 || dy == 0) neighborCount++
                        else diagonalCount++
                    }
                }
            }
            // A corner point typically has 2 neighbors and might have 1 diagonal
            if (neighborCount == 2) corners.add(point)
        }
        return corners
    }
}

fun main() {
    val fileName = if (realInput) "input.txt" else "sample.txt"
    val input = File("src/Day12/$fileName").readLines()

    val grid = input.map { line -> line.toCharArray().toList() }
    val visited = mutableSetOf<Pair<Int, Int>>()
    val costs1 = mutableListOf<Pair<Int, Int>>() // area + perimeter
    val costs2 = mutableListOf<Pair<Int, Int>>() // area + sides
    val shapes = mutableListOf<Shape>()
    
    grid.forEachIndexed { i, chars ->
        chars.forEachIndexed { j, c ->
            if (Pair(i, j) !in visited) {
                val (area, perimeter, shape) = exploreShape(Pair(i, j), grid, visited)
                costs1.add(Pair(area, perimeter))
                val corners = shape.findCorners()
                costs2.add(Pair(area, corners.size / 2))
                shapes.add(shape)
            }
        }
    }

    println("Part 1:")
    println("Costs: ${costs1.sumOf { it.first * it.second }}")
    println("Part 2:")
    println("Costs: ${costs2.sumOf { it.first * it.second }}")
}

fun exploreShape(
    start: Pair<Int, Int>,
    grid: List<List<Char>>,
    visited: MutableSet<Pair<Int, Int>>,
    shapePoints: MutableSet<Point> = mutableSetOf(),
    area: Int = 0,
    perimeter: Int = 0
): Triple<Int, Int, Shape> {
    if (start in visited) return Triple(area, perimeter, Shape(shapePoints))
    
    visited.add(start)
    shapePoints.add(Point(start.first, start.second))
    var area2 = area + 1
    var perimeter2 = perimeter
    val currentLetter = grid[start.first][start.second]

    val directions = listOf(
        Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1)
    )

    for ((dx, dy) in directions) {
        val newX = start.first + dx
        val newY = start.second + dy
        
        if (newX in grid.indices && newY in grid[0].indices) {
            if (grid[newX][newY] != currentLetter) {
                perimeter2++
            } else {
                val (subArea, subPerimeter, subShape) = exploreShape(
                    Pair(newX, newY), grid, visited, shapePoints, area2, perimeter2
                )
                area2 = subArea
                perimeter2 = subPerimeter
            }
        } else {
            perimeter2++
        }
    }
    
    return Triple(area2, perimeter2, Shape(shapePoints))
}