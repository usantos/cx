package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;

import static br.gov.caixa.loterias.apostas.utils.Utils.isErroNegocial;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.effect.AnalyticsEffect;
import br.gov.caixa.loterias.apostas.effect.AnimacaoEffect;
import br.gov.caixa.loterias.apostas.effect.CarrinhoEffect;
import br.gov.caixa.loterias.apostas.effect.FavoritarApostaEffect;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IncluirSurpresinhaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.model.AccordionModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;

public class SimulaViewModel extends AndroidViewModel {

    protected final MutableLiveData<SimulaUiState> uiState = new MutableLiveData<>(SimulaUiState.initial());
    protected final TeimosinhaReducer teimosinhaReducer = new TeimosinhaReducer();
    protected final FluxoApostaReducer fluxoApostaReducer = new FluxoApostaReducer();
    protected final QuantidadeNumerosReducer quantidadeNumerosReducer = new QuantidadeNumerosReducer(fluxoApostaReducer);
    protected final TrevosReducer trevosReducer = new TrevosReducer();
    protected final CarrinhoReducer carrinhoReducer = new CarrinhoReducer();
    protected final SurpresinhaReducer surpresinhaReducer = new SurpresinhaReducer();
    protected final SurpresinhaCarrinhoReducer surpresinhaCarrinhoReducer = new SurpresinhaCarrinhoReducer();
    protected final MesDaSorteReducer mesDaSorteReducer = new MesDaSorteReducer();
    protected final FavoritarApostaReducer favoritarApostaReducer = new FavoritarApostaReducer();
    protected final LimparReducer limparReducer = new LimparReducer();
    protected final TelaInicializadaReducer telaInicializadaReducer = new TelaInicializadaReducer(fluxoApostaReducer);
    protected final CompletarApostaReducer completarApostaReducer = new CompletarApostaReducer();
    protected final AdicionarCarrinhoReducer adicionarCarrinhoReducer = new AdicionarCarrinhoReducer();
    protected final ValorApostaReducer valorApostaReducer = new ValorApostaReducer();
    protected IdentificaoDeUmaApostaDas8Modalidades apostaPendente;
    protected BarraTituloDTO barraTituloPendente;

    protected ModalidadeEnum tipoJogoAtual;
    protected ParametroJogoDTO parametroJogoAtual;
    protected boolean isEspecialAtual;

    public SimulaViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<SimulaUiState> getUiState() {
        return uiState;
    }

    public void onEvent(SimulaUiEvent event) {
        if (handleCarrinhoEvents(event)) {
            return;
        }
        if(handleLimparEvents(event)){
            return;
        }
        if(handleCompletarEvents(event)){
            return;
        }
        if(handleCartelaEvents(event)){
            return;
        }
        if(handleTeimosinhaEvents(event)){
            return;
        }
        if(handleNumerosEvents(event)){
            return;
        }
        if(handleSurpresinhaEvents(event)){
            return;
        }
        if(handleTrevosEvents(event)){
            return;
        }
        if(handleFavoritarEvents(event)){
            return;
        }
        if(handleNextStepEvents(event)){
            return;
        }
    }

    protected boolean handleCarrinhoEvents(
            SimulaUiEvent event
    ) {

        if (event instanceof
                SimulaUiEvent.AdicionarCarrinhoSolicitado adicionarCarrinhoSolicitado) {

            handleAdicionarCarrinhoSolicitado(
                    adicionarCarrinhoSolicitado
            );

            return true;
        }

        if (event instanceof
                SimulaUiEvent.AdicionarCarrinhoClicado adicionarCarrinhoClicado) {

            handleAdicionarCarrinhoClicado(
                    adicionarCarrinhoClicado
            );

            return true;
        }

        if (event instanceof
                SimulaUiEvent.ConfirmarAdicionarCarrinho) {

            executarAdicionarCarrinhoPendente();

            return true;
        }

        if (event instanceof
                SimulaUiEvent.CarrinhoClicado) {

            abrirCarrinho();

            return true;
        }

        if (event instanceof
                SimulaUiEvent.CarregarCarrinho) {

            carregarCarrinho();

            return true;
        }

        if (event instanceof SimulaUiEvent.AbrirCarrinhoConsumido) {
            consumirAbrirCarrinho();
            return true;
        }

        if (event instanceof SimulaUiEvent.ComandoCarrinhoConsumido) {

            limparComandoCarrinho();

            return true;
        }

        return false;
    }
    protected boolean handleLimparEvents(
            SimulaUiEvent event
    ) {
        if (event instanceof SimulaUiEvent.LimparApostaSolicitado) {

            limparAposta();

            return true;
        }

        if (event instanceof SimulaUiEvent.ComandoTelaSimulaConsumido) {
            limparComandoTelaSimula();
            return true;
        }

        if (event instanceof SimulaUiEvent.ComandoFavoritarApostaConsumido) {
            limparComandoFavoritarAposta();
            return true;
        }

        if (event instanceof
                SimulaUiEvent.LimparMesDaSorte) {

            handleLimparMesDaSorte();

            return true;
        }

        if (event instanceof SimulaUiEvent.ComandoAnalyticsConsumido) {
            limparComandoAnalytics();
            return true;
        }

        if (event instanceof
                SimulaUiEvent.ComandoAnimacaoConsumido) {
            limparComandoAnimacao();
            return true;
        }

        if (event instanceof SimulaUiEvent.LimpezaApostaRenderizada) {
            limparFlagsDeRenderizacao();
            return true;
        }

        return false;
    }
    protected boolean handleCartelaEvents(
            SimulaUiEvent event
    ) {
        if (event instanceof SimulaUiEvent.ToggleOutrosNumeros) {
            toggleOutrosNumeros();
            return true;
        }

        if (event instanceof SimulaUiEvent.CartelaAlterada cartelaAlterada) {

            handleCartelaAlterada(cartelaAlterada);

            return true;
        }
        if (event instanceof SimulaUiEvent.ValorApostaAlterado valorApostaAlterado) {

            handleValorApostaAlterado(valorApostaAlterado);

            return true;
        }
        if (event instanceof SimulaUiEvent.InicializarParametrosAposta eventoParametrosAposta) {
            inicializarParametrosAposta(
                    eventoParametrosAposta
            );
            return true;
        }

        if (event instanceof SimulaUiEvent.TelaInicializada telaInicializada) {

            handleTelaInicializada(telaInicializada);

            return true;
        }

        if (event instanceof SimulaUiEvent.ComoJogarClicado) {

            handleComoJogar();

            return true;
        }

        if (event instanceof SimulaUiEvent.DialogComoJogarConsumido) {

            consumirDialogComoJogar();

            return true;
        }


        return false;
    }
    protected boolean handleTeimosinhaEvents(
            SimulaUiEvent event
    ) {
        if (event instanceof SimulaUiEvent.InicializarTeimosinhas eventoTeimosinhas) {
            inicializarTeimosinhas(eventoTeimosinhas);
            return true;
        }

        if (event instanceof SimulaUiEvent.ValorApostaAtualizado) {
            valorApostaAtualizado();
            return true;
        }

        if (event instanceof SimulaUiEvent.ClicarTeimosinhas) {
            clicarTeimosinhas();
            return true;
        }

        if (event instanceof
                SimulaUiEvent.QtdConcursoAtualizadaInternamente qtdConcursoAtualizadaInternamente) {

            handleQtdConcursoAtualizadaInternamente(
                    qtdConcursoAtualizadaInternamente
            );
            return true;
        }

        if (event instanceof SimulaUiEvent.ConfirmarTeimosinha confirmarTeimosinha) {
            confirmarTeimosinha(confirmarTeimosinha);
            return true;
        }

        if (event instanceof SimulaUiEvent.ConsumirEventoUnico) {
            consumirEventoUnico();
            return true;
        }

        return false;
    }
    protected boolean handleNumerosEvents(
            SimulaUiEvent event
    ) {
        if (event instanceof SimulaUiEvent.InicializarQuantidadeNumeros eventoQuantidadeNumeros) {
            inicializarQuantidadeNumeros(eventoQuantidadeNumeros);
            return true;
        }
        if (event instanceof
                SimulaUiEvent.QuantidadeNumerosAtualizadaInternamente quantidadeNumerosAtualizadaInternamente) {

            atualizarQuantidadeInterna(
                    quantidadeNumerosAtualizadaInternamente
            );
            return true;
        }

        if (event instanceof SimulaUiEvent.ClicarQuantidadeNumeros) {
            clicarQuantidadeNumeros();
            return true;
        }

        if (event instanceof SimulaUiEvent.SelecionarQuantidadeNumeros selecionarQuantidadeNumeros) {
            selecionarQuantidadeNumeros(selecionarQuantidadeNumeros);
            return true;
        }

        if (event instanceof SimulaUiEvent.ConfirmarQuantidadeNumeros confirmarQuantidadeNumeros) {
            confirmarQuantidadeNumeros(confirmarQuantidadeNumeros);
            return true;
        }

        if (event instanceof SimulaUiEvent.DialogQuantidadeNumerosConsumido) {
            consumirDialogQuantidadeNumeros();
            return true;
        }

        if (event instanceof SimulaUiEvent.AvisoQuantidadeNumerosConsumido) {
            consumirAvisoQuantidadeNumeros();
            return true;
        }

        if (event instanceof SimulaUiEvent.ValorApostaQuantidadeNumerosAtualizado) {
            valorApostaQuantidadeNumerosAtualizado();
            return true;
        }

        return false;
    }

    protected boolean handleCompletarEvents(
            SimulaUiEvent event
    ) {
        if (event instanceof SimulaUiEvent.CompletarApostaClicado completarApostaClicado) {

            handleCompletarApostaClicado(completarApostaClicado);

            return true;
        }
        if (event instanceof SimulaUiEvent.ComandoCompletarApostaConsumido) {

            limparComandoCompletarAposta();

            return true;
        }

        return false;
    }
    protected boolean handleNextStepEvents(
            SimulaUiEvent event
    ) {

        if (event instanceof SimulaUiEvent.NextStepClicado nextStepClicado) {

            handleNextStepClicado(nextStepClicado);

            return true;
        }

        if (event instanceof SimulaUiEvent.MesSorteAlterado mesSorteAlterado) {
            handleMesSorteAlterado(mesSorteAlterado);
            return true;
        }



        if (event instanceof
                SimulaUiEvent.SelecionarMesDaSorte selecionarMesDaSorte) {

            handleSelecionarMesDaSorte(
                    selecionarMesDaSorte
            );

            return true;
        }
        if (event instanceof SimulaUiEvent.VoltarEtapa) {

            updateState(fluxoApostaReducer::voltarEtapa);

            return true;
        }

        if (event instanceof
                SimulaUiEvent.SelecionarTimeCoracao selecionarTimeCoracao) {

            handleSelecionarTimeCoracao(
                    selecionarTimeCoracao
            );
            return true;
        }

        return false;
    }
    protected boolean handleFavoritarEvents(
            SimulaUiEvent event
    ) {
        if (event instanceof SimulaUiEvent.FavoritarApostaClicado) {
            abrirDialogFavoritarAposta();
            return true;
        }

        if (event instanceof SimulaUiEvent.DialogFavoritarApostaConsumido) {
            consumirDialogFavoritarAposta();
            return true;
        }
        if (event instanceof SimulaUiEvent.SalvarApostaFavoritaSolicitado salvarApostaFavoritaSolicitado) {
            handleSalvarApostaFavoritaSolicitado(salvarApostaFavoritaSolicitado);
            return true;
        }

        if (event instanceof SimulaUiEvent.ConfirmarSalvarApostaFavorita confirmarSalvarApostaFavorita) {
            salvarApostaFavorita(confirmarSalvarApostaFavorita.getApostaFavorita());
            return true;
        }

        return false;
    }
    protected boolean handleTrevosEvents(
            SimulaUiEvent event
    ) {

        if (event instanceof SimulaUiEvent.QuantidadeTrevosAlterada quantidadeTrevosAlterada) {
            handleQuantidadeTrevosAlterada(quantidadeTrevosAlterada);
            return true;
        }
        if (event instanceof SimulaUiEvent.TrevosAlterados trevosAlterados) {
            handleTrevosAlterados(trevosAlterados);
            return true;
        }
        if (event instanceof SimulaUiEvent.ClicarQuantidadeTrevos clicarQuantidadeTrevos) {
            clicarQuantidadeTrevos(
                    clicarQuantidadeTrevos
            );
            return true;
        }

        if (event instanceof SimulaUiEvent.SelecionarQuantidadeTrevos selecionarQuantidadeTrevos) {
            selecionarQuantidadeTrevos(
                    selecionarQuantidadeTrevos
            );
            return true;
        }

        if (event instanceof SimulaUiEvent.ConfirmarQuantidadeTrevos confirmarQuantidadeTrevos) {
            confirmarQuantidadeTrevos(
                    confirmarQuantidadeTrevos
            );
            return true;
        }

        if (event instanceof SimulaUiEvent.DialogQuantidadeTrevosConsumido) {
            consumirDialogQuantidadeTrevos();
            return true;
        }

        if (event instanceof
                SimulaUiEvent.AvisoTrevosMaximoConsumido) {

            consumirAvisoTrevosMaximo();
            return true;
        }
        if (event instanceof SimulaUiEvent.ToggleTrevo toggleTrevo) {
            handleToggleTrevo(
                    toggleTrevo
            );
            return true;
        }

        return false;
    }


    protected boolean handleSurpresinhaEvents(
            SimulaUiEvent event
    ) {
        if (event instanceof SimulaUiEvent.AlterarSurpresinha alterarSurpresinha) {

            handleAlterarSurpresinha(alterarSurpresinha.isHabilitada());

            return true;
        }

        if (event instanceof
                SimulaUiEvent.AtualizarQuantidadeSurpresinhas atualizarQuantidadeSurpresinhas) {

            handleQuantidadeSurpresinhas(
                    atualizarQuantidadeSurpresinhas
            );
            return true;
        }
        if (event instanceof
                SimulaUiEvent.AtualizarQuantidadeNumerosSurpresinha atualizarQuantidadeNumerosSurpresinha) {

            handleQuantidadeNumerosSurpresinha(
                    atualizarQuantidadeNumerosSurpresinha
            );
            return true;
        }
        if (event instanceof SimulaUiEvent.AdicionarSurpresinhaCarrinho adicionarSurpresinhaCarrinho) {
            handleAdicionarSurpresinhaCarrinho(
                    adicionarSurpresinhaCarrinho
            );
            return true;
        }
        if (event instanceof SimulaUiEvent.SolicitarSelecaoTimeCoracao) {
            handleSolicitarSelecaoTimeCoracao();
            return true;
        }
        if (event instanceof SimulaUiEvent.VoltarParaSurpresinha) {
            handleVoltarParaSurpresinha();
            return true;
        }
        if (event instanceof
                SimulaUiEvent.AtualizarQuantidadeTrevosSurpresinha atualizarQuantidadeTrevosSurpresinha) {

            handleQuantidadeTrevosSurpresinha(
                    atualizarQuantidadeTrevosSurpresinha
            );
            return true;
        }

        return false;
    }

    protected void handleSelecionarTimeCoracao(
            SimulaUiEvent.SelecionarTimeCoracao event
    ){
       updateState(state ->
                state
                        .copy()
                        .setEquipeSelecionada(
                                event.getEquipe()
                        )
        );
    }
    protected void inicializarParametrosAposta(
            SimulaUiEvent.InicializarParametrosAposta event
    ) {
        updateState( state ->
                state
                        .copy()
                        .setQtdDezenasPossiveisSelecionado(
                                event.getQtdDezenas()
                        )
                        .setQtdConcursoSelecionado(
                                event.getQtdConcurso()
                        )
        );
    }
    protected void handleQtdConcursoAtualizadaInternamente(
            SimulaUiEvent.QtdConcursoAtualizadaInternamente event
    ) {
        updateState( state ->
                state.copy()
                        .setQtdConcursoSelecionado(
                                event.getQuantidade()
                        )
        );
    }
    protected void atualizarQuantidadeInterna(
            SimulaUiEvent
                    .QuantidadeNumerosAtualizadaInternamente event
    ) {
        updateState( state ->
                state
                        .copy()
                        .setQtdDezenasPossiveisSelecionado(
                                event.getQuantidade()
                        )
        );
    }
    private void handleLimparMesDaSorte() {

        updateState(mesDaSorteReducer::limparMesDaSorte);
    }

    private void handleQuantidadeSurpresinhas(
            SimulaUiEvent.AtualizarQuantidadeSurpresinhas event
    ) {

        updateState( state ->
                surpresinhaReducer.atualizarQuantidadeSurpresinhas(
                        state,
                        event.getQuantidade(),
                        parametroJogoAtual,
                        tipoJogoAtual
                )
        );
    }

    private void handleQuantidadeTrevosSurpresinha(
            SimulaUiEvent.AtualizarQuantidadeTrevosSurpresinha event
    ) {

        updateState( state ->
                surpresinhaReducer.atualizarQuantidadeTrevosSurpresinha(
                        state,
                        event.getQuantidade(),
                        parametroJogoAtual,
                        tipoJogoAtual
                )
        );
    }

    private void handleQuantidadeNumerosSurpresinha(
            SimulaUiEvent.AtualizarQuantidadeNumerosSurpresinha event
    ) {

        updateState( state ->
                surpresinhaReducer.atualizarQuantidadeNumerosSurpresinha(
                        state,
                        event.getQuantidade(),
                        parametroJogoAtual,
                        tipoJogoAtual
                )
        );
    }

    private void handleToggleTrevo(
            SimulaUiEvent.ToggleTrevo event
    ) {
        SimulaUiState atual =
                getCurrentState();

        List<ParametroValorApostaDTO> valoresTrevos =
                obterValoresTrevosParaQuantidadeAtual(
                        atual
                );

       updateState(
                trevosReducer.toggleTrevo(
                        atual,
                        event,
                        valoresTrevos
                )
        );
    }

    private List<ParametroValorApostaDTO> obterValoresTrevosParaQuantidadeAtual(
            SimulaUiState atual
    ) {
        if (parametroJogoAtual == null
                || atual == null) {
            return new ArrayList<>();
        }

        int quantidadeNumeros =
                atual.getQtdDezenasPossiveisSelecionado();

        if (quantidadeNumeros <= 0) {
            quantidadeNumeros =
                    getQuantidadeMinimaSafe();
        }

        List<ParametroValorApostaDTO> valores =
                parametroJogoAtual.getValoresTrevoByNumero(
                        quantidadeNumeros
                );

        if (valores == null) {
            return new ArrayList<>();
        }

        return valores;
    }

    private void consumirAvisoTrevosMaximo() {

        updateState(trevosReducer::consumirAvisoTrevosMaximo);
    }

    private void selecionarQuantidadeTrevos(
            SimulaUiEvent.SelecionarQuantidadeTrevos event
    ) {

        updateState(state ->
                trevosReducer.selecionarQuantidadeTrevos(
                        state,
                        event
                )
        );
    }

    private void consumirDialogQuantidadeTrevos() {

        updateState(trevosReducer::consumirDialogQuantidadeTrevos);
    }

    protected void limparComandoAnimacao() {
        updateState(state ->
                state.copy()
                        .setComandoAnimacao(null)
        );
    }

    private void handleMesSorteAlterado(
            SimulaUiEvent.MesSorteAlterado event
    ) {

        updateState(state ->
                mesDaSorteReducer.mesSorteAlterado(
                        state,
                        event
                )
        );
    }
    private void handleSelecionarMesDaSorte(
            SimulaUiEvent.SelecionarMesDaSorte event
    ) {

       updateState(state ->
                mesDaSorteReducer.selecionarMesDaSorte(
                        state,
                        event
                )
        );
    }

    private void handleNextStepClicado(
            SimulaUiEvent.NextStepClicado event
    ) {

        updateState(state ->
                fluxoApostaReducer.nextStepClicado(
                        state,
                        event,
                        tipoJogoAtual
                )
        );
    }

    private void handleTrevosAlterados(
            SimulaUiEvent.TrevosAlterados event
    ) {

        updateState(state ->
                trevosReducer.trevosAlterados(
                        state,
                        event
                )
        );
    }

    private void handleQuantidadeTrevosAlterada(
            SimulaUiEvent.QuantidadeTrevosAlterada event
    ) {

        updateState(state ->
                trevosReducer.quantidadeTrevosAlterada(
                        state,
                        event
                )
        );
    }

    private void clicarQuantidadeTrevos(
            SimulaUiEvent.ClicarQuantidadeTrevos event
    ) {

        updateState(state ->
                trevosReducer.clicarQuantidadeTrevos(
                        state,
                        event
                )
        );
    }

    private void confirmarQuantidadeTrevos(
            SimulaUiEvent.ConfirmarQuantidadeTrevos event
    ) {

        updateState(state ->
                trevosReducer.confirmarQuantidadeTrevos(
                        state,
                        event
                )
        );
    }
    private void handleSalvarApostaFavoritaSolicitado(SimulaUiEvent.SalvarApostaFavoritaSolicitado event) {

        ApostaFavoritaDTO apostaFavorita = event.getApostaFavorita();

        if (apostaFavorita == null || parametroJogoAtual == null) {
            return;
        }

        prepararApostaFavorita(apostaFavorita, event.getNomeAposta(), event.getNumerosSelecionados());

        validarApostaFavorita(apostaFavorita);
    }

    private void toggleOutrosNumeros() {

        SimulaUiState atual = getCurrentState();

        boolean novoValor = !atual.isOpcaoOutrosNumerosSelecionada();

        updateState(atual.copy().setOpcaoOutrosNumerosSelecionada(novoValor));
    }

    private void prepararApostaFavorita(
            ApostaFavoritaDTO apostaFavorita,
            String nomeAposta,
            Object numerosSelecionados
    ) {

        favoritarApostaReducer
                .prepararApostaFavorita(
                        apostaFavorita,
                        parametroJogoAtual,
                        nomeAposta,
                        numerosSelecionados
                );
    }

    private void validarApostaFavorita(final ApostaFavoritaDTO apostaFavorita) {


        ServicoFactoryUtil.getDadosCorporativoService().validarApostaFavorita(apostaFavorita, new RequestListener<NetworkResponse>() {

            @Override
            public void onResponse(NetworkResponse response) {
                salvarApostaFavorita(apostaFavorita);
            }

            @Override
            public void onErrorResponse(VolleyError error) {


                if (isErroNegocial(error)) {

                    publicarComandoFavoritar(new FavoritarApostaEffect.ConfirmarValidacaoNegocial(apostaFavorita, MensagensNetwork.getErrorMessage(error)));

                    return;
                }

                publicarComandoFavoritar(new FavoritarApostaEffect.RedirecionarErro(error));
            }
        });
    }

    private void salvarApostaFavorita(final ApostaFavoritaDTO apostaFavorita) {


        ServicoFactoryUtil.getDadosCorporativoService().salvarApostaFavorita(apostaFavorita, new RequestListener<NetworkResponse>() {

            @Override
            public void onResponse(NetworkResponse response) {
                updateState(favoritarApostaReducer::sucessoFavoritar);
                publicarComandoFavoritar(new FavoritarApostaEffect.Sucesso());
            }

            @Override
            public void onErrorResponse(VolleyError error) {


                publicarComandoFavoritar(new FavoritarApostaEffect.RedirecionarErro(error));
            }
        });
    }

    private void publicarComandoFavoritar(
            FavoritarApostaEffect comando
    ) {

        updateState(state ->
                favoritarApostaReducer.publicarComando(
                        state,
                        comando
                )
        );
    }

    private void limparComandoFavoritarAposta() {

        updateState(favoritarApostaReducer::limparComando);
    }

    private void abrirDialogFavoritarAposta() {

       updateState(favoritarApostaReducer::abrirDialog);
    }

    private void consumirDialogFavoritarAposta() {

       updateState(favoritarApostaReducer::consumirDialog);
    }

    private void handleAlterarSurpresinha(
            boolean habilitada
    ) {
        updateState(state ->
                surpresinhaReducer.alterarSurpresinha(
                        state,
                        habilitada,
                        tipoJogoAtual,
                        parametroJogoAtual
                )
        );
    }

    protected void limparComandoTelaSimula() {
        updateState(state -> state.copy().setComandoTelaSimula(null));
    }

    protected void abrirCarrinho() {

        updateState(carrinhoReducer::abrirCarrinho);
    }

    protected void consumirAbrirCarrinho() {

       updateState(carrinhoReducer::consumirAbrirCarrinho
        );
    }

    protected void inicializarQuantidadeNumeros(
            SimulaUiEvent.InicializarQuantidadeNumeros event
    ) {

        updateState(state ->
                quantidadeNumerosReducer.inicializar(
                        state,
                        event
                )
        );
    }

    protected void clicarQuantidadeNumeros() {

        updateState(quantidadeNumerosReducer::clicar);
    }

    protected void selecionarQuantidadeNumeros(
            SimulaUiEvent.SelecionarQuantidadeNumeros event
    ) {

        updateState(state ->
                quantidadeNumerosReducer.selecionar(
                        state,
                        event,
                        tipoJogoAtual
                )
        );
    }

    protected void confirmarQuantidadeNumeros(
            SimulaUiEvent.ConfirmarQuantidadeNumeros event
    ) {
        SimulaUiState novoEstado =
                quantidadeNumerosReducer.confirmar(
                        getCurrentState(),
                        event,
                        tipoJogoAtual
                );

        novoEstado =
                fluxoApostaReducer.recalcularEstadoCartela(
                        novoEstado,
                        tipoJogoAtual
                );

        updateState(
                novoEstado
        );

        recalcularValorApostaAtual();
    }

    protected String montarTextoQuantidadeNumeros(
            int qtdPrognosticos
    ) {

        return quantidadeNumerosReducer
                .montarTextoQuantidadeNumeros(
                        qtdPrognosticos
                );
    }

    protected void consumirDialogQuantidadeNumeros() {

        updateState(quantidadeNumerosReducer::consumirDialog);
    }

    protected void consumirAvisoQuantidadeNumeros() {

        updateState(quantidadeNumerosReducer::consumirAviso);
    }

    protected void valorApostaQuantidadeNumerosAtualizado() {

        updateState(quantidadeNumerosReducer::valorApostaAtualizado);
    }

    protected void inicializarTeimosinhas(SimulaUiEvent.InicializarTeimosinhas event) {
        updateState(state ->
                teimosinhaReducer.inicializar(
                        state,
                        event.getQntTeimosinhas(),
                        event.getQntConcursos(),
                        event.getQntConcursoSelecionado(),
                        event.isEspecial(),
                        event.getTextoInicialTeimosinha()
                )
        );
    }

    protected void clicarTeimosinhas() {
        updateState(teimosinhaReducer::clicar);
    }

    protected void confirmarTeimosinha(SimulaUiEvent.ConfirmarTeimosinha event) {
        updateState(state ->
                teimosinhaReducer.confirmar(
                        state,
                        event.getPosition(),
                        tipoJogoAtual
                )
        );

        recalcularValorApostaAtual();
    }

    protected void consumirEventoUnico() {

       updateState(teimosinhaReducer::consumirEvento);
    }

    protected void valorApostaAtualizado() {

        updateState(teimosinhaReducer::valorAtualizado);
    }
    protected void handleComoJogar() {
        updateState(
                state ->
                        state.copy().
                                setMensagemComoJogar(obterTextoComoJogar(tipoJogoAtual, isEspecialAtual)).
                                setTitleComoJogar(obterTitleComoJogar(tipoJogoAtual, isEspecialAtual)).
                                setExibirDialogComoJogar(true)
        );
    }

    public static String obterTextoComoJogar(ModalidadeEnum modalidade, boolean especial) {
        return new AccordionModel(modalidade, especial).getBodyText();
    }

    public static String obterTitleComoJogar(ModalidadeEnum modalidade, boolean especial) {
        return new AccordionModel(modalidade, especial).getTituloComoJogar(modalidade);
    }

    protected void consumirDialogComoJogar() {
        updateState(state -> state.copy().setExibirDialogComoJogar(false));
    }

    protected void carregarCarrinho() {

        publicarEstadoCarrinho(CarrinhoSingleton.getInstance().getCarrinho(), true);

        if (LoginSP.isLoginRealizado()) {
            carregarCarrinhoOnline();
            return;
        }

        carregarCarrinhoLocal();
    }

    protected void carregarCarrinhoOnline() {

        ServicoFactoryUtil.getApostaService().buscaCarrinho(new RequestListener<CarrinhoDTOResponse>() {

            @Override
            public void onResponse(CarrinhoDTOResponse response) {

                CarrinhoDTO carrinho = response != null ? response.getPayload() : null;

                if (carrinho == null) {
                    carrinho = new CarrinhoDTO();
                }

                adicionarApostasLocaisNoCarrinho(carrinho);

                CarrinhoSingleton.getInstance().setCarrinho(carrinho);

                publicarEstadoCarrinho(carrinho, false);
            }

            @Override
            public void onErrorResponse(VolleyError error) {

                carregarCarrinhoLocal();
            }
        });
    }

    protected void carregarCarrinhoLocal() {

        CarrinhoDTO carrinho = new CarrinhoDTO();

        carrinho.setApostas(getApostasLocais());

        CarrinhoSingleton.getInstance().setCarrinho(carrinho);

        publicarEstadoCarrinho(carrinho, false);
    }

    protected void adicionarApostasLocaisNoCarrinho(CarrinhoDTO carrinho) {

        List<IdentificaoDeUmaApostaDas8Modalidades> apostasLocais = getApostasLocais();

        if (apostasLocais == null || apostasLocais.isEmpty()) {
            return;
        }

        if (carrinho.getApostas() == null) {
            carrinho.setApostas(new ArrayList<>());
        }

        carrinho.getApostas().addAll(apostasLocais);
    }

    protected List<IdentificaoDeUmaApostaDas8Modalidades> getApostasLocais() {

        DBLoteriasCrud crud = new DBLoteriasCrud(getApplication());

        return crud.readAllIdentificaoDeUmaApostaDas8Modalidades();
    }
    private void handleSolicitarSelecaoTimeCoracao() {
        updateState(surpresinhaReducer::abrirSelecaoTimeCoracaoSurpresinha
        );
    }
    protected void publicarEstadoCarrinho(
            CarrinhoDTO carrinho,
            boolean carregando
    ) {

        updateState(state ->
                carrinhoReducer.publicarEstadoCarrinho(
                        state,
                        carrinho,
                        carregando
                )
        );
    }

    protected void handleCartelaAlterada(
            SimulaUiEvent.CartelaAlterada event
    ) {

        updateState(state ->
                fluxoApostaReducer.cartelaAlterada(
                        state,
                        event
                )
        );
    }


    protected void handleValorApostaAlterado(SimulaUiEvent.ValorApostaAlterado event) {

        BigDecimal valor = event.getValor();

        updateState(state -> state.copy().setValorAposta(valor).setValorGrande(valor != null && valor.doubleValue() > 999.99));
    }

    protected void handleCompletarApostaClicado(
            SimulaUiEvent.CompletarApostaClicado event
    ) {

        updateState(state ->
                completarApostaReducer.completarAposta(
                        state,
                        event
                )
        );
    }

    protected void handleAdicionarCarrinhoClicado(
            SimulaUiEvent.AdicionarCarrinhoClicado event
    ) {

        updateState(state ->
                adicionarCarrinhoReducer
                        .adicionarCarrinho(
                                state,
                                event
                        )
        );
    }

    protected void limparComandoCompletarAposta() {

        updateState(completarApostaReducer::limparComando);
    }
    protected void handleTelaInicializada(
            SimulaUiEvent.TelaInicializada event
    ) {

        tipoJogoAtual =
                event.getModalidade();

        parametroJogoAtual =
                event.getParametroSimulacao();

        isEspecialAtual =
                event.isEspecial();

        BarraTituloDTO barraTituloDTO =
                criarBarraTitulo(
                        parametroJogoAtual,
                        event.isEspecial()
                );

        boolean exibirAnimacao =
                false;

        int animacaoRes =
                0;

        float velocidadeAnimacao =
                1f;

        if (event.isEspecial() && tipoJogoAtual == ModalidadeEnum.LOTOFACIL) {

                exibirAnimacao =
                        true;

                animacaoRes =
                        R.raw.lotofacil_independencia;

                velocidadeAnimacao =
                        0.60f;

            }


        int qtdMinima =
                getQuantidadeMinimaSafe();

        int qtdConcurso =
                getPrimeiraTeimosinhaSafe();

        int qtdTrevosMinima =
                getQuantidadeTrevosMinimaSafe();

        BigDecimal valorInicialAposta =
                obterValorApostaPorParametros(
                        qtdMinima,
                        qtdConcurso
                );

        AnimacaoEffect animacaoEffect =
                null;

        if (exibirAnimacao) {

            animacaoEffect =
                    new AnimacaoEffect.Exibir(
                            animacaoRes,
                            velocidadeAnimacao
                    );
        }

        String textoBotaoQuantidadeNumeros =
                qtdMinima > 0
                        ? montarTextoQuantidadeNumeros(
                        qtdMinima
                )
                        : "";

        updateState(
                telaInicializadaReducer.criarEstadoInicializado(
                        tipoJogoAtual,
                        event.isEspecial(),
                        barraTituloDTO,
                        valorInicialAposta,
                        animacaoEffect,
                        qtdMinima,
                        qtdConcurso,
                        qtdTrevosMinima,
                        textoBotaoQuantidadeNumeros
                )
        );
    }
    private int getPrimeiraTeimosinhaSafe() {

        if (parametroJogoAtual == null
                || parametroJogoAtual.getTeimosinhas() == null
                || parametroJogoAtual.getTeimosinhas().isEmpty()) {
            return 0;
        }

        Integer primeiraTeimosinha =
                parametroJogoAtual
                        .getTeimosinhas()
                        .get(0);

        return primeiraTeimosinha != null
                ? primeiraTeimosinha
                : 0;
    }

    private int getQuantidadeTrevosMinimaSafe() {

        if (parametroJogoAtual == null
                || parametroJogoAtual.getTrevos() == null
                || parametroJogoAtual
                .getTrevos()
                .getQtdMinima() == null) {
            return 0;
        }

        return parametroJogoAtual
                .getTrevos()
                .getQtdMinima();
    }
    private void handleAdicionarSurpresinhaCarrinho(
            SimulaUiEvent.AdicionarSurpresinhaCarrinho event
    ) {

        SimulaUiState state =
                getCurrentState();

        IncluirSurpresinhaDTO dto =
                surpresinhaCarrinhoReducer.criar(
                        state,
                        parametroJogoAtual,
                        tipoJogoAtual,
                        event.isEspelhoLotomania()
                );

        if (dto == null) {
            return;
        }
        ModalidadeEnum tipoJogoAnalytics =
                tipoJogoAtual != null
                        ? tipoJogoAtual
                        : state.getTipoJogo();

        updateState(
                atual -> atual.copy()
                        .setComandoCarrinho(
                                new CarrinhoEffect
                                        .AdicionarSurpresinha(dto)
                        )
                        .setComandoAnalytics(
                                new AnalyticsEffect.AdicionarCarrinho(tipoJogoAnalytics)
                        )
        );
    }

    private void handleAdicionarCarrinhoSolicitado(
            SimulaUiEvent.AdicionarCarrinhoSolicitado event
    ) {
        if (event.isEscolhaTimeCoracaoSurpresinha()) {
            handleAdicionarSurpresinhaTimemaniaComTime(event);
            return;
        }

        apostaPendente = event.getAposta();
        barraTituloPendente = getBarraTituloSafe(event.getBarraTituloDTO());

        if (LoginSP.isLoginRealizado() && checaLimiteDiario(apostaPendente)) {
            publicarComando(new CarrinhoEffect.MostrarConfirmacaoLimiteDiario());
            return;
        }

        executarAdicionarCarrinhoPendente();
    }
    private void handleAdicionarSurpresinhaTimemaniaComTime(
            SimulaUiEvent.AdicionarCarrinhoSolicitado event
    ) {
        updateState(state -> {
            SimulaUiState stateComTime =
                    state.copy()
                            .setEquipeSelecionada(event.getEquipeSelecionada())

                            // sai do modo "escolha de time vindo da surpresinha"
                            .setTelaSelecaoTimeAtivado(false)
                            .setEscolhaTimeCoracaoSurpresinha(false)

                            // mantém a surpresinha ligada
                            .setSurpresinhaHabilitada(true)

                            // volta para etapa base depois do comando
                            .setEtapaAposta(EtapaAposta.NUMEROS)
                            .setExibirInfoEtapa(false)
                            .setTextoInfoEtapa("")

                            // estado visual do rodapé
                            .setBotaoAdicionarHabilitado(true)
                            .setBotaoCompletarVisivel(false)
                            .setBotaoCompletarHabilitado(false)
                            .setBotaoNextStepHabilitado(false)
                            .setBotaoLimparHabilitado(false)
                            .setMostrarBotaoLimparAposta(false)
                            .setMostrarSalvarAposta(false)
                            .setApostaFavoritada(false);

            IncluirSurpresinhaDTO dto =
                    surpresinhaCarrinhoReducer.criar(
                            stateComTime,
                            parametroJogoAtual,
                            tipoJogoAtual,
                            false
                    );

            if (dto == null) {
                return state;
            }

            return stateComTime
                    .copy()
                    .setComandoCarrinho(
                            new CarrinhoEffect.AdicionarSurpresinha(dto)
                    );
        });
    }

    protected void executarAdicionarCarrinhoPendente() {

        if (apostaPendente == null) {
            return;
        }

        if (barraTituloPendente == null) {
            barraTituloPendente = getBarraTituloSafe(null);
        }

        publicarComando(new CarrinhoEffect.ExecutarAdicionarCarrinhoEffect(apostaPendente, barraTituloPendente));
    }

    private boolean checaLimiteDiario(IdentificaoDeUmaApostaDas8Modalidades aposta) {

        if (CarrinhoSingleton.getInstance().getCarrinho() == null || CarrinhoSingleton.getInstance().getCarrinho().getValorTotal() == null || SessaoUsuario.getInstance().getParametrosSimulacao() == null || SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario() == null || aposta == null || aposta.getValor() == null) {

            return false;
        }

        BigDecimal valorAtualCarrinho = CarrinhoSingleton.getInstance().getCarrinho().getValorTotal();

        BigDecimal valorLimiteDiario = SessaoUsuario.getInstance().getParametrosSimulacao().getValorLimiteDiario();

        return valorAtualCarrinho.add(aposta.getValor()).compareTo(valorLimiteDiario) > 0;
    }

    private void publicarComando(CarrinhoEffect carrinhoEffect) {
        updateState(state -> state.copy().setComandoCarrinho(carrinhoEffect));
    }

    private void limparComandoCarrinho() {
        updateState(state -> state.copy().setComandoCarrinho(null));
    }

    protected void limparAposta() {

        SimulaUiState state =
                uiState.getValue();

        if (state == null) {
            return;
        }

        ModalidadeEnum tipoJogo =
                tipoJogoAtual != null
                        ? tipoJogoAtual
                        : state.getTipoJogo();

        int qtdNumerosAtual =
                state.getQtdDezenasPossiveisSelecionado() > 0
                        ? state.getQtdDezenasPossiveisSelecionado()
                        : getQuantidadeMinimaSafe();

        int qtdConcursoAtual =
                state.getQtdConcursoSelecionado();

        BigDecimal valorApostaAtual =
                obterValorApostaPorParametros(
                        qtdNumerosAtual,
                        qtdConcursoAtual
                );

        updateState(
                limparReducer.limparAposta(
                        state,
                        tipoJogo,
                        qtdNumerosAtual,
                        valorApostaAtual
                )
        );
    }

    protected BigDecimal obterValorApostaPorParametros(int qtdPrognosticos, int qtdConcursos) {

        if (parametroJogoAtual == null) {
            return BigDecimal.ZERO;
        }

        if (qtdPrognosticos <= 0) {
            qtdPrognosticos = getQuantidadeMinimaSafe();
        }

        BigDecimal valorBase = BigDecimal.ZERO;

        if (parametroJogoAtual.getValoresAposta() != null && !parametroJogoAtual.getValoresAposta().isEmpty()) {

            if (tipoJogoAtual == ModalidadeEnum.MAIS_MILIONARIA && parametroJogoAtual.getTrevos() != null && parametroJogoAtual.getTrevos().getQtdMinima() != null) {

                br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO valorAposta = parametroJogoAtual.getValorApostaBy(qtdPrognosticos, parametroJogoAtual.getTrevos().getQtdMinima());

                if (valorAposta != null && valorAposta.getValor() != null) {

                    valorBase = valorAposta.getValor();
                }

            } else {

                for (br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO parametro : parametroJogoAtual.getValoresAposta()) {

                    if (parametro == null) {
                        continue;
                    }

                    if (parametro.getNumeroPrognosticos() != null && parametro.getNumeroPrognosticos() == qtdPrognosticos && parametro.getValor() != null) {

                        valorBase = parametro.getValor();
                        break;
                    }
                }
            }
        }

        if (valorBase.compareTo(BigDecimal.ZERO) == 0 && parametroJogoAtual.getValorApostaMinima() != null) {

            valorBase = parametroJogoAtual.getValorApostaMinima();
        }

        if (qtdConcursos > 0) {
            valorBase = valorBase.multiply(BigDecimal.valueOf(qtdConcursos));
        }

        return valorBase;
    }

    protected void limparFlagsDeRenderizacao() {

        updateState(limparReducer::limparFlagsDeRenderizacao);
    }

    protected BarraTituloDTO criarBarraTitulo(ParametroJogoDTO parametro, boolean especial) {

        BarraTituloDTO dto = new BarraTituloDTO();

        dto.setEspecial(especial);

        if (parametro != null && parametro.getConcurso() != null) {

            dto.setNumeroConcurso(String.valueOf(parametro.getConcurso().getNumero()));

            dto.setDataSorteio(formatarDataSorteio(parametro));
        }

        return dto;
    }

    protected String formatarDataSorteio(ParametroJogoDTO parametro) {

        try {

            if (parametro == null || parametro.getConcurso() == null || parametro.getConcurso().getDataHoraSorteio() == null) {

                return "";
            }

            DateFormat entrada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

            DateFormat saida = new SimpleDateFormat("dd/MM", Locale.getDefault());

            Date data = entrada.parse(parametro.getConcurso().getDataHoraSorteio());

            if (data == null) {
                return "";
            }

            return saida.format(data);

        } catch (Exception e) {
            return "";
        }
    }

    protected SimulaUiState getCurrentState() {

        SimulaUiState atual = uiState.getValue();

        if (atual == null) {
            return SimulaUiState.initial();
        }

        return atual;
    }

    protected void limparComandoAnalytics() {
       updateState(state ->
                state.copy()
                        .setComandoAnalytics(null)
        );
    }

    private BarraTituloDTO getBarraTituloSafe(BarraTituloDTO barraTituloDTO) {

        if (barraTituloDTO != null) {
            return barraTituloDTO;
        }

        SimulaUiState state = uiState.getValue();

        if (state != null && state.getBarraTituloDTO() != null) {

            return state.getBarraTituloDTO();
        }

        return new BarraTituloDTO();
    }

    private int getQuantidadeMinimaSafe() {

        if (parametroJogoAtual == null || parametroJogoAtual.getQuantidadeMinima() == null) {

            return 0;
        }

        return parametroJogoAtual.getQuantidadeMinima();
    }

    protected void recalcularValorApostaAtual() {
        updateState(state ->
                valorApostaReducer.recalcular(
                        state,
                        parametroJogoAtual,
                        tipoJogoAtual
                )
        );
    }
    private void handleVoltarParaSurpresinha() {
        updateState(
                surpresinhaReducer::voltarParaSurpresinha
        );
    }
    protected String obterTextoCompletar(
            SimulaUiState state
    ) {
        if (state == null) {
            return "Aposta aleatória";
        }

        switch (state.getEtapaAposta()) {

            case TIME_CORACAO:
                return "Time aleatório";

            case MES_SORTE:
                return "Mês aleatório";

            case TREVOS:

                int quantidadeTrevosSelecionados =
                        state.getTrevosSelecionados() != null
                                ? state.getTrevosSelecionados().size()
                                : 0;

                return quantidadeTrevosSelecionados
                        > 0
                        ? "Completar trevos"
                        : "Trevos aleatórios";

            default:
                break;
        }

        boolean possuiDezenasSelecionadas =
                state.getDezenasSelecionadas() != null
                        && !state.getDezenasSelecionadas().isEmpty();

        boolean possuiSuperSeteSelecionado =
                state.getQtdTotalSelecionadosSuperSete() > 0;

        return possuiDezenasSelecionadas
                || possuiSuperSeteSelecionado
                ? "Completar jogo"
                : "Aposta aleatória";
    }

    protected void updateState(
            SimulaUiState novoEstado
    ) {
        if (novoEstado == null) {
            return;
        }

        SimulaUiState estadoFinal =
                novoEstado
                        .setTextoCompletar(
                                obterTextoCompletar(
                                        novoEstado
                                )
                        );

        uiState.setValue(
                estadoFinal
        );
    }

    protected void updateState(
            StateReducer reducer
    ) {
        if (reducer == null) {
            return;
        }

        updateState(
                reducer.reduce(
                        getCurrentState()
                )
        );
    }

}
