# Dados e localizadores

`locators.json` contém os nomes consumidos pelas Pages e os IDs literais do APK. `android-ids.json` é o inventário de declarações estáticas mantido junto com o Android; não é uma massa de usuários.

Novas massas devem ser separadas por funcionalidade e ambiente, usando dados sintéticos aprovados. Não incluir CPF, senha SSO, chave Pix, token, cartão ou credencial de farm reais. Dados de execução e credenciais são responsabilidade do ambiente seguro da equipe. Os cenários iniciais são de visitante e não exigem massa de login.

Os caminhos e linhas dos dois inventários indicam o proprietário físico atual,
inclusive os renderizadores em splits `*_ui`; os IDs e expressões legadas foram
preservados. `modulo.estado` aponta para `simlo.modules.0001`, na apresentação
compartilhada da base. `modulo.acao` deve ser consultado dentro desse contêiner
quando a UI ainda estiver em preparação, falhar ou estiver indisponível.
