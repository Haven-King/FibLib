package dev.hephaestus.fiblib;

import java.io.InvalidObjectException;
import java.util.*;

public class CompositeMap<V> {
    private final HashMap<Key, V> contents = new HashMap<>();
    private final Class[] keySchema;

    public CompositeMap(Class... keyTypes) {
        keySchema = new Class[keyTypes.length];
        int i = 0;
        for (Class c : keyTypes) keySchema[i++] = c;
    }

    // Backwards from normal maps so we can use VarArgs :)
    public V put(V entry, Object... keys) throws InvalidObjectException {
        Key key = new Key(keys);
        if (!key.fitsSchema(keySchema)) throw new InvalidObjectException("Illegal key: " + key.keys.toString());
        contents.put(key, entry);
        return contents.get(key);
    }

    public V put(V entry, Key key) {
        contents.put(key, entry);
        return contents.get(key);
    }

    public V get(Object... keys) throws InvalidObjectException {
        Key key = new Key(keys);
        if (!key.fitsSchema(keySchema)) throw new InvalidObjectException("Illegal key: " + key.keys.toString());
        return contents.get(key);
    }

    public V getOrDefault(V d, Object... keys) throws InvalidObjectException {
        Key key = new Key(keys);
        if (!key.fitsSchema(keySchema)) throw new InvalidObjectException("Illegal key: " + key.keys.toString());
        return contents.getOrDefault(key, d);
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

        private boolean fitsSchema(Class[] schema) {
            if (schema.length != keys.size()) return false;

            for (int i = 0; i < schema.length; ++i) {
                if (!(keys.get(i).getClass() == schema[i])) return false;
            }

            return true;
        }
    }
}
