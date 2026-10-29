# Atualização obrigatória

O aplicativo consulta o Firebase Remote Config antes de liberar o fluxo de entrada, incluindo o login.

## Parâmetro

- Nome: `force_update`
- Tipo: Boolean
- Padrão local: `false`, em `app/src/main/res/xml/remote_config_defaults.xml`.
- `true`: mostra a tela de atualização, sem ação de fechar ou voltar ao aplicativo.
- `false`: libera o fluxo normal.

Cadastre e publique o parâmetro no projeto Firebase correspondente ao ambiente do aplicativo. Esta alteração de código não publica configurações no Firebase.

Use uma condição por versão do aplicativo para atingir somente versões antigas. Publicar `true` para todas as versões também bloqueará a versão atualizada.

## Comportamento

A consulta ocorre ao iniciar e ao retornar ao primeiro plano. Mudanças recebidas pelo listener em tempo real também atualizam o bloqueio. Durante a consulta inicial, o login não é apresentado.

Um valor obrigatório previamente ativado permanece bloqueando mesmo sem internet. Se não existir valor remoto ativado e a consulta falhar, vale o padrão local `false`.

O botão abre a página do pacote instalado no Google Play, com alternativa HTTPS se o aplicativo da loja não estiver disponível. Abrir a loja não libera a tela. O bloqueio permanece enquanto o valor ativado for `true`.

A tela usa o Design System e textos em recursos Android. O botão Voltar é consumido; sair pelo sistema ou encerrar o processo continua possível, mas reabrir o aplicativo aplica novamente a verificação.

## Validação

- `ForceUpdateViewModelTest`: consulta inicial, ativação/desativação remota, falha offline com valor obrigatório em cache e padrão `false` sem cache.
- `ForceUpdateGateUiTest`: oculta login, bloqueia Voltar, mantém a tela após a ação da loja e libera o conteúdo somente em `READY`.

O teste de interface usa configuração local e callback de loja; não publica parâmetros nem depende de alterações no Firebase de produção.
