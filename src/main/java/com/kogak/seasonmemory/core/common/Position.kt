package com.kogak.seasonmemory.core.common

@JvmInline
value class Position(private val packed: Long) {

    constructor(row: Int, col: Int) : this((row.toLong() shl 32) or (col.toLong() and 0xFFFFFFFFL))

    val row: Int get() = (packed shr 32).toInt()
    val col: Int get() = packed.toInt()
}