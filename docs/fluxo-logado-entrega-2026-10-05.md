# Fluxo logado e revisão de layout — 05/10/2026

Implementação no checkout Compose; referência em `/Users/ubiracysantos/Projects/legado/simlo-app-android`. O documento de pendências do início desta etapa permanece como registro da auditoria anterior.

## Implementado

- Biometria com consentimento persistido, refresh token protegido por AES/GCM no Android Keystore e separado por ambiente. Autenticação nativa no host existente, com confirmação de credencial em versões antigas. Entrada biométrica passa pela mesma verificação de conta; desativação e logout limpam o armazenamento. Não há restauração silenciosa de uma sessão autenticada.
- Cancelamento de navegador e biometria encerra a tentativa. Falha na consulta da conta revoga a sessão recém-adquirida; falha ao limpar mantém o bloqueio até nova tentativa. A opção Experimente limpa uma sessão ainda não validada antes de abrir a Home; falha na limpeza mantém o login com possibilidade de nova tentativa.
- Retorno após login/cadastro/termos/autoavaliação preserva a origem. Login validado recria a navegação privada e seus ViewModels; uma continuação de resgate somente é retomada pelo mesmo usuário. Compra retorna ao carrinho consultado novamente; resgate retorna à consulta da aposta. Nenhuma transação financeira é repetida automaticamente.
- Detalhes de compra, aposta, edição, filtro, lotérica e conferência são preservados em estado limitado a 96KiB, associado ao usuário. Após morte do processo, dados privados aguardam reautenticação do mesmo usuário. Logout e troca de usuário descartam o contexto anterior.
- Lote local com resposta incerta permite consultar a conta sem reenviar apostas. A tela Apostas preservadas compara o lote local com o carrinho remoto e permite excluir explicitamente somente o lote apresentado. Alteração do lote, outra conta ou falha de consulta impede essa exclusão. Apostas adicionadas posteriormente são mantidas.
- Ícones, fontes e componentes revisados contra os recursos originais. Correções abrangem login, compras, detalhe/edição de aposta, favoritas, carrinhos, cartões, busca de lotéricas e Rapidão. Rapidão possui oito etapas, favoritas, presets/limites de conta, máscara monetária, seletor de prêmio, números responsivos e resumo editável. A ajuda da busca de lotéricas abre Termos de uso, como no legado.

Referências e alterações visuais detalhadas: `LOGGED_LAYOUT_PARITY_AUDIT.md` e `LOGGED_LAYOUT_RESOURCE_MATRIX.tsv`.

## Revisão física posterior

A exigência de igualdade visual100% iniciou uma rodada posterior em aparelho. O status atual, correções verificadas e pendências estão em [exact-logged-device-status-2026-10-05.md](exact-logged-device-status-2026-10-05.md). Os resultados abaixo permanecem históricos desta entrega e não certificam a candidata visual mais recente.

## Evidência e limites

As capturas usam dados sintéticos, backend mock e pacote paralelo `.hmp.composevalidation`. O pacote oficial e seus dados não foram substituídos. O XCover7 permitiu capturar o login legado e percorrer as oito páginas do Rapidão. Capturas bloqueadas anteriores não contam como validação. Os demais estados da matriz foram inspecionados no emulador.

A matriz de recursos e uma captura inicial por rota não comprovam todos os estados dinâmicos nem equivalência pixel a pixel com o legado. Permanecem para homologação controlada: OAuth/biometria com credencial real, limites/cadastro/termos com servidor, confirmação de transferência após perda de resposta, cartão/Pix, localização e resgate. Não houve pagamento ou resgate real.

## Verificações

A etapa executou 522 testes JVM nos módulos diretamente afetados: app 48, cart 76, home 26, betting 70, auth 41, account 55, data 155, preferences 34 e CaixaDS 17; todos sem falhas. A matriz `modernCheck` passou com builds debug dos três flavors, lint, arquitetura e gates de cobertura. O carrinho atingiu 95,49% de linhas, acima do mínimo 95,01%, com testes reais da tela de revisão do lote.

Testes Android de autenticação: 16 no emulador e 16 no XCover7. Keystore: 8 no emulador e 8 no XCover7. A matriz global executou 27 testes de armazenamento, 3 de apresentação, 29 de carrinho, 4 de Home e 5 de notificações em cada dispositivo. O teste Android de salto entre páginas do Rapidão passou nos dois dispositivos. Os três builds release também passaram após as últimas correções visuais. Os ajustes finais de sessão e navegação passaram nos testes dos três flavors, lint e cobertura. A rodada de instrumentação anterior permanece válida; no último recheck global, suas cinco tarefas foram excluídas para evitar repeti-las durante a recaptura. O teste ampliado do Rapidão passou separadamente nos dois dispositivos, incluindo toques no centro dos indicadores 8 e 3 e verificação de página centralizada.

Capturas atuais: 78 estados de rotas no emulador; a rota que abre navegador externo foi preservada sem acionamento. Oito páginas do Rapidão foram percorridas no XCover7. A comparação encontrou e corrigiu corte no Pix, cores/trevos de modalidades, sobreposição no Combo e desalinhamento ao saltar páginas do Rapidão e sobreposição das áreas de toque dos indicadores. Combo, Pix e modalidades foram recapturados e revisados no APK atualizado. Oito páginas do Rapidão foram recapturadas no XCover7 após a correção final. O salto8→3 foi conferido no aparelho e a entrada monetária5432 produziu R$54,32; cancelar manteve o valor anterior, sem salvar configuração. A correção final das áreas de toque passou em instrumentação nos dois dispositivos, lint/cobertura do módulo e compilação debug/release. Os três APKs release completos foram verificados antes deste último ajuste localizado de layout.

Os diretórios `screens`, `emulator-screens` e `xcover-screens` são referência anterior, feita com APK antigo. A validação visual atual usa `final-emulator` e `final-xcover`. Mesmo na matriz atual, token representa estado de carregamento; a captura direta de Pix expirado contém código ativo, e sucesso mostra processamento. Essas imagens não aprovam os respectivos estados finais. Exclusão de lote pendente foi validada por testes Android com dados controlados, pois a captura inicial da rota não possui um lote pendente.

O Combo ainda utiliza imagem de placeholder na fixture mock; sua imagem/conteúdo real permanece incluído na homologação visual com dados do servidor.

Resultados de comandos, APK, checksum e capturas: `build/verification/logado-layout-20261005/`. Os logs da rodada final são preservados junto ao APK e seu checksum. `final-modern-release-recheck.log` contém a matriz final; `rapid-touch-final.log` registra a última correção do indicador; `delivery-summary.json` e `jvm-summary.json` resumem os resultados.
