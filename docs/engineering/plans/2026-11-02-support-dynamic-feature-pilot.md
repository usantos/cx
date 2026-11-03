# Implementação do piloto de FAQ dinâmico

O resultado verificável é um AAB com a apresentação real do FAQ em split, controlado pelo JSON `app_modules`. Desabilitado, o FAQ apresenta Empty State; termos e consentimento continuam na base.

1. **Configuração tipada:** modelos em Core Domain e adaptação Firebase em Onboarding Data. Testar JSON estrito, cache, schema, defaults, ativações e fechamento antes da implementação. Reutilizar a instância Firebase e os eventos existentes; não alterar configurações globais do SDK.
2. **Entrega:** porta de instalação em Support Domain, adaptador Play em Support Data, proprietário de estado Redux/ViewModel em Support Presentation. Testar instalação presente/ausente, duplicidade, erro/repetição, confirmação, desabilitação durante entrega e reentrada. Navegação apenas apresenta resultados, sem executar serviços diretamente.
3. **Split real:** mover renderização, respostas, links e recursos exclusivos para `:feature:support:faq`. A base mantém contrato Compose tipado; resolver implementação somente quando instalada/habilitada. Preservar IDs e termos. Configurar flavors, SplitCompat e proteção da entrada em R8.
4. **Gates:** reconhecer módulo dinâmico em arquitetura, Lint, testes, seleção incremental e JaCoCo. Testar exceção estrita de dependência da base, sem permitir bibliotecas comuns dependerem de aplicações. Manter todas as classes manuais no denominador e limiar global >95%.
5. **Integração:** composição Hilt fornece configuração e entrega. Observar ativação JSON no destino; atualizar configuração em foreground, com recuperação de 30 segundos e sem repetir operações de negócio. Exibir estados DS, retorno, progresso, confirmação e tentativa explícita.
6. **Validação:** testes de unidade/UI pertinentes, contratos de arquitetura/IDs, qualidade, APK TQS e AAB. Inspecionar split/base e testar entrega local por bundletool no emulador. Distinguir essa evidência de publicação Google Play ou teste no Samsung.
7. **Documentação:** publicar JSON de exemplo, chave, fluxo de entrega, limites do piloto, comandos e resultados reais. Comparar hashes dos headers e baselines antes/depois. Não preparar arquivos, criar commit nem push sem autorização específica.

Registro de execução e relatórios ficam fora do checkout em `simlo-artifacts/2026-11-02/dynamic-faq-pilot`. Cada etapa deve registrar o comando e o resultado; falha ou teste pendente não será reportado como aprovação.
