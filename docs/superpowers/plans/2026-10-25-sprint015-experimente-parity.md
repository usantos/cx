# Sprint015 Experimente parity implementation plan

> **For agentic workers:** Execute the approved user request continuously; independent cart, filter/account and notification transport tasks use the dispatching-parallel-agents workflow. Parent integrates presentation, runtime evidence and verification.

**Goal:** Compare `/Users/ubiracysantos/Projects/cx`, branch `Sprint015`, with Compose and port confirmed missing behavior.

**Architecture:** Retain Compose/CaixaDS and domain/data boundaries. Reuse captured-session network controls for the new notification inbox; historical synthetic messages must never appear as server history.

**Tech Stack:** Kotlin, Compose, Retrofit/OkHttp, Hilt, Android instrumentation, JVM tests.

**Spec:** User requests updated legacy through Experimente, implementation of differences and a list of novelties. User repeatedly requested continuous execution. Reference HEAD `5eb10f60d`.

## Global constraints

- Keep classic Home as default, minSdk29 and existing QR-only MLKit behavior.
- Preserve unrelated dirty work and third-party libraries.
- No real login, purchase, reservation or mutation of user server data during inspection.
- Legacy checkout sources remain unchanged. Validation-only resource overlay removes one identical duplicate string; package/launcher/Google configuration is isolated.
- Native bootstrap currently reports server connection failure; direct native login instrumentation is explicitly recorded as an inspection entry point, not proof of bootstrap success.

## Review focus

- Session change during notification fetch/confirmation must discard private data and prohibit cross-user mutation.
- Failed/partial mark-all must refresh truth and never claim every message was read.
- Loteca double/triple outcomes and visitor/remote cart mappings retain independent selections.
- Null-modality display filtering must preserve checkout amounts and nonnull future modalities.
- Resume refresh must not overwrite unsaved personal data, and initial resume must not duplicate loading.

## Tasks

- [x] Build isolated native reference and establish source fingerprint and validation overlays.
- [x] Compare cart, filters, account, campaign, pool details and notifications against current executable code.
- [x] Add regression tests and port structured Loteca cart rows/legend, readable title colors and null-modality filtering.
- [x] Add regression tests and port 130dp all-results retailer viewport and favorite-specific27character formatting.
- [x] Add safe clean-form resume refresh with dirty-form and deferred-initial-load controls.
- [x] Add independent session-bound notification inbox transport/domain and request/error tests.
- [x] Add central notification state/presentation: real history, Todas/Não lidas count, correct empty states, footer mutations/confirmation and retry.
- [x] Wire existing notification destination to central history, preserving preference destination.
- [x] Run focused JVM tests, compile/instrument affected UI and build PRD debug.
- [x] Record observed native/Compose states, already-ported novelties, remaining runtime limits and review final changes.

## Rulings

- Refresh Dados Pessoais only when clean; unlike native forced reset, unsaved user inputs and CEP-derived location are preserved.
- Zero own security alerts was a prior static-scan result; this feature change requires functional validation and does not inherit a new MobSF certification.
