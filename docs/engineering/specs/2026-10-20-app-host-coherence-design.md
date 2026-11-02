# Coerência arquitetural do módulo app

## Objetivo

Adequar o módulo `app` à fronteira APP → features → CORE já adotada no projeto e à estrutura aprovada pelo usuário: o `app` inicializa a aplicação, compõe o tema e os serviços globais e conecta os grafos de navegação das features. Fluxos, estado, ViewModels, regras e implementações de dados continuam pertencendo às features e ao CORE. A mudança é estrutural; não altera comportamento, payloads, persistência, segurança, rotas ou contratos públicos.

## Evidência e problema atual

- `AppContainer` tem 278 linhas, dezenas de dependências de casos de uso, propriedades de runtime e implementa 13 interfaces `*NavigationDependencies` de features. A `MainActivity`, o `AppNavHost` e as navegações das features dependem dessa composição agregada.
- `app/build.gradle` declara dependências diretas de 13 módulos `feature:*:data`, além dos módulos de feature. Cada feature principal já declara o seu próprio módulo Data.
- `app` já possui entrada Android, composição, navegação, inicialização, configuração, runtime e bindings Hilt. `composition` contém `AppContent` e uma fachada de compatibilidade de inicialização.
- `ARCHITECTURE.md` e os gates definem que regras e implementações pertencem às features/CORE, que os bindings ficam com seus donos e que contratos públicos antigos devem ser inventariados. A seção anterior de navegação ainda descreve `AppContainer` como composição ativa; este desenho a atualiza.

## Organização desejada

```text
app/src/main/java/br/gov/caixa/loterias/apostas/
├── LoteriasApplication.kt        # entry point Android
├── MainActivity.kt               # janela, lifecycle e instalação do conteúdo
├── LoteriasApp.kt                # entrada/fachada pública, se necessária
├── composition/                  # tema raiz e composição do conteúdo
├── di/                           # bindings exclusivamente transversais do host
├── initialization/               # decisões e coordenação de startup do host
├── navigation/                   # NavHost raiz, sessão global e ações entre features
└── platform/                     # configuração Android exclusiva do host, se necessária

feature/<domínio>/                 # telas, ViewModels, Domain, Data, DI e entry point
core/<serviço-compartilhado>/      # contratos e infraestrutura reutilizados
```

`platform` só permanece para código que seja de fato específico à montagem do aplicativo; componentes Android reutilizáveis ou pertencentes a um domínio residem em CORE ou na feature correspondente. Não se criam camadas ou módulos vazios para preencher o desenho.

## Regras de composição

1. A navegação raiz registra os entry points/grafos tipados publicados pelas features e aplica apenas políticas globais: destino inicial, sessão autenticada, restauração e transições entre domínios. Cada feature obtém suas próprias dependências através de sua composição/DI e preserva os ViewModels existentes, seus StateFlows e sua lógica de rota.
2. `AppContainer` deixa de ser a dependência usada por `MainActivity`, `AppNavHost`, `AppContent` e pelas features. As responsabilidades de startup, sessão, segurança/rede, ambiente e configuração ficam em contratos estreitos de host ou nos donos CORE/feature existentes. A composição não lista todos os casos de uso de todos os domínios.
3. Para consumidores públicos antigos, qualquer tipo ou construtor que precise permanecer terá uma fachada fina e explicitamente inventariada, sem `@Inject`, provedores duplicados, estado, regras, efeitos colaterais ou uso pelo fluxo ativo. A fachada só permanece enquanto a superfície pública exigir compatibilidade.
4. `app` depende dos módulos de entrada das features e dos módulos CORE que realmente usa. Não declara dependências diretas de `feature:*:data`, nem contém binding de repository/use case de feature. Cada feature continua incluindo seu próprio Data e registrando seus bindings. A agregação Hilt deve ser validada na compilação real dos flavors antes de remover dependências redundantes.
5. Hilt no `app` limita-se a serviços e valores transversais de composição/ambiente que não tenham um dono mais específico. O escopo e a ordem de inicialização dos clientes, sessão, push, fila e segurança permanecem os atuais.
6. A raiz do pacote não contém implementação de feature. `composition` não contém DI duplicada. A compatibilidade pública antiga fica fora das camadas ativas e é listada por caminho, símbolo e destino.

## Invariantes

- Mesmas rotas, defaults, argumentos, navegação/restauração e owners de estado.
- Mesmos construtores/StateFlows públicos das ViewModels e mesmo comportamento de carregamento, cancelamento, retry e efeitos.
- Mesmas políticas de autenticação, identidade de sessão, segurança, fila, ambiente, push e callbacks de lifecycle.
- Sem novos endpoints, repositórios artificiais, `ViewModel` universal, API de domínio inventada ou regra duplicada.
- Sem alteração de conteúdo visual, recursos, payloads de rede, esquemas/chaves locais, escopos Hilt ou comportamento por flavor.
- Preservar todo o trabalho existente no checkout compartilhado; sem reset, clean, commit ou push.

## Critérios de aceite

- `AppContainer` não é injetado nem chamado por `MainActivity`, `AppContent`, `AppNavHost` ou por qualquer grafo ativo de feature; se a ABI exigir sua permanência, fica apenas a fachada compatível inventariada.
- Os 13 contratos de navegação são satisfeitos por composições pertencentes às features, sem passagem de uma lista central de use cases do app.
- `app/build.gradle` não depende diretamente de módulos `feature:*:data`; Hilt resolve os bindings por meio dos módulos donos, sem providers duplicados ou mudança de escopo.
- A árvore de fontes do app contém somente entry points, composição, DI transversal, inicialização e navegação global, além de fachadas exatas listadas no inventário.
- O gate de arquitetura reprova fixtures para: uso ativo de `AppContainer`, dependência app→feature Data, caso de uso/repository em composição, provider de domínio em app e camada de feature no pacote raiz. Também aceita a fachada de compatibilidade apenas quando ela encaminha ao dono registrado.
- Compilação e testes dos flavors, verificações Clean Architecture e comparações dos contratos públicos confirmam ausência de mudança funcional e preservação das APIs requeridas.

## Fora de escopo

Não reescrever o conteúdo das features, refatorar ViewModels sem necessidade para remover a composição agregada, redesenhar telas, alterar a árvore de módulos CORE/features, mudar a estratégia de autenticação, substituir Hilt, remover contratos públicos solicitados para compatibilidade ou afirmar homologação externa não executada.

## Decisões abertas para o plano

O plano deverá descobrir, antes de remover a agregação atual, como cada feature fornece seu entry point tipado e como Hilt agrega os módulos Data que hoje aparecem diretamente em `app`. A decisão concreta entre composição injetada por feature e registro de grafos será tomada pela menor mudança compatível com os contratos existentes, validada pelo grafo Gradle/Hilt e sem criar novo serviço de domínio.
