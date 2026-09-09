# Full curriculum — Capital One Lead Software Engineer

Three milestones, 36 nodes, ~190 lessons. Every lesson has a **Do** — the thing that proves you learned it. Reading without the Do produces recognition, not knowledge.

Each node closes with a **Node complete when** test. If you can't pass it, the node isn't done regardless of how many lessons you've read.

At 10–12 hours/week this is roughly 34 weeks. At 20 hours/week, roughly 18.

---
---

# Milestone 1 — Foundations

*Nodes 1–9. Target: 8 weeks at 10–12 hrs/week.*

The goal is depth in what you already touch daily. If your Java and Postgres are strong, compress this to 3 weeks and start at node 4.

---

## 1. Java core

**1.1 Collections internals** — HashMap buckets, hashing, treeification at 8 entries, resize cost. ArrayList vs LinkedList real-world costs. TreeMap/LinkedHashMap ordering guarantees.
*Do: write a benchmark showing where LinkedList actually beats ArrayList. Note how narrow the window is.*

**1.2 equals, hashCode, and comparable** — the contracts, what breaks when you violate them, why records help.
*Do: build a class that violates the hashCode contract and demonstrate the object getting lost in a HashSet.*

**1.3 Streams** — laziness, short-circuiting, collectors, when a stream is slower than a loop, parallel stream pitfalls with shared state.
*Do: rewrite three nested loops from an old project as a stream pipeline. Then benchmark both.*

**1.4 Generics and type erasure** — bounded types, wildcards, PECS, why `List<int>` is impossible, heap pollution.
*Do: write a generic repository interface with bounded types that compiles cleanly with no raw-type warnings.*

**1.5 Modern Java** — records, sealed interfaces, pattern matching for switch, text blocks, Optional used correctly.
*Do: model a payment result as a sealed interface with record implementations and exhaustive switch handling.*

**1.6 Exception design** — checked vs unchecked in practice, try-with-resources, why swallowing exceptions is the most common production bug.
*Do: audit a codebase you own for empty catch blocks. Count them.*

**Node complete when:** you can explain how a HashMap resizes and why a poorly distributed hashCode degrades it from O(1) to O(n) — and now O(log n) since treeification.

---

## 2. Java concurrency

**2.1 The memory model** — happens-before, visibility vs atomicity, what `volatile` guarantees and what it doesn't.
*Do: write a program with a non-volatile flag that never terminates on a server JVM. Then fix it.*

**2.2 Locks** — `synchronized` vs `ReentrantLock`, fairness, read-write locks, lock striping, deadlock conditions.
*Do: deliberately construct a deadlock, then find it with a thread dump.*

**2.3 Atomics and lock-free** — CAS, `AtomicInteger`, `LongAdder`, ABA problem, when lock-free wins.
*Do: build a counter three ways (synchronized, AtomicLong, LongAdder) and benchmark under contention.*

**2.4 Executors and thread pools** — pool types, queue choice, rejection policies, sizing for CPU-bound vs I/O-bound work, why `Executors.newCachedThreadPool` is dangerous.
*Do: derive a pool size for an I/O-bound service from Little's Law and defend the number in writing.*

**2.5 CompletableFuture** — composition, `thenCompose` vs `thenCombine`, exception handling, the executor-context trap.
*Do: fan out three parallel service calls, combine results, and handle partial failure without blocking.*

**2.6 Virtual threads** — what they change, structured concurrency, pinning, where they don't help.
*Do: convert a thread-per-request handler to virtual threads and measure the difference under load.*

**Node complete when:** you can explain why a shared `HashMap` under concurrent writes can produce an infinite loop, not just a lost update.

---

## 3. JVM internals

**3.1 Memory layout** — heap generations, metaspace, stack, direct buffers, object header overhead.
*Do: calculate the actual memory cost of a `HashMap<Long, String>` with a million entries. Compare to naive estimate.*

**3.2 Garbage collection** — G1 vs ZGC vs Parallel, allocation rate, promotion, what actually causes long pauses.
*Do: enable GC logging on a service and read the output. Identify one full GC and explain what triggered it.*

**3.3 Profiling** — async-profiler or JFR, flame graphs, distinguishing CPU-bound from lock-bound from allocation-bound.
*Do: profile a slow endpoint and produce a flame graph. Name the actual bottleneck before you fix it.*

**3.4 Diagnosing failures** — heap dumps, `OutOfMemoryError` varieties, thread dumps, memory leak patterns in long-lived caches.
*Do: create a leak with an unbounded static map, capture the heap dump, and find it in a heap analyzer.*

**3.5 JIT basics** — interpretation, C1/C2, inlining, deoptimization, why microbenchmarks lie without JMH.
*Do: write one JMH benchmark correctly, with warmup and blackholes.*

**Node complete when:** given "our service pauses for 2 seconds every few minutes," you can list four distinct causes and the diagnostic that distinguishes them.

---

## 37. Software design and refactoring

*Numbered 37 so the existing diagrams stay valid — the number is an ID, not a position. Work this alongside nodes 1–3.*

This is the node that generalizes furthest. It applies to any language, any stack, any company, and it's most of what a lead actually does.

**37.1 Cohesion and coupling** — the two properties that predict whether a change is cheap or expensive. Afferent vs efferent coupling, connascence as a finer-grained lens.
*Do: pick a class you dislike working in. Name specifically which kind of coupling makes it painful.*

**37.2 SOLID, and its critics** — what each principle actually protects against, where the interpretations go wrong, and the credible arguments that SRP and OCP are over-applied.
*Do: find one place where following OCP would add an abstraction nobody needs. Argue the other side.*

**37.3 Patterns that earn their keep** — strategy, adapter, decorator, factory, repository, observer. And the ones that usually signal a problem: singleton, service locator, anemic domain models.
*Do: remove one pattern from your codebase that isn't paying for itself.*

**37.4 Domain modeling** — ubiquitous language, entities vs value objects, aggregates and invariant boundaries, making illegal states unrepresentable.
*Do: model money as a value object with currency. Make it impossible to add USD to EUR at compile time.*

**37.5 The cost of abstraction** — every abstraction is a bet on future change. Premature abstraction vs premature duplication, the rule of three, why "don't repeat yourself" is misapplied constantly.
*Do: find duplicated code you should leave duplicated. Write down why.*

**37.6 Refactoring discipline** — small behavior-preserving steps, tests as the safety net, the named catalog (extract method, replace conditional with polymorphism, introduce parameter object), refactoring separately from feature work.
*Do: refactor one gnarly method in ten commits, each individually revertible and green.*

**37.7 Working with legacy code** — seams, characterization tests, the strangler fig pattern, breaking dependencies to get code under test.
*Do: take an untested class with a hard dependency. Introduce a seam and get it under test without changing behavior.*

**37.8 Reading unfamiliar code** — entry points, tracing a request through, identifying the model vs the plumbing, reading tests as documentation, when to read and when to run.
*Do: pick an open-source project you've never seen. Produce an accurate architecture summary in two hours.*

**37.9 Debugging as a method** — hypothesis and falsification, bisection, minimal reproduction, reading stack traces properly, `git bisect`, resisting the urge to change things randomly.
*Do: next real bug, write your hypothesis down before touching anything. Track how often the first guess is right.*

**37.10 Code review** — what to look for in priority order (correctness, security, then design, then style), reviewing for the author's growth, disagreeing productively, when to stop commenting and start a conversation.
*Do: review a PR twice — once for defects, once for design. Note that they're different readings.*

**Node complete when:** you can look at a class and articulate why it will be expensive to change, using specific coupling terms rather than "it feels messy."

---

## 38. Testing strategy

*Also numbered out of sequence. Work it alongside nodes 1–3, right after node 37.*

**38.1 What tests are actually for** — confidence to change, not defect counting. Why a test suite that never fails is as useless as one that always does.
*Do: for each test file in one module, write the one sentence explaining what change it's meant to catch. Delete any you can't answer for.*

**38.2 The pyramid and its critics** — the classic shape, the trophy, why "unit" is the most overloaded word in testing, cost vs confidence per layer.
*Do: chart your current suite's actual distribution. It probably isn't the shape you assumed.*

**38.3 Tests that don't calcify** — testing behavior not implementation, why mocking internals makes refactoring impossible, the relationship between test brittleness and design quality.
*Do: find a test that broke during a refactor without a behavior change. That test was wrong.*

**38.4 Test doubles** — dummy, stub, spy, mock, fake, and their distinct purposes. Classicist vs mockist schools. When a real dependency beats a double.
*Do: replace one heavily mocked test with an in-memory fake. Compare readability.*

**38.5 Integration testing** — testing across real boundaries, Testcontainers, database tests that aren't slow, test data management, isolation between tests.
*Do: get a Postgres-backed test suite running under 30 seconds with real schema and real queries.*

**38.6 Contract testing** — consumer-driven contracts, why integration tests across services don't scale, Pact-style verification in a pipeline.
*Do: define a contract between two of your node 21 services. Break it deliberately and watch the build fail.*

**38.7 Property-based testing** — generators, invariants, shrinking. Where it finds bugs example-based tests never will.
*Do: property-test your node 9 ledger. Assert that debits and credits always sum to zero across any generated sequence.*

**38.8 Coverage and its abuse** — line vs branch vs path coverage, why 100% proves little, mutation testing as the honest alternative.
*Do: run a mutation testing tool on a well-covered module. The surviving mutants are your real gaps.*

**38.9 Testing concurrency and time** — deterministic tests for non-deterministic code, injecting clocks, controlling schedulers, the race detector, why `Thread.sleep` in a test is a defect.
*Do: find a flaky test. Fix the cause rather than adding a retry.*

**38.10 Testing what matters** — risk-based prioritization, what deserves exhaustive testing (money movement, authorization, data deletion) versus what doesn't, and what auditors expect evidence of.
*Do: rank the modules in one system by cost of failure. Compare the ranking to where your tests actually are.*

**Node complete when:** you can look at a suite and say which changes it would catch and which it would silently allow through.

---

## 4. Data structures

**4.1 Arrays, hashing, and strings** — hash collision handling, string interning, when to use a char array over a String.
*Do: 8 problems. Two-sum family, anagram grouping, subarray sums.*

**4.2 Stacks, queues, deques** — monotonic stack pattern, sliding window maximum, BFS queues.
*Do: 5 problems. Valid parentheses, daily temperatures, sliding window maximum.*

**4.3 Linked lists** — fast/slow pointers, reversal, cycle detection, dummy head technique.
*Do: 4 problems. Reverse in groups, detect cycle start, merge k sorted lists.*

**4.4 Trees and BSTs** — traversal orders, recursive vs iterative, BST invariants, balancing intuition, LCA.
*Do: 8 problems. Level order, validate BST, serialize/deserialize, lowest common ancestor.*

**4.5 Heaps and priority queues** — heapify, top-k pattern, two-heap median, custom comparators.
*Do: 5 problems. Kth largest, merge k lists with a heap, median from data stream.*

**4.6 Graphs** — adjacency list vs matrix, BFS/DFS, cycle detection, topological sort, union-find, Dijkstra.
*Do: 8 problems. Number of islands, course schedule, network delay time, redundant connection.*

**4.7 Tries** — prefix trees, autocomplete, where they beat hash maps.
*Do: 3 problems. Implement a trie, word search II, longest common prefix.*

**Node complete when:** given an unfamiliar problem, you name the right structure before writing any code, and can justify the choice against one alternative.

---

## 5. Algorithms

**5.1 Binary search** — on arrays, on rotated arrays, and on answer spaces (the version most people miss).
*Do: 6 problems, at least two of which search an answer space rather than an array.*

**5.2 Two pointers and sliding window** — fixed vs variable window, the shrink condition.
*Do: 6 problems. Longest substring without repeats, container with most water, minimum window substring.*

**5.3 Recursion and backtracking** — state, choice, undo. Pruning. Permutations vs combinations vs subsets.
*Do: 6 problems. Subsets, permutations, N-queens, word search.*

**5.4 Dynamic programming** — memoization first, then tabulation. The five recurring shapes: knapsack, LCS, LIS, interval, grid.
*Do: 8 problems, one from each shape plus three repeats of the hardest shape.*

**5.5 Greedy and intervals** — exchange argument, interval merging and scheduling, when greedy fails.
*Do: 4 problems. Merge intervals, meeting rooms II, jump game, task scheduler.*

**5.6 Complexity analysis** — amortized analysis, space complexity including recursion stack, reading complexity off a nested structure.
*Do: for the last 20 problems you solved, write the time and space complexity from memory. Then verify.*

**Node complete when:** you can state brute force, name the bottleneck, and give the optimized complexity — out loud, before coding.

---

## 6. Postgres depth

**6.1 Index types** — B-tree, hash, GIN, GiST, BRIN. Partial and expression indexes. Covering indexes and index-only scans.
*Do: create a partial index that a full index can't beat, and prove it with EXPLAIN.*

**6.2 Composite index column order** — leftmost prefix rule, equality-then-range ordering, why order changes everything.
*Do: build a three-column index, then write one query it serves and one it can't. Explain why.*

**6.3 Reading EXPLAIN ANALYZE** — seq scan vs index scan vs bitmap heap scan, nested loop vs hash vs merge join, estimated vs actual rows.
*Do: find a query where the planner's row estimate is off by 10x and explain the consequence.*

**6.4 Transactions and isolation** — the four levels, and which anomaly each prevents. Postgres's actual implementations. Serializable snapshot isolation.
*Do: open two psql sessions and reproduce a non-repeatable read, then a phantom, then prevent each.*

**6.5 MVCC and vacuum** — tuple versions, visibility, dead tuples, bloat, autovacuum tuning, transaction ID wraparound.
*Do: create table bloat deliberately, observe it, and reclaim it.*

**6.6 Locks and deadlocks** — row vs table locks, lock queues, `SELECT FOR UPDATE`, `NOWAIT` and `SKIP LOCKED`, deadlock detection.
*Do: build a job queue using `SKIP LOCKED` that supports concurrent workers without double-processing.*

**Node complete when:** given "a query got 400x slower and nothing changed," you name four distinct root causes and the diagnostic for each.

---

## 7. Postgres at scale

**7.1 Replication** — streaming vs logical, sync vs async, replica lag and its consequences for read-after-write.
*Do: describe how you'd route reads to a replica without ever showing a user their own stale write.*

**7.2 Connection pooling** — why Postgres connections are expensive, PgBouncer pooling modes, why serverless breaks pooling.
*Do: calculate the max connections your service tier can safely open, and explain what happens past it.*

**7.3 Partitioning** — range, list, hash. Partition pruning. When partitioning helps and when it just adds pain.
*Do: partition a time-series table by month and prove pruning happens in the query plan.*

**7.4 Sharding** — shard key selection, cross-shard queries, resharding, why this is a last resort.
*Do: pick a shard key for a customer transaction table and name the query it makes expensive.*

**7.5 Zero-downtime migrations** — expand/contract, adding columns safely, backfilling large tables, index creation concurrently, lock-avoiding DDL.
*Do: write a four-step plan to rename a column in a hot table with zero downtime.*

**7.6 Normalization and its limits** — normal forms, and the specific cases where denormalizing is the correct engineering call.
*Do: find one place in a system you know where denormalization is justified. Write the justification.*

**Node complete when:** you can plan a schema change to a table taking 5,000 writes/second without taking a lock that blocks them.

---

## 8. NoSQL patterns

**8.1 The four families** — key-value, document, wide-column, graph. The access pattern each one serves.
*Do: for four systems you know, state which family fits and why.*

**8.2 DynamoDB data modeling** — partition key, sort key, composite keys, item collections, hot partition avoidance.
*Do: model a customer-transactions access pattern and identify the hot partition risk.*

**8.3 Single-table design** — overloaded keys, GSI overloading, when single-table is over-engineering.
*Do: model three access patterns in one table, then write the argument against doing so.*

**8.4 Indexes and capacity** — GSI vs LSI, projection types, on-demand vs provisioned, throttling behavior.
*Do: calculate the RCU/WCU cost of one realistic workload.*

**8.5 Consistency and conditional writes** — eventually consistent vs strongly consistent reads, condition expressions, optimistic locking with version attributes.
*Do: implement an optimistic-locking update that safely rejects a concurrent conflicting write.*

**8.6 Choosing between Postgres and DynamoDB** — the real decision criteria: access pattern stability, query flexibility, scale profile, transaction needs.
*Do: take one table from a system you own. Write the case for moving it, then the case against.*

**Node complete when:** you can argue both directions on a specific table with concrete access patterns, not generic scaling claims.

---

## 9. Project — transaction ledger

Build a double-entry ledger service. This is the single most transferable portfolio piece for a bank.

**9.1 Double-entry model** — accounts, journal entries, debits and credits balancing to zero, why balances are derived and never mutated.
*Do: design the schema. Every entry must have two sides summing to zero, enforced by constraint.*

**9.2 Immutability** — append-only entries, corrections as reversing entries rather than updates, no UPDATE or DELETE on the journal.
*Do: revoke UPDATE and DELETE at the database role level. Implement a correction flow that works anyway.*

**9.3 Idempotent writes** — idempotency keys, storage and TTL, behavior on retry mid-flight, behavior when the key store is down.
*Do: hammer your endpoint with duplicate requests concurrently. Assert exactly one entry exists.*

**9.4 Balance computation** — running balance vs computed-on-read, snapshot/checkpoint strategy, reconciliation between the two.
*Do: implement both, then write a reconciliation job that detects drift.*

**9.5 Concurrency correctness** — isolation level choice, row locking, preventing double-spend under concurrent debits.
*Do: run 100 concurrent debits against a balance of 50. Assert the balance never goes negative.*

**9.6 Audit surface** — who did what and when, immutable audit log, query by point-in-time.
*Do: answer "what was account X's balance on a given date" without a full table scan.*

**Node complete when:** the concurrency test passes reliably and the reconciliation job proves the two balance methods agree.

---
---

# Milestone 2 — Distributed systems

*Nodes 10–21. Target: 12 weeks at 10–12 hrs/week.*

This is the largest milestone and the one the job description leads with. Don't compress it.

---

## 10. Spring Boot

**10.1 Dependency injection** — constructor injection, bean scopes, lifecycle, why field injection is a testing problem.
*Do: convert a field-injected class to constructor injection and write a test with no Spring context.*

**10.2 Configuration** — profiles, `@ConfigurationProperties`, externalized config, secrets that never reach a properties file.
*Do: configure the same service for three environments with zero code changes.*

**10.3 Auto-configuration** — how it works, conditional beans, debugging why a bean didn't get created.
*Do: read the auto-config report and explain one bean you didn't declare.*

**10.4 Actuator and health** — health indicators, readiness vs liveness endpoints, custom health checks, metrics export.
*Do: write a custom health indicator that fails when a downstream dependency is unreachable.*

**10.5 Testing layers** — unit, slice tests (`@WebMvcTest`, `@DataJpaTest`), integration with Testcontainers.
*Do: write one test at each layer for the same feature. Note what each catches that the others don't.*

**10.6 Error handling** — `@ControllerAdvice`, problem detail responses, not leaking internals in error bodies.
*Do: build a global handler that returns RFC 7807 problem details and never exposes a stack trace.*

**Node complete when:** you can start a new service with health checks, config profiles, and integration tests in under an hour.

---

## 11. REST API design

**11.1 Resource modeling** — nouns not verbs, nesting depth, when to break REST purity deliberately.
*Do: design the resource model for a dispute-management API. Justify one non-RESTful endpoint.*

**11.2 HTTP semantics** — method safety and idempotency, correct status codes, `PUT` vs `PATCH`, `201` vs `202`.
*Do: audit an existing API for wrong status codes. There will be some.*

**11.3 Pagination and filtering** — offset vs cursor pagination, why offset breaks at scale, stable sort keys.
*Do: implement cursor pagination that stays correct when rows are inserted mid-pagination.*

**11.4 Versioning** — URI vs header vs media type, backward-compatible change rules, deprecation policy.
*Do: write the rules for what counts as a breaking change in your API. Be specific.*

**11.5 Conditional requests and caching** — ETags, `If-Match` for optimistic concurrency, `Cache-Control` semantics.
*Do: implement `If-Match` on an update endpoint so concurrent edits conflict instead of overwriting.*

**11.6 Contract-first design** — OpenAPI, generated clients, contract testing.
*Do: write the spec before the code for one endpoint. Generate the client.*

**Node complete when:** you can defend every status code and header in an API you designed, without appeal to convention alone.

---

## 12. gRPC and contracts

**12.1 Protobuf** — message definition, field numbering, wire format, why field numbers are permanent.
*Do: define a schema, serialize a message, and look at the actual bytes.*

**12.2 Schema evolution** — adding and reserving fields, optional semantics, forward and backward compatibility rules.
*Do: evolve a schema three times. Prove old clients still parse new messages.*

**12.3 Service definitions and streaming** — unary, server streaming, client streaming, bidirectional. When each fits.
*Do: implement a server-streaming endpoint and explain why it beats polling here.*

**12.4 Deadlines, cancellation, metadata** — deadline propagation across hops, cancellation semantics, passing correlation IDs.
*Do: propagate a deadline across three services and observe the whole chain cancel.*

**12.5 gRPC vs REST** — the actual decision criteria: internal vs external, latency budget, streaming needs, browser support, debuggability.
*Do: pick one service boundary and write the argument for each choice.*

**Node complete when:** you can state a rule for which protocol a new internal service should use, with reasoning.

---

## 13. Distributed core

**13.1 Why distributed is hard** — the eight fallacies, partial failure, the impossibility of distinguishing slow from dead.
*Do: write down what your service currently does when a dependency hangs but doesn't fail. Most systems handle this badly.*

**13.2 CAP and PACELC** — what CAP actually says (and doesn't), why PACELC is the more useful frame, latency vs consistency in the non-partitioned case.
*Do: classify three systems you use as CP or AP, and state the latency tradeoff each makes normally.*

**13.3 Consistency models** — linearizable, sequential, causal, read-your-writes, monotonic reads, eventual.
*Do: name the weakest model that would still be correct for three features you've built.*

**13.4 Replication and quorums** — leader-follower, multi-leader, leaderless, R+W>N, read repair, conflict resolution.
*Do: work out the quorum config for a system needing strong reads with one node down.*

**13.5 Consensus** — the problem consensus solves, Raft at a conceptual level (leader election, log replication, safety), where consensus hides in systems you use.
*Do: explain why a Raft cluster of 4 nodes is no more available than 3.*

**13.6 Time and ordering** — why wall clocks lie, NTP drift, logical clocks, vector clocks, hybrid clocks.
*Do: find one place in a system you know that orders events by timestamp. Explain how it can be wrong.*

**Node complete when:** you can explain why exactly-once delivery is impossible and what "effectively once" actually means operationally.

---

## 14. Resilience

**14.1 Timeouts** — connect vs read vs total, timeout budgets across hops, why a missing timeout is the most common outage cause.
*Do: audit every outbound call in one service for an explicit timeout. Fix the gaps.*

**14.2 Retries** — when retrying is safe, exponential backoff, jitter, retry budgets, retry storms and how they amplify outages.
*Do: implement backoff with full jitter. Explain why jitter matters more than the backoff curve.*

**14.3 Idempotency** — key design, storage choice and TTL, in-flight request handling, behavior when the idempotency store fails.
*Do: build an idempotent POST endpoint end to end. Test the concurrent-duplicate case specifically.*

**14.4 Circuit breakers** — states, thresholds, half-open probing, what to return when open, when a breaker makes things worse.
*Do: add a breaker to one dependency and force it open under fault injection.*

**14.5 Bulkheads and isolation** — thread pool and connection pool isolation, why one slow dependency shouldn't consume all capacity.
*Do: demonstrate a service exhausting its thread pool on one slow dependency. Then isolate it.*

**14.6 Load shedding and backpressure** — rate limiting algorithms (token bucket, leaky bucket, sliding window), queue depth as a signal, shedding gracefully.
*Do: implement token bucket rate limiting. Explain why it beats a fixed window.*

**14.7 Graceful degradation** — fallbacks, cached responses, feature-level shutoff, deciding what's safe to degrade in a financial context.
*Do: for a payment flow, list what may degrade and what absolutely must not.*

**Node complete when:** you can walk through a cascading failure from a single slow dependency and name the four controls that would have stopped it.

---

## 15. Kafka basics

**15.1 The log abstraction** — partitions as ordered logs, offsets, retention, why Kafka isn't a queue.
*Do: produce and consume from a topic. Watch offsets advance. Reset and replay.*

**15.2 Partitioning and ordering** — keys determine partition, ordering only within a partition, choosing keys, partition count consequences.
*Do: pick a key for a transaction event stream that preserves per-account ordering.*

**15.3 Consumer groups** — rebalancing, partition assignment, why consumers can't exceed partitions, rebalance storms.
*Do: scale consumers past partition count and observe idle consumers.*

**15.4 Delivery semantics** — at-least-once by default, offset commit timing, duplicates, idempotent producers and transactions.
*Do: force a duplicate by committing offsets after processing and crashing mid-batch.*

**15.5 Schema management** — schema registry, Avro or protobuf, compatibility modes, evolving event schemas safely.
*Do: define an event schema and evolve it under backward-compatibility rules.*

**15.6 Operations** — consumer lag as the key metric, retention vs compaction, dead-letter topics, poison messages.
*Do: build a consumer that routes unparseable messages to a DLQ instead of blocking.*

**Node complete when:** you can explain why consumer lag is the metric that matters and what three different lag patterns indicate.

---

## 16. Event-driven architecture

**16.1 Event styles** — event notification, event-carried state transfer, event sourcing. Coupling implications of each.
*Do: for one integration you know, write the event payload three ways and compare coupling.*

**16.2 The dual-write problem** — why writing to a database and a broker in one operation is unsafe, and how it fails.
*Do: construct the failure. Write to DB, kill the process before publishing, observe the inconsistency.*

**16.3 Transactional outbox** — the pattern, polling vs CDC, ordering, cleanup.
*Do: implement an outbox with a relay. Prove no event is lost on crash.*

**16.4 Sagas** — choreography vs orchestration, compensating actions, semantic rollback, when a saga can't compensate.
*Do: design a payment saga. Identify the step where compensation is impossible and say what you do instead.*

**16.5 Event sourcing** — event store, projections, replay, snapshots, versioning events, why it's usually the wrong default.
*Do: build a small event-sourced aggregate. Then write the case against using it broadly.*

**16.6 Consumer idempotency** — processed-message tracking, deduplication windows, making handlers naturally idempotent.
*Do: make one consumer safe to replay from offset zero without side-effect duplication.*

**Node complete when:** you can explain the dual-write problem and implement the outbox pattern without reference material.

---

## 17. Caching

**17.1 Cache strategies** — cache-aside, read-through, write-through, write-behind, refresh-ahead. Failure behavior of each.
*Do: implement cache-aside with correct miss handling. Then name what it does on a write.*

**17.2 Invalidation** — TTL, explicit invalidation, versioned keys, why invalidation is genuinely one of the hard problems.
*Do: find a stale-read window in your own invalidation design. There is one.*

**17.3 Redis data structures** — strings, hashes, sorted sets, sets, streams. Using the right one instead of serialized JSON everywhere.
*Do: implement a leaderboard with a sorted set and a rate limiter with a sliding window.*

**17.4 Stampede protection** — thundering herd on expiry, request coalescing, probabilistic early expiry, locking.
*Do: simulate 500 concurrent misses on one hot key. Then coalesce them into one origin call.*

**17.5 Distributed cache concerns** — consistent hashing, hot keys, eviction policies, Redis persistence and durability limits.
*Do: explain what data you would never put in Redis alone, and why.*

**17.6 Correctness in a financial context** — what may be cached, what may not, staleness budgets tied to business rules.
*Do: for a balance display, define the acceptable staleness and defend it.*

**Node complete when:** you can name three ways a cache makes a system less reliable, not more.

---

## 18. Observability

**18.1 The three signals** — logs, metrics, traces. What each answers and what it can't.
*Do: take one recent incident and note which signal would have identified it fastest.*

**18.2 Structured logging** — JSON logs, consistent fields, correlation IDs, log levels used meaningfully, what never goes in a log at a bank.
*Do: define a log schema. Include the rule for PII and card data exclusion.*

**18.3 Metrics** — counters, gauges, histograms, cardinality explosion, percentiles vs averages, why p99 matters more than mean.
*Do: instrument one endpoint with a latency histogram. Explain why you can't average percentiles.*

**18.4 Distributed tracing** — spans, context propagation, sampling strategies, OpenTelemetry, trace-to-log correlation.
*Do: propagate a trace across three services and find one span you didn't expect.*

**18.5 RED and USE methods** — rate/errors/duration for services, utilization/saturation/errors for resources.
*Do: build a dashboard for one service following RED. Nothing else on it.*

**18.6 SLIs, SLOs, error budgets** — choosing an SLI that reflects user experience, setting an achievable SLO, error budget policy.
*Do: write one SLO for a service you own, including what happens when the budget is exhausted.*

**18.7 Alerting** — symptom-based vs cause-based alerts, alert fatigue, what deserves a page at 3am.
*Do: cut an existing alert list in half. Justify each deletion.*

**Node complete when:** you can design the observability for a new service before writing its business logic.

---

## 19. Docker

**19.1 Images and layers** — layer caching, build order for cache efficiency, image size, multi-stage builds.
*Do: cut a Java image's size by more than half with multi-stage build and a slim base.*

**19.2 Dockerfile correctness** — non-root user, no secrets in layers, `.dockerignore`, pinned base images, signal handling and PID 1.
*Do: fix a container that ignores SIGTERM and takes 30 seconds to stop.*

**19.3 Networking and volumes** — bridge networks, port publishing, named volumes, bind mounts and their pitfalls.
*Do: run a service plus Postgres plus Redis on one Compose network with persistent data.*

**19.4 Java in containers** — container-aware heap sizing, CPU limits and GC thread counts, why old JVMs misbehave in containers.
*Do: set container memory limits and verify the JVM respects them.*

**19.5 Image security** — vulnerability scanning, minimal and distroless bases, SBOM generation, image signing.
*Do: scan one of your images. Fix the highest-severity finding.*

**Node complete when:** you can write a production-grade Dockerfile for a Spring Boot service from memory, non-root and multi-stage.

---

## 20. Kubernetes

**20.1 Core objects** — Pod, ReplicaSet, Deployment, Service, Ingress. What each one actually does.
*Do: deploy a service and expose it. Then delete a pod and watch it come back.*

**20.2 Configuration** — ConfigMap, Secret, environment injection, why native Secrets are only base64, external secret operators.
*Do: inject config three ways and state the security difference.*

**20.3 Probes** — liveness vs readiness vs startup. What each failure does. The classic mistake of pointing liveness at a dependency.
*Do: configure all three correctly for a service with a slow startup and a database dependency.*

**20.4 Resources and scheduling** — requests vs limits, QoS classes, OOMKill, CPU throttling, node affinity, taints and tolerations.
*Do: explain why setting a CPU limit can make latency worse.*

**20.5 Autoscaling** — HPA on CPU and custom metrics, scale-up vs scale-down behavior, cluster autoscaler, cold start cost.
*Do: configure an HPA on a queue-depth metric rather than CPU.*

**20.6 Deployment strategies** — rolling update mechanics, maxSurge and maxUnavailable, blue/green, canary, rollback behavior.
*Do: perform a rolling update with zero dropped requests. Then a rollback.*

**20.7 Stateful workloads** — StatefulSet, persistent volumes, why databases in Kubernetes are a real decision not a default.
*Do: write the argument for and against running Postgres in your cluster.*

**Node complete when:** given "the deployment rolled out and errors spiked but every pod reports healthy," you name three probe or resource misconfigurations that produce this.

---

## 21. Project — microservices platform

Take the ledger from node 9 and decompose it. The goal is to experience distributed failure firsthand.

**21.1 Service decomposition** — identify bounded contexts, draw the boundaries, database per service, what data gets duplicated.
*Do: split into at least three services. Write down what you lost by splitting (you lost joins and transactions).*

**21.2 Inter-service communication** — pick sync or async per boundary and justify each. Implement both styles somewhere.
*Do: one gRPC call, one Kafka event. Explain why each got its protocol.*

**21.3 Distributed data consistency** — apply the outbox pattern, implement one saga with compensation.
*Do: kill a service mid-saga. Verify the compensation runs and the system reaches a consistent state.*

**21.4 API gateway and edge** — routing, authentication at the edge, rate limiting, request/response transformation.
*Do: put a gateway in front. Enforce auth once, at the edge.*

**21.5 Full observability** — traces across every hop, per-service RED dashboards, correlation IDs in every log line.
*Do: trace a single request across all services and produce the waterfall.*

**21.6 Deployment** — containerize everything, deploy to Kubernetes (kind or minikube is fine), rolling updates, health checks.
*Do: deploy a breaking change, watch it fail health checks, and roll back.*

**21.7 Chaos testing** — kill services, add latency, partition the network, fill a disk.
*Do: write down what you expected to happen, then run it. The gap is the lesson.*

**Node complete when:** you can kill any single service and the system degrades predictably rather than failing unpredictably.

---
---

# Milestone 3 — Cloud, security, and the regulatory domain

*Nodes 22–36. Target: 14 weeks at 10–12 hrs/week.*

Nodes 30, 31, and 36 are the ones that differentiate you for this specific team. Don't skip them for the more familiar material.

---

## 22. AWS core

**22.1 IAM deeply** — principals, users vs roles, assume-role, trust policies, identity vs resource policies, permission boundaries, policy evaluation order.
*Do: write a policy granting least-privilege access to one S3 prefix. Then explain the evaluation logic that makes it work.*

**22.2 Cross-account access** — role assumption across accounts, external IDs, why banks use many accounts.
*Do: diagram a three-account setup (dev, prod, security) with the trust relationships.*

**22.3 VPC and networking** — CIDR planning, public vs private subnets, route tables, internet and NAT gateways, VPC endpoints.
*Do: design a VPC where application servers have no route to the internet but can still reach S3.*

**22.4 Security groups vs NACLs** — stateful vs stateless, evaluation order, when you need both.
*Do: explain why a return packet works with a security group but can be blocked by a NACL.*

**22.5 Compute options** — EC2, ECS on EC2 vs Fargate, EKS. The decision criteria between them.
*Do: pick one for a new service and write the three-line justification.*

**22.6 Availability and regions** — AZs, multi-AZ vs multi-region, RTO and RPO, what each level of redundancy costs.
*Do: define RTO and RPO for a payments service, then price the architecture that meets it.*

**Node complete when:** you can write an IAM policy from scratch and explain why an explicit deny beats any allow.

---

## 23. AWS data layer

**23.1 RDS and Aurora** — Multi-AZ vs read replicas (different things), failover behavior, Aurora storage architecture, backup and PITR.
*Do: explain what happens to in-flight connections during a Multi-AZ failover.*

**23.2 DynamoDB in production** — capacity modes, auto-scaling, adaptive capacity, DAX, global tables, streams.
*Do: design a DynamoDB Stream consumer that reacts to writes without polling.*

**23.3 S3** — storage classes, lifecycle policies, versioning, object lock (WORM), presigned URLs, event notifications.
*Do: configure Object Lock in compliance mode and verify even the root account can't delete early.*

**23.4 Data protection** — encryption at rest with KMS, encryption in transit enforcement, bucket policies denying unencrypted uploads.
*Do: write a bucket policy that rejects any upload not encrypted with a specific KMS key.*

**23.5 Choosing a store** — the decision tree across RDS, DynamoDB, S3, ElastiCache, and OpenSearch.
*Do: place five workloads from a system you know onto that tree.*

**Node complete when:** you can explain S3 Object Lock and why it's the foundation of a compliant audit archive.

---

## 24. AWS messaging and serverless

**24.1 SQS** — standard vs FIFO, visibility timeout, long polling, DLQs, message deduplication.
*Do: build a consumer where a slow handler causes duplicate processing. Then fix the visibility timeout.*

**24.2 SNS and fan-out** — topics, subscriptions, filter policies, SNS-to-SQS fan-out pattern.
*Do: implement fan-out where two consumers receive different subsets via filter policies.*

**24.3 EventBridge** — event bus, rules, schema registry, third-party integrations, scheduled events.
*Do: route one event type to two targets based on payload content.*

**24.4 Lambda** — execution model, cold starts, concurrency and reserved concurrency, timeouts, layers, when Lambda is wrong.
*Do: identify one workload where Lambda's cost model beats a container, and one where it loses badly.*

**24.5 Step Functions** — state machines, standard vs express, error handling and retry, when orchestration beats choreography.
*Do: build a workflow with a retry policy and a catch branch that compensates.*

**24.6 API Gateway** — REST vs HTTP APIs, authorizers, throttling, usage plans, request validation.
*Do: configure a custom authorizer and throttle one client independently.*

**Node complete when:** you can choose between SQS, SNS, EventBridge, and Kafka for a given integration and defend it.

---

## 25. Infrastructure as code

**25.1 Why IaC** — drift, reproducibility, review, and the compliance angle: infrastructure changes as auditable artifacts.
*Do: articulate the compliance argument for IaC in two sentences. It's the one that matters at a bank.*

**25.2 Terraform fundamentals** — providers, resources, state, state locking, remote backends, modules.
*Do: provision a VPC and one service. Then deliberately drift it manually and detect the drift.*

**25.3 Terraform in teams** — workspaces, module versioning, plan review as a control gate, avoiding state corruption.
*Do: structure a repo so dev and prod share modules but never share state.*

**25.4 AWS CDK** — constructs, synthesis to CloudFormation, when typed IaC beats HCL.
*Do: build the same small stack in both. Write down which you'd pick for a team and why.*

**25.5 Policy as code** — OPA/Conftest, tfsec/checkov, preventing non-compliant infrastructure at plan time.
*Do: write a policy that blocks any unencrypted S3 bucket from being planned.*

**Node complete when:** your infrastructure change process produces an artifact an auditor could review.

---

## 26. CI/CD

**26.1 Pipeline design** — stages, fail-fast ordering, build once and promote the same artifact, artifact immutability.
*Do: build a pipeline that produces one artifact and promotes it across three environments unchanged.*

**26.2 Testing in the pipeline** — unit, integration with Testcontainers, contract tests, smoke tests post-deploy.
*Do: add a post-deploy smoke test that triggers an automatic rollback on failure.*

**26.3 Security gates** — SAST, dependency scanning, secret scanning, container scanning, license checks.
*Do: add dependency scanning and set the severity threshold that fails the build. Defend the threshold.*

**26.4 Deployment strategies** — rolling, blue/green, canary, feature flags as a decoupling mechanism.
*Do: implement a feature flag that decouples deploy from release. Explain why this matters for change control.*

**26.5 Segregation of duties in the pipeline** — why the author can't be the sole approver, automated evidence capture, break-glass procedures.
*Do: design an approval flow satisfying SoD without blocking on a single human.*

**26.6 Rollback and recovery** — automated rollback triggers, database migration rollback (the hard part), forward-fix vs rollback.
*Do: write the rollback plan for a deploy that included a schema migration.*

**Node complete when:** you can describe a pipeline that produces audit evidence automatically as a side effect of deploying.

---

## 27. AppSec

**27.1 Broken access control** — the top OWASP category. IDOR, missing function-level checks, horizontal vs vertical privilege escalation.
*Do: find an endpoint in code you own that trusts a client-supplied ID. There usually is one.*

**27.2 Injection** — SQL, NoSQL, command, LDAP. Parameterization, why ORMs don't automatically save you, dynamic query construction.
*Do: write a query that's injectable despite using an ORM.*

**27.3 XSS and CSRF** — stored vs reflected vs DOM XSS, output encoding, CSP, SameSite cookies, CSRF tokens and when they're unnecessary.
*Do: write a CSP that would break an inline-script page, then fix the page rather than weakening the policy.*

**27.4 SSRF and deserialization** — why SSRF is severe in cloud (metadata endpoint), allowlisting, unsafe deserialization in Java.
*Do: explain how SSRF reaches IMDS and what IMDSv2 changes.*

**27.5 Supply chain** — dependency confusion, typosquatting, transitive vulnerabilities, SBOM, pinning and provenance.
*Do: generate an SBOM for one project and identify your deepest transitive dependency.*

**27.6 Threat modeling** — STRIDE, data flow diagrams, trust boundaries, doing it early enough to matter.
*Do: threat model your node 21 platform. Find at least three issues you hadn't considered.*

**Node complete when:** you can run a threat model on a design doc and produce findings that change the design.

---

## 28. Identity

**28.1 OAuth 2.0 flows** — authorization code with PKCE, client credentials, device flow. Which to use where, and which are deprecated.
*Do: implement authorization code with PKCE. Explain what PKCE prevents.*

**28.2 OIDC** — how it layers on OAuth, ID token vs access token, the userinfo endpoint, discovery.
*Do: state precisely what an ID token is for and why it must not be used as an access token.*

**28.3 JWT** — structure, signing algorithms, the `alg: none` and algorithm confusion attacks, validation checklist, clock skew.
*Do: write a token validator. Include every check: signature, issuer, audience, expiry, not-before.*

**28.4 Token lifecycle** — access vs refresh tokens, rotation, revocation, the fundamental revocation problem with stateless tokens.
*Do: design a revocation approach and state its latency window honestly.*

**28.5 Storage and transport** — httpOnly cookies vs localStorage, XSS vs CSRF tradeoff, SameSite, token binding.
*Do: pick a storage approach for a banking web app and name the attack you're now exposed to.*

**28.6 Authorization models** — RBAC, ABAC, ReBAC, policy engines, centralized vs embedded decisions, entitlements as a service.
*Do: model permissions for a system with customers, agents, and auditors. RBAC alone won't be enough.*

**Node complete when:** you can list every JWT validation step from memory and name the attack each one prevents.

---

## 29. Cryptography and secrets

**29.1 Practical crypto** — symmetric vs asymmetric, AES-GCM, authenticated encryption, why you never design your own scheme.
*Do: encrypt and decrypt with AES-GCM. Explain what the authentication tag protects against.*

**29.2 Hashing and passwords** — cryptographic hashes vs password hashes, bcrypt/argon2, salts, why SHA-256 alone is wrong for passwords.
*Do: state the correct password storage scheme and the parameter you'd tune.*

**29.3 KMS and envelope encryption** — data keys vs master keys, key policies, grants, why envelope encryption exists.
*Do: implement envelope encryption. Explain why you don't send the plaintext to KMS.*

**29.4 Key rotation** — rotating without re-encrypting everything, key versioning, what rotation actually protects against.
*Do: design a rotation scheme for a table with 100M encrypted rows.*

**29.5 Secrets management** — Secrets Manager vs Parameter Store, dynamic secrets, rotation automation, secrets in CI, what never goes in an env var.
*Do: automate database credential rotation with zero downtime.*

**29.6 Tokenization** — tokenization vs encryption, PCI scope reduction, format-preserving tokens, the vault model.
*Do: explain how tokenizing card numbers removes systems from PCI scope.*

**Node complete when:** you can explain envelope encryption and why tokenization is the standard answer for card data.

---

## 30. Regulatory core

*The differentiating node. Almost no candidate prepares this.*

**30.1 The regulatory landscape** — SOX, PCI-DSS, GLBA Safeguards, BSA/AML, OFAC, FCRA, Reg E, Reg Z. What each governs, one paragraph each.
*Do: for each, write the single sentence explaining how it constrains an engineer.*

**30.2 Supervision and examination** — OCC as primary regulator for a national bank, FFIEC IT handbooks, what an exam actually involves, MRAs.
*Do: explain what an examiner asks for and why "we have logs" is not an adequate answer.*

**30.3 Audit trails** — what makes a log an audit record, required fields (actor, action, object, timestamp, outcome, justification), append-only design.
*Do: design an audit event schema. Include the field most people forget: why the action was taken.*

**30.4 Tamper evidence** — hash chaining, Merkle trees, WORM storage, digital signatures, separation of audit store from application store.
*Do: implement a hash-chained log. Prove that altering entry 50 invalidates everything after it.*

**30.5 Data lineage** — provenance tracking, proving where a number came from, lineage across service boundaries and ETL.
*Do: for a computed customer-facing figure, trace every input back to its source system.*

**30.6 Model risk management (SR 11-7)** — model inventory, validation, monitoring, why this now applies to ML and LLM features.
*Do: write down what you'd need to document before shipping an ML-driven decision to customers.*

**30.7 Privacy** — GLBA, CCPA/CPRA, consent as a data structure, purpose limitation, data minimization, right to deletion vs retention obligations.
*Do: resolve the conflict: a customer requests deletion of data you're legally required to retain for seven years.*

**Node complete when:** you can design a system that proves, two years later, that a decision followed the policy in force on that date.

---

## 31. Controls and holds

**31.1 Control types** — preventive, detective, corrective. Manual vs automated. Why automated preventive controls are worth the most.
*Do: classify five controls in a system you know. Most will be detective. Note which should be preventive.*

**31.2 Segregation of duties** — the principle, applying it to code, deploy, and data access; four-eyes principle; break-glass with logging.
*Do: design production data access requiring two people, without making incident response impossible.*

**31.3 Change management** — normal vs standard vs emergency change, approval evidence, linking a deploy to a ticket to an approval.
*Do: design a pipeline that automatically produces the evidence package for one deploy.*

**31.4 Retention and legal hold** — retention schedules by data class, defensible deletion, legal hold overriding deletion, hold release.
*Do: design a hold mechanism that suspends deletion for one customer's data across five services.*

**31.5 Reconciliation** — control totals, break detection, aging breaks, automated versus manual resolution.
*Do: build a reconciliation job for your node 9 ledger that detects and reports breaks.*

**31.6 Evidence and controls testing** — what auditors accept as evidence, sampling, continuous control monitoring, automated attestation.
*Do: automate the evidence collection for one control so no human assembles it quarterly.*

**31.7 Access reviews** — periodic recertification, orphaned accounts, privilege creep, joiner-mover-leaver.
*Do: write the query that identifies every account with production access and no activity in 90 days.*

**Node complete when:** you can explain segregation of duties in a CI/CD pipeline and design one that satisfies it without blocking delivery.

---

## 32. TypeScript and React

**32.1 TypeScript type system** — structural typing, generics, conditional and mapped types, discriminated unions, `unknown` vs `any`.
*Do: model an API response as a discriminated union so impossible states don't compile.*

**32.2 Type safety at boundaries** — runtime validation (zod or similar), why types disappear at runtime, generated types from OpenAPI.
*Do: validate one API response at runtime and derive the TypeScript type from the schema.*

**32.3 React fundamentals revisited** — reconciliation, keys, hooks rules, effect dependencies, why effects are overused.
*Do: find one `useEffect` in your code that shouldn't exist. Remove it.*

**32.4 State architecture** — local vs lifted vs global, server state as a distinct category, caching and invalidation with a query library.
*Do: replace manual fetch-and-store with a query library. Note how much code disappears.*

**32.5 Performance** — memoization and when it hurts, virtualization, code splitting, bundle analysis, Core Web Vitals.
*Do: analyze a bundle. Find the largest dependency and decide whether it earns its size.*

**32.6 Accessibility** — semantic HTML first, ARIA only when needed, keyboard navigation, focus management, WCAG 2.1 AA.
*Do: navigate one of your apps using only a keyboard. Fix what you can't reach.*

**32.7 Front-end security** — XSS in React (`dangerouslySetInnerHTML`), CSP with React, token handling, CORS in practice, what belongs in a browser.
*Do: explain precisely what CORS protects and why it is not protecting your API.*

**Node complete when:** you can explain the full request path from URL entry to rendered pixel, including DNS, TLS, and hydration.

---

## 33. Go essentials

**33.1 Syntax and idioms** — structs, interfaces satisfied implicitly, no inheritance, composition, the standard project layout.
*Do: port one small Java service to Go. Note what the type system won't let you express.*

**33.2 Error handling** — explicit errors, wrapping, `errors.Is` and `errors.As`, why there are no exceptions and what that costs.
*Do: write a call chain that wraps errors with context at each level.*

**33.3 Goroutines and channels** — the concurrency model, buffered vs unbuffered, select, worker pools, goroutine leaks.
*Do: create a goroutine leak, then detect it.*

**33.4 Context** — cancellation, deadlines, propagation through call chains, values (and why they're overused).
*Do: propagate a deadline through three layers and cancel cleanly.*

**33.5 Testing and tooling** — table-driven tests, the race detector, benchmarks, `go vet`, modules.
*Do: run the race detector against concurrent code and fix what it finds.*

**33.6 Why Go for services** — startup time, memory footprint, deployment simplicity, and where Java still wins.
*Do: write the two-paragraph comparison you'd give a team choosing between them.*

**Node complete when:** you can read Go code fluently and discuss the tradeoff against Java for a new service.

---

## 34. AI tooling

**34.1 Agentic workflows** — multi-step task delegation, tool use, when to let an agent run unattended and when to supervise.
*Do: delegate one real multi-file refactor. Record where it went wrong.*

**34.2 Codebase comprehension** — using AI to explore unfamiliar systems, generating architecture summaries, mapping dependencies.
*Do: point a tool at an unfamiliar open-source repo and produce an accurate architecture summary. Verify it.*

**34.3 Test generation and migration** — generating meaningful tests rather than tautologies, large-scale mechanical migrations.
*Do: generate tests for one module. Count how many actually test behavior versus restate implementation.*

**34.4 AI-assisted review** — what it catches reliably, what it misses, why it never replaces a human reviewer for authorization logic.
*Do: run AI review on a PR alongside a human. Compare the findings.*

**34.5 RAG** — chunking strategy, embeddings, retrieval quality, evaluation, why naive RAG usually disappoints.
*Do: build a small RAG system over your own documentation. Then measure retrieval accuracy honestly.*

**34.6 LLM API mechanics** — context windows, tool calling, structured output, streaming, cost and latency modeling.
*Do: implement one tool-calling loop with structured output and error handling.*

**34.7 Governance in a regulated environment** — data leakage, prompt injection, IP concerns, applying SR 11-7 to LLM features, human-in-the-loop requirements.
*Do: write the control set you'd require before an LLM touches customer-facing decisions.*

**Node complete when:** you have a real story about AI tooling producing confidently wrong output, and the process change that catches it.

---

## 35. System design

**35.1 The method** — requirements, scale estimation, API contract, data model, high-level design, deep dive, bottlenecks, tradeoffs. In that order, every time.
*Do: run the full method on one problem, timing each phase.*

**35.2 Estimation** — QPS, storage, bandwidth, connection counts. Rough numbers fast, and knowing when precision matters.
*Do: memorize latency numbers every engineer should know. Estimate the storage for 10 years of transaction data.*

**35.3 Core design problems** — rate limiter, URL shortener, notification service, news feed, distributed cache.
*Do: five designs, one per week, written out fully.*

**35.4 Financial domain designs** — payment processing with idempotency, card authorization at low latency, fraud detection pipeline, real-time ledger.
*Do: four designs. For each, state the consistency requirement before designing.*

**35.5 Regulatory domain designs** — audit evidence pipeline, consent and entitlements service, data retention and legal hold, controls testing platform, data lineage tracker.
*Do: five designs. These map directly to this team's work.*

**35.6 Tradeoff articulation** — for every choice, the alternative and the cost. Practicing the sentence "I'd choose X, accepting Y, because Z."
*Do: revisit three earlier designs and write the tradeoff sentence for each major decision.*

**Node complete when:** you can take an ambiguous regulatory prompt, clarify it into requirements, and design a system in 45 minutes.

---

## 36. Project — evidence platform

The capstone. This is the portfolio piece that maps to the job description.

**36.1 Define the problem** — a system that proves what decision was made, when, by whom, under which policy version, with tamper-evident records.
*Do: write a one-page problem statement as though for a regulator.*

**36.2 Immutable event store** — append-only storage, hash chaining, periodic anchoring, S3 Object Lock for the archive tier.
*Do: implement the chain. Demonstrate that any alteration is detectable.*

**36.3 Policy versioning** — policies as versioned artifacts, decisions referencing the policy version applied, reconstructing historical policy state.
*Do: change a policy, then correctly re-answer a question about a decision made before the change.*

**36.4 Lineage tracking** — every input to a decision recorded with its source and version, traversable graph.
*Do: given a decision ID, produce the full input tree with sources.*

**36.5 Retention and legal hold** — retention schedule enforcement, hold placement and release, deletion that respects both.
*Do: place a hold, attempt deletion, verify it's blocked, release, verify deletion proceeds.*

**36.6 Auditor interface** — point-in-time queries, evidence export, read-only access with its own audit trail.
*Do: export an evidence package for one decision. It should stand alone without you explaining it.*

**36.7 Access control** — entitlements, segregation of duties, auditors who can read everything but change nothing.
*Do: model three roles. Verify none can alter the audit trail, including the administrator.*

**36.8 Operational maturity** — full observability, deployment automation, runbook, threat model, documented SLOs.
*Do: write the runbook. Then have someone else follow it.*

**Node complete when:** you can hand the evidence export to someone unfamiliar with the system and they can verify the decision independently.

---
---

# Continuous track

Not sequential. Run these alongside everything above, roughly an hour a week each.

## A. Engineering leadership

**A.1 Team structure** — Team Topologies, cognitive load, Conway's Law and the inverse maneuver.
**A.2 Delivery metrics** — DORA four keys, what they reveal, how they get gamed.
**A.3 Agile in practice** — the actual purpose of each ceremony, estimation, WIP limits, why velocity isn't productivity.
**A.4 Technical debt** — taxonomy, quantification, making the business case.
**A.5 Incident management** — on-call structure, severity levels, blameless postmortems, action item follow-through.
**A.6 Decision records** — ADRs, design review process, documenting the rejected alternatives.
**A.7 Mentoring and feedback** — SBI feedback, delegation vs abdication, growing people past yourself.
**A.8 Influence and communication** — technical risk to non-technical executives, influencing without authority, written over verbal.

*Do, weekly: write one ADR-style document about a decision you made. Include what you rejected and why.*

## B. Web and browser fundamentals

**B.1 HTTP** — 1.1 vs 2 vs 3, head-of-line blocking, multiplexing, connection reuse.
**B.2 Caching headers** — `Cache-Control`, `ETag`, revalidation, CDN behavior.
**B.3 Cookies and sessions** — attributes, `SameSite`, scoping, session fixation.
**B.4 CORS** — preflight triggers, credentials mode, what it protects and what it doesn't.
**B.5 TLS** — handshake, certificate chains, mTLS, certificate pinning, rotation.
**B.6 Real-time transports** — WebSockets vs SSE vs long polling, and the load balancer implications of each.
**B.7 The full path** — URL entry to rendered page, every step.

*Do: write the URL-to-pixel walkthrough once, from memory, then check it.*

## C. Python fluency

**C.1 Idiomatic Python** — comprehensions, generators, context managers, dataclasses, type hints.
**C.2 Standard library** — the parts worth knowing: `collections`, `itertools`, `functools`, `pathlib`.
**C.3 Tooling** — virtual environments, uv or poetry, ruff, pytest, mypy.
**C.4 Where Python fits** — scripting, data work, ML services. Where it doesn't: latency-critical paths.

*Do: write your operational scripts in Python instead of bash for a month.*

---

## Progress tracking

Copy this and mark as you go.

```
Milestone 1 — Foundations
[ ] 1  Java core            [ ] 4  Data structures    [ ] 7  Postgres at scale
[ ] 2  Java concurrency     [ ] 5  Algorithms         [ ] 8  NoSQL patterns
[ ] 3  JVM internals        [ ] 6  Postgres depth     [ ] 9  Project: ledger
[ ] 37 Software design      [ ] 38 Testing strategy

Milestone 2 — Distributed systems
[ ] 10 Spring Boot          [ ] 14 Resilience         [ ] 18 Observability
[ ] 11 REST API design      [ ] 15 Kafka basics       [ ] 19 Docker
[ ] 12 gRPC contracts       [ ] 16 Event-driven       [ ] 20 Kubernetes
[ ] 13 Distributed core     [ ] 17 Caching            [ ] 21 Project: services

Milestone 3 — Cloud, security, regulatory
[ ] 22 AWS core             [ ] 27 AppSec             [ ] 32 TypeScript & React
[ ] 23 AWS data layer       [ ] 28 Identity           [ ] 33 Go essentials
[ ] 24 AWS messaging        [ ] 29 Crypto & secrets   [ ] 34 AI tooling
[ ] 25 Infra as code        [ ] 30 Regulatory core    [ ] 35 System design
[ ] 26 CI/CD                [ ] 31 Controls & holds   [ ] 36 Project: evidence

Continuous
[ ] A Leadership   [ ] B Web fundamentals   [ ] C Python
```
