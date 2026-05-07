/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.domain.models

data class BoardSlot(
    val position: IntArray = IntArray(2),
    var card: Int = -1,
    var isBlocked: Boolean = false
)