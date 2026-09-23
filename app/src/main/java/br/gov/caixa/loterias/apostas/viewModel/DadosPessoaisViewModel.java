package br.gov.caixa.loterias.apostas.viewModel;

import static br.gov.caixa.loterias.apostas.utils.Utils.isErroNegocial;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.effect.DadosPessoaisEffect;
import br.gov.caixa.loterias.apostas.event.DadosPessoaisEvent;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorAlteracaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.model.DadosPessoaisModel;
import br.gov.caixa.loterias.apostas.state.DadosPessoaisState;

/**
 * ViewModel da tela de dados pessoais.
 *
 * Ponto de entrada unico: onEvent(DadosPessoaisEvent).
 * Sem SingleLiveEvent: os efeitos viajam em slots de comando dentro do
 * DadosPessoaisState e sao limpos pelos eventos ...Consumido.
 *
 * O CEP chega da View ja completo e apenas com digitos.
 */
public class DadosPessoaisViewModel extends ViewModel {

    private static final String ERRO_CEP_INVALIDO = "CEP_INVALIDO";
    private static final int TAMANHO_CEP = 8;
    private static final String APENAS_NUMEROS = "\\D";
    private static final int POSICAO_MASCULINO = 0;

    private final DadosPessoaisModel model;

    private final MutableLiveData<DadosPessoaisState> uiState = new MutableLiveData<>();

    private boolean carregamentoIniciado;

    public DadosPessoaisViewModel() {
        this(new DadosPessoaisModel());
    }

    public DadosPessoaisViewModel(DadosPessoaisModel model) {
        this.model = model;
        this.uiState.setValue(new DadosPessoaisState());
    }

    public LiveData<DadosPessoaisState> getUiState() {
        return uiState;
    }

    // ------------------------------------------------------------------
    // Ponto de entrada unico
    // ------------------------------------------------------------------

    public void onEvent(DadosPessoaisEvent event) {
        if (event == null) {
            return;
        }
        if (handleCicloEvents(event)) {
            return;
        }
        if (handleEdicaoEvents(event)) {
            return;
        }
        if (handleAcaoEvents(event)) {
            return;
        }
        handleComandoConsumidoEvents(event);
    }

    private boolean handleCicloEvents(DadosPessoaisEvent event) {
        if (event instanceof DadosPessoaisEvent.TelaIniciada) {
            carregarDados(false);
            return true;
        }
        if (event instanceof DadosPessoaisEvent.TelaRetomada) {
            carregarDados(true);
            return true;
        }
        return false;
    }

    private boolean handleEdicaoEvents(DadosPessoaisEvent event) {
        if (event instanceof DadosPessoaisEvent.CepAlterado cepAlterado) {
            handleCepAlterado(cepAlterado);
            return true;
        }
        if (event instanceof DadosPessoaisEvent.LimiteAlterado limiteAlterado) {
            handleLimiteAlterado(limiteAlterado);
            return true;
        }
        if (event instanceof DadosPessoaisEvent.SexoSelecionado sexoSelecionado) {
            handleSexoSelecionado(sexoSelecionado);
            return true;
        }
        if (event instanceof DadosPessoaisEvent.NotificacaoAlterada notificacaoAlterada) {
            handleNotificacaoAlterada(notificacaoAlterada);
            return true;
        }
        return false;
    }

    private boolean handleAcaoEvents(DadosPessoaisEvent event) {
        if (event instanceof DadosPessoaisEvent.NaoSeiCepClicado) {
            publicarComandoDialog(new DadosPessoaisEffect.ConfirmarSaidaParaCorreios());
            return true;
        }
        if (event instanceof DadosPessoaisEvent.RedirecionamentoCorreiosConfirmado) {
            publicarComandoNavegacao(new DadosPessoaisEffect.AbrirSiteCorreios());
            return true;
        }
        if (event instanceof DadosPessoaisEvent.RecalcularLimiteClicado) {
            recalcularLimite();
            return true;
        }
        if (event instanceof DadosPessoaisEvent.AtualizarClicado) {
            salvar();
            return true;
        }
        if (event instanceof DadosPessoaisEvent.VoltarClicado) {
            publicarComandoNavegacao(new DadosPessoaisEffect.FecharTela());
            return true;
        }
        return false;
    }

    private void handleComandoConsumidoEvents(DadosPessoaisEvent event) {
        if (event instanceof DadosPessoaisEvent.ComandoRedeConsumido) {
            limparComandoRede();
        } else if (event instanceof DadosPessoaisEvent.ComandoDialogConsumido) {
            limparComandoDialog();
        } else if (event instanceof DadosPessoaisEvent.ComandoNavegacaoConsumido) {
            limparComandoNavegacao();
        } else if (event instanceof DadosPessoaisEvent.ComandoAnalyticsConsumido) {
            limparComandoAnalytics();
        }
    }

    // ------------------------------------------------------------------
    // Carregamento inicial
    // ------------------------------------------------------------------

    private void carregarDados(boolean forcarCarregamento) {
        if (carregamentoIniciado && !forcarCarregamento) {
            return;
        }
        carregamentoIniciado = true;
        atualizarCarregamento();

        model.carregarDadosUsuario(new DadosPessoaisModel.Callback<>() {
            @Override
            public void onSuccess(ApostadorDTOResponse response) {
                if (response == null || response.getPayload() == null) {
                    finalizarCarregamentoComFalhaInterna();
                    return;
                }
                ApostadorDTO apostador = response.getPayload();
                DadosPessoaisState state = getCurrentState();
                state.setApostador(apostador);
                state.setMunicipioSelecionado(apostador.getMunicipioId());
                state.setLimiteDigitado(apostador.getLimiteDiario());
                state.setCep(apostador.getCep());
                state.setAceitaNoticias(Boolean.TRUE.equals(apostador.getAceitaReceberNoticias()));
                state.setPosicaoSexo(resolverPosicaoSexo(apostador));
                state.setErroCep(null);
                state.setComandoRede(criarComandoRedirect(response.getRedirect()));
                state.setComandoAnalytics(new DadosPessoaisEffect.RegistrarEvento(
                        DadosPessoaisEffect.RegistrarEvento.Tipo.ENTROU_EDITAR_CADASTRO
                ));
                validarLimiteAtual(state, apostador);
                updateState(state);

                carregarLocalizacao(apostador);
            }

            @Override
            public void onError(VolleyError error) {
                finalizarCarregamentoComErro(error);
            }
        });
    }

    private int resolverPosicaoSexo(ApostadorDTO apostador) {
        return ApostadorDTO.SexoEnum.F.equals(apostador.getSexo()) ? 1 : POSICAO_MASCULINO;
    }

    private void carregarLocalizacao(ApostadorDTO apostador) {
        if (temUfValida(apostador)) {
            finalizarCarregamentoInicial();
            return;
        }
        model.carregarUfs(new DadosPessoaisModel.Callback<>() {
            @Override
            public void onSuccess(UnidadeFederacaoDTOResponse response) {
                preencherUf(response, apostador);
                carregarMunicipios(apostador);
            }

            @Override
            public void onError(VolleyError error) {
                finalizarCarregamentoComErro(error);
            }
        });
    }

    private boolean temUfValida(ApostadorDTO apostador) {
        return apostador.getMunicipioId() == null
                || apostador.getMunicipioId().getIdUF() == null;
    }

    private void preencherUf(UnidadeFederacaoDTOResponse response, ApostadorDTO apostador) {
        if (response == null || response.getPayload() == null || temUfValida(apostador)) {
            return;
        }
        for (UnidadeFederacaoDTO item : response.getPayload()) {
            if (item == null || item.getId() == null) {
                continue;
            }
            if (item.getId().equals(apostador.getMunicipioId().getIdUF())) {
                getCurrentState().setUf(item.getSigla());
                return;
            }
        }
    }

    private void carregarMunicipios(ApostadorDTO apostador) {
        if (temUfValida(apostador)) {
            finalizarCarregamentoInicial();
            return;
        }
        String idUf = apostador.getMunicipioId().getIdUF().toString();
        model.carregarMunicipios(idUf, new DadosPessoaisModel.Callback<>() {
            @Override
            public void onSuccess(MunicipioDTOResponse response) {
                preencherMunicipio(response, apostador);
                if (response != null && response.getRedirect() != null) {
                    getCurrentState().setComandoRede(
                            criarComandoRedirect(response.getRedirect())
                    );
                }
                finalizarCarregamentoInicial();
            }

            @Override
            public void onError(VolleyError error) {
                finalizarCarregamentoComErro(error);
            }
        });
    }

    private void preencherMunicipio(MunicipioDTOResponse response, ApostadorDTO apostador) {
        if (response == null || response.getPayload() == null) {
            return;
        }
        if (apostador.getMunicipioId() == null || apostador.getMunicipioId().getNumero() == null) {
            return;
        }
        List<MunicipioDTO> municipios = response.getPayload();
        for (MunicipioDTO item : municipios) {
            if (item == null || item.getId() == null || item.getId().getNumero() == null) {
                continue;
            }
            if (item.getId().getNumero().equals(apostador.getMunicipioId().getNumero())) {
                getCurrentState().setMunicipio(item.getNome());
                return;
            }
        }
    }

    private void finalizarCarregamentoInicial() {
        carregamentoIniciado = false;
        DadosPessoaisState state = getCurrentState();
        state.setCarregando(false);
        state.setDadosCarregados(true);
        updateState(state);
    }

    // ------------------------------------------------------------------
    // CEP
    // ------------------------------------------------------------------

    /**
     * A View so envia CEP completo e apenas com digitos. O erro anterior e
     * limpo imediatamente, antes da consulta, para que o vermelho saia da
     * tela assim que o usuario informa um novo CEP.
     */
    private void handleCepAlterado(DadosPessoaisEvent.CepAlterado event) {
        String cep = event.getCep();
        if (cep == null || cep.length() != TAMANHO_CEP) {
            return;
        }
        DadosPessoaisState state = getCurrentState();
        state.setCep(cep);
        state.setErroCep(null);
        updateState(state);

        buscarCep(cep);
    }

    private void buscarCep(String cep) {
        atualizarCarregamento();
        model.buscarBairroPorCep(cep, new DadosPessoaisModel.Callback<>() {
            @Override
            public void onSuccess(BairroDTOResponse response) {
                DadosPessoaisState state = getCurrentState();
                state.setCarregando(false);

                if (response == null
                        || response.getPayload() == null
                        || response.getPayload().isEmpty()) {
                    definirCepInvalido(state);
                    return;
                }
                BairroDTO bairro = response.getPayload().get(0);
                if (bairro == null
                        || bairro.getMunicipio() == null
                        || bairro.getMunicipio().getUf() == null
                        || bairro.getMunicipio().getId() == null) {
                    definirCepInvalido(state);
                    return;
                }
                state.setUf(bairro.getMunicipio().getUf().getSigla());
                state.setMunicipio(bairro.getMunicipio().getNome());
                state.setMunicipioSelecionado(bairro.getMunicipio().getId());
                state.setErroCep(null);
                state.setComandoRede(criarComandoRedirect(response.getRedirect()));
                updateState(state);
            }

            @Override
            public void onError(VolleyError error) {
                DadosPessoaisState state = getCurrentState();
                if (isErroNegocial(error)) {
                    definirCepInvalido(state);
                    return;
                }
                state.setCarregando(false);
                state.setComandoRede(new DadosPessoaisEffect.ErroDeRede(error));
                updateState(state);
            }
        });
    }

    private void definirCepInvalido(DadosPessoaisState state) {
        limparLocalizacao(state);
        state.setCarregando(false);
        state.setErroCep(ERRO_CEP_INVALIDO);
        updateState(state);
    }

    // ------------------------------------------------------------------
    // Limite
    // ------------------------------------------------------------------

    private void handleLimiteAlterado(DadosPessoaisEvent.LimiteAlterado event) {
        DadosPessoaisState state = getCurrentState();
        ApostadorDTO apostador = state.getApostador();
        BigDecimal limite = converterLimite(event.getLimiteFormatado());
        if (apostador == null || limite == null) {
            return;
        }
        state.setLimiteDigitado(limite);
        validarLimiteAtual(state, apostador);
        updateState(state);
    }

    private BigDecimal converterLimite(String limiteFormatado) {
        if (limiteFormatado == null) {
            return null;
        }
        String apenasDigitos = limiteFormatado.replaceAll(APENAS_NUMEROS, "");
        if (apenasDigitos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(apenasDigitos).movePointLeft(2);
    }

    private void validarLimiteAtual(DadosPessoaisState state, ApostadorDTO apostador) {
        BigDecimal limite = state.getLimiteDigitado();
        BigDecimal limiteMinimo = apostador.getLimiteMinimoDiario();
        BigDecimal limiteMaximo = apostador.getLimiteDiarioAutorizado();

        if (limite == null || limiteMinimo == null || limiteMaximo == null) {
            state.setLimiteMenorQueMinimo(false);
            state.setLimiteMaiorQueMaximo(false);
            return;
        }
        state.setLimiteMenorQueMinimo(limite.compareTo(limiteMinimo) < 0);
        state.setLimiteMaiorQueMaximo(limite.compareTo(limiteMaximo) > 0);
    }

    private void recalcularLimite() {
        atualizarCarregamento();
        model.recalcularLimite(new DadosPessoaisModel.Callback<>() {
            @Override
            public void onSuccess(ApostadorDTOResponse response) {
                DadosPessoaisState state = getCurrentState();
                state.setCarregando(false);
                if (response != null && response.getPayload() != null) {
                    ApostadorDTO apostadorAtualizado = response.getPayload();
                    state.setApostador(apostadorAtualizado);
                    validarLimiteAtual(state, apostadorAtualizado);
                }
                state.setComandoAnalytics(new DadosPessoaisEffect.RegistrarEvento(
                        DadosPessoaisEffect.RegistrarEvento.Tipo.ENTROU_RECALCULAR_LIMITE
                ));
                if (response != null && response.getRedirect() != null) {
                    state.setComandoRede(criarComandoRedirect(response.getRedirect()));
                }
                updateState(state);
            }

            @Override
            public void onError(VolleyError error) {
                finalizarCarregamentoComErro(error);
            }
        });
    }

    // ------------------------------------------------------------------
    // Sexo e notificacao
    // ------------------------------------------------------------------

    private void handleSexoSelecionado(DadosPessoaisEvent.SexoSelecionado event) {
        DadosPessoaisState state = getCurrentState();
        if (state.getPosicaoSexo() == event.getPosicao()) {
            return;
        }
        state.setPosicaoSexo(event.getPosicao());
        updateState(state);
    }

    private void handleNotificacaoAlterada(DadosPessoaisEvent.NotificacaoAlterada event) {
        DadosPessoaisState state = getCurrentState();
        if (state.isAceitaNoticias() == event.isAceitaNoticias()) {
            return;
        }
        state.setAceitaNoticias(event.isAceitaNoticias());
        if (state.isDadosCarregados()) {
            state.setComandoDialog(
                    new DadosPessoaisEffect.MostrarAvisoNotificacao(event.isAceitaNoticias())
            );
        }
        updateState(state);
    }

    // ------------------------------------------------------------------
    // Salvar
    // ------------------------------------------------------------------

    private void salvar() {
        DadosPessoaisState state = getCurrentState();

        List<DadosPessoaisEffect.CampoObrigatorio> pendentes = validarCamposObrigatorios(state);
        if (!pendentes.isEmpty()) {
            publicarComandoDialog(
                    new DadosPessoaisEffect.CamposObrigatoriosPendentes(pendentes)
            );
            return;
        }
        if (!state.isLimiteValido() || state.isCarregando()) {
            return;
        }

        atualizarCarregamento();
        model.atualizarDadosUsuario(
                montarApostadorAlteracao(state),
                new DadosPessoaisModel.Callback<>() {
                    @Override
                    public void onSuccess(CadastrarApostadorDTOResponse response) {
                        DadosPessoaisState atual = getCurrentState();
                        atual.setCarregando(false);
                        atual.setComandoAnalytics(new DadosPessoaisEffect.RegistrarEvento(
                                DadosPessoaisEffect.RegistrarEvento.Tipo.EDICAO_CADASTRO_SUCESSO
                        ));
                        if (response != null && response.getRedirect() != null) {
                            atual.setComandoRede(criarComandoRedirect(response.getRedirect()));
                        } else {
                            atual.setComandoNavegacao(new DadosPessoaisEffect.CadastroAtualizado());
                        }
                        updateState(atual);
                    }

                    @Override
                    public void onError(VolleyError error) {
                        finalizarCarregamentoComErro(error);
                    }
                }
        );
    }

    private List<DadosPessoaisEffect.CampoObrigatorio> validarCamposObrigatorios(
            DadosPessoaisState state
    ) {
        List<DadosPessoaisEffect.CampoObrigatorio> pendentes = new ArrayList<>();
        String cep = state.getCep() == null
                ? ""
                : state.getCep().replaceAll(APENAS_NUMEROS, "");

        if (cep.length() != TAMANHO_CEP) {
            pendentes.add(DadosPessoaisEffect.CampoObrigatorio.CEP);
        }
        if (state.getUf() == null || state.getUf().trim().isEmpty()) {
            pendentes.add(DadosPessoaisEffect.CampoObrigatorio.UF);
        }
        if (state.getMunicipio() == null || state.getMunicipio().trim().isEmpty()
                || !state.isCepValido()) {
            pendentes.add(DadosPessoaisEffect.CampoObrigatorio.MUNICIPIO);
        }
        return pendentes;
    }

    private ApostadorAlteracaoDTO montarApostadorAlteracao(DadosPessoaisState state) {
        ApostadorAlteracaoDTO dto = new ApostadorAlteracaoDTO();
        dto.setCep(state.getCep().replaceAll(APENAS_NUMEROS, ""));
        dto.setMunicipioId(state.getMunicipioSelecionado());
        dto.setAceitaReceberNoticias(state.isAceitaNoticias());
        dto.setLimiteDiario(state.getLimiteDigitado());
        dto.setAlterandoLoterica(false);
        dto.setSexo(
                state.getPosicaoSexo() == POSICAO_MASCULINO
                        ? ApostadorAlteracaoDTO.SexoEnum.M
                        : ApostadorAlteracaoDTO.SexoEnum.F
        );
        return dto;
    }

    // ------------------------------------------------------------------
    // Comandos
    // ------------------------------------------------------------------

    private DadosPessoaisEffect criarComandoRedirect(RedirectEnum redirect) {
        return redirect == null ? null : new DadosPessoaisEffect.Redirecionar(redirect);
    }

    private void publicarComandoDialog(DadosPessoaisEffect comando) {
        DadosPessoaisState state = getCurrentState();
        state.setComandoDialog(comando);
        updateState(state);
    }

    private void publicarComandoNavegacao(DadosPessoaisEffect comando) {
        DadosPessoaisState state = getCurrentState();
        state.setComandoNavegacao(comando);
        updateState(state);
    }

    private void limparComandoRede() {
        DadosPessoaisState state = getCurrentState();
        if (state.getComandoRede() == null) {
            return;
        }
        state.setComandoRede(null);
        updateState(state);
    }

    private void limparComandoDialog() {
        DadosPessoaisState state = getCurrentState();
        if (state.getComandoDialog() == null) {
            return;
        }
        state.setComandoDialog(null);
        updateState(state);
    }

    private void limparComandoNavegacao() {
        DadosPessoaisState state = getCurrentState();
        if (state.getComandoNavegacao() == null) {
            return;
        }
        state.setComandoNavegacao(null);
        updateState(state);
    }

    private void limparComandoAnalytics() {
        DadosPessoaisState state = getCurrentState();
        if (state.getComandoAnalytics() == null) {
            return;
        }
        state.setComandoAnalytics(null);
        updateState(state);
    }

    // ------------------------------------------------------------------
    // Helpers de estado
    // ------------------------------------------------------------------

    private void limparLocalizacao(DadosPessoaisState state) {
        state.setUf("");
        state.setMunicipio("");
        state.setMunicipioSelecionado(null);
    }

    private void atualizarCarregamento() {
        DadosPessoaisState state = getCurrentState();
        state.setCarregando(true);
        updateState(state);
    }

    private void finalizarCarregamentoComErro(VolleyError error) {
        carregamentoIniciado = false;
        DadosPessoaisState state = getCurrentState();
        state.setCarregando(false);
        state.setComandoRede(new DadosPessoaisEffect.ErroDeRede(error));
        updateState(state);
    }

    private void finalizarCarregamentoComFalhaInterna() {
        carregamentoIniciado = false;
        DadosPessoaisState state = getCurrentState();
        state.setCarregando(false);
        state.setDadosCarregados(false);
        updateState(state);
    }

    private void updateState(DadosPessoaisState novoEstado) {
        if (novoEstado == null) {
            return;
        }
        uiState.setValue(novoEstado);
    }

    private DadosPessoaisState getCurrentState() {
        DadosPessoaisState atual = uiState.getValue();
        if (atual == null) {
            return new DadosPessoaisState();
        }
        return atual;
    }
}
