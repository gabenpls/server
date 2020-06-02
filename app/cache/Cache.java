package cache;

import java.util.HashMap;
import java.util.Map;

public class Cache<V> {

    Map<Key, V> cacheMap = new HashMap<>();


    public void put(Key key, V value) {
        cacheMap.put(key, value);
    }

    public V get(Key key) {
        return cacheMap.get(key);
    }

    public boolean contains(Key key) {
        return cacheMap.containsKey(key);
    }


}
