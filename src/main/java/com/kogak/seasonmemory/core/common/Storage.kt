/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */


package com.kogak.seasonmemory.core.common

import com.kogak.seasonmemory.core.domain.StorageDebug
import kotlin.random.Random

object Storage{

    private lateinit var valueList: ShortArray
    private lateinit var faceUpList: BooleanArray
    private lateinit var matchedList: BooleanArray

    fun create(size:Int):Storage{
        valueList = ShortArray(size)
        faceUpList = BooleanArray(size)
        matchedList = BooleanArray(size)
        length = size
        return this
    }

    private val inDebug = object: StorageDebug {
        override fun getValues(): List<Short> {
            return valueList.toList()
        }

        override fun getMatchedList(): List<Boolean>  {
            return matchedList.toList()
        }

        override fun getFaceUpList(): List<Boolean>  {
            return faceUpList.toList()
        }
    }

    var length:Int = 0
        private set

    fun shuffled(){
        for (i in length - 1 downTo 1) {
            var randomId = Random.nextInt(i + 1);

            if(valueList[i] >= 1){
                var tempValue = valueList[i]
                valueList[i] = valueList[randomId]
                valueList[randomId] = tempValue

                var tempFaceup = faceUpList[i]
                faceUpList[i] = faceUpList[randomId]
                faceUpList[randomId] = tempFaceup

                var tempMatched = matchedList[i]
                matchedList[i] =  matchedList[randomId]
                matchedList[randomId] = tempMatched
            }
        }
    }

    fun getValue(entity:Int):Short{
        return valueList[entity]
    }

    fun isFaceUp(entity:Int):Boolean{
        return faceUpList[entity]
    }

    fun isMatched(entity:Int):Boolean{
        return matchedList[entity]
    }

    fun putValue(slotIndex:Int, value:Short, faceUp:Boolean, matched:Boolean){
        valueList[slotIndex] = value
        faceUpList[slotIndex] = faceUp
        matchedList[slotIndex] = matched
    }

    fun debug(): StorageDebug {
        return inDebug
    }
}