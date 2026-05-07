
/*
 * Copyright (c) 2026 KoGaK Labs™ / macsftech
 * All rights reserved.
 * Part of the com.kogak.seasonmemory.core
 */

package com.kogak.seasonmemory.core.domain

interface StorageDebug{
    fun getValues():List<Short>
    fun getMatchedList():List<Boolean>
    fun getFaceUpList():List<Boolean>
}