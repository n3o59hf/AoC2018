package lv.n3o.aoc2018.tasks.t14

const val recipesLimit = 556061

fun task14a(): String {
    val recipesHolder = IntArray(recipesLimit * 3) { -1 }
    var currentRecipesCount = 2
    var recipesCycles = 0
    recipesHolder[0] = 3
    recipesHolder[1] = 7

    val elves = IntArray(2) { it }
    while (currentRecipesCount < recipesLimit + 10) {
        val recipeSum = "${elves.sumBy { recipesHolder[it] }}"
        recipeSum.split("").filterNot(String::isBlank).forEach { digit ->
            val d = digit.toInt()
            recipesHolder[currentRecipesCount] = d
            currentRecipesCount++
        }

        for (i in elves.indices) {
            elves[i] = (elves[i] + 1 + recipesHolder[elves[i]]) % currentRecipesCount
        }

        recipesCycles++
    }

    return recipesHolder.drop(recipesLimit).take(10).joinToString("") { "$it" }
}

fun task14b(): String {
    val recipes = sequence {
        var recipesHolder = IntArray(10) { -1 }
        var currentRecipesCount = 2
        var recipesCycles = 0
        recipesHolder[0] = 3
        recipesHolder[1] = 7
        yield(3)
        yield(7)

        val elves = IntArray(2) { it }
        while (true) {
            "${elves.sumBy { recipesHolder[it] }}".forEach { digit ->
                val d = Character.getNumericValue(digit)
                recipesHolder[currentRecipesCount] = d
                currentRecipesCount++
                yield(d)
            }

            for (i in elves.indices) {
                elves[i] = (elves[i] + 1 + recipesHolder[elves[i]]) % currentRecipesCount
            }

            recipesCycles++
            val size = recipesHolder.size
            if (currentRecipesCount > (size - 3)) {
                val newRecipesHolder = IntArray(size * 2) { -1 }
                System.arraycopy(recipesHolder, 0, newRecipesHolder, 0, size)
                recipesHolder = newRecipesHolder
            }
        }
    }

    val searchParam = recipesLimit.toString()
        .split("")
        .filterNot { it.isBlank() }
        .map { it.toInt() }
        .toIntArray()

    var currentPosition = -1
    var currentSearchParamPosition = 0

    recipes.first { i ->
        currentPosition++
        if (searchParam[currentSearchParamPosition] == i) {
            currentSearchParamPosition++
            if (currentSearchParamPosition == searchParam.size) return@first true
        } else {
            currentSearchParamPosition = 0
        }
        return@first false
    }

    return (currentPosition - searchParam.size + 1).toString()
}