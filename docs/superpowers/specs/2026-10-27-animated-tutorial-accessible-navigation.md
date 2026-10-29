# Navegação acessível dos tutoriais animados

O usuário confirmou a leitura das instruções de Bolões no Samsung, mas relatou a ausência de controles para avançar e voltar na fonte de sistema em 200%. As etapas intermediárias usam somente o gesto do pager.

Com fonte ampliada, os tutoriais de Bolões e +Milionária terão um rodapé de altura natural com Anterior e Próximo, separado do indicador e da área da animação. O espaço do rodapé será reservado na medição do conteúdo. Os controles chamam o callback de seleção de página já usado pelo pager e pelo ViewModel/Redux. Anterior fica desabilitado na primeira etapa e Próximo na última; a conclusão continua exclusivamente na ação já existente. Nenhuma regra de domínio é duplicada.

A escala padrão conserva o layout legado, as animações, cores e asserções geométricas existentes. Headers e baselines permanecem intactos. O checkbox final continua com nome/estado em um alvo único e as instruções têm um bloco somente na etapa ativa.

Duas regressões cobrem os dois tutoriais em viewport de 360 × 640 dp e fonte 200%: glifos completos, controles alcançáveis, sequência de callbacks de avanço/retorno, limites desabilitados e separação da ação final. Reteste humano no mesmo Samsung continua obrigatório; acesso local controlado não aprova o fluxo remoto de compra de bolões.
