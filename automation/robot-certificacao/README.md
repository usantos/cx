# SIMLO Robot - pacote para incorporação na certificação

Entrega independente criada do zero para copiar para `SIMLO-robot-teste-certificacao`. Mantém Testes, Funcionalidades, Pages, Resources, TestData e Scripts. Os testes usam **IDs do APK atualizado**, sem coordenadas, XPath posicional ou texto como fallback.

## O que está implementado

- Conexão Appium local ou Device Farm, UiAutomator2 e preservação dos dados do app (`noReset=true`, sem reinstalação automática).
- TQS por padrão; pacote PRD parametrizável; activity real `br.gov.caixa.loterias.apostas.MainActivity`, sem depender do alias de campanha.
- Três cenários: opções de entrada, acesso/saída de visitante e navegação pelas modalidades de resultados.
- Localizadores somente `id=`, inventário das declarações do Android, evidências, validação offline e relatório PDF.
- Adaptador local de resultados para RQM, sem simular publicação/autenticação no RQM.

Os três casos são um smoke inicial executável, não cobertura integral das 14 funcionalidades. A identificação das telas do Android abrange as 14 features; a criação dos cenários completos segue a matriz em [docs/COBERTURA.md](docs/COBERTURA.md). Resultados, contatos ou segredos reais não são distribuídos neste pacote.

## Requisitos e instalação

Python 3.11+; a combinação cliente foi verificada em Python 3.12. Robot 7.1.1 + AppiumLibrary 2.1.0 + Appium Python Client 3.2.1 + Selenium 4.16.0. Não atualizar Selenium/cliente isoladamente: AppiumLibrary 2.1.0 declara restrições para essas dependências. O lock registra o ambiente resolvido, sem caminhos da máquina de desenvolvimento.

Windows PowerShell, na pasta desta entrega:

```powershell
py -3.11 -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r requirements.lock.txt
Copy-Item environments.json.example environments.json
.\.venv\Scripts\python.exe Scripts\validar_entrega.py
```

Não é necessário ativar o ambiente para usar o executável acima. Se optar por ativá-lo, o comando é `.\.venv\Scripts\Activate.ps1`.

macOS/Linux:

```sh
python3 -m venv .venv
.venv/bin/python -m pip install -r requirements.lock.txt
cp environments.json.example environments.json
.venv/bin/python Scripts/validar_entrega.py
```

`requirements.txt` contém versões diretas; `requirements.lock.txt` contém o conjunto resolvido usado na verificação. O servidor Appium/driver são externos ao ambiente Python; consulte [appium-server/README.md](appium-server/README.md).

## Configuração local

Preencha `udid` em environments.json ou defina `SIMLO_UDID`. O APK com os IDs desta entrega precisa estar instalado. O pacote não instala, limpa dados ou remove aplicativos automaticamente. Configure **o mesmo ambiente do APK instalado**: `tqs` e `automacao` compartilham o package `.hmp`; o nome do pacote não distingue seus backends. PRD usa o package sem sufixo.

Variáveis opcionais têm precedência sobre o JSON: `SIMLO_CONFIG`, `SIMLO_TARGET`, `SIMLO_ENVIRONMENT`, `SIMLO_UDID`, `SIMLO_REMOTE_URL`.

Antes do smoke: aparelho desbloqueado, app na tela de entrada, sem login lembrado/biometria automática, serviços disponíveis, termos/tutorial e permissões iniciais já resolvidos e fonte padrão. Prepare esse estado numa conta/dispositivo de teste; o runner não encerra uma sessão autenticada nem apaga dados para forçar a pré-condição. Se um caso falhar, a execução para para preservar a evidência; restaure a pré-condição antes da próxima execução.

## Device Farm Keeggo

Use `environments.farm.json.example` como base. O endpoint de sessão fornecido é `https://labmobilecaixa-hml.keeggo.com:443/wd/hub`. **A conexão real com essa farm ainda depende de validação pela equipe com acesso.** O caminho OAuth não foi fornecido no pedido; o exemplo de tokenUrl é deliberadamente substituível e não é endpoint Keeggo validado.

Confirme com o administrador: URL de token, método de autenticação do cliente (`basic` ou `body`), scope, aplicação da credencial nas chamadas Appium, reserva/UDID, versões de Appium/UiAutomator2 e eventuais capacidades do fornecedor. O conector oferece os modos explícitos:

- `oauth_client_credentials`: solicita token no endpoint HTTPS configurado e envia Bearer somente à conexão da sessão. Credenciais em `SIMLO_FARM_CLIENT_ID` e `SIMLO_FARM_CLIENT_SECRET`; endpoint em `SIMLO_FARM_TOKEN_URL` ou tokenUrl.
- `bearer`: token obtido pela integração da equipe em `SIMLO_FARM_BEARER_TOKEN`.
- `none`: apenas quando a infraestrutura autorizada fornecer a autenticação fora do cliente. Deve ser escolhido explicitamente.

Os campos locais `oauthClientId` e `oauthClientSecret` também são aceitos, para compatibilidade, mas não devem ser versionados. Tokens não entram em capabilities nem nos argumentos Robot. Não usar token/usuário/senha na URL. Se o fornecedor usar outro contrato, adapte `Resources/SimloLibrary.py`; não invente nomes de capabilities. O OAuth oferecido é um adaptador genérico, não uma homologação da API Keeggo.

## Execução

Windows:

```powershell
.\.venv\Scripts\python.exe Scripts\executar.py
.\.venv\Scripts\python.exe Scripts\executar.py --suite login
.\.venv\Scripts\python.exe Scripts\executar.py --test CTA_02_Experimentar_E_Sair_Sem_Erro_De_Logout
```

macOS/Linux: troque o prefixo por `.venv/bin/python`. `--dry-run` confere sintaxe/keywords e **não executa no Android**. Também é possível usar diretamente:

```powershell
.\.venv\Scripts\python.exe -m robot --language pt-BR --exitonfailure --outputdir results\manual .\Testes\SUTA_login.robot
```

O runner cria diretório único em results com `log.html`, `report.html`, `output.xml` e capturas de tela. Não marque dry-run como certificação aprovada. Reexecutar não é retry automático de operações de negócio.

## IDs e implementação Android

Leia [docs/IDS_ANDROID.md](docs/IDS_ANDROID.md). O app exporta `automationId`/`uiId` como resource-id via Compose `testTagsAsResourceId`. Assim `id=simlo.account.login.experimente` localiza o botão independentemente do texto. IDs antigos foram preservados. Elementos repetidos podem compartilhar um ID de componente: localize a linha/contêiner correto antes do filho. Nenhum ID novo incorpora CPF, nome, cartão ou token.

Os IDs só existirão no APK que contém a alteração Android. Instalar este pacote Python não altera um APK antigo. Não copiar os IDs como `package:id/...`: os tags Compose são usados literalmente. A configuração desativa a autocomplementação de ID no UiAutomator2.

## Relatório PDF e RQM

```powershell
.\.venv\Scripts\python.exe Scripts\gerar_relatorio_pdf.py results\manual\output.xml --output results\manual\relatorio.pdf
.\Scripts\adapter_simlo.bat
```

O adaptador usa a mesma `.venv`, cria uma execução única, gera o PDF e `anexos.json`, preservando falha da suíte. Se `qm_AttachmentsFile` estiver definido, escreve uma lista UTF-8 com um caminho absoluto por linha. **Esse formato precisa ser conferido com o adapter RQM da equipe.** Não foi implementado um `open_connetion_rqm.bat` fictício: conexão, autenticação e publicação RQM permanecem no projeto receptor. `anexos.json` contém os anexos prontos para essa integração.

## Incorporação e manutenção

1. Copiar o conteúdo desta pasta para uma branch do projeto de certificação, preservando implementações existentes e revisando colisões de nomes.
2. Configurar farm/dispositivo e instalar o APK atualizado; conferir os IDs com Appium Inspector.
3. Executar `Scripts/validar_entrega.py` antes do smoke real e registrar versões do servidor/driver/APK.
4. Validar os três casos no dispositivo; incorporar novos fluxos conforme a matriz e massas autorizadas.
5. Manter Pages, TestData e contrato de IDs junto com mudanças no app.

No Android, `verifyAutomationIds` faz parte de `preCommitCheck`: confere inventário, seletores e IDs explícitos nos controles Compose básicos. Ele não compara pixels nem prova que todo nó aparece na árvore nativa; isso exige execução Android. Testes Robot em farm são etapa separada de CI/certificação e não substituem unitários, Lint, Detekt ou JaCoCo.

Referências: [Compose/UiAutomator](https://developer.android.com/develop/ui/compose/testing/interoperability), [AppiumLibrary 2.1.0](https://github.com/serhatbolsu/robotframework-appiumlibrary/tree/v2.1.0), [UiAutomator2](https://github.com/appium/appium-uiautomator2-driver).

## GitHub Copilot

As regras de manutenção estão em [.github/copilot-instructions.md](.github/copilot-instructions.md). Copie também a pasta oculta `.github/` ao incorporar esta entrega no repositório de certificação. Se já existir um arquivo de instruções nesse destino, integre as regras preservando as regras locais, em vez de substituí-lo. A configuração do Android fica no repositório do app; esta cópia contém todas as orientações necessárias ao pacote independente.
