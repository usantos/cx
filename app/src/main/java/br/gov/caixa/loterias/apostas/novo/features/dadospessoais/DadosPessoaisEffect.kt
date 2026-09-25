package br.gov.caixa.loterias.apostas.effect

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum
import com.android.volley.VolleyError

/**
 * Comandos emitidos pela ViewModel dentro do DadosPessoaisState.
 *
 * Nao usa SingleLiveEvent: cada comando fica em um slot do state e a
 * Activity devolve um evento ...Consumido para limpar o slot, no mesmo
 * padrao de CarrinhoEffect/AnalyticsEffect da SimulaViewModel.
 */
sealed class DadosPessoaisEffect {

    enum class CampoObrigatorio {
        CEP,
        UF,
        MUNICIPIO
    }

    // ------------------------------------------------------------------
    // Slot: comandoRede
    // ------------------------------------------------------------------

    data class ErroDeRede(val error: VolleyError?) : DadosPessoaisEffect()

    data class Redirecionar(val redirect: RedirectEnum) : DadosPessoaisEffect()

    // ------------------------------------------------------------------
    // Slot: comandoDialog
    // ------------------------------------------------------------------

    data class CamposObrigatoriosPendentes(
        val campos: List<CampoObrigatorio>
    ) : DadosPessoaisEffect()

    data class MostrarAvisoNotificacao(val ativado: Boolean) : DadosPessoaisEffect()

    data object ConfirmarSaidaParaCorreios : DadosPessoaisEffect()

    // ------------------------------------------------------------------
    // Slot: comandoNavegacao
    // ------------------------------------------------------------------

    data object CadastroAtualizado : DadosPessoaisEffect()

    data object AbrirSiteCorreios : DadosPessoaisEffect()

    data object FecharTela : DadosPessoaisEffect()

    // ------------------------------------------------------------------
    // Slot: comandoAnalytics
    // ------------------------------------------------------------------

    data class RegistrarEvento(val tipo: Tipo) : DadosPessoaisEffect() {
        enum class Tipo {
            ENTROU_EDITAR_CADASTRO,
            EDICAO_CADASTRO_SUCESSO,
            ENTROU_RECALCULAR_LIMITE
        }
    }
}
