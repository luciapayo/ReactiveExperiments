package experiments;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static utils.Printer.print;

public class Experiment2 extends Experiment {

    public static Experiment create() {
        return new Experiment2();
    }

    @Override
    public void run() {
        Observable.range(1, 100)
                .flatMap(i -> reactiveCache.storeObservable(i, "" + i))
                .flatMap(i -> reactiveCache.getAllObservable()
                        .doOnNext(v -> print(i + " - All values: " + v)))
                .subscribeOn(Schedulers.computation())
                .subscribe();
    }
}
