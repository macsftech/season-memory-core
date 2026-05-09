/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.inMemory

import com.kogak.seasonmemory.core.domain.models.GameRecord
import com.kogak.seasonmemory.core.domain.IDatabaseAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class DataBaseInMemory:IDatabaseAdapter {

    private val gameRecordList = HashMap<String, GameRecord>()

    override suspend fun saveRecord(gameId: String, record: GameRecord) {
        return withContext(Dispatchers.IO){
            gameRecordList[gameId] = record
        }
    }

    override suspend fun loadRecord(gameId: String): GameRecord {
        return withContext(Dispatchers.IO){
            return@withContext gameRecordList[gameId]?: GameRecord()
        }
    }
}