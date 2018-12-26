package lv.n3o.aoc2018.tasks.t09

import kotlin.math.sign

const val playersInput = 404
const val marblesInput = 71852

class NavigationList<T>(firstValue: T) {
    inner class Node(val value: T) {
        var next: Node = this
        var prev: Node = this
    }

    private var nodePosition = 0
    private var node: Node = Node(firstValue)
    var size = 1
        private set

    fun add(index: Int, value: T) {
        if (nodePosition < 0) {
            error("not supported when empty")
        } else {
            navigateTo(index - 1)
            val newNode = Node(value)
            newNode.prev = node
            newNode.next = node.next
            node.next.prev = newNode
            node.next = newNode
            size++
        }
    }

    fun removeAt(index: Int): T {
        if (size < 2) {
            error("empty list not supported")
        } else {
            navigateTo(index)
            val ret = node.value
            val removedNode = node
            removedNode.prev.next = removedNode.next
            removedNode.next.prev = removedNode.prev
            node = removedNode.prev
            nodePosition--
            size--

            return ret
        }
    }

    private fun navigateTo(index: Int) {
        var diff = index - nodePosition
        while (diff != 0) {
            node = when {
                diff > 0 -> node.next
                else -> node.prev
            }
            diff -= diff.sign
        }
        nodePosition = index
    }

    fun asSequence(): Sequence<T> = sequence {
        var currentNode = node
        var offset = nodePosition
        while (offset > 0) {
            currentNode = currentNode.prev
            offset--
        }

        for (i in 0 until size) {
            currentNode.value?.let { yield(it) }
            currentNode = currentNode.next
        }
    }

}

class Playground(private val players: Int, private val marbles: Int) {
    private var currentMarble = 0L
    private var currentPosition = 0
        set(i) {
            field = (i + circle.size) % circle.size
        }
    private var currentPlayer = 0
        set(i) {
            field = (i + players) % players
        }

    val playerScore = LongArray(players)
    private val circle = NavigationList(0L)

    private fun move() {
        currentMarble++
        currentPlayer++

        if (currentMarble % 23L == 0L) {
            playerScore[currentPlayer] += currentMarble
            currentPosition -= 7
            playerScore[currentPlayer] += circle.removeAt(currentPosition)
        } else {
            currentPosition++
            circle.add(currentPosition + 1, currentMarble)
            currentPosition++
        }
    }

    fun game() {
        while (currentMarble <= marbles) {
            move()
        }
    }

    override fun toString(): String =
        buildString {
            append("[$currentMarble] ")
            circle.asSequence().withIndex().forEach { (i, m) ->
                if (i == currentPosition) {
                    append("($m) ")
                } else {
                    append("$m ")
                }
            }
        }

}

fun task09a(): String =
    Playground(playersInput, marblesInput).apply { game() }.playerScore.max().toString()


fun task09b(): String =
    Playground(playersInput, marblesInput * 100).apply { game() }.playerScore.max().toString()
