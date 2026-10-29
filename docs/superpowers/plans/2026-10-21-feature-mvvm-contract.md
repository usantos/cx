# Contrato MVVM das features Implementation Plan

> **For agentic workers:** Executar nesta sessão com superpowers:executing-plans. Preservar o checkout existente; sem commits, reset, novo worktree ou mudanças de comportamento.

**Goal:** Confirmar e proteger MVVM em todas as features, usando Model, View, ViewModel, UseCases e Repository e DataSource/implementações conforme a responsabilidade exigir.

**Architecture:** View apresenta estado/ações do ViewModel; operações de aplicação usam casos de uso; repositories implementam portas de dados. Contratos e modelos existentes podem ser reutilizados entre features. Estados estritamente visuais podem permanecer em Compose; não criar classes ou módulos vazios.

**Tech Stack:** Kotlin/Compose, ViewModel/StateFlow, Hilt, Python e Gradle.

**Spec:** Instrução do usuário em21/10/2026: todas as features devem seguir MVVM com essas responsabilidades quando necessárias, preservando KISS e a validação anterior.

## Global Constraints

Preservar produção, persistência, APIs Android, testes e limiares. Gradle serial; nenhuma edição de inputs durante execução. Não instalar/desinstalar o pacote nativo nem alterar emuladores.

## Review Focus

- Repository acessado por View/estado/coordenador em presentation; rejeitar também nomes importados com alias e acessos qualificados.
- View chamando UseCase diretamente; manter navegação/DI como composição e Views puras válidas.
- ViewModel em arquivo com nome diferente; detectar a classe e alias do supertipo, sem depender apenas do nome do arquivo.
- Referências em comentários/strings; não tratar exemplos como dependências nas regras novas.
- Fluxo simples, stateless ou uso de domínio compartilhado; aceitar ausência de camadas próprias e operações de negócio inexistentes.

## Tarefas

- [x] Inventariar as14 features de apresentação e mapear Model/View/ViewModel/UseCases/Repository com caminhos existentes e reutilização de domínio.
- [x] Em scripts/test_feature_architecture.py, adicionar fixtures negativas/positivas para os limites de MVVM; observar RED antes de implementar.
- [x] Em scripts/check-feature-architecture.py, reforçar as fronteiras nos locais em que o gate atual depende apenas do nome do arquivo, sem exigir camadas artificiais.
- [x] Rodar a suíte Python e os gates Gradle reais, comparar hashes de produção/limiares com a validação anterior, revisar e documentar o contrato em docs/FEATURE_MVVM_CONTRACT.md e docs/APP_ARCHITECTURE.md.

## Ruling

O levantamento inicial encontrou ViewModels em todas as14 features e nenhum acesso direto a Repository/UseCase em suas Views. O trabalho será de verificação e proteção do contrato; só haverá alteração de runtime se surgir um desvio concreto. A matriz completa recém-concluída será referenciada como validação anterior, sem apresentá-la como reexecutada nesta tarefa.

## Atualização do levantamento

Foi encontrado um desvio em Cart: RemoveExpiredRemotePoolShare, em data, implementava UseCase e validava tipo/reserva. Corrigir com ExpiredPoolShareRepository (porta), RemoveExpiredPoolShare (regra no domínio) e RemoteExpiredPoolShareRepository (binding da sessão, DataSource e mapeamento). Preservar RemoveExpiredPoolShareUseCase e BoundCartRemovalRemoteDataSource como contratos, o texto da validação, a ordem dos efeitos, AppResult/cancelamento e todas as asserções anteriores. Adicionar dois testes de domínio e um de falha do binding; rodar matriz completa após a integração.

## Padronização explicitamente escolhida

O usuário escolheu `RepositoryImpl` e `DataSourceImpl`. Padronizadas 60 implementações e 10 contratos relacionados, incluindo fontes locais e dependências compartilhadas; consumidores, Hilt, testes e seletores de cobertura foram atualizados. O pós-login ganhou o contrato PostLoginAccountDataSource, sem alterar o corpo da operação ou o escopo Singleton. Os 8.022 literais de produção permanecem iguais; nenhum seletor JaCoCo perdeu classes anteriormente cobertas.

Os 98 testes de políticas, a formatação dos 1.876 arquivos Kotlin e a validação focada passaram. A matriz completa modernCheck/releaseCheck passou em 25min33s sobre 2.226 inputs congelados: 2.103 unitários, 196 aprovações instrumentadas e três skips condicionais de perfil já aprovados na rodada KISS anterior. Compilação AndroidTest dos três flavors passou. Os 49 conjuntos de regras de cobertura e os pré-requisitos foram preservados; nenhuma classe perdeu seleção após normalizar os nomes. Revisão independente sem achados restantes. Resultados e limites em docs/FEATURE_MVVM_CONTRACT.md.
