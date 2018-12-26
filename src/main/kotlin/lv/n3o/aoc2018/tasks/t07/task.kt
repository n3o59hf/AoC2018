package lv.n3o.aoc2018.tasks.t07

import lv.n3o.aoc2018.readInputLines

val order = readInputLines("07a")
    .map { line -> line.split(" ") }
    .groupBy(
        keySelector = { it[1] },
        valueTransform = { it[7] }
    )
    .mapValues { it.value.toSet() }

val letters = order.flatMap { listOf(it.key) + it.value }.toSet().sorted()

val requirement = letters.groupBy(
    keySelector = { it },
    valueTransform = { v -> order.filter { v in it.value }.map { it.key } }
).mapValues { it.value.flatten().toSet() }

fun task07a(): String =
    buildString {
        val usableLetters = letters.toMutableList()
        while (usableLetters.isNotEmpty()) {
            val l = usableLetters.first { l -> requirement[l]?.all { it !in usableLetters } ?: false }
            append(l)
            usableLetters.remove(l)
        }
    }


fun task07b(): String {
    val usableLetters = letters.toMutableList()
    var currentTime = 0
    val queue = mutableListOf<Pair<String, Int>>().toSortedSet(compareBy { it.second })

    while (usableLetters.isNotEmpty() || queue.isNotEmpty()) {
        val l =
            usableLetters.firstOrNull { l -> l !in queue.map { it.first } && requirement[l]?.all { it !in usableLetters } ?: false }

        if (l == null || queue.size == 5) {
            val current = queue.first()
            currentTime = current.second
            queue.remove(current)
            usableLetters.remove(current.first)
        } else {
            val requiredTime = 61 + l[0].toInt() - 'A'.toInt()
            queue.add(l to currentTime + requiredTime)
        }
    }

    return currentTime.toString()
}