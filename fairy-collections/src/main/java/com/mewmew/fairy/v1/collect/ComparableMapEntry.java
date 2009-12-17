package com.mewmew.fairy.v1.collect;

import java.util.Map;

class ComparableMapEntry<K extends Comparable,V> implements Map.Entry<K, V>, Comparable
{
    private final K key;
    private volatile V value;

    public ComparableMapEntry(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    public K getKey()
    {
        return key;
    }

    public V getValue()
    {
        return value;
    }

    public V setValue(V value)
    {
        try {
            return this.value;
        }
        finally {
            this.value = value ;
        }
    }

    public int compareTo(Object o)
    {
        if (o instanceof ComparableMapEntry) {
            return key.compareTo(((ComparableMapEntry)o).getKey()) ;
        }
        return -1;
    }
}
