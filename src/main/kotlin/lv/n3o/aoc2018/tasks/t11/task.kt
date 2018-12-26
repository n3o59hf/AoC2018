package lv.n3o.aoc2018.tasks.t11

import kotlin.math.abs

const val input = 5177

data class Coord(val serial: Int, val x: Int, val y: Int) {
    val rackId = x + 10
    val power: Int by lazy {
        val powerStarts = rackId * y
        val withSerial = powerStarts + serial
        val withMultiplier = withSerial * rackId
        val thirdNumber = (abs(withMultiplier) / 100) % 10
        thirdNumber - 5
    }

    fun group(size: Int) =
        (x until x + size).flatMap { x1 -> (y until y + size).map { y1 -> Coord(serial, x1, y1) } }

    fun groupPower(size: Int) =
        group(size).sumBy { it.power }
}


fun task11a(): String {
    val maxGroup = (1..298).flatMap { x -> (1..298).map { y -> Coord(input, x, y) } }.maxBy { it.groupPower(3) }
    return "${maxGroup?.x},${maxGroup?.y}"
}

fun task11b(): String {
    val maxGroup = (1..20).flatMap { size ->
        (1..301 - size).flatMap { x ->
            (1..301 - size).map { y -> size to Coord(input, x, y) }
        }
    }.maxBy { it.second.groupPower(it.first) }


    return "${maxGroup?.second?.x},${maxGroup?.second?.y},${maxGroup?.first}"
}