package com.traumtraum.adventapp2.base.arch.data.store

import io.reactivex.Maybe

/**
 * Interface for any type of store.
 */
interface Store<in Key, Value> {

    fun putSingular(value: Value)

    fun putAll(valueList: List<Value>)

    fun clear()

    fun getSingular(key: Key): Maybe<Value>

    fun getAll(): Maybe<List<Value>>

    /**
     * More descriptive interface for memory based stores.
     */
    interface MemoryStore<in Key, Value> : Store<Key, Value>

    /**
     * More descriptive interface for disk based stores.
     */
    interface DiskStore<in Key, Value> : Store<Key, Value>
}
