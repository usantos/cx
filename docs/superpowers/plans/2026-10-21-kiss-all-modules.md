# KISS em todos os módulos

> **For agentic workers:** Execução nesta sessão com escopos independentes; nenhuma edição de inputs durante Gradle.

**Goal:** Simplificar complexidade comprovadamente desnecessária no app, core, DS e features, preservando comportamento e cobertura.

**Architecture:** Responsabilidades continuam com seus módulos atuais. Usar fluxo direto e estado com um proprietário; manter sincronização, contratos e variantes visuais necessários.

**Tech Stack:** Kotlin, Compose, Hilt, Gradle/JaCoCo, Android.

**Spec:** Pedido do usuário em21/10/2026: aplicar KISS em todos os módulos.

## Restrições e revisão

Preservar alterações existentes, APIs/valores persistidos, fluxos de erro/cancelamento, leitura atualizada após suspensão, geometria/cores/semântica visual, testes e limiares. Sem novo framework ou factory genérica. Sem commit/push. Manter emulador5560 e AVDs originais.

## Tarefas

- [x] Registrar inventário dos65 módulos e inputs/regras de cobertura antes da edição.
- [x] Retirar os dois espelhos de estado de conclusão de conta; testar reset/cancelamento/erro no ViewModel proprietário e manter a restauração de navegação.
- [x] Simplificar a troca de ambiente do host para uma operação e um dispatcher, preservando atalho persistido, ordem de efeitos e falhas.
- [x] Usar o mesmo MutableStateFlow diretamente nos27 coordenadores; unir copy redundante de Support e tornar explícita a escolha do nome de modalidade.
- [x] Retirar wrappers de callbacks sem efeitos longos; testar recomposição com callbacks novos. Usar Boolean para conflito visual sem valor e unificar apenas os dois ramos Button equivalentes, com testes de variantes/disabled/loading.
- [x] Usar listas locais de projetos de cobertura como fonte única para inputs e dependências; comparar arquivos/fontes/classes/regras/tarefas resolvidos.
- [x] Revisar alterações de forma independente, executar testes e matriz modernCheck/releaseCheck, corrigir falhas sem reduzir cobertura e documentar evidências.
