package cache;

import java.util.HashMap;
import java.util.Map;

public class Cache<K, V> {

    private final Long cacheExpireTime;
    private Map<K, V> cacheMap = new HashMap<>();

    public Cache(Long TTL) {
        this.cacheExpireTime = TTL + System.currentTimeMillis();
    }

    public void put(K key, V value) {
        cacheMap.put(key, value);
    }

    public boolean isLive() {
        return System.currentTimeMillis() < cacheExpireTime;
    }

    public V get(K key) {
        return cacheMap.get(key);
    }

    public boolean contains(K key) {
        return isLive() && cacheMap.containsKey(key);
    }


}
