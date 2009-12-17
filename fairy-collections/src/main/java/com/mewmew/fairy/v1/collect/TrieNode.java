package com.mewmew.fairy.v1.collect;

import com.google.common.base.Function;

import java.util.Map;
import java.util.TreeMap;

class TrieNode<V>
{
    final Character c;
    volatile V value;
    
    // TODO : use sth more efficient for children
    final Map<Character, TrieNode<V>> children = new TreeMap<Character, TrieNode<V>>();

    TrieNode()
    {
        this.c = null;
    }

    TrieNode(Character c)
    {
        this.c = c;
    }

    TrieNode(String s)
    {
        if (s.isEmpty()) {
            throw new IllegalArgumentException("empty string !");
        }
        this.c = s.charAt(0);
        if (s.length() > 1) {
            children.put(s.charAt(1), new TrieNode<V>(s.substring(1)));
        }
    }

    public void clear()
    {
        children.clear();
    }

    public TrieNode<V> find(String keyStr)
    {
        return find(keyStr, null);
    }

    public TrieNode<V> find(String keyStr, Function<TrieNode<V>, Void> callback)
    {
        if (callback != null) {
            callback.apply(this);
        }
        if (keyStr.isEmpty()) {
            return this;
        }
        Character c = keyStr.charAt(0);
        TrieNode node = children.get(c);
        if (node != null) {
            return node.find(keyStr.substring(1));
        }
        return null;
    }

    public void traverse(Function<TrieNode<V>, Void> callback)
    {
        traverse(callback, null);
    }

    public void traverse(Function<TrieNode<V>, Void> pre, Function<TrieNode<V>, Void> post)
    {
        if (pre != null) {
            pre.apply(this);
        }
        for (TrieNode<V> node : children.values()) {
            node.traverse(pre, post);
        }
        if (post != null) {
            post.apply(this);
        }
    }

    public V getValue()
    {
        return value;
    }

    public void setValue(V value)
    {
        this.value = value;
    }

    public TrieNode<V> putIfAbsent(String keyStr)
    {
        if (keyStr.isEmpty()) {
            return this;
        }
        Character c = keyStr.charAt(0);
        TrieNode<V> node = children.get(c);
        if (node != null) {
            return node.putIfAbsent(keyStr.substring(1));
        }
        else {
            node = new TrieNode<V>(keyStr);
            children.put(c, node);
            return node.leaf();
        }
    }

    private TrieNode<V> leaf()
    {
        if (children.isEmpty()) {
            return this;
        }
        return children.values().iterator().next().leaf();
    }

    public V remove(String keyStr)
    {
        if (keyStr.isEmpty()) {
            return null;
        }
        TrieNode<V> node = children.get(keyStr.charAt(0));
        if (node != null) {
            if (keyStr.length() == 1) {
                try {
                    return node.value;
                }
                finally {
                    node.value = null;
                    if (node.children.size() == 0) {
                        children.remove(keyStr.charAt(0));
                    }
                }
            }
            else {
                return node.remove(keyStr.substring(1));
            }
        }
        return null;
    }

}
