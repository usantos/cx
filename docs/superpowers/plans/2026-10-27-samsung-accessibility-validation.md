# Validação Samsung das 14 features

**Objetivo:** validar os fluxos disponíveis no Samsung com TalkBack oficial e fonte de sistema em 200%, corrigir defeitos reproduzidos e registrar evidências por cenário.
**Arquitetura:** preservar MVVM, Clean Architecture e ReduxKotlin. Alterações só após reprodução; regras permanecem em Domain e estado nos ViewModels/stores existentes.
**Referência:** docs/ACCESSIBILITY.md e docs/architecture/FULL_AUDIT_20261027.md.

## Restrições
- Preservar todos os headers e os 37 hashes de configuração/baseline, sem supressões adicionais.
- Usar o app existente no Samsung, sem APK de testes, sem limpar dados e sem alterar o carrinho.
- Não contornar PIN, autenticação, biometria ou confirmação financeira. Não coletar credenciais ou dados pessoais em evidências.
- Não usar dumps UiAutomation como prova de foco/fala TalkBack: a captura pode suprimir o serviço. Capturas visuais comprovam layout; gestos/leitura exigem observação independente ou confirmação humana por cenário.
- Executar nesta sessão; preservar o checkout de migração, sem commit/push/reset.

## Execução
- [x] Conferir desbloqueio, serviço oficial, idioma, versão instalada e configurações iniciais; registrar hashes protegidos.
- [x] Preparar matriz por feature com passos, escala, caminho de entrada, evidência e status individual (aprovado/falhou/bloqueado/não executado).
- [x] Executar fluxos públicos e autenticação disponível com TalkBack/200%; preservar ações de domínio e carrinho.
- [x] Conferir fluxos restritos após login humano; registrar bloqueio concreto quando não houver acesso, sem substituir por fixtures.
- [ ] Reproduzir defeitos, escrever regressão pertinente antes da correção, corrigir e repetir cenários afetados.
- [ ] Executar testes e gates pertinentes às alterações; conferir hashes, instalar APK somente se houver mudanças.
- [ ] Atualizar docs/ACCESSIBILITY.md, matriz Samsung e guia técnico com evidências e limites reais; restaurar configurações temporárias.

## Foco de revisão
1. Foco após abrir/fechar diálogo, retorno de tela e teclado.
2. Conteúdo/ações alcançáveis em 200%, especialmente grades, carrosséis e rodapés.
3. Uma ativação por escolha e anúncio de estado, valor e erro.
4. Associação entre coluna, número, time, acerto e premiação sem depender de cor.
5. Fronteiras externas e rotas autenticadas sem confundir bloqueio de acesso com aprovação.

## Andamento da rodada autenticada

Login humano concluído. Seis respostas manuais registradas literalmente; Dúvidas em conferência. Fonte 200% e TalkBack oficial ativos. 28 regressões JVM anteriores passaram nos ciclos focados; revisão de constantes pelo Detekt expôs tipos Long em três fixtures, corrigidos para repetir os módulos. Novo defeito de quebra letra por letra em ações do processador de pagamento recebeu regressão em preparação. Gates finais, instalação e retestes continuam pendentes. Detalhamento e limites em docs/accessibility/SAMSUNG_VALIDATION_20261027.md.

## Requisito adicional confirmado no carrinho

- [x] Reproduzir por teste a ausência de leitura integral por bloco.
- [x] Compor descrição pt-BR com numerais/moeda por extenso; preservar ações e valores ocultos.
- [x] Verificar regressões de modalidades e integração; repetir gates afetados e hashes.
- [x] Instalar no mesmo Samsung e conferir leitura humana de Cart/Results: “Leitura completa e correta”; evidências registradas.

Design: docs/superpowers/specs/2026-10-27-cart-portuguese-readout-design.md.

## Verificação final de código e situação física

3.371 testes consolidados, zero falhas/erros/ignorados; 68 módulos com log por módulo e XML fresco. App/Results completos passaram após adaptar a paridade visual à árvore não fundida e manter asserções próprias dos blocos no leitor. Lint/Detekt/arquitetura/CaixaDS e montagem PRD passaram; Ktlint de 60 arquivos passou. APK instalado no Samsung com SHA igual ao build. Sessão real recuperada pela entrada normal e carrinho novamente conferido em 2/R$7. Fonte 200%/TalkBack mantidos; conferências auditivas Cart/Results solicitadas. Matriz física permanece parcial; não marcar conclusão integral das 14 features.


## Achado humano no tutorial de Bolões

O usuário encontrou somente Fechar/Pular/Vamos começar no TalkBack. As instruções são contornos vetoriais dentro de Lottie, sem texto semântico. Acrescentado texto equivalente às cinco etapas de Bolões e sete de +Milionária, somente na página ativa; controles continuam separados. Checkbox passa a ter nome, estado e clique em um único alvo. Seis regressões novas verificam o contrato. Testes de geometria continuam medindo os mesmos pixels e glifos na árvore não fundida, e ações verificam o novo alvo agrupado. Reteste humano no Samsung continua obrigatório.

A entrada da Home ao scanner foi corrigida para alcançar a alternativa manual mesmo sem permissão de câmera. A introdução recebeu duas regressões de fonte 200% e adaptação de corpo/rodapé; logo e apresentação na escala padrão preservados. Headers e baselines não foram alterados. Gates completos dos módulos afetados, App, Lint, Detekt e montagem estão em reexecução.


Gates da atualização do tutorial concluídos: 3.379 testes aprovados/zero falhas, erros e ignorados; 68 módulos, 73 arquivos Ktlint, Lint/Detekt/arquitetura/CaixaDS e montagem aprovados. APK instalado e hash conferido. Autenticação normal solicitada ao usuário para retomar os retestes físicos, sem captura de credenciais.
