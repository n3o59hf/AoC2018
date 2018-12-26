package lv.n3o.aoc2018.tasks.t04

import lv.n3o.aoc2018.readInputLines
import java.time.temporal.ChronoUnit
import java.util.*

data class LogRecord(val year: Int, val month: Int, val day: Int, val hour: Int, val minute: Int, val logLine: String) {
    val sortKey = 1L *
            year * 60L * 24L * 31L * 12L +
            month * 60L * 24L * 31L +
            day * 60L * 24L +
            hour * 60L +
            minute

    val hourMinute = hour * 60 + minute

    val guardId by lazy { logLine.split('#')[1].split(' ')[0].toInt() }

    companion object {
        fun parse(input: String): LogRecord {
            val (time, logLine) = input.split(']').map(String::trim)
            val (year, month, day, hour, minute) = time
                .split('[', '-', ' ', ':')
                .filterNot(String::isBlank)
                .map { it.toInt() }

            return LogRecord(year, month, day, hour, minute, logLine)
        }
    }

    fun until(logRecord: LogRecord): Int {
        val new = GregorianCalendar(
            logRecord.year,
            logRecord.month - 1,
            logRecord.day,
            logRecord.hour,
            logRecord.minute
        ).toZonedDateTime()

        val old = GregorianCalendar(year, month - 1, day, hour, minute).toZonedDateTime()

        return old.until(new, ChronoUnit.MINUTES).toInt()
    }
}

data class GuardSleep(val id: Int, val minutesAsleep: Int, val startsAt: LogRecord) {
    private val asleepMinutes by lazy {
        val arr = BooleanArray(24 * 60)
        for (i in 0 until minutesAsleep) {
            arr[(startsAt.hourMinute + i) % (24 * 60)] = true
        }
        arr
    }

    fun isAsleepInMinute(minute: Int) = asleepMinutes[minute]
}

fun task04a(): String {
    val data = readInputLines("04a")
        .filterNot(String::isBlank)
        .map(LogRecord.Companion::parse)
        .sortedBy { it.sortKey }


    val sleeps = sequence {
        var guardId = -1
        var asleep: LogRecord? = null
        data.forEach { l ->
            when {
                l.logLine.contains("Guard") -> guardId = l.guardId
                l.logLine.contains("falls asleep") -> asleep = l
                l.logLine.contains("wakes up") -> {
                    asleep?.let {
                        val result = it.until(l)
                        yield(GuardSleep(guardId, result, it))
                    }
                }
            }
        }
    }

    val times = sleeps.groupingBy { it.id }.fold(0) { accumulator, element -> accumulator + element.minutesAsleep }

    val sleepyGuard = times.maxBy { it.value }?.key ?: error("No Guards")

    val minuteCounter = IntArray(24 * 60)

    sleeps.filter { it.id == sleepyGuard }.forEach {
        for (i in 0 until it.minutesAsleep) {
            minuteCounter[(it.startsAt.hourMinute + i) % (24 * 60)]++
        }
    }

    val minute = minuteCounter.withIndex().groupBy({ it.value }, { it.index }).maxBy { it.key }?.value?.first() ?: -1
    return (sleepyGuard * minute).toString()
}

fun task04b(): String {
    val data = readInputLines("04a")
        .filterNot(String::isBlank)
        .map(LogRecord.Companion::parse)
        .sortedBy { it.sortKey }


    val sleeps = sequence {
        var guardId = -1
        var asleep: LogRecord? = null
        data.forEach { l ->
            when {
                l.logLine.contains("Guard") -> guardId = l.guardId
                l.logLine.contains("falls asleep") -> asleep = l
                l.logLine.contains("wakes up") -> {
                    asleep?.let {
                        val result = it.until(l)
                        yield(GuardSleep(guardId, result, it))
                    }
                }
            }
        }
    }

    val minuteCounter = Array<MutableList<Int>>(24 * 60) { mutableListOf() }

    sleeps.forEach {
        for (i in 0 until it.minutesAsleep) {
            minuteCounter[(it.startsAt.hourMinute + i) % (24 * 60)].add(it.id)
        }
    }
    data class GuardMinute(val id: Int, val minute: Int)

    val minute = minuteCounter.withIndex().flatMap { (i, l) -> l.map { g -> GuardMinute(g, i) } }

    val mostSleepy = minute.groupingBy { it }.eachCount().maxBy { it.value }?.key ?: error("No answer")

    return (mostSleepy.id * mostSleepy.minute).toString()
}