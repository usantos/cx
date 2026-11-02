# Fluxo logado e revisão visual — Implementation Plan

**Goal:** Completar os caminhos autenticados existentes e revisar layout/ícones contra o legado, mantendo as regras e textos.
**Architecture:** Manter os gateways/repos existentes e acrescentar restauração, retorno contextual e recuperação de operações inconclusivas nos pontos apropriados. Componentes e recursos visuais compartilhados continuam no CaixaDS.
**Tech Stack:** Kotlin, Compose, Hilt, AppAuth, SQLite e Android Keystore.
**Spec:** docs/fluxo-logado-pendencias-2026-10-05.md; referência visual/comportamental: /Users/ubiracysantos/Projects/legado/simlo-app-android.

## Restrições
- Não apagar dados nem substituir por assinatura incompatível o app instalado; validar com pacote paralelo.
- Trabalhar no checkout atual que contém a migração não versionada; não fazer commit/reset amplo.
- Não realizar pagamentos/resgates reais.
- Não apresentar testes mock como homologação do servidor ou paridade visual completa.
- Preservar a nova mensagem de sessão expirada e os textos exatos do legado.

## Execução
- [x] Sessão/biometria: revisar o legado, implementar o caminho seguro e testes de persistência/limpeza/retorno.
- [x] Contexto: restaurar os detalhes selecionados na recriação e preservar origem em login/cadastro/termos, sem repetir compra/resgate.
- [x] Transferência: tratamento utilizável para recibo pendente e redirects, preservando apostas e evitando reenvio incerto.
- [x] Layout: revisar recursos, ícones, componentes e todas as rotas da matriz; corrigir diferenças verificáveis e registrar capturas/limites.
- [x] Verificação: testes dos módulos afetados, gates, builds flavors e Android; revisão independente antes de concluir.

## Riscos de revisão
- Callback antigo após outra sessão não pode publicar dados nem restaurar credenciais.
- Recriação de detalhe/edição não pode misturar dados de outro usuário.
- Resposta perdida não pode duplicar apostas nem impedir consulta da conta indefinidamente.
- Retorno de login nunca repete transação financeira automaticamente.
- Ícones e layouts de erro devem manter significado, acessibilidade e ações do legado.
