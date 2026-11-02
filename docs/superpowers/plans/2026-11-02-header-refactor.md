# Refatoração dos headers compartilhados

## Autorização e contrato

O usuário autorizou expressamente refatorar `CurrentCartScaffold.kt` e
`CaixaDSScreenComponents.kt` em 02/11/2026. Esta tarefa é uma exceção estrutural à
proteção desses dois fontes; não autoriza mudar o visual, headers HTTP ou baselines.
O snapshot desta tarefa é a árvore após 3.613 testes e LINE95,31%, não HEAD.

Manter assinatura/defaults de CaixaDSScaffold, callbacks, geometria, medidas,
cores, tipografia, slots e ordem dos nós. Preservar o padding delegado: o valor
superior continua calculado na leitura, com a diferença entre arredondamento e
truncamento da toolbar. Não criar stateholders, stores, dependências ou nova API.

## Unidades verificáveis

- [x] Criar/rodar caracterização dos dois scaffolds antes das extrações: header azul/customizado, navegação/título, slots de rodapé, padding fracionário e direção, ações condicionais do carrinho, loading/erro/conteúdo. Capturas ficam fora do Git.
- [x] Core DS: manter fachada pública; separar renderização/configuração de chrome e adaptação do padding em helpers especializados. Conservar valores e fonte/dinâmica das leituras.
- [x] Cart: separar ações do topo e conteúdo do scaffold; manter dataclass de ações e footer existentes com os mesmos contratos. Calcular flags de visibilidade uma vez e usar a mesma política na largura.
- [x] IDs: os botões clear/favorite antes eram cobertos pela exceção dos headers. Nos helpers novos, publicar `cart.0143` e `cart.0144`, aditivamente; provar RED→GREEN e registrar no catálogo sem alterar validadores/exclusões.
- [x] Verificar igualdade das capturas antes/depois e callbacks/medidas. Executar qualidade, arquitetura/DS/IDs, regressões locais, revisão independente, suíte global/cobertura>95% e APK TQS.
- [x] Atualizar documentação/instruções com a exceção autorizada e as novas responsabilidades, preservando proteção futura de geometria/visual. Registrar limitações e resultados.

Sem staging, commit, push, instalação ou operação financeira. Uma invocação
Gradle por vez, executada apenas pelo integrador. Os outros 42 arquivos protegidos
e todas as regras de qualidade permanecem intactos.

## Fechamento

Dez caracterizações passaram antes; o caso dos IDs falhou por ausência da tag
antes da extração. Após implementar, 16 testes focados passaram e oito PNGs foram
comparados: pixels e bytes idênticos. Suite final de 3.624 testes sem falhas,
erros ou ignorados, LINE global 95,32%, qualidade/arquitetura/contratos e APKs
TQS/catálogo aprovados em 8min26s. Oito hashes de fontes conferidos; dois headers
alterados com autorização e 42 demais protegidos/configurações intactos.

Sem staging, commit, push, instalação ou Samsung. preCommitCheck completo NOT RUN
por fontes novas ainda não preparadas no Git; validações diretas passaram.
