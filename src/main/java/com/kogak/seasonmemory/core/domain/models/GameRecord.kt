/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.domain.models

import com.kogak.seasonmemory.core.domain.models.GameMode

data class GameRecord(
    val score:Int = 0,
    val level:Int = 0,
    var mode: GameMode = GameMode.NORMAL,
    val timestamp: Long = System.currentTimeMillis()
)
