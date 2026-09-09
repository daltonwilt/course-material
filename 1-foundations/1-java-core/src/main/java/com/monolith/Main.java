package com.monolith;

import java.util.HashMap;

public class Main {
    static void main() {

//        ---- 1. Basic put / get ----
        var map = new MyHashMap();
        map.put(1, 100);
        map.put(2, 200);
        System.out.println("get(1) = " + map.get(1));   // 100
        System.out.println("get(2) = " + map.get(2));   // 200

//        ---- 2. Missing key returns the -1 sentinel ----
        System.out.println("get(3) = " + map.get(3));   // -1, key was never put

//        ---- 3. Overwriting an existing key ----
//        Bucket.put() walks the chain first, so a duplicate key updates in place
//        instead of adding a second Pair
        map.put(1, 999);
        System.out.println("get(1) after overwrite = " + map.get(1));   // 999

//        ---- 4. Remove ----
        map.remove(2);
        System.out.println("get(2) after remove = " + map.get(2));      // -1

//        ---- 5. Collisions and chaining ----
//        getBucketIndex() is key % 769, so any two keys that differ by a
//        multiple of 769 land in the SAME bucket
        int a = 5;
        int b = 5 + 769;      // 774
        int c = 5 + 769 * 2;  // 1543
        System.out.println("bucket for " + a + " = " + map.getBucketIndex(a));
        System.out.println("bucket for " + b + " = " + map.getBucketIndex(b));
        System.out.println("bucket for " + c + " = " + map.getBucketIndex(c));

        map.put(a, 50);
        map.put(b, 60);
        map.put(c, 70);
//        All three share one bucket, but the LinkedList chain keeps them distinct.
//        This is the O(n) bin walk from the notes - java.util.HashMap would treeify
//        this bin once it got long enough
        System.out.println("get(" + a + ") = " + map.get(a));   // 50
        System.out.println("get(" + b + ") = " + map.get(b));   // 60
        System.out.println("get(" + c + ") = " + map.get(c));   // 70
}
