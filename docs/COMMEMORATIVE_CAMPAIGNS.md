# Campanhas comemorativas

A ativação pertence ao backend. O mês do relógio do aparelho não ativa nenhuma campanha por conta própria.

## Contrato existente, recuperado do legado

Endpoint: `parametros/configuraveis-app`, dentro de `mapa`:

```json
{
  "mapa": {
    "vigenciaMesOutubroRosa": true,
    "vigenciaOutubroRosaMegaSena": false
  }
}
```

- `vigenciaMesOutubroRosa=true` ativa Outubro Rosa na Home e no Drawer.
- A customização das telas da Mega-Sena exige também `vigenciaOutubroRosaMegaSena=true`.
- Flags ausentes/nulas equivalem a falso. Desativar a flag geral também desativa a Mega-Sena.
- Não há condição local `mês == outubro`: o backend informa a vigência, inclusive para testes fora de outubro.

Evidência: `ParametrosConfiguraveisDTO.java:61–64`, `SplashScreenActivity.java:170–171`, `EspecialUtils.java:112–121`, `PrincipalActivity.java:358–370` e `SideMenuExpandleAdapter.java` no checkout legado `/Users/ubiracysantos/Projects/cx/app/src/main/java/br/gov/caixa/loterias/apostas/`.

## Gestão extensível por área

O catálogo tipado reconhece `OUTUBRO_ROSA` e `SETEMBRO_AZUL`. Cada ativação define as áreas `HOME`, `DRAWER` e/ou `MEGA_SENA`, habilitação, prioridade e período opcional. Não foi encontrada uma flag de Setembro Azul no DTO legado consultado.

Para gerir Setembro Azul e futuras campanhas sem multiplicar flags, o cliente aceita a extensão **opcional e nova** `campanhasComemorativas` no mesmo endpoint. Esta extensão precisa ser publicada pelo backend; não é um contrato já confirmado em produção:

```json
{
  "mapa": {
    "campanhasComemorativas": [
      {
        "codigo": "SETEMBRO_AZUL",
        "ativa": true,
        "areas": ["HOME", "DRAWER"],
        "inicio": "2027-09-01",
        "fim": "2027-09-30",
        "prioridade": 10
      },
      {
        "codigo": "OUTUBRO_ROSA",
        "ativa": true,
        "areas": ["HOME", "DRAWER", "MEGA_SENA"],
        "inicio": "2027-10-01",
        "fim": "2027-10-31",
        "prioridade": 10
      }
    ]
  }
}
```

Quando a lista está presente ela substitui integralmente as flags antigas; `[]` desliga todas as campanhas. Quando ausente/nula, as flags legadas continuam funcionando. Datas ISO são inclusivas, avaliadas no fuso `America/Sao_Paulo`; sem datas, a ativação depende apenas de `ativa`. Uma regra com data inválida, período invertido, código desconhecido ou áreas explicitamente vazias/desconhecidas não ativa a campanha. Sem `areas`, o padrão é Home e Drawer. A maior prioridade vence por área; empates mantêm a ordem recebida.

O host atualiza a configuração ao retomar, e o repositório publica seu estado para todos os destinos. A Home também atualiza quando carrega/retoma. Em falha de atualização, mantém a última configuração válida da sessão; na primeira falha, não ativa campanhas. Não há polling nem persistência adicional em disco.

## Aplicação visual

- Domínio: `CommemorativeCampaignSettings` resolve a campanha por área e data.
- Dados: `CommemorativeCampaignMapping` traduz flags/DTOs sem levar cores para as regras de negócio.
- DS: `CaixaDSCampaignTheme`, `resolveCaixaDSLotteryStyle` e os resolvedores de arte centralizam as paletas e os recursos. Um único provider acima da navegação observa a configuração do repositório. Home, Drawer, simulador, seletores, marketplace/detalhes, carrinhos, favoritas, resultados, apostas/compras confirmadas, ajuda e resgate consomem os mesmos papéis, com as exceções de cada tela preservadas. A Home não exibe faixa permanente: o laço da campanha abre o painel informativo sob ação do usuário.
- A status bar mantém o azul CAIXA com ícones claros em todas as rotas, inclusive durante campanhas. O logo oficial do login é preservado no Drawer.
- Não são alteradas as regras de aposta, os destinos de navegação ou as paletas de outras modalidades. A auditoria completa de 64 consumidores e suas exceções está em `OCTOBER_ROSE_LEGACY_SCOPE_20261015.md`. A identidade de Mega-Sena é confirmada antes de aplicar a campanha; um fallback genérico de arte/cor não pode ativá-la em outra modalidade.

Para adicionar uma campanha, inclua sua identidade no catálogo do domínio, sua apresentação no catálogo do DS e o mapeamento entre os dois. O backend publica o código e as áreas; não são enviados nomes de classes, drawables ou cores arbitrárias.

## Fixtures de desenvolvimento

Os cenários de mock `CAMPAIGN_OCTOBER_ROSE` (flags reais do legado, incluindo Mega-Sena) e `CAMPAIGN_SEPTEMBER_BLUE` (extensão opcional) permitem validar o fluxo HTTP → domínio → DS. Exemplo: `./gradlew :app:assemblePrdDebug -PmockBackend=true -PmockScenario=CAMPAIGN_OCTOBER_ROSE`. Esses cenários só têm efeito quando o backend mock está explicitamente habilitado em debug.

## Validação inicial da infraestrutura (histórico)

646 testes JVM nos módulos domínio, dados, rede, DS, Home e app, mais 7 testes Android de Drawer/Home V2 aprovados. Gates DS/Compose e APK `prdDebug` padrão aprovados; build final restaurada para Home V2 e backend real. Capturas com backend mock verificaram Outubro Rosa na Home V2/Drawer e Setembro Azul na Home antiga; login, Home e suporte foram capturados com a status bar azul. Evidências em `build/verification/drawer-refinement-20261015/`. Os três achados da revisão independente sobre atualização da Home antiga e troca/desativação da campanha foram corrigidos e revistos.

## Papéis exatos de Outubro Rosa

A paleta de modalidade segue `EstiloModalidadeMKP`: fundo claro `#F4ADBC`, faixa/fundo escuro `#EE3E7F`, conteúdo sobre fundo claro `#9C005C`, conteúdo sobre fundo escuro branco, texto de lista e marcação de dezenas `#EE3E7F`. Carrinho antigo e lista de apostas confirmadas usam texto bordô `#9C005C`; o carrinho atual usa o título claro `#F4ADBC`. Os trevos sobre fundo claro e escuro usam os vetores originais distintos, e os concursos especiais preservam suas artes específicas.

Status bar e header da Home usam exatamente o mesmo token `Blue700`, sem diferença de degradê. A retirada da faixa não remove a ação do laço nem o painel informativo existente no legado.

## Validação ampliada de 15/10/2026

A auditoria completa de consumidores e a revisão da tela de Filtros estão em [OCTOBER_ROSE_COMPOSE_VALIDATION_20261015.md](OCTOBER_ROSE_COMPOSE_VALIDATION_20261015.md). `modernCheck` e `releaseCheck` passaram novamente, com os três Releases minificados e 1.691 execuções JVM sem falhas. Após a correção final dos CTAs da Home V2, a matriz do host e os três Releases foram revalidados. O relatório discrimina os 140 casos Android aprovados, capturas e limites de homologação; o resultado histórico acima não substitui essa rodada.
