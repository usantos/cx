---
applyTo: "docs/**,**/*.md,.github/instructions/**,scripts/documentation/**"
---
# Documentação e instruções do Copilot

- Escreva em português e confira a implementação atual. Preserve nomes de símbolos, endpoints e contratos externos. Guias técnicos continuam sendo documentação; instruções automáticas ficam em `.github/copilot-instructions.md` e `.github/instructions/*.instructions.md` com frontmatter `applyTo`.
- Mantenha regras essenciais no arquivo geral, pois clientes Copilot possuem suporte diferente a instruções por caminho. Não depender exclusivamente de AGENTS.md ou de configuração local do editor.
- Ao alterar acesso a dados, atualize `docs/DATA_ACCESS_BY_FEATURE.md`: responsabilidade, classes de entrada, serviço, método/caminho, parâmetros e origem, DTO/retorno, erros e limites. Confira o código, sem inventar contrato do servidor.
- Para inventário de APIs, use `scripts/documentation/inventory_api.py`; não editar saídas para esconder divergência com fontes.
- O guia PDF existente é gerado por `scripts/documentation/build_technical_guide.py`. Preserve capítulos, identidade e fontes em `docs/technical-guide/`; não substituir por outro PDF avulso. Quando a tarefa pedir PDF, gere e inspecione o resultado com a ferramenta disponível.
- Fontes, imagens necessárias e manifests ficam no Git; PDFs/APKs/ZIPs exportados e relatórios ficam nos diretórios ignorados. Não apagar imagens de documentação por parecerem cache.
- Evidências datadas devem indicar escopo, variante, comandos e limitações. Não converter dry-run em teste físico aprovado ou resultado histórico em execução atual.
- Ao modificar regras Copilot, mantenha equivalência com o pacote portátil Robot. Verifique links relativos e padrões applyTo antes de entregar.
