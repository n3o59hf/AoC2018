package lv.n3o.aoc2018.tasks.t23

import lv.n3o.aoc2018.readInputLines
import java.io.File
import kotlin.math.abs
import kotlin.math.max

val input = readInputLines("23a").map { Bot.parse(it) }.toSet()

fun main() {
    val input = File("data/23a").readLines().map { Bot.parse(it) }.toSet()
    // Part 1
    val maxRadiusBot = input.maxBy { it.r } ?: return
    println(input.count { it.copy(r = 0).intersects(maxRadiusBot) })

    // Part 2
    val startPosition = Coord(0, 0, 0)

    var currentRadius = max(input.deltaBy { it.pos.x }, max(input.deltaBy { it.pos.y }, input.deltaBy { it.pos.z }))

    var currentBots = setOf(Bot(Coord(0, 0, 0), currentRadius))

    while (currentRadius > 0) {
        currentRadius = (currentRadius / 2) + if (currentRadius > 2) 1 else 0

        val newGeneration = currentBots.flatMap { bot ->
            bot.pos.neighbors(currentRadius).map { c ->
                bot.copy(pos = c, r = currentRadius).let { newBot ->
                    newBot to input.count {
                        newBot.intersects(it)
                    }
                }
            }
        }
        val maxDistance = newGeneration.map { it.second }.max() ?: 0

        currentBots = newGeneration.filter { it.second == maxDistance }.map { it.first }.toSet()
    }

    println(currentBots.minBy { startPosition.distanceTo(it.pos) }?.pos?.distanceTo(startPosition))
}

inline fun <T> Iterable<T>.deltaBy(block: (T) -> Long): Long {
    val values = map(block)
    return abs((values.max() ?: 0L) - (values.min() ?: 0L))
}

data class Coord(val x: Long, val y: Long, val z: Long) {
    companion object {
        fun parse(input: String): Coord {
            val (x, y, z) = input
                .split(",")
                .map { it.filter { c -> c == '-' || c in '0'..'9' }.toLong() }

            return Coord(x, y, z)
        }

    }

    fun distanceTo(other: Coord): Long = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)

    fun neighbors(delta: Long): Iterable<Coord> =
        (-1L..1L).flatMap { xd ->
            (-1L..1L).flatMap { yd ->
                (-1L..1L).map { zd ->
                    this.copy(
                        x = this.x + xd * delta,
                        y = this.y + yd * delta,
                        z = this.z + zd * delta
                    )
                }
            }
        }

    override fun toString(): String = "<$x,$y,$z>"
}

data class Bot(val pos: Coord, val r: Long) {
    companion object {
        fun parse(input: String): Bot {
            val (coord, r) = input.split(", ")
            return Bot(Coord.parse(coord), r.split("=")[1].toLong())
        }
    }

    fun intersects(other: Bot) =
        pos.distanceTo(other.pos) <= r + other.r
}

// Cut here for post with main

fun task23a(): String {
    val maxRad = input.maxBy { it.r } ?: error("No max")
    return input.map { it.copy(r = 0) }.count { it.intersects(maxRad) }.toString()
}

fun task23b(): String {
    val startPosition = Coord(0, 0, 0)

    var currentRadius = max(
        abs((input.map { it.pos.x }.max() ?: 0) - (input.map { it.pos.x }.min() ?: 0)),
        max(
            abs((input.map { it.pos.y }.max() ?: 0) - (input.map { it.pos.y }.min() ?: 0)),
            abs((input.map { it.pos.z }.max() ?: 0) - (input.map { it.pos.z }.min() ?: 0))
        )
    )

    var currentBots = setOf(Bot(Coord(0, 0, 0), currentRadius))

    while (currentRadius > 0) {
        if (currentRadius > 2)
            currentRadius = (currentRadius / 2) + 1
        else {
            currentRadius /= 2
        }
        val newGeneration = currentBots.flatMap { bot ->
            bot.pos.neighbors(currentRadius).map { bot.copy(pos = it, r = currentRadius) }
        }

        val maxDistance = newGeneration.map { bot -> input.count { bot.intersects(it) } }.max() ?: 0

        currentBots = newGeneration.filter { bot -> input.count { bot.intersects(it) } == maxDistance }.toSet()
    }

    return currentBots.minBy { startPosition.distanceTo(it.pos) }?.pos?.distanceTo(startPosition).toString()
}

