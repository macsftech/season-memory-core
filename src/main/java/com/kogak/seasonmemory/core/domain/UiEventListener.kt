/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.domain

import com.kogak.seasonmemory.core.domain.models.BoardSlot

interface UiEventListener {
//    fun CardFlipped(card:Card)
//    fun MatchFound(matchedIds: List<Int>)
//    fun OnMatch(cardId1: Int,  cardId2: Int, pointsEarned: Int)
//    fun OnMismatch(cardId1: Int,cardId2: Int)
//    fun OnGameOver(finalRecord: GameRecord)
//    fun onScoreUpdated( newScore: Int)
//    fun OnStart(board:Array<Array<BoardSlot>>)
     fun onGameStateUpdated(boardSlot: Array<Array<BoardSlot>>, combo: Int, score: Int)
     fun onMatchFound(matchCount: Int)
     fun onMisMatch()
     fun onGameFinished(victory: Boolean)
}

