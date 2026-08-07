# ROS 2 Course App

A Kotlin Multiplatform (Android / iOS / Desktop) course reader that presents
[openAMRobot's ROS2_COMPLETE_COURSE.md](https://github.com/openAMRobot/openamr-platform-sw/blob/main/docs/ROS2_COMPLETE_COURSE.md)
as a navigable, offline app — structured the same way as
[`head_first_ooad_app`](https://github.com/AhmedOmran22/head_first_ooad_app):
feature-first, Clean Architecture (`presentation → domain → data`).

This is a separate project from the OpenAMR **operator** app built earlier in
this conversation — that one is a live ROS 2 control console; this one is a
static content reader. They don't share code.

## Architecture mapping (Flutter reference → this app)

| Reference app (Flutter)      | This app (KMP)                                  |
| ----------------------------- | ------------------------------------------------ |
| Cubit                         | `ScreenModel` + `StateFlow` (MVI-style)          |
| `get_it`                      | Koin                                             |
| `go_router`                   | Voyager                                          |
| bundled chapter Markdown      | bundled module Markdown, via Compose Multiplatform resources (`Res.readBytes`) |
| custom hand-rolled Markdown parser | **[multiplatform-markdown-renderer](https://github.com/mikepenz/multiplatform-markdown-renderer)** — see "Deliberate deviation" below |
| overview / key points / content tabs | Overview / Content / Activity tabs (see "Content mapping") |

## Content mapping

The course doc splits cleanly into 17 files (`composeApp/src/commonMain/composeResources/files/course/`),
pulled directly from the source doc's `##` sections — nothing retyped by hand.
Modules 1–13 each have a fixed shape (`### Learning Objectives`, numbered
subsections, `### Activity N`), which maps onto three tabs:

- **Overview** → the Learning Objectives bullets
- **Content** → all the numbered subsections (the actual teaching content, code included)
- **Activity** → the hands-on challenge at the end

The overview/setup intro, final project, best-practices reference, and cheat
sheet don't have that Objectives/Activity shape, so they render as a single
scrollable page instead of three tabs — `CourseModule.hasTabs` in
`CourseLocalDataSource.kt` controls which is which.

## Deliberate deviation from the reference app

The Flutter app hand-writes its own Markdown parser. This course doc leans
heavily on tables, deeply nested code fences (Python/C++/XML/bash), and ASCII
diagrams — exactly the content where a hand-rolled parser is most likely to
have subtle bugs. I used the established `multiplatform-markdown-renderer`
library (`com.mikepenz.markdown.m3.Markdown`) instead, which handles GFM
tables and code fences out of the box. Syntax highlighting for code blocks
(via the library's `-code` artifact) is a natural next step, left out for now
since wiring its highlighter API correctly needs verifying against the exact
library version in use.

## Running each target

```bash
# Android — from Android Studio (with the Kotlin Multiplatform plugin), or:
./gradlew :composeApp:installDebug

# Desktop
./gradlew :composeApp:run

# iOS — same caveat as the operator app: I did not hand-generate an
# iosApp.xcodeproj (too easy to silently corrupt by hand). Open this repo's
# root in Android Studio with the KMP plugin, or run the KMP wizard
# (https://kmp.jetbrains.com) into a scratch project and copy composeApp/ in —
# either generates a correct .xcodeproj wired to the ComposeApp framework, and
# the two Swift files in iosApp/iosApp/ drop in as-is.
```

## Honesty check

Written directly to files, not compiled here — this sandbox has no
JVM/Android/Gradle toolchain or access to Google's/Maven Central's package
hosts (only a fixed registry allowlist). I checked cross-file references by
hand and split the course doc programmatically (not retyped), but **build it
locally before relying on it**. Likely first things to hit if something's off:
the exact `multiplatform-markdown-renderer` `Markdown()` API for the pinned
version (0.39.2), or a Koin/Voyager version needing a bump.
