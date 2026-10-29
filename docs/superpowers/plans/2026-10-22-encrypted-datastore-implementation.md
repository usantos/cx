# Encrypted DataStore and crash recovery Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans. Complete each task and its review before dependent work.

**Goal:** Persist app-owned local records with authenticated encryption and recover acknowledged betting drafts/navigation without repeating uncertain remote operations.

**Architecture:** A new Android `:core:persistence` implements a JVM contract in `:core:domain`. Features own schemas, migrations and business validation. A file/namespace has exactly one DataStore per process; storage errors preserve ciphertext and block unsafe transitions. Route recovery requires the current authenticated lease and the ordinary FilaBRA gate.

**Tech Stack:** Android DataStore Core 1.3.0, Kotlin/coroutines, AES-GCM, Android Keystore, existing Hilt composition, JUnit/Robolectric and connected tests.

**Spec:** docs/superpowers/specs/2026-10-22-encrypted-datastore-recovery-design.md

## Global constraints

- Preserve this dirty checkout; no resets, automatic commits, private-data exports or real bets/payments/reservations.
- Do not persist passwords, CVV, OTP, access tokens or UI objects as recovery checkpoints.
- Namespace by explicit installation or environment plus opaque owner; bind scope/schema to AAD.
- Never silently replace damaged ciphertext with empty data; never generate a replacement key when an existing record has lost its key.
- Confirm updateData before publishing acknowledged draft/step changes. No after-the-fact/debounced durability claims.
- Keep legacy data until encrypted commit/read validation succeeds; no whole-database deletion.
- Never restore payment success/authorization or queue PASSED; never auto-generate Rapidão during recovery.
- Maintain global LINE coverage >95% without changing exclusions/baselines/thresholds.
- The PDF describes implemented state only after verification; unresolved vendor/server limits remain explicit.

## Review focus

1. Process death between legacy read/encrypted commit/legacy cleanup does not lose or re-import newer data.
2. Missing/invalid keys, tampering, disk-full and concurrent updates preserve recoverable files and surface errors.
3. Account/environment changes cannot read or publish a previous owner's private checkpoint.
4. Cart graph, receipt, fingerprints and ID allocator remain atomic; uncertain uploads are never resent.
5. Partial sports/Super Seven/surprise/complement selections survive acknowledged writes; fresh contests/rules are revalidated.

## Tasks

### 1. Secure persistence boundary
- [x] Add `core/domain/.../persistence/SecureRecordStore.kt`: immutable `SecureSnapshot(revision, values)`, `SecureStoreScope(environmentKey, ownerKey, record)`, `SecureRecordStore.read/update` and factory contract; typed storage failures.
- [x] Add `:core:persistence` and DataStore Core dependency centrally. Package `br.gov.caixa.loterias.core.persistence` owns bounded binary serializer, AES-GCM envelope/AAD, Keystore keys and one-instance registry.
- [x] Write tests first: disk contains no sentinel, two equal payloads have distinct IVs, tamper/AAD/wrong key/schema/size failure, concurrent increments, reopen, missing key, atomic write failure. Root runs RED then implementation GREEN.
- [x] Verify real Android Keystore with controlled connected fixture; no app-user records inspected.

### 2. Preference inventory and migrations
- [ ] Migrate app-owned preference adapters enumerated in `/tmp/secure-store-migration-inventory.txt`; keep public TLS envelope integrity/high-water; preserve biometric protection and owner validation.
- [ ] Resolve installation environment before network/auth composition without blocking I/O on Main; convert synchronous contracts/callers where needed rather than fire-and-forget durability.
- [ ] Make read/modify/write operations and tutorial paired flags atomic; use explicit scope for installation versus account/environment records.
- [ ] Test migration restart, false/absent values, two owners/environments and failure recovery. Do not touch vendor-private SDK persistence.

### 3. Cart transactional migration
- [ ] Migrate complete graph, transfer receipt/fingerprints and sequence together into one encrypted document; adapt visitor/read/transfer/recent-purchase stores.
- [ ] Preserve PENDING/CONFIRMED/CONFLICT protocol and read-only reconciliation. Quarantine ambiguous legacy ownership/environment for review.
- [ ] Reuse existing graph validation/mappers; verify mirror/sports/month/teams, ID high-water, tampering, transaction rollback and process restart. Delete only validated migrated legacy rows after durable commit.

### 4. Draft and checkpoint protocol
- [ ] Add a versioned bounded feature-owned checkpoint model for full partial game state and route/step, not a submission BetDraft serializer.
- [ ] Serialize candidate commands and commit draft+step before UI acknowledgement. On failure keep prior state, report recoverable error and prevent submission/navigation.
- [ ] Reload fresh parameters and validate owner, environment, contest, bounds and eligibility before restoring. Do not restore confirmations/loading/success flags.
- [ ] Tests cover all modes, two rapid taps, write failure, contest change, logout/expiry, same-owner reauthentication and actual process death.

### 5. Root recovery and uncertainty
- [ ] Persist only strict allowlisted safe routes/minimal references; restore after valid startup/auth/FilaBRA checks. Remove/replace raw private SavedState payload where applicable.
- [ ] Route payments/Pix/purchase mutation aftermath to read-only reconciliation; suppress RapidList auto-generation for recovery; preserve intent without replay.
- [ ] Keep the ordinary queue contract. No position guarantee without SDK/server support.

### 6. Owned files and cleanup
- [ ] Keep large artifacts outside preference maps; encrypted private blob storage and short-lived explicit export grants/cleanup.
- [ ] Explicit no-backup rules and orderly per-owner credential cleanup; no automatic ciphertext/key erasure on authentication errors.

### 7. Quality and architectural verification
- [ ] Update ownership/module/spec checks for the persistence module; forbid new direct app-owned preference writes outside migration/approved SDK bridges.
- [ ] Review each task and run scoped tests centrally. Run full qualityGate/architecture/build and fresh coverage >95%; validate process-kill fixtures on device without financial transactions.

### 8. Technical PDF and delivery
- [x] Produce themed PDF with verified technology, modules/packages, MVVM/Clean/Feature First diagrams, KISS/DRY/CID, developer commands/reports, Remote Config, safe certificate/signing runbook, Chucker and its actual limits, DSsample, current-vs-Legado mappings and provenance-labelled photos.
- [x] Refresh all counts/statuses after implementation; render and inspect every PDF page before delivery.

## Decisions / ledger

- User explicitly authorized implementation after learning DataStore was still a proposal; the existing spec is the implementation scope.
- CID means Confidencialidade, Integridade e Disponibilidade, as defined in the approved persistence spec.
- DataStore 1.3.0 is selected as a stable AndroidX release with the required serializer/factory APIs; no multiplatform conversion.
- Current inventory and recovery maps are evidence aids, not authoritative contracts; verify exact consumers while implementing each task.

## Verified documentation delivery - 22/10/2026 21:00 São Paulo

- Fresh 67-module qualityGate, feature ownership/architecture and 99-operation backend baseline gates passed. 3,059 unit tests / 610 suites, zero failures/errors/skips. Global LINE 44,454 / 46,775 = 95.04%; unchanged source fingerprint e17d1d548829b3abb26c73ced655653e203fa2df4d4652bab96dc6a4e2d6f58a.
- Core DataStore/Keystore fixture passed on physical SM-G556B and emulator Android16 separately, without user data. Native sample Splash logo reviewed uncropped in recorded emulator launch; updated title installed.
- Technical PDF has 238 visually verified pages, 107 unique HTTP operations (all99baseline), illustrative request/response models,109DTO definitions, RemoteConfig/pin rotation/Chucker, architecture and photographed historical comparisons. Reproducible sources and frozen evidence live in docs/technical-guide and scripts/documentation.
- The PDF explicitly does not mark the complete persistence/recovery spec achieved. Environment/bootstrap, biometric adapter, signed keyset cache, mock overrides, persistent notification switches/private artifacts, explicit runtime uncertainty/quarantine resolution, all-screen/process-death validation and vendor queue position guarantees remain outstanding. Existing biometric credentials are encrypted but not migrated to DataStore.
- No real bets/payments/reservations, commits, Firebase publication, private key/certificate reads, baseline regeneration or coverage-exclusion changes were performed for this delivery. Existing declarative UI/test complexity has documented narrow local suppressions exposed by formatting.
