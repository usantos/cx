# Catálogo de telas Compose

O `NavHost` registra 80 destinos catalogados, incluindo Home e o filtro do Marketplace. Os quatro seletores internos de números, modalidade e favoritos não são destinos artificiais. Marketplace, detalhe do bolão, resultados, login, onboarding, filtro, suporte e detalhe de aposta têm implementações próprias; os demais fluxos usam componentes Compose declarativos no respectivo módulo.

O catálogo comprova cobertura de navegação, não igualdade visual automática. O smoke garante renderização Compose sem crash com dados mock; a paridade exata de cada estado deve ser comprovada contra a referência legada na matriz visual.

| Feature | Rotas |
|---|---:|
| Home | 1 |
| Onboarding | 6 |
| Conta | 7 |
| Apostas, bolões e combos | 14 |
| Carrinho | 5 |
| Pagamento | 12 |
| Bilhetes, apostas e compras | 8 |
| Resultados | 4 |
| Jogo responsável | 5 |
| Notificações | 2 |
| Lotéricas e resgate | 10 |
| Filtro Marketplace | 1 |
| Suporte, termos e ajuda da modalidade | 5 |

A baseline completa preserva os nomes dos 414 layouts XML do checkout CX atual em `LEGACY_LAYOUT_BASELINE.txt`. Os 102 pontos de montagem de UI inventariados possuem destino explícito em `LEGACY_UI_INVENTORY.tsv`; ambos são validados pelo `modernCheck`.

Para verificar os 80 destinos/componentes navegáveis em um emulador com o APK `prdDebug` mock instalado:

```bash
./gradlew :app:assemblePrdDebug -PmockBackend=true
adb install -r app/build/outputs/apk/prd/debug/app-prd-debug.apk
./scripts/smoke-compose-routes.sh
```

Para validar a flavor `automacao`, cujo `applicationId` possui o sufixo `.hmp`, use:

```bash
./gradlew :app:installAutomacaoDebug -PmockBackend=true
./scripts/smoke-compose-routes.sh br.gov.caixa.loterias.apostas.hmp
```

O smoke exige processo ativo e conteúdo Compose acessível em cada rota; a única exceção é `account/change-login`, cuja UI visível passa ao Chrome por abrir imediatamente a Conta CAIXA. Para gerar a matriz visual completa a partir do mesmo catálogo, execute `./scripts/capture-compose-screens.sh`.

Antes do `NavHost`, telas Compose próprias cobrem sala de espera, indisponibilidade, atualização obrigatória e verificação de versão.

## Splashscreen

Enquanto a segurança de rede, a FilaBRA, a versão e as configurações são validadas, o app reproduz em Compose a animação institucional original `anim_ja_pensou`, em tela cheia, com `centerCrop`, velocidade `1.4x` e duração mínima de dois segundos. O arquivo Lottie original foi preservado como recurso `raw`; nenhum layout XML foi reintroduzido.

Os componentes que antes eram Fragments — cartelas, números, trevos, meses, times, cartões, paginação e cabeçalhos — são composição interna das telas, não destinos com ciclo de vida próprio. A rota `support/terms-view` usa um documento nativo `WebView` dentro do host Compose, sem layout XML, para preservar CSS, justificação e listas do HTML recebido como em `TermosAceiteActivity`. Injeta a fonte CAIXA regular original, mantém JavaScript desligado e abre links fora do documento com ACTION_VIEW/ACTION_SENDTO. A leitura não mostra tabs ou aceite; os fluxos existentes de dúvidas e aceite conservam sua lógica.

## Introdução

Na primeira execução, a introdução abre antes do login e mantém as três páginas, indicadores e dimensões do legado. A pergunta de maioridade aparece sobre a primeira página; “Não” leva à tela de restrição e “Sim” libera o pager. O botão mostra “Pular introdução” nas duas primeiras páginas e “Acessar aplicativo” na última. A conclusão é persistida usando a chave histórica `INTRODUCAO_KEY`, evitando reapresentar o fluxo após atualização do app.

A apresentação da +Milionária preserva os dois destinos distintos do legado: “Começar tutorial” abre o pager animado e grava `MAIS_MILIONARIA_TUTORIAL` somente quando o checkbox estiver marcado; “Pular apresentação” segue diretamente à simulação sem alterar essa preferência.

## Login

`LoginScreen` preserva o desenho da tela anterior: fundo azul CAIXA, logomarca central, ação contornada “Experimente”, divisor “OU”, CTA laranja “Faça login/cadastre-se”, linha de biometria e versão no rodapé. A implementação é integralmente Compose; logomarca e fontes são assets binários do CaixaDS, sem layout, drawable ou fonte declarados por XML.

O login continua abrindo o SSO no navegador seguro gerenciado pelo AppAuth, com o mesmo endpoint, client ID, redirect URI e escopos `openid profile offline_access` do fluxo anterior. A página exibida dentro do navegador permanece fornecida pelo servidor de identidade da CAIXA.

Ao retornar do navegador, durante a troca do authorization code pelo token, a tela usa o loading anterior: fundo azul da autenticação, overlay azul com 90% de opacidade, ícone da loteria de 44 dp girando a cada 1,2 segundo e texto “Carregando…”. O overlay mantém o atraso de 500 ms e fade-in de 200 ms para não piscar em operações rápidas.

## Alerta de Teimosinha

Ao abrir o detalhe de uma aposta original de Teimosinha que já teve resgate, `BetDetailRoute` exibe título “Atenção”, a orientação de uso da aposta de troca e o botão “Entendi”. O botão fecha somente o popup e mantém o usuário no detalhe.

O documento de `support/terms` (tela com abas de `TermosUsoActivity`) também usa WebView no host Compose, mas carrega o HTML recebido sem injetar fonte ou reescrever CSS. Links web navegam no documento e e-mail usa ACTION_SENDTO, como no controlador; JavaScript permanece desligado. As dúvidas continuam componentes Compose e callbacks do ViewModel.
