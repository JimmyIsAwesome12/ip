# Project context

This repository is a starter template for a greenfield Java project used in an introductory software engineering course in an undergraduate computer science program. Students use it as the starting point for their own projects.

# Default user context

Unless the user says otherwise, assume that you are assisting a student working on a project in this repository. If the user identifies themselves as an instructor or another project stakeholder, adapt your response to that role.

# Student profile

* Prior knowledge: Basic Java and OOP concepts.
* Level of programming experience: Decent
* IDE and level of expertise: Decent

# Guidance for interacting with users

* Explain the rationale for significant actions: what you did and why.
* Keep explanations brief but instructive, supporting learning through responsible use of AI. For example:

  * When suggesting a Git command, briefly explain what it does.
  * Add explanatory Javadoc comments to all classes and to nontrivial methods and fields when their purpose or behavior is not obvious.
  * Make generated code as self-explanatory as possible, and include explanatory comments where they improve understanding.
  * When faced with a design choice, choose the simplest option that is sufficient for the requirements, while briefly explaining relevant more advanced alternatives.

# Project-specific requirements

## Java version:

Ensure that Java 25 is used when running the application or build tasks. On macOS, use `sdk use java 25.0.3.fx-zulu` to switch to Java 25 if needed.

## Git

Use lightweight tags unless the user requests an annotated tag.
When proposing or creating a commit message, include enough detail to explain the rationale for the change.
Do not commit or push unless explicitly asked.

## Testing after code changes

After each update to the program's code, before considering the change done:

1. Update `test/ui-test-plan.md` if the change affects console behavior (new/changed commands, output format, etc.) — add or edit test cases so the plan still reflects expected behavior. No edit is needed for changes that don't affect the UI (e.g. pure refactors).
2. Invoke the `test-ui` skill to run the test plan against the program. If a test case fails, stop and fix the issue (or the test case, if the new behavior is intentional) before proceeding.
3. Update the JUnit tests under `src/test/java` so they still meet the coverage target below, then run `./gradlew test` and make sure it passes.

## JUnit test coverage

- **Target: cover roughly the top 50% highest-value methods** — the complex, core, or business-critical ones (currently: command parsing, the data-file format, `TaskList` operations, and date/time handling). Trivial getters, thin I/O wrappers (`Ui`), simple value objects (`ParsedCommand`), and glue code (`Lebron.main`) do not need dedicated tests.
- **JUnit tests must be kept up to date after every code change** to keep meeting this target: add tests for any new high-value method, and revise existing tests whose expected values the change alters. A code change is not done until `./gradlew test` passes.
- Test classes mirror the package of the class under test and are named `<ClassName>Test` (Gradle/JUnit convention), e.g. `lebron.parser.Parser` → `src/test/java/lebron/parser/ParserTest.java`.
- Long test method names may use the `featureUnderTest_scenario_expectedBehavior()` convention, e.g. `parse_todoWithoutDescription_throws()`.
