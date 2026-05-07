/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */


package com.kogak.seasonmemory.core.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ComboTimer(private var scope:CoroutineScope){
    private var job:Job? = null
    fun start(milliSeconds: Long, onFinish: () -> Unit){
        stop()
        job = scope.launch {
            delay(milliSeconds)
            onFinish()
        }
    }

    fun stop(){
        job?.cancel()
        job = null
    }
}