# LinkedList vs ArrayList

Notes on how `ArrayList` and `LinkedList` compare across reading, iteration, insertion,
and memory usage.

## What to compare

1. Reading the first and last element; reading the middle element
2. Iterating with an index and with an iterator
3. Adding an element at the end; inserting at the beginning; inserting in the middle

## Access cost

| Operation             | ArrayList | LinkedList |
|-----------------------|-----------|------------|
| Reading/Adding first  | O(1)      | O(1)       |
| Reading/Adding last   | O(1)      | O(1)       |
| Reading/Adding middle | O(1)      | O(n)       |

- For `ArrayList`, reading the middle has no change because you access based on its index.
- For `LinkedList` you don't have direct access like first and last. To reach an element in
  the middle you start at the first and iterate through until you reach the index of the
  middle one. You can start from the end of the list if your element is closer, but it is
  still iterating one node to the next.
- **Pointer chasing** is the costly issue of `LinkedList` performance.

## Iteration

- `ArrayList` performance is the same whether you iterate by index or by iterator.
- `LinkedList` by index is probably the worst way to go — every time you access an element
  you traverse the list again from the start until you reach it. Use the for-each pattern
  (iterator) instead.

```java
var ints = new LinkedList<Integer>();
var list = ints.stream().toList();
for (int i = 0; i < list.size(); i++) {
    for (int v : list) {
        // do something with v
    }
}
```

Moving a `LinkedList` to an `ArrayList` is still faster for iterating after creation.
Caveat: memory usage and desync between the two copies.

## ArrayList

**Cons**

1. Adding/removing an element at a given position consists of `arrayCopy()` moving the rest
   of the array one cell over.
2. Adding an element to the end (or a given position) when the list is full requires an
   `arrayCopy()` into a larger array.
3. No mechanism to shrink an `ArrayList` automatically when you remove elements.

**Pros**

1. Good for any regular list
2. Grows automatically when it is full

The price of resizing an array varies depending on the number of elements you need to move.

## LinkedList

**Cons**

1. Memory storage is usually more than an `ArrayList` since you are storing `Node` objects
   (exception: a single-object list).

**Pros**

1. No resizing performance cost — you are just adding/removing a node.
2. Good for stack and queue implementations.
3. Carries the exact number of nodes, no empty slots.

## Notes on memory

1. If you are debugging and run into memory issues, you may be using many `ArrayList`s that
   hold 1 or 0 objects. Changing those to `LinkedList` could help — but `List.of` is only
   40 bytes compared to `ArrayList` at 76 and `LinkedList` at 56.
2. Another memory issue: an `ArrayList` automatically grew to accommodate a larger size but
   has no automatic shrink mechanism. `trimToSize()` shrinks it if you need to keep the
   `ArrayList` and save memory — but you will have to grow the list again when you add.

## Tools

- **JMH** — how to measure performance
- **JOL** — how to measure the memory an object is consuming

---
[← 01 · Java Core](01-java-core.md)
