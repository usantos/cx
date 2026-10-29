---
applyTo: "core/auth/**,core/security/**,core/key-management/**,core/network/**,core/persistence/**,core/queue/**,app/**/environment/**,scripts/*remote-config*,scripts/*pins*,scripts/*sign*,docs/SECURE_PIN_ROTATION.md"
---
# Autenticação, rede, persistência e configuração remota

Consulte os contratos existentes e [SECURE_PIN_ROTATION](../../docs/SECURE_PIN_ROTATION.md). Preserve headers HTTP, política TLS, assinaturas, quórum e baselines.

- Não desabilitar hostname verification, pinning, validação de assinatura ou criptografia para contornar 503/erro de instalação. Diferencie resposta HTTP do backend de falha TLS, conectividade ou sessão.
- Não versionar segredos/chaves privadas/credenciais; exemplos usam placeholders. Pin público não é chave privada. Mantenha identidades dos signatários e política de rotação existentes.
- Remote Config: preserve listener em tempo real, recuperação de 30 segundos apenas no primeiro plano e validação/ativação existentes. Calendário de ícones é anual, respeita timezone e usa padrão fora das datas; mudança com app fechado tem agendamento próprio. Não publicar no Firebase como efeito de teste local.
- Sessões devem preservar isolamento por ambiente/usuário e rejeitar resultados tardios. Não equiparar nome/token local a sessão autorizada; saída de visitante não depende de logout remoto autenticado.
- Persistência deve conservar integridade, isolamento e recuperação; não apagar conteúdo para ocultar erro. Preserve cancelamento e intenção durável de operações financeiras incertas, sem reenvio automático.
- Logs/cURL devem permanecer sanitizados; debug não autoriza exposição de CPF, token, senha ou resposta pessoal. Release mantém logs de rede desabilitados.
