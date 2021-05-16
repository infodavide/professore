package org.infodavid.util.collection;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * The Class EterogenicMap.
 */
public class EterogenicMap<K,V> implements Map<K,V> {

    /** The delegate. */
    private Map<K,V> delegate;

    /**
     * Instantiates a new map.
     */
    public EterogenicMap() {
        super();

        delegate = new HashMap<>();
    }

    /**
     * Instantiates a new map.
     * @param delegate the delegate
     */
    public EterogenicMap(final Map<K,V> delegate) {
        super();

        this.delegate = delegate;
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#clear()
     */
    @Override
    public void clear() {
        delegate.clear();
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#compute(java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    public V compute(final K key, final BiFunction<? super K,? super V,? extends V> remappingFunction) {
        return delegate.compute(key, remappingFunction);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#computeIfAbsent(java.lang.Object, java.util.function.Function)
     */
    @Override
    public V computeIfAbsent(final K key, final Function<? super K,? extends V> mappingFunction) {
        return delegate.computeIfAbsent(key, mappingFunction);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#computeIfPresent(java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    public V computeIfPresent(final K key, final BiFunction<? super K,? super V,? extends V> remappingFunction) {
        return delegate.computeIfPresent(key, remappingFunction);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#containsKey(java.lang.Object)
     */
    @Override
    public boolean containsKey(final Object key) {
        return delegate.containsKey(key);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(final Object value) {
        return delegate.containsValue(value);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#entrySet()
     */
    @Override
    public Set<Entry<K,V>> entrySet() {
        return delegate.entrySet();
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        return delegate.equals(o);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#forEach(java.util.function.BiConsumer)
     */
    @Override
    public void forEach(final BiConsumer<? super K,? super V> action) {
        delegate.forEach(action);
    }

    /**
     * For each.
     * @param <T> the generic type
     * @param action the action
     * @param clazz the clazz
     * @see java.util.Map#forEach(java.util.function.BiConsumer)
     */
    public <T> void forEach(final BiConsumer<? super K,? super V> action, final Class<T> clazz) {
        Objects.requireNonNull(action);

        for (final Entry<K,V> entry : delegate.entrySet()) {
            K k;
            V v;

            try {

                k = entry.getKey();
                v = entry.getValue();
            }
            catch (final IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }

            if (clazz.isInstance(v)) {
                action.accept(k, v);
            }
        }
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#get(java.lang.Object)
     */
    @Override
    public V get(final Object key) {
        return delegate.get(key);
    }

    /**
     * Gets the.
     * @param <T> the generic type
     * @param key the key
     * @param clazz the clazz
     * @return the t
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final Object key, final Class<T> clazz) {
        return (T)delegate.get(key);
    }

    /**
     * Gets the delegate.
     * @return the delegate
     */
    public Map<K,V> getDelegate() {
        return delegate;
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#getOrDefault(java.lang.Object, java.lang.Object)
     */
    @Override
    public V getOrDefault(final Object key, final V defaultValue) {
        return delegate.getOrDefault(key, defaultValue);
    }

    /*
     * (non-javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#isEmpty()
     */
    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#keySet()
     */
    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#merge(java.lang.Object, java.lang.Object, java.util.function.BiFunction)
     */
    @Override
    public V merge(final K key, final V value, final BiFunction<? super V,? super V,? extends V> remappingFunction) {
        return delegate.merge(key, value, remappingFunction);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public V put(final K key, final V value) {
        return delegate.put(key, value);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#putAll(java.util.Map)
     */
    @Override
    public void putAll(final Map<? extends K,? extends V> m) {
        delegate.putAll(m);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#putIfAbsent(java.lang.Object, java.lang.Object)
     */
    @Override
    public V putIfAbsent(final K key, final V value) {
        return delegate.putIfAbsent(key, value);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#remove(java.lang.Object)
     */
    @Override
    public V remove(final Object key) {
        return delegate.remove(key);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#remove(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean remove(final Object key, final Object value) {
        return delegate.remove(key, value);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#replace(java.lang.Object, java.lang.Object)
     */
    @Override
    public V replace(final K key, final V value) {
        return delegate.replace(key, value);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#replace(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean replace(final K key, final V oldValue, final V newValue) {
        return delegate.replace(key, oldValue, newValue);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#replaceAll(java.util.function.BiFunction)
     */
    @Override
    public void replaceAll(final BiFunction<? super K,? super V,? extends V> function) {
        delegate.replaceAll(function);
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#size()
     */
    @Override
    public int size() {
        return delegate.size();
    }

    /*
     * (non-javadoc)
     * @see java.util.Map#values()
     */
    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    /**
     * Sets the delegate.
     * @param delegate the delegate to set
     */
    protected void setDelegate(final Map<K,V> delegate) {
        this.delegate = delegate;
    }
}
