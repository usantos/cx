# Android Lint

`gradle/android-lint.gradle` centralizes the policy for every module applying
`com.android.application` or `com.android.library`. `lintQualityCheck` runs
`:app:lintPrdDebug` and each Android library's `lintDebug` once. Pure JVM modules
use the Kotlin static-analysis checks instead. Other app flavors remain available
through their existing tasks, and release builds retain AGP's release lint checks.

The default AGP rules remain enabled, including lifecycle, context leaks,
compatibility, deprecated/discouraged API use, performance, resources, and security.
Accessibility, hardcoded UI strings, permission, resource-color, recycling, and API
compatibility checks are explicitly enabled. AndroidX/Compose dependency-provided
lint checks remain active. No issue, source directory, or production module is
blanket-suppressed. Warnings and errors both fail validation; `abortOnError` and
`checkReleaseBuilds` stay enabled.

Each Android module is checked directly, so `checkDependencies=false` avoids
repeating the same dependency findings in consumers. Generated-source checking
uses AGP's default exclusion, and test-specific detectors retain their default
scope. Lint is the version bundled with AGP 9.2.1 (32.2.1); no additional lint
engine or parallel rule framework is installed.

## Run and inspect

```sh
./gradlew lintQualityCheck
# Additional environment checks when needed:
./gradlew :app:lintTqsDebug :app:lintAutomacaoDebug
```

AGP writes HTML, XML, and SARIF reports using deterministic, variant-qualified
paths, without competing output files between flavors:

- `app/build/reports/lint-results-prdDebug.{html,xml,sarif}`
- `<android-library>/build/reports/lint-results-debug.{html,xml,sarif}`

The baseline controls static-analysis debt only and has no effect on coverage.

## Legacy debt

Normal validation never creates or refreshes a baseline. When current findings
are verified as legacy debt, generate the canonical variants explicitly:

```sh
./gradlew -PupdateLintBaseline=true lintQualityBaseline
./gradlew lintQualityCheck
```

The first command intentionally writes `config/lint/baselines/<module-path>.xml`,
for example `config/lint/baselines/app.xml` and
`config/lint/baselines/core/auth.xml`. Review and version the exact findings.
Do not refresh baselines automatically in hooks or CI: that would accept new
violations. Only existing baseline files are loaded by ordinary validation.
Remove resolved entries rather than expanding debt to make a build green.

An empty baseline is unnecessary and can be removed after checking that its
module passes without it. Baselines are shared by a module's flavors, so inspect
any additional environment-specific findings separately rather than automatically
adding them. The aggregate baseline task deliberately updates one variant per
module to prevent concurrent writes to the same module baseline.

Gradle/AGP may need the SDK and generated stubs for lint analysis. A missing SDK,
failed compilation, or unavailable dependency is a failed validation, never an
approved lint result. Existing report files do not establish the result of a new
execution.
