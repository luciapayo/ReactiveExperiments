package com.traumtraum.adventapp2.base.arch.data.reactive_store

import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

/**
 * Created by luciapayo on 13/11/2017
 */
interface ReactiveStore<in Key, Value> {

    fun storeSingular(model: Value): Completable

    fun storeAll(modelList: List<Value>): Completable

    fun replaceAll(modelList: List<Value>)

    fun getSingular(key: Key): Observable<Optional<Value>>

    fun getAll(): Observable<Optional<List<Value>>>
}
