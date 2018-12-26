package lv.n3o.aoc2018.tasks.t24

import lv.n3o.aoc2018.readInputLines
import kotlin.math.max

val input = readInputLines("24a")
val immuneSystem = input.takeWhile { it.isNotBlank() }.drop(1).map { Group.parse("ImmuneSystem", it) }
val infection = input.takeLastWhile { it.isNotBlank() }.drop(1).map { Group.parse("Infection", it) }

fun task24a(): String {
    val armies = listOf(immuneSystem.map { it.copy() }, infection.map { it.copy() }).flatten().toMutableList()
    while (armies.map { it.type }.toSet().size > 1) {
        armies.sortByDescending { it.iniative }
        armies.sortByDescending { it.effectivePower }

        val targets = armies.toMutableList()
        val attackers = armies.mapNotNull { attacker ->
            val defender = targets
                .filter { it.type != attacker.type }
                .maxBy { attacker.calculateDamageValueTo(it) }
                ?.takeIf { attacker.calculateDamageValueTo(it) > 0 }
            if (defender == null) null else {
                targets.remove(defender)
                attacker to defender
            }
        }.sortedBy { -it.first.iniative }

        attackers.forEach { (attacker, defender) ->
            attacker.doAttack(defender)
        }

        armies.removeIf { it.units == 0 }
    }

    return armies.sumBy { it.units }.toString()
}

fun task24b(): String {
    var boost = 0

    var armies: MutableList<Group>
    do {
        boost++
        armies = listOf(immuneSystem.map { it.copy(attack = it.attack + boost) }, infection.map { it.copy() }).flatten()
            .toMutableList()

        while (armies.map { it.type }.toSet().size > 1) {
            val allUnits = armies.sumBy { it.units }

            armies.sortByDescending { it.iniative }
            armies.sortByDescending { it.effectivePower }

            val targets = armies.toMutableList()
            val attackers = armies.mapNotNull { attacker ->
                val defender = targets
                    .filter { it.type != attacker.type }
                    .maxBy { attacker.calculateDamageValueTo(it) }
                    ?.takeIf { attacker.calculateDamageValueTo(it) > 0 }
                if (defender == null) null else {
                    targets.remove(defender)
                    attacker to defender
                }
            }.sortedBy { -it.first.iniative }

            attackers.forEach { (attacker, defender) ->
                attacker.doAttack(defender)
            }

            armies.removeIf { it.units == 0 }
            if (armies.sumBy { it.units } == allUnits) {
                break
            }
        }
    } while (armies.any { it.type == "Infection" })
    return armies.sumBy { it.units }.toString()
}

data class Group(
    val type: String,
    var units: Int,
    val hp: Int,
    val immunities: List<String>,
    val weaknesses: List<String>,
    val attack: Int,
    val attackType: String,
    val iniative: Int
) {
    val effectivePower: Int
        get() = units * attack

    fun calculateDamageValueTo(other: Group) =
        when {
            other.immunities.contains(attackType) -> 0
            other.weaknesses.contains(attackType) -> effectivePower * 2
            else -> effectivePower
        }

    fun doAttack(defender: Group) {
        val damageOutput = calculateDamageValueTo(defender)
        val unitsAfterwards = max(0, defender.units - damageOutput / defender.hp)

        defender.units = unitsAfterwards
    }

    companion object {
        fun parse(type: String, input: String): Group {
            val size = input.takeWhile { it != '(' }.split(" ")
            val typing =
                if (input.contains("(")) input.dropWhile { it != '(' }.drop(1).takeWhile { it != ')' }.split(";").map {
                    val pair = it.split(" to ").map(String::trim)
                    pair[0] to pair[1].split(", ")
                }.toMap() else emptyMap()
            val attackStats = input.takeLastWhile { it != ')' }.trim().split(" ").reversed()

            val units = size[0].toInt()
            val hp = size[4].toInt()

            val immunities = typing["immune"] ?: emptyList()
            val weaknesses = typing["weak"] ?: emptyList()

            val attack = attackStats[5].toInt()
            val attackType = attackStats[4]
            val iniative = attackStats[0].toInt()

            return Group(type, units, hp, immunities, weaknesses, attack, attackType, iniative)
        }
    }
}
