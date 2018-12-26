package lv.n3o.aoc2018.tasks.t03

import lv.n3o.aoc2018.readInputLines

data class Coord(val x: Int, val y: Int)
data class Rectangle(val id: Int, val x: Int, val y: Int, val w: Int, val h: Int) {
    companion object {
        fun parse(input: String): Rectangle {
            val (id, x, y, w, h) = input
                .replace("#", " ")
                .replace("@", " ")
                .replace(",", " ")
                .replace(":", " ")
                .replace("x", " ")
                .split(' ')
                .filter { !it.isBlank() }
                .map { it.toInt() }
            return Rectangle(id, x, y, w, h)
        }
    }

    val allCoords = (x until x + w).flatMap { i -> (y until y + h).map { j -> Coord(i, j) } }
}

fun task03a(): String {
    val data = readInputLines("03a").map(Rectangle.Companion::parse)

    val claims = data.flatMap(Rectangle::allCoords).groupingBy { it }.eachCount()

    return claims.count { it.value > 1 }.toString()
}


fun task03b(): String {
    val data = readInputLines("03a").map(Rectangle.Companion::parse)

    val claims = data.flatMap { c -> c.allCoords.map { Pair(c.id, it) } }.groupBy { it.second }

    val invalidClaims = claims.filter { it.value.size > 1 }.values.flatMap { l -> l.map { it.first } }.toSet()
    return data.first { !invalidClaims.contains(it.id) }.id.toString()
}