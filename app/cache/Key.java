package cache;

public class Key {

    private final Object key;
    private final Long lifeTime;

    public Object getKey() {
        return key;
    }

    public Key(Object key, Long ttl) {
        this.key = key;
        this.lifeTime = System.currentTimeMillis() + ttl;
    }


    public boolean isLive() {
        return System.currentTimeMillis() < lifeTime;
    }

    @Override
    public boolean equals(Object obj) {

        Key otherKey = (Key) obj;
        return otherKey.getKey().equals(this.getKey());
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }
}
