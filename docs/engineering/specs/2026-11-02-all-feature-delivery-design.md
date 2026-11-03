# Entrega por módulo em todas as features

## Solicitação e estado atual

Expandir o piloto de FAQ para todas as famílias de features, controlando a disponibilidade por `app_modules`, com Empty State quando desabilitado. A expansão deve preservar dados, jornadas, MVVM, Clean Architecture, ReduxKotlin, Design System, IDs, headers e baselines.

O piloto existente tem entrega local comprovada e quality gate aprovado: 3.715 testes, JaCoCo global 95,40%. Suas alterações ainda estão no workspace, sem staging/commit/push. Esta especificação descreve a expansão; não declara que as demais features já foram migradas.

Há 14 famílias em `feature/`: Account, Betting, Cart, Home, Marketplace, Marketplace Filter, Notifications, Onboarding, Payment, Responsible Gaming, Results, Retail, Support e Tickets. Support terá o FAQ existente e a ajuda opcional por modalidade como capacidades distintas. Core, host, modelos, serviços e proprietários de estado continuam na base.

## Decisão aprovada

O usuário aprovou a alternativa A (híbrida) em 02/11/2026: Account, Home e Responsible Gaming junto com o aplicativo; as demais UIs sob demanda. Esta aprovação autoriza implementar toda a tabela, sem repetir a pergunta de aprovação. As alternativas abaixo registram a decisão e seu contexto.

## Abordagens para a entrega inicial

**A — Híbrida, aprovada.** Todas as famílias ganham conteúdo real em splits. As UIs essenciais de Account, Home e Responsible Gaming são instaladas junto com o aplicativo; as demais usam entrega sob demanda. Isso mantém entrada, navegação e caminhos obrigatórios disponíveis sem um download adicional na primeira abertura. A flag desses módulos essenciais controla entradas opcionais, mas não evita seu download inicial.

**B — Todas as UIs sob demanda.** Inclusive Account e Home precisam de download antes do primeiro uso. A base mostra preparação/erro/Empty State e oferece recuperação quando não houver rede ou entrega disponível. Bootstrap, infraestrutura e caminhos obrigatórios continuam disponíveis na base; conteúdos compartilhados com esses caminhos permanecem nela. Essa opção maximiza o controle da solicitação de instalação por acesso, mas não garante login/Home prontos na primeira abertura offline.

A alternativa A foi escolhida antes de mover os renderizadores essenciais. O modo de entrega é definido no AAB; o Firebase não altera `install-time` para `on-demand` depois da publicação.

## Corte de arquitetura

Preservar bibliotecas `presentation` base com contratos, estado, ViewModels/Redux, DI, navegação, coordenadores de plataforma e casos de uso. Mover os renderizadores Compose e recursos exclusivos para novos módulos `com.android.dynamic-feature`, usando entradas tipadas como no FAQ. Não converter indiscriminadamente `presentation` inteiro: a base e outras features importam seus grafos, estados, fábricas e serviços.

Cada família terá contratos específicos de estado e ações, sem `Any`, mapas genéricos de tela ou nomes de classes vindos do Firebase. Recursos referenciados pelo bootstrap ou por outras famílias continuam com proprietário base. Catálogos/títulos de navegação permanecem disponíveis antes de instalar uma UI.

Extrair a entrega genérica do piloto para infraestrutura compartilhada: uma configuração Firebase por aplicação, registro local confiável, adaptador Play, estados de entrega e ponte Redux/ViewModel. Manter contratos de entrada específicos por feature; não copiar o instalador, listener ou polling do FAQ quinze vezes nem criar um localizador global de serviços.

Identidades de projetos/splits abaixo são propostas. Usar nomes únicos, confirmar os nomes efetivos no AAB e testar o registro; não criar quinze projetos cujo último nome seja simplesmente `ui`.

| Capacidade JSON | Projeto de UI proposto | Conteúdo real a extrair | Entrega em A |
| --- | --- | --- | --- |
| `account` | `:feature:account:account_ui` | Login, dados pessoais, cadastro e token; coordenação de SSO/biometria/sessão na base | Junto com o app |
| `home` | `:feature:home:home_ui` | Conteúdo Home atual/V2, resultados resumidos e componentes exclusivos | Junto com o app |
| `onboarding` | `:feature:onboarding:onboarding_ui` | Introdução/tutoriais opcionais e recursos exclusivos; Splash/fila/update/recuperação na base | Sob demanda |
| `betting` | `:feature:betting:betting_ui` | Modalidades, cartelas, rapidão, favoritos, combos e tutoriais | Sob demanda |
| `marketplace` | `:feature:marketplace:marketplace_ui` | Bolões, favoritos de lotéricas, detalhes e componentes | Sob demanda |
| `marketplace_filter` | `:feature:marketplace-filter:marketplace_filter_ui` | Filtros de bolões e controles exclusivos | Sob demanda |
| `cart` | `:feature:cart:cart_ui` | Listas, linhas, agrupamentos, favoritos, inclusão e revisão local | Sob demanda |
| `payment` | `:feature:payment:payment_ui` | Métodos, cartões, CVV, Pix, processamento e resultados | Sob demanda |
| `tickets` | `:feature:tickets:tickets_ui` | Apostas/compras, detalhes, comprovantes, scanner, seleção e conferência manual | Sob demanda |
| `results` | `:feature:results:results_ui` | Modalidades, concursos, detalhes e resultados de apostas | Sob demanda |
| `notifications` | `:feature:notifications:notifications_ui` | Central, mensagens, filtros e preferências; recebimento push na base | Sob demanda |
| `responsible_gaming` | `:feature:responsible-gaming:responsible_gaming_ui` | Apresentação de avaliação, conteúdos informativos e suspensão; regras e operações na base | Junto com o app |
| `retail` | `:feature:retail:retail_ui` | Busca/localização e apresentações de resgate, agência, lotérica, Pix e Mercado Pago | Sob demanda |
| `support_help` | `:feature:support:support_ui` | Ajuda opcional por modalidade e seus renderizadores | Sob demanda |
| `support_faq` | `:feature:support:faq` existente | FAQ, perguntas, respostas e links já extraídos | Sob demanda |

A coluna de entrega muda para sob demanda na alternativa B, respeitando os caminhos obrigatórios base. Não se promete remover toda a biblioteca de cada feature da base nem obter determinada economia sem medir AAB/APKs equivalentes.

## Dependências cruzadas identificadas

- Home usa `ResultsUiState` em sete fontes e chama tutorial de Betting; esses contratos e a continuação pós-login ficam na base.
- Marketplace registra Route/ViewModel de Marketplace Filter. Separar coordenação base de renderizadores antes de instalar os dois splits independentemente.
- Tickets e Retail usam `PaymentRegistrationHost`/`paymentRegistrationViewModel`; cadastro compartilhado precisa de contrato base, sem importar implementação do split Payment.
- App injeta `notifications.di.PushRuntime`. Serviço Firebase, registro de dispositivo/canal e recebimento push continuam na base mesmo com a central de mensagens desligada.
- `responsible/start/required` integra a conclusão da conta. Avaliação obrigatória e ações de proteção não podem ser substituídas por Empty State por uma flag opcional. Na alternativa B, sua apresentação necessária permanece base.
- Splash/fila/atualização usam Onboarding; bootstrap também usa dois drawables de Home. Não mover esses recursos como se fossem exclusivos.
- Os headers protegidos de Cart são internos e referenciam constantes, erros e footer. Manter o fechamento dessas dependências na base e criar um wrapper novo para hospedar o corpo dinâmico, sem modificar os arquivos protegidos.

## JSON e compatibilidade

Manter schema 1 e adicionar somente chaves locais conhecidas. Preservar `support_faq` e seus valores já publicados. Para não desabilitar o aplicativo existente quando o JSON antigo só contiver FAQ, novas capacidades disponíveis hoje terão padrão local `true`; o padrão do FAQ permanece `false`.

Exemplo dos padrões para publicação futura, sem modificar o Firebase nesta etapa de desenho:

```json
{
  "schema_version": 1,
  "modules": {
    "account": { "enabled": true },
    "home": { "enabled": true },
    "onboarding": { "enabled": true },
    "betting": { "enabled": true },
    "marketplace": { "enabled": true },
    "marketplace_filter": { "enabled": true },
    "cart": { "enabled": true },
    "payment": { "enabled": true },
    "tickets": { "enabled": true },
    "results": { "enabled": true },
    "notifications": { "enabled": true },
    "responsible_gaming": { "enabled": true },
    "retail": { "enabled": true },
    "support_help": { "enabled": true },
    "support_faq": { "enabled": false }
  }
}
```

Chave ausente usa o padrão explícito do registro; `enabled=false` explícito desabilita novas entradas opcionais. JSON inválido/schema incompatível preserva a última configuração válida. Testar também reinício após publicação inválida, preservando configuração válida localmente para não reabilitar uma capacidade desligada por erro de cache. Entradas desconhecidas não instalam código e não concedem acesso. Configuração não é autorização do backend.

O JSON não inclui URL, nome de classe, versão independente, entrega arbitrária nem comando de instalação. Mesmo com controle por módulo, código novo exige AAB compatível com a versão da base. Não implementar remoção automática ao desligar; ela continua fora deste escopo.

## Desligamento e dados em andamento

A flag não apaga estado, rascunhos, carrinho, credenciais nem histórico. A navegação base decide entre nova entrada opcional, caminho obrigatório e continuação de operação já iniciada.

- **Nova entrada desabilitada:** Empty State DS e saída funcional, sem iniciar download opcional, consulta ou escrita dessa entrada.
- **Consulta sem operação pendente:** pode apresentar Empty State, preservando estado e controles de retorno. FAQ mantém o comportamento imediato já testado.
- **Formulário/rascunho existente:** não recriar proprietário nem descartar seleção. Preservar dados pelos mecanismos existentes e testar toggle, retorno e recriação. Não persistir CVV/senha para resolver uma transição visual.
- **Pagamento, resgate ou transferência de carrinho já iniciados:** manter Route, ViewModel, Redux e identidade até concluir/recuperar a operação. Flag não cancela nem repete POST, não gera outra compra e não remove módulo em uso.
- **Nova escrita após desligamento:** bloquear antes de iniciar o efeito. Leituras de acompanhamento, conclusão e comprovantes de operação existente continuam acessíveis pelo contrato de continuidade.
- **Caminho obrigatório:** manter bootstrap, autenticação em conclusão/recuperação, termos/aceite e avaliação obrigatória acessíveis. As flags não contornam validações nem encerram sessões.

Permissões de continuidade são decisões locais tipadas por jornada/operação e identidade; não podem vir de um booleano remoto ou de um parâmetro de deep link não validado. A continuação precisa funcionar também nos destinos de compras/comprovantes usados por Payment/Retail/Tickets. Testar morte do processo com recuperação pelo estado persistido existente e introduzir somente metadados de recuperação necessários, sem armazenar segredos.

## Build, testes e qualidade

Preservar AGP 8.13.0, Kotlin 2.2.21, JDK/JVM 21, SDK 36, flavors existentes e entrada estável MainActivity. Manter bootstrap e SplitCompat na base, sem novos launchers ou instalação de variantes extras no Samsung.

- Generalizar registro, títulos, manifestos e keep de recursos por módulos realmente declarados. Contratos continuam exatos; nada de aceitar XML indiscriminadamente.
- Preparar o APK universal real de recursos uma vez por base/variante e compartilhá-lo entre suítes dinâmicas. Cada módulo conserva suas classes, classpath, execução e denominador JaCoCo.
- Compartilhar runner nativo/SPI de compatibilidade dos testes, sem copiar recursos falsos, alterar IDs ou excluir código de cobertura.
- Adaptar testes existentes preservando asserções, acrescentar TDD para defaults/cache, portas tipadas, dependências cruzadas, continuidade e navegação. Nenhum teste financeiro efetiva operações reais.
- Lint analisa todos os splits e consolida relatório na base; Ktlint, Detekt, arquitetura, ownership, IDs, contratos e seleção incremental continuam obrigatórios.
- Preservar todos os headers HTTP/visuais, os 45 hashes já inventariados e todos os baselines. LINE global deve permanecer estritamente >95%, incluindo classes manuais de todos os splits.
- Confirmar que cada implementação está ausente nas definições DEX da base e presente no split correto. Verificar proteção das entradas no build ofuscado antes de declarar R8 aprovado.
- Validar instalação inicial, execução pelo Studio, repetição do Play, entrega por acesso, reentrada, processo recriado, falha/tentativa e combinações de módulos no emulador dedicado. Diferenciar local-testing de distribuição Google Play.

## Ordem e conclusão

Após aprovação do desenho/mode de entrega: plano executável; infraestrutura compartilhada e cache; contratos/acoplamentos; migração de todas as superfícies da tabela; regressão por família; AAB/DEX; cenários de entrega/continuidade; gate global e documentação final.

Dividir implementação em unidades verificáveis não reduz o objetivo a um segundo piloto. Uma família só será marcada como migrada quando tiver código real extraído, rotas integradas, flag funcional, testes e empacotamento comprovados. Não considerar módulo vazio, simples chave JSON ou wrapper que delega toda UI à base como migração concluída.

Publicação Firebase/Google Play, staging, commit e push não fazem parte desta revisão de desenho. Preservar o trabalho do piloto e registrar evidências fora do checkout.
