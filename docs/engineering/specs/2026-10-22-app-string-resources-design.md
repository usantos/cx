# Recursos de texto do app inteiro

## Objetivo e escopo solicitado

Centralizar todos os textos locais exibidos ao usuário em recursos Android para permitir traduções futuras. O escopo inclui app, Design System, todas as features, mensagens locais de erros de domínio/dados, notificações produzidas no dispositivo, acessibilidade, diálogos, conteúdo explicativo estático e textos de fluxos auxiliares.

Preservar o português atual, cores, hierarquia, fontes, regras de negócio, endpoints, autenticação, valores financeiros e navegação. A migração não inclui produzir as traduções de outros idiomas neste momento.

## Levantamento

Inventário inicial: 2.369 candidatos a textos em 515 arquivos Kotlin de produção. O levantamento é heurístico: inclui alguns textos técnicos e não cobre sozinho rótulos curtos sem espaços, concatenações e arquivos XML/HTML. Por isso não constitui evidência de cobertura completa.

Dados do levantamento: `build/verification/localization-20261022/candidate-inventory.json` e `summary.json`.

Há textos em composables, modelos de estado, coordenadores, ViewModels, casos de uso e mapeadores de erro. Os módulos de domínio são JVM puros; não devem depender de `Context` nem de `R` Android.

## Abordagem proposta

### Recursos por proprietário

- `core/design-system/src/main/res/values/strings.xml`: textos dos componentes básicos e reutilizáveis do DS, inclusive acessibilidade e ações padrão.
- `feature/<nome>/presentation/src/main/res/values/strings.xml`: títulos, botões, mensagens e conteúdo próprios da feature.
- `app/src/main/res/values/strings.xml`: identificação e fluxos de inicialização exclusivos do app.
- Módulos Android que geram notificações, erros de plataforma ou telas independentes mantêm seus recursos próprios.
- Textos realmente compartilhados devem ter um único proprietário comum, com dependência explícita; não copiar mensagens iguais indiscriminadamente entre módulos.
- Usar nomes semânticos, com prefixos do módulo/contexto e sem nomes gerados por hash.

### Consumo e estado

- Em Compose, consumir com `stringResource` e `pluralStringResource` na camada que apresenta o texto.
- Em callbacks e APIs Android, resolver com recursos do contexto vigente; não invocar APIs composables fora de composição.
- Estados e mensagens que sobrevivem à recriação de Activity devem preservar a identidade do texto e os argumentos, em vez de armazenar a tradução já resolvida.
- Para erros locais de módulos JVM, usar mensagens estruturadas com chaves estáveis e argumentos, sem dependência Android. Resolver a chave para recurso na apresentação. Preservar mensagens remotas separadamente, como conteúdo recebido do serviço.
- Manter intactos os códigos de erro, status, confirmações de negócio, cancelamento e decisões de retry. Não reconhecer regras por comparação de texto traduzido.

### Parâmetros, formatação e textos ricos

- Substituir interpolações e concatenações apresentadas ao usuário por parâmetros posicionais (`%1$s`, `%2$d`).
- Usar `plurals` quando a quantidade muda a mensagem. Não concatenar número, substantivo e sufixo de plural na apresentação.
- Preservar negritos, links, quebras de linha e descrições de acessibilidade por recursos formatados/anotações; não localizar trechos pela redação em português.
- Permitir que traduções reordenem argumentos. Escapar apóstrofos, aspas, ampersands, percentuais e HTML conforme o formato de recursos Android.
- Marcas e nomes oficiais de produtos devem manter a grafia exigida; declarar `translatable="false"` quando apropriado.
- Conteúdo remoto permanece conteúdo remoto. URLs de destino, nomes de campos de rede, rotas, padrões de validação, chaves de armazenamento, tags de teste e identificadores não são textos de tradução.

### Prevenção de regressões

Adicionar verificação de produção para impedir novos textos locais de interface fora dos recursos. A verificação deve reconhecer chamadas de renderização, propriedades de títulos/mensagens/acessibilidade e mensagens locais de erro. Exceções técnicas precisam ser específicas e revisáveis; uma expressão regular que detecta apenas acentos não é cobertura suficiente.

A auditoria final inclui rótulos curtos, literais sem acentos, strings multilinha, defaults de funções, anotações, XML/manifesto e conteúdo estático. Textos de fixtures de teste permanecem identificados como dados de teste, sem serem confundidos com cópia de produção.

## Sequência de execução proposta

1. Catalogar os textos por módulo e classificar textos locais, conteúdo remoto e constantes técnicas.
2. Preparar os contratos de mensagens locais sem Android e testes de resolução/parâmetros. Definir a verificação de strings de produção com casos positivos e negativos.
3. Migrar DS e inicialização do app; depois todas as features, incluindo seus estados, validações e mensagens locais.
4. Migrar textos de serviços Android, notificações locais, acessibilidade e conteúdo estático restante.
5. Auditar novamente todos os literais candidatos e revisar as exceções.
6. Compilar todas as variantes suportadas, executar verificações de arquitetura/recursos e testes relevantes. Usar um idioma de teste/pseudolocalização para detectar concatenação, expansão de texto e recursos não resolvidos.
7. Instalar no celular com backend real, Home clássica e sem Chucker, sem criar apostas, pagamentos, reservas ou favoritos reais durante a validação.

## Critérios de conclusão

- Nenhum texto local apresentado ao usuário permanece definido diretamente em código de produção.
- Recursos compilam, parâmetros e plurais são válidos e textos ricos preservam aparência e interação.
- Domínio e dados JVM permanecem independentes do Android.
- Português mantém textos e comportamento existentes; erros remotos não são reescritos como mensagens locais.
- Verificação de strings, arquitetura, compilação e testes relevantes passam.
- Limitações ou exceções residuais devem ser listadas explicitamente; não declarar migração completa enquanto houver textos locais de produção pendentes.

## Alternativas consideradas

- Migrar somente chamadas `Text(...)`: menor custo, mas deixa de fora erros, estados, acessibilidade e notificações. Não satisfaz o escopo solicitado.
- Um catálogo global de strings já traduzidas em memória: facilita substituição mecânica, mas pode prender o idioma antigo em estados e contornar recursos/plurais Android. Não recomendado.
- Recursos Android por módulo com mensagens estruturadas para JVM: abordagem recomendada, por respeitar a modularização e permitir resolução no idioma vigente.
