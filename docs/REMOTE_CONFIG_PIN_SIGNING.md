# Gerar e assinar o parâmetro de pins

O script `scripts/sign-remote-config-pins.py` gera o valor **JSON** do parâmetro Firebase Remote Config `tls_pinning_keyset`. Ele não publica nada no Firebase, não gera chaves privadas e não altera o build do app.

Requisitos: Python 3.9+ e OpenSSL no PATH, em macOS/Linux. Execute a assinatura em ambiente controlado. As chaves precisam ser EC P-256 em PEM, pertencentes ao usuário atual, com permissões `0600` ou `0400`, fora do projeto e de qualquer repositório Git. Chaves PEM criptografadas são suportadas. Para HSM/KMS, mantenha a cerimônia de assinatura da operação; este script não implementa um provedor HSM/KMS.

## Assinar usando os pins atuais

O arquivo `.local/build-security.properties` já contém `SIMLO_TLS_DEFAULT_PINS`. O script lê a mesma estrutura e preserva todos os hosts e pins, sem substituí-los. Variáveis de ambiente `SIMLO_TLS_DEFAULT_PINS`, `SIMLO_KEYSET_TRUSTED_SIGNERS` e `SIMLO_KEYSET_SIGNATURE_THRESHOLD` têm precedência sobre o arquivo.

Crie uma pasta externa para as saídas públicas e execute, ajustando os caminhos para chaves que **já existem**:

```sh
mkdir -p "$HOME/simlo-remote-config"

python3 scripts/sign-remote-config-pins.py \
  --version 1 \
  --previous-version 0 \
  --valid-days 30 \
  --signer "root-a=$HOME/.config/simlo-signers/root-a.pem" \
  --signer "root-b=$HOME/.config/simlo-signers/root-b.pem" \
  --passphrase-env root-a=SIMLO_ROOT_A_PASSPHRASE \
  --passphrase-env root-b=SIMLO_ROOT_B_PASSPHRASE \
  --output "$HOME/simlo-remote-config/tls-pins-v1.json"
```

As variáveis de senha nomeadas acima devem estar disponíveis no ambiente da cerimônia; não coloque senhas nos argumentos, em arquivos do projeto ou no histórico do shell. Para chaves sem senha, omita o respectivo `--passphrase-env`. Nunca forneça o conteúdo de uma chave privada como argumento: `--signer` recebe somente seu caminho.

`--version` deve ser maior que a versão atualmente publicada/aceita. Informe essa versão anterior em `--previous-version`; o script não consulta o Firebase para descobri-la. O app também rejeita substituição por versões antigas. O horário padrão é o UTC atual; validade permitida: 1 a 180 dias. O padrão de quórum é 2, respeitando `SIMLO_KEYSET_SIGNATURE_THRESHOLD` quando configurado.

## Saídas e primeira configuração de confiança

São criados dois arquivos novos, sem sobrescrita:

- `tls-pins-v1.json`: envelope com `payload` Base64URL e assinaturas ECDSA SHA-256 em DER, também em Base64URL sem padding.
- `tls-pins-v1.signers.properties`: somente as chaves **públicas** SPKI de verificação em `SIMLO_KEYSET_TRUSTED_SIGNERS` e o limiar `SIMLO_KEYSET_SIGNATURE_THRESHOLD`.

Se ainda não houver signatários confiáveis configurados, o segundo arquivo fornece o material público para o bootstrap. A operação deve revisar e configurar esses valores no arquivo local ou no CI e publicar uma versão inicial do app com as âncoras corretas. O script não faz essa alteração automaticamente. Enquanto isso, o app mantém o fallback atual; ele não aceitará o manifesto apenas por estar no Firebase.

Para um quórum 2-de-3, no primeiro bootstrap passe também `--signer "root-c=/CAMINHO/EXTERNO/root-c.pem"` e seu `--passphrase-env`, se necessário. Assim, as três chaves públicas serão exportadas. Nas rotações seguintes, bastam duas dessas chaves; as demais chaves públicas já configuradas são preservadas na saída. Se já existe `SIMLO_KEYSET_TRUSTED_SIGNERS`, o script recusa qualquer assinatura cujo identificador/chave não corresponda ao material configurado.

Após revisão, copie **o conteúdo completo do JSON** para o valor JSON de `tls_pinning_keyset` no projeto Firebase do ambiente correspondente. Não use o nome `SIMLO_TLS_DEFAULT_PINS` como parâmetro remoto: ele é o fallback de build. Este JSON é o valor do parâmetro, não um template completo de publicação do Firebase.

## Proteções e limites

- Recusa chaves dentro do projeto, worktrees Git e repositórios bare; também verifica caminhos resolvidos para evitar desvios por symlink.
- Recusa chaves com permissões abertas, hard links, curva diferente de P-256 e a mesma chave sob identificadores diferentes.
- Valida schema, quantidade de hosts/pins, SHA-256, tamanho e janela de validade compatíveis com o verificador Kotlin.
- Verifica cada assinatura com sua chave pública antes de escrever as saídas; mensagens de falha não incluem diagnóstico bruto do OpenSSL, chaves nem senhas.
- Não copia chaves privadas, não as inclui no JSON e não modifica arquivos existentes.
- Certificados/pins públicos não autorizam rotação por si só: não podem substituir chaves privadas de assinatura.

Para rotacionar o certificado de um servidor, o manifesto deve incluir o pin atual e o novo **antes** da troca no servidor, respeitando a propagação e a validade. O script usa os pins atuais como solicitado; não descobre ou inventa o pin de um certificado futuro.

## Testes

```sh
python3 -m unittest discover -s scripts -p test_remote_config_signing.py
```

Os testes usam exclusivamente chaves efêmeras em diretório temporário externo ao projeto e removem-nas ao terminar. O JSON gerado também foi conferido com o `SignedKeySetVerifier` Kotlin do app, usando os pins locais atuais, sem publicar no Firebase nem configurar essas chaves de teste como âncoras do app.

## Novo certificado

Para extrair o pin de um novo certificado, preservar os pins já ativos e incrementar a versão automaticamente, use [o script de rotação](REMOTE_CONFIG_PIN_ROTATION_SCRIPT.md).
