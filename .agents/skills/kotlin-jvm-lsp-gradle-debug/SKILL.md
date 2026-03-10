---
name: kotlin-jvm-lsp-gradle-debug
description: Standardize Gradle/JVM Kotlin build, test result reading, and JDWP debug attach workflows with strong guardrails and structured output. Use this skill whenever running Gradle tasks, reading JUnit test results, debugging a Kotlin/JVM process, or troubleshooting build failures. Trigger proactively when the user asks to run tests, check what tests failed, debug a Gradle app or test process, investigate build failures, attach a debugger, or inspect Gradle output. This skill is especially important for reliably parsing JUnit XML reports — never rely on scrolling console output when this skill is available.
---

# Kotlin JVM Gradle Debug & Test

You are running a reliable, low-surprise Gradle/JVM build-test-debug loop. Prioritize determinism: verify preconditions first, parse structured XML instead of console text, report failures with exact context. Every command you run must be the narrowest one that answers the question.

---

## Phase 0 — Pre-flight (run before any Gradle command)

Check all of these. Abort and report the first failure clearly — do not run Gradle if the environment is broken.

```bash
# 1. Wrapper present
[ -f ./gradlew ] || { echo "FAIL gradlew: not found — wrong project root?"; exit 1; }

# 2. Wrapper executable
[ -x ./gradlew ] || chmod +x ./gradlew

# 3. Java available
java -version 2>&1 | head -1

# 4. Gradle version via wrapper
./gradlew -v 2>&1 | grep -E "^Gradle "
```

Emit exactly one line:
```
PRE-FLIGHT: gradlew=OK  java=<version>  gradle=<version>
```

If a check fails, emit `PRE-FLIGHT: FAIL reason=<which check> detail=<raw error>` and stop.

---

## Phase 1 — Build and Test

### Preferred test commands

```bash
./gradlew test                                        # all tests, all modules
./gradlew :<module>:test                              # single module
./gradlew test --tests "com.example.FooTest"          # single class
./gradlew test --tests "com.example.FooTest.myMethod" # single method
./gradlew test --rerun                                # force re-run (no clean needed)
```

**Do not use `clean` unless the user asks or compiled classes are the suspected root cause.** Clean discards the incremental cache and makes every subsequent build slower.

### Daemon hygiene

If tests hang, produce UP-TO-DATE unexpectedly, or behave inconsistently:

```bash
./gradlew --stop          # kill all daemons for this Gradle version
./gradlew test --rerun    # restart fresh
```

---

## Phase 2 — Reading Test Results (canonical method)

**Never parse console output.** Console log level, color codes, and truncation make it unreliable. JUnit XML reports are always written to disk after a test run, regardless of logging settings.

### Step 1: confirm reports exist

```bash
find . -path "*/build/test-results/*/*.xml" -not -path "*/.gradle/*" | head -20
```

If no files are found after a test run → the task may have been UP-TO-DATE and skipped. Run with `--rerun` and check again.

### Step 2: run the parser

```bash
python3 .agents/skills/kotlin-jvm-lsp-gradle-debug/scripts/parse-junit.py .
```

The script searches from the project root, parses all JUnit XML under `*/build/test-results/`, and emits:

```
SUITE_SUMMARY: modules=3 total=87 pass=85 skip=1 fail=1 time=4.23s
FAILURES[1]:
  [1] com.example.FooTest#shouldReturnBar
       type=org.junit.ComparisonFailure
       msg='expected:<1> but was:<2>'
```

Exit code from the script:
- `0` → all tests passed (or skipped)
- `1` → failures/errors present
- `2` → no XML reports found

### Fallback (no python3)

Parse manually — see `references/command-playbook.md#junit-xml-schema` for the XML structure.

---

## Phase 3 — JDWP Debug Attach

### Pre-attach: check port availability

```bash
lsof -i :5005 | grep LISTEN
```

If the port is occupied:
- Kill the holder: `lsof -ti :5005 | xargs kill -9`
- Or use a different port (see custom port section below)

### Start the suspended process

```bash
# Debug an application
./gradlew run --debug-jvm

# Debug tests (suspends before the first test runs)
./gradlew test --debug-jvm

# Debug a specific test class
./gradlew test --tests "com.example.FooTest" --debug-jvm
```

The JVM **suspends and waits** until a debugger attaches. The terminal will block — this is expected and correct. Do not cancel it; attach from the editor instead.

Default attach target: `host=127.0.0.1  port=5005`

### Custom port

If 5005 is in use, override via JVM args:

```bash
./gradlew test --debug-jvm \
  --jvm-args="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5006"
```

Or in `build.gradle.kts` for tests:
```kotlin
tasks.test {
    jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5006")
}
```

### Editor attach configs

See `references/command-playbook.md#debug-attach-configs` for VS Code, IntelliJ, and generic DAP configurations.

---

## Phase 4 — Recovery Sequence

Follow this order when builds or tests misbehave. Do not skip steps or jump ahead.

| Step | Command | When to use |
|------|---------|-------------|
| 1 | `./gradlew --stop` | Tests hang, daemon crash, stale classpath |
| 2 | `./gradlew test --rerun` | Tests show UP-TO-DATE when they should run |
| 3 | `./gradlew clean test` | Compiled classes suspected stale |
| 4 | `./gradlew clean build --refresh-dependencies` | Dependency resolution failures |
| 5 | `rm -rf ~/.gradle/caches/modules-*` then retry | Persistent cache corruption across projects |

After step 5, always re-run Phase 0 pre-flight before proceeding.

---

## Output Contract

Every session ends with this status block — no exceptions:

```
STATUS:  PASS | FAIL | BLOCKED
ENV:     jdk=<version>  gradle=<version>  os=<platform>
TESTS:   total=N  pass=N  fail=N  skip=N
FAILURES: <inline 1-line summary per failure, or "none">
DEBUG:   <port=N suspended=y|n, or "not started">
NEXT:    <exact next command to run, or exact user action required>
```

`NEXT` is always present. If blocked, it describes what the user must do manually (e.g., `NEXT: attach debugger to localhost:5005`).

---

## Common Failure Modes

| Symptom | Root cause | Fix |
|---------|------------|-----|
| `FAILURE: Build failed with exception` after clean | Missing dependency or compile error | Read full error, not just the last line |
| Tests always UP-TO-DATE | Gradle incremental build caching | `--rerun` or `--no-build-cache` |
| `NO_REPORTS` from parser after test run | Task cached, no new output written | `./gradlew test --rerun` |
| Debug session never attaches | Port conflict or firewall | Check `lsof -i :5005` |
| `Connection refused` on JDWP attach | Process exited before attach | Check terminal — it may have failed on startup |
| Daemon OOM | Memory config | Add `org.gradle.jvmargs=-Xmx2g` to `gradle.properties` |
| `Caused by: java.lang.OutOfMemoryError` in tests | Test JVM heap | `tasks.test { maxHeapSize = "1g" }` |

---

## References

- [`references/command-playbook.md`](references/command-playbook.md) — Full command reference, JUnit XML schema, editor debug configs
- [`references/tooling-matrix.md`](references/tooling-matrix.md) — LSP and debug adapter selection with tradeoffs
- [`scripts/parse-junit.py`](scripts/parse-junit.py) — JUnit XML parser (run directly; no install required beyond python3)
