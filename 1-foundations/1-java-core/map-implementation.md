# Map Implementations

Notes for **1.2 Map implementations**. Code quotes are from the JDK 21 `src.zip`
(`java.base/java/util/HashMap.java`) — the constants and logic here are unchanged since
Java 8.

---

## 1. HashMap: buckets and hashing

A `HashMap` is an array (the **table**) of **bins**. Each slot holds either `null`, a single
`Node`, a short linked list of `Node`s, or — past a threshold — a red-black tree of
`TreeNode`s.

```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;   // cached, never recomputed
    final K key;
    V value;
    Node<K,V> next;   // chain within the bin
}
```

The `hash` is cached on the node. `key.hashCode()` is called once at insert, never again
during resize or lookup — that's why resizing is cheap-ish and why a slow `hashCode()` hurts
you only at write time.

### Hash spreading

```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

The bin index is `(n - 1) & hash`, where `n` is the table length — a power of two, so the
mask is just the low bits. That masking throws away every high bit. XOR-ing the top 16 bits
down into the bottom 16 ("spreading") folds the high bits back in so that keys differing only
in their upper bits don't all collide.

Classic example: `Integer.hashCode()` returns the value itself. Without spreading, keys
`0x00000000`, `0x00010000`, `0x00020000` … all land in bin 0 for any table smaller than
65536.

Note `null` hashes to 0 → always bin 0. `HashMap` permits one `null` key.

### Lookup

1. `hash(key)` → spread hash
2. `(n - 1) & hash` → bin index
3. Walk the bin: compare `hash` first (cheap `int` compare), then `==`, then `.equals()`

The hash compare short-circuits most `equals()` calls. Two keys in the same bin usually have
different full hashes — they only collided on the low bits.

---

## 2. Load factor and resize cost

```java
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // 16
static final float DEFAULT_LOAD_FACTOR = 0.75f;
static final int MAXIMUM_CAPACITY = 1 << 30;
```

`threshold = capacity * loadFactor`. When `++size > threshold`, `resize()` runs: capacity
doubles, threshold doubles, and **every entry is redistributed**.

0.75 is the space/time tradeoff: lower means more empty slots but shorter chains, higher
means denser table but more collisions. The class javadoc has the math — under random hash
codes, bin occupancy follows a Poisson distribution with λ ≈ 0.5 at load factor 0.75:

```
0:    0.60653066
1:    0.30326533
2:    0.07581633
3:    0.01263606
...
8:    0.00000006
more: less than 1 in ten million
```

That's the justification for `TREEIFY_THRESHOLD = 8` — with a decent `hashCode()`, a bin of
8 should essentially never happen, so treeification is an insurance policy against bad hash
functions (or hostile input), not a normal code path.

### The power-of-two split trick

Because capacity always doubles, an entry either stays at index `j` or moves to
`j + oldCap` — decided by a single bit:

```java
do {
    next = e.next;
    if ((e.hash & oldCap) == 0) {   // the new high bit is 0 -> stay
        ... append to lo list
    } else {                        // -> move to j + oldCap
        ... append to hi list
    }
} while ((e = next) != null);
if (loTail != null) { loTail.next = null; newTab[j] = loHead; }
if (hiTail != null) { hiTail.next = null; newTab[j + oldCap] = hiHead; }
```

No rehashing, no modulo, no per-entry index recomputation — one AND per entry. It also
**preserves relative order** within a bin. (Java 7 reversed the list during transfer, which
is what made concurrent `put` able to create a cycle and spin a CPU forever in `get()`.
Java 8 fixed the ordering; it is still not thread-safe — use `ConcurrentHashMap`.)

### Cost

Resize is O(n) in the number of entries. Amortized over the doubling it's O(1) per insert,
but it's a **latency spike**, not a smooth cost: growing a map to 1M entries resizes ~16
times and re-links every entry each time.

**Sizing:** `new HashMap<>(expectedSize / 0.75f + 1)` avoids resizes. The constructor argument
is a *capacity*, not a size — it is rounded up to a power of two by `tableSizeFor()` and
stashed in `threshold` until the table is first allocated (allocation is lazy; a `HashMap`
you never write to has a `null` table).

---

## 3. Treeification

```java
static final int TREEIFY_THRESHOLD = 8;
static final int UNTREEIFY_THRESHOLD = 6;
static final int MIN_TREEIFY_CAPACITY = 64;
```

A long bin degrades lookup to O(n). Past the threshold, `HashMap` converts that bin into a
**red-black tree**, making worst-case lookup O(log n).

### The trigger

In `putVal`, after appending to the end of a bin:

```java
for (int binCount = 0; ; ++binCount) {
    if ((e = p.next) == null) {
        p.next = newNode(hash, key, value, null);
        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
            treeifyBin(tab, hash);
        break;
    }
    ...
    p = e;
}
```

Worth tracing precisely, because it is easy to state wrong: `p` starts at the bin's **head**,
which was already checked before the loop, so `binCount` counts the nodes walked *after* the
head — hence the `-1 for 1st` comment. `binCount` reaches 7 only when 8 nodes already exist,
and the append makes it **9 nodes in the bin at the moment `treeifyBin` is called**.

### The capacity guard

```java
final void treeifyBin(Node<K,V>[] tab, int hash) {
    int n, index; Node<K,V> e;
    if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
        resize();
    else if ((e = tab[index = (n - 1) & hash]) != null) {
        // convert the whole bin to TreeNodes, then hd.treeify(tab)
    }
}
```

This is the detail most write-ups miss. **A long bin in a small table is treated as a sizing
problem, not a collision problem.** Below capacity 64, `treeifyBin` resizes instead — the
extra bit of table index will likely split the crowded bin anyway. So a bin can hold far more
than 8 nodes and never be treeified, as long as the table is small.

`MIN_TREEIFY_CAPACITY` is 64 = `4 * TREEIFY_THRESHOLD`, chosen so resize thresholds and
treeification thresholds don't fight each other.

### Ordering inside the tree

A red-black tree needs a total order, but map keys are not required to be `Comparable`.
`TreeNode` orders by, in sequence:

1. `hash` (the spread hash)
2. if hashes tie and the key's class "implements `Comparable<C>` for its own class C"
   (`comparableClassFor`), `compareTo`
3. otherwise `tieBreakOrder`:

```java
static int tieBreakOrder(Object a, Object b) {
    int d;
    if (a == null || b == null ||
        (d = a.getClass().getName().compareTo(b.getClass().getName())) == 0)
        d = (System.identityHashCode(a) <= System.identityHashCode(b) ? -1 : 1);
    return d;
}
```

That last resort is arbitrary but consistent — enough to keep the tree balanced. Correctness
still comes from `equals()`; the ordering only steers the search. Key consequence: **making
your keys `Comparable` makes degenerate (all-same-hash) bins genuinely O(log n)**; otherwise
the tree still has to search both sides on hash ties.

### Untreeifying at 6

Two paths back to a linked list:

**During resize**, `split()` counts each half as it relinks:

```java
if (loHead != null) {
    if (lc <= UNTREEIFY_THRESHOLD)
        tab[index] = loHead.untreeify(map);
    else { ... loHead.treeify(tab); }
}
```

**During removal**, `removeTreeNode` does *not* count — it uses a structural shape test:

```java
if (root == null
    || (movable
        && (root.right == null
            || (rl = root.left) == null
            || rl.left == null))) {
    tab[index] = first.untreeify(map);  // too small
    return;
}
```

A tree that shallow holds roughly 2–6 nodes. Cheaper than maintaining a count.

The gap between 8 and 6 is **hysteresis**: if both thresholds were 8, a map sitting exactly at
the boundary would convert back and forth on every put/remove. The two-node gap makes
thrashing impossible.

Also note `TreeNode` is about twice the size of `Node` (parent, left, right, prev, red) —
that memory cost is exactly why treeification is opt-in at 8 rather than always-on.

---

## 4. TreeMap

A red-black tree of the whole map — no buckets, no hashing, no load factor, no resize.

- **Ordering:** sorted by natural key order (`Comparable`) or by a `Comparator` given to the
  constructor.
- **Cost:** `get`/`put`/`remove` are O(log n) — always, no best/worst distinction.
- **Identity comes from `compareTo`, not `equals`.** Two keys are "the same key" iff
  `compare(a, b) == 0`. If your `compareTo` is inconsistent with `equals`, `TreeMap` behaves
  correctly per the `SortedMap` contract but violates the `Map` contract. (Effective Java
  item 14: the classic offender is `BigDecimal` — `new BigDecimal("1.0")` and
  `new BigDecimal("1.00")` are not `equals` but do compare equal, so a `HashSet` keeps both
  and a `TreeSet` keeps one.)
- **No `null` keys** (it has to call `compareTo` on them) — unlike `HashMap`.
- **Extra API:** it implements `NavigableMap`, which is the actual reason to reach for it —
  `firstKey`/`lastKey`, `floorKey`/`ceilingKey`, `higherKey`/`lowerKey`, `headMap`/`tailMap`/
  `subMap`, `descendingMap`. Range queries and "nearest key" lookups a `HashMap` can't do.

Use it when you need order or range queries. Otherwise `HashMap` wins on constant factors.

---

## 5. LinkedHashMap

`extends HashMap` — same table, same bins, same treeification — plus a **doubly-linked list**
threaded through all entries. Its `Entry` adds `before`/`after` pointers, and it overrides the
hooks `HashMap` leaves for it (`newNode`, `afterNodeAccess`, `afterNodeInsertion`,
`afterNodeRemoval`).

- **Ordering:** *insertion order* by default. Re-putting an existing key does **not** move it;
  removing and re-putting does.
- **Access order:** the 3-arg constructor `LinkedHashMap<>(capacity, loadFactor, true)` moves
  an entry to the end on every `get` and `put`. That plus `removeEldestEntry` gives you an LRU
  cache in a few lines:

```java
new LinkedHashMap<K,V>(16, 0.75f, true) {
    @Override protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return size() > MAX_ENTRIES;
    }
};
```

- **Iteration is O(size)**, not O(capacity + size). Plain `HashMap` iteration walks the whole
  table including empty bins, so a large, sparse `HashMap` iterates slowly;
  `LinkedHashMap` just follows the chain.
- **Cost:** the ordering guarantee is nearly free at lookup time; you pay ~2 extra references
  per entry in memory and a few pointer writes per mutation.

---

## 6. Side by side

| | HashMap | LinkedHashMap | TreeMap |
|---|---|---|---|
| Structure | table of bins | table + doubly-linked list | red-black tree |
| get/put/remove | O(1) avg, O(log n) worst (treeified) | O(1) avg | O(log n) |
| Iteration order | none — and not stable across resizes | insertion or access order | sorted by key |
| Iteration cost | O(capacity + size) | O(size) | O(size) |
| `null` key | one allowed | one allowed | not allowed |
| Key identity via | `hashCode` + `equals` | `hashCode` + `equals` | `compareTo` / `Comparator` |
| Memory per entry | lowest | + 2 refs | + 3 refs and a color |
| Reach for it when | default | order matters, LRU caches | sorted/range/nearest-key queries |

---

## 7. Effective Java items 10–14, as they apply to keys

These items are on this reading list because **a map key is exactly where a broken `equals`
or `hashCode` blows up.**

- **Item 10 — obey the `equals` contract.** Reflexive, symmetric, transitive, consistent, and
  `x.equals(null)` is false. Symmetry breaks most often when a subclass "extends" equality; a
  broken contract means `map.get(k)` fails to find an entry you know is there.
- **Item 11 — always override `hashCode` when you override `equals`.** Equal objects must have
  equal hashes, or the two objects land in different bins and the map never compares them.
  This is the single most common map bug. Unequal objects *may* share a hash (legal, just
  slow) — but a `hashCode()` returning a constant turns every lookup into a bin scan, and
  post-Java-8 a treeified bin scan.
- **Item 12 — always override `toString`.** Not correctness; it's what makes a debugger view
  of a map readable when you're chasing a lookup that misses.
- **Item 13 — override `clone` judiciously.** Prefer copy constructors/factories. Relevant
  here because a shallow copy shares mutable key objects between two maps.
- **Item 14 — consider implementing `Comparable`.** Required for `TreeMap` keys; and as
  above, it's what makes treeified `HashMap` bins truly O(log n). Keep `compareTo` consistent
  with `equals`, and note the exception when you can't.

### The mutable key trap

The hash is cached at insert. If you mutate a key after putting it in a `HashMap`, its hash no
longer matches the bin it lives in — the entry becomes unreachable by `get`, but still shows
up in iteration and still counts toward `size()`. Same for `TreeMap` if the mutation changes
its ordering. **Keys should be immutable**, or at least never mutated in a field used by
`equals`/`hashCode`/`compareTo`.

---

## 8. Reading checklist

Read in `HashMap.java` (JDK 21 path:
`/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/lib/src.zip` →
`java.base/java/util/HashMap.java`):

1. The class-level javadoc, ~lines 140–230 — the design rationale and the Poisson table.
2. `hash()` — spreading.
3. `putVal()` — the bin walk and the `binCount >= TREEIFY_THRESHOLD - 1` check.
4. `resize()` — the lo/hi split on `(e.hash & oldCap)`.
5. `treeifyBin()` — the `MIN_TREEIFY_CAPACITY` guard that resizes instead.
6. `TreeNode.split()` and `TreeNode.removeTreeNode()` — the two ways back to a list.
7. `comparableClassFor()` and `tieBreakOrder()` — ordering keys that aren't `Comparable`.

Questions to be able to answer afterwards:

- Why is capacity always a power of two?
- Why does a bin of 20 nodes in a table of capacity 32 stay a linked list?
- How many nodes are in the bin when `treeifyBin` is actually called, and why isn't it 8?
- Why is `UNTREEIFY_THRESHOLD` 6 rather than 8?
- Why does an entry only ever move to `j` or `j + oldCap` on resize?
- What breaks if a key's `hashCode` changes after insertion?

Measure with **JMH** (throughput) and **JOL** (object layout / retained size) — the same
tools from the ArrayList/LinkedList notes. JOL makes the `Node` vs `TreeNode` size difference
concrete.
