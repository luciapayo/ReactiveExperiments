package com.traumtraum.adventapp2.base.arch.data.store

import io.reactivex.Maybe
import utils.Printer

/**
 * Created by Lucia on 10/02/2018
 */
class MemoryAndDiskStore<in Key, Value> constructor(private val memoryStore: Store.MemoryStore<Key, Value>,
                                                    private val diskStore: Store.DiskStore<Key, Value>)
    : Store<Key, Value> {

    override fun putSingular(value: Value) {
        diskStore.putSingular(value)
        memoryStore.putSingular(value)
    }

    override fun putAll(valueList: List<Value>) {
        diskStore.putAll(valueList)
        memoryStore.putAll(valueList)
    }

    override fun clear() {
        diskStore.clear()
        memoryStore.clear()
    }

    override fun getSingular(key: Key): Maybe<Value> =
            Maybe.concat(memoryStore.getSingular(key), getSingularFromDiskAndPutInMemory(key))
                    .firstElement()

    override fun getAll(): Maybe<List<Value>> {
        Printer.print("Get all from stores")
        return Maybe.concat(memoryStore.getAll(), getAllFromDiskAndPutInMemory())
                .firstElement()
    }

    private fun getSingularFromDiskAndPutInMemory(key: Key) =
            diskStore.getSingular(key)
                    .doOnSuccess { memoryStore.putSingular(it) }

    private fun getAllFromDiskAndPutInMemory() =
            diskStore.getAll()
                    .doOnSuccess { memoryStore.putAll(it) }

}
