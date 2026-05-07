/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.common

import com.kogak.seasonmemory.core.domain.models.GameRecord
import com.kogak.seasonmemory.core.domain.IDatabaseAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal object Database{
    private lateinit var adapter: IDatabaseAdapter

    fun initialize(databaseAdapter: IDatabaseAdapter) {
        adapter = databaseAdapter
    }

    suspend fun save(gameId: String, record: GameRecord) {
        if (adapter == null)  error("Database não inicializado!")
        if(gameId.isBlank() || record.score < 0)  error("Não foi póssivel salvar! gameId ou score inválidos.")
        withContext(Dispatchers.IO){
            adapter?.saveRecord(gameId, record)
        }
    }

    suspend fun getGameSave(gameId:String): GameRecord {
        return withContext(Dispatchers.IO){
            return@withContext adapter.loadRecord(gameId)
        }
    }
}