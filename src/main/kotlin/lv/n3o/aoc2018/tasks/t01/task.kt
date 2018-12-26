package lv.n3o.aoc2018.tasks.t01

import lv.n3o.aoc2018.infinite
import lv.n3o.aoc2018.readInputLines

fun task01a(): String =
    readInputLines("01a").map { it.trim() }.map { it.toInt() }.sum().toString()


fun task01b(): String {
    val frequencies = sequence {
        val data = readInputLines("01a").map(String::trim).map(String::toInt).infinite()
        var frequency = 0
        yield(0)

        data.forEach {
            frequency += it
            yield(frequency)
        }
    }

    val seen = mutableSetOf<Int>()
    return frequencies.first { !seen.add(it) }.toString()
}
