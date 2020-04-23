package dev.hephaestus.fiblib;

import java.util.*;

// Hey look at that, I rewrote it!
public class CompositeMap<K1, K2, K3, V> {
    private final HashMap<Key<K1, K2, K3>, V> contents = new HashMap<>();
    private int size = 0;

    @SuppressWarnings("unused")
    public int size() {
        return size;
    }

    @SuppressWarnings("unused")
    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(K1 key1, K2 key2, K3 key3) {
        return containsKey(new Key<>(key1, key2, key3));
    }

    public boolean containsKey(Key<K1, K2, K3> key) {
        return contents.containsKey(key);
    }

    public V get(Key<K1, K2, K3> key) {
        return contents.get(key);
    }

    public V get(K1 key1, K2 key2, K3 key3) {
        return get(new Key<>(key1, key2, key3));
    }

    public V put(Key<K1, K2, K3> key, V value) {
        if (!contents.containsKey(key)) {
            ++size;
        }

        return contents.put(key, value);
    }

    @SuppressWarnings("UnusedReturnValue")
    public V put(K1 key1, K2 key2, K3 key3, V value) {
        return put(new Key<>(key1, key2, key3), value);
    }

    public V remove(Key<K1, K2, K3> key) {
        if (contents.containsKey(key)) {
            size--;
            return contents.remove(key);
        }

        return null;
    }

    @SuppressWarnings("unused")
    public V remove(K1 key1, K2 key2, K3 key3) {
        return remove(new Key<>(key1, key2, key3));
    }

    public int remove(K1 key1, K2 key2) {
        Key<K1, K2, K3> toRemove = new Key<>(key1, key2, null);
        Set<Key<K1, K2, K3>> remove = new HashSet<>();
        for (Key<K1, K2, K3> key : keySet()) {
            if (key.equals(toRemove))
                remove.add(key);
        }

        for (Key<K1, K2, K3> key : remove)
            remove(key);

        return remove.size();
    }

    public int remove(K1 key1) {
        return remove(key1, null);
    }

    @SuppressWarnings("unused")
    public void clear() {
        this.contents.clear();
        this.size = 0;
    }

    @SuppressWarnings("unused")
    public Set<Key<K1, K2, K3>> keySet() {
        return contents.keySet();
    }

    public Set<Map.Entry<Key<K1, K2, K3>, V>> entrySet() {
        return contents.entrySet();
    }

    public V getOrDefault(Key<K1, K2, K3> key, V defaultValue) {
        return contents.getOrDefault(key, defaultValue);
    }

    public V getOrDefault(K1 key1, K2 key2, K3 key3, V defaultValue) {
        return getOrDefault(new Key<>(key1, key2, key3), defaultValue);
    }

    public static class Key<K1, K2, K3> {
        private final K1 key1;
        private final K2 key2;
        private final K3 key3;

        public Key(K1 key1, K2 key2, K3 key3) {
            this.key1 = key1;
            this.key2 = key2;
            this.key3 = key3;
        }

        @SuppressWarnings("unused")
        public K1 getKey1() {
            return key1;
        }

        @SuppressWarnings("unused")
        public K2 getKey2() {
            return key2;
        }

        @SuppressWarnings("unused")
        public K3 getKey3() {
            return key3;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key<?, ?, ?> key = (Key<?, ?, ?>) o;
            return Objects.equals(key1, key.key1) &&
                    (Objects.equals(key2, key.key2) || key2 == null) &&
                    (Objects.equals(key3, key.key3) || key3 == null);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key1, key2, key3);
        }
    }
}
