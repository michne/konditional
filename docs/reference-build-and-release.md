# Build, docs, and release workflow

This repository now uses one consolidated toolchain for local validation,
documentation, and publication.

## Local build surface

Use the smallest command that proves your change:

- `make build` compiles all Gradle modules.
- `make test` runs the Kotlin test suite.
- `make detekt` runs static analysis.
- `make check` mirrors the default verification path used in CI.

The Gradle build is wired through the local convention plugins in
`build-logic/`, which keeps Kotlin, JUnit, and publishing settings in one
place instead of repeating them in each module build file.

## Documentation

Docs live under `docs/` and are built with Zensical using `zensical.toml`.

- `make docs-install` creates `docs/venv` and installs the docs toolchain.
- `make docs-build` builds the static site into `site/`.
- `make docs-serve` runs the local docs server.
- `make docs-clean` removes generated docs output.

This replaces the older split MkDocs/Docusaurus setup so there is one source
of truth for documentation configuration.

## Publishing

Publishing flows are intentionally routed through the Makefile and the scripts
in `scripts/`.

- `make validate-publish` checks credentials, version shape, and publishable
  module discovery.
- `make publish-plan PUBLISH_TARGET=<local|snapshot|release|github>` runs the
  non-interactive publication planner.
- `make publish-local`, `make publish-snapshot`, `make publish-release`, and
  `make publish-github` execute the corresponding publication task.

The publishable modules are the library modules that apply the
`konditional.published-library` convention plugin.

## GitHub Actions

GitHub Actions now follow the same split as local workflows:

- `CI` runs tests on Linux and macOS and builds the docs with Zensical.
- `Docs Pages` builds the Zensical site on `main`, uploads the generated
  `site/` directory as a Pages artifact, and deploys it through the GitHub
  Pages actions flow.
- `Publish Snapshot` validates the current version, tests the build, and only
  publishes when the version is a snapshot.
- `Release` runs from version tags, validates the release build, publishes the
  configured artifacts, and attaches built jars to the GitHub release.

That keeps CI, local commands, and release automation aligned around the same
Gradle tasks and docs entrypoints.
