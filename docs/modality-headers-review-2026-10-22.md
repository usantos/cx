# Cabeçalhos de modalidades — 22/10/2026

## Referência e correção da comparação anterior

A simulação atual do CX (`SimulaActivity` e `LotecaActivity`) consome `CabecalhoSimulacaoFragment` e `cabecalho_simula.xml`. A rodada anterior mediu `fragment_barra_titulo_simulacao`, usado por outro fluxo, inclusive confirmação de inclusão. Por isso18sp e as margens daquele componente não eram a referência correta para esta simulação. A implementação e os testes foram corrigidos para o consumidor efetivo.

A instrumentação offline inflou o recurso do APK nativo e executou seu método `preencheDados` sobre views isoladas, sem abrir Activities ou serviços de aposta. Foram extraídas23 combinações de modalidade/tipo, as11 paletas e as artes de `EstiloModalidadeMKP`. O título temporário usado pelo probe de Mega30 foi restaurado ao final. A rede do emulador foi desativada durante o probe e reativada depois.

## Medidas reproduzidas

- Altura mínima48dp; cresce quando o texto precisa de mais altura.
- Voltar48×48dp, seta nativa15×23dp centralizada; título começa após o botão, sem a margem adicional24dp da confirmação.
- Título Futura Std Bold16sp, margem final24dp; sem reticências ou limite fixo de linhas.
- Lotofácil da Independência e Loteca Especial: redução20%, resultando12,8sp em fonte padrão.
- Ajuda48×48dp, margem final4dp, desenho nativo centralizado.
- Concurso/Resultado/Sorteio: topo6dp, rótulo Caixa Std Regular14sp, valor Caixa Std Bold12sp; alinhamento horizontal central e nenhum intervalo vertical adicional. Concurso termina10dp antes da coluna seguinte; Sorteio/Resultado termina8dp antes de Ajuda.
- Heading no título e metadados agrupados para acessibilidade. Fonte ampliada testada a1,6.

Os arquivos de Futura Std Bold, Caixa Std Regular e Caixa Std Bold são idênticos, por SHA256, aos do CX.

## Paletas extraídas do APK

| Modalidade | Fundo | Texto/ícones |
|---|---|---|
| Mega-Sena | #00A651 | #FFFFFF |
| Quina, incluindo alias id1 | #2E3192 | #FFFFFF |
| Lotofácil | #91278F | #FFFFFF |
| Lotomania, ids5/16 | #E67200 | #FFFFFF |
| +Milionária | #2A3580 | #FFFFFF |
| Super Sete | #BED730 | #038141 |
| Loteca | #ED1C24 | #FFFFFF |
| Timemania | #FFF200 | #038141 |
| Dia de Sorte | #E3C021 | #7E6906 |
| Dupla Sena | #A62B43 | #FFFFFF |
| Lotogol | #00A651 | #FFFFFF |

Lotogol usa a paleta padrão verde neste cabeçalho do Legado: `EstiloModalidadeMKP` não tem um case próprio. Isso foi reproduzido somente no cabeçalho da simulação, sem alterar os tokens globais da modalidade.

Outubro Rosa continua condicionado à configuração já existente da campanha Mega-Sena: fundo #F4ADBC e tinta #9C005C. Os especiais conservam a arte nativa, mesmo nessa campanha. Timemania conserva sua paleta independente.

## Concursos especiais

| Caso | Regra conferida/correção |
|---|---|
| Mega da Virada | Título nativo em duas linhas; vetor `cabecalho_mega_da_virada_detalhe_aposta`, que substitui a imagem simples usada antes. |
| Quina de São João | Título em duas linhas e imagem simples; resolve também o identificador1 usado pelos parâmetros de simulação. |
| Lotofácil da Independência | Duas linhas, fonte reduzida e arte nativa. |
| Dupla de Páscoa | Duas linhas e `cabecalho_dupla_pascoa_carrossel`; transparência da imagem preservada sobre fundo branco, como no nativo. |
| Loteca Especial | Título fixo `loteca especial`, fonte reduzida e arte `loteca_copa_simples`; não utiliza um alias arbitrário do backend. |
| Mega-Sena30 anos | Consulta o concurso configurado compartilhado; exige modalidade Mega-Sena e tipo ESPECIAL. Título `mega-sena\n30 anos`, fonte16sp e arte própria. Número normal ou de outra modalidade não troca o título. |
| Loteca dos Pais | O consumidor atual de simulação do próprio CX não aplica o override Pais: mantém Loteca Especial/arte Copa. A configuração foi exercitada e essa regra foi preservada. Outros cabeçalhos do CX, como `BarraTituloSimulacaoFragment`, têm regras diferentes; não foram aplicadas globalmente à simulação. |

A configuração usa o cache compartilhado de parâmetros, como o Legado usa as preferências do Splash. É carregada também quando a aposta já tem parâmetros preparados. Falha na configuração não bloqueia os parâmetros da aposta nem inventa um número de concurso especial.

## Verificação e limites

Compilação PRD e seis gates passaram: arquitetura, DS, migração Compose, formatação, contratos de backend e segurança de rede. Passaram185 testes de apresentação de apostas,2 do novo caso de uso e208 do app, total395. Os10 testes instrumentados cobrem a matriz de13 entradas normais (incluindo aliases),9 especiais/configurações, Outubro Rosa, medidas, fonte ampliada e fluxo manual controlado sem envio.

Evidências: `build/verification/modality-headers-20261022`, com JSON nativo, hashes das fontes, capturas de cada cabeçalho e logs. Os testes reproduziram os desvios antes das correções, inclusive a borda transparente da Dupla de Páscoa.

APK instalado no Samsung físico; Timemania foi conferida com cabeçalho amarelo/verde, concurso2458 e data22/10 após repetir o GET inicial que falhou. Backend real, Home do Legado, sem Chucker, inspetor ou log de rede. Nenhuma aposta, pagamento, cobrança Pix ou reserva real foi criada. Os especiais históricos foram verificados com configuração e serviços controlados; não se afirma sua disponibilidade atual no backend real.
