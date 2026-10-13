# Fluxo logado — pendências verificadas em 05/10/2026

Este arquivo registra a auditoria anterior à implementação. O resultado atual e as pendências de homologação estão em [fluxo-logado-entrega-2026-10-05.md](fluxo-logado-entrega-2026-10-05.md).

Escopo: continuar a migração no checkout Compose, usando o legado em `/Users/ubiracysantos/Projects/legado/simlo-app-android` como referência. Esta etapa é uma auditoria do código atual, sem nova alteração de comportamento.

## Implementação

1. **Biometria funcional.** `LoginRoute` encaminha a opção a `LoginViewModel.setBiometricEnabled`, que altera somente o estado em memória. Não há BiometricPrompt ou gateway biométrico nos fontes de produção examinados. Completar preferência, habilitação e entrada conforme os caminhos reais do legado.
2. **Sessão e retorno após recriação.** `SessionTokenStore`, AuthorizationAttemptRegistry e EndSessionRegistry mantêm seu estado em memória. O gateway faz a troca do código de autorização, mas não mantém o refresh token recebido. Definir e implementar a retomada conforme o legado, sem ressuscitar sessões encerradas ou repetir operações inconclusivas.
3. **Retorno contextual de conta.** Compra/resgate usam mapeamentos diretos para login/cadastro/termos (`onPaymentRedirect`, `onPrizeRedirect`, `onValidationRedirect`). O estado de retorno implementado em Experimente não é preenchido por esses handlers. Completar ida/volta de autenticação e exigências cadastrais com a origem correta e nova validação, sem reenvio automático da compra/resgate.
4. **Carrinho local com resultado incerto.** `RemotePostLoginAccountRepository` integra preparação, envio e limpeza, mas `RequiresReview` encerra a validação do cadastro com mensagem genérica. Não existe tela/ação de reconciliação verificável para esse estado. Revisar também carrinho local com redirect de cadastro/termos: a reserva ocorre antes da resposta, e o caminho atual exige redirect nulo para confirmar o lote. Não presumir que receber qualquer carrinho confirma cada aposta enviada; verificar a garantia efetiva do contrato.
5. **Contexto dos detalhes e edição.** `selectedPurchase`, `selectedBet`, `betToEdit`, filtro e dados de conferência usam `remember` em LoteriasApp. As telas dependem dessas instâncias e podem perder o conteúdo na recriação. Restaurar pela identificação/contrato da entidade e manter os campos necessários à edição; descartar estados de outra sessão.

## Validação para concluir o fluxo

6. **Percurso autenticado completo.** Exercitar login → bloqueio/cadastro/termos/autoavaliação → Home → aposta individual/Combo/Rapidão/Bolão → carrinho/favoritos → pagamento → Minhas compras/Minhas apostas → detalhe/edição/resgate. Conferir estados de consulta, suspensão, expiração, erros, cancelamento e reentrada contra o legado. A existência das rotas não certifica esse percurso.
7. **Integrações reais.** OAuth e callback, transferência com servidor, localização, cartão/Pix, consulta de compra e resgate precisam de homologação controlada. Os 461 testes JVM e oito testes de armazenamento da etapa Experimente não homologam essas integrações. Evitar cobranças ou resgates reais ao testar; usar ambiente/dados autorizados.
8. **Android e entrega.** Executar a matriz comparativa no mesmo dispositivo, testes instrumentados das features autenticadas, cobertura global e builds dos flavors/debug/release. As evidências históricas das auditorias não aprovam a árvore atual. O APK da última etapa é AutomacaoDebug com backend simulado e pacote paralelo.

## Infraestrutura já presente

AppAuth, verificação pós-login, bloqueio parcial, conteúdo de sessão da Home, carrinho remoto, validação tipada do carrinho, favoritos, compra e resgate possuem implementações. A transferência visitante foi conectada na etapa Experimente. Documentos antigos ainda citam lista vazia no pós-login e validador genérico; essas descrições não representam esses pontos no código atual.

## Ordem sugerida

Primeiro retorno contextual e recuperação dos detalhes; depois tratamento de transferência pendente, sessão/biometria; por fim execução integrada, comparação visual e gates de entrega. A persistência de credenciais exige desenho específico para armazenamento seguro e compatibilidade com o legado antes de alterar o gateway.
