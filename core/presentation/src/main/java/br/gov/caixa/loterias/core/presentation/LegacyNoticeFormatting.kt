package br.gov.caixa.loterias.core.presentation

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

const val LEGACY_OPERATION_FAILED = "Não foi possível concluir esta operação. Por favor, tente novamente."
const val LEGACY_REQUEST_FAILED = "Opa, ocorreu um erro na sua requisição. Por favor, tente novamente dentro de alguns instantes."
const val LEGACY_INVALID_IDENTIFICATION = "O CPF vinculado ao e-mail informado deve ser o mesmo CPF cadastrado nas Loterias CAIXA."
const val LEGACY_LOGIN_REQUIRED = "Sua sessão expirou. É necessário realizar o login novamente para continuar utilizando o aplicativo Loterias CAIXA."
const val LEGACY_UPDATED_TERMS = "O termo de Uso foi Atualizado. É necessário aceitar novamente."
const val LEGACY_OFFLINE = "Parece que sua internet está com alguma instabilidade. Verifique sua conexão e tente novamente."
const val LEGACY_CONNECTION_FAILED = "Falha na tentativa de conexão com o servidor"
const val LEGACY_RESPONSE_TIMEOUT = "Tempo de resposta esgotou"
// MensagensNetwork's no-HTTP fallback (sistema_indisponivel), including Volley ParseError.
const val LEGACY_INVALID_RESPONSE = "O Tempo de conexão expirou."

/** Use a snapshot of local time supplied by the caller, never time sampled at recomposition. */
fun legacyDiagnosticMessage(message: String, code: String?, now: LocalDateTime): String = buildString {
    append(message).append("\n\n")
    code?.let { append(it).append('\n') }
    append(now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.forLanguageTag("pt-BR"))))
}
