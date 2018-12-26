package lv.n3o.aoc2018.tasks.t13

import lv.n3o.aoc2018.readInputLines

val maze = readInputLines("13a")
    .withIndex()
    .flatMap { (y, line) ->
        line.mapIndexed { x, c ->
            Coord(x, y) to when (c) {
                '^' -> '|'
                'v' -> '|'
                '>' -> '-'
                '<' -> '-'
                else -> c
            }
        }
    }.toMap()

fun task13a(): String {
    val carts = readInputLines("13a")
        .withIndex()
        .flatMap { (y, line) ->
            line.mapIndexed { x, c ->
                when (c) {
                    '^', 'v', '>', '<' -> Cart(x, y, c)
                    else -> null
                }
            }
        }
        .filterNotNull()
        .map { it.coord to it }
        .toMap()
        .toMutableMap()

    fun step(): String? {
        val order = carts
            .entries
            .sortedBy { it.key }
            .map { it.value }

        order.forEach { cart ->
            carts.remove(cart.coord)
            cart.move()
            if (carts[cart.coord] != null) {
                return "${cart.coord.x},${cart.coord.y}"
            } else {
                carts[cart.coord] = cart
            }
        }
        return null
    }

    return sequence { while (true) yield(step()) }.filterNotNull().first()
}

fun task13b(): String {
    var carts = readInputLines("13a")
        .withIndex()
        .flatMap { (y, line) ->
            line.mapIndexed { x, c ->
                when (c) {
                    '^', 'v', '>', '<' -> Cart(x, y, c)
                    else -> null
                }
            }
        }
        .filterNotNull()

    fun step() {
        carts.sortedBy { it.coord }.forEach { cart ->
            if (!cart.crashed) {
                cart.move()
                val collisionPartnerList = carts.filter { cart.id != it.id && cart.coord == it.coord && !it.crashed }
                if (collisionPartnerList.size > 1) error(collisionPartnerList)
                val collisionPartner = collisionPartnerList.firstOrNull()
                if (collisionPartner != null) {
                    cart.crashed = true
                    collisionPartner.crashed = true
                }
            }
        }

        carts = carts.filterNot { it.crashed }
    }

    while (carts.size > 1) {
        step()
    }

    return "${carts.first().coord.x},${carts.first().coord.y}"
}


const val R_LEFT = 0
const val R_STRAIGHT = 1
const val R_RIGHT = 2

data class Coord(val x: Int, val y: Int) : Comparable<Coord> {
    override fun compareTo(other: Coord): Int {
        val yComp = y.compareTo(other.y)
        return if (yComp == 0) x.compareTo(other.x) else yComp
    }


    operator fun plus(other: Coord) = Coord(x + other.x, y + other.y)

    fun rotate(angle90: Int): Coord = when (angle90) {
        R_LEFT -> Coord(y, -x)
        R_RIGHT -> Coord(-y, x)
        R_STRAIGHT -> this
        else -> error("Unsupported angle")
    }

    override fun toString() = "[${x.toString().padStart(3, ' ')},${y.toString().padStart(3, ' ')}]"
}

val D_UP = Coord(0, -1)
val D_DOWN = Coord(0, 1)
val D_RIGHT = Coord(1, 0)
val D_LEFT = Coord(-1, 0)

class Cart(initalX: Int, initialY: Int, symbol: Char) {
    val id: Long = initialY.toLong().shl(32) + initalX
    var crashed = false
    var coord = Coord(initalX, initialY)
    var direction = when (symbol) {
        '^' -> D_UP
        'v' -> D_DOWN
        '>' -> D_RIGHT
        '<' -> D_LEFT
        else -> error("Not a cart")
    }
    var rotation = R_LEFT

    fun move() {
        coord += direction
        val currentPosition = maze[coord] ?: error("Out of maze")
        when (currentPosition) {
            '/' -> direction = direction.rotate(
                when (direction) {
                    D_UP, D_DOWN -> R_RIGHT
                    D_RIGHT, D_LEFT -> R_LEFT
                    else -> error(direction)
                }
            )
            '\\' -> direction = direction.rotate(
                when (direction) {
                    D_UP, D_DOWN -> R_LEFT
                    D_RIGHT, D_LEFT -> R_RIGHT
                    else -> error(direction)
                }
            )
            '+' -> {
                direction = direction.rotate(rotation)
                rotation = (rotation + 1) % 3
            }
            '|' -> {
                if (direction != D_UP && direction != D_DOWN) error("Wrong direction")
            }
            '-' -> {
                if (direction != D_LEFT && direction != D_RIGHT) error("Wrong direction")
            }
            else -> error("Out of maze: $currentPosition")
        }
    }

    override fun toString(): String = when (direction) {
        D_UP -> '^'
        D_DOWN -> 'v'
        D_RIGHT -> '>'
        D_LEFT -> '<'
        else -> error("Not a cart")
    }.toString()
}