package lv.n3o.aoc2018.tasks.t06

import lv.n3o.aoc2018.readInputLines
import kotlin.math.abs

data class Point(val id: Int, val c: Coord)
data class Coord(val x: Int, val y: Int) {
    val neighbors
        get() = listOf(
            Coord(x - 1, y),
            Coord(x + 1, y),
            Coord(x, y - 1),
            Coord(x, y + 1)
        )

    fun distance(other: Coord) = abs(x - other.x) + abs(y - other.y)
}


fun task06a(): String {
    val data = readInputLines("06a")
        .filterNot { it.isBlank() }
        .withIndex()
        .map {
            it.value.split(",", " ").filterNot(String::isBlank).map(String::toInt)
                .let { c -> Point(it.index, Coord(c[0], c[1])) }
        }

    val infinite = mutableSetOf<Int>()
    val xDelta = 0//data.sortedBy { it.c.x }.map { it.c.x }.zipWithNext().map { it.second - it.first }.max() ?: 0
    val yDelta = 0// data.sortedBy { it.c.y }.map { it.c.y }.zipWithNext().map { it.second - it.first }.max() ?: 0
    val minX = data.sortedBy { it.c.x }.first().c.x - xDelta
    val maxX = data.sortedBy { it.c.x }.last().c.x + xDelta
    val minY = data.sortedBy { it.c.y }.first().c.y - yDelta
    val maxY = data.sortedBy { it.c.y }.last().c.y + yDelta


    val map = mutableMapOf<Coord, Int>()

    data.forEach {
        map[it.c] = it.id
    }

    var muted = true

    while (muted) {
        val newCoords = mutableMapOf<Coord, Int>()

        map.forEach { (c, i) ->
            if (i > -1) {
                if (c.x < minX || c.x > maxX || c.y < minY || c.y > maxY) {
                    infinite.add(i)
                } else {
                    c.neighbors.forEach {
                        val oldCoord = map[it]
                        if (oldCoord == null) {
                            val newCoord = newCoords[it]
                            if (newCoord == null || newCoord == i) {
                                newCoords[it] = i
                            } else {
                                newCoords[it] = -1
                            }
                        }
                    }
                }
            }
        }

        map.putAll(newCoords)
        muted = newCoords.isNotEmpty()
    }

    return map
        .filter { it.value !in infinite && it.value != -1 }
        .map { it.value }
        .groupingBy { it }
        .eachCount()
        .maxBy { it.value }
        ?.value
        .toString()
}

fun task06b(): String {
    val data = readInputLines("06a")
        .filterNot { it.isBlank() }
        .withIndex()
        .map {
            it.value.split(",", " ").filterNot(String::isBlank).map(String::toInt)
                .let { c -> Point(it.index, Coord(c[0], c[1])) }
        }

    val area = 10000

    val minX = data.sortedBy { it.c.x }.first().c.x - (area / (data.size - 1))
    val maxX = data.sortedBy { it.c.x }.last().c.x + (area / (data.size - 1))
    val minY = data.sortedBy { it.c.y }.first().c.y - (area / (data.size - 1))
    val maxY = data.sortedBy { it.c.y }.last().c.y + (area / (data.size - 1))


    return (minY..maxY)
        .sumBy { y ->
            (minX..maxX).count { x ->
                val sum = data.sumBy { Coord(x, y).distance(it.c) }
                sum < area
            }
        }
        .toString()

}