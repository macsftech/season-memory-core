/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.domain.models

import com.kogak.seasonmemory.core.common.Position

data class Card(
    val value: Short,
    var faceUp: Boolean = false,
    var isMatched: Boolean = false,
    val position: Position = Position(0,0),
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Card
        if (value != other.value) return false
        return value == other.value
    }
    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + value
        return result
    }
}