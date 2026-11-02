# Sample por tipos — plano de implementação

Objetivo: organizar o catálogo por finalidade e permitir explorar a prévia real,
consultar parâmetros e copiar exemplo de uso/assinatura. Escopo de cópia confirmado
pelo usuário em 02/11/2026; código-fonte interno do DS não será distribuído.

A estrutura atual agrupa por pacote, com grupos genéricos. Será mantida a lista
com busca, adicionando filtros sem criar outra pilha de navegação. A alternativa
de telas de tipos separadas aumenta navegação sem melhorar o acesso ao exemplo.
Tipos são metadados explícitos de cada entrada; IDs, demos e recursos das APIs
permanecem os mesmos. Estado local de busca/filtro/seleção é salvo no host.

- [x] RED: teste de navegação por tipo e cópia exata para clipboard.
- [x] Classificação completa, ordenação e filtros combinados com busca.
- [x] Separar host, lista, filtros e detalhe/código em arquivos coesos.
- [x] Copiar uso e assinatura com ação DS, ID estável e confirmação acessível.
- [x] Testes, qualidade, arquitetura/IDs, APK do Sample e documentação.

Restrições: nenhum header protegido ou baseline modificado; sem dependências
novas, autenticação, serviços, staging, commit, push ou instalação adicional.
Validar também cobertura global pelo Gradle, preservando mínimo estrito >95%.

## Resultado

Sample: 14 testes aprovados. Global: 3.628 testes sem falhas, erros ou ignorados;
JaCoCo LINE 95,38% (60.249/63.168). Qualidade, arquitetura/IDs e APK aprovados.
A revisão independente confirmou a correção do seletor CardPicker e não deixou
achados pendentes. Headers e 40 baselines preservados; fontes fora do Sample
sem mudanças. APK instalado somente no emulador para inspeção visual.

Precommit completo não executado porque há fontes novos fora do staging.
Nenhum arquivo preparado, commit ou push. Ferramentas crapper/dryer/mutator
indisponíveis; não são alegadas métricas dessas ferramentas.
Evidências e registro de ações fora do repositório:
`/Users/ubiracysantos/simlo-artifacts/2026-11-02/sample-catalog/`.
