# 01 · Java Core
**Level 1** · **Topics:** Collections · Streams  
**Code:** [java-course/01-java-core](https://github.com/daltonwilt/java-course/tree/main/01-java-core)

**Budget:** ~32 hours. Three weeks at 10–12 hrs/week.
**Study order:** 1.1 → 1.3 → 1.2 → 1.5 → 1.4 → 1.6 → 1.7 (not numeric order — see note)

---

## Setup (2 hrs, do this first)

- **JDK 25** (current LTS). Several lessons need `--enable-preview`.
- **JMH** — add to a scratch Maven/Gradle project now. Four of seven Do exercises are benchmarks, and hand-rolled timing loops will lie to you.
- **JShell** — for 1.4 and 1.6. Faster feedback than a full compile cycle.
- **JDK source attached in your IDE.** Non-optional for 1.2. Verify you can ctrl-click into `HashMap.put()` and land on real code.
- **VisualVM or JFR** — for the memory measurements in 1.1.

*Checkpoint: run one trivial JMH benchmark end to end before starting.*

---

## Week 1 — Collections and contracts (12 hrs)

### 1.1 List implementations (3 hrs)
📺 [ArrayList vs. LinkedList — JEP Café #20](https://www.youtube.com/watch?v=ul4wHrbJ8Fk)
📖 `ArrayList` and `LinkedList` javadoc; Effective Java item 26 (raw types) as background
🔧 JMH + JOL (Java Object Layout) for the memory measurement

*Do: benchmark where LinkedList actually beats ArrayList. Note how narrow the window is.*
*Focus: cache locality is the real story, not Big-O. Paumard shows this on screen.*

### 1.3 equals, hashCode, comparable (4 hrs)
📺 [Optimizing your equals() methods — JEP Café #21](https://www.youtube.com/watch?v=kuzjX_efuDs)
📺 [Records: Patterns, Performance, Serialization — JEP Café #8](https://www.youtube.com/watch?v=1oC9ESbyvqs)
📖 Effective Java items 10–14 (the canonical treatment — read all five)

*Do: build a class that violates the hashCode contract and demonstrate the object getting lost in a HashSet.*

### 1.2 Map implementations (5 hrs)
📖 JDK source: `HashMap.treeifyBin()`, `HashMap.resize()`, `HashMap.hash()`. Read the class-level comment first — it explains the design rationale.
📖 [Deepak Vadgama — HashMap internals](https://deepakvadgama.com/blog/java-hashmap-internals/) for a guided tour of the same code
📖 Effective Java item 11 revisited, now with the implementation in view

*Do: force a bucket past 8 entries with a deliberately bad hashCode. Observe the O(n) → O(log n) transition.*
*Key constants: TREEIFY_THRESHOLD 8, UNTREEIFY_THRESHOLD 6, MIN_TREEIFY_CAPACITY 64. Understand why the third exists — it's the one people miss.*

---

## Week 2 — Type system and streams (11 hrs)

### 1.5 Generics and type erasure (5 hrs)
📺 [Java Generics Finally Explained — Geekific](https://www.youtube.com/watch?v=NAZkukK5zX0) (wildcards and PECS; stops short of the rest)
📖 [dev.java Generics tutorial](https://dev.java/learn/generics/) — the official sequence
📖 Effective Java items 26–33 — heap pollution, `@SafeVarargs`, bounded wildcards. This is where the video's gap gets filled.

*Do: write a generic repository interface with bounded types that compiles cleanly with no raw-type warnings.*
*Stretch: decompile a generic class with `javap -c` and find the bridge method.*

### 1.4 Streams (6 hrs)
📺 [Exploring Collectors — Venkat Subramaniam](https://www.youtube.com/watch?v=z1eaTv_FASg) + [companion JShell gist](https://gist.github.com/nicokosi/d65f6a880587d085d844806368e03815)
📺 [Deep Dive into Gatherers — JEP Café #24](https://www.youtube.com/watch?v=fgQQIV3B-uo)
📖 Effective Java items 42–48 (especially 48: parallel streams, and when not to)
📖 [dev.java Streams tutorial](https://dev.java/learn/api/streams/)

*Do: rewrite three nested loops from an old project as a stream pipeline. Then benchmark both.*
*Expect the stream version to sometimes lose. That result is the lesson — write down why.*

---

## Week 3 — Modern Java and error handling (7 hrs)

### 1.6 Modern Java (4 hrs)
📺 [Pattern Matching full tutorial — JEP Café #14](https://www.youtube.com/watch?v=aKaw9W789wU)
📺 [Data-Oriented Programming in Java 21 — JEP Café #22](https://www.youtube.com/watch?v=Y2pmZlP-cOU)
📖 JEP 395 (records), JEP 409 (sealed classes), JEP 441 (pattern matching for switch) — read the Motivation sections only
⚠️ Effective Java 3rd ed. predates all of this. Don't look for it there.

*Do: model a payment result as a sealed interface with record implementations and exhaustive switch handling.*

### 1.7 Exception design (3 hrs)
📖 Effective Java items 69–77 — the whole chapter, it's short
📖 `try`-with-resources: JLS 14.20.3, or the dev.java exceptions tutorial

*Do: audit a codebase you own for empty catch blocks. Count them.*
*The count is the point. Write it down and fix the three worst.*

---

## Consolidation exercise (optional, 3 hrs)

Ties five lessons together and doubles as portfolio material:

Build an in-memory `Ledger` type. Accounts as records with correct `equals`/`hashCode`. Entries stored in a `HashMap` keyed by account. A sealed `PostingResult` interface with record variants, consumed by exhaustive switch. A stream pipeline computing balances per account. Custom exceptions for the failure paths, no empty catch blocks.

This is a scaled-down version of node 9's project. Doing it now makes node 9 faster.

---

## Resources worth owning

| Resource | Covers | Notes |
|---|---|---|
| **Effective Java, 3rd ed.** (Bloch) | 1.3, 1.5, 1.7, parts of 1.4 | The single highest-value purchase for this node. Stops at Java 9. |
| **dev.java** | all lessons | Official tutorials, maintained by the team that makes JEP Café. Free. |
| **JEP Café playlist** | 1.1, 1.2, 1.3, 1.4, 1.6 | [Full series](https://www.youtube.com/playlist?list=PLX8CzqL3ArzV4BpOzLanxd4bZr46x5e87) |
| **Inside Java Newscast** | 1.6 ongoing | Nicolai Parlog. Keeps you current after this node. |
| **JDK source** | 1.1, 1.2 | Free, authoritative, and the only real answer for map internals. |
| **Optimizing Java** (Evans et al.) | benchmarking method | Only if your JMH results look suspicious. |

---

## Done when

1. You can explain how a HashMap resizes, and why a poorly distributed hashCode degrades lookup from O(1) to O(n) — and to O(log n) once a bin treeifies.
2. You can explain why LinkedList's O(1) insertion rarely beats ArrayList in practice, in terms of memory layout rather than Big-O.
3. You have four JMH benchmarks you wrote yourself and can defend the numbers in.

Item 3 is the real gate. Reading about performance and measuring it are different skills, and only one of them survives an interview.

---

## Notes
- [List implementations: ArrayList vs. LinkedList](/java/01-java-core-notes/01-java-core-list-implementations.md)
- [Map implementations: HashMap internals](/java/01-java-core-notes/01-java-core-map-implementations.md)

---
[Roadmap](../README.md) · [02 · Java Concurrency](../java/02-java-concurrency.md) →
