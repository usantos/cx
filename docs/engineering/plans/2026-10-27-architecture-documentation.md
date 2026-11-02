# Architectural review and branded technical guide

Execute inline in current migration checkout. No commits, header edits, baseline changes or broad application refactoring.

1. Run repository quickQualityCheck, then explicitly lintQualityCheck and architecture gates if an unrelated stage prevents execution. Preserve every result and distinguish each scope.
2. Review architectural boundaries manually in parallel, including indirect ViewModel/platform dependencies that import gates cannot detect. Record concrete findings and remediation, not speculative violations.
3. Regenerate local API inventory and the technical guide using the existing Loterias CAIXA logo, fonts and blue/orange template. Add document control, a real linked table of contents, current checks, architectural findings and recent app changes. Correct obsolete minSdk and edition date. Label historical tests/coverage explicitly.
4. Render all PDF pages; inspect cover, contents, architecture, tables, API/code pages and appendix. Check text bounds, page overflow and output integrity. Link final PDF and review report.

Ruling: inspection requested, not a broad architectural migration. Two P2 findings will be documented with remediation; production behavior remains unchanged. Existing Ktlint debt will not trigger mass formatting or baseline expansion. A failed aggregate is never described as a passed qualityGate.


## Completed evidence
- Detekt passed (2,387 Kotlin files); Android lintQualityCheck passed across 36 Android modules. verifyCleanArchitecture, verifyFeatureModules and verifyAppArchitecturePolicy passed.
- quickQualityCheck failed at Ktlint: 10,946 reported occurrences / 488 files. Explicit Lint/architecture run succeeded without exclusions. No formatting or baseline changes applied.
- Manual review: 2 open P2 findings, in docs/architecture/REVIEW_20261027.md. No architecture migration was implied by a review-only request.
- Regenerated 111 HTTP operations / 110 DTOs, all 99 baseline contracts present. Current module count 68 and App minSdk29 corrected in current guide.
- Final PDF: 246 rendered pages, 143 outline entries, 20 working TOC links, no blank pages or out-of-page glyphs. Contact sheets and detailed visual checks completed. White table headings corrected for legibility on institutional blue.
- Source fingerprint unchanged: 4,171 production/configuration inputs. 37 protected baseline/config hashes and 2 protected header hashes match. Application code untouched; no commit/push.
- Evidence: build/verification/architecture-docs-20261027/{checks,lint-architecture,pdf-build}.log and final-pdf-validation.json.
