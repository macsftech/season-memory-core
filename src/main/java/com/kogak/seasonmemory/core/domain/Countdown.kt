/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

import com.kogak.seasonmemory.core.domain.GameTimer
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class Countdown: GameTimer {
    // Cria 1 única thread dedicada para contar o tempo (SingleThread)
    // Isso evita criar mil threads se o usuário clicar rápido demais

    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    // Guardamos o "Futuro" (a promessa da tarefa) para poder cancelar ela
    private var currentTask: ScheduledFuture<*>? = null

    // Inicia (ou Reinicia) a contagem
    override fun start(seconds: Long, onFinish: () -> Unit) {
        // Isso faz o efeito de "Resetar o tempo" quando o jogador acerta
        stop()

        // 2. Agenda a nova execução
        currentTask = scheduler.schedule({
            // Essa linha roda numa Thread Separada (Worker Thread)
            onFinish()
        }, seconds, TimeUnit.SECONDS)
    }

    // Para o timer (usado quando o jogo acaba ou reseta)
    override fun stop() {
        if (currentTask != null && !currentTask!!.isDone) {
            currentTask!!.cancel(false) // false = não interrompa se já estiver rodando o finalzinho
        }
        currentTask = null
    }

    // Matar a thread quando fechar o jogo para não vazar memória
    override fun shutdown() {
        scheduler.shutdownNow()
    }
}