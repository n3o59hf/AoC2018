package lv.n3o.aoc2018.tasks.t20

import lv.n3o.aoc2018.readInputLine
import java.util.*

val input = readInputLine("20a").substringBefore("$").substringAfter("^")

val rooms: Map<Coord, Room>
    get() = sequence {
        val stack = ArrayDeque<Room>()
        val starterRoom = Room(initialCoord = Coord(0, 0), initialDepth = 0)
        var currentRoom = starterRoom
        input.forEach { c ->
            when (c) {
                'N' -> {
                    val room = Room()
                    room.down = currentRoom
                    currentRoom.up = room
                    currentRoom = room
                }
                'E' -> {
                    val room = Room()
                    room.right = currentRoom
                    currentRoom.left = room
                    currentRoom = room
                }
                'S' -> {
                    val room = Room()
                    room.up = currentRoom
                    currentRoom.down = room
                    currentRoom = room
                }
                'W' -> {
                    val room = Room()
                    room.left = currentRoom
                    currentRoom.right = room
                    currentRoom = room
                }
                '|' -> currentRoom = stack.first
                '(' -> stack.push(currentRoom)
                ')' -> currentRoom = stack.pop()
            }
            currentRoom.coord
            currentRoom.depth
        }
        val seen = mutableSetOf<Room>()
        val toCheck = mutableSetOf<Room>()
        toCheck.add(starterRoom)
        while (toCheck.isNotEmpty()) {
            val current = toCheck.first()
            toCheck.remove(current)
            if (seen.add(current)) {
                yield(current)
                toCheck.addAll(listOfNotNull(current.up, current.right, current.down, current.left))
            }
        }
    }.associateBy { it.coord }


fun task20a(): String =
    rooms.values.map { it.depth }.max().toString()

fun task20b(): String =
    rooms.values.count { it.depth >= 1000 }.toString()

data class Coord(val x: Int, val y: Int) {
    companion object {
        val UP = Coord(0, -1)
        val DOWN = Coord(0, 1)
        val RIGHT = Coord(1, 0)
        val LEFT = Coord(-1, 0)
    }

    operator fun plus(other: Coord) = Coord(x + other.x, y + other.y)
}

class Room(
    var up: Room? = null,
    var right: Room? = null,
    var down: Room? = null,
    var left: Room? = null,
    initialCoord: Coord? = null,
    initialDepth: Int = -1
) {
    val coord: Coord by lazy {
        initialCoord
            ?: up?.coord?.let { it + Coord.DOWN }
            ?: down?.coord?.let { it + Coord.UP }
            ?: right?.coord?.let { it + Coord.LEFT }
            ?: left?.coord?.let { it + Coord.RIGHT }
            ?: error("At least one coord should be present")
    }

    val depth: Int by lazy {
        if (initialDepth > -1) initialDepth
        else (up?.depth ?: right?.depth ?: down?.depth ?: left?.depth ?: error("Not connected")) + 1
    }

    override fun equals(other: Any?): Boolean =
        coord == (other as? Room)?.coord

    override fun hashCode(): Int = coord.hashCode()
}

@Suppress("unused")
fun Map<Coord, Room>.print() {
    val xMin = keys.minBy { it.x }?.x ?: 0
    val xMax = (keys.maxBy { it.x }?.x ?: 0)

    val yMin = keys.minBy { it.y }?.y ?: 0
    val yMax = (keys.maxBy { it.y }?.y ?: 0)

    for (y in yMin..yMax) {
        var firstLine = ""
        var secondLine = ""

        for (x in xMin..xMax) {
            val coord = Coord(x, y)
            val room = this[coord]

            if (room == null) {
                firstLine += "##"
                secondLine += "#  "
            } else {
                firstLine += if (room.up != null) "#|" else "##"
                secondLine += if (room.left != null) "-." else "#."
            }
        }
        println("$firstLine#")
        println("$secondLine#")
    }
    for (x in xMin until xMax) {
        print("##")
    }
    println("#")
    println()
    println()
}
