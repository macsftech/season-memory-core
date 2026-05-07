/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.domain.models

data class BoardRules(
    val shuffled:Boolean = true,
    val maxCardsPerMatch:Int = 2,
    val probability:Float =1f
)
