# feature:support

Implementação Compose das telas de ajuda, dúvidas, respostas, termos de uso e política de privacidade.

O módulo contém contratos de UI, reducer, modelos imutáveis, porta `SupportRepository` e componentes Compose. Não declara Activity, Fragment ou layout XML. Conteúdo HTML do backend é exibido por `WebView` encapsulada em `AndroidView`, sem inflar layout legado.

O repository concreto será fornecido pela camada de dados e deve adaptar os serviços atuais `secaoDuvidas`, `duvidasPerguntasRespostas`, `termo` e `aceiteTermo` sem expor Volley para a feature.

O task `check` executa testes, lint e falha quando a cobertura de linhas do reducer fica abaixo de 95%.
