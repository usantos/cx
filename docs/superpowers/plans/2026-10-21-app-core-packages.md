# Pacotes do APP e CORE Implementation Plan

> **For agentic workers:** Executar inline com superpowers:executing-plans e uma revisão independente final. Checkout existente, sem reset, staging, commits, push ou novo worktree.

**Goal:** Organizar APP e os demais módulos CORE por responsabilidade, preservando comportamento, testes e cobertura e seguindo a separação MVVM/Clean explicitada pelo usuário.

**Architecture:** Entradas Android/owners registrados mantêm FQNs. APP conserva composição/DI/navegação e organiza ambiente/runtime/tema. CORE organiza Common, Domain, Auth, Network, DS, Navigation, Presentation, Queue, Key Management e Testing por papel/capacidade; Security e Data mantêm agrupamentos que já são coesos. Três RepositoryImpl puros ainda em Domain serão movidos com seus testes para Data, conforme o desenho do usuário: Presentation → UseCase → contrato Domain → implementação Data.

**Tech Stack:** Kotlin/Compose, Hilt, Gradle, Python.

**Spec:** Pedidos do usuário de21/10/2026 nesta conversa: aplicar a organização a APP/CORE e respeitar Presentation (Screen/ViewModel/UiState/UiEvent), Domain (UseCase/Model/Repository interface) e Data (RepositoryImpl/DataSource/Retrofit/persistência/DTO).

## Global Constraints

Corpos, literais, persistência, endpoints, escopos Hilt, testes e mínimos preservados. Nada de novas fachadas, aliases públicos ou módulos vazios. Gradle serial e inputs congelados durante build. Emulador5560 e pacote nativo preservados; nenhuma instrumentação padrão do app. Não introduzir Room nem trocar SQLite/SharedPreferences nesta tarefa.

## Review Focus

Imports implícitos e wildcard; classes R/BuildConfig geradas; entradas Android e referências por nome; seletores JaCoCo e classes migrando de Domain para Data; debug/release e helpers internal entre pacotes.

## Tarefas

- [x] Inventariar e registrar snapshots, mapa de destinos/símbolos/JVM owners e seleção anterior de cobertura.
- [x] Organizar pacotes APP/CORE e extrair modelos/contratos mistos de Core Domain; mover três RepositoryImpl e dois arquivos de testes para Data.
- [x] Proteger Domain contra RepositoryImpl com um teste de política RED/GREEN; atualizar referências/gates necessários e preservar seleção de cobertura por mapeamento de donos.
- [x] Compilar debug/AndroidTest dos três flavors, rodar políticas/formatação, comparar cobertura e executar modernCheck/releaseCheck. Rodar AndroidTest de DS e Auth devido às superfícies movidas.
- [x] Obter revisão independente, registrar resultados/limites e atualizar documentação.

## Decisões

Prosseguir nesta sessão conforme a autorização para a organização. Preservar MainActivity, LoteriasApplication, serviço push, hosts debug e os três owners públicos registrados. Mover código de biblioteca por capacidade sem inventar MVVM para utilitários. As três implementações de sessão terão cobertura em Data e os mesmos testes; a mudança de dono entre gates será explicitada, sem ocultar classes ou reduzir mínimos. Métodos e asserções não serão alterados na reorganização.

## Registro da execução

208 arquivos movidos e três extraídos de Core Domain. O namespace registrado dos DTOs de transporte das features foi preservado; apenas seus imports mudaram. Compilação focada passou em1min49s, incluindo testes migrados e AndroidTest de app/DS/Auth.100 políticas passaram após os dois casos RED/GREEN. A revisão independente confirmou corpos/extrações e identificou dez keeps R8 antigos; os owners foram corrigidos com normalização inversa idêntica ao original. A comparação dos49 gates confirma nenhuma classe medida perdida globalmente e a transferência de sete classes de Repository de Domain para Data, sem mudar regras/mínimos/fontes/pré-requisitos. Matriz em execução com2.239 inputs congelados.

## Correção final de fronteira e cobertura

A conferência do desenho do usuário identificou CampaignViewModel acessando um contrato de Repository e coordenação de casos de uso em RepositoryCampaignConfiguration dentro de Data. O contrato e a mesma implementação foram movidos/renomeados para CampaignConfigurationUseCases/CampaignConfigurationUseCasesImpl em Domain; corpos, ordem de segurança/refresh e escopos preservados. Três políticas adicionais protegem Core Data contra execução de UseCases, Core ViewModels contra contratos no pacote repository e permitem DI apenas compor construtores (103 políticas aprovadas). Dois testes comportamentais adicionais cobrem os caminhos faltantes dos Repositories de sessão; cobertura Data95,146% e Domain96,868% aprovadas sem baixar mínimos. A matriz parcial foi cancelada e preservada como modern-release-before-campaign-interrupted.log. Nova matriz completa iniciada sobre2.239 inputs finais.

## Resultado final

209 arquivos de origem movidos e três extraídos, totalizando 212 destinos. Matriz final modernCheck/releaseCheck aprovada em 20min18s: 3.676 tarefas, 2.105 unitários e 196 aprovações instrumentadas, sem falhas/erros. Os três skips de perfil foram preservados e têm aprovação anterior na rodada KISS; nenhuma alteração visual. DS (21) e Auth (13) passaram em execução adicional de 1min59s, sem skips. Nenhum teste anterior perdido; dois testes comportamentais acrescentados. 103 políticas e formatação dos 1.889 arquivos Kotlin aprovadas.

Os 49 gates mantêm regras/mínimos/fontes e quatro listas de pré-requisitos; nenhuma classe medida perdida globalmente. Transferências: sete classes de Repository Domain → Data e duas de campanha Data → Domain. Os 8.022 literais de produção e 2.239 hashes de inputs finais permanecem iguais. AndroidTest dos três flavors compilado; releases PRD/TQS/Automação gerados com IDs/hashes conferidos, sem instalar o app oficial. Revisão independente e limite da posterior conferência local de campanha documentados em docs/APP_CORE_PACKAGES.md. Nenhum commit ou push.
