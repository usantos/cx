# Migração integral — execução de 09/09/2026

A migração integral e a igualdade visual de todo o aplicativo **ainda não estão concluídas**.
Este registro descreve as correções verificáveis desta execução; não substitui o inventário
nem a homologação dos fluxos pendentes em `COMPOSE_COMPLETION_CHECKLIST.md`.

## Implementação

- Mantida a mensagem exata de sessão expirada com uma única ação `Entendi` e encaminhamento ao login.
- DataSources separados dos repositories de lotéricas e de leitura da última notificação;
  o carrinho recebe falhas HTTP pelo contrato de dados, sem depender diretamente de Retrofit.
- Convertidas para Kotlin as quatro classes Java restantes de produção: filtro de bolões,
  parâmetros, regras e destinos de navegação. Preservados os contratos HTTP, a normalização
  e as cópias imutáveis. O gate de migração agora impede reintroduzir fontes Java de produção;
  os testes Java de contrato continuam exercitando a interoperabilidade dos modelos Kotlin.
- Login usa os caminhos do ícone digital do legado, geometria do switch, fontes e medidas revisadas.
  A preferência visual não constitui implementação completa de autenticação biométrica.
- Confirmação da aposta individual deixa a tela provisória e passa a usar cabeçalho da modalidade,
  concurso, sorteio, rodapé, seta e ações da referência. Simulação transporta os parâmetros e valores
  reais na navegação, incluindo o mínimo de compra; combos preservam seu valor e mensagem.
- Detalhe do carrinho favorito mantém título, textos, exclusão de aposta, exclusão do último carrinho,
  retorno à lista e Termos de Uso. A lista invalida apostas em cache depois do detalhe.
- DTOs de favoritos preservam quantidades de números/trevos, colunas do Super Sete, time e mês da sorte.
  Surpresinhas exibem a quantidade correta de `XX`; números de Lotomania preservam `100` como `00`.
- Fontes, cores, ícones de teimosinha/exclusão e espaçamentos foram comparados com o legado.
  Os vetores foram convertidos em `ImageVector` Kotlin; as imagens de densidade foram reaproveitadas.
- Reaposta permanece no detalhe e mostra `Aposta adicionada ao carrinho com sucesso!`, com `Entendi`,
  como `AdicionarApostaCarrinhoHelper.postAdicionarApostaCarrinhoMinhasApostas` no legado.
  Removida a navegação incorreta para uma confirmação sem dados. O sucesso só aparece após resposta
  do serviço e fica no estado do ViewModel até ser reconhecido.
- Corrigidas dependências de Activity Compose e de testes de corrotinas, desugaring de `java.time`
  em preferences e expectativas de testes que ainda continham `Sim/Não` para sessão expirada.
- A cobertura de domínio/rede incorpora a execução real dos testes de contrato em data/preferences
  contra as mesmas classes. Nenhum limiar foi reduzido para aprovar a migração.

## Comparação visual

Referência: `/Users/ubiracysantos/Projects/legado/simlo-app-android`.
Cópia isolada para captura: `/tmp/simlo-legacy-ref.0RXt4W`, APK com sufixo `.legacy`.
A cópia foi atualizada com os fontes/recursos do legado informado e compilada em modo offline;
o AAR FilaBRA foi recuperado do cache local. O detalhe de favoritos recebe dados determinísticos
somente na cópia de teste. O projeto legado informado não foi alterado.

Capturas finais em `build/visual-audit/compose-migration-20260909-final/`:

| Rota | Comparação | SSIM sem status bar |
|---|---|---|
| `cart/included` | Mesmos dados de modalidade, concurso, sorteio e valor | 0.978702 |
| `cart/favorite-detail` | Mesma aposta de seis números e teimosinha | 0.995791 |

Os valores são auxiliares. Não provam igualdade de todos os pixels, estados, modalidades ou dispositivos.
As capturas correspondem ao APK mock da árvore final validada, já com os vetores em Kotlin.
As duas telas foram inspecionadas novamente após a compilação e os testes finais.

## Validação

Validação da árvore final, sem alterações de produção durante as execuções abaixo:

```sh
./gradlew modernCheck --max-workers=1 --console=plain
./gradlew releaseCheck --max-workers=1 --console=plain
./gradlew :app:assembleAutomacaoDebug -PmockBackend=true --console=plain
```

- **modernCheck aprovado** em 3m42s: build debug dos três flavors, lint, testes JVM,
  cobertura e gates de arquitetura, contratos, identidade e migração.
- **releaseCheck aprovado**: APKs Prd, Tqs e Automacao produzidos com R8, sem assinatura de distribuição.
  A verificação serial reutilizou os APKs que acabavam de ser compilados na execução anterior.
- **54 testes Android executados** na rodada final: 19 de armazenamento, três de apresentação,
  23 de carrinho, quatro de Home e cinco de notificações, todos sem falhas.
- APK mock compilado e instalado no emulador Pixel 9 Pro / Android 16. A checagem de reaposta
  verificou a resposta mock, texto exato, uma ação `Entendi` e permanência no detalhe após confirmação.
  Capturas e resultado: `build/visual-audit/compose-repeat-dialog-20260909/`.
- Logs reproduzíveis em `build/verification/compose-migration-20260909/` (`modernCheck.log`,
  `releaseCheck.log`, `mock-apk.log`); cobertura por módulo em `coverage.json`.
  Carrinho: 95,43%; dados: 96,05%; domínio: 98,61%; rede: 99,61% de linhas.
- `git diff --check` sem erros de whitespace; apenas avisos preexistentes de conversão CRLF/LF
  nos arquivos de propriedades do Gradle.

A execução conjunta de debug e release revelou uma falha interna do lint ao ler um arquivo
temporário `kspCaches/automacaoRelease/backups/.../AppContainer_Factory.java` removido pelo KSP.
As verificações foram repetidas separadamente, com um worker, e passaram sem desativar lint,
excluir testes ou reduzir cobertura. O build conjunto não é registrado como aprovado.

## Pendências para conclusão integral

- Auditar e comparar todos os destinos/estados restantes do inventário, incluindo telas que ainda
  dependem de fixtures de serviços no legado.
- Integrar autenticação biométrica persistida; o switch isolado não atende esse fluxo.
- Concluir a coordenação da transferência do carrinho local após login. O pós-login ainda envia
  `emptyList()` deliberadamente; os recibos duráveis existentes não estão conectados a esse envio.
- Verificar as variações especiais das confirmações de simulação e os demais estados de reaposta.
- Concluir a homologação de upgrades, recriação de processo, fontes ampliadas, assinatura/distribuição
  das variantes release e integrações de autenticação/pagamento/resgate no ambiente adequado.
