# SPEC.md — Modernização e Refatoração Android

## 1. Papel do agente

Atue como um **Staff Android Engineer / Software Architect**, especialista em:

- Kotlin e Java
- Jetpack Compose e Material Design 3
- MVVM e Clean Architecture
- SOLID, DRY, KISS e Separation of Concerns
- Coroutines, StateFlow e Flow
- Dependency Injection com Hilt ou Koin
- Retrofit, OkHttp, Room e DataStore
- Testes unitários, integração e UI
- Segurança, performance e qualidade de código Android

Sua missão é analisar, planejar e executar a refatoração do aplicativo Android legado, atualmente desenvolvido em Java e XML, migrando progressivamente para Kotlin, Jetpack Compose, MVVM e Clean Architecture.

**Não realize uma simples conversão sintática de Java para Kotlin. A migração deve resultar em uma arquitetura moderna, organizada, testável, escalável e de fácil manutenção.**

---

## 2. Objetivo principal

Modernizar o aplicativo sem alterar indevidamente o comportamento funcional, contratos de APIs, regras de negócio, navegação, persistência ou funcionalidades existentes.

Objetivos obrigatórios:

1. Migrar código Java para Kotlin.
2. Migrar telas XML para Jetpack Compose.
3. Implementar MVVM com gerenciamento de estado unidirecional.
4. Separar responsabilidades em Presentation, Domain e Data.
5. Aplicar princípios SOLID e Clean Architecture.
6. Eliminar acoplamentos desnecessários entre UI, regras de negócio e infraestrutura.
7. Centralizar o gerenciamento de dependências.
8. Implementar estados explícitos de loading, success, empty e error.
9. Melhorar tratamento de erros e observabilidade.
10. Introduzir testes automatizados.
11. Preservar compatibilidade, performance e segurança.
12. Remover código legado apenas quando sua substituição estiver comprovadamente validada.

## 3. Diretrizes inegociáveis

### 3.1 Preservação funcional

Antes de modificar qualquer funcionalidade:

- Identifique o fluxo existente.
- Documente seus comportamentos e regras.
- Identifique dependências diretas e indiretas.
- Localize as chamadas de API.
- Verifique armazenamento local e cache.
- Identifique efeitos colaterais.
- Identifique permissões e integrações externas.
- Levante os estados de tela e seus tratamentos.
- Identifique o comportamento em casos de falha.
- Avalie os testes existentes.

Não elimine funcionalidades porque o código parece obsoleto.

Não modifique contratos de API sem necessidade comprovada.

Não altere nomes de campos de integração, serialização ou persistência de forma incompatível.

### 3.2 Migração incremental

A migração deve ser executada por funcionalidades, utilizando uma abordagem incremental.

Durante a transição, é permitido manter Java, Kotlin, XML e Compose no mesmo projeto.

Utilize interoperabilidade com `ComposeView`, `AndroidView` ou componentes legados somente quando necessário.

Não realize mudanças globais de arquitetura sem avaliação de impacto.

Preserve uma versão compilável e verificável a cada etapa concluída.

### 3.3 Sem implementação simulada em produção

É proibido:

- Criar respostas de API fictícias para substituir chamadas reais.
- Remover validações existentes.
- Ignorar erros silenciosamente.
- Criar implementações vazias apenas para compilar.
- Substituir integrações reais por mocks na aplicação de produção.
- Remover verificações de segurança.
- Alterar fluxos de autenticação sem preservar o protocolo existente.
- Introduzir dependências desnecessárias.

Mocks e fakes são permitidos exclusivamente em testes, previews e ambientes explicitamente preparados para isso.

---

## 4. Arquitetura obrigatória

Utilize a divisão:

- Presentation
- Domain
- Data

### Presentation

Responsável exclusivamente pela apresentação de informações, gerenciamento de estado da tela e interação com o usuário.

Pode conter:

- Composable Screens
- Stateless UI Components
- ViewModels
- UiState
- UiAction
- UiModels
- Navigation
- UI Mappers
- Temas e recursos visuais

Não deve conter:

- Chamadas Retrofit diretamente.
- SQL ou acesso ao Room diretamente.
- Acesso direto a SharedPreferences.
- Regras de negócio complexas.
- Implementações de Repository.
- Credenciais ou configurações sensíveis.
- Controle manual de threads para operações de negócio.

### Domain

Responsável por regras de negócio independentes do framework Android.

Pode conter:

- Entidades de domínio.
- Interfaces de Repository.
- Use Cases.
- Validadores de negócio.
- Regras e políticas de domínio.
- Tipos de resultado e erros de domínio.

Restrições:

- Não importar classes de Android.
- Não depender de Compose.
- Não depender de Retrofit ou Room.
- Não acessar recursos visuais.
- Não utilizar Context, Activity ou Fragment.
- Não conhecer implementações concretas da camada Data.

Os Use Cases devem possuir responsabilidade clara e única.

### Data

Responsável pelo acesso, processamento e disponibilização dos dados para o domínio.

Pode conter:

- Repository Implementations
- Remote Data Sources
- Local Data Sources
- API Services
- DTOs
- Entities de banco
- Mappers
- Cache
- DataStore
- Persistência
- Tratamento de erros técnicos

As implementações concretas devem satisfazer os contratos definidos na camada Domain.

**Regra de dependência:** Presentation depende de Domain; Data depende das abstrações de Domain. Domain não depende de Presentation nem de Data.

---

## 5. Estrutura de diretórios

Utilize preferencialmente organização por feature com camadas internas, mantendo módulos compartilhados quando fizer sentido.

Estrutura de referência:

```text
app/
 └── src/main/java/com/example/app/
     ├── core/
     │   ├── common/
     │   ├── designsystem/
     │   │   ├── theme/
     │   │   └── components/
     │   ├── navigation/
     │   ├── network/
     │   ├── database/
     │   ├── datastore/
     │   ├── security/
     │   ├── di/
     │   └── util/
     │
     ├── feature/
     │   ├── authentication/
     │   │   ├── presentation/
     │   │   │   ├── LoginScreen.kt
     │   │   │   ├── LoginRoute.kt
     │   │   │   ├── LoginViewModel.kt
     │   │   │   ├── LoginUiState.kt
     │   │   │   ├── LoginAction.kt
     │   │   │   └── components/
     │   │   ├── domain/
     │   │   │   ├── model/
     │   │   │   ├── repository/
     │   │   │   └── usecase/
     │   │   └── data/
     │   │       ├── repository/
     │   │       ├── remote/
     │   │       ├── local/
     │   │       ├── dto/
     │   │       └── mapper/
     │   │
     │   └── home/
     │       ├── presentation/
     │       ├── domain/
     │       └── data/
     │
     └── MainActivity.kt
```

A estrutura é uma referência, não uma exigência de criar diretórios vazios.

Não criar módulos Gradle para cada classe ou tela. Modularizar quando houver ganhos concretos de isolamento, reutilização, tempo de build ou responsabilidade arquitetural.

Evitar duplicação de implementações de infraestrutura entre features.

---

## 6. Padrão MVVM

Cada tela com estado de negócio deverá possuir uma ViewModel responsável por:

- Produzir UiState.
- Receber ações da interface.
- Executar Use Cases.
- Coordenar operações assíncronas.
- Converter resultados em estados de apresentação.
- Gerenciar estados de carregamento e erro.
- Preservar estado relevante durante mudanças de configuração.

Regras:

1. Utilizar `ViewModel` do Android Jetpack.
2. Preferir `MutableStateFlow` privado e `StateFlow` público.
3. Utilizar `viewModelScope` para coroutines.
4. Impedir mutações externas do estado.
5. Não armazenar Activity, Fragment ou View na ViewModel.
6. Evitar Context na ViewModel; quando indispensável, encapsular dependências Android.
7. Não acessar Retrofit diretamente.
8. Não adicionar lógica de renderização na ViewModel.
9. Não criar ViewModels gigantes.
10. Preferir estados imutáveis.
11. Utilizar `SavedStateHandle` para dados pequenos necessários à restauração de estado.
12. Não persistir objetos grandes no SavedStateHandle.

### 6.1 Estado da tela

Usar `data class` ou `sealed interface` conforme a necessidade da funcionalidade.

Exemplo:

```kotlin
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)
```

Para telas com estados mutuamente exclusivos, considerar uma sealed interface:

```kotlin
sealed interface ScreenUiState {
    data object Loading : ScreenUiState
    data object Empty : ScreenUiState
    data class Success(
        val items: List<ItemUiModel>
    ) : ScreenUiState
    data class Error(
        val message: String
    ) : ScreenUiState
}
```

Escolher o modelo mais adequado para cada fluxo.

### 6.2 Ações do usuário

Representar as ações de forma explícita:

```kotlin
sealed interface LoginAction {
    data class EmailChanged(
        val value: String
    ) : LoginAction

    data class PasswordChanged(
        val value: String
    ) : LoginAction

    data object Submit : LoginAction
}
```

Preferir um ponto de entrada claro para as ações ou métodos explícitos da ViewModel quando forem mais simples.

### 6.3 Fluxo unidirecional

Aplicar:

```text
User Interaction
       |
       v
   UI Action
       |
       v
    ViewModel
       |
       v
     UseCase
       |
       v
   Repository
       |
       v
   DataSource
       |
       v
     Result
       |
       v
    ViewModel
       |
       v
     UiState
       |
       v
   Compose UI
```

O Compose deverá observar o UiState e renderizar a interface de acordo com esse estado.

Para eventos como navegação, mensagens e confirmação de operações, modelar o resultado de maneira consistente com UDF. Evitar eventos perdidos durante mudanças de lifecycle ou coletores ausentes.

---

## 7. Jetpack Compose

Migrar XML para Composables seguindo boas práticas:

- Utilizar Material 3 quando compatível com a identidade visual atual.
- Preservar layout, textos, estilos, ícones, cores e acessibilidade existentes.
- Criar componentes reutilizáveis quando houver reutilização real.
- Usar state hoisting.
- Preferir componentes stateless.
- Separar a Route da Screen.
- Utilizar `collectAsStateWithLifecycle()`.
- Utilizar `remember` apenas para estado local de composição.
- Utilizar `rememberSaveable` quando o estado local precisar sobreviver à recriação suportada.
- Utilizar `LaunchedEffect` e `DisposableEffect` somente para efeitos apropriados.
- Não executar operações de rede durante composição.
- Evitar trabalho pesado na main thread.
- Preservar scroll, teclado, foco e tratamento de back.
- Garantir comportamento adequado com diferentes tamanhos de tela e fontes.
- Aplicar semantics e content descriptions.
- Fornecer previews dos componentes relevantes.

### 7.1 Route e Screen

Adotar separação semelhante a:

```kotlin
@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    onNavigateHome: () -> Unit
) {
    val uiState by viewModel.uiState
        .collectAsStateWithLifecycle()

    LoginScreen(
        state = uiState,
        onAction = viewModel::onAction
    )

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onNavigateHome()
        }
    }
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    onAction: (LoginAction) -> Unit
) {
    // Renderizar elementos de UI.
}
```

A Route integra ViewModel, lifecycle e navegação.

A Screen renderiza o estado e emite ações, permitindo previews e testes isolados.

Revisar o consumo de estados terminais para evitar navegações duplicadas.

---

## 8. Repositories e Use Cases

Toda fonte de dados deve ser encapsulada por Repository, com contratos de domínio claros.

Exemplo:

```kotlin
interface AuthRepository {
    suspend fun login(
        email: String,
        password: String
    ): AuthResult
}
```

Use Case:

```kotlin
class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): AuthResult {
        require(email.isNotBlank())
        return repository.login(email, password)
    }
}
```

A implementação concreta deve residir na camada Data.

Não criar Use Cases que apenas delegam trivialmente todas as chamadas sem trazer benefício concreto. Priorizar Use Cases quando houver regra de negócio, reutilização, orquestração ou quando ajudarem a estabelecer limites arquiteturais.

Regras complementares:

- Não retornar Response do Retrofit para Presentation.
- Não expor DTOs para a ViewModel.
- Não expor entidades do Room para a UI.
- Usar mappers explícitos.
- Preservar tratamento de erros de negócio.
- Não capturar CancellationException como erro comum.
- Propagar corretamente cancelamentos de coroutines.
- Não transformar erros técnicos em mensagens falsas de sucesso.

---

## 9. Dependency Injection

Inspecionar inicialmente o mecanismo existente.

Se já houver Hilt ou Koin bem estruturado, preservar o framework em vez de introduzir outro sem necessidade.

Caso não exista um mecanismo consistente, escolher uma única solução compatível com o projeto e justificar a escolha.

As dependências devem ser fornecidas por construtores sempre que possível.

Evitar:

- Instanciação manual de Repository dentro de Activity.
- Service locator global sem necessidade.
- Singletons com estado mutável descontrolado.
- Dependências estáticas que dificultem testes.
- Instanciar Retrofit em cada tela.
- Passar Context indiscriminadamente entre camadas.

Definir escopos corretos para ViewModels, Repositories e serviços.

---

## 10. Networking e tratamento de erros

Preservar as integrações e contratos existentes.

Analisar:

- Base URLs por ambiente.
- Interceptors.
- Headers.
- Autenticação.
- Refresh tokens.
- Timeouts.
- Certificados.
- SSL pinning.
- Serialização.
- Criptografia.
- Retry.
- Logs.
- Tratamento de erros HTTP.
- Erros de conectividade.
- Cache e comportamento offline.

Implementar resultados tipados quando necessário, separando:

- Sucesso.
- Falha de rede.
- Timeout.
- Erro de autenticação.
- Erro de autorização.
- Erro de servidor.
- Erro de validação.
- Erro inesperado.

Não registrar tokens, credenciais, senhas, dados pessoais ou payloads sensíveis.

Não modificar pinning, RASP, criptografia ou fluxos de autenticação sem análise específica e testes de regressão.

---

## 11. Navegação

Avaliar o sistema de navegação existente antes de substituí-lo.

Durante a migração híbrida:

- Preservar os fluxos de Activities e Fragments ainda não migrados.
- Integrar novas telas Compose sem quebrar a navegação.
- Preservar deep links, intents, extras e back stack.
- Preservar os fluxos de autenticação e logout.
- Evitar enviar objetos completos entre telas.
- Preferir IDs e argumentos tipados.

Quando a migração para Compose estiver suficientemente consolidada, avaliar a adoção de uma arquitetura de navegação unificada, utilizando a solução estável e compatível mais adequada ao projeto.

Não migrar toda a navegação de uma vez apenas para utilizar uma biblioteca nova.

---

## 12. Concorrência e lifecycle

Converter implementações legadas quando apropriado:

- AsyncTask → Coroutines.
- Threads manuais → Dispatchers adequados.
- Callbacks assíncronos → suspend functions ou Flow.
- LiveData → StateFlow, quando fizer sentido.
- Observable UI manual → StateFlow + Compose.
- Handlers para operações de negócio → Coroutines.

Não substituir automaticamente WorkManager por coroutines comuns.

Manter WorkManager em trabalhos persistentes e adiáveis que precisem sobreviver ao encerramento do processo.

Garantir:

- Structured concurrency.
- Propagação de cancelamento.
- Ausência de memory leaks.
- Escopos corretos.
- Tratamento de exceções.
- Ausência de operações bloqueantes na thread principal.

---

## 13. Qualidade e segurança

Aplicar:

- SOLID.
- DRY sem abstração excessiva.
- KISS.
- Single Responsibility.
- Encapsulamento.
- Imutabilidade sempre que viável.
- Nomes descritivos.
- Dependências explícitas.
- Separação entre código Android e regras de domínio.

Proibido introduzir:

- God Activities.
- God ViewModels.
- Repositories com responsabilidades desconexas.
- Classes `Utils` gigantes.
- Estados globais mutáveis desnecessários.
- `!!` sem justificativa.
- Capturas genéricas de exceções sem tratamento.
- Uso indiscriminado de `GlobalScope`.
- Operações de I/O na main thread.

Configurar análise estática e formatação com ferramentas compatíveis com o projeto, preferencialmente Detekt e ktlint quando aplicável.

---

## 14. Testes obrigatórios

Para cada funcionalidade migrada, implementar ou atualizar:

### Domain

- Testes unitários dos Use Cases.
- Validações de regras de negócio.
- Casos de sucesso e falha.
- Casos de borda.

### Data

- Testes dos Repositories.
- Testes dos Mappers.
- Testes de serialização relevantes.
- Testes de cache e persistência.
- Testes de tratamento de erros.

### Presentation

- Testes de ViewModel.
- Verificação das transições de UiState.
- Testes de loading, success, empty e error.
- Testes das ações do usuário.
- Testes de navegação.
- Testes Compose UI para fluxos críticos.

Utilizar bibliotecas adequadas já existentes quando possível, como JUnit, MockK ou Mockito, Turbine e Compose UI Test.

Não introduzir duplicidade desnecessária de frameworks.

Exigir testes de regressão para fluxos críticos existentes.

---

## 15. Estratégia de execução

### Fase 0 — Auditoria

Antes de alterar código, analisar:

1. Estrutura do projeto.
2. Módulos Gradle.
3. Versões de Java, Kotlin, AGP e Gradle.
4. SDK mínimo, compileSdk e targetSdk.
5. Bibliotecas e plugins existentes.
6. Activities, Fragments e layouts.
7. Adapters e componentes reutilizados.
8. Chamadas de rede.
9. Repositories existentes.
10. Regras de negócio.
11. Navegação.
12. Banco local.
13. Services, Receivers e Workers.
14. Segurança.
15. Testes.
16. Dependências obsoletas.
17. Fluxos críticos.

Gerar um documento `MIGRATION_AUDIT.md` com:

- Inventário de funcionalidades.
- Estrutura atual.
- Problemas arquiteturais.
- Riscos.
- Dependências.
- Prioridade das migrações.
- Estimativa de complexidade relativa.
- Critérios de validação.

### Fase 1 — Preparação

- Verificar compatibilidade das ferramentas.
- Configurar Kotlin e Compose.
- Configurar Compose Compiler e Compose BOM compatíveis.
- Preparar Dependency Injection.
- Criar estrutura base de arquitetura.
- Criar Design System inicial.
- Preparar tratamento de erros.
- Configurar testes.
- Garantir build funcional.

**Não atualizar todas as dependências indiscriminadamente.**

Atualizar apenas o necessário, preservando compatibilidade.

### Fase 2 — Extração das regras de negócio

Para cada feature:

- Identificar as regras existentes.
- Extrair entidades do domínio.
- Definir contratos de Repository.
- Implementar Use Cases relevantes.
- Migrar operações assíncronas.
- Isolar integrações Android.
- Implementar testes de domínio.

### Fase 3 — Refatoração da camada Data

- Criar Data Sources.
- Implementar Repositories.
- Criar DTOs.
- Criar Mappers.
- Organizar Retrofit/OkHttp.
- Organizar armazenamento local.
- Centralizar tratamento de erros.
- Cobrir comportamento com testes.

### Fase 4 — Migração da Presentation

Para cada tela:

1. Identificar seu contrato funcional.
2. Criar UiState.
3. Criar UiActions.
4. Criar ViewModel.
5. Integrar Use Cases.
6. Criar Compose Screen.
7. Criar componentes reutilizáveis.
8. Integrar navegação.
9. Preservar aparência visual.
10. Validar estados.
11. Implementar testes.
12. Remover implementação antiga após validação.

### Fase 5 — Consolidação

Após migrar as funcionalidades:

- Revisar navegação.
- Remover código morto.
- Eliminar dependências realmente obsoletas.
- Consolidar o Design System.
- Identificar duplicações.
- Revisar performance.
- Revisar segurança.
- Executar testes.
- Validar fluxos críticos.

---

## 16. Critérios de aceite

Uma feature só pode ser considerada migrada quando:

- [ ] Está implementada em Kotlin.
- [ ] Sua UI alvo utiliza Compose.
- [ ] Utiliza ViewModel quando há estado de tela.
- [ ] Seu estado segue fluxo unidirecional.
- [ ] Não acessa diretamente Data Sources pela UI.
- [ ] A lógica de domínio está corretamente isolada.
- [ ] Os Repositories possuem responsabilidades claras.
- [ ] A integração com APIs foi preservada.
- [ ] O comportamento funcional foi preservado.
- [ ] A navegação foi validada.
- [ ] Loading, empty, success e error foram tratados quando aplicáveis.
- [ ] Estados e efeitos colaterais foram testados.
- [ ] Foram incluídos testes automatizados relevantes.
- [ ] Não foram introduzidas regressões conhecidas.
- [ ] Não existem implementações temporárias indevidas.
- [ ] O build foi executado com sucesso.
- [ ] A execução foi validada em dispositivo/emulador quando o ambiente permitir.
- [ ] A implementação legada substituída foi removida com segurança.

---

## 17. Relatório obrigatório por feature

Após cada migração, atualizar `MIGRATION_PROGRESS.md` contendo:

**Feature:** nome.

**Situação anterior:** estrutura Java/XML e problemas encontrados.

**Nova implementação:** classes e arquivos criados ou modificados.

**Arquitetura:** Presentation, Domain e Data.

**Regras preservadas:** comportamentos e integrações mantidos.

**Testes executados:** comandos e resultados reais.

**Pendências:** problemas ainda não resolvidos.

**Riscos:** impacto potencial da migração.

**Status:** não iniciado, em andamento, bloqueado ou concluído.

Não afirmar que testes passaram sem executá-los.

---

## 18. Ordem de priorização

Utilizar os seguintes critérios:

1. Baixa dependência de outros módulos.
2. Baixo risco funcional.
3. Facilidade de validar comportamento.
4. Alto potencial de reutilização.
5. Impacto positivo na manutenção.
6. Complexidade arquitetural.
7. Criticidade do negócio.

Começar preferencialmente com uma feature relativamente simples, mas representativa, que permita validar o padrão arquitetural.

Não começar automaticamente pela autenticação, navegação principal ou outros componentes críticos.

---

## 19. Instruções operacionais para o Codex

Você está autorizado a analisar e refatorar o código existente respeitando os limites desta especificação.

Siga este ciclo:

**INSPECIONAR → PLANEJAR → IMPLEMENTAR → COMPILAR → TESTAR → VALIDAR → DOCUMENTAR.**

Regras de execução:

1. Inspecione arquivos reais antes de decidir mudanças.
2. Não presuma a existência de classes ou bibliotecas.
3. Não invente implementações de regras de negócio ausentes.
4. Reutilize contratos e componentes funcionais existentes.
5. Prefira alterações pequenas e verificáveis.
6. Não reescreva todo o projeto de uma só vez.
7. Não remova código que ainda é utilizado.
8. Registre decisões arquiteturais relevantes.
9. Resolva erros de compilação introduzidos pela migração.
10. Documente bloqueios que não possam ser resolvidos.
11. Não faça commits ou pushes automaticamente, salvo autorização explícita.
12. Não altere arquivos de configuração sensíveis desnecessariamente.
13. Respeite as convenções estabelecidas no projeto.
14. Não execute operações destrutivas sem justificativa e segurança.
15. Continue progressivamente para a próxima feature após a conclusão validada da anterior.

Se encontrar conflitos entre arquitetura ideal e compatibilidade com o legado, priorize preservar o funcionamento e implemente uma transição incremental.

---

## 20. Entregáveis finais

Produzir e manter:

- `MIGRATION_AUDIT.md`
- `MIGRATION_PLAN.md`
- `MIGRATION_PROGRESS.md`
- `ARCHITECTURE.md`
- Código refatorado.
- Testes automatizados.
- Componentes Compose.
- Configuração de dependências.
- Relatório de validação final.

Ao concluir, apresentar:

1. Quantidade de telas identificadas.
2. Quantidade de telas migradas.
3. Quantidade de classes Java ainda existentes.
4. Quantidade de classes Kotlin introduzidas.
5. Features concluídas e pendentes.
6. Problemas corrigidos.
7. Riscos remanescentes.
8. Testes efetivamente executados.
9. Resultado dos builds.
10. Próximas ações recomendadas.

**Inicie realizando a auditoria do repositório atual. Em seguida, estabeleça a ordem de migração e execute incrementalmente a primeira feature elegível, seguindo integralmente esta especificação.**
