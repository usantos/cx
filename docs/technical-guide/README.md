# Guia técnico - Loterias CAIXA

O PDF em `output/pdf/guia-tecnico-loterias-caixa.pdf` utiliza o template visual do projeto: logotipo Loterias CAIXA, fontes CAIXA Std, azul #005CA9, laranja #F39200, margens A4, paginação, marcadores e sumário navegável. A edição de 29/10/2026 inclui acesso aos dados e serviços por funcionalidade, a auditoria arquitetural, os contratos de MVVM/Redux, o calendário anual de ícones e evidências discriminadas por execução.

## Regeneração

Com Python 3.9+ e ReportLab/Pillow disponíveis:

```sh
python3 scripts/documentation/inventory_api.py
python3 scripts/documentation/build_technical_guide.py
```

O inventário é extraído dos arquivos de API/DTO do checkout. O gerador usa a data de America/Sao_Paulo e o minSdk declarado no App. O logo e as fontes vêm de core/design-system; nenhum recurso de identidade do aplicativo é alterado para produzir o documento.

## Evidências

Atualize `verification.json` e `quality-summary.json` somente depois de executar e ler as verificações. Esses arquivos não são preenchidos como aprovação automática pelo gerador. `verification-20261022.json` preserva a edição anterior. Os arquivos vigentes de cobertura/testes devem identificar sua execução e data; as cópias `coverage-summary-20261022.json` e `unit-tests-by-module-before-architecture-20261028.json` preservam o histórico.

A edição atual descreve ReduxKotlin nas 14 features, a ponte compartilhada de Core Presentation, ownership/lifecycle dos stores, actions semânticas e a fila durável de Betting. O manifesto vigente discrimina os testes de Core Presentation, das 14 features e App PRD; Lint, Detekt, formatação dos arquivos alterados, gates e Samsung têm resultados próprios. Os manifestos `verification-support-pilot-20261027.json` e `quality-support-pilot-20261027.json` preservam o piloto anterior.

A revisão atual está em [FULL_AUDIT_20261028.md](../architecture/FULL_AUDIT_20261028.md). O inventário distingue 68 módulos de 17 agrupadores, 46 ViewModels de features, 45 bindings Redux e 72 arquivos de reducers. `architecture-audit.json` fornece os dados usados pelo gerador, incluindo os achados corrigidos.

Ktlint global e formatação dos arquivos alterados são resultados diferentes. Há dívida global anterior, inclusive em um header protegido que esta auditoria não pode alterar; os baselines permanecem intactos. O manifesto de qualidade discrimina essa limitação. Build debug e testes JVM/Robolectric não comprovam release/R8, homologação do backend ou validação humana de TalkBack. O histórico Samsung de 27/10 foi preservado em `verification-samsung-20261027.json` e `quality-samsung-20261027.json`.

Antes de entregar outra edição, renderize todas as páginas e revise capa, sumário, tabelas, código e anexos. Não confunda geração bem-sucedida com validação de conteúdo ou diagramação.

A atualização de 28/10/2026 inclui `app_launcher_icon`, os cinco presets, capturas Samsung lado a lado, retorno ao padrão e os quatro atalhos em abertura fria/quente. Consulte [launcher-icons.md](launcher-icons.md). A validação das campanhas no aparelho usa a prévia DEBUG; não houve publicação global no Firebase.

## Consolidação das novidades de 28/10/2026

O [resumo completo](../UPDATES_20261028.md) reúne calendário anual/execução com app fechado, ApostaShake, preferências de navegação, correções MVVM/Clean/Redux, comentários em português, KTX `toUri`, cobertura e pre-commit. O guia incorpora o JSON completo de campanhas, instalação/bloqueios do hook e estado real do CI.

`pre-commit-summary.json` identifica a última execução integral: 3.510 testes Android/JVM, 190 de ferramentas, 21 de hooks e 33 verificações XML aprovados; JaCoCo 95,11%. O processo saiu com código 1 somente por Ktlint (6.897/363 arquivos). Os manifestos vigentes foram reconciliados com esse log; arquivos `*-architecture-20261028.json` conservam a captura anterior. A atualização documental não é uma nova execução de testes, instalação Samsung ou publicação Firebase.

## Arquivos para versionamento

O Git conserva os fontes, os manifestos JSON e as imagens necessárias em `docs/technical-guide/images/`. O resumo histórico Samsung usado pelo gerador está em `samsung-a11y-summary-20261027.json`; contém somente os dados apresentados no guia, sem depender dos logs/caches locais de `build/`.

`output/` contém exportações locais (PDF, APK/AAB, prévias e capturas intermediárias) e é ignorado pelo Git. O PDF final permanece disponível localmente e é regenerável pelos comandos acima. O gerador cria `output/pdf/` automaticamente em um clone novo. As três ilustrações do launcher usadas no guia foram preservadas byte a byte na pasta de imagens versionada. A compilação do aplicativo usa os recursos de `app/src/main/res/`.

## Acesso aos dados e serviços por funcionalidade - 29/10/2026

O PDF existente incorpora o capítulo **10 - Dados, classes e serviços por funcionalidade**, a partir de [DATA_ACCESS_BY_FEATURE.md](../DATA_ACCESS_BY_FEATURE.md). Ele reúne classes/casos de uso, serviços, endpoints, parâmetros de rota/query/body, respostas, filtros e JSON dinâmico, autenticação SSO, Firebase e persistência local. O dicionário tipado continua no Apêndice B.

O inventário contém 111 combinações de método/rota declarada, incluindo compatibilidade; as tabelas funcionais distinguem variantes e consumidores. O extrator também lê contratos em arquivos sem sufixo `Api.kt`, como `AccountVerificationBackend.kt`. Regenerar o PDF pelo comando existente preserva os capítulos anteriores, a identidade visual, a paginação e o sumário navegável. Esta atualização é documental e não representa nova execução de testes ou homologação no Samsung.

## Histórico do piloto Dynamic Feature Ajuda/FAQ — 02/11/2026

As fontes atuais incluem o [guia do piloto](../DYNAMIC_FEATURES.md), a seção de
[arquitetura](../APP_ARCHITECTURE.md) e a disponibilidade/entrega no inventário de
[acesso aos dados](../DATA_ACCESS_BY_FEATURE.md), já consumido pelo gerador do
capítulo funcional. O FAQ concreto passa ao split `faq`; contratos, Redux,
serviços e conteúdo obrigatório permanecem na base. `app_modules` schema 1 usa
`support_faq.enabled=false` por padrão; o guia traz os JSONs de habilitação e
desabilitação, os comandos TQS e bundletool local-testing com serial explícito.

O guia do piloto registra o AAB TQS Debug final (96.706.427 bytes e SHA-256),
a separação das definições DEX base/FAQ, testes unitários de entrega/configuração
e hashes preservados. A rodada focada aprovou FAQ (15 testes), Support
Presentation (46) e host/navegação (16), além de Ktlint/Detekt/Lint e contratos de
arquitetura/IDs/XML. A entrega local final no emulador dedicado passou em
7,264 s (um teste com configuração/conteúdo controlados e SDK real local-testing).
Os ajustes nativos de Robolectric usam SDK 36, recursos reais do APK universal e
SPI de testes alinhado ao upstream, sem snapshots nem edição de produção.

O build final de AAB/APK instrumental passou. A execução final
`quality-verified.log` terminou com BUILD SUCCESSFUL em 9 min 21 s: Ktlint,
Detekt, Android Lint, testes unitários e cobertura aprovados, além dos contratos
de arquitetura/IDs/XML/escopo incremental/Compose. `full-summary.json` registra
`passed=true`; JaCoCo global atingiu 95,40% (60.796/63.729 linhas, incluindo o FAQ
manual). `protected-final.json` confirmou os 45 arquivos protegidos sem alteração.
A proveniência final usa `quality-verified.log`, `device-final.log` e
`final-aab-inspection.json`, preservando as tentativas anteriores fora do checkout.

Quality gate não equivale ao runner de pre-commit com validação de index/workspace.
Não houve stage, commit ou push. Release/R8, Google Play, publicação Firebase e
Samsung permanecem NOT RUN para este piloto. Esta atualização de
fontes não exporta outro PDF nem modifica os manifestos históricos de evidência.


## Expansão de Dynamic Features — 03/11/2026

O [guia atualizado](../DYNAMIC_FEATURES.md) descreve as 15 capacidades, seus
splits, os três módulos install-time e os 12 on-demand, o JSON completo de
Remote Config e as proteções de jornadas em andamento. Configure `app_modules`
com **Tipo de dados: JSON** no console Firebase; no Android, a leitura continua
por `getString()` seguida da validação do contrato pelo app. A arquitetura e o
inventário funcional também incluem os contratos de entrega, sem transferir
serviços, ViewModels ou regras de negócio para os renderizadores.

As evidências da expansão são independentes do piloto: AABs Debug e Release/R8,
separação das classes em todos os splits, SDK local-testing das 15 capacidades,
Empty State/retry/reentrada por chave e geometria instrumental. O Release
permanece sem assinatura de produção. Os resultados atuais de qualidade e seus
limites estão registrados no guia; os números históricos acima não representam
a expansão. Esta atualização das fontes não exporta novamente o PDF.
