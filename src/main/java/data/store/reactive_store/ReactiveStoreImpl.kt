package com.traumtraum.adventapp2.base.arch.data.reactive_store

import com.traumtraum.adventapp2.base.arch.data.store.Store
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import utils.Printer
import java.util.*

/**
 * Created by Lucia on 11/02/2018
 */
class ReactiveStoreImpl<in Key, Value> constructor(private val store: Store<Key, Value>,
                                                   private val extractKeyFromModel: (Value) -> Key)
    : ReactiveStore<Key, Value> {

    private val allStream: Subject<Optional<List<Value>>> = PublishSubject.create<Optional<List<Value>>>()
    private val streamByKeyMap: MutableMap<Key, Subject<Optional<Value>>> = HashMap()
    private val singleThreadScheduler = Schedulers.single()

    override fun storeSingular(model: Value) =
            Completable.fromCallable { storeSingularInternal(model) }!!

    private fun storeSingularInternal(model: Value) {
        Printer.print("Storing value: " + model)
        val key = extractKeyFromModel.invoke(model)
        store.putSingular(model)
        //getOrCreateSubjectForKey(key).onNext(Optional.of(model))
        getAllFromStore().subscribe({ allStream.onNext(it) },
                { throw RuntimeException("Error propagating changes to $model", it) })
    }

    override fun storeAll(modelList: List<Value>) =
            Completable.fromCallable { storeAllInternal(modelList) }
                    .subscribeOn(singleThreadScheduler)
                    .observeOn(Schedulers.computation())!!

    private fun storeAllInternal(modelList: List<Value>) {
        store.putAll(modelList)
        allStream.onNext(Optional.of(modelList))
        // Publish in all the existing single item streams.
        // This could be improved publishing only in the items that changed. Maybe use DiffUtils?
        streamByKeyMap.forEach { publishInKey(it) }
    }

    override fun replaceAll(modelList: List<Value>) =
            Completable.fromCallable { replaceAllInternal(modelList) }
                    .subscribeOn(singleThreadScheduler)
                    .observeOn(Schedulers.computation())!!

    private fun replaceAllInternal(modelList: List<Value>) {
        store.clear()
        storeAll(modelList)
    }

    override fun getSingular(key: Key): Observable<Optional<Value>> =
            Observable.concat(Observable.defer { getSingularFromStoreObservable(key) },
                    Observable.defer { getOrCreateSubjectForKey(key) })
                    .subscribeOn(singleThreadScheduler)
                    .observeOn(Schedulers.computation())

    private fun getSingularFromStoreObservable(key: Key): Observable<Optional<Value>> =
            Observable.fromCallable { store.getSingular(key) }
                    .flatMapMaybe { it }
                    .map { Optional.of(it) }
                    .defaultIfEmpty(Optional.empty())

    override fun getAll(): Observable<Optional<List<Value>>> {
        return Observable.concat(Observable.defer { getAllFromStore() }, allStream)
    }

    private fun getAllFromStore() =
            Observable.fromCallable { store.getAll() }
                    .flatMapMaybe { it }
                    .map { Optional.of(it) }
                    .defaultIfEmpty(Optional.empty())

    private fun getOrCreateSubjectForKey(key: Key): Subject<Optional<Value>> =
            streamByKeyMap[key] ?: createAndStoreNewSubjectForKey(key)

    private fun createAndStoreNewSubjectForKey(key: Key): Subject<Optional<Value>> {
        val subject = PublishSubject.create<Optional<Value>>()
        streamByKeyMap[key] = subject
        return subject
    }

    private fun publishInKey(entry: Map.Entry<Key, Subject<Optional<Value>>>) {
        getSingularFromStoreObservable(entry.key).subscribe({ entry.value.onNext(it) },
                { throw RuntimeException("Error propagating changes to every key", it) })
    }
}