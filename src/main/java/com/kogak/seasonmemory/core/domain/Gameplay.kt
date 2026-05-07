/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.domain

import com.kogak.seasonmemory.core.common.Database
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
    private val databaseAdapter: IDatabaseAdapter = DataBaseInMemory()
): GameContext {

    override var uiEventListener: UiEventListener? = null

    private var combo = 0
    private val board = Board()
    private var match = 0
    private var isMatch = false
    private var currentScore:Int = 0
    private var isShuffled:Boolean = true
    private var isStarting:Boolean = false
    private var firstCardFlipped: Int? = null
    private val cardFlippedList:MutableList<Int> = mutableListOf()

    private var countdownSecond : Long = 5000
    private lateinit var gameMode: GameMode
    private lateinit var comboTimer: ComboTimer
    private lateinit var scope: CoroutineScope

    fun init(pairs:Int,shuffled:Boolean=true){
        Database.initialize(databaseAdapter)
        isShuffled = shuffled
        if(!isStarting) {
            println("começou!")
            loadGameRecord()
            board.loadMap(strategy.getCurrentMap())
            board.start(strategy.getRules())
            isStarting = true
            comboTimer = ComboTimer(scope)
            init(pairs,isShuffled)
            updateStateUI()
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

    private fun generateCards(pairs:Int):List<CardBlueprint>{
        var originalCard = mutableListOf<CardBlueprint>()
        for ( value in 0 until pairs ){
            originalCard.add(CardBlueprint(Random.nextInt(1,99).toShort()))
        }
        return originalCard + originalCard
    }

    fun getCard(slotPosition:IntArray): Card? {
        return board.getCard(slotPosition)
    }

    fun moveCard(oldBoardPosition:IntArray, newBoardPosition:IntArray){
       board.moveCard(oldBoardPosition,newBoardPosition)
    }

    fun flipCard(slotPosition: IntArray){
        val card = board.slots[slotPosition[0]][slotPosition[1]].card
        if(card > 0 ){
            if(!board.getCardMatched(card)){
                board.flipCard(card)
                cardFlippedList.add(card)
                checkMatch()
            }
            firstCardFlipped = firstCardFlipped?: card
            updateStateUI()
        }
    }

    fun getMatch():Int{
        return match
    }

    private fun checkMatch():Boolean {
        if (cardFlippedList.size > 1 ){
            val cardFirstValue = board.getCardValue(cardFlippedList.first())
            cardFlippedList.forEachIndexed { index,currentCard ->
                var currentCardValue = board.getCardValue(currentCard)
                if(index >= 1 && currentCardValue == cardFirstValue){
                    println("primeira carta => ${cardFlippedList.first()} valor:$cardFirstValue")
                    println("segunda carta => $currentCard valor:$currentCardValue")
                    match++
                    board.matchedCard(cardFlippedList.first())
                    board.matchedCard(currentCard)
                    print(board.slots.flatten())
                }
            }
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
        uiEventListener = null
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
        uiEventListener?.onGameStateUpdated(board.slots, combo, strategy.score)
    }

//    fun setUiEventListener(newUiEventListener:UiEventListener){
//        UiEventListener = newUiEventListener
//    }
}