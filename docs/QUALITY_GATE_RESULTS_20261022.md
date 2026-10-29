# Resultado da qualidade - medição atual em 22/10/2026

**APROVADO: 95.04% de cobertura global LINE**, 44,454 / 46,775 linhas de produção; 2,321 não cobertas, em 67 módulos. A condição permanece estritamente maior que 95%.

- **3059 testes unitários em 610 suítes**, zero falhas, erros ou ignorados.
- Ktlint, Detekt, Android Lint, testes e cobertura passaram no `qualityGate`.
- `verifyFeatureModules`, `verifyAppArchitecturePolicy` e `verifyLegacyBackendParity` passaram na mesma execução.
- Fontes/configurações idênticos antes e depois do gate (3.923 arquivos): `e17d1d548829b3abb26c73ced655653e203fa2df4d4652bab96dc6a4e2d6f58a`.
- Data/fuso da coleta: 2026-10-22T21:00:26-03:00.
- O teste instrumentado isolado de DataStore/Keystore passou em celular SM-G556B e emulador Android 16. Seus dados não entram na métrica unitária.
- Nenhum limite ou exclusão de cobertura foi reduzido. Baselines preservados; funções declarativas existentes têm supressões locais documentadas de complexidade/comprimento.
- Não foram executados CI remoto nem release/R8 nesta rodada. Recriação e reabertura não equivalem a teste completo de morte de processo em todos os fluxos.

Comando: `./gradlew qualityGate verifyFeatureModules verifyAppArchitecturePolicy verifyLegacyBackendParity --continue --console=plain -PhomeV2=false -PmockBackend=false -PnetworkLogging=false`.

Evidências congeladas: [verificação](technical-guide/verification.json), [cobertura](technical-guide/coverage-summary.json), [testes por módulo](technical-guide/unit-tests-by-module.json). Relatórios navegáveis: `build/reports/jacoco/aggregate/html/index.html` e `build/reports/quality/full-summary.json`.

## Histórico da integração inicial (substituído pela medição acima)

<details><summary>Medição anterior e validação inicial dos gates</summary>

# Resultado da integração de qualidade — 22/10/2026

**NÃO APROVADO para a meta global de 95%.** O gate detecta corretamente a lacuna de testes; não foram reduzidos os limites nem excluídas camadas de produção para alterar a métrica.

## Validação executada

- `./gradlew qualityGate --console=plain`: Ktlint, Detekt (perfil sintático), Android Lint e testes unitários passaram; `verifyCoverage` retornou falha por cobertura insuficiente.
- 1.927 testes unitários, zero falhas, erros ou ignorados, na seleção canônica de variantes.
- 65 módulos compilados e contabilizados; soma dos contadores de módulos igual ao agregado.
- Controle real de novas violações: Detekt rejeitou WildcardImport e Ktlint rejeitou parameter-list-spacing em um arquivo de prova ausente dos baselines; arquivo removido e baselines preservados.
- Execução direta de `scripts/git-hooks/pre-commit` no checkout real: retornou 1, mostrou todas as etapas e **COMMIT BLOCKED** com cobertura real de 42,87%; nenhum commit foi criado.
- 12 testes do verificador de cobertura e 11 testes isolados de Git/hooks passaram, incluindo ponderação, arredondamento, XML ausente, módulo sem testes e commit bloqueado por cobertura de fixture de 93%.
- Grafos Gradle com análise excluída (inclusive lintAnalyzePrdDebug), cobertura excluída, atualização de baseline ou formatação misturadas à validação foram rejeitados; hashes dos baselines permaneceram iguais.
- `detektBaseline`, `ktlintBaseline` (incluindo check final) e `lintQualityBaseline` executados explicitamente.
- `quickQualityCheck :app:assemblePrdDebug`: passou e gerou o APK PRD Debug.
- `quickQualityCheck :app:lintTqsDebug :app:lintAutomacaoDebug`: passou, preservando as verificações dos outros flavors.
- Nenhuma formatação automática, instalação no celular, aposta, pagamento ou reserva foi realizada. Testes instrumentados não foram executados nesta integração.
- CI remoto não executado; secrets e proteção de branches precisam ser configurados no GitHub.
- Detekt estável 1.23.8 não oferece resolução de tipos compatível validada com Kotlin 2.4. A regra SuspendFunWithCoroutineScopeReceiver permanece pendente; detalhes e fontes oficiais no [guia](QUALITY_GATE.md).

## Cobertura real (testes unitários)

**42.87%** = 17,757 / 41,423 linhas elegíveis. Não cobertas: 23,666. Mínimo: 95,00%.

Os maiores volumes de linhas não exercitadas estão em core:design-system e nas apresentações de Betting, Tickets, Home, Cart, Retail e Payment. O cálculo inclui toda a UI escrita à mão; a falta de testes não remove suas linhas do denominador.

| Módulo | Cobertura LINE | Cobertas / elegíveis | Não cobertas |
|---|---:|---:|---:|
| `:app` | 14.93% | 143 / 958 | 815 |
| `:core:auth` | 56.31% | 482 / 856 | 374 |
| `:core:common` | 100.00% | 58 / 58 | 0 |
| `:core:data` | 97.50% | 117 / 120 | 3 |
| `:core:design-system` | 21.51% | 1080 / 5021 | 3941 |
| `:core:domain` | 97.69% | 127 / 130 | 3 |
| `:core:key-management` | 99.35% | 154 / 155 | 1 |
| `:core:navigation` | 73.20% | 325 / 444 | 119 |
| `:core:network` | 97.35% | 1248 / 1282 | 34 |
| `:core:presentation` | 68.61% | 94 / 137 | 43 |
| `:core:queue` | 22.79% | 31 / 136 | 105 |
| `:core:security` | 26.36% | 34 / 129 | 95 |
| `:core:testing` | 93.33% | 14 / 15 | 1 |
| `:feature:account:api` | 100.00% | 3 / 3 | 0 |
| `:feature:account:data` | 87.92% | 233 / 265 | 32 |
| `:feature:account:domain` | 100.00% | 167 / 167 | 0 |
| `:feature:account:presentation` | 39.33% | 706 / 1795 | 1089 |
| `:feature:account:transport` | 96.40% | 107 / 111 | 4 |
| `:feature:betting:data` | 95.62% | 459 / 480 | 21 |
| `:feature:betting:domain` | 97.03% | 359 / 370 | 11 |
| `:feature:betting:presentation` | 35.13% | 1758 / 5004 | 3246 |
| `:feature:betting:transport` | 98.35% | 238 / 242 | 4 |
| `:feature:cart:api` | 100.00% | 51 / 51 | 0 |
| `:feature:cart:data` | 67.92% | 597 / 879 | 282 |
| `:feature:cart:domain` | 98.77% | 241 / 244 | 3 |
| `:feature:cart:presentation` | 38.66% | 970 / 2509 | 1539 |
| `:feature:cart:transport` | 99.32% | 147 / 148 | 1 |
| `:feature:home:data` | 0.00% | 0 / 4 | 4 |
| `:feature:home:domain` | 100.00% | 19 / 19 | 0 |
| `:feature:home:presentation` | 26.64% | 821 / 3082 | 2261 |
| `:feature:marketplace:api` | 100.00% | 126 / 126 | 0 |
| `:feature:marketplace:data` | 100.00% | 122 / 122 | 0 |
| `:feature:marketplace:domain` | 100.00% | 109 / 109 | 0 |
| `:feature:marketplace:presentation` | 38.68% | 658 / 1701 | 1043 |
| `:feature:marketplace:transport` | 98.55% | 68 / 69 | 1 |
| `:feature:marketplace-filter:presentation` | 43.29% | 371 / 857 | 486 |
| `:feature:notifications:data` | 71.94% | 100 / 139 | 39 |
| `:feature:notifications:domain` | 100.00% | 38 / 38 | 0 |
| `:feature:notifications:presentation` | 34.25% | 225 / 657 | 432 |
| `:feature:onboarding:data` | 57.14% | 8 / 14 | 6 |
| `:feature:onboarding:domain` | 100.00% | 14 / 14 | 0 |
| `:feature:onboarding:presentation` | 16.97% | 132 / 778 | 646 |
| `:feature:payment:data` | 98.23% | 277 / 282 | 5 |
| `:feature:payment:domain` | 99.47% | 186 / 187 | 1 |
| `:feature:payment:presentation` | 37.84% | 755 / 1995 | 1240 |
| `:feature:payment:transport` | 100.00% | 19 / 19 | 0 |
| `:feature:responsible-gaming:data` | 96.49% | 55 / 57 | 2 |
| `:feature:responsible-gaming:domain` | 100.00% | 48 / 48 | 0 |
| `:feature:responsible-gaming:presentation` | 21.65% | 202 / 933 | 731 |
| `:feature:responsible-gaming:transport` | 93.75% | 30 / 32 | 2 |
| `:feature:results:data` | 95.58% | 108 / 113 | 5 |
| `:feature:results:domain` | 92.98% | 53 / 57 | 4 |
| `:feature:results:presentation` | 21.30% | 197 / 925 | 728 |
| `:feature:results:transport` | 100.00% | 68 / 68 | 0 |
| `:feature:retail:data` | 95.54% | 257 / 269 | 12 |
| `:feature:retail:domain` | 100.00% | 130 / 130 | 0 |
| `:feature:retail:presentation` | 29.23% | 594 / 2032 | 1438 |
| `:feature:retail:transport` | 100.00% | 13 / 13 | 0 |
| `:feature:support:data` | 100.00% | 32 / 32 | 0 |
| `:feature:support:domain` | 100.00% | 20 / 20 | 0 |
| `:feature:support:presentation` | 29.38% | 188 / 640 | 452 |
| `:feature:support:transport` | 84.62% | 11 / 13 | 2 |
| `:feature:tickets:data` | 94.62% | 545 / 576 | 31 |
| `:feature:tickets:domain` | 85.89% | 140 / 163 | 23 |
| `:feature:tickets:presentation` | 31.80% | 1075 / 3381 | 2306 |

## Dívida estática registrada

Detekt: **6,204**; Ktlint: **43,375**; Lint: **514** em 34 baselines de módulos Android.

Zero violações novas nos relatórios normais após os baselines. Isso não significa ausência de problemas no código legado.

| Regra Detekt | Ocorrências no baseline |
|---|---:|
| MagicNumber | 3460 |
| MaxLineLength | 1700 |
| WildcardImport | 772 |
| CyclomaticComplexMethod | 107 |
| LongMethod | 51 |
| TooManyFunctions | 31 |
| LongParameterList | 24 |
| EmptyCatchBlock | 16 |
| TooGenericExceptionCaught | 11 |
| ReturnCount | 9 |
| UnusedParameter | 9 |
| LargeClass | 8 |
| SwallowedException | 3 |
| ThrowsCount | 2 |
| NestedBlockDepth | 1 |

GlobalCoroutineUsage, NewLineAtEndOfFile, ForbiddenComment, UnusedPrivateMember e TooGenericExceptionThrown: nenhuma ocorrência no baseline. SuspendFunWithCoroutineScopeReceiver não foi medida por falta de resolução de tipos.

| Detector Lint | Ocorrências no baseline |
|---|---:|
| UnusedResources | 227 |
| IconLocation | 125 |
| UseKtx | 32 |
| IconDuplicates | 20 |
| IconDensities | 13 |
| VectorPath | 11 |
| IconDipSize | 10 |
| GradleDependency | 9 |
| InlinedApi | 9 |
| VectorRaster | 7 |
| ComposableNaming | 7 |
| AutoboxingStateCreation | 5 |
| NewerVersionAvailable | 4 |
| IconDuplicatesConfig | 4 |
| ModifierParameter | 4 |
| DiscouragedApi | 3 |
| GifUsage | 3 |
| FrequentlyChangingValue | 3 |
| LockedOrientationActivity | 2 |
| SimilarGradleDependency | 2 |
| UseCompatLoadingForDrawables | 2 |
| UseOfNonLambdaOffsetOverload | 2 |
| QueryPermissionsNeeded | 2 |
| UnspecifiedRegisterReceiverFlag | 1 |
| AndroidGradlePluginVersion | 1 |
| DataExtractionRules | 1 |
| LocalContextResourcesRead | 1 |
| ModifierFactoryExtensionFunction | 1 |
| IconXmlAndPng | 1 |
| SourceLockedOrientationActivity | 1 |
| CheckResult | 1 |

## Artefatos e próximos requisitos de aprovação

- [Guia, versões, comandos, regras, exclusões e relatórios](QUALITY_GATE.md).
- [Instalação conservadora do hook e configuração de CI](quality/hooks-and-ci.md).
- Relatório navegável: `build/reports/jacoco/aggregate/html/index.html`.
- Dados atuais estruturados: `build/reports/jacoco/aggregate/summary.json`.
- Resumos separados: `build/reports/quality/full-summary.json` e `quick-summary.json`.
- Para aprovar o gate é necessário ampliar os testes sobre as linhas de produção existentes até alcançar 95%; não há exceção para esta dívida.
- Para concluir a regra semântica do Detekt, revalidar uma versão estável compatível ou decidir explicitamente sobre uma alpha, sem mudar o toolchain silenciosamente.
- O hook foi criado; a instalação no repositório do desenvolvedor é explícita com `sh scripts/install-git-hooks.sh`. Não foi criado commit nem alterada configuração remota de proteção de branch.


</details>
