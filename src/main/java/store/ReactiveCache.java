package store;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

public class ReactiveCache {

    private static CacheStore cache = new CacheStore();

    public Observable<List<String>> getAllObservable() {
        return Observable.fromCallable(() -> cache.getAll()).subscribeOn(Schedulers.computation());
    }

    public Observable<Integer> storeObservable(Integer key, String value) {
        return Observable.fromCallable(() -> cache.store(key, value)).subscribeOn(Schedulers.computation());
    }
}
