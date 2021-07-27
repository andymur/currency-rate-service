package com.andymur.sme.challenge.cache;

/**
 * Simple interface to fetch from the cache
 */
public interface CacheRequester<K, V> {
    V requestByKey(K key);
}
