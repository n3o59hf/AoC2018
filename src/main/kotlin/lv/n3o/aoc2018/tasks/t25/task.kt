package lv.n3o.aoc2018.tasks.t25

import lv.n3o.aoc2018.readInputLines
import kotlin.math.abs

data class Point(val x: Int, val y: Int, val z: Int, val t: Int) {

    companion object {
        fun parse(s: String) = s.split(",").map { it.trim().toInt() }.let { (x, y, z, t) ->
            Point(x, y, z, t)
        }
    }

    fun distanceTo(other: Point) =
        abs(x - other.x) + abs(y - other.y) + abs(z - other.z) + abs(t - other.t)
}

val points = readInputLines("25a").map(Point.Companion::parse)

fun task25a(): String {
    val notUsedPoints = points.toSet().toMutableList()

    val clusters = mutableSetOf<Set<Point>>()

    while (notUsedPoints.isNotEmpty()) {
        val currentPoints = mutableSetOf(notUsedPoints.removeAt(0))
        var currentSize: Int
        do {
            currentSize = currentPoints.size

            currentPoints.addAll(
                notUsedPoints.filter { point -> currentPoints.any { it.distanceTo(point) <= 3 } }
            )

            notUsedPoints.removeAll { it in currentPoints }
        } while (currentSize < currentPoints.size)
        clusters.add(currentPoints)
    }

    return clusters.size.toString()
}
