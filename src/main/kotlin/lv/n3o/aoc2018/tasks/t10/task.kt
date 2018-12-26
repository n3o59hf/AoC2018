package lv.n3o.aoc2018.tasks.t10

import lv.n3o.aoc2018.readInputLines
import kotlin.math.abs

data class Coord(val x: Int, val y: Int) {
    operator fun plus(other: Coord): Coord {
        return Coord(x + other.x, y + other.y)
    }

    fun height(other: Coord) = abs(other.y - y)
}

class Point(val coord: Coord, private val velocity: Coord) {
    fun step(): Point = Point(coord + velocity, velocity)
}

val allowedChars = setOf(
    ',',
    '-',
    '0',
    '1',
    '2',
    '3',
    '4',
    '5',
    '6',
    '7',
    '8',
    '9'
)

fun bounds(points: Set<Coord>): Pair<Coord, Coord> {
    val leftTop = Coord(points.map { it.x }.min() ?: 0, points.map { it.y }.min() ?: 0)
    val rightBottom = Coord(points.map { it.x }.max() ?: 0, points.map { it.y }.max() ?: 0)
    return leftTop to rightBottom
}

fun getMessage(points: Iterable<Point>): String = buildString {
    val pointSet = points.map { it.coord }.toSet()
    val (leftTop, rightBottom) = bounds(pointSet)
    (leftTop.y..rightBottom.y).forEach { y ->
        (leftTop.x..rightBottom.x).forEach { x ->
            append(if (Coord(x, y) in pointSet) "#" else ".")
        }
        if (y != rightBottom.y) append("\n")
    }
}

fun task10a(): String {
    val points = readInputLines("10a")
        .map { s -> s.replace("velocity", ",").filter { it in allowedChars } }
        .map { it.split(",").map(String::toInt) }
        .map {
            val (x, y, dx, dy) = it
            Point(Coord(x, y), Coord(dx, dy))
        }

    var pointCloud = points
    val height: (List<Point>) -> Int = { p -> bounds(p.map { it.coord }.toSet()).let { it.first.height(it.second) } }
    var currentHeight = height(pointCloud)
    while (true) {
        val newPoints = pointCloud.map { it.step() }
        val newHeight = height(newPoints)
        if (newHeight < currentHeight) {
            pointCloud = newPoints
            currentHeight = newHeight
        } else {
            break
        }
    }

    return getMessage(pointCloud)
}

fun task10b(): String {
    val points = readInputLines("10a")
        .map { s -> s.replace("velocity", ",").filter { it in allowedChars } }
        .map { it.split(",").map(String::toInt) }
        .map {
            val (x, y, dx, dy) = it
            Point(Coord(x, y), Coord(dx, dy))
        }

    var time = 0
    var pointCloud = points
    val height: (List<Point>) -> Int = { p -> bounds(p.map { it.coord }.toSet()).let { it.first.height(it.second) } }
    var currentHeight = height(pointCloud)
    while (true) {
        val newPoints = pointCloud.map { it.step() }
        val newHeight = height(newPoints)
        if (newHeight < currentHeight) {
            pointCloud = newPoints
            currentHeight = newHeight
            time++
        } else {
            break
        }
    }

    return time.toString()
}