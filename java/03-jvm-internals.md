# Node 3 — JVM internals: study plan

**Budget:** ~36 hours. Three weeks at 10–12 hrs/week.
**Study order:** 3.1 → 3.2 → 3.3 → 3.4 → 3.5

Note on sequencing: 3.5 (JIT) sits last but if your JMH numbers from node 1 confused you, pull it forward. Everything in this node is measurement, and the JIT is the main reason measurements lie.

---

## Setup (3 hrs, do this first)

- **JDK 25.** Current LTS.
- **JOL (Java Object Layout)** — OpenJDK tool, prints actual object layout including header, field order, and padding. Essential for 3.1; nothing else shows you real bytes.
- **JDK Mission Control + JFR** — JFR is built into the JVM and nearly free to run. JMC reads the recordings.
- **async-profiler** — better flame graphs than JFR for CPU and allocation profiling, and it avoids the safepoint bias that makes `jstack`-based profilers wrong.
- **Eclipse MAT** — heap dump analysis for 3.4. Learn the "dominator tree" view specifically.
- **jcmd** — the one CLI tool that replaces most of the others. `jcmd <pid> help` lists what it can do.

*Checkpoint: run `jcmd <pid> VM.flags` against any running JVM and identify its collector before starting.*

---

## Week 1 — Memory and collection (12 hrs)

### 3.1 Memory layout (5 hrs)
📖 [JEP 519: Compact Object Headers](https://openjdk.org/jeps/519) — read Motivation and the layout diagrams
📖 JOL samples (ships with the tool) — work through the first six
🔧 JOL `ClassLayout.parseClass(...).toPrintable()`
*Do: calculate the actual memory cost of a `HashMap<Long, String>` with a million entries. Compare to naive estimate.*

**Currency note that matters:** the object header has been 12 bytes since compressed oops arrived in Java 6. JEP 519 shrank it to 8 bytes in JDK 25 — shipped as a product feature, but **not enabled by default**; you need `-XX:+UseCompactObjectHeaders`. JEP 534 proposes making it default in JDK 27. Sources disagree on the default, so don't trust any of them including me: run JOL twice, once with the flag and once without, and read the real numbers.

*Beyond the header: field alignment and padding, compressed oops and the 32GB cliff, why `Integer` costs 16 bytes to hold 4 bytes of data, and `@Contended` / false sharing.*

### 3.2 Garbage collection (7 hrs)
📖 [Oracle HotSpot GC Tuning Guide](https://docs.oracle.com/en/java/javase/25/gctuning/) — the official reference, skim the whole thing then read G1 closely
📖 JEP 474 (generational ZGC, default since JDK 23) and JEP 490 (non-generational ZGC removed in 24)
🔧 `-Xlog:gc*:file=gc.log:time,uptime,level,tags` then feed it to [GCeasy](https://gceasy.io) for a first read
*Do: enable GC logging on a service and read the output. Identify one full GC and explain what triggered it.*

**The collectors as of JDK 25:**
| Collector | Use when | Pause profile |
|---|---|---|
| **G1** (default since JDK 9) | general purpose, heaps >4GB | tens of ms, region-based |
| **ZGC** (generational since 23) | latency-critical, large heaps | sub-millisecond, concurrent |
| **Parallel** | batch, throughput over latency | long but efficient |
| **Serial** | small containers, single core | simple, predictable |

*What actually causes long pauses: high allocation rate forcing frequent young collections, premature promotion filling old gen, humongous object allocation in G1, and reference processing. Know all four — "the heap was too small" is the amateur answer.*

---

## Week 2 — Measurement and diagnosis (12 hrs)

### 3.3 Profiling (6 hrs)
📖 [async-profiler README](https://github.com/async-profiler/async-profiler) — read the section on why safepoint bias matters
📖 [Brendan Gregg on flame graphs](https://www.brendangregg.com/flamegraphs.html) — the canonical explanation of how to read one
🔧 async-profiler in `cpu`, `alloc`, and `lock` modes; JFR for continuous low-overhead recording
*Do: profile a slow endpoint and produce a flame graph. Name the actual bottleneck before you fix it.*

*The discipline: CPU-bound, lock-bound, and allocation-bound look completely different in a flame graph. Run all three modes on the same workload so you learn the signatures. The width of a frame is time, not call count — misreading this is the most common beginner error.*

### 3.4 Diagnosing failures (6 hrs)
📖 Eclipse MAT documentation on the dominator tree and "leak suspects" report
📖 The `OutOfMemoryError` varieties: Java heap space, GC overhead limit exceeded, Metaspace, Direct buffer memory, unable to create native thread. Each has a different cause and a different fix.
🔧 `-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=...` — set this in every service you run, now
🔧 `jcmd <pid> GC.heap_info`, `Thread.print`, `VM.native_memory` (needs NMT enabled)
*Do: create a leak with an unbounded static map, capture the heap dump, and find it in a heap analyzer.*

*Leak patterns worth recognizing: unbounded caches, listeners never deregistered, ThreadLocals in pooled threads, ClassLoader leaks on redeploy. The last one is the hardest and the most common in app servers.*

---

## Week 3 — JIT and consolidation (12 hrs)

### 3.5 JIT basics (5 hrs)
📖 [JMH samples](https://github.com/openjdk/jmh) — work through `JMHSample_01` through `JMHSample_12`. These are a JIT course disguised as a benchmarking tutorial.
📖 Aleksey Shipilev, "[Java Benchmarking: How Do You Do It?](https://shipilev.net/#benchmarking)" — why naive microbenchmarks are wrong
🔧 `-XX:+PrintCompilation`, and `-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining`
*Do: write one JMH benchmark correctly, with warmup and blackholes.*

*Concepts: interpreter → C1 → C2 tiering, inlining and the thresholds that stop it, escape analysis and scalar replacement, loop unrolling, deoptimization and why it happens. The practical upshot: dead code elimination and constant folding will silently delete your benchmark's body unless you use a Blackhole.*

### Consolidation exercise (7 hrs)

Take a real service — ideally your node 9 ledger — and run a full performance investigation:

1. Baseline it under load with JMH or a load generator.
2. Capture a JFR recording across the run.
3. Produce CPU, allocation, and lock flame graphs.
4. Read the GC log and characterise allocation rate and promotion.
5. Form one hypothesis about the bottleneck, change one thing, re-measure.
6. Write it up: what you predicted, what you measured, what you changed, what happened.

The write-up is the artifact. "I profiled a service and found the bottleneck was X, which surprised me because I expected Y" is one of the strongest things you can bring to a lead-level conversation, and it's the shape of the answer node 3's completion test is asking for.

---

## Resources worth owning

| Resource | Covers | Notes |
|---|---|---|
| **Optimizing Java** (Evans, Gough, Newland) | 3.1–3.5 | The best single book on this node. Covers JVM internals, GC, JIT, and benchmarking method together. |
| **JVM Anatomy Quarks** (Shipilev) | 3.1, 3.2, 3.5 | [shipilev.net/jvm/anatomy-quarks](https://shipilev.net/jvm/anatomy-quarks/) — short, rigorous posts on single JVM behaviors. Free, and unmatched in precision. |
| **HotSpot GC Tuning Guide** | 3.2 | Official, free, current per release. |
| **JOL** | 3.1 | OpenJDK tool. No