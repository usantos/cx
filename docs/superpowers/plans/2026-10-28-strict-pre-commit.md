# Pre-commit completo e bloqueante

Pedido: impedir commits normais enquanto arquitetura, Ktlint, Detekt, Lint, testes, cobertura e demais gates da auditoria não aprovarem, sem alterar headers/baselines.

Implementação no checkout atual, preservando alterações anteriores. Reutilizar o instalador e o hook existentes; um runner Python valida staging e executa uma tarefa Gradle agregada sem opções de exclusão. O CI usa o mesmo runner. A validação exige workspace sem mudanças pendentes fora do index e bloqueia mudanças durante a execução. Git permite desabilitar hooks localmente; proteção remota depende de check obrigatório no servidor e não será presumida.

- [x] Reproduzir lacunas com commits reais em repositórios temporários: staging parcial, falha arquitetural, mudança durante validação e sucesso.
- [x] Implementar runner/hook e tarefa agregada reutilizando gates existentes e testes Python.
- [x] Isolar fixtures XML em diretório temporário para não disputar os fontes com compilação.
- [x] Atualizar instalador/documentação/CI mantendo falhas bloqueantes, sem autoformatar ou ampliar baselines.
- [x] Executar testes de scripts, fixture XML, conferir grafo Gradle e provar bloqueio no checkout atual; instalar o hook.

Revisão: falha do processo preservada; processo ausente ou interrompido bloqueia; alterações no index/workspace bloqueiam; nenhum arquivo é adicionado automaticamente; debt Ktlint continua impedindo aprovação.

## Verificação concluída

Hook instalado e bloqueio real por staging pendente confirmado, sem commit no projeto. Testes: 3.510 Android/JVM, 190 de ferramentas Python, 21 do hook e 33 contratos XML aprovados; JaCoCo LINE 95,11%. A execução integral reprovou exclusivamente em Ktlint (6.897 ocorrências/363 arquivos preexistentes), impedindo aprovação. Headers e 37 configurações/baselines intactos. Revisão independente sem Critical/Important. Evidências em `build/verification/pre-commit-20261028/final-summary.json`.
