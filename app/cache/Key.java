package cache;

public class Key {

    private final Object key;

    public Object getKey() {
        return key;
    }

    public Key(Object key) {
        this.key = key;
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
