package com.traumtraum.adventapp2.base.arch.data.store.memory_store

import com.traumtraum.adventapp2.base.arch.data.store.Store.MemoryStore
import com.traumtraum.adventapp2.base.extensions.filterSingular
import data.TimestampProvider
import io.reactivex.Maybe
import io.reactivex.Single
import java.util.concurrent.ConcurrentHashMap

/**
 * Generic memory cache with timeout for the entries.
 */
class Cache<in Key, Value> constructor(private val extractKeyFromModel: (Value) -> Key,
                                       private val timestampProvider: TimestampProvider,
                                       private val itemLifespanMs: Long? = null)
    : MemoryStore<Key, Value> {

    private val cache: LinkedHashMap<Key, CacheEntry<Value>> = LinkedHashMap()

    override fun putSingular(value: Value) {
        val key = extractKeyFromModel.invoke(value)
        cache.put(key, CacheEntry(value, timestampProvider.currentTimeMillis()))
    }

    override fun putAll(valueList: List<Value>) {
        val valueMap = valueList.associateBy(extractKeyFromModel,
                                             { CacheEntry(it, timestampProvider.currentTimeMillis()) })
        cache.putAll(valueMap)
    }

    override fun clear() {
        cache.clear()
    }

    override fun getSingular(key: Key): Maybe<Value> =
        cache[key]
            ?.filterSingular { it.hasNotExpired() }
            ?.cachedObject
            ?.let { Maybe.just(it) }
            ?: Maybe.empty()

    override fun getAll(): Maybe<List<Value>> {
        val valueList = cache.values
            .filter { it.hasNotExpired() }
            .map { it.cachedObject }

        return Maybe.just(valueList)
            .filter { it.isNotEmpty() }
    }

    private fun <T> CacheEntry<T>.hasNotExpired(): Boolean =
        itemLifespanMs
            ?.let { this.creationTimestamp + it > timestampProvider.currentTimeMillis() }
            ?: true
}