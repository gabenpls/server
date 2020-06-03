package cache;

import java.util.HashMap;
import java.util.Map;

public class Cache<K, V> {

    private final Long TTL;
    private Map<Key, V> cacheMap = new HashMap<>();

    public Cache(Long TTL) {
        this.TTL = TTL;
    }

    public void put(K key, V value) {
        cacheMap.put(new Key(key, TTL + System.currentTimeMillis()), value);
    }

    public boolean contains(K key) {
        Long expTime = 0L;
        Key correctKey = new Key(1, 1L);
        for (Key k : cacheMap.keySet()) {
            if (k.isCorrect(key)) {
                correctKey = k;
                System.out.println("expT " + correctKey.getExpiredTime());
            }
        }
        System.out.println(cacheMap.containsKey(correctKey));
        return System.currentTimeMillis() < correctKey.getExpiredTime() && cacheMap.containsKey(correctKey);
    }


    public V get(K key) {
        return cacheMap.get(key);
    }

}
