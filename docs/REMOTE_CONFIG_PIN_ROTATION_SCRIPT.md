# Rotação a partir de um novo certificado

Execute na raiz do projeto:

```sh
python3 scripts/rotate-remote-config-pins.py \
  --certificate "/caminho/novo-certificado.cer" \
  --hostname mobileloterias.caixa.gov.br \
  --previous "$HOME/simlo-remote-config/tls-pins-v1.json" \
  --output "$HOME/simlo-remote-config/tls-pins-v2.json"
```

O exemplo usa o manifesto versão 1 que já foi gerado. Nas próximas rotações, informe em `--previous` o JSON assinado **mais recente efetivamente publicado no Firebase**. O script não consulta o Firebase para descobrir a versão publicada. Não use um template completo de exportação do Firebase: informe o conteúdo do parâmetro `tls_pinning_keyset`.

O certificado deve ser o certificado público do servidor, em PEM ou DER, obtido da infraestrutura responsável. Arquivos contendo uma chave privada são recusados. O script confere o hostname e rejeita certificados expirados; ele extrai o SPKI e calcula seu SHA-256 em Base64. A cadeia de confiança e a data de ativação do certificado devem ser homologadas pela infraestrutura; o app continua exigindo a validação TLS normal, além do pinning.

## Resultado

- Verifica as assinaturas do manifesto anterior com as chaves públicas confiáveis do build local.
- Preserva **todos** os hosts e pins desse manifesto, incluindo os adicionados por rotações remotas anteriores.
- Acrescenta o pin novo somente ao hostname informado. Se a chave pública do certificado já estiver presente, mantém uma única ocorrência e renova o manifesto com a próxima versão.
- Usa a versão autenticada anterior + 1, automaticamente. `--version N` permite escolher outra versão, desde que maior.
- Assina com `root-a` e `root-b`, por padrão em `~/.config/simlo-signers/`, respeitando o quórum configurado.
- Gera o envelope JSON e um arquivo auxiliar `.signers.properties` somente com as chaves públicas.
- Sem `--update-local-defaults`, mantém o properties intacto. O manifesto anterior e os arquivos de saída existentes nunca são sobrescritos.
- Não publica nada no Firebase e não gera novas chaves privadas.

A base é o manifesto assinado anterior: isso preserva a configuração remota ativa sem reintroduzir automaticamente pins removidos que ainda existam em um fallback antigo do APK. No bootstrap atual, esse manifesto já contém todos os pins locais preservados.

## Atualizar também o fallback das próximas compilações

Acrescente `--update-local-defaults` ao comando:

```sh
python3 scripts/rotate-remote-config-pins.py \
  --certificate "/caminho/novo-certificado.cer" \
  --hostname mobileloterias.caixa.gov.br \
  --previous "$HOME/simlo-remote-config/tls-pins-v1.json" \
  --output "$HOME/simlo-remote-config/tls-pins-v2.json" \
  --update-local-defaults
```

Com essa opção, o script atualiza `SIMLO_TLS_DEFAULT_PINS` no arquivo indicado por `--properties` (por padrão, `.local/build-security.properties`). Ele une os pins/hosts locais existentes aos do manifesto gerado, sem duplicá-los nem removê-los. O JSON remoto continua baseado no manifesto anterior autenticado; os pins exclusivos do fallback local permanecem apenas no fallback.

A alteração preserva os demais valores, comentários, permissões e terminações de linha do properties. A gravação usa substituição atômica e só acontece após a assinatura ter sucesso. Se a gravação falhar, os novos arquivos de saída são removidos; uma mudança concorrente detectada no properties não é sobrescrita. Se a união exceder os limites do app, nada é gerado ou alterado.

Atualizar esse arquivo afeta as próximas compilações que o utilizarem. Se o CI fornece `SIMLO_TLS_DEFAULT_PINS` por variável de ambiente, atualize também esse valor no pipeline: ele tem precedência sobre o arquivo local. A opção não modifica o APK já instalado nem publica no Firebase.

## Publicação e troca do servidor

1. Revise o resultado gerado e publique seu conteúdo completo em `tls_pinning_keyset`, tipo **JSON**, no ambiente Firebase correto.
2. Aguarde a propagação e confirme em dispositivos que a nova versão foi aceita.
3. Só então instale o novo certificado no servidor. Durante a transição, ambos os pins permanecem aceitos.
4. Para retirar pins antigos, faça uma operação separada e revisada com uma versão maior. Este script é aditivo e não remove pins.

O limite do app é de 5 pins por host. Se a inclusão exceder esse limite, o script falha; ele não elimina pins automaticamente.

## Opções

- `--valid-days 30`: validade do novo manifesto; padrão 30 dias, máximo 180. Renove antes da expiração. Um manifesto anterior expirado, mas com assinaturas autênticas, pode ser usado como histórico para gerar a renovação.
- `--properties /caminho/build-security.properties`: configuração pública de confiança alternativa; padrão `.local/build-security.properties`.
- `--signer root-a=/CAMINHO/EXTERNO/root-a.pem --signer root-c=/CAMINHO/EXTERNO/root-c.pem`: selecionar signatários explicitamente.
- `--passphrase-env root-a=NOME_DA_VARIAVEL`: usar senha de chave criptografada pelo ambiente, sem colocá-la nos argumentos ou no projeto.
- `--signers-output /caminho/chaves-publicas.properties`: selecionar o arquivo auxiliar público.

São preservadas as proteções do script de assinatura: chaves privadas fora do projeto e de qualquer Git, permissões restritas, signatários distintos e correspondência com as chaves públicas configuradas. Veja [assinatura e bootstrap](REMOTE_CONFIG_PIN_SIGNING.md).

## Validação

```sh
python3 -m unittest discover -s scripts -p 'test_remote_config_*.py'
```

Os testes geram apenas certificados e chaves efêmeras externas ao projeto, removidos ao final. Nenhuma rotação real é produzida ou publicada durante os testes.
