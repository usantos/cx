package br.gov.caixa.loterias.apostas.novo.features.dadospessoais

import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO
import br.gov.caixa.loterias.apostas.model.bo.RequestListener
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorAlteracaoDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTOResponse
import com.android.volley.VolleyError

open class DadosPessoaisModel {

    interface Callback<T> {
        fun onSuccess(response: T?)
        fun onError(error: VolleyError?)
    }

    open fun carregarDadosUsuario(callback: Callback<ApostadorDTOResponse>) {
        DadosUsuarioBO.getInstance().getDadosUsuario(criarListener(callback))
    }

    open fun carregarUfs(callback: Callback<UnidadeFederacaoDTOResponse>) {
        DadosCorporativosSilceBO.getInstance().ufs(criarListener(callback))
    }

    open fun carregarMunicipios(
        idUf: String,
        callback: Callback<MunicipioDTOResponse>
    ) {
        DadosCorporativosSilceBO.getInstance().municipios(
            idUf,
            criarListener(callback)
        )
    }

    open fun buscarBairroPorCep(
        cep: String,
        callback: Callback<BairroDTOResponse>
    ) {
        DadosCorporativosSilceBO.getInstance().bairros(
            null,
            null,
            null,
            cep,
            criarListener(callback)
        )
    }

    open fun recalcularLimite(callback: Callback<ApostadorDTOResponse>) {
        DadosUsuarioBO.getInstance().recalcularLimite(criarListener(callback))
    }

    open fun atualizarDadosUsuario(
        apostadorAlteracaoDTO: ApostadorAlteracaoDTO,
        callback: Callback<CadastrarApostadorDTOResponse>
    ) {
        DadosUsuarioBO.getInstance().putDadosUsuario(
            apostadorAlteracaoDTO,
            criarListener(callback)
        )
    }

    private fun <T> criarListener(callback: Callback<T>): RequestListener<T> {
        return object : RequestListener<T>() {
            override fun onResponse(response: T?) {
                callback.onSuccess(response)
            }

            override fun onErrorResponse(error: VolleyError?) {
                callback.onError(error)
            }
        }
    }
}
