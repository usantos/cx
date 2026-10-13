# Ambientes de teste — 07/09/2026

## Fontes confrontadas

Legado: `AmbienteEnum`, `BuildConfigManager`, `LoginFragment`, `AppIndisponivelActivity`, `PrincipalActivity` e `build.gradle`. Moderno: `app/build.gradle`, `AppContainer`, `ApplicationModule` e `HomeDrawer`.

## Diferença funcional encontrada

O legado apresenta seis opções no login e na indisponibilidade: EXTERNO, INTERNO, INTERNO_ESTEIRA, EXTERNO_ESTEIRA, HMP e PILOTO. Salva `AMBIENTE_SELECIONADO`; TQS resolve constantes com o sufixo selecionado. Produção e automação ignoram a seleção no `BuildConfigManager`. Ao trocar, exibe “Mudança de Ambiente” / “O aplicativo será encerrado para aplicar as mudanças de ambiente.” e fecha as Activities ao confirmar.

Há inconsistências na referência: HMP é o nome salvo, mas suas constantes usam HML; a Home usa fallback EXTERNO, enquanto login e resolução de URLs usam EXTERNO_ESTEIRA. Reproduzir somente o rótulo poderia informar um ambiente diferente do backend efetivo.

## Alteração realizada

`LegacyTestEnvironment` registra as seis identidades e URLs literais, sem reflexão. `homeEnvironmentLabel` resolve o indicador pelo endpoint efetivamente configurado: TQS atual mostra EXTERNO_ESTEIRA; automação atual mostra EXTERNO; produção não mostra indicador. URLs/flavors desconhecidos não recebem rótulos inventados. O Compose recebe o texto resolvido e mantém o rodapé Sair/versão.

Os dois testes de catálogo/indicador cobrem todos os ambientes, endpoints, barra final, ocultação em produção, URL HTTP, hostname com sufixo malicioso e configurações desconhecidas. O catálogo não troca endpoints nem autoriza novos hosts. Fontes atuais: 139 testes JVM aprovados (120 de estados/domínio/rotas, 3 envelope, 15 repositories, 1 preferências), compilação isolada Compose/host concluída, em `build/verification/home-environment-20260907/`. Catálogo/indicador: 11/11 linhas no escopo medido; não inclui renderização.

## Pendências — não considerar seleção migrada

- Restaurar seleção no login/indisponibilidade, persistência da mesma chave e aviso de encerramento.
- Recriar consistentemente o grafo, sessão e caches de conta/carrinho ao trocar. Apenas `finishAffinity` deixaria singletons modernos no ambiente antigo até a morte do processo.
- Integrar REST, histórico, OIDC, conta CAIXA e FilaBRA. Piloto usa `loteriasplt`, não `loteriasdes`.
- Obter pins aprovados por ambiente/provedor. O fallback contém apenas o host produtivo; não há evidência de que seus pins sirvam para TQS/Piloto ou pagamentos. A aceitação silenciosa de hosts sem pin foi corrigida; ver `SECURE_PIN_ROTATION.md`.
- Incorporar hosts internos exatos na política de confiança com configuração aprovada, sem wildcard genérico para domínios internos.
- Validar menu, seleção, navegador, retomada/reinício e todos os flavors no APK. Não houve alteração de URL real nem conexão com ambientes remotos nesta auditoria.
