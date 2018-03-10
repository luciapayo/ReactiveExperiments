package store;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

public class ReactiveDb {

    private static CacheStore cache = new CacheStore();

    public Observable<List<String>> getAllObservable() {
        return Observable.fromCallable(() -> cache.getAll()).subscribeOn(Schedulers.io());
    }

    public Observable<Integer> storeObservable(Integer key, String value) {
        return Observable.fromCallable(() -> cache.store(key, value)).subscribeOn(Schedulers.io());
    }
}
