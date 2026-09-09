package com.monolith;

// 706. Design HashMap
import java.util.LinkedList;
import java.util.List;

class Pair<U, V> {
    U first;
    V second;

    public Pair(U first, V second) {
        this.first = first;
        this.second = second;
    }
}

//
class Bucket {
    List<Pair<Integer, Integer>> list;

    public Bucket() {
        list = new LinkedList<Pair<Integer, Integer>>();
    }

    public void put(int key, int value) {
        for (Pair<Integer, Integer> p :list) {
            if (p.first == key) {
                p.second = value;
                return;
            }
        }

        list.addFirst(new Pair<Integer, Integer>(key, value));
    }

    public Integer get(int key) {
        for (Pair<Integer, Integer> p :list) {
            if (p.first == key) {
                return p.second;
            }
        }
        return null;
    }

    public void remove(int key) {
        for (Pair<Integer, Integer> p :list) {
            if (p.first == key) {
                list.remove(p);
                return;
            }
        }
    }
}

public class MyHashMap {
    Bucket[] buckets;
    int keyRange = 769;

    public MyHashMap() {
        buckets = new Bucket[769];
        for (int i = 0; i < keyRange; i++) {
            this.buckets[i] = new Bucket();
        }
    }

    public int getBucketIndex(int key) {
//        floorMod keeps the index non-negative. Plain % takes the sign of the key,
//        so a negative key would index outside the array
        return Math.floorMod(key, keyRange);
    }

    public void put(int key, int value) {
        int bucketIdx = this.getBucketIndex(key);
        this.buckets[bucketIdx].put(key, value);
    }

//    -1 is the LeetCode 706 contract for a missing key, so the sentinel only
//    lives here at the boundary. Bucket.get() itself returns null for absent
    public int get(int key) {
        Integer v = buckets[getBucketIndex(key)].get(key);
        return v == null ? -1 : v;
    }

//    Lets a caller tell "stored -1" apart from "not present", the same reason
//    java.util.HashMap has containsKey alongside get
    public boolean containsKey(int key) {
        return buckets[getBucketIndex(key)].get(key) != null;
    }

    public void remove(int key) {
        int bucketIdx = this.getBucketIndex(key);
        this.buckets[bucketIdx].remove(key);
    }
}
