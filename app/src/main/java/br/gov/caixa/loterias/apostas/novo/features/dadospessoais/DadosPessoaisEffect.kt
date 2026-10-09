package br.gov.caixa.loterias.apostas.novo.features.dadospessoais

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum
import com.android.volley.VolleyError

sealed class DadosPessoaisEffect {

    enum class CampoObrigatorio {
        CEP,
        UF,
        MUNICIPIO
    }

    data class ErroDeRede(val error: VolleyError?) : DadosPessoaisEffect()

    data class Redirecionar(val redirect: RedirectEnum) : DadosPessoaisEffect()

    data class CamposObrigatoriosPendentes(
        val campos: List<CampoObrigatorio>
    ) : DadosPessoaisEffect()

    data class MostrarAvisoNotificacao(val ativado: Boolean) : DadosPessoaisEffect()

    data object ConfirmarSaidaParaCorreios : DadosPessoaisEffect()

    data object CadastroAtualizado : DadosPessoaisEffect()

    data object AbrirSiteCorreios : DadosPessoaisEffect()

    data object FecharTela : DadosPessoaisEffect()

    data object FalhaAoCarregarDados: DadosPessoaisEffect()

    data class RegistrarEvento(val tipo: Tipo) : DadosPessoaisEffect() {
        enum class Tipo {
            ENTROU_EDITAR_CADASTRO,
            EDICAO_CADASTRO_SUCESSO,
            ENTROU_RECALCULAR_LIMITE
        }
    }
}
