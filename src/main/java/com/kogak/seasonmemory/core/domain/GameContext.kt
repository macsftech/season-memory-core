/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */


package com.kogak.seasonmemory.core.domain

interface GameContext {
   val uiEventListener:UiEventListener?
   fun addScore(points:Long)
   fun addCombo(combo:Int)
   fun getBoard(): Board
   fun applyPenalty(penaltyType: String)
   fun getCurrentCombo(): Int
}