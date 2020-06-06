package cache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class Cache<K, V> {

    private final Long TTL;
    private Map<K, Value<V>> cacheMap = new HashMap<>();

    public Cache(Long TTL) {
        this.TTL = TTL;
    }

    public void put(K key, V value) {
        cacheMap.put(key, new Value<V>(value, TTL));
    }

    private boolean contains(K key) {
        Value<V> elem = cacheMap.get(key);
        if (elem == null) {
            return false;
        } else {
            if (elem.isAlive()) {
                return true;
            } else {
                cacheMap.remove(key);
                return false;
            }
        }
    }

    public synchronized V getIfContains(K key) {
        if (contains(key)) {
            Value<V> elem = cacheMap.get(key);
            return elem == null ? null : elem.getValue();
        } else {
            return null;
        }
    }

}
