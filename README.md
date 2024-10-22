# SplinterSector Server

This is a Kotlin Multiplatform project targeting Server (and Desktop).

* `/composeApp` is for code that will be shared across all Compose Multiplatform non-server apps.
  It contains several subfolders:
  - `src/commonMain` is for code that’s common for all targets.
  - `src/desktopMain` is for code that targets Desktop.
  - Other folders may come and go...

* `/server` is for the Ktor server application.

* `/shared` is for the code that will be shared between all targets in the project.
  The most important subfolder is `src/commonMain`. If preferred, code can be added to the
  platform-specific folders here too. `src/jvmMain`, obviously, targets JVM.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…