# Loading institucional — revisão de 07/09/2026

## Referência e diferenças corrigidas

Referência: `LoadingViewLoterias.java`, `dialog_loading.xml`, `DialogFullScreenTheme` em `values/styles.xml` e cores/strings do checkout legado. **Não confundir com `alerta_carregando.xml`**, que usa ProgressBar e outro layout. O login usa `AlertDialogUtils`, que delega a `LoadingViewLoterias`; o retry de indisponibilidade chama o mesmo componente diretamente.

- No legado, o alpha é aplicado à raiz inteira. No Compose anterior, `.background(...).alpha(...)` deixava o fundo fora da camada animada. A ordem agora inclui o fundo no fade.
- Delay de 500 ms e fade de 200 ms preservados. O atraso faz parte da especificação de animação, em vez de uma corrotina independente do relógio de animação.
- Rotação de 1.200 ms iniciada junto com a montagem, inclusive durante o atraso. O legado não define interpolador; `ValueAnimator`/`ViewPropertyAnimator` usam AccelerateDecelerate. A fórmula foi confrontada com os fontes locais do Android SDK 36, substituindo a rotação linear e o easing padrão distinto do Compose.
- Texto CAIXA Bold de 17 sp com `includeFontPadding`, sem herdar espaçamento/altura de linha de Material `bodyLarge`. Ícone de 44 dp, sem espaçamento extra entre ele e o texto.
- Overlay `#E6005EAB` preservado. O PNG moderno `loterias_loading_icon` é idêntico por SHA-256 ao `icon_loteria` xxxhdpi legado: `1d5a4aab47edf12ae794ba19a3b78964a1439997e55f7dabcc574a383a4f3576`. Isso não comprova equivalência da rasterização em todas as densidades.
- O novo modal compartilhado remove `FLAG_DIM_BEHIND`, equivalente ao `backgroundDimEnabled=false` legado. Resgate/cadastro deixam de duplicar a configuração de janela nas features. Login e indisponibilidade também passam a bloquear interação durante o loading, não apenas desenhar uma camada sem interceptação.

## Uso

```kotlin
if (state.isLoading) {
    CaixaDSAuthenticationLoadingDialog()
}
```

Não envolver em outro `Dialog` nem adicionar scrim por fora. O estado da operação pertence ao ViewModel; o componente não inicia/reenvia requisições, não navega e sai da composição quando a operação deixa o estado de loading. `CaixaDSAuthenticationLoading` continua disponível como conteúdo visual para telas dedicadas; ele sozinho não bloqueia interação. A revisão posterior `AUTHENTICATION_RETURN_PARITY_AUDIT.md` substituiu a variante não modal da tela de token por `AccountTokenScreen`, compartilhado com o retorno OAuth real e com a mensagem legada “Carregando…”.

## Validação e limites

- 11 testes JVM do CaixaDS aprovados: oito existentes de tokens e três novos de movimento. O teste usa as mesmas especificações de animação do componente, verificando atraso, pontos intermediários do fade, rotação em três ciclos e a curva legada.
- Compilação isolada dos fontes atuais do CaixaDS e consumidores alterados. Artefatos em `build/verification/caixads-loading-parity-20260907/`; dependências e recursos já disponíveis localmente. Essa verificação não executa geração de recursos, Hilt/KSP ou empacotamento.
- Dois novos testes instrumentados verificam pixels do fundo antes/depois do atraso e descarte/remontagem da animação. **Compilação não equivale a execução desses testes no Android.**
- `./gradlew :core:design-system:testDebugUnitTest --offline` continua falhando antes das tarefas, ao abrir o lock de `gradle-9.4.1-bin.zip` fora da área permitida. Nenhum desvio de permissões foi aplicado.
- Permanecem pendentes execução instrumentada, screenshots pareados, bloqueio real de Voltar/toques, barras do sistema, escala de animação/acessibilidade, densidades e todos os flavors/Release. Não declarar igualdade visual integral nem cobertura global maior que 95% com estes resultados.
