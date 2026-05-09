package com.kogak.seasonmemory.core.domain

import com.kogak.seasonmemory.core.common.Position
import com.kogak.seasonmemory.core.common.Storage
import com.kogak.seasonmemory.core.domain.models.GenerateCardRules
import com.kogak.seasonmemory.core.domain.models.BoardSlot
import com.kogak.seasonmemory.core.domain.models.BoardSlotBluePrint
import com.kogak.seasonmemory.core.domain.models.Card
import com.kogak.seasonmemory.core.domain.models.CardBlueprint
import kotlin.random.Random

class Board(
    private val generateCardRules: GenerateCardRules,
    private val rows:Int = 8,
    private val cols:Int = 6,
) {
    private lateinit var map:List<String>
    private lateinit var storage: Storage
    private lateinit var cards : List<CardBlueprint>

    val slots = Array(rows) { Array(cols){ BoardSlotBluePrint(intArrayOf(0,0)) }}

    init{
        storage = Storage.create(rows*cols)
    }

    fun start(){
        if(map == null ) throw IllegalArgumentException("Jogo não pode ser iniciado!")
        generateGrid(generateCardRules)
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

    fun moveCard(oldBoardPosition:Position, newBoardPosition:Position):Card?{
        val oldSlot = slots[oldBoardPosition.row][oldBoardPosition.col]
        val nextSlot = slots[newBoardPosition.row][newBoardPosition.col]

        //se slot antigo tem carta e o novo slot não tem carta
        if(!oldSlot.isBlocked && oldSlot.card > 0 && !nextSlot.isBlocked && nextSlot.card < 0 ){
            nextSlot.card = oldSlot.card
            oldSlot.card = -1
            return getCard(newBoardPosition)
        }
        return null
    }

    fun getCard(slotPosition: Position):Card?{
        val slot = slots[slotPosition.row][slotPosition.col]

        if(slot.card < 0) return null
        return Card(
            storage.getValue(slot.card),
            storage.isFaceUp(slot.card),
            storage.isMatched(slot.card),
            position = slotPosition
        )
    }

    private fun generateGrid(rules: GenerateCardRules) {
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

                    slots[row][colIndex] = BoardSlotBluePrint(
                        position = intArrayOf(row, colIndex),
                        card = -1,
                        isBlocked = shouldBlock
                    )
                }
            }
        }

        // Gera o deck de cartas apenas com a quantidade exata de espaços válidos no mapa
        var cardsGenerated = generateCards(
            validPositions.size,
            rules.maxCardsPerMatch,
            rules.probability
        )

        if(rules.shuffled) {
            cardsGenerated.shuffled()
        }

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
    }

    private fun generateCards(deckSize:Int, maxCardsPerMatch:Int, probability: Float):List<CardBlueprint>{

        val resultDeck = mutableListOf<CardBlueprint>()

        // 1. Cria um "saco" de números únicos (1 a 99) já embaralhado.
        // Isso evita sorteios repetidos e falsos matches.
        val availableValues = (1..99).iterator()

        // 2. Preenche o deck enquanto houver espaço
        while (resultDeck.size < deckSize) {
            val spaceLeft = deckSize - resultDeck.size
            val cardValue = if (availableValues.hasNext()) availableValues.next().toShort() else 0

            //val newCard = CardBlueprint(cardValue)

            // Se a fase permite combos maiores, jogamos os dados (probabilidade)
            var groupSize = when {
                maxCardsPerMatch > 2 && Random.nextFloat() < probability -> {
                    // BINGO! A probabilidade bateu.
                    // Agora sorteia aleatoriamente se vai ser um trio (3) ou quarteto (4)
                    maxCardsPerMatch // Random.nextInt(3, maxCardsPerMatch + 1)
                }
                else -> 2
            }

            // 2. Ajuste de segurança: O grupo não pode ser maior que o espaço que sobra
            // E nem pode deixar sobrar apenas 1 espaço (já que o grupo mínimo é 2)
            if (groupSize > spaceLeft) {
                groupSize = spaceLeft
            }

            // Se após o sorteio sobrar 1 espaço sobrando no deck total,
            // aumentamos o grupo atual para absorver esse espaço ou reduzimos.
            if (spaceLeft - groupSize == 1) {
                groupSize--
            }

            repeat(groupSize) {
                resultDeck.add(CardBlueprint(cardValue))
            }
        }
       return resultDeck
   }
}
