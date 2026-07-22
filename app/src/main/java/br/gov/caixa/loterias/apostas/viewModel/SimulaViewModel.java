package br.gov.caixa.loterias.apostas.viewModel;

import static br.gov.caixa.loterias.apostas.utils.Utils.isErroNegocial;

import android.app.Application;
import android.util.Log;

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
import br.gov.caixa.loterias.apostas.effect.AnimacaoEffect;
import br.gov.caixa.loterias.apostas.effect.CarrinhoEffect;
import br.gov.caixa.loterias.apostas.effect.CompletarApostaEffect;
import br.gov.caixa.loterias.apostas.effect.FavoritarApostaEffect;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.CarrinhoSingleton;
import br.gov.caixa.loterias.apostas.model.dao.DBLoteriasCrud;
import br.gov.caixa.loterias.apostas.model.model.AccordionModel;
import br.gov.caixa.loterias.apostas.model.sp.LoginSP;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;
import br.gov.caixa.loterias.apostas.utils.ServicoFactoryUtil;
import br.gov.caixa.loterias.apostas.viewModel.reducer.AdicionarCarrinhoReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.CarrinhoReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.CompletarApostaReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.FavoritarApostaReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.FluxoApostaReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.LimparReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.MesDaSorteReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.QuantidadeNumerosReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.SurpresinhaReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.TeimosinhaReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.TelaInicializadaReducer;
import br.gov.caixa.loterias.apostas.viewModel.reducer.TrevosReducer;

public class SimulaViewModel extends AndroidViewModel {

    private final MutableLiveData<SimulaUiState> uiState = new MutableLiveData<>(SimulaUiState.initial());
    private final TeimosinhaReducer teimosinhaReducer = new TeimosinhaReducer();
    private final FluxoApostaReducer fluxoApostaReducer = new FluxoApostaReducer();
    private final QuantidadeNumerosReducer quantidadeNumerosReducer = new QuantidadeNumerosReducer(fluxoApostaReducer);
    private final TrevosReducer trevosReducer = new TrevosReducer();
    private final CarrinhoReducer carrinhoReducer = new CarrinhoReducer();
    private final SurpresinhaReducer surpresinhaReducer = new SurpresinhaReducer();
    private final MesDaSorteReducer mesDaSorteReducer = new MesDaSorteReducer();
    private final FavoritarApostaReducer favoritarApostaReducer = new FavoritarApostaReducer();
    private final LimparReducer limparReducer = new LimparReducer();
    private final TelaInicializadaReducer telaInicializadaReducer = new TelaInicializadaReducer(fluxoApostaReducer);
    private final CompletarApostaReducer completarApostaReducer = new CompletarApostaReducer();
    private final AdicionarCarrinhoReducer adicionarCarrinhoReducer = new AdicionarCarrinhoReducer();
    private IdentificaoDeUmaApostaDas8Modalidades apostaPendente;
    private BarraTituloDTO barraTituloPendente;

    private ModalidadeEnum tipoJogoAtual;
    private ParametroJogoDTO parametroJogoAtual;
    private boolean isEspecialAtual;

    public SimulaViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<SimulaUiState> getUiState() {
        return uiState;
    }

    public void onEvent(SimulaUiEvent event) {

        if (event instanceof SimulaUiEvent.TelaInicializada) {

            handleTelaInicializada((SimulaUiEvent.TelaInicializada) event);

            return;
        }

        if (event instanceof SimulaUiEvent.AdicionarCarrinhoSolicitado) {

            handleAdicionarCarrinhoSolicitado((SimulaUiEvent.AdicionarCarrinhoSolicitado) event);

            return;
        }

        if (event instanceof SimulaUiEvent.ConfirmarAdicionarCarrinho) {

            executarAdicionarCarrinhoPendente();

            return;
        }

        if (event instanceof SimulaUiEvent.ComandoCarrinhoConsumido) {

            limparComandoCarrinho();

            return;
        }

        if (event instanceof SimulaUiEvent.LimparApostaSolicitado) {

            limparAposta();

            return;
        }

        if (event instanceof SimulaUiEvent.CompletarApostaClicado) {

            handleCompletarApostaClicado((SimulaUiEvent.CompletarApostaClicado) event);

            return;
        }
        if (event instanceof SimulaUiEvent.ToggleOutrosNumeros) {
            toggleOutrosNumeros();
            return;
        }

        if (event instanceof SimulaUiEvent.AdicionarCarrinhoClicado) {

            handleAdicionarCarrinhoClicado((SimulaUiEvent.AdicionarCarrinhoClicado) event);

            return;
        }

        if (event instanceof SimulaUiEvent.ComandoCompletarApostaConsumido) {

            limparComandoCompletarAposta();

            return;
        }

        if (event instanceof SimulaUiEvent.CartelaAlterada) {

            handleCartelaAlterada((SimulaUiEvent.CartelaAlterada) event);

            return;
        }
        if (event instanceof SimulaUiEvent.ValorApostaAlterado) {

            handleValorApostaAlterado((SimulaUiEvent.ValorApostaAlterado) event);

            return;
        }

        if (event instanceof SimulaUiEvent.CarregarCarrinho) {
            carregarCarrinho();
            return;
        }
        if (event instanceof SimulaUiEvent.ComoJogarClicado) {

            handleComoJogar();

            return;
        }

        if (event instanceof SimulaUiEvent.DialogComoJogarConsumido) {

            consumirDialogComoJogar();

            return;
        }

        if (event instanceof SimulaUiEvent.LimpezaApostaRenderizada) {

            limparFlagsDeRenderizacao();
        }
        if (event instanceof SimulaUiEvent.InicializarTeimosinhas) {
            inicializarTeimosinhas((SimulaUiEvent.InicializarTeimosinhas) event);
            return;
        }

        if (event instanceof SimulaUiEvent.ClicarTeimosinhas) {
            clicarTeimosinhas();
            return;
        }

        if (event instanceof SimulaUiEvent.ConfirmarTeimosinha) {
            confirmarTeimosinha((SimulaUiEvent.ConfirmarTeimosinha) event);
            return;
        }

        if (event instanceof SimulaUiEvent.ConsumirEventoUnico) {
            consumirEventoUnico();
            return;
        }

        if (event instanceof SimulaUiEvent.ValorApostaAtualizado) {
            valorApostaAtualizado();
            return;
        }
        if (event instanceof SimulaUiEvent.InicializarQuantidadeNumeros) {
            inicializarQuantidadeNumeros((SimulaUiEvent.InicializarQuantidadeNumeros) event);
            return;
        }

        if (event instanceof SimulaUiEvent.ClicarQuantidadeNumeros) {
            clicarQuantidadeNumeros();
            return;
        }

        if (event instanceof SimulaUiEvent.SelecionarQuantidadeNumeros) {
            selecionarQuantidadeNumeros((SimulaUiEvent.SelecionarQuantidadeNumeros) event);
            return;
        }

        if (event instanceof SimulaUiEvent.ConfirmarQuantidadeNumeros) {
            confirmarQuantidadeNumeros((SimulaUiEvent.ConfirmarQuantidadeNumeros) event);
            return;
        }

        if (event instanceof SimulaUiEvent.DialogQuantidadeNumerosConsumido) {
            consumirDialogQuantidadeNumeros();
            return;
        }

        if (event instanceof SimulaUiEvent.AvisoQuantidadeNumerosConsumido) {
            consumirAvisoQuantidadeNumeros();
            return;
        }

        if (event instanceof SimulaUiEvent.ValorApostaQuantidadeNumerosAtualizado) {
            valorApostaQuantidadeNumerosAtualizado();
            return;
        }
        if (event instanceof SimulaUiEvent.CarrinhoClicado) {
            abrirCarrinho();
            return;
        }

        if (event instanceof SimulaUiEvent.AbrirCarrinhoConsumido) {
            consumirAbrirCarrinho();
            return;
        }
        if (event instanceof SimulaUiEvent.AlterarSurpresinha) {

            handleAlterarSurpresinha(((SimulaUiEvent.AlterarSurpresinha) event).isHabilitada());

            return;
        }

        if (event instanceof SimulaUiEvent.ComandoTelaSimulaConsumido) {
            limparComandoTelaSimula();
            return;
        }
        if (event instanceof SimulaUiEvent.SurpresinhaAberta) {

            handleSurpresinhaAberta();
            return;
        }

        if (event instanceof SimulaUiEvent.SurpresinhaFechada) {

            handleSurpresinhaFechada();
            return;
        }
        if (event instanceof SimulaUiEvent.FavoritarApostaClicado) {
            abrirDialogFavoritarAposta();
            return;
        }

        if (event instanceof SimulaUiEvent.DialogFavoritarApostaConsumido) {
            consumirDialogFavoritarAposta();
            return;
        }
        if (event instanceof SimulaUiEvent.SalvarApostaFavoritaSolicitado) {
            handleSalvarApostaFavoritaSolicitado((SimulaUiEvent.SalvarApostaFavoritaSolicitado) event);
            return;
        }

        if (event instanceof SimulaUiEvent.ConfirmarSalvarApostaFavorita) {
            salvarApostaFavorita(((SimulaUiEvent.ConfirmarSalvarApostaFavorita) event).getApostaFavorita());
            return;
        }

        if (event instanceof SimulaUiEvent.ComandoFavoritarApostaConsumido) {
            limparComandoFavoritarAposta();
            return;
        }
        if (event instanceof SimulaUiEvent.NextStepClicado) {

            handleNextStepClicado((SimulaUiEvent.NextStepClicado) event);

            return;
        }

        if (event instanceof SimulaUiEvent.QuantidadeTrevosAlterada) {
            handleQuantidadeTrevosAlterada((SimulaUiEvent.QuantidadeTrevosAlterada) event);
            return;
        }
        if (event instanceof SimulaUiEvent.MesSorteAlterado) {
            handleMesSorteAlterado((SimulaUiEvent.MesSorteAlterado) event);
            return;
        }

        if (event instanceof SimulaUiEvent.TrevosAlterados) {
            handleTrevosAlterados((SimulaUiEvent.TrevosAlterados) event);
            return;
        }
        if (event instanceof SimulaUiEvent.ClicarQuantidadeTrevos) {
            clicarQuantidadeTrevos(
                    (SimulaUiEvent.ClicarQuantidadeTrevos) event
            );
            return;
        }

        if (event instanceof SimulaUiEvent.SelecionarQuantidadeTrevos) {
            selecionarQuantidadeTrevos(
                    (SimulaUiEvent.SelecionarQuantidadeTrevos) event
            );
            return;
        }

        if (event instanceof SimulaUiEvent.ConfirmarQuantidadeTrevos) {
            confirmarQuantidadeTrevos(
                    (SimulaUiEvent.ConfirmarQuantidadeTrevos) event
            );
            return;
        }

        if (event instanceof SimulaUiEvent.DialogQuantidadeTrevosConsumido) {
            consumirDialogQuantidadeTrevos();
            return;
        }
        if (event instanceof SimulaUiEvent.ComandoAnalyticsConsumido) {
            limparComandoAnalytics();
            return;
        }
        if (event instanceof
                SimulaUiEvent.ComandoAnimacaoConsumido) {

            limparComandoAnimacao();
            return;
        }
        if (event instanceof
                SimulaUiEvent.AvisoTrevosMaximoConsumido) {

            consumirAvisoTrevosMaximo();
            return;
        }
        if (event instanceof SimulaUiEvent.ToggleTrevo) {
            handleToggleTrevo(
                    (SimulaUiEvent.ToggleTrevo) event
            );
            return;
        }
        if(event instanceof
                SimulaUiEvent.AtualizarQuantidadeSurpresinhas){

            handleQuantidadeSurpresinhas(
                    (SimulaUiEvent
                            .AtualizarQuantidadeSurpresinhas)
                            event
            );
            return;
        }
        if(event instanceof
                SimulaUiEvent.AtualizarQuantidadeNumerosSurpresinha){

            handleQuantidadeNumerosSurpresinha(
                    (SimulaUiEvent
                            .AtualizarQuantidadeNumerosSurpresinha)
                            event
            );
            return;
        }
        if(event instanceof
                SimulaUiEvent.AtualizarQuantidadeTrevosSurpresinha){

            handleQuantidadeTrevosSurpresinha(
                    (SimulaUiEvent
                            .AtualizarQuantidadeTrevosSurpresinha)
                            event
            );
            return;
        }
        if (event instanceof
                SimulaUiEvent.SelecionarMesDaSorte) {

            handleSelecionarMesDaSorte(
                    (SimulaUiEvent
                            .SelecionarMesDaSorte)
                            event
            );

            return;
        }
        if (event instanceof
                SimulaUiEvent.LimparMesDaSorte) {

            handleLimparMesDaSorte();

            return;
        }
        if (event instanceof
                SimulaUiEvent.QuantidadeNumerosAtualizadaInternamente) {

            atualizarQuantidadeInterna(
                    (SimulaUiEvent.QuantidadeNumerosAtualizadaInternamente)
                            event
            );
            return;
        }
        if (event instanceof
                SimulaUiEvent.QtdConcursoAtualizadaInternamente) {

            handleQtdConcursoAtualizadaInternamente(
                    (SimulaUiEvent
                            .QtdConcursoAtualizadaInternamente)
                            event
            );
            return;
        }
        if(event instanceof SimulaUiEvent.InicializarParametrosAposta){
            inicializarParametrosAposta(
                    (SimulaUiEvent
                            .InicializarParametrosAposta)
                            event
            );
            return;
        }
        if(event instanceof
                SimulaUiEvent.SelecionarTimeCoracao){

            handleSelecionarTimeCoracao(
                    (SimulaUiEvent.SelecionarTimeCoracao)
                            event
            );
            return;
        }

    }
    private void handleSelecionarTimeCoracao(
            SimulaUiEvent.SelecionarTimeCoracao event
    ){
        uiState.setValue(
                getCurrentState()
                        .toBuilder()
                        .setEquipeSelecionada(
                                event.getEquipe()
                        )
                        .build()
        );
    }
    private void inicializarParametrosAposta(
            SimulaUiEvent.InicializarParametrosAposta event
    ) {
        uiState.setValue(
                getCurrentState()
                        .toBuilder()
                        .setQtdDezenasPossiveisSelecionado(
                                event.getQtdDezenas()
                        )
                        .setQtdConcursoSelecionado(
                                event.getQtdConcurso()
                        )
                        .build()
        );
    }
    private void handleQtdConcursoAtualizadaInternamente(
            SimulaUiEvent.QtdConcursoAtualizadaInternamente event
    ) {
        SimulaUiState atual =
                getCurrentState();

        uiState.setValue(
                atual.toBuilder()
                        .setQtdConcursoSelecionado(
                                event.getQuantidade()
                        )
                        .build()
        );
    }
    private void atualizarQuantidadeInterna(
            SimulaUiEvent
                    .QuantidadeNumerosAtualizadaInternamente event
    ) {
        uiState.setValue(
                getCurrentState()
                        .toBuilder()
                        .setQtdDezenasPossiveisSelecionado(
                                event.getQuantidade()
                        )
                        .build()
        );
    }
    private void handleLimparMesDaSorte() {

        uiState.setValue(
                mesDaSorteReducer.limparMesDaSorte(
                        getCurrentState()
                )
        );
    }

    private void handleQuantidadeSurpresinhas(
            SimulaUiEvent.AtualizarQuantidadeSurpresinhas event
    ) {

        uiState.setValue(
                surpresinhaReducer.atualizarQuantidadeSurpresinhas(
                        getCurrentState(),
                        event.getQuantidade(),
                        parametroJogoAtual,
                        tipoJogoAtual
                )
        );
    }

    private void handleQuantidadeTrevosSurpresinha(
            SimulaUiEvent.AtualizarQuantidadeTrevosSurpresinha event
    ) {

        uiState.setValue(
                surpresinhaReducer.atualizarQuantidadeTrevosSurpresinha(
                        getCurrentState(),
                        event.getQuantidade(),
                        parametroJogoAtual,
                        tipoJogoAtual
                )
        );
    }

    private void handleQuantidadeNumerosSurpresinha(
            SimulaUiEvent.AtualizarQuantidadeNumerosSurpresinha event
    ) {

        uiState.setValue(
                surpresinhaReducer.atualizarQuantidadeNumerosSurpresinha(
                        getCurrentState(),
                        event.getQuantidade(),
                        parametroJogoAtual,
                        tipoJogoAtual
                )
        );
    }
    private void recalcularValorSurpresinha() {

        uiState.setValue(
                surpresinhaReducer.recalcularValorSurpresinha(
                        getCurrentState(),
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

        uiState.setValue(
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

        uiState.setValue(
                trevosReducer.consumirAvisoTrevosMaximo(
                        getCurrentState()
                )
        );
    }

    private void selecionarQuantidadeTrevos(
            SimulaUiEvent.SelecionarQuantidadeTrevos event
    ) {

        uiState.setValue(
                trevosReducer.selecionarQuantidadeTrevos(
                        getCurrentState(),
                        event
                )
        );
    }

    private void consumirDialogQuantidadeTrevos() {

        uiState.setValue(
                trevosReducer.consumirDialogQuantidadeTrevos(
                        getCurrentState()
                )
        );
    }

    private void limparComandoAnimacao() {

        SimulaUiState atual =
                getCurrentState();

        uiState.setValue(
                atual.toBuilder()
                        .setComandoAnimacao(null)
                        .build()
        );
    }

    private void handleMesSorteAlterado(
            SimulaUiEvent.MesSorteAlterado event
    ) {

        uiState.setValue(
                mesDaSorteReducer.mesSorteAlterado(
                        getCurrentState(),
                        event
                )
        );
    }
    private void handleSelecionarMesDaSorte(
            SimulaUiEvent.SelecionarMesDaSorte event
    ) {

        uiState.setValue(
                mesDaSorteReducer.selecionarMesDaSorte(
                        getCurrentState(),
                        event
                )
        );
    }

    private void handleNextStepClicado(
            SimulaUiEvent.NextStepClicado event
    ) {

        uiState.setValue(
                fluxoApostaReducer.nextStepClicado(
                        getCurrentState(),
                        event,
                        tipoJogoAtual
                )
        );
    }

    private void handleTrevosAlterados(
            SimulaUiEvent.TrevosAlterados event
    ) {

        uiState.setValue(
                trevosReducer.trevosAlterados(
                        getCurrentState(),
                        event
                )
        );
    }

    private void handleQuantidadeTrevosAlterada(
            SimulaUiEvent.QuantidadeTrevosAlterada event
    ) {

        uiState.setValue(
                trevosReducer.quantidadeTrevosAlterada(
                        getCurrentState(),
                        event
                )
        );
    }

    private void clicarQuantidadeTrevos(
            SimulaUiEvent.ClicarQuantidadeTrevos event
    ) {

        uiState.setValue(
                trevosReducer.clicarQuantidadeTrevos(
                        getCurrentState(),
                        event
                )
        );
    }

    private void confirmarQuantidadeTrevos(
            SimulaUiEvent.ConfirmarQuantidadeTrevos event
    ) {

        uiState.setValue(
                trevosReducer.confirmarQuantidadeTrevos(
                        getCurrentState(),
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

        uiState.setValue(atual.toBuilder().setOpcaoOutrosNumerosSelecionada(novoValor).build());
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

                uiState.setValue(
                        favoritarApostaReducer
                                .sucessoFavoritar(
                                        getCurrentState()
                                )
                );

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

        uiState.setValue(
                favoritarApostaReducer.publicarComando(
                        getCurrentState(),
                        comando
                )
        );
    }

    private void limparComandoFavoritarAposta() {

        uiState.setValue(
                favoritarApostaReducer.limparComando(
                        getCurrentState()
                )
        );
    }

    private void abrirDialogFavoritarAposta() {

        uiState.setValue(
                favoritarApostaReducer.abrirDialog(
                        getCurrentState()
                )
        );
    }

    private void consumirDialogFavoritarAposta() {

        uiState.setValue(
                favoritarApostaReducer.consumirDialog(
                        getCurrentState()
                )
        );
    }

    private void handleSurpresinhaAberta() {

        uiState.setValue(
                surpresinhaReducer.surpresinhaAberta(
                        getCurrentState()
                )
        );
    }

    private void handleSurpresinhaFechada() {

        uiState.setValue(
                surpresinhaReducer.surpresinhaFechada(
                        getCurrentState()
                )
        );
    }

    private void handleAlterarSurpresinha(
            boolean habilitada
    ) {

        uiState.setValue(
                surpresinhaReducer.alterarSurpresinha(
                        getCurrentState(),
                        habilitada,
                        tipoJogoAtual
                )
        );
    }

    private void limparComandoTelaSimula() {

        SimulaUiState atual = getCurrentState();

        uiState.setValue(atual.toBuilder().setComandoTelaSimula(null).build());
    }

    private void abrirCarrinho() {

        uiState.setValue(
                carrinhoReducer.abrirCarrinho(
                        getCurrentState()
                )
        );
    }

    private void consumirAbrirCarrinho() {

        uiState.setValue(
                carrinhoReducer.consumirAbrirCarrinho(
                        getCurrentState()
                )
        );
    }

    private void inicializarQuantidadeNumeros(
            SimulaUiEvent.InicializarQuantidadeNumeros event
    ) {

        uiState.setValue(
                quantidadeNumerosReducer.inicializar(
                        getState(),
                        event
                )
        );
    }

    private void clicarQuantidadeNumeros() {

        uiState.setValue(
                quantidadeNumerosReducer.clicar(
                        getState()
                )
        );
    }

    private void selecionarQuantidadeNumeros(
            SimulaUiEvent.SelecionarQuantidadeNumeros event
    ) {

        uiState.setValue(
                quantidadeNumerosReducer.selecionar(
                        getState(),
                        event,
                        tipoJogoAtual
                )
        );
    }

    private void confirmarQuantidadeNumeros(
            SimulaUiEvent.ConfirmarQuantidadeNumeros event
    ) {

        SimulaUiState novoEstado =
                quantidadeNumerosReducer.confirmar(
                        getState(),
                        event,
                        tipoJogoAtual
                );

        uiState.setValue(
                novoEstado
        );

        if (novoEstado != null
                && novoEstado.isSurpresinhaHabilitada()) {
            recalcularValorSurpresinha();
        }
    }

    private String montarTextoQuantidadeNumeros(
            int qtdPrognosticos
    ) {

        return quantidadeNumerosReducer
                .montarTextoQuantidadeNumeros(
                        qtdPrognosticos
                );
    }

    private void consumirDialogQuantidadeNumeros() {

        uiState.setValue(
                quantidadeNumerosReducer.consumirDialog(
                        getState()
                )
        );
    }

    private void consumirAvisoQuantidadeNumeros() {

        uiState.setValue(
                quantidadeNumerosReducer.consumirAviso(
                        getState()
                )
        );
    }

    private void valorApostaQuantidadeNumerosAtualizado() {

        uiState.setValue(
                quantidadeNumerosReducer.valorApostaAtualizado(
                        getState()
                )
        );
    }

    private void inicializarTeimosinhas(SimulaUiEvent.InicializarTeimosinhas event) {
        uiState.setValue(
                teimosinhaReducer.inicializar(
                        getCurrentState(),
                        event.getQntTeimosinhas(),
                        event.getQntConcursos(),
                        event.getQntConcursoSelecionado(),
                        event.isEspecial(),
                        event.getTextoInicialTeimosinha()
                )
        );
    }

    private void clicarTeimosinhas() {
        uiState.setValue(
                teimosinhaReducer.clicar(
                        getCurrentState()
                )
        );
    }

    private void confirmarTeimosinha(SimulaUiEvent.ConfirmarTeimosinha event) {
        uiState.setValue(
                teimosinhaReducer.confirmar(
                        getCurrentState(),
                        event.getPosition(),
                        tipoJogoAtual
                )
        );
    }

    private void consumirEventoUnico() {

        uiState.setValue(
                teimosinhaReducer.consumirEvento(
                        getCurrentState()
                )
        );
    }

    private void valorApostaAtualizado() {

        uiState.setValue(
                teimosinhaReducer.valorAtualizado(
                        getCurrentState()
                )
        );
    }

    private SimulaUiState getState() {
        SimulaUiState state = uiState.getValue();
        return state != null ? state : SimulaUiState.initial();
    }

    private void handleComoJogar() {

        SimulaUiState atual = getCurrentState();

        uiState.setValue(atual.toBuilder().setMensagemComoJogar(obterTextoComoJogar(tipoJogoAtual, isEspecialAtual)).setTitleComoJogar(obterTitleComoJogar(tipoJogoAtual, isEspecialAtual)).setExibirDialogComoJogar(true).build());
    }

    public static String obterTextoComoJogar(ModalidadeEnum modalidade, boolean especial) {
        return new AccordionModel(modalidade, especial).getBodyText();
    }

    public static String obterTitleComoJogar(ModalidadeEnum modalidade, boolean especial) {
        return new AccordionModel(modalidade, especial).getTituloComoJogar(modalidade);
    }

    private void consumirDialogComoJogar() {

        SimulaUiState atual = getCurrentState();

        uiState.setValue(atual.toBuilder().setExibirDialogComoJogar(false).build());
    }

    private void carregarCarrinho() {

        publicarEstadoCarrinho(CarrinhoSingleton.getInstance().getCarrinho(), true);

        if (LoginSP.isLoginRealizado()) {
            carregarCarrinhoOnline();
            return;
        }

        carregarCarrinhoLocal();
    }

    private void carregarCarrinhoOnline() {

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

    private void carregarCarrinhoLocal() {

        CarrinhoDTO carrinho = new CarrinhoDTO();

        carrinho.setApostas(getApostasLocais());

        CarrinhoSingleton.getInstance().setCarrinho(carrinho);

        publicarEstadoCarrinho(carrinho, false);
    }

    private void adicionarApostasLocaisNoCarrinho(CarrinhoDTO carrinho) {

        List<IdentificaoDeUmaApostaDas8Modalidades> apostasLocais = getApostasLocais();

        if (apostasLocais == null || apostasLocais.isEmpty()) {
            return;
        }

        if (carrinho.getApostas() == null) {
            carrinho.setApostas(new ArrayList<IdentificaoDeUmaApostaDas8Modalidades>());
        }

        carrinho.getApostas().addAll(apostasLocais);
    }

    private List<IdentificaoDeUmaApostaDas8Modalidades> getApostasLocais() {

        DBLoteriasCrud crud = new DBLoteriasCrud(getApplication());

        return crud.readAllIdentificaoDeUmaApostaDas8Modalidades();
    }

    private void publicarEstadoCarrinho(
            CarrinhoDTO carrinho,
            boolean carregando
    ) {

        uiState.setValue(
                carrinhoReducer.publicarEstadoCarrinho(
                        getCurrentState(),
                        carrinho,
                        carregando
                )
        );
    }

    private void handleCartelaAlterada(
            SimulaUiEvent.CartelaAlterada event
    ) {

        uiState.setValue(
                fluxoApostaReducer.cartelaAlterada(
                        getCurrentState(),
                        event
                )
        );
    }


    private void handleValorApostaAlterado(SimulaUiEvent.ValorApostaAlterado event) {

        BigDecimal valor = event.getValor();

        uiState.setValue(getCurrentState().toBuilder().setValorAposta(valor).setValorGrande(valor != null && valor.doubleValue() > 999.99).build());
    }

    private boolean isNumerosCompletos(int qtdSelecionadas, int qtdPossiveis) {

        return qtdSelecionadas == qtdPossiveis;
    }

    private void handleCompletarApostaClicado(
            SimulaUiEvent.CompletarApostaClicado event
    ) {

        uiState.setValue(
                completarApostaReducer.completarAposta(
                        getCurrentState(),
                        event
                )
        );
    }

    private void atualizarEtapaAposta() {

        SimulaUiState atual = getCurrentState();

        uiState.setValue(atual.toBuilder().setBotaoNextStepHabilitado(false).setEtapaAposta(EtapaAposta.PRONTA).build());
    }

    private void handleAdicionarCarrinhoClicado(
            SimulaUiEvent.AdicionarCarrinhoClicado event
    ) {

        uiState.setValue(
                adicionarCarrinhoReducer
                        .adicionarCarrinho(
                                getCurrentState(),
                                event
                        )
        );
    }

    private void limparComandoCompletarAposta() {

        uiState.setValue(
                completarApostaReducer.limparComando(
                        getCurrentState()
                )
        );
    }
    private void handleTelaInicializada(
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

        if (event.isEspecial()) {

            if (tipoJogoAtual == ModalidadeEnum.LOTECA) {

                exibirAnimacao =
                        true;

                animacaoRes =
                        R.raw.copa_loteca_3;

                velocidadeAnimacao =
                        1.75f;

            } else if (tipoJogoAtual == ModalidadeEnum.QUINA) {

                exibirAnimacao =
                        true;

                animacaoRes =
                        R.raw.quina_sao_joao_1;

                velocidadeAnimacao =
                        1.5f;
            }
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

        uiState.setValue(
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

    private void handleAdicionarCarrinhoSolicitado(SimulaUiEvent.AdicionarCarrinhoSolicitado event) {

        if (event.isEscolhaTimeCoracaoSurpresinha()) {

            publicarComando(new CarrinhoEffect.AdicionarTimeSurpresa(event.getEquipeSelecionada()));

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

    private void executarAdicionarCarrinhoPendente() {

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

        SimulaUiState atual = getCurrentState();

        uiState.setValue(atual.toBuilder().setComandoCarrinho(carrinhoEffect).build());
    }

    private void limparComandoCarrinho() {

        SimulaUiState atual = uiState.getValue();

        if (atual == null) {
            return;
        }

        uiState.setValue(atual.toBuilder().setComandoCarrinho(null).build());
    }

    private void limparAposta() {

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

        uiState.setValue(
                limparReducer.limparAposta(
                        state,
                        tipoJogo,
                        qtdNumerosAtual,
                        valorApostaAtual
                )
        );
    }

    private BigDecimal obterValorApostaPorParametros(int qtdPrognosticos, int qtdConcursos) {

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

    private boolean isValorGrande(BigDecimal valor) {

        return valor != null && valor.doubleValue() > 999.99;
    }


    private void limparFlagsDeRenderizacao() {

        uiState.setValue(
                limparReducer.limparFlagsDeRenderizacao(
                        uiState.getValue()
                )
        );
    }

    private BarraTituloDTO criarBarraTitulo(ParametroJogoDTO parametro, boolean especial) {

        BarraTituloDTO dto = new BarraTituloDTO();

        dto.setEspecial(especial);

        if (parametro != null && parametro.getConcurso() != null) {

            dto.setNumeroConcurso(String.valueOf(parametro.getConcurso().getNumero()));

            dto.setDataSorteio(formatarDataSorteio(parametro));
        }

        return dto;
    }

    private String formatarDataSorteio(ParametroJogoDTO parametro) {

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

    private SimulaUiState getCurrentState() {

        SimulaUiState atual = uiState.getValue();

        if (atual == null) {
            return SimulaUiState.initial();
        }

        return atual;
    }

    private void limparComandoAnalytics() {
        SimulaUiState atual = getCurrentState();

        uiState.setValue(
                atual.toBuilder()
                        .setComandoAnalytics(null)
                        .build()
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

}