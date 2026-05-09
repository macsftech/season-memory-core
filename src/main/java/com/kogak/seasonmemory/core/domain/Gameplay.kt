/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.domain

import com.kogak.seasonmemory.core.common.Database
import com.kogak.seasonmemory.core.common.Position
import com.kogak.seasonmemory.core.domain.models.BoardSlot
import com.kogak.seasonmemory.core.domain.models.GameMode
import com.kogak.seasonmemory.core.domain.models.GameRecord
import com.kogak.seasonmemory.core.domain.models.Card
import com.kogak.seasonmemory.core.domain.models.CardBlueprint
import com.kogak.seasonmemory.core.inMemory.DataBaseInMemory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.random.Random

class Gameplay(
    private val strategy: BaseGameplayStrategy,
    private val board:Board,
    private val databaseAdapter: IDatabaseAdapter = DataBaseInMemory()
): GameContext {

    override var gameplayEventListener: GameplayEventListener? = null
    private var combo = 0
    private var match = 0
    private var isMatch = false
    private var currentScore:Int = 0
    private var isStarting:Boolean = false
    private var firstCardFlipped: Int? = null
    private val cardFlippedList:MutableList<Int> = mutableListOf()
    private var countdownSecond : Long = 5000
    private lateinit var gameMode: GameMode
    private lateinit var comboTimer: ComboTimer
    private lateinit var scope: CoroutineScope

    fun initialize(){
        Database.initialize(databaseAdapter)
        if(!isStarting) {
            gameplayEventListener?.showMessage("[CORE] Gameplay começou!")
            println("[CORE] Gameplay começou!")
            loadGameRecord()
            strategy.onTurnStart()
            board.loadMap(strategy.getCurrentMap())
            board.start()
            comboTimer = ComboTimer(scope)
            updateStateUI()
            isStarting = true
        }
    }

    private fun loadGameRecord(){
        scope.launch {
            try {
                val gameRecord = Database.getGameSave(strategy.getName())
                gameMode = gameRecord.mode
                strategy.load(gameRecord)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }


    fun saveGameRecord(){
        scope.launch {
            try {
                val gameRecord = GameRecord(
                    level =  strategy.currentLevel ,
                    score = strategy.score,
                    mode =  strategy.gameMode
                )
                Database.save(strategy.getName(),gameRecord)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun getCard(slotPosition: Position): Card? {
        return board.getCard(slotPosition)
    }

    fun moveCard(slotPosition:Position, newSlotPosition:Position){
        val cardMoved = board.moveCard(slotPosition,newSlotPosition)
        if(cardMoved is Card){
            updateStateUI()
        }
        else {
            gameplayEventListener?.showMessage("não é possivel move carta!")
            println("\n não é possivel move carta!")
        }
    }

    fun flipCard(slotPosition: Position){
        val card = board.slots[slotPosition.row][slotPosition.col].card
        if(card > 0 ){
            if(!board.getCardMatched(card) && !board.getCardFaceUP(card)){ // não pode ter sido encontrada nem esta virada para cima
                board.flipCard(card)
                cardFlippedList.add(card)
                if(cardFlippedList.size >= 2 ) checkMatch()
            }
            firstCardFlipped = firstCardFlipped?: card
            updateStateUI()
        }
    }

    fun getMatch():Int{
        return match
    }

    private fun checkMatch():Boolean {
        val firstCardFlipped = cardFlippedList.first()
        match = 0
        if (cardFlippedList.size > 1 ){
            val cardFirstValue = board.getCardValue(firstCardFlipped)
            var countCardsMatched = 1
            cardFlippedList.forEachIndexed { index,currentCard ->
                var currentCardValue = board.getCardValue(currentCard)
                if(index >= 1 && currentCardValue == cardFirstValue){
                    countCardsMatched++
                    board.matchedCard(firstCardFlipped)
                    board.matchedCard(currentCard)
                }else{
                    board.faceDownCard(currentCard)
                }
            }
            match = countCardsMatched
            updateStateUI()
        }
        return match > 0
    }

    fun setCoroutineScope(newScope:CoroutineScope){
        scope = newScope
    }

    fun dispose(){
        TODO("encerrar todos os listener timers jobs e tarefas em segundo planos...")
        comboTimer.stop()
        scope.cancel()
        gameplayEventListener = null
    }

    private fun checkCombo(){
         //TODO("melehora lógica de combo sem usar courotines apenas checando o currenttime
         // evitando processamento desnecessário para isso!")
        if (match > 0) {
            combo++
            comboTimer.start(countdownSecond){
                combo = 0
            }
        }
        else {
            combo = 0
            comboTimer.stop()
        }
   }

    override fun addScore(points: Long) {
        strategy.calculateScore(points.toInt(),combo)
    }

    override fun addCombo(combo: Int) {
        strategy.resolveCombo(isMatch,combo)
    }

    override fun getBoard(): Board {
        return board
    }

    override fun applyPenalty(penaltyType: String) {
        //strategy.resolvePenalty(isMatch,combo)
    }

    override fun getCurrentCombo(): Int {
        return combo
    }

    private fun updateStateUI(){
            val slots = board.slots.flatten().map {
                val ( position, _cardEntity, isBlocked ) =  it
                val cardBlueprint = getCard(Position(position[0],position[1]))?.let {
                    CardBlueprint(it.value,it.faceUp,it.isMatched)
                }
                BoardSlot(position, isBlocked, card = cardBlueprint )
         }
        gameplayEventListener?.onGameStateUpdated(slots, combo, strategy.score)
    }

//    fun setUiEventListener(newUiEventListener:UiEventListener){
//        UiEventListener = newUiEventListener
//    }
}