# Experimente — plano de implementação

Objetivo autorizado: reproduzir os fluxos de visitante do checkout legado, preservando Compose, CaixaDS e o comportamento autenticado.

- [x] Entrada individual sem consulta autenticada; Combo/Bolão/Rapidão com confirmação de login e contexto de retorno.
- [x] Carrinho visitante SQLite compatível com a leitura/recibo existentes: inserir, restaurar, excluir, limpar, limites, modalidades especiais e espelho.
- [x] Pós-login: preparar lote persistente, enviar pelo contrato existente, confirmar somente resposta válida da mesma sessão, limpar somente itens intactos. Resposta perdida requer revisão, sem reenvio automático.
- [x] Menu: manter acessos públicos e solicitar login nas opções privadas.
- [x] Aposta Shake: preferências legadas, confirmação, tutorial, detector com ciclo de vida e preenchimento por modalidade.
- [x] Testes de regressão, build APK, armazenamento no XCover7 e interface no emulador, sem apagar dados do usuário. Validação visual física permanece pendente (aparelho bloqueado).

Implementação no checkout atual: os arquivos da migração estão majoritariamente não versionados; um worktree não conteria este estado. Não realizar commit ou reset amplo.
