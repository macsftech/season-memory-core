/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.domain

import com.kogak.seasonmemory.core.domain.models.GameRecord

interface IDatabaseAdapter {
  suspend fun saveRecord(gameId: String,record: GameRecord)
  suspend fun loadRecord(gameId: String): GameRecord
}
