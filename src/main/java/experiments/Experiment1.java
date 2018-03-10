package experiments;

import io.reactivex.schedulers.Schedulers;

import static utils.Printer.print;


public class Experiment1 extends Experiment {

    public static Experiment create() {
        return new Experiment1();
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            store(i);
            getAll(i);
        }
    }

    private void store(final int i) {
        reactiveCache.storeObservable(i, "" + i)
                .subscribeOn(Schedulers.computation())
                .subscribe();
    }

    private void getAll(final int i) {
        reactiveCache.getAllObservable()
                .subscribeOn(Schedulers.computation())
                .subscribe(v -> print(i + " - All values: " + v));
    }
}
