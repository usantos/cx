# Bibliotecas e toolchain

| Área | Biblioteca | Versão |
|---|---|---:|
| Build | Android Gradle Plugin | 8.13.0 |
| Build | Gradle Wrapper | 8.14.6 |
| Linguagem | Kotlin / Compose Compiler | 2.4.10 |
| Processamento de código | KSP | 2.3.6 |
| Injeção de dependências | Hilt / Dagger | 2.58 |
| UI | Compose BOM | 2026.06.00 |
| Design System | CaixaDS (`:core:design-system`) | interno |
| Ícones | Compose Material Icons Core | gerenciado pelo BOM |
| UI | Activity Compose | 1.12.4 |
| Inicialização | AndroidX Core SplashScreen | 1.2.0 |
| Lifecycle | Lifecycle Compose | 2.10.0 |
| Navegação | Navigation Compose | 2.9.8 |
| UI | Compose WebView (`io.github.kevinnzou:compose-webview`) | 0.33.6 |
| Animações vetoriais | Lottie Compose | 6.7.1 |
| Imagens/GIFs | Coil Compose / GIF | 3.5.0 |
| Concorrência | Coroutines | 1.11.0 |
| HTTP | Retrofit | 3.0.0 |
| HTTP | OkHttp / MockWebServer | 5.4.0 |
| JSON | Gson | 2.15.0 |
| Configuração remota | Firebase Remote Config BOM | 34.18.0 |
| OAuth | AppAuth | 0.11.1 |
| Compatibilidade do fornecedor | AppCompat / ConstraintLayout | 1.7.1 / 2.2.2 |
| Sala de espera | FilaBRAAdapter | 2.6.5.1 (AAR local verificado) |
| Câmera | CameraX | 1.6.2 |
| Leitura de bilhetes | ML Kit Barcode Scanning | 17.3.0 |
| QR Code de resgate | ZXing Core | 3.5.4 |
| Testes | JUnit 4 | 4.13.2 |
| Cobertura | Jacoco | 0.8.13 |
| Análise Android | Lint independente do AGP | 9.2.1 |

As versões ficam centralizadas em `gradle/libs.versions.toml` (plugins de build permanecem no composition root). O AGP 8.13.0 usa o plugin Kotlin Android explícito; Hilt processa o grafo com KSP, sem `kapt`. Não há versões dinâmicas, repositórios HTTP nem credenciais Maven no build moderno. AppCompat e ConstraintLayout existem apenas para compatibilidade binária do AAR FilaBRA. A tela da fila chama o WebView como composable; a biblioteca encapsula a ponte para o motor Android nativo exigida pela plataforma.

O módulo principal `firebase-config` é usado em vez dos antigos módulos KTX separados. Remote Config transporta somente um manifesto público assinado para rotação de pins TLS; a decisão de confiança pertence a `:core:key-management`, não ao Firebase ou ao backend. Retrofit/OkHttp executa as APIs e gera cURL sanitizado somente quando `-PnetworkLogging=true` em debug; release mantém essa instrumentação desligada.

A combinação Android Studio/AGP 8.13 usa SDK 36 e Java 21 (toolchain, testes e
bytecode). O R8 é fixado em 9.1.57 no classpath de build: Dependências compiladas com Kotlin 2.4 requerem R8
>=9.1.29, conforme a [matriz oficial](https://developer.android.com/build/kotlin-support).
Hilt 2.58 mantém a compatibilidade do plugin com AGP 8; a linha 2.59+ exige AGP 9.
Não se ignora a validação AAR nem se suprimem avisos de compileSdk incompatível.

Kotlin/Compose Compiler 2.4.10 é mantido; o leitor dos metadados no classpath de
processamento é atualizado separadamente para Hilt 2.58. Java e bytecode permanecem em 21.

`gradle/android-processors.gradle` atualiza apenas o leitor de metadados dos
processadores para 2.3.21. Mantém a validação estrita e evita impor versões à
biblioteca padrão Kotlin do runtime.
