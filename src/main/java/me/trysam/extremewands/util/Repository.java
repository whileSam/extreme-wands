package me.trysam.extremewands.util;

public interface Repository<K, V> {

    void set(K key, V value);

    V get(K key);


}
