package br.gov.caixa.loterias.apostas.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.gov.caixa.loterias.apostas.effect.DadosPessoaisEffect
import br.gov.caixa.loterias.apostas.event.DadosPessoaisEvent
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorAlteracaoDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTOResponse
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTOResponse
import br.gov.caixa.loterias.apostas.model.model.DadosPessoaisModel
import br.gov.caixa.loterias.apostas.state.DadosPessoaisState
import br.gov.caixa.loterias.apostas.utils.Utils.isErroNegocial
import com.android.volley.VolleyError
import java.math.BigDecimal

class DadosPessoaisViewModel(
    private val model: DadosPessoaisModel = DadosPessoaisModel()
) : ViewModel() {

    private companion object {
        const val ERRO_CEP_INVALIDO = "CEP_INVALIDO"
        const val TAMANHO_CEP = 8
        const val POSICAO_MASCULINO = 0
        val APENAS_NUMEROS = Regex("\\D")
    }

    private val _uiState = MutableLiveData(DadosPessoaisState())
    val uiState: LiveData<DadosPessoaisState> = _uiState

    private var carregamentoIniciado = false

    fun onEvent(event: DadosPessoaisEvent) {
        when (event) {
            is DadosPessoaisEvent.TelaIniciada -> carregarDados(false)
            is DadosPessoaisEvent.TelaRetomada -> carregarDados(true)

            is DadosPessoaisEvent.CepAlterado -> handleCepAlterado(event)
            is DadosPessoaisEvent.LimiteAlterado -> handleLimiteAlterado(event)
            is DadosPessoaisEvent.SexoSelecionado -> handleSexoSelecionado(event)
            is DadosPessoaisEvent.NotificacaoAlterada -> handleNotificacaoAlterada(event)

            is DadosPessoaisEvent.NaoSeiCepClicado ->
                updateState { it.copy(comandoDialog = DadosPessoaisEffect.ConfirmarSaidaParaCorreios) }

            is DadosPessoaisEvent.RedirecionamentoCorreiosConfirmado ->
                updateState { it.copy(comandoNavegacao = DadosPessoaisEffect.AbrirSiteCorreios) }

            is DadosPessoaisEvent.RecalcularLimiteClicado -> recalcularLimite()
            is DadosPessoaisEvent.AtualizarClicado -> salvar()

            is DadosPessoaisEvent.VoltarClicado ->
                updateState { it.copy(comandoNavegacao = DadosPessoaisEffect.FecharTela) }

            is DadosPessoaisEvent.ComandoRedeConsumido ->
                updateState { if (it.comandoRede == null) it else it.copy(comandoRede = null) }

            is DadosPessoaisEvent.ComandoDialogConsumido ->
                updateState { if (it.comandoDialog == null) it else it.copy(comandoDialog = null) }

            is DadosPessoaisEvent.ComandoNavegacaoConsumido ->
                updateState { if (it.comandoNavegacao == null) it else it.copy(comandoNavegacao = null) }

            is DadosPessoaisEvent.ComandoAnalyticsConsumido ->
                updateState { if (it.comandoAnalytics == null) it else it.copy(comandoAnalytics = null) }
        }
    }

    private fun carregarDados(forcarCarregamento: Boolean) {
        if (carregamentoIniciado && !forcarCarregamento) {
            return
        }
        carregamentoIniciado = true
        atualizarCarregamento()

        model.carregarDadosUsuario(object : DadosPessoaisModel.Callback<ApostadorDTOResponse> {
            override fun onSuccess(response: ApostadorDTOResponse?) {
                val apostador = response?.payload
                if (apostador == null) {
                    finalizarCarregamentoComFalhaInterna()
                    return
                }
                updateState { state ->
                    validarLimite(
                        state.copy(
                            apostador = apostador,
                            municipioSelecionado = apostador.municipioId,
                            limiteDigitado = apostador.limiteDiario,
                            cep = apostador.cep,
                            aceitaNoticias = apostador.aceitaReceberNoticias == true,
                            posicaoSexo = resolverPosicaoSexo(apostador),
                            erroCep = null,
                            comandoRede = criarComandoRedirect(response.redirect),
                            comandoAnalytics = DadosPessoaisEffect.RegistrarEvento(
                                DadosPessoaisEffect.RegistrarEvento.Tipo.ENTROU_EDITAR_CADASTRO
                            )
                        ),
                        apostador
                    )
                }
                carregarLocalizacao(apostador)
            }

            override fun onError(error: VolleyError?) {
                finalizarCarregamentoComErro(error)
            }
        })
    }

    private fun resolverPosicaoSexo(apostador: ApostadorDTO): Int {
        return if (ApostadorDTO.SexoEnum.F == apostador.sexo) 1 else POSICAO_MASCULINO
    }

    private fun carregarLocalizacao(apostador: ApostadorDTO) {
        if (semUfValida(apostador)) {
            finalizarCarregamentoInicial()
            return
        }
        model.carregarUfs(object : DadosPessoaisModel.Callback<UnidadeFederacaoDTOResponse> {
            override fun onSuccess(response: UnidadeFederacaoDTOResponse?) {
                val sigla = buscarSiglaUf(response, apostador)
                if (sigla != null) {
                    updateState { it.copy(uf = sigla) }
                }
                carregarMunicipios(apostador)
            }

            override fun onError(error: VolleyError?) {
                finalizarCarregamentoComErro(error)
            }
        })
    }

    private fun semUfValida(apostador: ApostadorDTO): Boolean {
        return apostador.municipioId?.idUF == null
    }

    private fun buscarSiglaUf(
        response: UnidadeFederacaoDTOResponse?,
        apostador: ApostadorDTO
    ): String? {
        val idUf = apostador.municipioId?.idUF ?: return null
        return response?.payload
            ?.firstOrNull { it?.id != null && it.id == idUf }
            ?.sigla
    }

    private fun carregarMunicipios(apostador: ApostadorDTO) {
        val idUf = apostador.municipioId?.idUF
        if (idUf == null) {
            finalizarCarregamentoInicial()
            return
        }
        model.carregarMunicipios(
            idUf.toString(),
            object : DadosPessoaisModel.Callback<MunicipioDTOResponse> {
                override fun onSuccess(response: MunicipioDTOResponse?) {
                    val nome = buscarNomeMunicipio(response, apostador)
                    carregamentoIniciado = false
                    updateState { state ->
                        state.copy(
                            municipio = nome ?: state.municipio,
                            comandoRede = criarComandoRedirect(response?.redirect)
                                ?: state.comandoRede,
                            carregando = false,
                            dadosCarregados = true
                        )
                    }
                }

                override fun onError(error: VolleyError?) {
                    finalizarCarregamentoComErro(error)
                }
            }
        )
    }

    private fun buscarNomeMunicipio(
        response: MunicipioDTOResponse?,
        apostador: ApostadorDTO
    ): String? {
        val numero = apostador.municipioId?.numero ?: return null
        return response?.payload
            ?.firstOrNull { it?.id?.numero != null && it.id.numero == numero }
            ?.nome
    }

    private fun finalizarCarregamentoInicial() {
        carregamentoIniciado = false
        updateState { it.copy(carregando = false, dadosCarregados = true) }
    }

    private fun handleCepAlterado(event: DadosPessoaisEvent.CepAlterado) {
        val cep = event.cep
        if (cep == null || cep.length != TAMANHO_CEP) {
            return
        }
        updateState { it.copy(cep = cep, erroCep = null) }
        buscarCep(cep)
    }

    private fun buscarCep(cep: String) {
        atualizarCarregamento()
        model.buscarBairroPorCep(cep, object : DadosPessoaisModel.Callback<BairroDTOResponse> {
            override fun onSuccess(response: BairroDTOResponse?) {
                val bairro = response?.payload?.firstOrNull()
                val municipio = bairro?.municipio

                if (municipio?.uf == null || municipio.id == null) {
                    definirCepInvalido()
                    return
                }
                updateState { state ->
                    state.copy(
                        uf = municipio.uf.sigla,
                        municipio = municipio.nome,
                        municipioSelecionado = municipio.id,
                        erroCep = null,
                        carregando = false,
                        comandoRede = criarComandoRedirect(response.redirect)
                    )
                }
            }

            override fun onError(error: VolleyError?) {
                if (isErroNegocial(error)) {
                    definirCepInvalido()
                    return
                }
                updateState {
                    it.copy(
                        carregando = false,
                        comandoRede = DadosPessoaisEffect.ErroDeRede(error)
                    )
                }
            }
        })
    }

    private fun definirCepInvalido() {
        updateState {
            it.copy(
                uf = "",
                municipio = "",
                municipioSelecionado = null,
                carregando = false,
                erroCep = ERRO_CEP_INVALIDO
            )
        }
    }

    private fun handleLimiteAlterado(event: DadosPessoaisEvent.LimiteAlterado) {
        val limite = converterLimite(event.limiteFormatado) ?: return
        updateState { state ->
            val apostador = state.apostador ?: return@updateState state
            validarLimite(state.copy(limiteDigitado = limite), apostador)
        }
    }

    private fun converterLimite(limiteFormatado: String?): BigDecimal? {
        if (limiteFormatado == null) {
            return null
        }
        val apenasDigitos = APENAS_NUMEROS.replace(limiteFormatado, "")
        if (apenasDigitos.isEmpty()) {
            return BigDecimal.ZERO
        }
        return BigDecimal(apenasDigitos).movePointLeft(2)
    }

    private fun validarLimite(
        state: DadosPessoaisState,
        apostador: ApostadorDTO
    ): DadosPessoaisState {
        val limite = state.limiteDigitado
        val limiteMinimo = apostador.limiteMinimoDiario
        val limiteMaximo = apostador.limiteDiarioAutorizado

        if (limite == null || limiteMinimo == null || limiteMaximo == null) {
            return state.copy(limiteMenorQueMinimo = false, limiteMaiorQueMaximo = false)
        }
        return state.copy(
            limiteMenorQueMinimo = limite < limiteMinimo,
            limiteMaiorQueMaximo = limite > limiteMaximo
        )
    }

    private fun recalcularLimite() {
        atualizarCarregamento()
        model.recalcularLimite(object : DadosPessoaisModel.Callback<ApostadorDTOResponse> {
            override fun onSuccess(response: ApostadorDTOResponse?) {
                updateState { state ->
                    val apostador = response?.payload
                    val base = if (apostador == null) {
                        state
                    } else {
                        validarLimite(state.copy(apostador = apostador), apostador)
                    }
                    base.copy(
                        carregando = false,
                        comandoAnalytics = DadosPessoaisEffect.RegistrarEvento(
                            DadosPessoaisEffect.RegistrarEvento.Tipo.ENTROU_RECALCULAR_LIMITE
                        ),
                        comandoRede = criarComandoRedirect(response?.redirect) ?: base.comandoRede
                    )
                }
            }

            override fun onError(error: VolleyError?) {
                finalizarCarregamentoComErro(error)
            }
        })
    }

    private fun handleSexoSelecionado(event: DadosPessoaisEvent.SexoSelecionado) {
        updateState { state ->
            if (state.posicaoSexo == event.posicao) state
            else state.copy(posicaoSexo = event.posicao)
        }
    }

    private fun handleNotificacaoAlterada(event: DadosPessoaisEvent.NotificacaoAlterada) {
        updateState { state ->
            if (state.aceitaNoticias == event.aceitaNoticias) {
                return@updateState state
            }
            state.copy(
                aceitaNoticias = event.aceitaNoticias,
                comandoDialog = if (state.dadosCarregados) {
                    DadosPessoaisEffect.MostrarAvisoNotificacao(event.aceitaNoticias)
                } else {
                    state.comandoDialog
                }
            )
        }
    }

    private fun salvar() {
        val state = currentState()

        val pendentes = validarCamposObrigatorios(state)
        if (pendentes.isNotEmpty()) {
            updateState {
                it.copy(comandoDialog = DadosPessoaisEffect.CamposObrigatoriosPendentes(pendentes))
            }
            return
        }
        if (!state.limiteValido || state.carregando) {
            return
        }

        atualizarCarregamento()
        model.atualizarDadosUsuario(
            montarApostadorAlteracao(state),
            object : DadosPessoaisModel.Callback<CadastrarApostadorDTOResponse> {
                override fun onSuccess(response: CadastrarApostadorDTOResponse?) {
                    val redirect = criarComandoRedirect(response?.redirect)
                    updateState { atual ->
                        atual.copy(
                            carregando = false,
                            comandoAnalytics = DadosPessoaisEffect.RegistrarEvento(
                                DadosPessoaisEffect.RegistrarEvento.Tipo.EDICAO_CADASTRO_SUCESSO
                            ),
                            comandoRede = redirect ?: atual.comandoRede,
                            comandoNavegacao = if (redirect == null) {
                                DadosPessoaisEffect.CadastroAtualizado
                            } else {
                                atual.comandoNavegacao
                            }
                        )
                    }
                }

                override fun onError(error: VolleyError?) {
                    finalizarCarregamentoComErro(error)
                }
            }
        )
    }

    private fun validarCamposObrigatorios(
        state: DadosPessoaisState
    ): List<DadosPessoaisEffect.CampoObrigatorio> {
        val pendentes = mutableListOf<DadosPessoaisEffect.CampoObrigatorio>()
        val cep = state.cep?.let { APENAS_NUMEROS.replace(it, "") }.orEmpty()

        if (cep.length != TAMANHO_CEP) {
            pendentes.add(DadosPessoaisEffect.CampoObrigatorio.CEP)
        }
        if (state.uf.isNullOrBlank()) {
            pendentes.add(DadosPessoaisEffect.CampoObrigatorio.UF)
        }
        if (state.municipio.isNullOrBlank() || !state.cepValido) {
            pendentes.add(DadosPessoaisEffect.CampoObrigatorio.MUNICIPIO)
        }
        return pendentes
    }

    private fun montarApostadorAlteracao(state: DadosPessoaisState): ApostadorAlteracaoDTO {
        return ApostadorAlteracaoDTO().apply {
            cep = APENAS_NUMEROS.replace(state.cep.orEmpty(), "")
            municipioId = state.municipioSelecionado
            setAceitaReceberNoticias(state.aceitaNoticias)
            limiteDiario = state.limiteDigitado
            alterandoLoterica = false
            sexo = if (state.posicaoSexo == POSICAO_MASCULINO) {
                ApostadorAlteracaoDTO.SexoEnum.M
            } else {
                ApostadorAlteracaoDTO.SexoEnum.F
            }
        }
    }


    private fun criarComandoRedirect(redirect: RedirectEnum?): DadosPessoaisEffect? {
        return redirect?.let { DadosPessoaisEffect.Redirecionar(it) }
    }

    private fun atualizarCarregamento() {
        updateState { it.copy(carregando = true) }
    }

    private fun finalizarCarregamentoComErro(error: VolleyError?) {
        carregamentoIniciado = false
        updateState {
            it.copy(
                carregando = false,
                comandoRede = DadosPessoaisEffect.ErroDeRede(error)
            )
        }
    }

    private fun finalizarCarregamentoComFalhaInterna() {
        carregamentoIniciado = false
        updateState { it.copy(carregando = false, dadosCarregados = false) }
    }

    private fun updateState(reducer: (DadosPessoaisState) -> DadosPessoaisState) {
        _uiState.value = reducer(currentState())
    }

    private fun currentState(): DadosPessoaisState {
        return _uiState.value ?: DadosPessoaisState()
    }
}
