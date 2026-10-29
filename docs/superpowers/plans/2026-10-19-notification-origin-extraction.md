# Notification origin and repository extraction implementation plan

> **For agentic workers:** Execute inline with superpowers:executing-plans. Preserve the shared dirty checkout and do not commit/push. The user's current instruction authorizes the bounded refactoring and investigation; a new product contract must not be inferred.

**Goal:** Verify the actual inbox origin, separate local responsibilities and Hilt bindings, and preserve all existing public APIs and StateFlows without inventing network contracts.
**Architecture:** Keep LocalNotificationRepository as the synchronized facade over destination-owned message and preference state. Isolate the existing demonstration catalog with explicit provenance. Split Hilt badge/read bindings into focused modules while retaining the existing NotificationsModule public methods as compatibility delegates. Keep all business protocols and the ViewModel unchanged.
**Tech Stack:** Existing Kotlin/JVM17, coroutines, Hilt/KSP, JUnit, Compose tests and JaCoCo. No new dependencies.
**Spec:** SPEC.md and the current user instruction; original references are under /Users/ubiracysantos/Projects/simlo-app-android-legacy-app-backup-20260903/src/main/java/.

## Constraints and evidence
- LocalNotificationRepository() and all seven repository operations keep their signatures and destination ownership.
- NotificationsModule's seven public functions retain parameter/return types and behavior; @Provides/@Singleton move to the appropriate modules for badge/read bindings, avoiding duplicate bindings.
- NotificationsViewModel, UiState, StateFlows, event queue, factory public API and Domain contracts stay unchanged.
- The legacy MensagensPushActivity calls MensagemPushModel.getAllMensagensMock; these are not real FCM or backend history records.
- FCM currently displays system banners, without inbox persistence or a category/date mapping.
- The known unread/read/push-registration endpoints do not establish the full inbox/delete/preferences contract. Never submit synthetic IDs to them or fabricate missing category/date semantics.
- User clarification: “É pra comparar com o Legado mesmo.” Preserve the legacy local inbox as the reference for this bounded extraction. Explicitly document its sample origin; this does not establish a real remote inbox or claim blanket SPEC3.3 compliance.
- Preserve the repository's single synchronized monitor, order, snapshot isolation, independent switches and fresh destination semantics.
- Preserve all coverage patterns/inputs/minima and all visual oracles. Do not create a replacement empty/success stub.

## Review focus
1. Unknown local IDs never call remote services or mutate another item.
2. Returned snapshots cannot mutate retained state; concurrent operations stay serialized by the facade.
3. Preference master/category switches remain independent and reset with a new destination.
4. Hilt scopes and per-destination repository ownership remain identical after splitting modules.
5. Real popup/read/push registration flows remain separate; sample IDs never enter authenticated PUTs.

## Tasks
- [x] Capture baseline sources/status/APIs and run Data/Domain/Notifications/App JVM suites.
- [x] Verify original Java model/activity, FCM receiver and known network contracts; document provenance and missing fields/operations.
- [x] Extract LegacyNotificationInboxSamples.kt, LocalNotificationMessages.kt and LocalNotificationPreferences.kt; facade delegates under its original monitor. Run existing characterization suite before and after; compare exact catalog/label bodies.
- [x] Extract UnreadNotificationsModule.kt and LatestNotificationModule.kt. Retain public compatibility delegates in NotificationsModule and the original local inbox provider. Preserve two singleton read bindings and unscoped badge/inbox bindings. Exercise factory using the real inbox provider and compile the Hilt component.
- [x] Apply the user’s legacy-reference clarification: retain local sample semantics, without inventing integration or treating sample records as real backend history.
- [x] Run Data/Domain/Notifications checks, three App JVM/lint/assemble debug flavors and existing architectural/security gates. Run affected Notifications Android tests on an available owned emulator; keep hardware/private homologation limits explicit.
- [x] Review source/binary public APIs, scope/coverage and update the five mandatory migration documents plus the origin audit with exact current results.

## Execution
Root owns edits, Gradle/device operations and reporting. JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-17.jdk/Contents/Home; offline Gradle. Evidence and baseline: build/verification/notification-origin-20261019/. No concurrent Gradles or production/payload logging. Existing dirty work is preserved; no worktree reset, staging, commit, push or security changes.

Completed: final-validation.log PASS (1m24s), all scoped checks and three debug flavors; 838 JVM and 10 Android, no failures/errors/skips. Fresh review without actionable findings. Existing real-source/global homologation limits remain documented.
