package lv.n3o.aoc2018.tasks.t17

import lv.n3o.aoc2018.readInputLines

const val DEBUG = false

val clay = readInputLines("17a").flatMap { line ->
    line
        .split(",")
        .map { components ->
            components
                .trim()
                .split("=")
                .let { it.first() to it.last().split("..").map(String::toInt) }
        }
        .toMap().let { map ->
            ((map["x"]?.first() ?: 0)..(map["x"]?.last() ?: 0)).flatMap { x ->
                ((map["y"]?.first() ?: 0)..(map["y"]?.last() ?: 0)).map { y ->
                    Coord(x, y)
                }
            }
        }
}.toSet()

val minX = (clay.minBy { it.x }?.x ?: 500) - 1
val maxX = (clay.maxBy { it.x }?.x ?: 500) + 1
val minY = clay.minBy { it.y }?.y ?: 0
val maxY = clay.maxBy { it.y }?.y ?: 0
val start = Coord(500, 0)

fun waterFill(): Pair<Set<Coord>, Set<Coord>> {
    val waterSource = mutableSetOf(start)
    val waterStationary = mutableSetOf<Coord>()
    val exhaustedSource = mutableSetOf<Coord>()

    print(exhaustedSource, waterStationary, waterSource - exhaustedSource)

    fun Coord.onSomething() = clay.contains(down) || waterStationary.contains(down)
    fun Coord.free() = !clay.contains(this) && !waterStationary.contains(this) && !waterSource.contains(this)

    while ((waterSource - exhaustedSource).isNotEmpty()) {
        val lowest = (waterSource - exhaustedSource).maxBy { it.y } ?: error("no sources")
        when {
            lowest.y == maxY -> {
                exhaustedSource.add(lowest)
            }
            lowest.down.free() -> {
                var current = lowest.down
                while (current.free() && current.y <= maxY) {
                    waterSource.add(current)
                    current = current.down
                }
            }
            lowest.onSomething() && (lowest.left.free() || lowest.right.free()) -> {
                var current = lowest
                val checked = mutableSetOf<Coord>()
                var underflow = false
                while (current.onSomething() && current.left.free()) {
                    checked.add(current.left)
                    current = current.left
                    if (current.down.free()) {
                        waterSource.add(current.down)
                        underflow = true
                    }
                }

                current = lowest
                while (current.onSomething() && current.right.free()) {
                    checked.add(current.right)
                    current = current.right
                    if (current.down.free()) {
                        waterSource.add(current.down)
                        underflow = true
                    }
                }

                if (!underflow) {
                    waterSource.addAll(checked)
                }
            }
            lowest.onSomething() &&
                    (waterSource.contains(lowest.right) || waterSource.contains(lowest.left) ||
                            (clay.contains(lowest.right) && clay.contains(lowest.left))) -> {
                var leftMost = lowest
                while (waterSource.contains(leftMost.left)) leftMost = leftMost.left
                var rightMost = lowest
                while (waterSource.contains(rightMost.right)) rightMost = rightMost.right
                val line = (leftMost.x..rightMost.x).map { x -> Coord(x, lowest.y) }
                if (line.all { it.onSomething() }) {
                    exhaustedSource.removeAll(line)
                    waterSource.removeAll(line)
                    waterStationary.addAll(line)
                } else {
                    exhaustedSource.add(lowest)
                }
            }
            else -> {
                exhaustedSource.add(lowest)
            }

        }

        print(waterSource, waterStationary, waterSource - exhaustedSource)
    }

    print(exhaustedSource, waterStationary, waterSource - exhaustedSource)

    return waterSource to waterStationary
}

fun task17a(): String {
    val (waterSource, waterStationary) = waterFill()
    return (waterSource + waterStationary).filter { it.y in minY..maxY }.size.toString()
}

fun task17b(): String {
    val (_, waterStationary) = waterFill()
    return (waterStationary).filter { it.y in minY..maxY }.size.toString()
}

fun print(waterSource: Set<Coord>, waterStationary: Set<Coord>, waterActive: Set<Coord>) {
    @Suppress("ConstantConditionIf")
    if (!DEBUG) return
    for (y in minY..maxY) {
        for (x in minX..maxX) {
            val c = Coord(x, y)
            print(
                when {
                    clay.contains(c) -> "#"
                    waterActive.contains(c) -> "+"
                    waterStationary.contains(c) -> "~"
                    waterSource.contains(c) -> "|"
                    else -> "."
                }
            )
        }
        println()
    }
    println()
}

data class Coord(val x: Int, val y: Int) : Comparable<Coord> {
    override fun compareTo(other: Coord): Int {
        val yComp = y.compareTo(other.y)
        return if (yComp == 0) x.compareTo(other.x) else yComp
    }

    val down by lazy {
        Coord(x, y + 1)
    }

    val left by lazy {
        Coord(x - 1, y)
    }

    val right by lazy {
        Coord(x + 1, y)
    }

    override fun toString() = buildString {
        append("[")
        append(x.toString().padStart(2, ' '))
        append(',')
        append(y.toString().padStart(2, ' '))
        append("]")
    }
}
