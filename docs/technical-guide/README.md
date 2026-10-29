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
