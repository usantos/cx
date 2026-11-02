# Migração nominal de toda a família para LoteriasCaixaDS

## Escopo confirmado pelo usuário

Renomear toda a família: objeto, temas, componentes, tokens, ícones, helpers,
arquivos, previews e consumidores. Os módulos `core/design-system` e
`sample/design-system` e o pacote genérico `core.designsystem` conservam sua
função arquitetural. A migração não redesenha o app nem muda regras de negócio.

## Contratos

- APIs passam de `CaixaDS*` para `LoteriasCaixaDS*`; helpers camelCase seguem o novo prefixo.
- Arquivos e referências de código/documentação atuais acompanham os símbolos. Os recursos próprios `caixads_*` passam a `loterias_caixa_ds_*` sem alterar bytes; aliases negociais ficam iguais.
- IDs, keys, symbols, localizadores, enums/valores e parâmetros externos permanecem iguais. Inventários de automação atualizam somente origem/linha.
- Baselines ficam byte a byte intactas. Corrigir achados expostos por arquivo/assinatura nova, sem novas supressões, exclusões, severidades ou limiares.
- Relatórios e planos históricos preservam os nomes medidos; links para fontes podem acompanhar os caminhos novos. Registros JSON históricos não são reescritos.
- Verificação principal passa a `verifyLoteriasCaixaDS`; `verifyCaixaDS` continua como alias de compatibilidade para a mesma verificação, sem relaxar a política.
- A seleção salva do catálogo deve reconhecer o identificador anterior depois da atualização.
- JDK/JVM21, AGP8.13.0, dependências, headers HTTP, parâmetros e comportamento visual permanecem iguais. Renomear referências/arquivos de headers é alteração nominal autorizada desta migração.

## Unidades verificáveis

- [x] Snapshot, inventário e auditoria de contratos/baselines antes da mudança. Baseline recente: 3.624 testes, LINE95,32%.
- [x] Migração mecânica idempotente, com mapa de caminhos e comparação de recursos/enums/IDs. Preservar árvore e alterações anteriores não commitadas.
- [x] Compilar app/catálogo e diagnosticar qualidade. Corrigir problemas reais de formatação/imports/constantes/coesão mantendo valores, fluxo e baselines.
- [x] Restaurar seleção antiga no catálogo por adaptação nominal na fronteira, com RED→GREEN comportamental.
- [x] Atualizar verificadores/fixtures por nomes novos, provar casos positivos/negativos sem reduzir cobertura dos contratos e manter alias CLI antigo.
- [x] Revisão independente, testes globais, LINE>95%, qualidade, contratos, APKs e comparação visual dos headers.
- [x] Documentar nomes atuais, exceções históricas, resultados atuais e limites. Sem staging, commit, push ou instalação automática.

Uma invocação Gradle por vez. A renomeação global é responsabilidade do integrador;
correções locais independentes só começam após congelar o mapa inicial de nomes.

## Resultado verificado em 02/11/2026

- Família inteira migrada e catálogo compatível com seleção salva antiga.
- Ktlint, Detekt, Android Lint, arquitetura e contratos aprovados.
- 3.625 testes, sem falhas, erros ou ignorados; LINE global de 95,37%
  (60.171/63.090), superior ao mínimo estrito de 95%.
- Testes Android dos 16 módulos afetados compilados; APKs TQS Debug e catálogo
  gerados, contendo tipos novos do DS e nenhum tipo antigo.
- Oito capturas de headers idênticas byte a byte; 40 baselines, 21 recursos,
  seis enums, 1.779 IDs atuais e 88 legados preservados.
- Revisão estática independente concluída sem achados pendentes; asserções do
  catálogo também verificam ID antigo desconhecido e pacote incorreto.
- `preCommitCheck` completo não executado: fontes novos permanecem fora do
  staging. As validações foram executadas diretamente. Sem commit, push ou
  instalação em dispositivo físico.

Registro de ações, diff imutável revisado, relatórios e capturas:
`/Users/ubiracysantos/simlo-artifacts/2026-11-02/ds-rename/`.
As ferramentas crapper/dryer/mutator não estavam disponíveis; nenhuma métrica
ou execução dessas ferramentas é alegada.

As dez fixtures isoladas do verificador DS aprovaram oito rejeições e duas
aceitações, com restauração conferida. Os 28 testes Python de contratos,
fontes Git/Windows e módulos também passaram. A cópia temporária usada para
exercitar o gate foi removida; os logs foram preservados fora do repositório.
