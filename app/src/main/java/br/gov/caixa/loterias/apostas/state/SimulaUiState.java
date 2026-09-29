package br.gov.caixa.loterias.apostas.state;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.effect.AnalyticsEffect;
import br.gov.caixa.loterias.apostas.effect.AnimacaoEffect;
import br.gov.caixa.loterias.apostas.effect.CarrinhoEffect;
import br.gov.caixa.loterias.apostas.effect.CompletarApostaEffect;
import br.gov.caixa.loterias.apostas.effect.FavoritarApostaEffect;
import br.gov.caixa.loterias.apostas.effect.ScreenSimulaEffect;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;

public class SimulaUiState {

    private BarraTituloDTO barraTituloDTO;

    private FavoritarApostaEffect favoritarApostaEffect;

    private CarrinhoEffect carrinhoEffect;
    private CompletarApostaEffect completarApostaEffect;

    private ModalidadeEnum tipoJogo;

    private List<Integer> dezenasSelecionadas;
    private ParametroEquipe equipeSelecionada;
    private boolean especial;

    private boolean telaSelecaoTimeAtivado;
    private boolean escolhaTimeCoracaoSurpresinha;

    private int qtdDezenasPossiveisSelecionado;
    private int qtdConcursoSelecionado;

    private boolean mostrarBotaoLimparAposta;
    private boolean mostrarSalvarAposta;

    private boolean limparNumerosCartela;
    private boolean limparTimeCoracao;
    private boolean limparSuperSete;
    private boolean limparLoteca;
    private boolean limparLotogol;
    private boolean limparOpcaoOutrosNumeros;

    private int qtdTotalSelecionadosSuperSete;
    private int palpitesLoteca;

    private int jogosLoteca;
    private int simplesLoteca;
    private int duplasLoteca;
    private int triplasLoteca;

    private boolean botaoFinalizarVisivel;
    private boolean botaoCompletarHabilitado;
    private boolean botaoAdicionarHabilitado;
    private boolean botaoNextStepHabilitado;
    private boolean botaoLimparHabilitado;

    private BigDecimal valorAposta;
    private boolean valorGrande;
    private BigDecimal valorCarrinho;
    private int quantidadeApostasCarrinho;
    private boolean carregandoCarrinho;
    private boolean exibirCarrinho;
    private CarrinhoDTO carrinho;
    private boolean exibirDialogComoJogar;
    private String mensagemComoJogar;
    private String titleComoJogar;
    private List<String> labelsTeimosinhas;
    private List<Integer> concursosTeimosinhas;
    private String textoBotaoTeimosinhas;
    private boolean exibirDialogTeimosinha;
    private boolean exibirDialogSemTeimosinha;
    private boolean atualizarValorApostaPorTeimosinha;
    private List<String> labelsQuantidadeNumeros;
    private List<Integer> qtdPrognosticosQuantidadeNumeros;

    private boolean exibirDialogQuantidadeNumeros;
    private boolean exibirAvisoQuantidadeNumerosMaxima;
    private boolean avisouQuantidadeNumerosMaxima;

    private int posicaoQuantidadeNumerosSelecionada;
    private String textoBotaoPrognosticosSelecionado;
    private String textoBotaoQuantidadeTrevos;


    private boolean atualizarValorApostaPorQuantidadeNumeros;
    private boolean resetarTrevosPorQuantidadeNumeros;
    private String textoSelecionados = "Selecione os números:";
    private String textoCompletar = "Aposta aleatória";

    private String textoEscolha = "Escolha";
    private boolean abrirCarrinho;
    private ScreenSimulaEffect screenSimulaEffect;
    private boolean surpresinhaHabilitada;
    private boolean exibirDialogFavoritarAposta;
    private boolean apostaFavoritada;
    private EtapaAposta etapaAposta;
    private boolean exibirOpcaoOutrosNumeros;
    private boolean opcaoOutrosNumerosSelecionada;
    private AnalyticsEffect analyticsEffect;
    private boolean exibirInfoEtapa;
    private String textoInfoEtapa;
    private AnimacaoEffect animacaoEffect;
    private boolean exibirDialogQuantidadeTrevos;
    private List<String> labelsQuantidadeTrevos;
    private List<ParametroValorApostaDTO> valoresTrevos;
    private int posicaoTrevosSelecionada;
    private String subtituloTrevos;
    private ParametroValorApostaDTO valorTrevosSelecionado;
    private boolean exibirAvisoTrevosMaximo;
    private boolean avisouTrevosMaximo;
    private boolean avisouAumentoPalpitesLoteca;
    private boolean avisoAumentoPalpitesLotecaJaExibido;
    private int quantidadeSurpresinhas;
    private int quantidadeNumerosSurpresinha;
    private int quantidadeTrevosSurpresinha;
    private List<Integer> trevosSelecionados;
    private int quantidadeTrevosSelecionada;
    private ParametroMesDeSorte mesSelecionado;
    private boolean possuiAlteracoesPendentes;

    /**
     * Cria um estado com os valores padrão (equivalente ao antigo {@code new Builder()}).
     */
    private SimulaUiState() {
        this.barraTituloDTO = new BarraTituloDTO();
        this.dezenasSelecionadas = new ArrayList<>();
        this.botaoFinalizarVisivel = true;
        this.botaoCompletarHabilitado = true;
        this.botaoAdicionarHabilitado = false;
        this.botaoNextStepHabilitado = false;
        this.botaoLimparHabilitado = false;
        this.valorCarrinho = BigDecimal.ZERO;
        this.quantidadeApostasCarrinho = 0;
        this.carregandoCarrinho = false;
        this.exibirCarrinho = false;
        this.carrinho = null;
        this.valorAposta = BigDecimal.ZERO;
        this.valorGrande = false;
        this.titleComoJogar = "";
        this.mensagemComoJogar = "";
        this.especial = false;
        this.labelsTeimosinhas = new ArrayList<>();
        this.concursosTeimosinhas = new ArrayList<>();
        this.textoBotaoTeimosinhas = "";
        this.exibirDialogTeimosinha = false;
        this.exibirDialogSemTeimosinha = false;
        this.atualizarValorApostaPorTeimosinha = false;
        this.labelsQuantidadeNumeros = new ArrayList<>();
        this.qtdPrognosticosQuantidadeNumeros = new ArrayList<>();
        this.qtdConcursoSelecionado = 0;
        this.exibirDialogQuantidadeNumeros = false;
        this.exibirAvisoQuantidadeNumerosMaxima = false;
        this.avisouQuantidadeNumerosMaxima = false;
        this.possuiAlteracoesPendentes = false;
        this.posicaoQuantidadeNumerosSelecionada = 0;
        this.textoBotaoPrognosticosSelecionado = "";
        this.textoBotaoQuantidadeTrevos = "2 trevos";
        this.screenSimulaEffect = null;
        this.surpresinhaHabilitada = false;
        this.exibirDialogFavoritarAposta = false;
        this.apostaFavoritada = false;
        this.etapaAposta = EtapaAposta.NUMEROS;
        this.atualizarValorApostaPorQuantidadeNumeros = false;
        this.resetarTrevosPorQuantidadeNumeros = false;
        this.analyticsEffect = null;
        this.exibirInfoEtapa = false;
        this.textoInfoEtapa = "1/2";
        this.exibirDialogQuantidadeTrevos = false;
        this.labelsQuantidadeTrevos = new ArrayList<>();
        this.valoresTrevos = new ArrayList<>();
        this.posicaoTrevosSelecionada = 0;
        this.subtituloTrevos = "";
        this.valorTrevosSelecionado = null;
        this.exibirAvisoTrevosMaximo = false;
        this.avisouTrevosMaximo = false;
        this.avisouAumentoPalpitesLoteca = false;
        this.avisoAumentoPalpitesLotecaJaExibido = false;
        this.trevosSelecionados = new ArrayList<>();
        this.quantidadeTrevosSelecionada = 0;
        this.quantidadeSurpresinhas = 0;
        this.qtdTotalSelecionadosSuperSete = 0;
        this.quantidadeNumerosSurpresinha = 0;
        this.mesSelecionado = null;
        this.quantidadeTrevosSurpresinha = 0;
        this.favoritarApostaEffect = null;
    }

    /**
     * Construtor de cópia (equivalente ao antigo {@code Builder(SimulaUiState state)}).
     */
    private SimulaUiState(SimulaUiState state) {
        this.barraTituloDTO = state.barraTituloDTO;
        this.exibirDialogFavoritarAposta = state.isExibirDialogFavoritarAposta();
        this.carrinhoEffect = state.carrinhoEffect;
        this.completarApostaEffect = state.completarApostaEffect;
        this.valorCarrinho = state.valorCarrinho;
        this.screenSimulaEffect = state.screenSimulaEffect;
        this.analyticsEffect = state.analyticsEffect;
        this.surpresinhaHabilitada = state.surpresinhaHabilitada;
        this.quantidadeApostasCarrinho = state.quantidadeApostasCarrinho;
        this.carregandoCarrinho = state.carregandoCarrinho;
        this.exibirCarrinho = state.exibirCarrinho;
        this.textoSelecionados = state.textoSelecionados;
        this.textoCompletar = state.textoCompletar;
        this.textoEscolha = state.textoEscolha;
        this.carrinho = state.carrinho;
        this.etapaAposta = state.etapaAposta;
        this.tipoJogo = state.tipoJogo;

        this.dezenasSelecionadas = state.dezenasSelecionadas != null
                ? new ArrayList<>(state.dezenasSelecionadas)
                : new ArrayList<>();

        this.equipeSelecionada = state.equipeSelecionada;

        this.telaSelecaoTimeAtivado = state.telaSelecaoTimeAtivado;
        this.escolhaTimeCoracaoSurpresinha = state.escolhaTimeCoracaoSurpresinha;

        this.qtdDezenasPossiveisSelecionado = state.qtdDezenasPossiveisSelecionado;
        this.qtdConcursoSelecionado = state.qtdConcursoSelecionado;
        this.exibirOpcaoOutrosNumeros =
                state.exibirOpcaoOutrosNumeros;

        this.opcaoOutrosNumerosSelecionada =
                state.opcaoOutrosNumerosSelecionada;

        this.mostrarBotaoLimparAposta = state.mostrarBotaoLimparAposta;
        this.mostrarSalvarAposta = state.mostrarSalvarAposta;

        this.limparNumerosCartela = state.limparNumerosCartela;
        this.limparTimeCoracao = state.limparTimeCoracao;
        this.limparSuperSete = state.limparSuperSete;
        this.limparLoteca = state.limparLoteca;
        this.limparLotogol = state.limparLotogol;
        this.abrirCarrinho = state.abrirCarrinho;
        this.limparOpcaoOutrosNumeros = state.limparOpcaoOutrosNumeros;

        this.qtdTotalSelecionadosSuperSete = state.qtdTotalSelecionadosSuperSete;

        this.palpitesLoteca = state.palpitesLoteca;
        this.jogosLoteca = state.jogosLoteca;
        this.simplesLoteca = state.simplesLoteca;
        this.duplasLoteca = state.duplasLoteca;
        this.triplasLoteca = state.triplasLoteca;

        this.botaoFinalizarVisivel = state.botaoFinalizarVisivel;

        this.botaoCompletarHabilitado = state.botaoCompletarHabilitado;
        this.botaoAdicionarHabilitado = state.botaoAdicionarHabilitado;
        this.botaoNextStepHabilitado = state.botaoNextStepHabilitado;
        this.botaoLimparHabilitado = state.botaoLimparHabilitado;

        this.valorAposta = state.valorAposta;
        this.valorGrande = state.valorGrande;
        this.exibirDialogComoJogar = state.exibirDialogComoJogar;
        this.mensagemComoJogar = state.mensagemComoJogar;
        this.titleComoJogar = state.titleComoJogar;
        this.especial = state.especial;
        this.labelsTeimosinhas = state.labelsTeimosinhas != null
                ? new ArrayList<>(state.labelsTeimosinhas)
                : new ArrayList<>();
        this.concursosTeimosinhas = state.concursosTeimosinhas != null
                ? new ArrayList<>(state.concursosTeimosinhas)
                : new ArrayList<>();
        this.textoBotaoTeimosinhas = state.textoBotaoTeimosinhas;
        this.exibirDialogTeimosinha = state.exibirDialogTeimosinha;
        this.exibirDialogSemTeimosinha = state.exibirDialogSemTeimosinha;
        this.atualizarValorApostaPorTeimosinha = state.atualizarValorApostaPorTeimosinha;
        this.labelsQuantidadeNumeros =
                state.labelsQuantidadeNumeros != null
                        ? new ArrayList<>(state.labelsQuantidadeNumeros)
                        : new ArrayList<>();

        this.qtdPrognosticosQuantidadeNumeros =
                state.qtdPrognosticosQuantidadeNumeros != null
                        ? new ArrayList<>(state.qtdPrognosticosQuantidadeNumeros)
                        : new ArrayList<>();

        this.exibirDialogQuantidadeNumeros =
                state.exibirDialogQuantidadeNumeros;

        this.exibirAvisoQuantidadeNumerosMaxima =
                state.exibirAvisoQuantidadeNumerosMaxima;

        this.avisouQuantidadeNumerosMaxima =
                state.avisouQuantidadeNumerosMaxima;

        this.posicaoQuantidadeNumerosSelecionada =
                state.posicaoQuantidadeNumerosSelecionada;

        this.textoBotaoPrognosticosSelecionado =
                state.textoBotaoPrognosticosSelecionado;
        this.textoBotaoQuantidadeTrevos = state.textoBotaoQuantidadeTrevos;

        this.atualizarValorApostaPorQuantidadeNumeros =
                state.atualizarValorApostaPorQuantidadeNumeros;

        this.resetarTrevosPorQuantidadeNumeros =
                state.resetarTrevosPorQuantidadeNumeros;
        this.apostaFavoritada = state.apostaFavoritada;
        this.exibirInfoEtapa = state.exibirInfoEtapa;
        this.textoInfoEtapa = state.textoInfoEtapa;
        this.animacaoEffect = state.animacaoEffect;
        this.exibirDialogQuantidadeTrevos =
                state.exibirDialogQuantidadeTrevos;

        this.labelsQuantidadeTrevos =
                state.labelsQuantidadeTrevos != null
                        ? new ArrayList<>(state.labelsQuantidadeTrevos)
                        : new ArrayList<>();

        this.valoresTrevos =
                state.valoresTrevos != null
                        ? new ArrayList<>(state.valoresTrevos)
                        : new ArrayList<>();

        this.trevosSelecionados =
                state.trevosSelecionados != null
                        ? new ArrayList<>(state.trevosSelecionados)
                        : new ArrayList<>();

        this.posicaoTrevosSelecionada =
                state.posicaoTrevosSelecionada;

        this.subtituloTrevos =
                state.subtituloTrevos;

        this.valorTrevosSelecionado =
                state.valorTrevosSelecionado;
        this.exibirAvisoTrevosMaximo = state.exibirAvisoTrevosMaximo;
        this.avisouTrevosMaximo = state.avisouTrevosMaximo;
        this.avisouAumentoPalpitesLoteca = state.avisouAumentoPalpitesLoteca;
        this.avisoAumentoPalpitesLotecaJaExibido = state.avisoAumentoPalpitesLotecaJaExibido;
        this.quantidadeTrevosSelecionada = state.quantidadeTrevosSelecionada;
        this.quantidadeSurpresinhas = state.quantidadeSurpresinhas;
        this.quantidadeNumerosSurpresinha = state.quantidadeNumerosSurpresinha;
        this.quantidadeTrevosSurpresinha = state.quantidadeTrevosSurpresinha;
        this.mesSelecionado = state.mesSelecionado;
        this.possuiAlteracoesPendentes = state.possuiAlteracoesPendentes;
        this.favoritarApostaEffect = state.favoritarApostaEffect;
    }

    /**
     * Retorna uma nova instância com os mesmos valores do estado atual,
     * permitindo aplicar setters sem afetar a instância original (que pode
     * já estar publicada em um {@code LiveData}).
     */
    public SimulaUiState copy() {
        return new SimulaUiState(this);
    }

    public static SimulaUiState initial() {
        return new SimulaUiState()
                .setBarraTituloDTO(new BarraTituloDTO())
                .setExibirDialogComoJogar(false)
                .setExibirDialogFavoritarAposta(false)
                .setApostaFavoritada(false)
                .setMostrarSalvarAposta(false)
                .setMesSelecionado(null)
                .setEtapaAposta(EtapaAposta.NUMEROS)
                .setComandoCarrinho(null)
                .setAbrirCarrinho(false)
                .setComandoFavoritarAposta(null)
                .setComandoTelaSimula(null)
                .setSurpresinhaHabilitada(false)
                .setComandoCompletarAposta(null)
                .setTipoJogo(null)
                .setDezenasSelecionadas(new ArrayList<>())
                .setEquipeSelecionada(null)
                .setTelaSelecaoTimeAtivado(false)
                .setEscolhaTimeCoracaoSurpresinha(false)
                .setQtdDezenasPossiveisSelecionado(0)
                .setQtdConcursoSelecionado(0)
                .setTextoSelecionados("Selecione os números:")
                .setTextoCompletar("Aposta aleatória")
                .setTextoEscolha("Escolha")
                .setMostrarBotaoLimparAposta(false)
                .setMostrarSalvarAposta(false)
                .setLimparNumerosCartela(false)
                .setLimparTimeCoracao(false)
                .setLimparSuperSete(false)
                .setLimparLoteca(false)
                .setLimparLotogol(false)
                .setLimparOpcaoOutrosNumeros(false)
                .setQtdTotalSelecionadosSuperSete(0)
                .setPalpitesLoteca(15)
                .setJogosLoteca(0)
                .setSimplesLoteca(0)
                .setDuplasLoteca(0)
                .setMensagemComoJogar("")
                .setTitleComoJogar("")
                .setTriplasLoteca(0)
                .setBotaoCompletarVisivel(true)
                .setBotaoCompletarHabilitado(true)
                .setBotaoAdicionarHabilitado(false)
                .setBotaoNextStepHabilitado(false)
                .setBotaoLimparHabilitado(false)
                .setTrevosSelecionados(new ArrayList<>())
                .setValorCarrinho(BigDecimal.ZERO)
                .setQuantidadeApostasCarrinho(0)
                .setCarregandoCarrinho(false)
                .setExibirCarrinho(false)
                .setPossuiAlteracoesPendentes(false)
                .setCarrinho(null)
                .setValorAposta(BigDecimal.ZERO)
                .setValorGrande(false)
                .setEspecial(false)
                .setLabelsTeimosinhas(new ArrayList<>())
                .setConcursosTeimosinhas(new ArrayList<>())
                .setTextoBotaoTeimosinhas("")
                .setExibirDialogTeimosinha(false)
                .setExibirDialogSemTeimosinha(false)
                .setAtualizarValorApostaPorTeimosinha(false)
                .setLabelsQuantidadeNumeros(new ArrayList<>())
                .setQtdPrognosticosQuantidadeNumeros(new ArrayList<>())
                .setExibirDialogQuantidadeNumeros(false)
                .setExibirAvisoQuantidadeNumerosMaxima(false)
                .setAvisouQuantidadeNumerosMaxima(false)
                .setPosicaoQuantidadeNumerosSelecionada(0)
                .setTextoBotaoPrognosticosSelecionado("")
                .setTextoBotaoQuantidadeTrevos("2 trevos")
                .setAtualizarValorApostaPorQuantidadeNumeros(false)
                .setResetarTrevosPorQuantidadeNumeros(false)
                .setExibirDialogQuantidadeTrevos(false)
                .setLabelsQuantidadeTrevos(new ArrayList<>())
                .setValoresTrevos(new ArrayList<>())
                .setPosicaoTrevosSelecionada(0)
                .setSubtituloTrevos("")
                .setQuantidadeTrevosSelecionada(0)
                .setValorTrevosSelecionado(null)
                .setQuantidadeSurpresinhas(0)
                .setQuantidadeTrevosSurpresinha(0)
                .setExibirInfoEtapa(false)
                .setTextoInfoEtapa("")
                .setQuantidadeNumerosSurpresinha(0)
                .setAvisouAumentoPalpitesLoteca(false)
                .setAvisoAumentoPalpitesLotecaJaExibido(false);
    }
    public List<Integer> getTrevosSelecionados(){return trevosSelecionados;}

    public String getTextoSelecionados() {
        return textoSelecionados;
    }
    public String getTextoCompletar() {
        return textoCompletar;
    }
    public String getTextoEscolha(){return textoEscolha;}
    public boolean isExibirDialogQuantidadeTrevos() {
        return exibirDialogQuantidadeTrevos;
    }

    public List<String> getLabelsQuantidadeTrevos() {
        return new ArrayList<>(labelsQuantidadeTrevos);
    }

    public List<ParametroValorApostaDTO> getValoresTrevos() {
        return new ArrayList<>(valoresTrevos);
    }

    public int getPosicaoTrevosSelecionada() {
        return posicaoTrevosSelecionada;
    }

    public String getSubtituloTrevos() {
        return subtituloTrevos;
    }
    public Boolean getPossuiAlteracoesPendentes(){return possuiAlteracoesPendentes;}

    public ParametroValorApostaDTO getValorTrevosSelecionado() {
        return valorTrevosSelecionado;
    }

    public boolean isEspecial() {
        return especial;
    }
    public ScreenSimulaEffect getComandoTelaSimula() {
        return screenSimulaEffect;
    }

    public boolean isSurpresinhaHabilitada() {
        return surpresinhaHabilitada;
    }

    public boolean isExibirDialogFavoritarAposta() {
        return exibirDialogFavoritarAposta;
    }

    public List<String> getLabelsTeimosinhas() {
        return new ArrayList<>(labelsTeimosinhas);
    }

    public List<Integer> getConcursosTeimosinhas() {
        return new ArrayList<>(concursosTeimosinhas);
    }

    public String getTextoBotaoTeimosinhas() {
        return textoBotaoTeimosinhas;
    }

    public boolean isExibirDialogTeimosinha() {
        return exibirDialogTeimosinha;
    }

    public boolean isAbrirCarrinho() {
        return abrirCarrinho;
    }

    public boolean isExibirDialogSemTeimosinha() {
        return exibirDialogSemTeimosinha;
    }

    public boolean isAtualizarValorApostaPorTeimosinha() {
        return atualizarValorApostaPorTeimosinha;
    }


    public BarraTituloDTO getBarraTituloDTO() {
        return barraTituloDTO;
    }
    public String getTextoInfoEtapa(){ return textoInfoEtapa;}
    public Boolean getExibirInfoEtapa(){return exibirInfoEtapa;}

    public boolean isExibirDialogComoJogar() {
        return exibirDialogComoJogar;
    }

    public AnalyticsEffect getComandoAnalytics() {
        return analyticsEffect;
    }
    public EtapaAposta getEtapaAposta() {
        return etapaAposta;
    }
    public BigDecimal getValorCarrinho() {
        return valorCarrinho;
    }

    public int getQuantidadeApostasCarrinho() {
        return quantidadeApostasCarrinho;
    }
    public boolean isApostaFavoritada() {
        return apostaFavoritada;
    }

    public CarrinhoDTO getCarrinho() {
        return carrinho;
    }

    public boolean isExibirOpcaoOutrosNumeros() {
        return exibirOpcaoOutrosNumeros;
    }

    public boolean isOpcaoOutrosNumerosSelecionada() {
        return opcaoOutrosNumerosSelecionada;
    }

    public CarrinhoEffect getComandoCarrinho() {
        return carrinhoEffect;
    }
    public FavoritarApostaEffect getComandoFavoritarAposta() {
        return favoritarApostaEffect;
    }


    public CompletarApostaEffect getComandoCompletarAposta() {
        return completarApostaEffect;
    }

    public ModalidadeEnum getTipoJogo() {
        return tipoJogo;
    }

    public List<Integer> getDezenasSelecionadas() {
        return new ArrayList<>(dezenasSelecionadas);
    }

    public ParametroEquipe getEquipeSelecionada() {
        return equipeSelecionada;
    }

    public boolean isTelaSelecaoTimeAtivado() {
        return telaSelecaoTimeAtivado;
    }

    public boolean isEscolhaTimeCoracaoSurpresinha() {
        return escolhaTimeCoracaoSurpresinha;
    }

    public int getQtdDezenasPossiveisSelecionado() {
        return qtdDezenasPossiveisSelecionado;
    }

    public int getQtdConcursoSelecionado() {
        return qtdConcursoSelecionado;
    }

    public boolean isMostrarBotaoLimparAposta() {
        return mostrarBotaoLimparAposta;
    }

    public boolean isMostrarSalvarAposta() {
        return mostrarSalvarAposta;
    }

    public boolean isLimparNumerosCartela() {
        return limparNumerosCartela;
    }

    public boolean isLimparTimeCoracao() {
        return limparTimeCoracao;
    }

    public boolean isLimparSuperSete() {
        return limparSuperSete;
    }

    public boolean isLimparLoteca() {
        return limparLoteca;
    }

    public boolean isLimparLotogol() {
        return limparLotogol;
    }

    public boolean isLimparOpcaoOutrosNumeros() {
        return limparOpcaoOutrosNumeros;
    }

    public int getQtdTotalSelecionadosSuperSete() {
        return qtdTotalSelecionadosSuperSete;
    }

    public int getJogosLoteca() {
        return jogosLoteca;
    }
    public int getPalpitesLoteca(){return palpitesLoteca;}

    public int getSimplesLoteca() {
        return simplesLoteca;
    }

    public int getDuplasLoteca() {
        return duplasLoteca;
    }

    public int getTriplasLoteca() {
        return triplasLoteca;
    }

    public boolean isBotaoFinalizarVisivel() {
        return botaoFinalizarVisivel;
    }

    public boolean isBotaoCompletarHabilitado() {
        return botaoCompletarHabilitado;
    }

    public boolean isBotaoAdicionarHabilitado() {
        return botaoAdicionarHabilitado;
    }
    public boolean isBotaoNextStepHabilitado(){return botaoNextStepHabilitado;}

    public boolean isBotaoLimparHabilitado() {
        return botaoLimparHabilitado;
    }

    public BigDecimal getValorAposta() {
        return valorAposta;
    }

    public boolean isValorGrande() {
        return valorGrande;
    }

    public String getMensagemComoJogar() {
        return mensagemComoJogar;
    }

    public String getTitleComoJogar() {
        return titleComoJogar;
    }

    public List<String> getLabelsQuantidadeNumeros() {
        return labelsQuantidadeNumeros;
    }

    public List<Integer> getQtdPrognosticosQuantidadeNumeros() {
        return qtdPrognosticosQuantidadeNumeros;
    }

    public boolean isExibirDialogQuantidadeNumeros() {
        return exibirDialogQuantidadeNumeros;
    }

    public boolean isExibirAvisoQuantidadeNumerosMaxima() {
        return exibirAvisoQuantidadeNumerosMaxima;
    }

    public boolean isAvisouQuantidadeNumerosMaxima() {
        return avisouQuantidadeNumerosMaxima;
    }

    public int getPosicaoQuantidadeNumerosSelecionada() {
        return posicaoQuantidadeNumerosSelecionada;
    }

    public String getTextoBotaoPrognosticosSelecionado() {
        return textoBotaoPrognosticosSelecionado;
    }
    public String getTextoBotaoQuantidadeTrevos() {
        return textoBotaoQuantidadeTrevos;
    }

    public boolean isAtualizarValorApostaPorQuantidadeNumeros() {
        return atualizarValorApostaPorQuantidadeNumeros;
    }

    public boolean isResetarTrevosPorQuantidadeNumeros() {
        return resetarTrevosPorQuantidadeNumeros;
    }
    public boolean isExibirAvisoTrevosMaximo(){
        return exibirAvisoTrevosMaximo;
    }
    public boolean isAvisouTrevosMaximo(){
        return avisouTrevosMaximo;
    }
    public boolean isAvisouAumentoPalpitesLoteca(){
        return avisouAumentoPalpitesLoteca;
    }
    public boolean isAvisoAumentoPalpitesLotecaJaExibido(){
        return avisoAumentoPalpitesLotecaJaExibido;
    }
    public AnimacaoEffect getComandoAnimacao(){
        return animacaoEffect;
    }
    public int getQuantidadeTrevosSelecionada(){return quantidadeTrevosSelecionada;}
    public int getQuantidadeSurpresinhas(){return quantidadeSurpresinhas;}
    public int getQuantidadeNumerosSurpresinha(){return quantidadeNumerosSurpresinha;}
    public int getQuantidadeTrevosSurpresinha(){return quantidadeTrevosSurpresinha;}
    public ParametroMesDeSorte getMesSelecionado(){return mesSelecionado;}


    public SimulaUiState setQuantidadeTrevosSelecionada(
            int quantidadeTrevosSelecionada
    ){
        this.quantidadeTrevosSelecionada = quantidadeTrevosSelecionada;
        return this;
    }
    public SimulaUiState setPossuiAlteracoesPendentes(
            boolean possuiAlteracoesPendentes
    ){
        this.possuiAlteracoesPendentes = possuiAlteracoesPendentes;
        return this;
    }

    public SimulaUiState setLabelsQuantidadeNumeros(
            List<String> labelsQuantidadeNumeros
    ) {
        this.labelsQuantidadeNumeros =
                labelsQuantidadeNumeros != null
                        ? new ArrayList<>(labelsQuantidadeNumeros)
                        : new ArrayList<>();
        return this;
    }
    public SimulaUiState setComandoTelaSimula(
            ScreenSimulaEffect screenSimulaEffect
    ) {
        this.screenSimulaEffect = screenSimulaEffect;
        return this;
    }
    public SimulaUiState setExibirDialogQuantidadeTrevos(
            boolean exibirDialogQuantidadeTrevos
    ) {
        this.exibirDialogQuantidadeTrevos =
                exibirDialogQuantidadeTrevos;
        return this;
    }
    public SimulaUiState setExibirAvisoTrevosMaximo(
            boolean exibirAvisoTrevosMaximo
    ) {
        this.exibirAvisoTrevosMaximo =
                exibirAvisoTrevosMaximo;
        return this;
    }

    public SimulaUiState setAvisouTrevosMaximo(
            boolean avisouTrevosMaximo
    ) {
        this.avisouTrevosMaximo =
                avisouTrevosMaximo;
        return this;
    }

    public SimulaUiState setAvisouAumentoPalpitesLoteca(
            boolean avisouAumentoPalpitesLoteca
    ) {
        this.avisouAumentoPalpitesLoteca =
                avisouAumentoPalpitesLoteca;
        return this;
    }

    public SimulaUiState setAvisoAumentoPalpitesLotecaJaExibido(
            boolean avisoAumentoPalpitesLotecaJaExibido
    ) {
        this.avisoAumentoPalpitesLotecaJaExibido =
                avisoAumentoPalpitesLotecaJaExibido;
        return this;
    }


    public SimulaUiState setLabelsQuantidadeTrevos(
            List<String> labelsQuantidadeTrevos
    ) {
        this.labelsQuantidadeTrevos =
                labelsQuantidadeTrevos != null
                        ? new ArrayList<>(labelsQuantidadeTrevos)
                        : new ArrayList<>();
        return this;
    }

    public SimulaUiState setValoresTrevos(
            List<ParametroValorApostaDTO> valoresTrevos
    ) {
        this.valoresTrevos =
                valoresTrevos != null
                        ? new ArrayList<>(valoresTrevos)
                        : new ArrayList<>();
        return this;
    }
    public SimulaUiState setTrevosSelecionados(
            List<Integer> trevosSelecionados
    ) {
        this.trevosSelecionados =
                trevosSelecionados != null
                        ? new ArrayList<>(trevosSelecionados)
                        : new ArrayList<>();
        return this;
    }

    public SimulaUiState setPosicaoTrevosSelecionada(
            int posicaoTrevosSelecionada
    ) {
        this.posicaoTrevosSelecionada =
                posicaoTrevosSelecionada;
        return this;
    }

    public SimulaUiState setSubtituloTrevos(
            String subtituloTrevos
    ) {
        this.subtituloTrevos =
                subtituloTrevos != null
                        ? subtituloTrevos
                        : "";
        return this;
    }

    public SimulaUiState setValorTrevosSelecionado(
            ParametroValorApostaDTO valorTrevosSelecionado
    ) {
        this.valorTrevosSelecionado =
                valorTrevosSelecionado;
        return this;
    }
    public SimulaUiState setSurpresinhaHabilitada(
            boolean surpresinhaHabilitada
    ) {
        this.surpresinhaHabilitada = surpresinhaHabilitada;
        return this;
    }

    public SimulaUiState setComandoFavoritarAposta(
            FavoritarApostaEffect favoritarApostaEffect
    ) {
        this.favoritarApostaEffect =
                favoritarApostaEffect;
        return this;
    }
    public SimulaUiState setExibirInfoEtapa(boolean exibirInfoEtapa) {
        this.exibirInfoEtapa = exibirInfoEtapa;
        return this;
    }

    public SimulaUiState setTextoInfoEtapa(String textoInfoEtapa) {
        this.textoInfoEtapa = textoInfoEtapa;
        return this;
    }


    public SimulaUiState setAbrirCarrinho(boolean abrirCarrinho) {
        this.abrirCarrinho = abrirCarrinho;
        return this;
    }
    public SimulaUiState setExibirDialogFavoritarAposta(
            boolean exibirDialogFavoritarAposta
    ) {
        this.exibirDialogFavoritarAposta =
                exibirDialogFavoritarAposta;
        return this;
    }
    public SimulaUiState setComandoAnalytics(
            AnalyticsEffect analyticsEffect
    ) {
        this.analyticsEffect = analyticsEffect;
        return this;
    }
    public SimulaUiState setQtdPrognosticosQuantidadeNumeros(
            List<Integer> qtdPrognosticosQuantidadeNumeros
    ) {
        this.qtdPrognosticosQuantidadeNumeros =
                qtdPrognosticosQuantidadeNumeros != null
                        ? new ArrayList<>(qtdPrognosticosQuantidadeNumeros)
                        : new ArrayList<>();
        return this;
    }

    public SimulaUiState setExibirDialogQuantidadeNumeros(
            boolean exibirDialogQuantidadeNumeros
    ) {
        this.exibirDialogQuantidadeNumeros =
                exibirDialogQuantidadeNumeros;
        return this;
    }
    public SimulaUiState setTextoEscolha(
            String textoEscolha
    ) {
        this.textoEscolha =
                textoEscolha != null
                        ? textoEscolha
                        : "Escolha";
        return this;
    }
    public SimulaUiState setTextoSelecionados(
            String textoSelecionados
    ) {
        this.textoSelecionados =
                textoSelecionados != null
                        ? textoSelecionados
                        : "Selecione os números:";
        return this;
    }
    public SimulaUiState setTextoCompletar(
            String textoCompletar
    ) {
        this.textoCompletar =
                textoCompletar != null
                        ? textoCompletar
                        : "Aposta aleatória";
        return this;
    }
    public SimulaUiState setApostaFavoritada(boolean apostaFavoritada) {
        this.apostaFavoritada = apostaFavoritada;
        return this;
    }
    public SimulaUiState setExibirAvisoQuantidadeNumerosMaxima(
            boolean exibirAvisoQuantidadeNumerosMaxima
    ) {
        this.exibirAvisoQuantidadeNumerosMaxima =
                exibirAvisoQuantidadeNumerosMaxima;
        return this;
    }

    public SimulaUiState setAvisouQuantidadeNumerosMaxima(
            boolean avisouQuantidadeNumerosMaxima
    ) {
        this.avisouQuantidadeNumerosMaxima =
                avisouQuantidadeNumerosMaxima;
        return this;
    }

    public SimulaUiState setPosicaoQuantidadeNumerosSelecionada(
            int posicaoQuantidadeNumerosSelecionada
    ) {
        this.posicaoQuantidadeNumerosSelecionada =
                posicaoQuantidadeNumerosSelecionada;
        return this;
    }

    public SimulaUiState setTextoBotaoPrognosticosSelecionado(
            String textoBotaoPrognosticosSelecionado
    ) {
        this.textoBotaoPrognosticosSelecionado =
                textoBotaoPrognosticosSelecionado != null
                        ? textoBotaoPrognosticosSelecionado
                        : "";
        return this;
    }
    public SimulaUiState setTextoBotaoQuantidadeTrevos(
            String textoBotaoQuantidadeTrevos
    ) {
        this.textoBotaoQuantidadeTrevos =
                textoBotaoQuantidadeTrevos != null
                        ? textoBotaoQuantidadeTrevos
                        : "2 trevos";
        return this;
    }
    public SimulaUiState setAtualizarValorApostaPorQuantidadeNumeros(
            boolean atualizarValorApostaPorQuantidadeNumeros
    ) {
        this.atualizarValorApostaPorQuantidadeNumeros =
                atualizarValorApostaPorQuantidadeNumeros;
        return this;
    }
    public SimulaUiState setEtapaAposta(EtapaAposta etapaAposta) {
        this.etapaAposta = etapaAposta;
        return this;
    }

    public SimulaUiState setResetarTrevosPorQuantidadeNumeros(
            boolean resetarTrevosPorQuantidadeNumeros
    ) {
        this.resetarTrevosPorQuantidadeNumeros =
                resetarTrevosPorQuantidadeNumeros;
        return this;
    }

    public SimulaUiState setMensagemComoJogar(String mensagemComoJogar) {
        this.mensagemComoJogar = mensagemComoJogar;
        return this;
    }

    public SimulaUiState setTitleComoJogar(String titleComoJogar) {
        this.titleComoJogar = titleComoJogar;
        return this;
    }

    public SimulaUiState setBarraTituloDTO(
            BarraTituloDTO barraTituloDTO
    ) {
        this.barraTituloDTO = barraTituloDTO != null
                ? barraTituloDTO
                : new BarraTituloDTO();
        return this;
    }


    public SimulaUiState setValorCarrinho(
            BigDecimal valorCarrinho
    ) {
        this.valorCarrinho =
                valorCarrinho != null
                        ? valorCarrinho
                        : BigDecimal.ZERO;
        return this;
    }

    public SimulaUiState setExibirDialogComoJogar(boolean exibirDialogComoJogar) {
        this.exibirDialogComoJogar = exibirDialogComoJogar;
        return this;
    }

    public SimulaUiState setQuantidadeApostasCarrinho(
            int quantidadeApostasCarrinho
    ) {
        this.quantidadeApostasCarrinho =
                quantidadeApostasCarrinho;
        return this;
    }

    public SimulaUiState setCarregandoCarrinho(
            boolean carregandoCarrinho
    ) {
        this.carregandoCarrinho =
                carregandoCarrinho;
        return this;
    }

    public SimulaUiState setExibirCarrinho(
            boolean exibirCarrinho
    ) {
        this.exibirCarrinho =
                exibirCarrinho;
        return this;
    }

    public SimulaUiState setCarrinho(
            CarrinhoDTO carrinho
    ) {
        this.carrinho = carrinho;
        return this;
    }

    public SimulaUiState setEspecial(boolean especial) {
        this.especial = especial;
        return this;
    }
    public SimulaUiState setExibirOpcaoOutrosNumeros(boolean value) {
        this.exibirOpcaoOutrosNumeros = value;
        return this;
    }

    public SimulaUiState setOpcaoOutrosNumerosSelecionada(boolean value) {
        this.opcaoOutrosNumerosSelecionada = value;
        return this;
    }

    public SimulaUiState setLabelsTeimosinhas(List<String> labelsTeimosinhas) {
        this.labelsTeimosinhas = labelsTeimosinhas != null
                ? new ArrayList<>(labelsTeimosinhas)
                : new ArrayList<>();
        return this;
    }
    public SimulaUiState setMesSelecionado(ParametroMesDeSorte mesSelecionado) {
        this.mesSelecionado = mesSelecionado;
        return this;
    }
    public SimulaUiState setConcursosTeimosinhas(List<Integer> concursosTeimosinhas) {
        this.concursosTeimosinhas = concursosTeimosinhas != null
                ? new ArrayList<>(concursosTeimosinhas)
                : new ArrayList<>();
        return this;
    }

    public SimulaUiState setTextoBotaoTeimosinhas(String textoBotaoTeimosinhas) {
        this.textoBotaoTeimosinhas = textoBotaoTeimosinhas != null
                ? textoBotaoTeimosinhas
                : "";
        return this;
    }

    public SimulaUiState setExibirDialogTeimosinha(boolean exibirDialogTeimosinha) {
        this.exibirDialogTeimosinha = exibirDialogTeimosinha;
        return this;
    }

    public SimulaUiState setExibirDialogSemTeimosinha(boolean exibirDialogSemTeimosinha) {
        this.exibirDialogSemTeimosinha = exibirDialogSemTeimosinha;
        return this;
    }

    public SimulaUiState setAtualizarValorApostaPorTeimosinha(boolean atualizarValorApostaPorTeimosinha) {
        this.atualizarValorApostaPorTeimosinha = atualizarValorApostaPorTeimosinha;
        return this;
    }

    public SimulaUiState setComandoCarrinho(
            CarrinhoEffect carrinhoEffect
    ) {
        this.carrinhoEffect = carrinhoEffect;
        return this;
    }

    public SimulaUiState setComandoCompletarAposta(
            CompletarApostaEffect completarApostaEffect
    ) {
        this.completarApostaEffect = completarApostaEffect;
        return this;
    }

    public SimulaUiState setTipoJogo(
            ModalidadeEnum tipoJogo
    ) {
        this.tipoJogo = tipoJogo;
        return this;
    }

    public SimulaUiState setDezenasSelecionadas(
            List<Integer> dezenasSelecionadas
    ) {
        this.dezenasSelecionadas = dezenasSelecionadas != null
                ? new ArrayList<>(dezenasSelecionadas)
                : new ArrayList<>();
        return this;
    }

    public SimulaUiState setEquipeSelecionada(
            ParametroEquipe equipeSelecionada
    ) {
        this.equipeSelecionada = equipeSelecionada;
        return this;
    }

    public SimulaUiState setTelaSelecaoTimeAtivado(
            boolean telaSelecaoTimeAtivado
    ) {
        this.telaSelecaoTimeAtivado = telaSelecaoTimeAtivado;
        return this;
    }

    public SimulaUiState setEscolhaTimeCoracaoSurpresinha(
            boolean escolhaTimeCoracaoSurpresinha
    ) {
        this.escolhaTimeCoracaoSurpresinha = escolhaTimeCoracaoSurpresinha;
        return this;
    }

    public SimulaUiState setQtdDezenasPossiveisSelecionado(
            int qtdDezenasPossiveisSelecionado
    ) {
        this.qtdDezenasPossiveisSelecionado = qtdDezenasPossiveisSelecionado;
        return this;
    }

    public SimulaUiState setQtdConcursoSelecionado(
            int qtdConcursoSelecionado
    ) {
        this.qtdConcursoSelecionado = qtdConcursoSelecionado;
        return this;
    }

    public SimulaUiState setMostrarBotaoLimparAposta(
            boolean mostrarBotaoLimparAposta
    ) {
        this.mostrarBotaoLimparAposta = mostrarBotaoLimparAposta;
        return this;
    }
    public SimulaUiState setComandoAnimacao(
            AnimacaoEffect animacaoEffect
    ) {
        this.animacaoEffect = animacaoEffect;
        return this;
    }

    public SimulaUiState setMostrarSalvarAposta(
            boolean mostrarSalvarAposta
    ) {
        this.mostrarSalvarAposta = mostrarSalvarAposta;
        return this;
    }

    public SimulaUiState setLimparNumerosCartela(
            boolean limparNumerosCartela
    ) {
        this.limparNumerosCartela = limparNumerosCartela;
        return this;
    }

    public SimulaUiState setLimparTimeCoracao(
            boolean limparTimeCoracao
    ) {
        this.limparTimeCoracao = limparTimeCoracao;
        return this;
    }

    public SimulaUiState setLimparSuperSete(
            boolean limparSuperSete
    ) {
        this.limparSuperSete = limparSuperSete;
        return this;
    }

    public SimulaUiState setLimparLoteca(
            boolean limparLoteca
    ) {
        this.limparLoteca = limparLoteca;
        return this;
    }

    public SimulaUiState setLimparLotogol(
            boolean limparLotogol
    ) {
        this.limparLotogol = limparLotogol;
        return this;
    }

    public SimulaUiState setLimparOpcaoOutrosNumeros(
            boolean limparOpcaoOutrosNumeros
    ) {
        this.limparOpcaoOutrosNumeros = limparOpcaoOutrosNumeros;
        return this;
    }

    public SimulaUiState setQtdTotalSelecionadosSuperSete(
            int qtdTotalSelecionadosSuperSete
    ) {
        this.qtdTotalSelecionadosSuperSete = qtdTotalSelecionadosSuperSete;
        return this;
    }
    public SimulaUiState setPalpitesLoteca(
            int palpitesLoteca
    ) {
        this.palpitesLoteca = palpitesLoteca;
        return this;
    }

    public SimulaUiState setJogosLoteca(
            int jogosLoteca
    ) {
        this.jogosLoteca = jogosLoteca;
        return this;
    }

    public SimulaUiState setSimplesLoteca(
            int simplesLoteca
    ) {
        this.simplesLoteca = simplesLoteca;
        return this;
    }

    public SimulaUiState setDuplasLoteca(
            int duplasLoteca
    ) {
        this.duplasLoteca = duplasLoteca;
        return this;
    }

    public SimulaUiState setTriplasLoteca(
            int triplasLoteca
    ) {
        this.triplasLoteca = triplasLoteca;
        return this;
    }
    public SimulaUiState setQuantidadeSurpresinhas(
            int quantidadeSurpresinhas
    ) {
        this.quantidadeSurpresinhas = quantidadeSurpresinhas;
        return this;
    }
    public SimulaUiState setQuantidadeNumerosSurpresinha(
            int quantidadeNumerosSurpresinha
    ) {
        this.quantidadeNumerosSurpresinha = quantidadeNumerosSurpresinha;
        return this;
    }
    public SimulaUiState setQuantidadeTrevosSurpresinha(
            int quantidadeTrevosSurpresinha
    ) {
        this.quantidadeTrevosSurpresinha = quantidadeTrevosSurpresinha;
        return this;
    }

    public SimulaUiState setBotaoCompletarVisivel(
            boolean botaoCompletarVisivel
    ) {
        this.botaoFinalizarVisivel = botaoCompletarVisivel;
        return this;
    }


    public SimulaUiState setBotaoCompletarHabilitado(
            boolean botaoCompletarHabilitado
    ) {
        this.botaoCompletarHabilitado = botaoCompletarHabilitado;
        return this;
    }

    public SimulaUiState setBotaoAdicionarHabilitado(
            boolean botaoAdicionarHabilitado
    ) {
        this.botaoAdicionarHabilitado = botaoAdicionarHabilitado;
        return this;
    }
    public SimulaUiState setBotaoNextStepHabilitado(
            boolean botaoNextStepHabilitado
    ) {
        this.botaoNextStepHabilitado = botaoNextStepHabilitado;
        return this;
    }


    public SimulaUiState setBotaoLimparHabilitado(
            boolean botaoLimparHabilitado
    ) {
        this.botaoLimparHabilitado = botaoLimparHabilitado;
        return this;
    }

    public SimulaUiState setValorAposta(
            BigDecimal valorAposta
    ) {
        this.valorAposta = valorAposta != null
                ? valorAposta
                : BigDecimal.ZERO;
        return this;
    }

    public SimulaUiState setValorGrande(
            boolean valorGrande
    ) {
        this.valorGrande = valorGrande;
        return this;
    }
}