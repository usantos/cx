# Matriz de expansão da certificação

## Implementado nesta entrega

| Caso | Pré-condição | Verificação | Situação |
| --- | --- | --- | --- |
| CTA_01 | Tela inicial sem login lembrado | Experimente, entrada e biometria identificáveis | Implementado; dry-run validado |
| CTA_02 | Tela inicial sem login lembrado | Visitante entra na Home e Sair retorna ao login sem erro de logout remoto | Implementado; dry-run validado |
| CTA_03 | Tela inicial sem login lembrado | Visitante abre modalidades de resultados, volta e sai | Implementado; dry-run validado |

Dry-run não abre uma sessão Appium, não testa o APK, a rede nem comprova que os IDs aparecem na árvore nativa. Execuções físicas devem produzir evidências próprias, com hash/versão do APK, ambiente e versões de servidor/driver.

## Próximos grupos a implementar no repositório receptor

| Funcionalidade | Cenários propostos | Dependências |
| --- | --- | --- |
| Account | Login SSO, usuário lembrado, troca de usuário, cadastro e limites | Massa SSO autorizada; seletores do navegador; tratamento de desafio/biometria sem bypass |
| Home | Menu, campanhas, atalhos e preferências | Configuração de campanha controlada |
| Betting | Modalidades, números, surpresinhas, favoritos, combo e Rapidão | Concursos disponíveis; cenários por modalidade |
| Cart | Carrinho vazio/com itens, exclusão, carrinho preservado e favoritos | Massa controlada; estado conhecido |
| Results | Concurso, busca inválida, faixas e acessibilidade | Concursos e dados esperados versionados |
| Marketplace | Consulta, detalhe, cotas e inclusão | Bolões disponíveis; reserva somente em ambiente autorizado |
| Marketplace Filter | Quantidade de apostas/dezenas, valores, filtros combinados | Massa determinística para cada filtro |
| Payment | Pix, tokenização, processamento e falha | Sandbox autorizado; não usar cartões reais |
| Retail | Lotéricas, favoritas, localização e resgate | Massa de prêmio e autorização para operações com efeito |
| Notifications | Caixa remota, leitura, exclusão e preferências locais | Usuário/mensagens de teste; separar os dois repositórios |
| Responsible Gaming | Autoavaliação e suspensão | Conta de teste descartável e autorização para bloqueio |
| Support | FAQs, seções, links e termos | Dados/URLs esperados por ambiente |
| Onboarding | Tutorial, indisponibilidade, atualização obrigatória | Controle de preferências/Remote Config |
| Launcher/atalhos | Cinco ícones, calendário, retorno padrão e quatro atalhos | Publicação controlada; observação do launcher Android, fora da árvore Compose do app |

Não há casos vazios, `Pass Execution` ou testes que são marcados como aprovados sem implementar o fluxo. Cenários financeiros/SSO não foram simulados como certificação concluída.
