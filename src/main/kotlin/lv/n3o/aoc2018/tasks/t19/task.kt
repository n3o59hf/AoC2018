package lv.n3o.aoc2018.tasks.t19

import lv.n3o.aoc2018.readInputLines


fun task19a(): String {
    val input = readInputLines("19a")
    val testProgram = input.dropWhile { it.startsWith("#") }.map { ins ->
        ins.split(" ").map { it.trim() }.filter { it.isNotBlank() }.let {
            Instruction(
                it[0],
                it[1].toLong(),
                it.getOrNull(2)?.toLong() ?: 0,
                it.getOrNull(3)?.toInt() ?: 0
            )
        }
    }

    val ipIndex = input.first { it.startsWith("#ip") }.split(" ")[1].toInt()

    val regs = longArrayOf(0, 0, 0, 0, 0, 0)

    val programRange = 0 until testProgram.size
    while (regs[ipIndex] in programRange) {
        operation(testProgram[regs[ipIndex].toInt()], regs)
        regs[ipIndex]++
    }

    return regs[0].toString()
}

/**
 * Program interpretation
 */
fun task19b(): String = (1..10551329).filter { 10551329 % it == 0 }.sum().toString()

data class Instruction(
    @JvmField val op: String,
    @JvmField val first: Long,
    @JvmField val second: Long,
    @JvmField val result: Int
) {
    @JvmField
    val immediateFirst = op[2] == 'i'
    @JvmField
    val immediateSecond = op[3] == 'i'
    @JvmField
    val shortOp = op.substring(0, 2)

    override fun toString(): String =
        "$op ${first.toString().padStart(2, ' ')} ${second.toString().padStart(2, ' ')} ${result.toString().padStart(
            2,
            ' '
        )}"
}

inline fun LongArray.op(
    instruction: Instruction,
    immediateFirst: Boolean,
    immediateSecond: Boolean,
    impl: (Long, Long) -> Long
) =
    when {
        immediateFirst && immediateSecond ->
            this[instruction.result] = impl(instruction.first, instruction.second)
        immediateFirst && !immediateSecond ->
            this[instruction.result] = impl(instruction.first, get(instruction.second.toInt()))
        !immediateFirst && immediateSecond ->
            this[instruction.result] = impl(get(instruction.first.toInt()), instruction.second)
        !immediateFirst && !immediateSecond ->
            this[instruction.result] = impl(get(instruction.first.toInt()), get(instruction.second.toInt()))
        else -> error("Impossible")
    }

fun operation(instruction: Instruction, regs: LongArray) {
    when (instruction.shortOp) {
        "no" -> Unit
        "ad" -> regs.op(instruction, false, instruction.immediateSecond) { a, b -> a + b }
        "mu" -> regs.op(instruction, false, instruction.immediateSecond) { a, b -> a * b }
        "di" -> regs.op(instruction, instruction.immediateFirst, instruction.immediateSecond) { a, b -> a / b }
        "ba" -> regs.op(instruction, false, instruction.immediateSecond) { a, b -> a and b }
        "bo" -> regs.op(instruction, false, instruction.immediateSecond) { a, b -> a or b }
        "se" -> regs.op(instruction, instruction.immediateSecond, true) { a, _ -> a }
        "gt" -> regs.op(
            instruction,
            instruction.immediateFirst,
            instruction.immediateSecond
        ) { a, b -> if (a > b) 1 else 0 }
        "eq" -> regs.op(
            instruction,
            instruction.immediateFirst,
            instruction.immediateSecond
        ) { a, b -> if (a == b) 1 else 0 }
        else -> error("Should not happen $instruction")
    }
}

