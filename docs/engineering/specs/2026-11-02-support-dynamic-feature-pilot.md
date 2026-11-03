# Piloto de Dynamic Feature: Ajuda/FAQ

## Objetivo e escopo

Introduzir um módulo Android Dynamic Feature para a apresentação de perguntas frequentes, preservando a interface existente, MVVM, ReduxKotlin, Design System, IDs e contratos de serviços. Validar entrega, navegação, empacotamento e regressão antes de expandir para outras funcionalidades. Não publicar no Google Play, alterar Firebase, instalar outro aplicativo, criar commit ou fazer push como parte da implementação sem pedido específico.

O usuário aprovou começar por Ajuda/FAQ e definiu controle por JSON, com Empty State quando o módulo estiver desabilitado. A proposta técnica abaixo restringe o piloto à parte opcional: termos, privacidade, aceite obrigatório, autenticação e regras das modalidades permanecem disponíveis na base. O modo de entrega proposto é sob demanda, com validação local por App Bundle/bundletool; a entrega sob demanda é a decisão técnica deste piloto, coerente com o controle remoto solicitado.

## Decisão de arquitetura

Criar `:feature:support:faq` com `com.android.dynamic-feature`, associado a `:app` por `android.dynamicFeatures`. Não transformar toda a feature Support em módulo opcional, pois ela atende obrigações de aceite durante o login.

A apresentação concreta de seções, perguntas, respostas e links do FAQ será movida fisicamente ao módulo dinâmico. Recursos exclusivos poderão acompanhá-la; recursos usados pela base continuarão nela. Não criar apenas um módulo vazio ou um invólucro cujo conteúdo real continue todo no APK base.

O primeiro corte reutiliza os contratos e o proprietário de estado existentes em Support Presentation, além dos casos de uso Domain/Data já fornecidos na base. Um contrato estreito de conteúdo Compose, definido na parte base de Support, recebe estado e callbacks; o módulo dinâmico implementa a apresentação. O carregador conhece somente o contrato e a entrada pública do módulo. Não expõe um localizador global de serviços, não injeta implementações dinâmicas no componente Hilt da base e não faz a View chamar repositories ou clientes de rede.

A base não referencia diretamente a classe concreta do FAQ. A resolução da entrada ocorre somente após confirmação da instalação. A entrada concreta e seu construtor terão proteção específica contra remoção/renomeação por R8, acompanhada de verificação do empacotamento. O domínio, a rede e a autenticação permanecem com os mesmos contratos.

Esse corte valida a entrega de código real, mas mantém a orquestração de conteúdo e as dependências comuns na base. Portanto não promete grande redução inicial do APK nem representa migração completa de Support; os tamanhos serão medidos no bundle e nos splits produzidos.

## Entrega e navegação

Preservar as rotas existentes de dúvidas e a entrada da aba FAQ de termos. Uma integração de plataforma encapsula Play Feature Delivery e publica estado de instalação para um proprietário de apresentação com Redux: verificação, solicitação, progresso, confirmação quando exigida, pronto e falha recuperável. A View apresenta estado e ações usando componentes do DS.

Antes da disponibilidade do módulo, o usuário pode voltar ou tentar novamente após falha. Uma mesma entrada não dispara downloads concorrentes. Eventos de instalação antigos não abrem uma rota abandonada; listeners são removidos quando seu proprietário termina. Reentrada consulta os módulos realmente instalados, inclusive após morte do processo. Instalação do módulo não repete aceite de termos, login, compra ou outra operação remota.

A infraestrutura de splits deve ser integrada à Application e ao contexto da Activity conforme necessário para classes/recursos. Hilt permanece na base. Como o piloto recebe dependências explicitamente pelo contrato de apresentação, não exige agregar anotações Hilt de código ausente ao componente principal.

Os flavors TQS, PRD e Automação e os build types devem corresponder à base, mantendo AGP 8.13.0, Kotlin configurado e JDK/JVM 21. A entrega Google Play pertence ao AAB correspondente à mesma versão do aplicativo.

Instalação direta do APK base por ADB não comprova entrega sob demanda. O piloto deve oferecer instruções e comando reproduzível de bundletool para instalação local com splits e teste de entrega, além de documentar o comportamento de um APK base sem o módulo. Um APK universal de teste pode incluir o módulo, mas não serve como prova de download sob demanda.

## Controle por Remote Config

Adicionar o parâmetro `app_modules`, com tipo de dados **JSON** no console Firebase Remote Config e leitura por `getString()` no Android, com o seguinte contrato inicial:

```json
{
  "schema_version": 1,
  "modules": {
    "support_faq": {
      "enabled": false
    }
  }
}
```

O padrão local desabilita módulos opcionais. O registro local reconhece somente `support_faq` nesta fase; entradas de outros módulos não instalam código nem alteram fluxos ainda não integrados. A configuração não recebe nomes de classes, URLs de download, comandos, credenciais ou regras de autenticação. A migração dos demais módulos será feita individualmente; adicionar uma chave ao JSON não os transforma em Dynamic Features.

A integração reutiliza a infraestrutura Firebase existente, mantendo SDK e parsing em Data e expondo configuração tipada por contrato de Domain. Cache/defaults são inicializados sem bloquear login. Valores inválidos, schema incompatível ou parâmetros ausentes não habilitam módulos; uma publicação inválida não substitui a última configuração válida da sessão. Sem configuração válida, o padrão é desabilitado. O JSON não é mecanismo de autorização do backend.

Quando `enabled=false`, mostrar `LoteriasCaixaDSEmptyState` com a mensagem “Este módulo não está disponível no momento.” e ação de voltar. Não oferecer o FAQ antigo como fallback, não iniciar novos downloads nem resolver sua implementação. Isso também vale para o módulo já instalado. Se o FAQ estiver aberto, a configuração ativada o substitui pela Empty State; não altera termos/consentimento, login, carrinho ou pagamento.

Quando `enabled=true`, consultar a instalação real e solicitar o split se necessário; falha permite tentativa explícita, e habilitação após Empty State pode retomar a entrada atual. Eventos antigos de instalação não atravessam a decisão desabilitada. Não desinstalar automaticamente o código já baixado ao desligar a configuração.

O Firebase controla disponibilidade, não o empacotamento Gradle nem a distribuição de código. O AAB instalado precisa incluir o split e sua versão correspondente; a instalação pertence ao Play Feature Delivery ou ao ambiente local de testes. O listener de configuração é complementado pela atualização em foreground já praticada pelo projeto, sem prometer entrega instantânea garantida nem criar polling de operações de negócio.

## Qualidade e ferramentas

Reconhecer o plugin dinâmico nos inventários de módulos, cobertura e tarefas de validação. Toda classe manual movida ou adicionada deve continuar no denominador global do JaCoCo. Selecionar a variante correta do módulo dinâmico no agregado; não ignorá-lo para manter o limiar.

A política de dependências ganha uma regra específica de empacotamento: somente um módulo que usa o plugin dinâmico e está declarado no `dynamicFeatures` de sua base pode depender dessa aplicação. Bibliotecas comuns continuam impedidas de depender de `:app`; ciclos reais, fontes de outro módulo e dependências indevidas continuam bloqueados. Acrescentar fixtures positivas e negativas para os dois tipos de módulo.

Preservar IDs existentes e cadastrar os controles novos de entrega nos inventários de automação. Manter baselines e headers HTTP/visuais. Testes não efetivam operações financeiras nem usam respostas simuladas como prova de entrega Google Play.

## Aceitação e validação

1. Regressões de JSON/defaults/cache/schema, desabilitação antes/depois da instalação e com FAQ aberto, reabilitação, instalação ausente/presente, erro, tentativa explícita, concorrência, saída e retorno, processo recriado e conclusão tardia são escritas antes das implementações correspondentes.
2. Testes do FAQ preservam seções, expansão, links, acessibilidade e IDs; testes de termos confirmam funcionamento sem depender da instalação.
3. O AAB contém código e recursos exclusivos no split do FAQ; a base não contém a implementação concreta da apresentação dinâmica.
4. O procedimento local demonstra instalar a base sem o FAQ, solicitar o módulo, renderizar o FAQ e retornar; a evidência distingue bundletool local de validação Google Play.
5. Arquitetura, Ktlint, Detekt, Lint, testes, inventários e APK/AAB pertinentes passam, com JaCoCo global estritamente maior que 95% e sem exclusões ou baselines adicionais.
6. A documentação registra comandos, artefatos, tamanhos e limitações; não declara publicação Play, instalação Samsung ou validação R8 sem executá-las.

## Referências

- [Play Feature Delivery](https://developer.android.com/guide/playcore/feature-delivery).
- [Entrega sob demanda](https://developer.android.com/guide/playcore/feature-delivery/on-demand).
- [Injeção em módulos dinâmicos](https://developer.android.com/training/dependency-injection/hilt-multi-module).
