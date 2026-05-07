/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.inMemory

import com.kogak.seasonmemory.core.domain.GameTimer

class FakerGameTimer: GameTimer {
    // Guardamos o callback que o Gameplay mandou executar
    private var pendingCallback: (() -> Unit)? = null
    private var remainingTime: Long = 0

    override fun start(seconds: Long, onFinish: () -> Unit) {
        // Apenas anotamos o pedido, não executamos nada ainda
        pendingCallback = onFinish
        remainingTime = seconds
    }

    override fun stop() {
        pendingCallback = null
        remainingTime = 0
    }

    override fun shutdown() {}

    // MÉTODO MÁGICO PARA O TESTE:
    // Forçamos o tempo a passar manualmente
    fun advanceTime(seconds: Long) {
        if (remainingTime > 0) {
            remainingTime -= seconds
            if (remainingTime <= 0) {
                // O tempo acabou! Executa o callback agora (sincronamente)
                pendingCallback?.invoke()
                stop()
            }
        }
    }
}