package lv.n3o.aoc2018.tasks.t15

import lv.n3o.aoc2018.readInputLines

const val DEBUG = false

data class Coord(val x: Int, val y: Int) : Comparable<Coord> {
    override fun compareTo(other: Coord): Int {
        val yComp = y.compareTo(other.y)
        return if (yComp == 0) x.compareTo(other.x) else yComp
    }

    val adjacent by lazy {
        listOf(
            Coord(x, y - 1),
            Coord(x - 1, y),
            Coord(x + 1, y),
            Coord(x, y + 1)
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


class Character(
    val type: Char,
    initialX: Int,
    initialY: Int,
    private val field: MutableMap<Coord, Char>,
    private val damageValue: Int = 3
) :
    Comparable<Character> {
    var health = 200
    var c = Coord(initialX, initialY)
    private val enemy = if (type == 'E') 'G' else 'E'
    val dead
        get() = health <= 0

    private val adjacent
        get() = c.adjacent.filter { field[it] == '.' }

    override fun compareTo(other: Character): Int =
        c.compareTo(other.c)

    private fun takeDamage(from: Character, ammount: Int = 3) {
        log("Take $ammount of damage from $from")
        if (dead) error("Already dead")
        health -= ammount
        if (dead) {
            field[c] = '.'
            log("Died")
        }
    }

    private fun moveTo(c2: Coord) {
        log("Moving to $c2")
        if (field[c] != type) error("Battlefield out of sync")
        field[c] = '.'
        if (field[c2] != '.') error("Coordinate taken")
        field[c2] = type
        c = c2
    }

    fun doAction(all: Iterable<Character>): Boolean = when {
        dead -> true
        c.adjacent.map { field[it] }.contains(enemy) -> {
            doBattle(
                all
                    .filterNot { it.dead }
                    .filter { it.c in c.adjacent }
                    .filter { it.type == enemy }
            )
            true
        }
        field.containsValue(enemy) -> {
            doMove(
                all
                    .filterNot { it.dead }
                    .filter { it.type == enemy }
                    .flatMap { it.adjacent }
                    .toSet()
                    .filter { field[it] == '.' }
                    .sorted())

            if (c.adjacent.map { field[it] }.contains(enemy)) {
                doBattle(
                    all
                        .filterNot { it.dead }
                        .filter { it.c in c.adjacent }
                        .filter { it.type == enemy }
                )
            }
            true
        }
        else -> {
            log("Notifying that battle has ended")
            false
        }
    }


    private fun doBattle(enemies: List<Character>) {
        log("Enemies around: ${enemies.joinToString(",") { "($it)" }}")
        enemies.sorted().sortedBy { it.health }.firstOrNull()?.takeDamage(from = this, ammount = damageValue)
    }

    private fun doMove(potentialCoords: List<Coord>) {
        log("On the move")
        val endPoints = sequence {
            var stepCounter = 0
            var moves = setOf(c)
            val oldMoves = mutableSetOf(c)
            while (moves.isNotEmpty()) {
                stepCounter++
                oldMoves.addAll(moves)
                moves = moves.flatMap { it.adjacent }.filter { field[it] == '.' }.filterNot { it in oldMoves }.toSet()
                yield(stepCounter to moves)
            }
        }

        val movePlan = endPoints
            .firstOrNull { coords -> coords.second.any { it in potentialCoords } } ?: kotlin.run {
            log("No valid move space")
            return
        }


        val endpoint = movePlan.second.first { it in potentialCoords }

        log("Target location $endpoint in ${movePlan.first} steps")
        doMoveOneStep(endpoint)
    }

    private fun doMoveOneStep(to: Coord) {
        var moveCounter = -1
        val moveMap = mutableMapOf<Coord, Int>()
        moveMap[c] = moveCounter
        var currentNextMoves = setOf(c)
        while (true) {
            moveCounter++
            currentNextMoves.forEach { moveMap[it] = moveCounter }
            if (to in currentNextMoves) break
            currentNextMoves = currentNextMoves
                .flatMap { it.adjacent }
                .filter { field[it] == '.' }
                .filter { moveMap[it] == null }
                .toSet()
        }

        currentNextMoves = setOf(to)

        while (moveCounter > 0) {
            moveMap.entries.filter { it.value == moveCounter }.filter { it.key !in currentNextMoves }.forEach {
                moveMap.remove(it.key)
            }

            moveCounter--
            currentNextMoves = currentNextMoves
                .flatMap { it.adjacent }
                .filter { moveMap[it] == moveCounter }
                .toSet()
        }

        val potentialMoves = moveMap.filter { it.value == 1 }.map { it.key }.sorted()
        log("Potential moves $potentialMoves")

        moveTo(potentialMoves.first())
    }

    override fun toString(): String {
        val health = if (dead) "---" else health.toString().padStart(3, '0')
        return "$type($health) $c"
    }
}


fun task15a(): String {
    val field = readInputLines("15a")
        .map { it.trim() }
        .mapIndexed { y, line -> line.mapIndexed { x, char -> Coord(x, y) to char } }
        .flatten()
        .toMap()
        .toMutableMap()

    val characters = field
        .entries
        .filter { it.value == 'E' || it.value == 'G' }
        .map { Character(it.value, it.key.x, it.key.y, field) }


    field.print(characters)

    var moves = 0
    while (characters.sorted().filterNot { it.dead }.all {
            it.doAction(characters)
        }) {
        moves++
        @Suppress("ConstantConditionIf")
        if (DEBUG) println("============== Round $moves =================")
        field.print(characters)
    }

    @Suppress("ConstantConditionIf")
    if (DEBUG) println("============== In the end =================")
    field.print(characters)

    return (moves * characters.filterNot { it.dead }.sumBy { it.health }).toString()
}

fun task15b(): String {
    var elfPower = 3
    battle@ while (true) {
        elfPower++
        val field = readInputLines("15a")
            .map { it.trim() }
            .mapIndexed { y, line -> line.mapIndexed { x, char -> Coord(x, y) to char } }
            .flatten()
            .toMap()
            .toMutableMap()

        val characters = field
            .entries
            .filter { it.value == 'E' || it.value == 'G' }
            .map { Character(it.value, it.key.x, it.key.y, field, if (it.value == 'E') elfPower else 3) }

        field.print(characters)

        var moves = 0
        while (characters.sorted().filterNot { it.dead }.all {
                it.doAction(characters)
            }) {
            if (characters.any { it.type == 'E' && it.dead }) {
                @Suppress("ConstantConditionIf")
                if (DEBUG) println("!!!!!!!!!!! Elf died !!!!!!!!!!!!!!!!")
                continue@battle
            }
            moves++
            @Suppress("ConstantConditionIf")
            if (DEBUG) println("============== Round $moves =================")
            field.print(characters)
        }

        if (characters.any { it.type == 'E' && it.dead }) {
            @Suppress("ConstantConditionIf")
            if (DEBUG) println("!!!!!!!!!!! Elf died !!!!!!!!!!!!!!!!")
            continue@battle
        }

        @Suppress("ConstantConditionIf")
        if (DEBUG) println("============== In the end =================")
        field.print(characters)

        return (moves * characters.filterNot { it.dead }.sumBy { it.health }).toString()
    }
}

operator fun <T> Map<Coord, T>.get(x: Int, y: Int) = this[Coord(x, y)]
operator fun <T> MutableMap<Coord, T>.set(x: Int, y: Int, value: T) {
    this[Coord(x, y)] = value
}

operator fun Iterable<Character>.get(x: Int, y: Int) = firstOrNull { it.c == Coord(x, y) }

fun Map<Coord, Char>.print(characters: Iterable<Character>?) {
    @Suppress("ConstantConditionIf")
    if (!DEBUG) return
    val c = characters?.sorted()?.filterNot { it.dead }?.groupBy { it.c.y } ?: emptyMap()
    println()

    val xMax = keys.map { it.x }.max() ?: 0
    val yMax = keys.map { it.y }.max() ?: 0

    (-3..yMax).forEach { y ->
        (-3..xMax).forEach { x ->
            when {
                x >= 0 && y == -3 -> print((x % 100) / 10)
                x >= 0 && y == -2 -> print((x % 10))
                y >= 0 && x == -3 -> print((y % 100) / 10)
                y >= 0 && x == -2 -> print(y % 10)
                y >= 0 && x >= 0 -> print(this[x, y])
                else -> print(' ')
            }
        }
        print("   ")
        print(c[y]?.joinToString(", ") { "$it" } ?: "")
        println()
    }
    println()
}


@Suppress("ConstantConditionIf")
fun Character.log(line: String) {
    if (DEBUG) println("$this: $line")
}
