package lv.n3o.aoc2018

import java.io.File

fun readInputLine(name: String) = File("data/$name").readText()

fun readInputLines(name: String) =
    File("data/$name").readLines()


fun <T> List<T>.infinite() = sequence {
    while (true) {
        yieldAll(this@infinite)
    }
}