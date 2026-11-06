package br.gov.caixa.loterias.core.presentation.formatting

import br.gov.caixa.loterias.core.presentation.formatting.localizedMessage as messageText
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

val LEGACY_OPERATION_FAILED get() = messageText("nao_foi_possivel_concluir_esta_o_3c7db3")
val LEGACY_REQUEST_FAILED get() = messageText("opa_ocorreu_um_erro_na_sua_requi_3fbf1f")
val LEGACY_INVALID_IDENTIFICATION get() = messageText("o_cpf_vinculado_ao_e_mail_inform_f8be0d")
val LEGACY_LOGIN_REQUIRED get() = messageText("sua_sessao_expirou_e_necessario_a7a802")
val LEGACY_UPDATED_TERMS get() = messageText("o_termo_de_uso_foi_atualizado_e_959939")
val LEGACY_OFFLINE get() = messageText("parece_que_sua_internet_esta_com_15865a")
val LEGACY_CONNECTION_FAILED get() = messageText("falha_na_tentativa_de_conexao_co_3a89ba")
val LEGACY_RESPONSE_TIMEOUT get() = messageText("tempo_de_resposta_esgotou_18ae0a")

// Alternativa sem HTTP do MensagensNetwork (sistema_indisponivel), incluindo ParseError do Volley.
val LEGACY_INVALID_RESPONSE get() = messageText("o_tempo_de_conexao_expirou_806cae")

/** Usa um retrato do horário local fornecido pelo chamador, nunca uma leitura do relógio durante a recomposição. */
fun legacyDiagnosticMessage(message: String, code: String?, now: LocalDateTime): String = buildString {
    append(message).append("\n\n")
    code?.let { append(it).append('\n') }
    append(now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.forLanguageTag("pt-BR"))))
}
