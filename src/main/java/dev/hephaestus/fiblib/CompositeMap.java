package dev.hephaestus.fiblib;

import java.util.*;

public class CompositeMap<V> {
    private final HashMap<Key, V> contents = new HashMap<>();

    // Backwards from normal maps so we can use VarArgs :)
    public V put(V entry, Object... keys) {
        Key key = new Key(keys);
        contents.put(key, entry);
        return contents.get(key);
    }

    public V put(V entry, Key key) {
        contents.put(key, entry);
        return contents.get(key);
    }

    public V get(Object... keys) {
        Key key = new Key(keys);
        return contents.get(key);
    }

    public V getOrDefault(V d, Object... keys) {
        return contents.getOrDefault(new Key(keys), d);
    }

    public Set<Map.Entry<Key, V>> entrySet() {
        return contents.entrySet();
    }

    public void clear() {
        this.contents.clear();
    }

    public boolean containsKey(Object... keys) {
        return contents.containsKey(new Key(keys));
    }

    public static class Key {
        private final ArrayList<Object> keys = new ArrayList<>();

        private Key(Object... keys) {
            this.keys.addAll(Arrays.asList(keys));
        }

        Object get(int index) {
            return keys.get(index);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return keys.equals(key.keys);
        }

        @Override
        public int hashCode() {
            return Objects.hash(keys);
        }
    }
}
