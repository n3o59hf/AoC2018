package lv.n3o.aoc2018.tasks.t08

import lv.n3o.aoc2018.readInputLine

val data = readInputLine("08a").split(" ").filterNot { it.isBlank() }.map { it.toInt() }

val rootRecord: Record
    get() {
        val dataIterator = data.iterator()

        fun readRecord(): Record {
            val childrenCount = dataIterator.next()
            val metadataCount = dataIterator.next()
            return Record(
                (0 until childrenCount).map { readRecord() },
                (0 until metadataCount).map { dataIterator.next() })
        }

        return readRecord()
    }

fun task08a(): String = rootRecord.metadataSum.toString()


fun task08b(): String = rootRecord.checksum.toString()

data class Record(
    val children: List<Record>,
    val metadata: List<Int>
) {
    val metadataSum: Int by lazy {
        children.sumBy { it.metadataSum } + metadata.sum()
    }

    val checksum: Int by lazy {
        if (children.isEmpty())
            metadata.sum()
        else
            metadata.map { children.getOrNull(it - 1) }.sumBy { it?.checksum ?: 0 }
    }

}
