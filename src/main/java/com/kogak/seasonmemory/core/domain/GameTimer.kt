/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.domain
interface GameTimer {
    fun start(seconds: Long, onFinish: () -> Unit)
    fun stop()
    fun shutdown()
}
