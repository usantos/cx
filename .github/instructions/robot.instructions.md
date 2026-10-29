---
applyTo: "automation/robot-certificacao/**"
---
# Automação Robot independente

Siga também [as instruções portáteis](../../automation/robot-certificacao/.github/copilot-instructions.md) e o [README](../../automation/robot-certificacao/README.md) do pacote. Todas as regras necessárias devem permanecer nele quando for copiado para outro repositório.

- Testes em `Testes/SUTA_<funcionalidade>.robot`, casos `CTA_<numero>_<nome>`, Gherkin pt-BR. Pages encapsulam interação/seletores, Funcionalidades expressam negócio e Testes expressam cenários/resultados.
- Use apenas IDs publicados no TestData; sem XPath posicional, coordenadas, sleeps fixos ou fallback silencioso por texto. Preserve acessibilidade e contrato Android.
- Não limpar dados, reinstalar APK ou encerrar sessão autenticada automaticamente para forçar pré-condições. Configure ambiente/UDID explicitamente e use dispositivo autorizado.
- Não versionar environments.json, segredos, massas pessoais, APKs, screenshots reais ou results. Autenticação Keeggo e RQM exige contrato confirmado, sem endpoints/capabilities inventados.
- Execute `python Scripts/validar_entrega.py` a partir do pacote, com dependências do lock. Na raiz Android, `python3 scripts/check-automation-ids.py` confere a integração. Dry-run não é execução no dispositivo.
