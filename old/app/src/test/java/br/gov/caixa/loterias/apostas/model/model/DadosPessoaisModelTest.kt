package br.gov.caixa.loterias.apostas.model.model

import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO
import br.gov.caixa.loterias.apostas.model.bo.RequestListener
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorAlteracaoDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTOResponse
import br.gov.caixa.loterias.apostas.novo.features.dadospessoais.DadosPessoaisModel
import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.eq
import org.mockito.ArgumentMatchers.isNull
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify

/**
 * Testes da camada de acesso a dados da tela de dados pessoais.
 *
 * Os BOs reais (DadosUsuarioBO/DadosCorporativosSilceBO) sao mockados
 * estaticamente para validar apenas a responsabilidade do DadosPessoaisModel:
 * repassar a chamada certa, com os parametros certos, e converter o
 * RequestListener de volta para o Callback do Model.
 */
class DadosPessoaisModelTest {

    private val model = DadosPessoaisModel()

    private class CallbackCapturado<T> : DadosPessoaisModel.Callback<T> {
        var sucesso: T? = null
        var chamouSucesso = false
        var erro: VolleyError? = null
        var chamouErro = false

        override fun onSuccess(response: T?) {
            chamouSucesso = true
            sucesso = response
        }

        override fun onError(error: VolleyError?) {
            chamouErro = true
            erro = error
        }
    }

    private fun criarErro(statusCode: Int): VolleyError {
        return VolleyError(NetworkResponse(statusCode, null, false, 0L, null))
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> captorDeListener(): ArgumentCaptor<RequestListener<T>> {
        return ArgumentCaptor.forClass(RequestListener::class.java)
            as ArgumentCaptor<RequestListener<T>>
    }

    @Test
    fun carregarDadosUsuario_devePropagarSucessoDoBO() {
        val bo = mock(DadosUsuarioBO::class.java)
        mockStatic(DadosUsuarioBO::class.java).use { estatico ->
            estatico.`when`<DadosUsuarioBO> { DadosUsuarioBO.getInstance() }.thenReturn(bo)

            val callback = CallbackCapturado<ApostadorDTOResponse>()
            model.carregarDadosUsuario(callback)

            val captor = captorDeListener<ApostadorDTOResponse>()
            verify(bo).getDadosUsuario(captor.capture())

            val resposta = ApostadorDTOResponse()
            captor.value.onResponse(resposta)

            assertTrue(callback.chamouSucesso)
            assertSame(resposta, callback.sucesso)
            assertFalse(callback.chamouErro)
        }
    }

    @Test
    fun carregarDadosUsuario_devePropagarErroDoBO() {
        val bo = mock(DadosUsuarioBO::class.java)
        mockStatic(DadosUsuarioBO::class.java).use { estatico ->
            estatico.`when`<DadosUsuarioBO> { DadosUsuarioBO.getInstance() }.thenReturn(bo)

            val callback = CallbackCapturado<ApostadorDTOResponse>()
            model.carregarDadosUsuario(callback)

            val captor = captorDeListener<ApostadorDTOResponse>()
            verify(bo).getDadosUsuario(captor.capture())

            val erro = criarErro(500)
            captor.value.onErrorResponse(erro)

            assertTrue(callback.chamouErro)
            assertSame(erro, callback.erro)
            assertFalse(callback.chamouSucesso)
        }
    }

    @Test
    fun carregarUfs_deveDelegarParaBoECapturarSucesso() {
        val bo = mock(DadosCorporativosSilceBO::class.java)
        mockStatic(DadosCorporativosSilceBO::class.java).use { estatico ->
            estatico.`when`<DadosCorporativosSilceBO> { DadosCorporativosSilceBO.getInstance() }
                .thenReturn(bo)

            val callback = CallbackCapturado<UnidadeFederacaoDTOResponse>()
            model.carregarUfs(callback)

            val captor = captorDeListener<UnidadeFederacaoDTOResponse>()
            verify(bo).ufs(captor.capture())

            val resposta = UnidadeFederacaoDTOResponse()
            captor.value.onResponse(resposta)

            assertSame(resposta, callback.sucesso)
        }
    }

    @Test
    fun carregarMunicipios_deveRepassarIdUfInformado() {
        val bo = mock(DadosCorporativosSilceBO::class.java)
        mockStatic(DadosCorporativosSilceBO::class.java).use { estatico ->
            estatico.`when`<DadosCorporativosSilceBO> { DadosCorporativosSilceBO.getInstance() }
                .thenReturn(bo)

            val callback = CallbackCapturado<MunicipioDTOResponse>()
            model.carregarMunicipios("35", callback)

            val idUfCaptor = ArgumentCaptor.forClass(String::class.java)
            val listenerCaptor = captorDeListener<MunicipioDTOResponse>()
            verify(bo).municipios(idUfCaptor.capture(), listenerCaptor.capture())

            assertEquals("35", idUfCaptor.value)

            val resposta = MunicipioDTOResponse()
            listenerCaptor.value.onResponse(resposta)
            assertSame(resposta, callback.sucesso)
        }
    }

    @Test
    fun buscarBairroPorCep_deveRepassarApenasOCep() {
        val bo = mock(DadosCorporativosSilceBO::class.java)
        mockStatic(DadosCorporativosSilceBO::class.java).use { estatico ->
            estatico.`when`<DadosCorporativosSilceBO> { DadosCorporativosSilceBO.getInstance() }
                .thenReturn(bo)

            val callback = CallbackCapturado<BairroDTOResponse>()
            model.buscarBairroPorCep("01001000", callback)

            val listenerCaptor = captorDeListener<BairroDTOResponse>()
            verify(bo).bairros(
                isNull(),
                isNull(),
                isNull(),
                eq("01001000"),
                listenerCaptor.capture()
            )

            val resposta = BairroDTOResponse()
            listenerCaptor.value.onResponse(resposta)
            assertSame(resposta, callback.sucesso)
        }
    }

    @Test
    fun recalcularLimite_deveDelegarParaBo() {
        val bo = mock(DadosUsuarioBO::class.java)
        mockStatic(DadosUsuarioBO::class.java).use { estatico ->
            estatico.`when`<DadosUsuarioBO> { DadosUsuarioBO.getInstance() }.thenReturn(bo)

            val callback = CallbackCapturado<ApostadorDTOResponse>()
            model.recalcularLimite(callback)

            val captor = captorDeListener<ApostadorDTOResponse>()
            verify(bo).recalcularLimite(captor.capture())

            val erro = criarErro(400)
            captor.value.onErrorResponse(erro)
            assertSame(erro, callback.erro)
        }
    }

    @Test
    fun atualizarDadosUsuario_deveRepassarApostadorAlteracaoDTO() {
        val bo = mock(DadosUsuarioBO::class.java)
        mockStatic(DadosUsuarioBO::class.java).use { estatico ->
            estatico.`when`<DadosUsuarioBO> { DadosUsuarioBO.getInstance() }.thenReturn(bo)

            val alteracao = ApostadorAlteracaoDTO().apply { cep = "20000000" }
            val callback = CallbackCapturado<CadastrarApostadorDTOResponse>()
            model.atualizarDadosUsuario(alteracao, callback)

            val dtoCaptor = ArgumentCaptor.forClass(ApostadorAlteracaoDTO::class.java)
            val listenerCaptor = captorDeListener<CadastrarApostadorDTOResponse>()
            verify(bo).putDadosUsuario(dtoCaptor.capture(), listenerCaptor.capture())

            assertSame(alteracao, dtoCaptor.value)

            val resposta = CadastrarApostadorDTOResponse()
            listenerCaptor.value.onResponse(resposta)
            assertSame(resposta, callback.sucesso)
            assertNull(callback.erro)
        }
    }
}
