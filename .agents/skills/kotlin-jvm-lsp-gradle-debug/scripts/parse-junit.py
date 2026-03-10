#!/usr/bin/env python3
"""
parse-junit.py — Parse Gradle JUnit XML test results into a compact structured report.

Usage:
    python3 parse-junit.py [project-root]

Exit codes:
    0  All tests passed (or skipped)
    1  One or more failures/errors present
    2  No JUnit XML reports found

Output is line-oriented key=value / tagged blocks — minimal tokens, machine-readable.
"""

import sys
import os
import xml.etree.ElementTree as ET
from pathlib import Path
from dataclasses import dataclass, field
from typing import List


MAX_MSG_CHARS = 300   # max chars per failure message before truncation
MAX_STD_CHARS = 200   # max chars for stdout/stderr snippets


@dataclass
class Failure:
    suite: str
    method: str
    classname: str
    failure_type: str
    message: str
    body: str
    stdout: str = ""
    stderr: str = ""


@dataclass
class Summary:
    modules: int = 0
    total: int = 0
    skipped: int = 0
    failures: int = 0
    errors: int = 0
    time: float = 0.0
    failure_details: List[Failure] = field(default_factory=list)


def _trunc(s: str, n: int) -> str:
    s = (s or "").strip()
    return s if len(s) <= n else s[:n] + "…"


def parse_xml(path: Path, summary: Summary) -> None:
    try:
        tree = ET.parse(path)
        root = tree.getroot()
    except ET.ParseError as e:
        print(f"WARN: malformed XML skipped: {path} ({e})", file=sys.stderr)
        return

    # Handle both <testsuite> root and <testsuites> wrapper
    suites = [root] if root.tag == "testsuite" else root.findall("testsuite")

    for suite in suites:
        tests = int(suite.get("tests", 0))
        if tests == 0:
            continue  # skip empty suites (e.g., skipped module with no tests at all)

        summary.modules += 1
        summary.total += tests
        summary.skipped += int(suite.get("skipped", 0))
        summary.failures += int(suite.get("failures", 0))
        summary.errors += int(suite.get("errors", 0))
        summary.time += float(suite.get("time", 0.0))
        suite_name = suite.get("name", path.stem)

        for tc in suite.findall("testcase"):
            # Collect both <failure> and <error> elements
            problem_els = tc.findall("failure") + tc.findall("error")
            if not problem_els:
                continue

            stdout_el = tc.find("system-out")
            stderr_el = tc.find("system-err")

            for el in problem_els:
                msg_attr = el.get("message", "")
                body_text = el.text or ""
                # Prefer the body text for message if attribute is empty
                message = _trunc(msg_attr or body_text, MAX_MSG_CHARS)
                body = _trunc(body_text, MAX_MSG_CHARS) if msg_attr else ""

                summary.failure_details.append(Failure(
                    suite=suite_name,
                    method=tc.get("name", "?"),
                    classname=tc.get("classname", suite_name),
                    failure_type=el.get("type", "?"),
                    message=message,
                    body=body,
                    stdout=_trunc(stdout_el.text if stdout_el is not None else "", MAX_STD_CHARS),
                    stderr=_trunc(stderr_el.text if stderr_el is not None else "", MAX_STD_CHARS),
                ))


def find_reports(root: Path) -> List[Path]:
    # Gradle writes to: <module>/build/test-results/<taskName>/TEST-*.xml
    reports = []
    for p in root.rglob("build/test-results/**/*.xml"):
        # Exclude Gradle's own internal cache files
        parts = p.parts
        if ".gradle" in parts or "gradle-wrapper" in parts:
            continue
        reports.append(p)
    return sorted(reports)


def main() -> None:
    root = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else Path.cwd()

    reports = find_reports(root)

    if not reports:
        print(f"NO_REPORTS: no JUnit XML found under {root}/*/build/test-results/")
        print("HINT: run './gradlew test' first, or check --tests filter excluded all cases")
        sys.exit(2)

    summary = Summary()
    for r in reports:
        parse_xml(r, summary)

    passed = summary.total - summary.failures - summary.errors - summary.skipped
    fail_total = summary.failures + summary.errors

    # --- Primary summary line ---
    print(
        f"SUITE_SUMMARY:"
        f" modules={summary.modules}"
        f" total={summary.total}"
        f" pass={passed}"
        f" skip={summary.skipped}"
        f" fail={fail_total}"
        f" time={summary.time:.2f}s"
    )

    # --- Failure details ---
    if summary.failure_details:
        print(f"FAILURES[{len(summary.failure_details)}]:")
        for i, f in enumerate(summary.failure_details, 1):
            print(f"  [{i}] {f.classname}#{f.method}")
            print(f"       type={f.failure_type}")
            print(f"       msg={f.message!r}")
            if f.body:
                print(f"       detail={f.body!r}")
            if f.stdout:
                print(f"       stdout={f.stdout!r}")
            if f.stderr:
                print(f"       stderr={f.stderr!r}")
    else:
        print("FAILURES: none")

    # Exit 1 signals failures to the caller (agent can check $?)
    sys.exit(1 if fail_total > 0 else 0)


if __name__ == "__main__":
    main()
