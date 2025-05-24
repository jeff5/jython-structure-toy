package example.runtime;

import java.lang.invoke.MethodHandles;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

public class PyDict extends AbstractMap<Object, Object>
        implements WithType {

    /** The dictionary as a hash map preserving insertion order. */
    private final LinkedHashMap<Object, Object> map =
            new LinkedHashMap<Object, Object>();

    // slot functions -------------------------------------------------

    @SuppressWarnings("unused")
    private Object __str__() throws Throwable { return mapRepr(this); }

    private static String mapRepr(Map<? extends Object, ?> map)
            throws Throwable {
        StringJoiner sj = new StringJoiner(", ", "{", "}");
        for (Map.Entry<? extends Object, ?> e : map.entrySet()) {
            String key = e.getKey().toString();
            String value = e.getValue().toString();
            sj.add(key + ": " + value);
        }
        return sj.toString();
    }

    // plumbing -------------------------------------------------------

    @Override
    public PyType getType() { return TYPE; }

    public static final PyType TYPE = PythonRuntime.typeFactory
            .register("dict", MethodHandles.lookup());

    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return new EntrySetImpl();
    }

    /**
     * Override {@code Map.get} to get from inner map.
     *
     * @param key whose associated value is to be returned
     * @return value at {@code key} or {@code null} if not found
     */
    @Override
    public Object get(Object key) { return map.get(key); }

    /**
     * Override {@code Map.put} to save in inner map.
     *
     * @param key with which the specified value is to be associated
     * @param value to be associated
     * @return previous value associated
     */
    @Override
    public Object put(Object key, Object value) {
        return map.put(key, value);
    }

    /**
     * Override {@code Map.remove} to delete from inner map.
     *
     * @param key for which the entry is to be removed
     * @return previous value associated
     */
    @Override
    public Object remove(Object key) { return map.remove(key); }

    /**
     * An instance of this class is returned by
     * {@link PyDict#entrySet()}, and provides the view of the entries
     * in the {@code PyDict} mentioned there.
     */
    private class EntrySetImpl
            extends AbstractSet<Entry<Object, Object>> {

        @Override
        public Iterator<Entry<Object, Object>> iterator() {
            return new EntrySetIteratorImpl();
        }

        @Override
        public int size() { return map.size(); }
    }

    /**
     * An instance of this class is returned by
     * {@link EntrySetImpl#iterator()}. It is backed by an iterator on
     * the underlying {@link #map}.
     */
    private class EntrySetIteratorImpl
            implements Iterator<Entry<Object, Object>> {

        /** Backing iterator on the "real" implementation. */
        private final Iterator<Entry<Object, Object>> mapIterator =
                map.entrySet().iterator();

        @Override
        public boolean hasNext() { return mapIterator.hasNext(); }

        @Override
        public Entry<Object, Object> next() {
            Entry<Object, Object> e = mapIterator.next();
            return new SimpleEntry<Object, Object>(e.getKey(),
                    e.getValue());
        }

        @Override
        public void remove() { mapIterator.remove(); }
    }
}
