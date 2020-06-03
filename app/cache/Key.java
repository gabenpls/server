package cache;

public class Key {

    private final Object key;
    private final Long expiredTime;

    public Object getKey() {
        return key;
    }

    public Long getExpiredTime() {
        return expiredTime;
    }

    public Key(Object key, Long expiredTime) {
        this.key = key;
        this.expiredTime = expiredTime;
    }

    public Long findExpireTime(Object key) {
        if (this.getKey().equals(key)) {
            return this.getExpiredTime();
        } else return 1L;
    }

    public boolean isCorrect(Object key) {
        if (this.getKey().equals(key)) {
            return true;
        } else return false;
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
