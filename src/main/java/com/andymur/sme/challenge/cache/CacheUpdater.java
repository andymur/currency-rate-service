package com.andymur.sme.challenge.cache;

/**
 * Simple interface to update the cache
 */
public interface CacheUpdater<K, V> {
    void updateEntry(K key, V value);
}
