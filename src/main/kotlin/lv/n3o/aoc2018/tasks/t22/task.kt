package lv.n3o.aoc2018.tasks.t22

import java.util.*
import kotlin.math.abs
import kotlin.math.max

val xMultiplier = 16807
val yMultiplier = 48271
val modulo = 20183

val depth = 11991
val target = Coord(6, 797)
//val depth = 510
//val target = Coord(10, 10)

const val NOTHING = 0
const val CLIMBING_GEAR = 1
const val TORCH = 2

class Cave {
    val erosion = mutableMapOf<Coord, Int>()

    operator fun get(c: Coord): Int {
        return if (erosion[c] != null) {
            erosion[c] ?: 0
        } else {
            val index = when {
                c.x < 0 || c.y < 0 -> error("Wrong coordinates $c")
                c.x == 0 && c.y == 0 -> 0
                c.x == target.x && c.y == target.y -> 0
                c.y == 0 -> c.x * xMultiplier
                c.x == 0 -> c.y * yMultiplier
                else -> get(c.left) * get(c.up)
            }

            val erosionLevel = (depth + index) % modulo
            erosion[c] = erosionLevel
            erosionLevel
        }
    }

    operator fun get(x: Int, y: Int) = get(Coord(x, y))

    fun canAccessWithTool(c: Coord, t: Int) = when (this[c] % 3) {
        0 -> t == CLIMBING_GEAR || t == TORCH
        1 -> t == NOTHING || t == CLIMBING_GEAR
        else -> t == TORCH || t == NOTHING
    }
}

fun task22a(): String {
    val cave = Cave()
    return (0..target.x).sumBy { x -> (0..target.y).sumBy { y -> cave[x, y] % 3 } }.toString()
}

fun task22b(): String {
    val cave = Cave()

    data class CoordTool(val coord: Coord, val tool: Int)

    val exploration = mutableMapOf(CoordTool(Coord(0, 0), TORCH) to 0)

    data class Position(val coordTool: CoordTool, val time: Int) {
        val coord = coordTool.coord
        val tool = coordTool.tool

        constructor(coord: Coord, tool: Int, time: Int) : this(CoordTool(coord, tool), time)

        val nextPositions by lazy {
            val currentCaveType = cave[coord] % 3
            val otherTool = when {
                currentCaveType == 0 && tool == CLIMBING_GEAR -> TORCH
                currentCaveType == 0 && tool == TORCH -> CLIMBING_GEAR
                currentCaveType == 1 && tool == CLIMBING_GEAR -> NOTHING
                currentCaveType == 1 && tool == NOTHING -> CLIMBING_GEAR
                currentCaveType == 2 && tool == TORCH -> NOTHING
                currentCaveType == 2 && tool == NOTHING -> TORCH
                else -> error("You shouldn't be here")
            }

            coord.neighbors.mapNotNull {
                if (cave.canAccessWithTool(it, tool)) Position(it, tool, time + 1) else null
            } + Position(coord, otherTool, time + 7)
        }
    }

    var currentTime = 0
    var maxDepth = 0
    val checkNext = TreeSet<Position> { a, b ->
        val time = a.time.compareTo(b.time)
        val distance = a.coord.distance(target).compareTo(b.coord.distance(target))
        val xy = (a.coord.x + a.coord.y * modulo).compareTo(b.coord.x + b.coord.y * modulo)
        if (time != 0) time else if (distance != 0) distance else xy
    }
    checkNext.add(Position(Coord(0, 0), TORCH, 0))
    while ((exploration[CoordTool(target, TORCH)] ?: Int.MAX_VALUE) > currentTime - 14) {
        val current = checkNext.first()
        checkNext.remove(current)

        val newMaxDepth = max(maxDepth, max(current.coord.x, current.coord.y))
        maxDepth = newMaxDepth
        currentTime = current.time
        current.nextPositions.forEach {
            if ((exploration[it.coordTool] ?: Int.MAX_VALUE) > it.time) {
                exploration[it.coordTool] = it.time
                checkNext.add(it)
            }
        }

    }

    return exploration[CoordTool(target, TORCH)].toString()
}

data class Coord(val x: Int, val y: Int) {
    companion object {
        val UP = Coord(0, -1)
        val DOWN = Coord(0, 1)
        val RIGHT = Coord(1, 0)
        val LEFT = Coord(-1, 0)
    }

    operator fun plus(other: Coord) = Coord(x + other.x, y + other.y)

    val left by lazy { this + LEFT }
    val up by lazy { this + UP }
    val right: Coord by lazy { this + RIGHT }
    val down: Coord by lazy { this + DOWN }

    val neighbors by lazy {
        listOf(
            left,
            up,
            right,
            down
        ).filterNot { it.x < 0 || it.y < 0 }
    }

    fun distance(other: Coord) = abs(x - other.x) + abs(y - other.y)
}