package com.kogak.seasonmemory.core.domain

import com.kogak.seasonmemory.core.common.Storage
import com.kogak.seasonmemory.core.domain.models.BoardRules
import com.kogak.seasonmemory.core.domain.models.BoardSlot
import com.kogak.seasonmemory.core.domain.models.Card
import com.kogak.seasonmemory.core.domain.models.CardBlueprint
import kotlin.random.Random

class Board(
    private val rows:Int = 8,
    private val cols:Int = 6
) {
    private lateinit var map:List<String>
    private lateinit var storage: Storage
    private lateinit var cards : List<CardBlueprint>

    val slots = Array(rows) { Array(cols){ BoardSlot(intArrayOf(0,0)) }}

    init{
        storage = Storage.create(rows*cols)
    }

    fun start(rules: BoardRules = BoardRules()){
        if(map == null ) throw IllegalArgumentException("Jogo não pode ser iniciado!")
        generateGrid(rules)
    }

    fun loadMap(newMap:List<String>){
       map = newMap
    }

    fun loadCards(newCards:List<CardBlueprint>){
        cards = newCards
    }

    fun faceDownCard(entityCard:Int){
        storage.putValue( entityCard, storage.getValue(entityCard), faceUp = false, matched = true)
    }

    fun faceUpCard(entityCard:Int){
        storage.putValue( entityCard, storage.getValue(entityCard), faceUp = true, matched = true)
    }

    fun matchedCard(entityCard:Int){
        storage.putValue( entityCard, storage.getValue(entityCard),  faceUp = true, matched = true)
    }

    fun flipCard(entityCard:Int){
        storage.putValue( entityCard, storage.getValue(entityCard), faceUp = true , storage.isMatched(entityCard))
    }

    fun getCardValue(entityCard:Int):Short{
        return storage.getValue(entityCard)
    }

    fun getCardMatched(entityCard:Int):Boolean{
        return storage.isMatched(entityCard)
    }

    fun getCardFaceUP(entityCard:Int):Boolean{
        return storage.isFaceUp(entityCard)
    }

    fun moveCard(oldBoardPosition:IntArray, newBoardPosition:IntArray){

        val oldSlot = slots[oldBoardPosition[0]][oldBoardPosition[1]]
        val nextSlot = slots[newBoardPosition[0]][newBoardPosition[1]]

        //println("slot origem:$oldSlot slot destino: $nextSlot")
        //se slot antigo tem carta e o novo slot não tem carta

        if(oldSlot.isBlocked || oldSlot.card < 0 ||  nextSlot.isBlocked || nextSlot.card >= 0 ){
            println("Não é possivel move carta")
        }
        else{
            nextSlot.card = oldSlot.card
            oldSlot.card = -1
        }

        //println("slot origem:$oldSlot slot destino: $nextSlot")
    }

    fun getCard(slotPosition:IntArray):Card?{
        val slot = slots[slotPosition[0]][slotPosition[1]]
        if(slot.card < 0) return null

        return Card(
            storage.getValue(slot.card),
            storage.isFaceUp(slot.card),
            storage.isMatched(slot.card),
            position = slotPosition.copyOf()
        )
    }

    private fun generateGrid(rules: BoardRules) {
        if (rules.shuffled) storage.shuffled()

        // Nomenclatura clara: armazena os pares (Linha, Coluna) disponíveis para cartas
        val validPositions = mutableListOf<Pair<Int, Int>>()

        map.forEachIndexed { row, slotTypeRaw ->
            val cleanSlotTypeRaw = slotTypeRaw.replace(" ", "")
            cleanSlotTypeRaw.forEachIndexed { colIndex, slotType ->
                if (row < rows && colIndex < cols) {
                    val shouldBlock = slotType == '#'
                    val shouldEmpty = slotType == '0'
                    //val shouldEmptyAndBlock  = slotType == '*'

                    if (!shouldEmpty) {
                        validPositions.add(Pair(row, colIndex))
                    }

                    slots[row][colIndex] = BoardSlot(
                        position = intArrayOf(row, colIndex),
                        card = -1,
                        isBlocked = shouldBlock
                    )
                }
            }
        }

        // Gera o deck de cartas apenas com a quantidade exata de espaços válidos no mapa
        val cardsGenerated = generateCards(
            validPositions.size,
            rules.maxCardsPerMatch,
            rules.probability
        )
        if(rules.shuffled) cardsGenerated.shuffled()
        cardsGenerated.forEachIndexed { index, card ->
            val row = validPositions[index].first
            val col = validPositions[index].second
            val cardIndex = (row * cols) + col
            storage.putValue(
                slotIndex = cardIndex,
                value = card.value,
                faceUp = card.isFlipped,
                matched = card.isMatched
            )
            slots[row][col].card = cardIndex
        }

        println(storage.debug().getValues())
    }

    private fun generateCards(deckSize:Int, maxCardsPerMatch:Int, probability: Float):List<CardBlueprint>{

        val resultDeck = mutableListOf<CardBlueprint>()

        // 1. Cria um "saco" de números únicos (1 a 99) já embaralhado.
        // Isso evita sorteios repetidos e falsos matches.
        val availableValues = (1..99).shuffled().iterator()

        // 2. Preenche o deck enquanto houver espaço
        while (resultDeck.size < deckSize) {
            val cardValue = availableValues.next().toShort()
            val newCard = CardBlueprint(cardValue)

            // Começa sempre assumindo que é um par normal (o padrão do jogo)
            var groupSize = 2

            // Se a fase permite combos maiores, jogamos os dados (probabilidade)
            if (maxCardsPerMatch > 2 && Random.nextFloat() < probability) {
                // BINGO! A probabilidade bateu.
                // Agora sorteia aleatoriamente se vai ser um trio (3) ou quarteto (4)
                groupSize = Random.nextInt(3, maxCardsPerMatch + 1)
            }

            // 4. Trava de Segurança Crítica (Continua igual)
            val spaceLeft = deckSize - resultDeck.size
            val actualGroupSize = if (groupSize > spaceLeft) spaceLeft else groupSize

            // 5. Adiciona as cartas
            for (i in 0 until actualGroupSize) {
                resultDeck.add(newCard.copy())
            }
        }
       return resultDeck
   }
}
