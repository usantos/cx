# GitHub Copilot no projeto SIMLO

O projeto usa o formato nativo de instruções do Copilot. As regras valem para manutenção do app inteiro; a configuração não depende de AGENTS.md.

## Arquivos e abrangência

| Arquivo | Conteúdo |
|---|---|
| [Instruções gerais](../.github/copilot-instructions.md) | Arquitetura, restrições, segurança, variantes, validação e mapa dos guias |
| [Android](../.github/instructions/android.instructions.md) | App, Core, features, sample, MVVM/Redux, CaixaDS, acessibilidade e IDs |
| [Qualidade](../.github/instructions/quality.instructions.md) | Gradle, scripts, CI, testes, pre-commit e JaCoCo |
| [Segurança](../.github/instructions/security.instructions.md) | Sessão, TLS, rede, persistência e Remote Config |
| [Documentação](../.github/instructions/documentation.instructions.md) | Guias, contratos, evidências, geração do PDF e manutenção das instruções |
| [Robot no Android](../.github/instructions/robot.instructions.md) | Integração com o pacote de certificação |
| [Robot portátil](../automation/robot-certificacao/.github/copilot-instructions.md) | Regras independentes para copiar ao repositório da equipe |

As instruções específicas usam frontmatter `applyTo` com caminhos relativos à raiz. As regras essenciais também aparecem no arquivo geral, pois o suporte a arquivos por caminho varia por cliente/versão. Os guias existentes continuam sendo as referências técnicas: não foram convertidos em prompts nem substituídos.

## Uso no Android Studio e demais clientes

1. Abra a raiz completa do repositório no IDE com GitHub Copilot habilitado e atualizado.
2. Confira nas configurações do plugin que as instruções personalizadas do repositório estão habilitadas, quando essa opção estiver disponível.
3. Inicie uma conversa no contexto deste projeto. Confira as referências da resposta para verificar se `.github/copilot-instructions.md` foi usado, quando o cliente exibir essa informação.
4. Se o cliente não carregar instruções por caminho, anexe o arquivo específico da área ou peça que ele seja lido antes de editar. O arquivo geral também orienta essa leitura.
5. Solicite a implementação com validação e atualização da documentação. O agente deve informar o que executou e qualquer etapa pendente.

Exemplo de solicitação:

> Leia as instruções do repositório e de Android. Corrija o fluxo de usuário da feature account, mantendo MVVM, Clean Architecture, ReduxKotlin e IDs existentes. Preserve headers e baselines. Atualize testes e documentação e informe os resultados das verificações executadas.

## Validação obrigatória

Instruções de IA orientam o Copilot; o bloqueio técnico continua no hook e no CI. Nenhuma instrução garante que toda sugestão será correta ou que o cliente carregará todos os arquivos.

```sh
# Instalar o hook, preservando configurações existentes:
sh scripts/install-git-hooks.sh
# Depois de preparar manualmente no staging o conteúdo final completo:
python3 scripts/validate-pre-commit.py
```

O runner exige workspace/index correspondentes e executa `preCommitCheck`. Não usar `--no-verify`, exclusões de tarefas, redução de cobertura ou atualização de baselines. Consulte [qualidade](QUALITY_GATE.md) e [hook/CI](quality/hooks-and-ci.md). A proteção remota exige que a equipe configure o check obrigatório na branch.

## Incorporação do pacote Robot

Copie todo o conteúdo de `automation/robot-certificacao/`, incluindo `.github/`, para o repositório de certificação. Quando já houver instruções Copilot no destino, integre o conteúdo sem sobrescrever regras existentes. Links e comandos do arquivo portátil são relativos ao próprio pacote e não dependem do checkout Android.

## Manutenção

Atualize instruções quando a arquitetura, os comandos ou os contratos mudarem. Não gravar contagens de testes ou percentuais históricos como garantias permanentes. Fontes técnicas continuam em `docs/`; PDFs, ZIPs, APKs e resultados são artefatos gerados, fora do commit.

Formato conferido na [documentação oficial do GitHub sobre instruções de repositório](https://docs.github.com/en/copilot/how-tos/copilot-on-github/customize-copilot/add-custom-instructions/add-repository-instructions) e na [documentação do VS Code sobre instruções personalizadas](https://code.visualstudio.com/docs/agent-customization/custom-instructions), em 29/10/2026. A disponibilidade por cliente deve ser conferida na versão usada pela equipe.
