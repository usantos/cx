# Calendário anual de ícones pelo Remote Config

Pedido: JSON com todos os ícones e períodos, troca automática e padrão fora das datas. Usuário confirmou repetição anual e execução também com app fechado.

## Contrato

`app_launcher_icon` passa a conter objeto JSON com `default_icon: "default"`, `timezone: "America/Sao_Paulo"` e `campaigns`. Cada campanha tem `icon`, `start` e `end` no formato estrito `MM-DD`, com extremos inclusivos. Agosto Lilás 08-01/08-31, Setembro Amarelo 09-01/09-30, Outubro Rosa 10-01/10-31, Novembro Azul 11-01/11-30. Os períodos repetem anualmente. Intervalos que cruzam o ano são aceitos; em sobreposição vence a primeira entrada válida. Falta de datas, ícone desconhecido ou intervalo inválido não ativa campanha; JSON inválido retorna ao padrão. Texto simples antigo deixa de forçar campanha sem data.

Domain representa os períodos e resolve uma data/instante, incluindo próxima meia-noite no fuso configurado. Data interpreta JSON com Gson já disponível e recebe Clock para testes. Repositório mantém leitura dos valores ativados, sem fetch extra, e prepara os defaults/cache Firebase antes da leitura em processo de segundo plano.

Runtime Android agenda alarme inexato para a próxima avaliação diária, sem permissão de alarme exato. Receiver privado aplica ícone via controlador existente e agenda novamente; boot/mudança de hora retomam o calendário. Abertura/retorno ao app também reavaliam. O Android pode adiar tarefas em economia de bateria e bloqueia execução de apps forçadamente parados até a próxima abertura.

## Tarefas

- [x] Testes RED para períodos anuais, limites, virada do ano, timezone, valores inválidos, leitura atualizada e retorno ao padrão.
- [x] Modelo/resolução Domain e parser/repositório Data; preservar enum/recursos e a chave existente.
- [x] Testes RED do agendamento e receiver; implementação Android para boot/data/execução em segundo plano e retomada do app.
- [x] Atualizar defaults XML e exemplo JSON independente, documentação técnica e testes de regressão.
- [x] Verificar suites, análise dos arquivos alterados e cobertura oficial >95%, preservando headers/baselines/exclusões.

## Limites

Nenhum header, baseline, recurso de ícone, ID/destino de atalho ou exclusão do JaCoCo será alterado. Sem publicar Firebase sem acesso/evidência. Entregar JSON completo e informar se atualização remota foi publicada.

## Resultado final

3.458 testes aprovados, nenhuma falha/erro/ignorado. `verifyCoverage aggregateTestReport :app:assemblePrdDebug -PnetworkLogging=false --continue` aprovado; JaCoCo LINE95,05% (53.908/56.713) em68módulos. Ktlint dos18arquivos, limites de arquitetura deApp/features e hashesdosheaders/baselines/configuraçãodecobertura conferidos.

APK atualizado no Samsung SM_G556B (RXGL10CDZ0W). Após atualização, sem abrirMainActivity, receiveragendou29/10/2026à00:00Brasília; únicoaliasOutubroRosaLauncher e4atalhosdinâmicosregistrados. Nenhumhoráriododispositivoalterado.

Publicação no Firebase pendente: aba real do projetoapp-loterias-simlo-prd informa projeto inexistente ou falta de permissão. JSON definido nos defaults locais e emconfig/firebase/app_launcher_icon.json; não alegar publicação remota.
