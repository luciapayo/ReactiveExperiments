package experiments;

import store.ReactiveCache;
import store.ReactiveDb;

public abstract class Experiment {

    ReactiveCache reactiveCache = new ReactiveCache();
    ReactiveDb reactiveDb = new ReactiveDb();

    public abstract void run();
}
