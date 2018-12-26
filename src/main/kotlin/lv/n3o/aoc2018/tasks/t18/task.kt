package lv.n3o.aoc2018.tasks.t18

import lv.n3o.aoc2018.readInputLines

const val DEBUG = false

val field = readInputLines("18a")
    .filter { it.isNotBlank() }
    .mapIndexed { y, line -> line.trim().mapIndexed { x, c -> Coord(x, y) to c } }
    .flatten()
    .toMap()

val xMin = field.entries.minBy { it.key.x }?.key?.x ?: 0
val xMax = field.entries.maxBy { it.key.x }?.key?.x ?: 0
val yMin = field.entries.minBy { it.key.y }?.key?.y ?: 0
val yMax = field.entries.maxBy { it.key.y }?.key?.y ?: 0

fun task18a(): String {
    var field = mutableMapOf(*field.entries.map { it.key to it.value }.toTypedArray())

    @Suppress("ConstantConditionIf")
    if (DEBUG) {
        println("Initial state:")
        field.print()
    }

    for (i in 1..10) {
        val newField = mutableMapOf<Coord, Char>()
        (yMin..yMax).forEach { y ->
            (xMin..xMax).forEach { x ->
                val current = field[x, y]
                val adjacent = field.getAdjacent(x, y)
                newField[x, y] = when {
                    current == '.' && adjacent.count { it == '|' } >= 3 -> '|'
                    current == '|' && adjacent.count { it == '#' } >= 3 -> '#'
                    current == '#' && adjacent.any { it == '|' } && adjacent.any { it == '#' } -> '#'
                    current == '#' -> '.'
                    else -> current
                }
            }
        }

        field = newField
        @Suppress("ConstantConditionIf")
        if (DEBUG) {
            println("After $i minutes:")
            field.print()
        }
    }

    return (field.values.count { it == '|' } * field.values.count { it == '#' }).toString()
}

fun task18b(): String {
    var field = mutableMapOf(*field.entries.map { it.key to it.value }.toTypedArray())

    val oldResults = mutableListOf<Map<Coord, Char>>()

    var i = 1
    while (i <= 1000000000) {
        oldResults.add(field)
        val newField = mutableMapOf<Coord, Char>()
        (yMin..yMax).forEach { y ->
            (xMin..xMax).forEach { x ->
                val current = field[x, y]
                val adjacent = field.getAdjacent(x, y)
                newField[x, y] = when {
                    current == '.' && adjacent.count { it == '|' } >= 3 -> '|'
                    current == '|' && adjacent.count { it == '#' } >= 3 -> '#'
                    current == '#' && adjacent.any { it == '|' } && adjacent.any { it == '#' } -> '#'
                    current == '#' -> '.'
                    else -> current
                }
            }
        }

        field = newField

        if (oldResults.contains(field)) {
            val cycle = oldResults.size - oldResults.indexOf(field)
            @Suppress("ConstantConditionIf")
            if (DEBUG) {
                println("Detected cycle with size $cycle")
            }
            val skipCycles = (1000000000 - i - 1) / cycle
            i += skipCycles * cycle
            oldResults.clear()
        }

        i++
    }

    return (field.values.count { it == '|' } * field.values.count { it == '#' }).toString()
}

fun Map<Coord, Char>.print() {
    @Suppress("ConstantConditionIf")
    if (!DEBUG) return
    (yMin..yMax).forEach { y ->
        (xMin..xMax).forEach { x ->
            print(this[x, y])
        }
        println()
    }
    println()
}

operator fun Map<Coord, Char>.get(x: Int, y: Int) = this[Coord(x, y)] ?: '.'
fun Map<Coord, Char>.getAdjacent(x: Int, y: Int) = Coord(x, y).adjacent.map { this[it] ?: '.' }

operator fun MutableMap<Coord, Char>.set(x: Int, y: Int, value: Char) {
    this[Coord(x, y)] = value
}

data class Coord(val x: Int, val y: Int) : Comparable<Coord> {
    override fun compareTo(other: Coord): Int {
        val yComp = y.compareTo(other.y)
        return if (yComp == 0) x.compareTo(other.x) else yComp
    }

    val adjacent by lazy {
        listOf(
            Coord(x - 1, y - 1),
            Coord(x, y - 1),
            Coord(x + 1, y - 1),
            Coord(x - 1, y),
            Coord(x + 1, y),
            Coord(x - 1, y + 1),
            Coord(x, y + 1),
            Coord(x + 1, y + 1)
        )

    }

    override fun toString() = buildString {
        append("[")
        append(x.toString().padStart(2, ' '))
        append(',')
        append(y.toString().padStart(2, ' '))
        append("]")
    }
}