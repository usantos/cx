package br.gov.caixa.loterias.apostas.state

import br.gov.caixa.loterias.apostas.effect.DadosPessoaisEffect
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioIdDTO
import java.math.BigDecimal

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

    val cepValido: Boolean
        get() = erroCep == null && municipioSelecionado != null

    val limiteValido: Boolean
        get() = !limiteMenorQueMinimo && !limiteMaiorQueMaximo
}
