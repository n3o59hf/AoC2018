package lv.n3o.aoc2018.tasks.t05

import lv.n3o.aoc2018.readInputLine
import java.util.*

val letter = CharArray(26) { (it + 65).toChar() }

fun task05a(): String =
    react(readInputLine("05a")).length.toString()


fun task05b(): String =
    letter
        .associate {
            Pair(it, react(react(readInputLine("05a")), it).length)
        }
        .minBy { it.value }
        ?.value
        .toString()

private fun react(data: String, ignoredChar: Char = ' '): String {
    val stack = ArrayDeque<Char>()
    val ignored = ignoredChar.toLowerCase()

    data.forEach { c ->
        when {
            c.toLowerCase() == ignored -> {
            }
            c == stack.firstOrNull()?.switchCase() -> stack.pop()
            else -> stack.push(c)
        }
    }

    return stack.joinToString("")
}

private fun Char.switchCase() = when (isLowerCase()) {
    true -> toUpperCase()
    else -> toLowerCase()
}
