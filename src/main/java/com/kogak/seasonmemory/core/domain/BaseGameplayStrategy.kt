/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.domain

import com.kogak.seasonmemory.core.common.helpers.LevelTest
import com.kogak.seasonmemory.core.domain.models.GenerateCardRules
import com.kogak.seasonmemory.core.domain.models.GameMode
import com.kogak.seasonmemory.core.domain.models.GameRecord

open class BaseGameplayStrategy(
    val initialLevel:Int= 0,
    val initialScore: Int = 0
):GameplayStrategy {

    open var currentLevel:Int = initialLevel
        protected set
    open var score:Int = initialScore
        protected set
    open var gameMode: GameMode = GameMode.NORMAL
        protected set
    open var generateCardRules : GenerateCardRules = GenerateCardRules()
        protected set

    override fun onTurnStart() {}
    override fun onTurnEnd() {}
    override fun calculateScore(basePoints: Int, currentCombo: Int): Int {
        return  0
    }

    override fun resolveCombo(isMatch: Boolean, currentCombo: Int): Int {
        return  0
    }

    override fun getRules(): GenerateCardRules {
        return generateCardRules
    }

    override fun getCurrentMap(): List<String> {
        return when(currentLevel){
            1 -> LevelTest.blockedSlots
            2 -> LevelTest.swapCards
            3 -> LevelTest.emptySlot
            else -> LevelTest.default
        }
    }

    override fun getName(): String {
        return "default"
    }

    override fun load(gameRecord: GameRecord) {
        currentLevel =1
        score = 0
        gameMode = GameMode.NORMAL
    }

    override fun changeMode(newGameMode: GameMode){
        gameMode = newGameMode
    }

    override fun changeLevel(newLevel: Int) {
        currentLevel = newLevel
    }
}