# Exceção temporária para testar envios Pix concorrentes em PRD

## Estado atual

Experimento encerrado a pedido do usuário. A configuração de múltiplos envios e seus caminhos de execução foram removidos do código. Uma nova compilação protegida, com o texto completo do botão de sucesso, foi instalada em `emulator-5554`, preservando os dados. O APK experimental local foi removido; `output/prd-debug-pix-protegido.apk` contém a versão corrigida. As seções abaixo registram o experimento anterior e não descrevem a configuração atual.

## Autorização e limite

O usuário pediu explicitamente retirar temporariamente o bloqueio de múltiplos cliques para testar o backend de produção. Foi preparado e instalado um APK PRD Debug com a exceção ativada. Nenhum clique em Resgatar ou operação financeira real foi executado por automação; o teste real depende dos acionamentos manuais do usuário.

## Implementação

- `-PallowRepeatedPixRedemption=true` ativa `ALLOW_REPEATED_PIX_REDEMPTION` somente em Debug. Padrão `false`; Release sempre gera `false`, mesmo com a propriedade ativada.
- A composição injeta a configuração no ViewModel. O parâmetro é `false` por padrão em construções diretas e testes.
- Cada clique manual Pix durante a confirmação pode iniciar uma solicitação independente, mantendo o botão disponível enquanto as respostas estão pendentes. Não há disparo automático de três chamadas.
- Sessão, avisos, etapa e chave confirmada continuam validados. Cancelamento, saída e outros canais continuam bloqueados durante pendências. Mercado Pago mantém o envio único.
- Resultados são guardados e publicados somente após finalizar a última solicitação; respostas fora de ordem não abrem sucesso ou redirecionamento com outros envios pendentes.

## Validação

- RED: parâmetro do modo de teste ainda inexistente.
- GREEN inicial: três chamadas simuladas pendentes em modo explícito; uma chamada no modo normal/Mercado Pago; zero chamadas com entrada de sessão desabilitada.
- Revisão encontrou saída antecipada após a primeira resposta. RED adicional reproduziu o caso com três respostas independentes; correção adia os resultados até concluir todas.
- PASSOU: Retail Presentation 119 testes e Retail UI PRD 45 testes; total 164, zero falhas, erros ou ignorados.
- PASSOU: `verifyCleanArchitecture`, `verifyAutomationIds`, `:app:assemblePrdDebug` e geração de BuildConfig Release. Conferidos os campos gerados: Debug `true`, Release `false`.
- PASSOU: revisão independente após a correção, formatação Kotlin e `git diff --check`.
- Não executados: resgate financeiro real, teste de idempotência do backend real, suíte global de todos os módulos/ambientes e Lint/cobertura global.

## Artefatos locais e restauração

- Instalado em `emulator-5554` com `adb install -r`, preservando dados: `output/prd-debug-pix-concorrente.apk`.
- Preservada antes da atualização a versão instalada com bloqueio: `output/prd-debug-pix-protegido.apk`.
- Restaurar a versão protegida após o teste manual:

```sh
adb -s emulator-5554 install -r output/prd-debug-pix-protegido.apk
```

Para novas compilações protegidas, omitir a opção ou usar `-PallowRepeatedPixRedemption=false`. Os APKs estão em diretório ignorado; não são publicados pelo Git. Instalar uma versão reinicia o aplicativo e pode exigir autenticação novamente.
