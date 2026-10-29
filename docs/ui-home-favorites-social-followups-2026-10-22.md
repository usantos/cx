# Ajustes Home, favoritos e Repasses — 22/10/2026

## Apostas favoritas

- O `LayerDrawable` nativo dos trapézios contém offsets negativos e uma rotação. Desenhado diretamente no Canvas Compose, ele escapava dos limites de 80×55dp, cobrindo nomes, números e ações de outras apostas.
- Aplicado recorte aos limites do Canvas. Mantidos os recursos originais, cores, faixas, fontes, margens e o Header Caixa atual.
- Teste de regressão reproduziu o vazamento antes da alteração: pixels fora da faixa recebiam a cor verde. A mesma verificação passou para dez modalidades após o recorte.
- Referências: `cx/app/src/main/res/layout/linearlayout_favoritas.xml` e `background_trapeze_megasena.xml`.

## Regra de favoritar aposta já realizada

- `DetalhesMinhasApostasButtonHelper.onValidaApostaListener` do Legado trata HTTP 400 como erro negocial, exibe a mensagem do backend e chama a inclusão somente após confirmação.
- `DialogUtils.dialogSim` encaminha rótulos Sim e Não; `configuraBotao` exibe o botão Não mesmo sem listener, fechando o diálogo sem incluir.
- A tela Compose agora preserva a mensagem negocial do backend e exibe Sim/Não. O diálogo bloqueia toque externo/Voltar como o padrão nativo. Não deixa o texto fixo afirmar que qualquer erro é uma duplicidade específica.
- Teste percorre cancelar, nova validação e confirmação; nenhuma inclusão ocorre ao escolher Não. Sim envia o rascunho validado uma única vez.

## Header da Home

- Sino, Outubro Rosa e trevo usam áreas de 48dp e centros igualmente espaçados, com ícones centralizados.
- Controles ficam ausentes durante o carregamento inicial e só aparecem com modalidades válidas, depois do primeiro frame do conteúdo carregado. Sem temporizador arbitrário.
- Voltar na Home abre confirmação de saída. Cancelar mantém a Activity; Sair encerra a Activity. Menu lateral aberto continua sendo fechado pelo próprio drawer antes da confirmação.
- Textos novos da confirmação pertencem aos recursos da Home.

## Último resultado

- Removida a soma do padding de 180 pixels com 40dp no fim do conteúdo.
- Margem final única de 16dp após a arrecadação total; o restante da apresentação permanece igual.
- Matriz instrumentada cobre onze modalidades, inclusive Dupla Sena, +Milionária, Super Sete, Timemania, Dia de Sorte e jogos esportivos. A verificação mede a margem final no fim da rolagem.

## Repasses sociais

- O botão estava fixo em 52dp, com padding Material e texto em negrito, cortando a segunda linha.
- Conforme `content_repasses_sociais.xml:ButtonAposteSociais`, largura 206dp, fonte Caixa regular 16sp, altura ajustada ao conteúdo e padding vertical de 15dp. Texto centralizado, recurso de string e ação original de retorno.

## Verificação

- APK PRD com backend real, Home clássica, logging desativado e sem Chucker.
- Compilação e verificações de DS, arquitetura e formatação: `/tmp/simlo-home-favorites-social-final.log` e `/tmp/simlo-home-favorites-social-recheck.log`.
- Interface: `/tmp/simlo-latest-{ds,betting,tickets,home,social}-ui.log`.
- Os testes utilizam fixtures; nenhuma aposta, pagamento, reserva ou favorito real é criado.

## Pedidos arquiteturais separados

Strings globais e DataStore criptografado/retomada ainda não foram implementados integralmente. Propostas em `docs/superpowers/specs/2026-10-22-app-string-resources-design.md` e `docs/superpowers/specs/2026-10-22-encrypted-datastore-recovery-design.md`.

Compose Multiplatform não foi aplicado, conforme a condição expressa do usuário de não converter sem compatibilidade total. A vaga na FilaBra não pode ser garantida por estado local se o servidor/SDK a expirar ou revogar.
