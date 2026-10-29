# GitHub Copilot — SIMLO Robot Teste de Certificação

Responda e escreva comentários/documentação em português. Este pacote é independente: preserve seu funcionamento após copiar todo o conteúdo, incluindo `.github/`, para o repositório de certificação. Leia o README e os contratos em docs antes de alterar.

## Organização

- `Testes/SUTA_<funcionalidade>.robot`, casos `CTA_<numero>_<nome>`, Gherkin com `--language pt-BR`.
- Pages encapsulam interação e seletores; Funcionalidades expressam linguagem de negócio; Testes definem cenários e resultados esperados. Resources guarda configuração/conexão; TestData contém contratos e massas não sensíveis.
- Use somente IDs publicados em TestData. Não adicionar XPath posicional, coordenadas, sleeps fixos ou fallback silencioso por texto. IDs Compose são literais, sem acrescentar `package:id/`. Respeite escopo de componentes repetidos.
- O inventário Android vem das fontes do app; não inventar ID para um APK que não o publica. Confira com Appium Inspector no APK correto. Se o repositório Android não estiver disponível, solicite o contrato atualizado.

## Conexão e segurança

- Python 3.11+, dependências conforme requirements.lock.txt; não atualizar AppiumLibrary, cliente Appium e Selenium isoladamente. Servidor Appium e UiAutomator2 possuem instalação externa documentada.
- TQS é o ambiente padrão. TQS e Automação compartilham package `.hmp` e possuem backends distintos; confira a variante do APK. Use a activity real MainActivity, não o alias de ícone.
- Preserve noReset e a configuração explícita de UDID/URL/ambiente. Não instalar/remover apps, limpar dados ou efetuar logout de usuário autenticado para forçar pré-condições.
- Não versionar environments.json, .env, tokens, CPF, senha, chaves, APKs, results ou capturas pessoais. Segredos vêm das variáveis documentadas. Não inserir token na URL/capabilities/logs.
- Confirme o contrato de autenticação Keeggo/RQM; nunca inventar URL OAuth, capabilities ou publicação RQM. Preserve validação TLS.
- Não automatizar CAPTCHA, contornar biometria ou efetivar apostas/pagamentos no smoke. Não alterar headers, baselines ou regras de negócio Android para facilitar testes.

## Validação

Na raiz deste pacote e com o Python do ambiente virtual:

```sh
python Scripts/validar_entrega.py
python Scripts/executar.py --dry-run
```

Com dispositivo autorizado, APK correto e pré-condições do README atendidas:

```sh
python Scripts/executar.py --suite login
```

Na integração com o Android, execute também `./gradlew verifyAutomationIds` na raiz Android. Não confundir validação offline com aprovação no dispositivo/farm; não ignorar falhas de negócio ou usar SKIP para obter sucesso. O adapter RQM preserva falha e somente prepara anexos conforme contrato documentado.

Atualize README, docs/COBERTURA.md, docs/IDS_ANDROID.md e TestData quando aplicável. Registre versão/ambiente/limitações das evidências. Preserve arquivos e implementação existentes no repositório receptor ao incorporar o pacote.
