package com.traumtraum.adventapp2.base.arch.data.store.memory_store

/**
 * Cache entry that contains the object and the creation timestamp.
 */
data class CacheEntry<out T> (val cachedObject: T, val creationTimestamp: Long)