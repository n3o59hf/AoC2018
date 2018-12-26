package lv.n3o.aoc2018.tasks.t16

import lv.n3o.aoc2018.readInputLines

val input = readInputLines("16a")
val samples = input
    .chunked(4)
    .takeWhile { it[0].startsWith("Before") }
    .map { it.take(3) }
    .map { (before, instruction, after) ->
        Sample(
            before.split("[")[1].split("]")[0].split(",").map { it.trim().toInt() },
            instruction.split(" ").map { it.trim().toInt() }.let { Instruction(it[0], it[1], it[2], it[3]) },
            after.split("[")[1].split("]")[0].split(",").map { it.trim().toInt() }
        )
    }

val testProgram = input.takeLastWhile { it.isNotBlank() }
    .map { ins -> ins.split(" ").map { it.trim().toInt() }.let { Instruction(it[0], it[1], it[2], it[3]) } }

val ops = listOf(
    "addr", "addi",
    "mulr", "muli",
    "banr", "bani",
    "borr", "bori",
    "setr", "seti",
    "gtir", "gtri", "gtrr",
    "eqir", "eqri", "eqrr"
)

fun task16a() = samples
    .count { sample ->
        ops.count { op ->
            operation(op, sample.instruction, sample.before) == sample.after
        } > 2
    }
    .toString()

fun task16b(): String {
    val opcodes = mutableMapOf<Int, Set<String>>()
    samples.forEach { (before, instruction, after) ->
        val possibleOps = ops.filter { op ->
            operation(op, instruction, before) == after
        }

        if (opcodes[instruction.opcode] == null) {
            opcodes[instruction.opcode] = possibleOps.toSet()
        } else {
            opcodes[instruction.opcode] = opcodes[instruction.opcode].orEmpty().intersect(possibleOps)
        }
    }

    while (opcodes.any { it.value.size > 1 }) {
        val uniqe = opcodes.values.filter { it.size == 1 }.map { it.first() }.toSet()
        opcodes.mapValuesTo(opcodes) { (_, value) ->
            if (value.size > 1) {
                value - uniqe
            } else {
                value
            }
        }
    }

    val opTable = opcodes.map { it.key to it.value.take(1).first() }.toMap()

    var reg = listOf(0, 0, 0, 0)
    testProgram.forEach { instruction ->
        reg = operation(opTable, instruction, reg) ?: error("Illegal operation")
    }
    return reg[0].toString()
}

data class Sample(val before: List<Int>, val instruction: Instruction, val after: List<Int>)
data class Instruction(val opcode: Int, val first: Int, val second: Int, val result: Int)

fun List<Int>.set(index: Int, value: Int) =
    if (size <= index) null else
        mapIndexed { i, t -> if (index == i) value else t }

fun List<Int>.op(instruction: Instruction, immediate: Boolean, impl: (Int, Int) -> Int) =
    when {
        immediate -> opi(instruction, impl)
        else -> opr(instruction, impl)
    }

fun List<Int>.op(instruction: Instruction, immediateFirst: Boolean, immediateSecond: Boolean, impl: (Int, Int) -> Int) =
    when {
        immediateFirst && immediateSecond -> set(
            instruction.result,
            impl(instruction.first, instruction.second)
        )
        immediateFirst && !immediateSecond -> set(
            instruction.result,
            impl(instruction.first, get(instruction.second))
        )
        !immediateFirst && immediateSecond -> set(
            instruction.result,
            impl(get(instruction.first), instruction.second)
        )
        !immediateFirst && !immediateSecond -> set(
            instruction.result,
            impl(get(instruction.first), get(instruction.second))
        )
        else -> error("Impossible")
    }

fun List<Int>.opr(instruction: Instruction, impl: (Int, Int) -> Int) =
    set(instruction.result, impl(get(instruction.first), get(instruction.second)))

fun List<Int>.opi(instruction: Instruction, impl: (Int, Int) -> Int) =
    set(instruction.result, impl(get(instruction.first), instruction.second))

fun operation(opTable: Map<Int, String>, instruction: Instruction, inputState: List<Int>) =
    operation(opTable[instruction.opcode] ?: "", instruction, inputState)

fun operation(op: String, instruction: Instruction, inputState: List<Int>): List<Int>? {
    val immediateFirst = op[2] == 'i'
    val immediateSecond = op[3] == 'i'
    val shortOp = op.substring(0, 2)
    return when (shortOp) {
        "ad" -> inputState.op(instruction, immediateSecond) { a, b -> a + b }
        "mu" -> inputState.op(instruction, immediateSecond) { a, b -> a * b }
        "ba" -> inputState.op(instruction, immediateSecond) { a, b -> a and b }
        "bo" -> inputState.op(instruction, immediateSecond) { a, b -> a or b }
        "se" -> inputState.op(instruction, immediateSecond, true) { a, _ -> a }
        "gt" -> inputState.op(instruction, immediateFirst, immediateSecond) { a, b -> if (a > b) 1 else 0 }
        "eq" -> inputState.op(instruction, immediateFirst, immediateSecond) { a, b -> if (a == b) 1 else 0 }
        else -> error("Unknown $op $shortOp")
    }
}

//581 too low