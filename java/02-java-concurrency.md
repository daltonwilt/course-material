# Node 2 — Java concurrency: study plan

**Budget:** ~40 hours. Four weeks at 10–12 hrs/week.
**Study order:** 2.1 → 2.7 in sequence. Each lesson depends on the one before.

This node is harder than node 1 and the material is less forgiving. Budget the extra week.

---

## Setup (3 hrs, do this first)

- **JDK 25.** Virtual threads are final since 21; structured concurrency is still preview, so you'll need `--enable-preview`.
- **JCStress** — OpenJDK's concurrency stress harness, by Shipilev. This is the right tool for 2.1: it runs memory-model experiments thousands of times and reports which interleavings actually occurred on your hardware. Nothing else makes reordering visible.
- **JMH** — again, for contention benchmarks in 2.3.
- **jcmd / jstack** — thread dumps for 2.2. Practice capturing one before you need it.
- **A machine with ≥4 cores.** Several experiments produce no interesting result on fewer.

*Checkpoint: run one JCStress sample test and read the output table before starting.*

---

## Week 1 — Visibility and mutual exclusion (11 hrs)

### 2.1 The memory model (6 hrs)
📖 [JSR-133 FAQ](https://www.cs.umd.edu/~pugh/java/memoryModel/jsr-133-faq.html) — start here, it's short and authoritative
📖 JCiP ch. 3 (sharing objects) then ch. 16 (the JMM itself)
📖 [Shipilev — JMM Pragmatics](https://shipilev.net/talks/narnia-2555-jmm-pragmatics-en.pdf) — the hard version, read after the above
🔧 JCStress samples, especially the reordering and publication cases
*Do: write a program with a non-volatile flag that never terminates on a server JVM. Then fix it.*
*The gate: you must be able to state what `volatile` guarantees (visibility + ordering, no atomicity for compound ops) and what it doesn't.*

### 2.2 Locks (5 hrs)
📖 JCiP ch. 10 (liveness hazards) and ch. 13 (explicit locks)
📖 `ReentrantLock` and `ReentrantReadWriteLock` javadoc — the fairness discussion is in there
🔧 jstack for deadlock detection; the JVM reports deadlocks explicitly in the dump
*Do: deliberately construct a deadlock, then find it with a thread dump.*
*Know the four Coffman conditions and which one lock ordering removes.*

---

## Week 2 — Lock-free and pooling (11 hrs)

### 2.3 Atomics and lock-free (5 hrs)
📖 JCiP ch. 15 — CAS, atomic variables, nonblocking algorithms
📖 `LongAdder` javadoc — the class comment explains striping and when it beats `AtomicLong`
🔧 JMH with increasing thread counts
*Do: build a counter three ways (synchronized, AtomicLong, LongAdder) and benchmark under contention.*
*The crossover point is the lesson. At one thread, synchronized often wins.*

### 2.4 Executors and thread pools (6 hrs)
📖 JCiP ch. 6 (task execution) and ch. 8 (applying thread pools) — ch. 8 has the sizing formula
📖 `ThreadPoolExecutor` javadoc — read the constructor parameter docs carefully, that's the whole API
📖 The four `RejectedExecutionHandler` policies and what each does to the caller
*Do: derive a pool size for an I/O-bound service from Little's Law and defend the number.*
*Formula: N_threads = N_cpu × U_cpu × (1 + W/C). Know what happens when you get W/C wrong in each direction.*
*Why `newCachedThreadPool` is dangerous: unbounded thread creation under load. Know the failure mode.*

---

## Week 3 — Async composition and virtual threads (11 hrs)

### 2.5 CompletableFuture (6 hrs)
📖 `CompletableFuture` javadoc — dense but complete; the async-suffix variants matter
📖 Modern Java in Action ch. 16 (JCiP predates this API entirely)
📖 The common ForkJoinPool: understand that you're borrowing a shared, non-configurable resource
*Do: fan out three parallel service calls, combine results, and handle partial failure without blocking.*
*Traps to hit deliberately: `thenApply` vs `thenApplyAsync` executor context; swallowed exceptions when you forget `exceptionally`; blocking calls on the common pool starving parallel streams elsewhere.*

### 2.6 Virtual threads (5 hrs)
📺 [Java 19 Virtual Threads — JEP Café #11](https://www.youtube.com/watch?v=lKSSBvRDmTg)
📺 [Concurrent and Asynchronous Programming: state of Loom — Paumard, Devoxx UK](https://www.youtube.com/watch?v=m0pq_wkBfPo)
📖 JEP 444 (virtual threads, final) and JEP 491 (synchronize without pinning)
*Do: convert a thread-per-request handler to virtual threads and measure the difference under load.*
*Currency check: JEP 491 in Java 24 largely eliminated pinning on `synchronized` blocks. If you cite the old pinning advice, you'll sound two releases behind — know the before and after.*

---

## Week 4 — Structured concurrency and consolidation (10 hrs)

### 2.7 Structured concurrency (5 hrs)
📺 [Java Asynchronous Programming Full Tutorial with Loom and Structured Concurrency — JEP Café #13](https://www.youtube.com/watch?v=2nOj8MKHvmw)
📖 JEP 505 (structured concurrency, latest preview — the API was revamped in Java 25, so older tutorials show the wrong shape)
📖 Scoped values as the ThreadLocal replacement
*Do: fan out three calls under one scope. Kill one and verify the siblings cancel.*
⚠️ *API churn warning: `StructuredTaskScope` has changed signature across previews. Verify any example against your JDK before debugging it.*

### Consolidation exercise (5 hrs)

Build the same component twice:

A service that fetches from three slow downstream endpoints, applies a timeout, combines results, and degrades gracefully when one fails.

**Version A:** `ThreadPoolExecutor` + `CompletableFuture`.
**Version B:** virtual threads + `StructuredTaskScope`.

Then compare: lines of code, where cancellation lives, what a stack trace looks like when one call fails, and throughput under 10,000 concurrent requests.

This is the single most useful artifact in the node. It's a concrete answer to "when would you use virtual threads," and it feeds directly into node 14 (resilience) and node 21 (the microservices project).

---

## Resources worth owning

| Resource | Covers | Notes |
|---|---|---|
| **Java Concurrency in Practice** (Goetz, 2006) | 2.1–2.4 | Still the canonical text, 20 years on. Predates CompletableFuture, virtual threads, and structured concurrency entirely. |
| **JCStress** | 2.1 | The only practical way to observe memory-model behavior. Free, OpenJDK. |
| **JEP Café playlist** | 2.6, 2.7 | [Full series](https://www.youtube.com/playlist?list=PLX8CzqL3ArzV4BpOzLanxd4bZr46x5e87) |
| **Inside Java Newscast** | 2.6, 2.7 ongoing | Loom is still moving. Parlog tracks each release. |
| **JEPs themselves** | 2.6, 2.7 | Read the Motivation and Alternatives sections — that's where the design reasoning is. |
| **dev.java concurrency tutorial** | all | Official, free, maintained. |

---

## Node complete when

1. You can explain why a shared `HashMap` under concurrent writes can produce an infinite loop, not just a lost update.
2. You can explain why virtual threads don't speed up CPU-bound work, and what pinning is — including what changed in Java 24.
3. You can show both versions of the consolidation exercise and argue when each is the right choice.

Item 3 is the gate. "I've used virtual threads" is a claim anyone makes; having built the same thing both ways and measured it is the thing that holds up under questioning.