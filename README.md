# utils

A small library of general purpose Java utilities shared by my personal projects (notably [jaudio-stream](https://github.com/OllieMilton/jaudio-stream) and jtunes).

## What's in it

### `ollie.common.datastructure`

Buffer abstractions with a common `Buffer<T>`/`ByteBuffer` interface: `get`/`put`, `seek`, `pos`, `size`, `freeSpace` and `commit`.

- `ArrayBuffer` / `ArrayByteBuffer` — grow once, write once buffers backed by a plain array; `commit()` fixes the logical end of the data.
- `CircularBuffer` / `CircularByteBuffer` — fixed capacity ring buffers that refuse to overwrite unread data (throwing `BufferOverflowException`) rather than silently dropping it. These back the bounded buffer streaming client in jaudio-stream.

### `ollie.utils.state`

`StateHolder<T>` — a thread safe holder for a state enum with listener callbacks on transition, conditional transitions, terminal states (transitions out of a terminal state throw until `reset()`), and blocking waits for a state condition with optional timeout. `StateTransitionListener` is the callback interface.

### `ollie.utils.concurrent`

- `SimpleDeBounce` / `QueuedDeBounce` — windowed rate limiters: at most one invocation per wall clock window. The simple variant drops suppressed calls; the queued variant queues them and runs each in the next available window.
- `ConditionalWait` / `WaitCondition` — block a thread until an arbitrary condition on a delivered value is met, with optional timeout.
- `Promise` — a minimal settable future.
- `VolatileReference` — a volatile wrapper for safe cross thread publication of a mutable reference.

### Everything else

- `ollie.utils.sortsearch` — `Search.binarySearch` (with optional comparator) and `Sort.mergeSort`.
- `ollie.utils.serialisation.JSONBuilder` — a small fluent JSON string builder.
- `ollie.utils.logging.l4j` — a log4j2 appender (`LogEntryAppender`) that forwards structured `LogEntry` objects to registered `LogEntryListener`s.
- `Maths`, `Strings`, `ExceptionUtil` — odds and ends.

## Building

```
./gradlew build
```

## Releasing

Bump `version` in `build.gradle.kts`, then:

```
./gradlew publish
```

This clones the git backed [maven-repo](https://github.com/OllieMilton/maven-repo), publishes into it and pushes — no local repository checkout needed.

## Consuming

```kotlin
repositories {
	mavenCentral()
	maven {
		url = uri("https://raw.githubusercontent.com/OllieMilton/maven-repo/main")
	}
}

dependencies {
	implementation("ollie.utils:utils:1.74")
}
```
