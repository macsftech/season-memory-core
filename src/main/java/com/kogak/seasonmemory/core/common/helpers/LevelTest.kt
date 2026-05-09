/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.common.helpers

object LevelTest {
    var default = """
        0 0 0 0 0 0
        0 0 0 0 0 0
        0 1 1 1 1 0
        0 1 1 1 1 0
        0 1 1 1 1 0
        0 0 0 0 0 0
        0 0 0 0 0 0
    """.trimIndent().lines()

    var blockedSlots = """
        # 0 0 0 0 0
        0 0 0 0 0 0
        0 1 # 1 1 0
        0 1 1 1 1 0
        0 1 1 1 1 0
        0 0 0 0 0 0
        0 0 0 0 0 0
    """.trimIndent().lines()

    var swapCards = """
        1 0 0 0 0 0
        0 0 0 0 0 0
        0 1 1 1 1 0
        0 1 1 1 1 0
        0 1 1 1 0 0
        0 0 0 0 0 0
        0 0 0 0 0 0
    """.trimIndent().lines()

    var emptySlot = """
        0 0 0 0 0 0
        0 0 0 0 0 0
        0 1 0 1 1 0
        0 1 1 1 1 0
        0 1 1 1 0 0
        0 0 0 0 0 0
        0 0 0 0 0 0
    """.trimIndent().lines()
}