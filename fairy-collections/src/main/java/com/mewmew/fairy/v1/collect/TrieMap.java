/*
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
 
  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
*/
package com.mewmew.fairy.v1.collect;

import com.google.common.base.Function;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/*
 * My first stab at making a Trie
 * WARNING : this is not thread safe. Do a Collections.synchronizeMap() if necessary.
 *
 * TODO : write some unit test. 
 */
public class TrieMap<V> implements Map<String, V>
{
    final AtomicInteger size = new AtomicInteger(0);
    final TrieNode<V> root = new TrieNode<V>();

    public TrieMap()
    {
    }

    public int size()
    {
        return size.get();
    }

    public boolean isEmpty()
    {
        return size.get() == 0;
    }

    public boolean containsKey(Object key)
    {
        if (key == null) {
            return false;
        }
        String keyStr = key.toString();
        TrieNode<V> vTrieNode = root.find(keyStr);
        return vTrieNode != null && vTrieNode.getValue() != null;
    }

    public boolean containsValue(final Object value)
    {
        final AtomicBoolean bool = new AtomicBoolean(false);
        root.traverse(new Function<TrieNode<V>, Void>()
        {
            public Void apply(TrieNode<V> from)
            {
                if (value.equals(from.getValue())) {
                    bool.set(true);
                }
                return null;
            }
        });
        return bool.get();
    }

    public V get(Object key)
    {
        if (key == null) {
            return null;
        }
        String keyStr = key.toString();
        TrieNode<V> node = root.find(keyStr);
        return node.getValue();
    }

    public V put(String key, V value)
    {
        if (key == null) {
            throw new NullPointerException("key cannot be null");
        }
        TrieNode<V> node = root.putIfAbsent(key);
        try {
            return node.getValue();
        }
        finally {
            node.setValue(value);
            size.getAndIncrement();
        }
    }

    public V remove(Object key)
    {
        if (key == null) {
            return null;
        }
        String keyStr = key.toString();
        final V v = root.remove(keyStr);
        if (v != null) {
            size.getAndDecrement();
        }
        return v;
    }

    public void putAll(Map<? extends String, ? extends V> m)
    {
        for (Entry<? extends String, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public void clear()
    {
        size.set(0);
        root.clear();
    }

    public Set<String> keySet()
    {
        final AtomicReference<String> key = new AtomicReference<String>("");
        final Set<String> set = new TreeSet<String>();
        root.traverse(new Function<TrieNode<V>, Void>()
        {
            public Void apply(TrieNode<V> node)
            {
                if (node.c != null) {
                    key.set(key.get()+node.c);
                }
                if (node.getValue() != null)
                    set.add(key.get());
                return null;
            }
        }, new Function<TrieNode<V>, Void>()
        {
            public Void apply(TrieNode<V> node)
            {
                if (node.c != null) {
                    key.set(key.get().substring(0, key.get().length()-1));
                }
                return null;
            }
        });
        return set ;
    }

    public Collection<V> values()
    {
        final Set<V> set = new TreeSet<V>();
        root.traverse(new Function<TrieNode<V>, Void>()
        {
            public Void apply(TrieNode<V> node)
            {
                if (node.getValue() != null) {
                    set.add(node.getValue());
                }
                return null;
            }
        });
        return set ;
    }

    public Set<Entry<String, V>> entrySet()
    {
        final AtomicReference<String> key = new AtomicReference<String>("");
        final Set<Entry<String, V>> set = new TreeSet<Entry<String, V>>();
        root.traverse(new Function<TrieNode<V>, Void>()
        {
            public Void apply(TrieNode<V> node)
            {
                if (node.c != null) {
                    key.set(key.get()+node.c);
                }
                if (node.value != null)
                    set.add(new ComparableMapEntry<String,V>(key.get(), node.value){

                    });
                return null;
            }
        }, new Function<TrieNode<V>, Void>()
        {
            public Void apply(TrieNode<V> node)
            {
                if (node.c != null) {
                    key.set(key.get().substring(0, key.get().length()-1));
                }
                return null;
            }
        });
        return set ;
    }

}

