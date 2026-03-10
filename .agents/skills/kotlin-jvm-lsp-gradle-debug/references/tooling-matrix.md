# Kotlin JVM Tooling Matrix

## LSP Backends

### Option A: Kotlin Official LSP (`Kotlin/kotlin-lsp`)
- **Status**: experimental / pre-alpha (as of early 2025)
- **Strength**: official direction from JetBrains/Kotlin team
- **Architecture**: IntelliJ-based analysis engine exposed over LSP
- **Constraint**: JVM-only Kotlin Gradle projects; tight IntelliJ coupling
- **Use when**: coupling to IntelliJ engine is acceptable, or user wants official roadmap alignment

### Option B: FWCD Kotlin Language Server (`fwcd/kotlin-language-server`)
- **Status**: deprecated in favor of official LSP
- **Strength**: open-source, editor-agnostic, battle-tested in Neovim/Helix/VS Code
- **Constraint**: may lag on newer Kotlin/Gradle releases; community maintenance only
- **Use when**: strict open-source requirement, or official LSP is not yet stable enough for the project

## Debug Backends

### Option A: JVM JDWP (primary — always prefer)
- **Protocol**: Java Debug Wire Protocol, built into every JVM
- **Mechanism**: `--debug-jvm` Gradle flag; attach to port 5005
- **Editor support**: any editor with Java/JVM debug attach (VS Code, IntelliJ, nvim-dap, emacs-dap)
- **No install required** — works out of the box with any standard JDK

### Option B: FWCD Kotlin Debug Adapter (`fwcd/kotlin-debug-adapter`)
- **Status**: community adapter, pairs with fwcd language server
- **Strength**: Kotlin-aware source mapping in editors without Java plugin
- **Constraint**: depends on fwcd language server; deprecated ecosystem
- **Use when**: FWCD language server already in use and source mapping is poor with raw JDWP

## Selection Rules

1. **For debugging**: always start with JDWP (Option A). It has no dependencies, works everywhere, and is the most reliable. Only consider the debug adapter if source mapping is broken.

2. **For LSP**: assess whether the project requires the official Kotlin LSP features. If in doubt, use `fwcd/kotlin-language-server` for immediate compatibility and flag the deprecation tradeoff explicitly.

3. **Always state** which backend was chosen and why — including known limitations. Do not silently pick one.

4. **Do not mix** `fwcd/kotlin-language-server` with the official Kotlin LSP in the same workspace — they conflict on classpath indexing.

## Deprecation Posture

Deprecated tools should be used when they are the most reliable option for a given project, not avoided on principle. Reliability > novelty. State the deprecation status in the output so the user can plan migration at their own pace.
