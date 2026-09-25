package br.gov.caixa.loterias.apostas.state

import br.gov.caixa.loterias.apostas.effect.DadosPessoaisEffect
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioIdDTO
import java.math.BigDecimal

/**
 * Estado da tela de dados pessoais.
 *
 * Alem dos dados da tela, carrega os slots de comando (rede, dialog,
 * navegacao e analytics), que substituem o SingleLiveEvent. Cada slot e
 * limpo pela ViewModel quando a Activity envia o evento ...Consumido.
 *
 * Imutavel: a ViewModel sempre emite uma nova instancia via copy().
 */
data class DadosPessoaisState(
    val apostador: ApostadorDTO? = null,
    val municipioSelecionado: MunicipioIdDTO? = null,
    val uf: String? = null,
    val municipio: String? = null,
    val cep: String? = null,
    val erroCep: String? = null,
    val limiteDigitado: BigDecimal? = null,
    val posicaoSexo: Int = 0,
    val aceitaNoticias: Boolean = false,
    val carregando: Boolean = false,
    val dadosCarregados: Boolean = false,
    val limiteMenorQueMinimo: Boolean = false,
    val limiteMaiorQueMaximo: Boolean = false,
    val comandoRede: DadosPessoaisEffect? = null,
    val comandoDialog: DadosPessoaisEffect? = null,
    val comandoNavegacao: DadosPessoaisEffect? = null,
    val comandoAnalytics: DadosPessoaisEffect? = null
) {

    /**
     * Derivado: o CEP so e valido quando a consulta nao retornou erro
     * e resolveu um municipio.
     */
    val cepValido: Boolean
        get() = erroCep == null && municipioSelecionado != null

    val limiteValido: Boolean
        get() = !limiteMenorQueMinimo && !limiteMaiorQueMaximo
}
