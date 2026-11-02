# Correções da revisão de arquitetura e qualidade

**Objetivo:** corrigir os quatro grupos solicitados: fronteiras da integração de notificações, 119 achados Detekt, 64 achados Android Lint e registro obsoleto do inspetor. Extrair textos de interface hardcoded e usar comentários em português no código do aplicativo.

**Arquitetura:** DTOs e acesso remoto pertencem à feature; repositories coordenam portas e conversões de domínio. Core Network conserva clientes configurados, pins e sessões capturadas. Textos de componentes Compose pertencem aos recursos Android de cada feature. Mensagens de regras, ViewModels e mapeadores usam bundles JVM próprios com TextCatalog; Domain permanece independente de Android.

**Restrições:** preservar trabalho local não relacionado, Home clássica, minSdk 29, leitura QR via ML Kit, release sem Chucker/logs e bibliotecas de fornecedor. Não ampliar baselines nem desabilitar regras para passar os gates. Não realizar transações reais. Novos comentários do app em português. A autorização atual é executar todas as correções e validar sem novas confirmações de rotina.

## Execução

- [ ] Extrair transporte remoto de notificações, corrigir propriedade dos DTOs e quantidade de parâmetros na composição.
- [ ] Corrigir achados Detekt em produção e testes, conservando as asserções e os limites das regras.
- [ ] Corrigir recursos de ícones, verificações obsoletas de API e recursos sem uso reportados pelo Lint.
- [ ] Remover o registro obsoleto do inspetor standalone.
- [ ] Extrair textos hardcoded de apresentação para recursos e converter comentários do app para português.
- [ ] Executar testes relevantes, verificar arquitetura, Detekt e Lint completos; corrigir regressões.
- [ ] Fazer revisão independente final e registrar resultados e limitações reais.

## Foco de regressão

- Notificações devem manter identidade capturada, descarte de respostas antigas e confirmação ligada à sessão.
- Falhas parciais ao marcar notificações devem recarregar a verdade do servidor.
- Mudanças de recursos de ícones devem preservar tamanho, densidade e referências de recursos.
- Textos parametrizados devem preservar concurso, contadores e conteúdo completo em português.
- Entrada na simulação deve continuar igual ao legado, com bloqueio de envio de apostas encerradas no carrinho.

## Evidências durante a execução

- Recursos JVM conferidos contra os textos originais e incluídos em teste de empacotamento: 35 bundles e 590 chaves.
- Revisão independente estática: paridade exata dos textos, contratos de notificações e callbacks/estado dos componentes extraídos.
- A execução integrada inicial detectou imports ausentes de testes, aliases const incompatíveis com consulta dinâmica de recursos e testes de rede que não ativavam explicitamente o modo debug; correções aplicadas sem alterar a política de logs da produção.
- Resultados finais dos gates e dos testes serão registrados após a execução completa; nenhuma alteração de baseline foi autorizada ou aplicada.
