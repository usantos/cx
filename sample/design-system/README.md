# Sample CAIXA Design System

Aplicativo Android independente para explorar o módulo `:core:design-system` com o tema azul e laranja da CAIXA. Não inicializa o aplicativo Loterias, autenticação, repositórios ou serviços de negócio.

Use o JDK 21 e o Android SDK 36 configurados para o projeto. O sample pode ser selecionado como aplicação no Android Studio ou executado pelo terminal:

```sh
./gradlew :sample:design-system:assembleDebug
./gradlew :sample:design-system:installDebug
./gradlew :sample:design-system:testDebugUnitTest
```

O launcher se chama **CAIXA • Design System**. O catálogo agrupa 121 APIs públicas Compose por família, incluindo os primitivos do DS. A busca aceita o nome, o pacote ou um parâmetro da API. Toque em um item para ver um exemplo interativo, a chamada de uso e as assinaturas das sobrecargas. Os controles de navegação e o botão Voltar do sistema retornam à lista preservando a busca.

A página `CaixaDSTextField` mostra as variantes `OUTLINED`, `LABELLED` e `LEGACY`. A página `CaixaDSBasicTextField` apresenta as sobrecargas `String` e `TextFieldValue`. Os exemplos mantêm o estado local; ações incrementam um contador demonstrativo. Diálogos, menus e folhas abrem mediante uma ação explícita e podem ser fechados.

O exemplo Web usa apenas `file:///android_asset/catalog.html`, com JavaScript desativado e uma política que bloqueia outras origens. O manifesto não solicita a permissão de internet. A animação usa um arquivo JSON vetorial incluído nos recursos. Os valores de pagamento/carrinho são fictícios e nenhum callback realiza uma operação financeira.

Os exemplos e as entradas do registro estão separados em arquivos pequenos por família. Cada API mantém um identificador de pacote e nome estável; as sobrecargas compartilham a página de detalhes. Os textos de interface, a documentação e os snippets estão em `src/main/res/values/strings.xml`.

Os testes locais verificam identidade e documentação de todas as entradas, navegação/busca, alterações de estado via ações semânticas, edição dos campos e layout dos exemplos. Robolectric verifica o contrato da integração Android; a aparência real de WebView e controles nativos requer um dispositivo/emulador.

Validação local em 22/10/2026: dez testes do catálogo passaram, incluindo a abertura real da `CatalogActivity`, busca, retorno mantendo posição da lista, recriação da Activity no detalhe, alterações nos exemplos e ausência de sobreposição dos campos. No emulador Android 16 (API 36), foram conferidos a lista, a edição das três variantes e a renderização do HTML local. Capturas ficam em `build/reports/manual/` após esta execução; o APK está em `build/outputs/apk/debug/design-system-debug.apk`.
