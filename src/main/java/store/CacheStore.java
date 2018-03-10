package store;

import utils.Printer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheStore {
    private Map<Integer, String> cache = new ConcurrentHashMap<>();

    public Integer store(Integer key, String value) {
        Printer.print(key + "- store");
        Object o = new Object();
        int i = 5 + 8;
        cache.put(key, value);
        return key;
    }

    public String get(Integer key) {
        return cache.get(key);
    }

    public List<String> getAll() {
        ArrayList<String> values = new ArrayList<>();
        for (Integer key : cache.keySet()) {
            values.add(get(key));
        }

        return values;
    }
}
