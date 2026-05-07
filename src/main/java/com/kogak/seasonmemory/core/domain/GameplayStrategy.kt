/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.domain

import com.kogak.seasonmemory.core.domain.models.BoardRules
import com.kogak.seasonmemory.core.domain.models.GameMode
import com.kogak.seasonmemory.core.domain.models.GameRecord

internal interface GameplayStrategy {
    fun onTurnStart(context:GameContext)
    fun onTurnEnd(context:GameContext)
    fun calculateScore(basePoints: Int, currentCombo: Int): Int
    fun resolveCombo(isMatch: Boolean, currentCombo: Int): Int
    fun getRules(): BoardRules
    fun getCurrentMap():List<String>
    fun getName():String
    fun load(gameRecord: GameRecord)
    fun changeMode(newGameMode: GameMode)
    fun changeLevel(newLevel:Int)
}
