# Separação da coordenação, apresentação e mapeamento da Home

## Objetivo

Refatorar o `HomeScreen.kt` atual sem alterar comportamento, recursos, geometria, textos, navegação ou contratos públicos. A referência é a cópia local em `build/verification/home-refactor-20261017/HomeScreen.before.kt`, incluindo as refatorações anteriores desta conversa.

## Organização

- `HomeScreen.kt`: mantém a função pública e `HomeSection`, delegando ao coordenador.
- `HomeRoute.kt`: mantém coleta dos ViewModels, efeitos de lifecycle/navegação, estado salvo, animações, drawer, recuperação, callbacks das duas versões e prioridade dos avisos.
- `HomeLegacyContent.kt`: composição clássica com estado de apresentação imutável e callbacks, sem ViewModels ou navegação direta.
- `HomeQuickAccess.kt`, `HomeLotteryCard.kt`, `HomeResultsContent.kt` e `HomeComponents.kt`: componentes visuais e estilos auxiliares existentes.
- `HomeLottery.kt` e `HomeLotteryMapper.kt`: modelo e conversão dos parâmetros/estilos/campanha, incluindo visibilidade de bolões.
- `HomeCarousel.kt`: funções puras existentes de índice e escala.
- A apresentação V2 já separada em `HomeV2Screen.kt` permanece inalterada.

## Contratos obrigatórios

- Assinatura/defaults de `HomeScreen`, ViewModels recebidos e owners permanecem iguais.
- Os três estados `rememberSaveable`, animações e drawer continuam sendo criados antes do retorno de recuperação; seus valores sobrevivem à recuperação e restauração.
- Preservar as chaves e a ordem dos efeitos, inclusive refresh no RESUMED e consumo único das decisões.
- Preferências/resultados V2 continuam dentro do mesmo ramo, após o retorno de recuperação; não unificar ações V2/clássicas com comportamentos diferentes.
- Manter o `key(homeLotteries.size)`, índice inicial, efeito de modalidade solicitada e dimensões/animações do carrossel.
- Preservar mapeamento de modalidades, aliases existentes, preços mínimos, Combo sintético, estilos e fontes distintas de campanha.
- Preservar prioridade revisão local → pagamento → notificação, guards dos erros/redirects, abertura de URI e tratamento de exceções.
- Não alterar módulos, dependências, gates ou arquivos preexistentes fora da Home e da documentação necessária.

## Verificação

Executar baseline JVM e testes de caracterização Android antes da extração. Testar mapeadores, decisão consumida uma vez, callback protegido, restauração do drawer e retenção durante recuperação. Capturar as versões clássica/V2 com dados determinísticos antes/depois e comparar pixels. Compilar/testar os três flavors e executar `modernCheck releaseCheck` em AVD dedicado. Fazer revisão independente contra a baseline local.

## Execução

Executar inline na árvore atual para preservar o trabalho local; sem commits ou integração de branch. O usuário autorizou a extração e a preservação de comportamento.
