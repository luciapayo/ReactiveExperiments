package experiments;

import com.traumtraum.adventapp2.base.arch.data.reactive_store.ReactiveStore;
import com.traumtraum.adventapp2.base.arch.data.reactive_store.ReactiveStoreImpl;
import com.traumtraum.adventapp2.base.arch.data.store.MemoryAndDiskStore;
import com.traumtraum.adventapp2.base.arch.data.store.Store;
import com.traumtraum.adventapp2.base.arch.data.store.memory_store.Cache;
import data.TimestampProvider;
import data.store.disk_store.DiskCache;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import utils.Printer;

import java.util.concurrent.TimeUnit;

public class Experiment3 extends Experiment {

    private TimestampProvider timestampProvider = new TimestampProvider();
    private Store.MemoryStore<String, Integer> memoryStore = new Cache<>(Object::toString, timestampProvider, null);
    private Store.DiskStore<String, Integer> diskStore = new DiskCache<>(Object::toString, timestampProvider, null);
    private Store<String, Integer> store = new MemoryAndDiskStore<>(memoryStore, diskStore);
    private ReactiveStore<String, Integer> reactiveStore = new ReactiveStoreImpl<>(store, Object::toString);

    public static Experiment create() {
        return new Experiment3();
    }

    @Override
    public void run() {
        reactiveStore.getSingular("2")
                .subscribe(valueList -> Printer.print(valueList.toString()));

        Observable.range(1, 10)
                .toList()
                .flatMapCompletable(value -> reactiveStore.storeAll(value))
                .subscribe();
    }
}
