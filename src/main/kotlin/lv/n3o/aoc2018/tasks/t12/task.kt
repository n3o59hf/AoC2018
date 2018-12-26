package lv.n3o.aoc2018.tasks.t12

import lv.n3o.aoc2018.readInputLines

val data = readInputLines("12a")

val initialState = data[0].split(": ")[1]
val stateTransitions = data
    .drop(2)
    .map { it.split(" => ").let { s -> s[0].map(Char::b) to s[1][0].b } }
    .toMap()

fun task12a(): String {
    var stateMap = mutableMapOf<Int, Boolean>()
    initialState.forEachIndexed { i, c ->
        stateMap[i] = c.b
    }

    fun pattern(pos: Int) = (pos - 2..pos + 2).map { stateMap[it] ?: false }

    for (generation in 1..20) {
        val from = (stateMap.keys.min() ?: 0) - 2
        val to = (stateMap.keys.max() ?: 0) + 2
        val newStateMap = mutableMapOf<Int, Boolean>()
        (from..to).forEach {
            val pattern = pattern(it)
            val newTransition = stateTransitions[pattern]
            newStateMap[it] = newTransition ?: false
        }
        stateMap = newStateMap
    }

    return stateMap.entries.sumBy { if (it.value) it.key else 0 }.toString()
}

fun task12b(): String {
    var stateMap = mutableMapOf<Long, Boolean>()
    initialState.forEachIndexed { i, c ->
        stateMap[i.toLong()] = c.b
    }

    fun pattern(pos: Long) = (pos - 2..pos + 2).map { stateMap[it] ?: false }

    var generationCounter = 50000000000L
    while (generationCounter > 0) {
        val from = (stateMap.keys.min() ?: 0)
        val to = (stateMap.keys.max() ?: 0)
        val newStateMap = mutableMapOf<Long, Boolean>()
        (from - 2..to + 2).forEach {
            val pattern = pattern(it)
            val newTransition = stateTransitions[pattern]
            if (newTransition == true) newStateMap[it] = true
        }
        val newFrom = (newStateMap.keys.min() ?: 0)
        val newTo = (newStateMap.keys.max() ?: 0)
        if ((to - from) == (newTo - newFrom) &&
            (from..to).map { stateMap[it] == true }.zip((newFrom..newTo).map { newStateMap[it] == true })
                .all { it.first == it.second }
        ) {
            //stable state
            stateMap = newStateMap.mapKeys { entry -> entry.key + generationCounter - 1 }.toMutableMap()
            generationCounter = 0L
        } else {
            stateMap = newStateMap
            generationCounter--
        }

    }

    return stateMap.entries.fold(0L) { a, v -> a + if (v.value) v.key else 0 }.toString()
}

val Char.b
    get() = this == '#'