# Kotlin JVM Command Playbook

## Environment Checks

```bash
java -version 2>&1 | head -1
./gradlew -v 2>&1 | grep -E "^Gradle "
./gradlew tasks --all 2>&1 | grep -v "^$"   # all available tasks
```

---

## Build and Test

```bash
# All tests
./gradlew test

# Single module
./gradlew :my-module:test

# Single class or method
./gradlew test --tests "com.example.FooTest"
./gradlew test --tests "com.example.FooTest.shouldWork"

# Force re-run without cleaning
./gradlew test --rerun

# Skip test cache entirely
./gradlew test --no-build-cache

# Full build (compile + test + check)
./gradlew build

# Compile only (no tests)
./gradlew classes testClasses
```

---

## Locating JUnit XML Reports

Gradle writes one XML file per test suite under:
```
<module-root>/build/test-results/<taskName>/TEST-<classname>.xml
```

Find all:
```bash
find . -path "*/build/test-results/*/*.xml" -not -path "*/.gradle/*"
```

Run the bundled parser from the project root:
```bash
python3 .agents/skills/kotlin-jvm-lsp-gradle-debug/scripts/parse-junit.py .
# or for a specific module root:
python3 .agents/skills/kotlin-jvm-lsp-gradle-debug/scripts/parse-junit.py ./my-module
```

---

## JUnit XML Schema

When `python3` is unavailable, parse manually. The key structure:

```xml
<testsuite name="com.example.FooTest"
           tests="5" skipped="1" failures="1" errors="0" time="0.42">

  <!-- passing test: no child elements -->
  <testcase name="shouldPass" classname="com.example.FooTest" time="0.01"/>

  <!-- skipped test -->
  <testcase name="shouldSkip" classname="com.example.FooTest" time="0.00">
    <skipped message="reason"/>
  </testcase>

  <!-- failed test -->
  <testcase name="shouldFail" classname="com.example.FooTest" time="0.03">
    <failure message="expected:<1> but was:<2>"
             type="org.junit.ComparisonFailure">
      full stack trace text here
    </failure>
    <system-out>println output here</system-out>
    <system-err>stderr here</system-err>
  </testcase>

</testsuite>
```

Key attributes to extract:
- `testsuite/@tests` — total count
- `testsuite/@failures` — assertion failures
- `testsuite/@errors` — unexpected exceptions
- `testsuite/@skipped` — skipped
- `testcase/failure/@message` — short failure description
- `testcase/failure` text content — full stack trace

---

## Debug Attach — JDWP

### Pre-flight: check port

```bash
lsof -i :5005 | grep LISTEN
# If in use — identify holder:
lsof -i :5005
# Kill holder:
lsof -ti :5005 | xargs kill -9
```

### Start suspended process

```bash
# Suspend application process
./gradlew run --debug-jvm

# Suspend before first test
./gradlew test --debug-jvm

# Suspend before specific test
./gradlew test --tests "com.example.FooTest" --debug-jvm
```

Terminal blocks — this is correct. The JVM waits for attach.

### Custom port (port 5005 occupied)

```bash
./gradlew test --debug-jvm \
  --jvm-args="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5006"
```

Or permanently in `build.gradle.kts`:
```kotlin
tasks.withType<Test> {
    jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5006")
}
```

### Raw JDWP JVM flag (no Gradle flag)

```
-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005
```

Options:
- `suspend=y` — process waits for debugger (use for tests)
- `suspend=n` — process starts immediately (use for long-running apps)
- `address=*:5005` — listen on all interfaces, port 5005

---

## Debug Attach Configs

### VS Code (`launch.json`)

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Attach to Gradle JVM (5005)",
      "request": "attach",
      "hostName": "127.0.0.1",
      "port": 5005
    }
  ]
}
```

### IntelliJ IDEA

`Run → Edit Configurations → + → Remote JVM Debug`
- Transport: Socket
- Debugger mode: Attach to remote JVM
- Host: `localhost`
- Port: `5005`

### Generic DAP (`nvim-dap` / other LSP clients)

```lua
-- nvim-dap example (Lua)
require('dap').configurations.kotlin = {
  {
    type = 'java',
    name = 'Attach Gradle JVM',
    request = 'attach',
    hostName = '127.0.0.1',
    port = 5005,
  },
}
```

### Terminal verify attach is possible

```bash
# Quick check — should print "succeeded" when JVM is waiting
nc -z 127.0.0.1 5005 && echo "port open" || echo "port closed"
```

---

## Recovery Sequence

```bash
# Step 1: stop daemons
./gradlew --stop

# Step 2: force re-run (no clean)
./gradlew test --rerun

# Step 3: clean compile + test
./gradlew clean test

# Step 4: refresh dependencies + clean build
./gradlew clean build --refresh-dependencies

# Step 5: nuke local Gradle module cache (last resort)
rm -rf ~/.gradle/caches/modules-2
./gradlew build
```

---

## Gradle Properties — Common Tuning

Add to `gradle.properties` in project root:

```properties
# Daemon heap (increase if daemon OOM)
org.gradle.jvmargs=-Xmx2g -XX:+HeapDumpOnOutOfMemoryError

# Parallel builds
org.gradle.parallel=true

# Build cache
org.gradle.caching=true

# Continuous build (re-run on file change)
# Use: ./gradlew test --continuous
```

Test JVM heap in `build.gradle.kts`:
```kotlin
tasks.withType<Test> {
    maxHeapSize = "1g"
    useJUnitPlatform()
}
```
