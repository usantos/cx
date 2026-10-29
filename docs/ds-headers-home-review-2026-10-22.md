# DS, cabeçalhos e Home — 22/10/2026

## Escopo confirmado

Componentes básicos e reutilizáveis pertencem ao DS. Blocos específicos, estados, navegação, requisições e regras de negócio permanecem nas features. Não foram transferidos modelos de Domain, ViewModels ou casos de uso para o DS.

## Correções

- Jogos, Dúvidas e Termo de Uso passam a reutilizar o Header Caixa: título16sp, seta12×18dp e os mesmos espaçamentos. Antes, o cabeçalho próprio de suporte usava20sp e seta15×23dp.
- Os três renderizadores de cabeçalho estão no DS: `CaixaDSTopBar` (Caixa), `CaixaDSHomeTopBar` (Home branca com menu/sino/campanha/trevo) e `CaixaDSModalityTopBar` (cores/arte e medidas nativas da modalidade). Home e simulação mantêm adaptadores de dados/callbacks nas features.
- Texto, ícones, botões, checkboxes, radio, cartões, superfícies, separadores, diálogos, menus, scaffold, drawer, bottom sheet, progresso e pull to refresh são consumidos por `components.base`. São28 funções/overloads e174 arquivos consumidores migrados, preservando os defaults do renderer instalado. As importações usam aliases para evitar alterar a composição específica das telas.
- A criação, configuração, cliente de console e descarte da WebView URL foram extraídos para `CaixaDSWebPage`. O componente recebe eventos de console como texto. Validação de URL, parser, broadcasts, atraso e saída da fila continuam em `core/queue`. A dependência direta do renderer WebView foi removida desse módulo. O HTML somente leitura já permanecia no DS.
- Apresentações genéricas de status e avisos nativos também pertencem ao DS. As condições e destinos das ações permanecem nos consumidores.
- A faixa da Home era uma máscara branca de80% aplicada ao rodapé também quando Outubro Rosa abria. A máscara agora pertence somente ao acesso rápido e não é desenhada sobre a campanha, inclusive em transições. O deslocamento medido da campanha, o carrossel e as regras do carrinho foram preservados.

Os três desenhos XML copiados para o DS são vetores já auditados, idênticos por SHA256 aos recursos existentes. Foram registrados individualmente no inventário, sem liberar layouts XML ou relaxar o gate.

## Verificação

Compilação PRD, testes e seis gates passaram: arquitetura, DS, migração Compose, formatação, contratos do backend e segurança de rede. A cobertura exigida do parser da fila também passou.

- 516 testes unitários: DS27, fila4, Home75, suporte17, apostas185 e app208.
- 81 testes instrumentados: controles/WebViews do DS4, cabeçalhos/apostas10, pagamento16, carrinho12, Home12, leitor4, suporte6, Bolões15 e Repasses2.
- Comparação de imagem entre o cabeçalho de suporte e o padrão Caixa; entre controles antes/depois da centralização; campanha sem máscara; cores/artes/medidas dos cabeçalhos e fontes ampliadas.
- WebViews testadas com HTML local: JavaScript desativado por padrão, ativação explícita, encaminhamento ao callback atualizado e remoção do renderer.
- `scripts/test-ds-ownership.py` rejeitou quatro consumidores inválidos (Material importado, chamada Material qualificada, WebView nativa e AndroidView), aceitou o consumidor DS e restaurou todos os fixtures. O baseline final passou.

`verifyCaixaDS` rejeita controles Material3 diretos, WebViews/clientes e widgets nativos fora do DS, inclusive formas qualificadas verificadas. Tipos de estado e anotações técnicas de UI permanecem permitidos. Primitivos de layout/desenho podem compor os blocos específicos das features.

Evidências e mapas de migração: `build/verification/ds-basic-components-20261022`.

## Celular e limites

APK instalado no Samsung físico com backend real, Home do Legado e Chucker/inspetor/log de rede desativados. O primeiro acesso mostrou aviso e a tela de indisponibilidade LTR-503; uma nova tentativa voltou a exibir o aviso. A conferência visual da campanha no aparelho depende de o carregamento real voltar a funcionar. Os cenários visuais acima passaram em ambiente instrumentado controlado.

Nenhuma aposta, pagamento, cobrança Pix, reserva, exclusão de cartão ou alteração de favorita real foi realizada.
