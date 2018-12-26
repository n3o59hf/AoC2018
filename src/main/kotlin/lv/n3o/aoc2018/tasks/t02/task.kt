package lv.n3o.aoc2018.tasks.t02

import lv.n3o.aoc2018.readInputLines

data class Box(val name: String) {
    val frequencies = name.groupingBy { it }.eachCount()

    val revFrequencies = frequencies.entries.groupBy(
        keySelector = { it.value },
        valueTransform = { it.key }
    )

    fun getDiff(other: Box): Int =
        name.zip(other.name).count { it.first != it.second }

}

fun task02a(): String {
    val data = readInputLines("02a").map(::Box)

    val twos = data.count { it.revFrequencies.containsKey(2) }
    val threes = data.count { it.revFrequencies.containsKey(3) }
    return (twos * threes).toString()
}

fun task02b(): String {
    val data = readInputLines("02a").map(::Box)

    val suspicious =
        data
            .mapIndexed { i, a -> data.drop(i).map { b -> Pair(a, b) } }
            .flatten()
            .filter { it.first.getDiff(it.second) == 1 }

    val (a, b) = suspicious.first()

    return a.name.zip(b.name).filter { it.first == it.second }.joinToString(separator = "") { it.first.toString() }
}