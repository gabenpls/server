package cache;

public class Value<V> {
    private V Value;
    private Long expireTime;

    public Value(V value, Long TTL) {
        this.Value = value;
        this.expireTime = TTL + System.currentTimeMillis();
    }

    public boolean isAlive() {
        return this.expireTime > System.currentTimeMillis();
    }

    public V getValue() {
        return Value;
    }
}
