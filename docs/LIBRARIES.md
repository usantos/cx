# Bibliotecas e toolchain

| Área | Biblioteca | Versão |
|---|---|---:|
| Build | Android Gradle Plugin | 9.2.1 |
| Build | Gradle Wrapper | 9.4.1 |
| Linguagem | Kotlin / Compose Compiler | 2.4.10 |
| Processamento de código | KSP | 2.3.6 |
| Injeção de dependências | Hilt / Dagger | 2.60.1 |
| UI | Compose BOM | 2026.08.00 |
| Design System | CaixaDS (`:core:design-system`) | interno |
| Ícones | Compose Material Icons Core | gerenciado pelo BOM |
| UI | Activity Compose | 1.13.0 |
| Inicialização | AndroidX Core SplashScreen | 1.2.0 |
| Lifecycle | Lifecycle Compose | 2.11.0 |
| Navegação | Navigation Compose | 2.10.0 |
| UI | Compose WebView (`io.github.kevinnzou:compose-webview`) | 0.33.6 |
| Animações vetoriais | Lottie Compose | 6.7.1 |
| Imagens/GIFs | Coil Compose / GIF | 3.6.1 |
| Concorrência | Coroutines | 1.11.0 |
| HTTP | Retrofit | 3.0.0 |
| HTTP | OkHttp / MockWebServer | 5.5.0 |
| JSON | Gson | 2.14.0 |
| Configuração remota | Firebase Remote Config BOM | 34.18.0 |
| OAuth | AppAuth | 0.11.1 |
| Compatibilidade do fornecedor | AppCompat / ConstraintLayout | 1.7.1 / 2.2.2 |
| Sala de espera | FilaBRAAdapter | 2.6.5.1 (AAR local verificado) |
| Câmera | CameraX | 1.6.2 |
| Leitura de bilhetes | ML Kit Barcode Scanning | 17.3.0 |
| QR Code de resgate | ZXing Core | 3.5.4 |
| Testes | JUnit 4 | 4.13.2 |
| Cobertura | Jacoco | 0.8.13 |

As versões ficam centralizadas em `gradle/libs.versions.toml` (plugins de build permanecem no composition root). O AGP 9.2.1 usa Kotlin integrado; Hilt processa o grafo com KSP, sem `kapt`. Não há versões dinâmicas, repositórios HTTP nem credenciais Maven no build moderno. AppCompat e ConstraintLayout existem apenas para compatibilidade binária do AAR FilaBRA. A tela da fila chama o WebView como composable; a biblioteca encapsula a ponte para o motor Android nativo exigida pela plataforma.

O módulo principal `firebase-config` é usado em vez dos antigos módulos KTX separados. Remote Config transporta somente um manifesto público assinado para rotação de pins TLS; a decisão de confiança pertence a `:core:key-management`, não ao Firebase ou ao backend. Retrofit/OkHttp executa as APIs e gera cURL sanitizado somente quando `-PnetworkLogging=true` em debug; release mantém essa instrumentação desligada.
