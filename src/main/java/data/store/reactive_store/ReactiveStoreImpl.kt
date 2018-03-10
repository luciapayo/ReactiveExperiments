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

    private val allStream: Subject<Optional<List<Value>>> = PublishSubject.create<Optional<List<Value>>>().toSerialized()
    private val streamByKeyMap: MutableMap<Key, Subject<Optional<Value>>> = HashMap()
    private val singleThreadScheduler = Schedulers.single()

    override fun storeSingular(model: Value) =
            Completable.fromCallable { storeSingularInternal(model) }
                    .subscribeOn(singleThreadScheduler)

    private fun storeSingularInternal(model: Value) {
        val key = extractKeyFromModel.invoke(model)
        store.putSingular(model)
        getOrCreateSubjectForKey(key).onNext(Optional.of(model))
        getAllFromStore().subscribe({ allStream.onNext(it) })
    }

    override fun storeAll(modelList: List<Value>) =
            Completable.fromCallable { storeAllInternal(modelList) }
                    .subscribeOn(singleThreadScheduler)

    private fun storeAllInternal(modelList: List<Value>) {
        Printer.print("Storing model: " + modelList)
        store.putAll(modelList)
        allStream.onNext(Optional.of(modelList))
        // Publish in all the existing single item streams.
        // This could be improved publishing only in the items that changed. Maybe use DiffUtils?
        publishInEachKey()
    }

    override fun replaceAll(modelList: List<Value>) {
        store.clear()
        storeAll(modelList)
    }

    override fun getSingular(key: Key): Observable<Optional<Value>> =
            Observable.concat(Observable.defer { getSingularFromStore(key) }, Observable.defer { getOrCreateSubjectForKey(key) })
                    .observeOn(Schedulers.computation())

    override fun getAll(): Observable<Optional<List<Value>>> =
            Observable.concat(Observable.defer { getAllFromStore() }, allStream)
                    .observeOn(Schedulers.computation())

    private fun getSingularFromStore(key: Key) =
            store.getSingular(key)
                    .map { Optional.of(it) }
                    .defaultIfEmpty(Optional.empty())
                    .toObservable()
                    .subscribeOn(singleThreadScheduler)

    private fun getAllFromStore() =
            Observable.fromCallable { store.getAll() }
                    .flatMapMaybe { it }
                    .map { Optional.of(it) }
                    .defaultIfEmpty(Optional.empty())
                    .subscribeOn(singleThreadScheduler)

    private fun getOrCreateSubjectForKey(key: Key): Subject<Optional<Value>> {
        Printer.print("Get or create")
        synchronized(streamByKeyMap) {
            return streamByKeyMap[key] ?: createAndStoreNewSubjectForKey(key)
        }
    }

    private fun createAndStoreNewSubjectForKey(key: Key): Subject<Optional<Value>> {
        val subject = PublishSubject.create<Optional<Value>>().toSerialized()
        streamByKeyMap.put(key, value = subject)
        return subject
    }

    /**
     * Publishes the cached data in each independent stream only if it exists already.
     */
    private fun publishInEachKey() {
        synchronized(streamByKeyMap) {
            streamByKeyMap.forEach { publishInKey(it) }
        }
    }

    /**
    Publishes the cached value if there is an already existing stream for the passed key. The case where there isn't a stream for the passed key
    means that the data for this key is not being consumed and therefore there is no need to publish.
     */
    private fun publishInKey(entry: Map.Entry<Key, Subject<Optional<Value>>>) {
        getSingularFromStore(entry.key).subscribe { entry.value.onNext(it) }
    }
}