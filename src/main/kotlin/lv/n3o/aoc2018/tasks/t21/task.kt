package lv.n3o.aoc2018.tasks.t21

import java.io.File

typealias Registers = LongArray

val input = File("data/21a").readLines()

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

val zeroRegCheck = testProgram.indexOfFirst { it.op == "eqrr" }.toLong()

fun main() {
    val regs = Registers(6) { 0 }

    val seenValues = mutableSetOf<Long>()
    while (true) {
        if (regs[ipIndex] == zeroRegCheck) {
            if (!seenValues.add(regs[5])) {
                println(seenValues.first())
                println(seenValues.last())
                return
            }
        }
        operation(testProgram[regs[ipIndex].toInt()], regs)
        regs[ipIndex]++
    }
}

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
}

inline fun Registers.op(
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
        "ad" -> regs.op(instruction, false, instruction.immediateSecond) { a, b -> a + b }
        "mu" -> regs.op(instruction, false, instruction.immediateSecond) { a, b -> a * b }
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

// Cut here for post with main

fun task21a(): String {

    val regs = longArrayOf(0, 0, 0, 0, 0, 0)

    val zeroRegCheck = 28L // eqrr 5 0
    val regToCheck = 5

    while (regs[ipIndex] != zeroRegCheck) {
        operation(testProgram[regs[ipIndex].toInt()], regs)
        regs[ipIndex]++
    }

    return regs[regToCheck].toString()
}

fun task21b(): String {
    val regs = longArrayOf(0, 0, 0, 0, 0, 0)

    val seenRegs = mutableSetOf<Long>()
    while (true) {
        if (regs[ipIndex] == zeroRegCheck) {
            if (!seenRegs.add(regs[5])) {
                return seenRegs.last().toString()
            }
        }
        operation(testProgram[regs[ipIndex].toInt()], regs)
        regs[ipIndex]++
    }
}

