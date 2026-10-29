# Servidor Appium local

A versão do servidor e do UiAutomator2 deve ser compatível com o Android do aparelho e com o ambiente da equipe. O cliente Python está fixado no requirements; driver/servidor são instalações independentes. Registre as versões homologadas em cada execução. Não execute `npm install -g appium` indiscriminadamente na máquina compartilhada da farm.

Depois de instalar as versões homologadas:

```sh
appium --version
appium driver list --installed
appium --address 127.0.0.1 --port 4723
```

O perfil local usa `http://127.0.0.1:4723/`. Se o servidor for iniciado com `--base-path /wd/hub`, ajuste remoteUrl. O perfil Keeggo usa o endpoint remoto e não requer Appium rodando no computador do executor. Não habilitar `--relaxed-security` ou desativar verificação TLS: estes cenários não precisam dessas opções.
