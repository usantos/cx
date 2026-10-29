# Home clássica — estados restantes

Referência: fontes locais do Legado e release `dca5d9d9bcce3e888aee75d5e2b59d5f1d7ac80b` (`release/1.24.0-BFF-ROSA`). O checkout antigo não contém Outubro Rosa; a comparação da campanha usa os blobs dessa release, sem alterar o checkout.

## Ajustes desta etapa

- Resultados: consulta com carregamento bloqueante; card expande após sucesso; erro mantém fechado; cache separado por modalidade ao fechar/reabrir. Atualizar os parâmetros reinicia a apresentação/cache.
- Resultados das onze modalidades: medidas/tipografia, grades, colunas Super Sete, trevos, segundo sorteio da Dupla Sena, quantidade de ganhadores, premiações, arrecadação, regiões, link do portal e paginação de jogos da Loteca. Metadados de seleção/país dos times preservados do backend.
- Cabeçalhos normais: nomes do enum original, fonte Futura com autosizing8..17sp. Instantânea/Combo preservam o contêiner vazio32dp acima do banner.
- Paletas: prêmios/calendário/relógio escuros em Timemania, Dia de Sorte e Super Sete; controles brancos sobre o fundo escuro em Dia de Sorte/Super Sete. Lotogol herda a paleta base Mega-Sena conforme EstiloModalidadeMKP.
- Concursos especiais: imagens originais e fonte Futura; Mega da Virada, Quina de São João, Dupla de Páscoa, Lotofácil da Independência e Loteca. Mega 30 anos e Loteca dos Pais usam os números configurados reais `concursoMega30Anos`/`concursoLotecaPais`, modalidade e tipo ESPECIAL.
- Outubro Rosa: imagens originais, margem inicial22dp/final34dp, título a42dp, parágrafos com spans de16dp como a origem, negritos/rosa, alinhamento à direita, cor padrão medida no tema original `#8A000000`.
- Painéis: mesma translação calculada pelo rodapé; quatro atalhos na release ROSA; abertura1300ms/fechamento2500ms dos atalhos; campanha700ms/1000ms; rotação da fita; botões ocultos durante a animação; rodapé fixo com camada branca de alpha0,8. Alternar os painéis aguarda o fechamento anterior.
- Concurso sem estimativa: uma única composição de texto multilinha com negrito, número com tamanho próprio e legenda invisível preservando espaço. Texto do prazo corrigido para “Apostas se encerram em”.
- Carrinho, pagamento e notificações: valores/count/limites e ações; pop-up com margens horizontais50dp.
- Revenda de bolões: contador extra após o encerramento individual, número/tipo/modalidade correspondentes, azul acima de10min, amarelo abaixo de10min e vermelho abaixo de5min, formato com horas quando necessário. Expirar o prazo consulta novamente os parâmetros. Concursos exclusivos de revenda criam/substituem os cards temporários na mesma ordem da origem; a Home admite parâmetros exclusivamente de bolões. O clique em “Aposte” mantém o aviso de encerramento individual e orienta a compra de bolão sem trocar o concurso.

## Matriz exercitada

| Área | Estados |
| --- | --- |
| Resultados | onze modalidades; carregando; falha; vazio; resposta de outra modalidade; reabrir cache; zero/um/vários ganhadores; sem faixas; limite30regiões; link portal; segundo sorteio; mês ausente; navegação Loteca/reset por concurso; Mega rosa |
| Cards | normal; ESPECIAL por modalidade; especiais configurados correspondentes/não correspondentes/ausentes; sem estimativa; encerrado; Instantânea; Combo |
| Painéis | fechado; aberto; abrindo; fechando; alternância sequencial; bloqueio de toque durante animação |
| Carrinho/avisos | total zero/positivo; contagem até999/excedente; compra de pagamento pendente; aviso/modal de notificação; link/fechamento |
| Revenda | antes do fechamento individual; prazo ativo; vencido; data inválida; limites de cor; expiração/reconsulta |

## Evidências e limites

Capturas e relatórios locais em `build/verification/home-remaining-20261021/`. Os resultados renderizados têm37capturas específicas. A sonda opcional `HomeNativeOracleUiTest` carrega o APK original em DexClassLoader isolado; não instala o aplicativo antigo nem executa seu Application.onCreate. O APK de referência não inclui a campanha ROSA, portanto as imagens/medidas da campanha são conferidas com a release git indicada. O APK da sonda fica apenas em assets de teste opcionais por propriedade Gradle; nunca entra no aplicativo de produção.

A montagem fora da Activity original exige resolver alinhamento e inicializar o provedor Joda. Essas capturas não constituem uma certificação automática de equivalência de todos os pixels. Testes de texto/dimensões/interação e comparação dos fontes complementam a inspeção visual. Conteúdo real variável do servidor não é uma referência congelada.

A lista de ganhadores do segundo sorteio da Dupla Sena mantém os dados corretos do segundo sorteio; não replica o defeito do Legado que reapresentava a lista do primeiro.

## Execução

- 433 testes unitários: Home75, dados de apostas29, dados de resultados7, core/data10, core/domain188 e core/network124; zero falhas/ignorados.
- 60 testes de interface da Home no emulador e1 teste de campanha no Samsung; zero falhas/ignorados. Após os últimos ajustes,30 testes de geometria/animação/paleta foram novamente executados.20 deles confrontam o ViewHolder original, incluindo Instantânea e Combo; as cores do cabeçalho/prêmio são assertivas independentes contra o APK de referência.
- O APK PRD compila com `ENABLE_MOCK_BACKEND=false`, `HOME_V2=false`, `ENABLE_NETWORK_INSPECTOR=false` e `ENABLE_NETWORK_LOGGING=false`. O APK original opcional não está empacotado no aplicativo de produção.
- Versão final instalada no emulador. O Samsung foi desconectado depois dos primeiros58 testes; a atualização final nele depende de reconexão USB.
- Logs preservados em `build/verification/home-remaining-20261021/logs/`, hash do APK em `apk.sha256` e resumo unitário em `unit-results.json`.

Verificação real no emulador: Home aberta após o tutorial, menu aberto automaticamente; depois de fechar o menu, Mega-Sena3073 com estimativaR$200.000.000,00, sorteio22/10/2026 às21h e botões de aposta/bolão. Captura/XML em `final-home.png`/`final-home.xml`. O modo visual de navegação não ativa sessão sintética quando mockBackend=false.
