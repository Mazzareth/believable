# Repository Guidelines

## Project Structure & Module Organization
Source lives in `src/main/java`, with resources in `src/main/resources` and metadata templates in `src/main/templates`. Generated assets are written to `src/generated/resources` (do not edit by hand). Tests are in `src/test/java`. Build outputs land in `build/`, with runtime files under `run/` and logs in `logs/`.

## Build, Test, and Development Commands
Use Gradle (JDK 21 required; see `build.gradle` toolchain).
- `gradle build`: compile and package the mod.
- `gradle test`: run unit tests (JUnit 5).
- `gradle runClient`: launch a dev client.
- `gradle runServer`: launch a dev server (nogui).
- `gradle runGameTestServer`: execute registered GameTests and exit.
- `gradle runData`: generate data into `src/generated/resources`.

## Coding Style & Naming Conventions
Follow existing code patterns in `src/main/java`. Prefer standard Java conventions: 4-space indentation, braces on the same line, and `UpperCamelCase` for types, `lowerCamelCase` for methods/fields, `UPPER_SNAKE_CASE` for constants. Resource paths should be lowercase with underscores (e.g., `assets/<modid>/textures/...`).

## Testing Guidelines
Tests use JUnit 5. Place new tests in `src/test/java` with class names like `ThingTest`. Keep tests deterministic and avoid relying on external resources. Run them via `gradle test`. GameTests are enabled via the NeoForge run configs if used.

## Commit & Pull Request Guidelines
No Git history is available in this workspace, so follow a simple convention: imperative, concise subjects (e.g., `Add entity registry`), optional scope prefix (`client: Fix tooltip`). PRs should include a short summary, linked issues if applicable, and screenshots for visual changes.

## Configuration Notes
Mod metadata is generated from `src/main/templates` into `build/generated/sources/modMetadata`; don’t edit generated output directly. Keep `gradle.properties` in sync with mod identifiers and versions.
