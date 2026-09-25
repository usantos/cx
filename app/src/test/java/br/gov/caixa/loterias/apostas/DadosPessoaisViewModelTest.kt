package br.gov.caixa.loterias.apostas

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import br.gov.caixa.loterias.apostas.effect.DadosPessoaisEffect
import br.gov.caixa.loterias.apostas.event.DadosPessoaisEvent
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorAlteracaoDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioIdDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTOResponse
import br.gov.caixa.loterias.apostas.model.model.DadosPessoaisModel
import br.gov.caixa.loterias.apostas.viewModel.DadosPessoaisViewModel
import com.android.volley.NetworkResponse
import com.android.volley.VolleyError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class DadosPessoaisViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeModel: FakeDadosPessoaisModel
    private lateinit var viewModel: DadosPessoaisViewModel

    @Before
    fun setUp() {
        fakeModel = FakeDadosPessoaisModel()
        viewModel = DadosPessoaisViewModel(fakeModel)
    }

    private fun criarErro(statusCode: Int): VolleyError {
        return VolleyError(NetworkResponse(statusCode, null, false, 0L, null))
    }

    private fun apostadorPadrao(
        cep: String? = "01001000",
        idUf: Long? = 1L,
        numeroMunicipio: Long? = 100L,
        limiteMinimo: BigDecimal = BigDecimal("10.00"),
        limiteDiario: BigDecimal = BigDecimal("50.00"),
        limiteMaximo: BigDecimal = BigDecimal("100.00"),
        sexo: ApostadorDTO.SexoEnum = ApostadorDTO.SexoEnum.M,
        aceitaNoticias: Boolean = false
    ): ApostadorDTO = ApostadorDTO().apply {
        setCpf("12345678900")
        setNome("Nome Teste")
        setEmail("teste@teste.com")
        setCep(cep)
        setMunicipioId(
            MunicipioIdDTO().apply {
                setIdUF(idUf)
                setNumero(numeroMunicipio)
            }
        )
        setLimiteMinimoDiario(limiteMinimo)
        setLimiteDiario(limiteDiario)
        setLimiteDiarioAutorizado(limiteMaximo)
        setSexo(sexo)
        setAceitaReceberNoticias(aceitaNoticias)
    }

    private fun carregarApostadorSemLocalizacao(apostador: ApostadorDTO = apostadorPadrao(idUf = null)) {
        fakeModel.dadosUsuarioResposta = ApostadorDTOResponse().apply { payload = apostador }
        viewModel.onEvent(DadosPessoaisEvent.TelaIniciada)
    }

    @Test
    fun telaIniciada_comSucessoCompleto_devePopularEstadoEEncerrarCarregamento() {
        val apostador = apostadorPadrao()
        fakeModel.dadosUsuarioResposta = ApostadorDTOResponse().apply { payload = apostador }
        fakeModel.ufsResposta = UnidadeFederacaoDTOResponse().apply {
            payload = mutableListOf(
                UnidadeFederacaoDTO().apply {
                    id = 1L
                    sigla = "SP"
                }
            )
        }
        fakeModel.municipiosResposta = MunicipioDTOResponse().apply {
            payload = mutableListOf(
                MunicipioDTO().apply {
                    id = MunicipioIdDTO().apply {
                        idUF = 1L
                        numero = 100L
                    }
                    nome = "São Paulo"
                }
            )
        }

        viewModel.onEvent(DadosPessoaisEvent.TelaIniciada)

        val state = viewModel.uiState.value!!
        assertSame(apostador, state.apostador)
        assertEquals("SP", state.uf)
        assertEquals("São Paulo", state.municipio)
        assertFalse(state.carregando)
        assertTrue(state.dadosCarregados)
        assertEquals(
            DadosPessoaisEffect.RegistrarEvento.Tipo.ENTROU_EDITAR_CADASTRO,
            (state.comandoAnalytics as DadosPessoaisEffect.RegistrarEvento).tipo
        )
    }

    @Test
    fun telaIniciada_semApostador_deveFinalizarComFalhaInterna() {
        fakeModel.dadosUsuarioResposta = ApostadorDTOResponse()

        viewModel.onEvent(DadosPessoaisEvent.TelaIniciada)

        val state = viewModel.uiState.value!!
        assertFalse(state.carregando)
        assertFalse(state.dadosCarregados)
    }

    @Test
    fun telaIniciada_comErroDeRede_deveExporErroDeRede() {
        fakeModel.dadosUsuarioErro = criarErro(500)

        viewModel.onEvent(DadosPessoaisEvent.TelaIniciada)

        val state = viewModel.uiState.value!!
        assertFalse(state.carregando)
        assertTrue(state.comandoRede is DadosPessoaisEffect.ErroDeRede)
    }

    @Test
    fun telaIniciada_semUfValida_naoDeveBuscarLocalizacao() {
        val apostador = apostadorPadrao(idUf = null)
        fakeModel.dadosUsuarioResposta = ApostadorDTOResponse().apply { payload = apostador }

        viewModel.onEvent(DadosPessoaisEvent.TelaIniciada)

        val state = viewModel.uiState.value!!
        assertFalse(state.carregando)
        assertTrue(state.dadosCarregados)
        assertFalse(fakeModel.ufsChamado)
    }

    @Test
    fun cepAlterado_comMenosDeOitoDigitos_naoDisparaBusca() {
        viewModel.onEvent(DadosPessoaisEvent.CepAlterado("1234"))

        assertNull(fakeModel.cepRecebido)
        assertNull(viewModel.uiState.value!!.cep)
    }

    @Test
    fun cepAlterado_comOitoDigitos_deveAtualizarLocalizacao() {
        fakeModel.bairroResposta = BairroDTOResponse().apply {
            payload = mutableListOf(
                BairroDTO().apply {
                    municipio = MunicipioDTO().apply {
                        id = MunicipioIdDTO().apply {
                            idUF = 33L
                            numero = 200L
                        }
                        nome = "Rio de Janeiro"
                        uf = UnidadeFederacaoDTO().apply { sigla = "RJ" }
                    }
                }
            )
        }

        viewModel.onEvent(DadosPessoaisEvent.CepAlterado("20000000"))

        assertEquals("20000000", fakeModel.cepRecebido)
        val state = viewModel.uiState.value!!
        assertEquals("RJ", state.uf)
        assertEquals("Rio de Janeiro", state.municipio)
        assertNull(state.erroCep)
        assertFalse(state.carregando)
        assertTrue(state.cepValido)
    }

    @Test
    fun cepAlterado_semMunicipioNaResposta_deveDefinirCepInvalido() {
        fakeModel.bairroResposta = BairroDTOResponse().apply { payload = mutableListOf() }

        viewModel.onEvent(DadosPessoaisEvent.CepAlterado("99999999"))

        val state = viewModel.uiState.value!!
        assertEquals("CEP_INVALIDO", state.erroCep)
        assertEquals("", state.uf)
        assertEquals("", state.municipio)
        assertNull(state.municipioSelecionado)
    }

    @Test
    fun cepAlterado_comErroNegocial_deveDefinirCepInvalido() {
        fakeModel.bairroErro = criarErro(400)

        viewModel.onEvent(DadosPessoaisEvent.CepAlterado("99999999"))

        assertEquals("CEP_INVALIDO", viewModel.uiState.value!!.erroCep)
    }

    @Test
    fun cepAlterado_comErroDeRede_deveExporErroDeRede() {
        fakeModel.bairroErro = criarErro(500)

        viewModel.onEvent(DadosPessoaisEvent.CepAlterado("99999999"))

        assertTrue(viewModel.uiState.value!!.comandoRede is DadosPessoaisEffect.ErroDeRede)
    }

    @Test
    fun limiteAlterado_dentroDaFaixa_deveSerValido() {
        carregarApostadorSemLocalizacao()

        viewModel.onEvent(DadosPessoaisEvent.LimiteAlterado("R$ 80,00"))

        val state = viewModel.uiState.value!!
        assertEquals(0, BigDecimal("80.00").compareTo(state.limiteDigitado))
        assertTrue(state.limiteValido)
    }

    @Test
    fun limiteAlterado_abaixoDoMinimo_deveMarcarInvalido() {
        carregarApostadorSemLocalizacao()

        viewModel.onEvent(DadosPessoaisEvent.LimiteAlterado("R$ 5,00"))

        val state = viewModel.uiState.value!!
        assertTrue(state.limiteMenorQueMinimo)
        assertFalse(state.limiteValido)
    }

    @Test
    fun limiteAlterado_acimaDoMaximo_deveMarcarInvalido() {
        carregarApostadorSemLocalizacao()

        viewModel.onEvent(DadosPessoaisEvent.LimiteAlterado("R$ 150,00"))

        val state = viewModel.uiState.value!!
        assertTrue(state.limiteMaiorQueMaximo)
        assertFalse(state.limiteValido)
    }

    @Test
    fun limiteAlterado_semApostadorCarregado_naoDeveAlterarEstado() {
        viewModel.onEvent(DadosPessoaisEvent.LimiteAlterado("R$ 80,00"))

        assertNull(viewModel.uiState.value!!.limiteDigitado)
    }

    @Test
    fun sexoSelecionado_deveAtualizarPosicao() {
        viewModel.onEvent(DadosPessoaisEvent.SexoSelecionado(1))

        assertEquals(1, viewModel.uiState.value!!.posicaoSexo)
    }

    @Test
    fun notificacaoAlterada_antesDeCarregarDados_naoDeveMostrarDialogo() {
        viewModel.onEvent(DadosPessoaisEvent.NotificacaoAlterada(true))

        val state = viewModel.uiState.value!!
        assertTrue(state.aceitaNoticias)
        assertNull(state.comandoDialog)
    }

    @Test
    fun notificacaoAlterada_aposCarregarDados_deveMostrarDialogo() {
        carregarApostadorSemLocalizacao()

        viewModel.onEvent(DadosPessoaisEvent.NotificacaoAlterada(true))

        val state = viewModel.uiState.value!!
        assertTrue(state.comandoDialog is DadosPessoaisEffect.MostrarAvisoNotificacao)
    }

    @Test
    fun naoSeiCepClicado_deveAbrirDialogoDeConfirmacao() {
        viewModel.onEvent(DadosPessoaisEvent.NaoSeiCepClicado)

        assertTrue(
            viewModel.uiState.value!!.comandoDialog is DadosPessoaisEffect.ConfirmarSaidaParaCorreios
        )
    }

    @Test
    fun redirecionamentoCorreiosConfirmado_deveAbrirSiteCorreios() {
        viewModel.onEvent(DadosPessoaisEvent.RedirecionamentoCorreiosConfirmado)

        assertTrue(
            viewModel.uiState.value!!.comandoNavegacao is DadosPessoaisEffect.AbrirSiteCorreios
        )
    }

    @Test
    fun recalcularLimiteClicado_comSucesso_deveAtualizarApostadorEAnalytics() {
        val novoApostador = apostadorPadrao(limiteDiario = BigDecimal("60.00"))
        fakeModel.recalcularResposta = ApostadorDTOResponse().apply { payload = novoApostador }

        viewModel.onEvent(DadosPessoaisEvent.RecalcularLimiteClicado)

        val state = viewModel.uiState.value!!
        assertSame(novoApostador, state.apostador)
        assertFalse(state.carregando)
        assertEquals(
            DadosPessoaisEffect.RegistrarEvento.Tipo.ENTROU_RECALCULAR_LIMITE,
            (state.comandoAnalytics as DadosPessoaisEffect.RegistrarEvento).tipo
        )
    }

    @Test
    fun recalcularLimiteClicado_comErro_deveExporErroDeRede() {
        fakeModel.recalcularErro = criarErro(500)

        viewModel.onEvent(DadosPessoaisEvent.RecalcularLimiteClicado)

        assertTrue(viewModel.uiState.value!!.comandoRede is DadosPessoaisEffect.ErroDeRede)
    }

    @Test
    fun atualizarClicado_comCamposObrigatoriosPendentes_naoDeveSalvar() {
        viewModel.onEvent(DadosPessoaisEvent.AtualizarClicado)

        val state = viewModel.uiState.value!!
        val comando = state.comandoDialog as DadosPessoaisEffect.CamposObrigatoriosPendentes
        assertTrue(comando.campos.contains(DadosPessoaisEffect.CampoObrigatorio.CEP))
        assertTrue(comando.campos.contains(DadosPessoaisEffect.CampoObrigatorio.UF))
        assertTrue(comando.campos.contains(DadosPessoaisEffect.CampoObrigatorio.MUNICIPIO))
        assertNull(fakeModel.apostadorAlteracaoRecebido)
    }

    @Test
    fun atualizarClicado_comLimiteInvalido_naoDeveSalvar() {
        prepararEstadoValidoParaSalvar()
        viewModel.onEvent(DadosPessoaisEvent.LimiteAlterado("R$ 150,00"))

        viewModel.onEvent(DadosPessoaisEvent.AtualizarClicado)

        assertNull(fakeModel.apostadorAlteracaoRecebido)
    }

    @Test
    fun atualizarClicado_comSucessoSemRedirect_deveNavegarParaCadastroAtualizado() {
        prepararEstadoValidoParaSalvar()
        fakeModel.atualizarResposta = CadastrarApostadorDTOResponse()

        viewModel.onEvent(DadosPessoaisEvent.AtualizarClicado)

        val alteracao = fakeModel.apostadorAlteracaoRecebido as ApostadorAlteracaoDTO
        assertEquals("01001000", alteracao.cep)
        assertEquals(0, BigDecimal("50.00").compareTo(alteracao.limiteDiario))
        assertEquals(ApostadorAlteracaoDTO.SexoEnum.M, alteracao.sexo)

        val state = viewModel.uiState.value!!
        assertFalse(state.carregando)
        assertEquals(
            DadosPessoaisEffect.RegistrarEvento.Tipo.EDICAO_CADASTRO_SUCESSO,
            (state.comandoAnalytics as DadosPessoaisEffect.RegistrarEvento).tipo
        )
        assertTrue(state.comandoNavegacao is DadosPessoaisEffect.CadastroAtualizado)
    }

    @Test
    fun atualizarClicado_comRedirectNaResposta_naoDeveNavegarParaCadastroAtualizado() {
        prepararEstadoValidoParaSalvar()
        fakeModel.atualizarResposta = CadastrarApostadorDTOResponse().apply {
            redirect = RedirectEnum.HOME
        }

        viewModel.onEvent(DadosPessoaisEvent.AtualizarClicado)

        val state = viewModel.uiState.value!!
        val comandoRede = state.comandoRede as DadosPessoaisEffect.Redirecionar
        assertEquals(RedirectEnum.HOME, comandoRede.redirect)
        assertFalse(state.comandoNavegacao is DadosPessoaisEffect.CadastroAtualizado)
    }

    @Test
    fun atualizarClicado_comErro_deveExporErroDeRede() {
        prepararEstadoValidoParaSalvar()
        fakeModel.atualizarErro = criarErro(500)

        viewModel.onEvent(DadosPessoaisEvent.AtualizarClicado)

        assertTrue(viewModel.uiState.value!!.comandoRede is DadosPessoaisEffect.ErroDeRede)
    }

    @Test
    fun voltarClicado_deveFecharTela() {
        viewModel.onEvent(DadosPessoaisEvent.VoltarClicado)

        assertTrue(viewModel.uiState.value!!.comandoNavegacao is DadosPessoaisEffect.FecharTela)
    }

    @Test
    fun comandoRedeConsumido_deveLimparSlot() {
        fakeModel.dadosUsuarioErro = criarErro(500)
        viewModel.onEvent(DadosPessoaisEvent.TelaIniciada)

        viewModel.onEvent(DadosPessoaisEvent.ComandoRedeConsumido)

        assertNull(viewModel.uiState.value!!.comandoRede)
    }

    @Test
    fun comandoDialogConsumido_deveLimparSlot() {
        viewModel.onEvent(DadosPessoaisEvent.NaoSeiCepClicado)

        viewModel.onEvent(DadosPessoaisEvent.ComandoDialogConsumido)

        assertNull(viewModel.uiState.value!!.comandoDialog)
    }

    @Test
    fun comandoNavegacaoConsumido_deveLimparSlot() {
        viewModel.onEvent(DadosPessoaisEvent.VoltarClicado)

        viewModel.onEvent(DadosPessoaisEvent.ComandoNavegacaoConsumido)

        assertNull(viewModel.uiState.value!!.comandoNavegacao)
    }

    @Test
    fun comandoAnalyticsConsumido_deveLimparSlot() {
        fakeModel.recalcularResposta = ApostadorDTOResponse().apply { payload = apostadorPadrao() }
        viewModel.onEvent(DadosPessoaisEvent.RecalcularLimiteClicado)

        viewModel.onEvent(DadosPessoaisEvent.ComandoAnalyticsConsumido)

        assertNull(viewModel.uiState.value!!.comandoAnalytics)
    }

    private fun prepararEstadoValidoParaSalvar() {
        carregarApostadorSemLocalizacao()
        fakeModel.bairroResposta = BairroDTOResponse().apply {
            payload = mutableListOf(
                BairroDTO().apply {
                    municipio = MunicipioDTO().apply {
                        id = MunicipioIdDTO().apply {
                            idUF = 1L
                            numero = 100L
                        }
                        nome = "São Paulo"
                        uf = UnidadeFederacaoDTO().apply { sigla = "SP" }
                    }
                }
            )
        }
        viewModel.onEvent(DadosPessoaisEvent.CepAlterado("01001000"))
    }

    private class FakeDadosPessoaisModel : DadosPessoaisModel() {

        var dadosUsuarioResposta: ApostadorDTOResponse? = null
        var dadosUsuarioErro: VolleyError? = null

        var ufsResposta: UnidadeFederacaoDTOResponse? = null
        var ufsErro: VolleyError? = null
        var ufsChamado = false

        var municipiosResposta: MunicipioDTOResponse? = null
        var municipiosErro: VolleyError? = null
        var idUfRecebido: String? = null

        var bairroResposta: BairroDTOResponse? = null
        var bairroErro: VolleyError? = null
        var cepRecebido: String? = null

        var recalcularResposta: ApostadorDTOResponse? = null
        var recalcularErro: VolleyError? = null

        var atualizarResposta: CadastrarApostadorDTOResponse? = null
        var atualizarErro: VolleyError? = null
        var apostadorAlteracaoRecebido: ApostadorAlteracaoDTO? = null

        override fun carregarDadosUsuario(callback: Callback<ApostadorDTOResponse>) {
            val erro = dadosUsuarioErro
            if (erro != null) callback.onError(erro) else callback.onSuccess(dadosUsuarioResposta)
        }

        override fun carregarUfs(callback: Callback<UnidadeFederacaoDTOResponse>) {
            ufsChamado = true
            val erro = ufsErro
            if (erro != null) callback.onError(erro) else callback.onSuccess(ufsResposta)
        }

        override fun carregarMunicipios(idUf: String, callback: Callback<MunicipioDTOResponse>) {
            idUfRecebido = idUf
            val erro = municipiosErro
            if (erro != null) callback.onError(erro) else callback.onSuccess(municipiosResposta)
        }

        override fun buscarBairroPorCep(cep: String, callback: Callback<BairroDTOResponse>) {
            cepRecebido = cep
            val erro = bairroErro
            if (erro != null) callback.onError(erro) else callback.onSuccess(bairroResposta)
        }

        override fun recalcularLimite(callback: Callback<ApostadorDTOResponse>) {
            val erro = recalcularErro
            if (erro != null) callback.onError(erro) else callback.onSuccess(recalcularResposta)
        }

        override fun atualizarDadosUsuario(
            apostadorAlteracaoDTO: ApostadorAlteracaoDTO,
            callback: Callback<CadastrarApostadorDTOResponse>
        ) {
            apostadorAlteracaoRecebido = apostadorAlteracaoDTO
            val erro = atualizarErro
            if (erro != null) callback.onError(erro) else callback.onSuccess(atualizarResposta)
        }
    }
}


