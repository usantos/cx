# Loterias CAIXA Android

Aplicativo Android modular em Jetpack Compose, estruturado com MVVM, Clean Architecture e injeção de dependências por Hilt.

## Estrutura

```text
app/                 composition root, único host Android e Navigation Compose
core/common/         Result, erros, dispatchers e contratos compartilhados
core/domain/         entidades, contratos de repository e casos de uso
core/data/           data sources, implementations de repository e mapeadores
core/preferences/    persistência Android de preferências atrás de contratos de domínio
core/network/        Retrofit, OkHttp, DTOs, logs e cURL sanitizado
core/design-system/  CaixaDS: tokens, tema, ícones e componentes
core/presentation/   contratos de UI, reducers e fluxos reutilizáveis
core/*               autenticação, navegação, chaves e integrações isoladas
feature/*            telas Compose, ViewModels, UiState e ações por funcionalidade
docs/                arquitetura, bibliotecas, telas, segurança e decisões
```

Não existe módulo `app-compose`: a aplicação Compose definitiva é o próprio `:app`. Também não existem layouts XML, Fragments ou Activities de feature. Permanecem somente o `AndroidManifest.xml` e uma `MainActivity` mínima, pois esses dois pontos são exigidos pela plataforma Android.

## Home selecionável

A Home v2 usa a apresentação do projeto CX (`HomeV2View`), portada para o host
Compose e ligada aos casos de uso existentes. Ela é o padrão, como no CX.

```bash
./gradlew :app:assembleTqsDebug -PhomeV2=true
./gradlew :app:assembleTqsDebug -PhomeV2=false
```

A mesma propriedade vale para `prd`, `tqs`, `automacao`, debug e release.
`false` mantém a home anterior. A ordem das modalidades é persistida localmente;
concursos, prêmios, disponibilidade de bolões e resultados vêm dos serviços.
Ver [auditoria da Home v2 e pendências de paridade](docs/HOME_V2_CX_PARITY_20261013.md).

## Verificação

```bash
./gradlew modernCheck
```

O comando compila o APK, executa lint, testes, gates de cobertura, regras do CaixaDS, validações de segurança e as restrições de dependência da Clean Architecture.

## Documentação

- [Arquitetura](docs/APP_ARCHITECTURE.md)
- [Bibliotecas e toolchain](docs/LIBRARIES.md)
- [Ambientes e product flavors](docs/ENVIRONMENTS.md)
- [Catálogo de telas](docs/SCREENS.md)
- [CaixaDS](docs/CAIXA_DS.md)
- [Estado da migração](docs/MIGRATION_STATUS.md)
- [Rotação segura de pins TLS](docs/SECURE_PIN_ROTATION.md)
- [Paridade e migração dos backends](docs/BACKEND_MIGRATION.md)
- [Backend mock e catálogo de cenários](docs/MOCK_BACKEND.md)
