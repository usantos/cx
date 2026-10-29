# Plano de extração da Home

**Goal:** Separar coordenação, componentes visuais e mapeadores da Home sem alterar o comportamento.
**Spec:** `docs/superpowers/specs/2026-10-17-home-extraction-design.md`.
**Execution:** Inline com `superpowers:executing-plans`, na árvore atual.

## Global Constraints

- Usar a baseline local `build/verification/home-refactor-20261017/HomeScreen.before.kt`.
- Preservar API pública, owners, efeitos, estado salvo, layouts/recursos/textos e diferenças clássica/V2.
- Sem alterações de gates, dependências ou outras refatorações.

## Review Focus

- Estado salvo/animações/drawer sobrevivem ao retorno antecipado de recuperação.
- Os eventos são consumidos uma vez e somente no lifecycle originalmente previsto.
- V2 não carrega resultados/preferências enquanto a recuperação está visível.
- Campanha global da Mega-Sena e campanhas das features mantêm suas origens distintas.
- Proteções dos CTAs e prioridade dos avisos permanecem iguais nas duas versões.

## Task 1: Caracterizar e salvar a referência

- [x] Salvar fonte/hash e executar baseline JVM/gates.
- [x] Escrever testes Android de coordenação e capturas determinísticas; executar na fonte anterior à extração em ambas as versões.
- [x] Escrever testes dos mapeadores e observar a falha do novo ponto de extração antes da implementação.

## Task 2: Extrair mantendo os contratos

**Interfaces:** `HomeScreen` pública inalterada; `HomeRoute` interna recebe os mesmos parâmetros. `HomeLegacyContent` recebe `HomeUiState`, resultados, modalidades e estado/callbacks de apresentação.

- [x] Extrair modelo, mapeadores, auxiliares puros e componentes visuais sem alterar os corpos.
- [x] Extrair o conteúdo clássico e seus callbacks, mantendo os estados no coordenador antes da recuperação.
- [x] Delegar a API pública ao coordenador e executar testes JVM/Hilt nos três flavors.

## Task 3: Verificar e revisar

- [x] Reexecutar os testes Android nas duas versões e comparar as imagens com a baseline.
- [x] Executar `modernCheck releaseCheck --offline --continue` no AVD dedicado.
- [x] Obter revisão independente, corrigir achados relevantes e conferir fontes/whitespace.
- [x] Registrar evidências e encerrar somente o AVD iniciado pela tarefa.

## Resultado

Concluído sem mudança de contrato/comportamento identificada. `HomeScreen` passou de1264 para43 linhas; coordenação em `HomeRoute`, conteúdo clássico/componentes e mapeadores em arquivos próprios. Comparação estrutural e revisão independente aprovadas.

`modernCheck releaseCheck --offline --continue --console=plain` passou em19m26s,2155 tarefas. Relatórios:1786 casos JVM e93 Android dos módulos, sem falhas/erros/skips. As execuções adicionais do app passaram22 casos V2/Home/drawer/carrinho e6 clássicos. Dez capturas comparadas: oito exatas e duas com pequenas diferenças de sombreado apenas nas superfícies clicadas, registradas sem máscaras. Os seis APKs normais mantêm backend mock desativado. Evidências em `build/verification/home-refactor-20261017/`.

O AVD dedicado5560 foi limpo e encerrado; o AVD5556 existente permanece ativo. Alterações deixadas sem commit na árvore atual.
