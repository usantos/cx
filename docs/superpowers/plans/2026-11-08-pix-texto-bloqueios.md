# Texto e bloqueios do resgate Pix

- Pedido: exibir o texto completo do botão de sucesso e restaurar os bloqueios de Resgatar.
- Removido o modo temporário de envios concorrentes; confirmação mantém trava de envio único, validações de sessão, etapa, avisos e chave original.
- Resgatar e Cancelar desabilitados durante processamento.
- Botão de sucesso usa largura disponível e altura adaptável para exibir “Acesse Minhas apostas”, inclusive com fonte ampliada, preservando IDs existentes.
- RED: testes reproduziram corte de texto nas escalas 1 e 1,5 e botão habilitado durante processamento.
- GREEN: 114 testes de Presentation e 48 de UI PRD, zero falhas/erros/ignorados. Inclui três confirmações rápidas produzindo apenas um envio e ausência de corte nas duas escalas.
- Passaram arquitetura, IDs de automação, compilação PRD Debug, formatação Kotlin e git diff --check.
- APK universal protegido instalado em emulator-5554 com instalação preservando dados; artefato experimental removido.
- Nenhuma operação financeira real executada por automação. Não validada idempotência do backend real.
- Busca por Quina Friday/Friday sem referências no código atual e no legado consultado; existem Quina e Quina de São João.
