package lv.n3o.aoc2018

import kotlinx.coroutines.*
import lv.n3o.aoc2018.tasks.t01.task01a
import lv.n3o.aoc2018.tasks.t01.task01b
import lv.n3o.aoc2018.tasks.t02.task02a
import lv.n3o.aoc2018.tasks.t02.task02b
import lv.n3o.aoc2018.tasks.t03.task03a
import lv.n3o.aoc2018.tasks.t03.task03b
import lv.n3o.aoc2018.tasks.t04.task04a
import lv.n3o.aoc2018.tasks.t04.task04b
import lv.n3o.aoc2018.tasks.t05.task05a
import lv.n3o.aoc2018.tasks.t05.task05b
import lv.n3o.aoc2018.tasks.t06.task06a
import lv.n3o.aoc2018.tasks.t06.task06b
import lv.n3o.aoc2018.tasks.t07.task07a
import lv.n3o.aoc2018.tasks.t07.task07b
import lv.n3o.aoc2018.tasks.t08.task08a
import lv.n3o.aoc2018.tasks.t08.task08b
import lv.n3o.aoc2018.tasks.t09.task09a
import lv.n3o.aoc2018.tasks.t09.task09b
import lv.n3o.aoc2018.tasks.t10.task10a
import lv.n3o.aoc2018.tasks.t10.task10b
import lv.n3o.aoc2018.tasks.t11.task11a
import lv.n3o.aoc2018.tasks.t11.task11b
import lv.n3o.aoc2018.tasks.t12.task12a
import lv.n3o.aoc2018.tasks.t12.task12b
import lv.n3o.aoc2018.tasks.t13.task13a
import lv.n3o.aoc2018.tasks.t13.task13b
import lv.n3o.aoc2018.tasks.t14.task14a
import lv.n3o.aoc2018.tasks.t14.task14b
import lv.n3o.aoc2018.tasks.t15.task15a
import lv.n3o.aoc2018.tasks.t15.task15b
import lv.n3o.aoc2018.tasks.t16.task16a
import lv.n3o.aoc2018.tasks.t16.task16b
import lv.n3o.aoc2018.tasks.t17.task17a
import lv.n3o.aoc2018.tasks.t17.task17b
import lv.n3o.aoc2018.tasks.t18.task18a
import lv.n3o.aoc2018.tasks.t18.task18b
import lv.n3o.aoc2018.tasks.t19.task19a
import lv.n3o.aoc2018.tasks.t19.task19b
import lv.n3o.aoc2018.tasks.t20.task20a
import lv.n3o.aoc2018.tasks.t20.task20b
import lv.n3o.aoc2018.tasks.t21.task21a
import lv.n3o.aoc2018.tasks.t21.task21b
import lv.n3o.aoc2018.tasks.t22.task22a
import lv.n3o.aoc2018.tasks.t22.task22b
import lv.n3o.aoc2018.tasks.t23.task23a
import lv.n3o.aoc2018.tasks.t23.task23b
import lv.n3o.aoc2018.tasks.t24.task24a
import lv.n3o.aoc2018.tasks.t24.task24b
import lv.n3o.aoc2018.tasks.t25.task25a
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.system.measureNanoTime

val taskList = listOf(
    "01" to listOf(::task01a, ::task01b),
    "02" to listOf(::task02a, ::task02b),
    "03" to listOf(::task03a, ::task03b),
    "04" to listOf(::task04a, ::task04b),
    "05" to listOf(::task05a, ::task05b),
    "06" to listOf(::task06a, ::task06b),
    "07" to listOf(::task07a, ::task07b),
    "08" to listOf(::task08a, ::task08b),
    "09" to listOf(::task09a, ::task09b),
    "10" to listOf(::task10a, ::task10b),
    "11" to listOf(::task11a, ::task11b),
    "12" to listOf(::task12a, ::task12b),
    "13" to listOf(::task13a, ::task13b),
    "14" to listOf(::task14a, ::task14b),
    "15" to listOf(::task15a, ::task15b),
    "16" to listOf(::task16a, ::task16b),
    "17" to listOf(::task17a, ::task17b),
    "18" to listOf(::task18a, ::task18b),
    "19" to listOf(::task19a, ::task19b),
    "20" to listOf(::task20a, ::task20b),
    "21" to listOf(::task21a, ::task21b),
    "22" to listOf(::task22a, ::task22b),
    "23" to listOf(::task23a, ::task23b),
    "24" to listOf(::task24a, ::task24b),
    "25" to listOf(::task25a)
)


fun main(args: Array<String>) {
    val threadPool = Executors.newFixedThreadPool(max(Runtime.getRuntime().availableProcessors() - 1, 1))
    val dispatcher = threadPool.asCoroutineDispatcher()

    runBlocking {
        taskList.map { (name, tasks) ->
            GlobalScope.async(dispatcher) {
                buildString {
                    append("=== DAY $name ===\n")
                    tasks.forEachIndexed { index, task ->
                        var answer: Any? = null
                        val time = measureNanoTime {
                            answer = task()
                        }
                        val timeText = (time / 1000000.0).roundToInt().toString()
                            .padStart(6, ' ')
                            .chunked(3)
                            .joinToString(" ")

                        append("PART ${'A' + index}: \n$answer\nTook $timeText ms\n\n")
                    }
                }
            }
        }.forEach {
            println(it.await())
            println()
        }
    }

    dispatcher.cancel()
    threadPool.shutdown()
}
