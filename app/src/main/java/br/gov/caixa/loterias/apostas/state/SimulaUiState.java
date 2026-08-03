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

    private final BarraTituloDTO barraTituloDTO;

    private final FavoritarApostaEffect favoritarApostaEffect;

    private final CarrinhoEffect carrinhoEffect;
    private final CompletarApostaEffect completarApostaEffect;

    private final ModalidadeEnum tipoJogo;

    private final List<Integer> dezenasSelecionadas;
    private final ParametroEquipe equipeSelecionada;
    private final boolean especial;

    private final boolean telaSelecaoTimeAtivado;
    private final boolean escolhaTimeCoracaoSurpresinha;

    private final int qtdDezenasPossiveisSelecionado;
    private final int qtdConcursoSelecionado;

    private final boolean mostrarBotaoLimparAposta;
    private final boolean mostrarSalvarAposta;

    private final boolean limparNumerosCartela;
    private final boolean limparTimeCoracao;
    private final boolean limparSuperSete;
    private final boolean limparLoteca;
    private final boolean limparLotogol;
    private final boolean limparOpcaoOutrosNumeros;

    private final int qtdTotalSelecionadosSuperSete;

    private final int jogosLoteca;
    private final int simplesLoteca;
    private final int duplasLoteca;
    private final int triplasLoteca;

    private final boolean botaoFinalizarVisivel;
    private final boolean botaoCompletarHabilitado;
    private final boolean botaoAdicionarHabilitado;
    private final boolean botaoNextStepHabilitado;
    private final boolean botaoLimparHabilitado;

    private final BigDecimal valorAposta;
    private final boolean valorGrande;
    private final BigDecimal valorCarrinho;
    private final int quantidadeApostasCarrinho;
    private final boolean carregandoCarrinho;
    private final boolean exibirCarrinho;
    private final CarrinhoDTO carrinho;
    private final boolean exibirDialogComoJogar;
    private final String mensagemComoJogar;
    private final String titleComoJogar;
    private final List<String> labelsTeimosinhas;
    private final List<Integer> concursosTeimosinhas;
    private final String textoBotaoTeimosinhas;
    private final boolean exibirDialogTeimosinha;
    private final boolean exibirDialogSemTeimosinha;
    private final boolean atualizarValorApostaPorTeimosinha;
    private final List<String> labelsQuantidadeNumeros;
    private final List<Integer> qtdPrognosticosQuantidadeNumeros;

    private final boolean exibirDialogQuantidadeNumeros;
    private final boolean exibirAvisoQuantidadeNumerosMaxima;
    private final boolean avisouQuantidadeNumerosMaxima;

    private final int posicaoQuantidadeNumerosSelecionada;
    private final String textoBotaoPrognosticosSelecionado;
    private final String textoBotaoQuantidadeTrevos;


    private final boolean atualizarValorApostaPorQuantidadeNumeros;
    private final boolean resetarTrevosPorQuantidadeNumeros;
    private final String textoSelecionados;
    private final String textoCompletar;

    private final String textoEscolha;
    private final boolean abrirCarrinho;
    private final ScreenSimulaEffect screenSimulaEffect;
    private final boolean surpresinhaHabilitada;
    private final boolean exibirDialogFavoritarAposta;
    private final boolean apostaFavoritada;
    private final EtapaAposta etapaAposta;
    private final boolean exibirOpcaoOutrosNumeros;
    private final boolean opcaoOutrosNumerosSelecionada;
    private final AnalyticsEffect analyticsEffect;
    private final boolean exibirInfoEtapa;
    private final String textoInfoEtapa;
    private final AnimacaoEffect animacaoEffect;
    private final boolean exibirDialogQuantidadeTrevos;
    private final List<String> labelsQuantidadeTrevos;
    private final List<ParametroValorApostaDTO> valoresTrevos;
    private final int posicaoTrevosSelecionada;
    private final String subtituloTrevos;
    private final ParametroValorApostaDTO valorTrevosSelecionado;
    private final boolean exibirAvisoTrevosMaximo;
    private final boolean avisouTrevosMaximo;
    private final int quantidadeSurpresinhas;
    private final int quantidadeNumerosSurpresinha;
    private final int quantidadeTrevosSurpresinha;
    private final List<Integer> trevosSelecionados;
    private final int quantidadeTrevosSelecionada;
    private final ParametroMesDeSorte mesSelecionado;
    private final boolean possuiAlteracoesPendentes;
    private SimulaUiState(
            Builder builder
    ) {
        this.barraTituloDTO = builder.barraTituloDTO != null
                ? builder.barraTituloDTO
                : new BarraTituloDTO();

        this.exibirDialogComoJogar = builder.exibirDialogComoJogar;
        this.screenSimulaEffect =
                builder.screenSimulaEffect;
        this.analyticsEffect = builder.analyticsEffect;

        this.exibirOpcaoOutrosNumeros =
                builder.exibirOpcaoOutrosNumeros;

        this.opcaoOutrosNumerosSelecionada =
                builder.opcaoOutrosNumerosSelecionada;

        this.surpresinhaHabilitada =
                builder.surpresinhaHabilitada;

        this.exibirAvisoTrevosMaximo = builder.exibirAvisoTrevosMaximo;
        this.avisouTrevosMaximo = builder.avisouTrevosMaximo;


        this.carrinhoEffect = builder.carrinhoEffect;
        this.completarApostaEffect = builder.completarApostaEffect;

        this.tipoJogo = builder.tipoJogo;
        this.titleComoJogar = builder.titleComoJogar;
        this.exibirDialogFavoritarAposta = builder.exibirDialogFavoritarAposta;
        this.favoritarApostaEffect = builder.favoritarApostaEffect;
        this.apostaFavoritada = builder.apostaFavoritada;
        this.animacaoEffect = builder.animacaoEffect;

        this.dezenasSelecionadas = builder.dezenasSelecionadas != null
                ? new ArrayList<>(builder.dezenasSelecionadas)
                : new ArrayList<>();

        this.equipeSelecionada = builder.equipeSelecionada;

        this.telaSelecaoTimeAtivado = builder.telaSelecaoTimeAtivado;
        this.escolhaTimeCoracaoSurpresinha = builder.escolhaTimeCoracaoSurpresinha;

        this.qtdDezenasPossiveisSelecionado = builder.qtdDezenasPossiveisSelecionado;
        this.qtdConcursoSelecionado = builder.qtdConcursoSelecionado;

        this.mostrarBotaoLimparAposta = builder.mostrarBotaoLimparAposta;
        this.mostrarSalvarAposta = builder.mostrarSalvarAposta;

        this.limparNumerosCartela = builder.limparNumerosCartela;
        this.limparTimeCoracao = builder.limparTimeCoracao;
        this.limparSuperSete = builder.limparSuperSete;
        this.limparLoteca = builder.limparLoteca;
        this.limparLotogol = builder.limparLotogol;
        this.limparOpcaoOutrosNumeros = builder.limparOpcaoOutrosNumeros;

        this.qtdTotalSelecionadosSuperSete = builder.qtdTotalSelecionadosSuperSete;

        this.jogosLoteca = builder.jogosLoteca;
        this.simplesLoteca = builder.simplesLoteca;
        this.duplasLoteca = builder.duplasLoteca;
        this.triplasLoteca = builder.triplasLoteca;

        this.botaoFinalizarVisivel = builder.botaoCompletarVisivel;

        this.botaoCompletarHabilitado = builder.botaoCompletarHabilitado;
        this.botaoAdicionarHabilitado = builder.botaoAdicionarHabilitado;
        this.botaoNextStepHabilitado = builder.botaoNextStepHabilitado;
        this.botaoLimparHabilitado = builder.botaoLimparHabilitado;

        this.valorAposta = builder.valorAposta != null
                ? builder.valorAposta
                : BigDecimal.ZERO;

        this.valorGrande = builder.valorGrande;
        this.valorCarrinho =
                builder.valorCarrinho != null
                        ? builder.valorCarrinho
                        : BigDecimal.ZERO;

        this.quantidadeApostasCarrinho =
                builder.quantidadeApostasCarrinho;

        this.carregandoCarrinho =
                builder.carregandoCarrinho;

        this.exibirCarrinho =
                builder.exibirCarrinho;

        this.carrinho =
                builder.carrinho;

        this.mensagemComoJogar =
                builder.mensagemComoJogar != null
                        ? builder.mensagemComoJogar
                        : "";

        this.especial = builder.especial;
        this.etapaAposta = builder.etapaAposta;

        this.labelsTeimosinhas = builder.labelsTeimosinhas != null
                ? new ArrayList<>(builder.labelsTeimosinhas)
                : new ArrayList<>();

        this.concursosTeimosinhas = builder.concursosTeimosinhas != null
                ? new ArrayList<>(builder.concursosTeimosinhas)
                : new ArrayList<>();

        this.textoBotaoTeimosinhas = builder.textoBotaoTeimosinhas != null
                ? builder.textoBotaoTeimosinhas
                : "";

        this.exibirDialogTeimosinha = builder.exibirDialogTeimosinha;
        this.exibirDialogSemTeimosinha = builder.exibirDialogSemTeimosinha;
        this.atualizarValorApostaPorTeimosinha = builder.atualizarValorApostaPorTeimosinha;
        this.labelsQuantidadeNumeros =
                builder.labelsQuantidadeNumeros != null
                        ? new ArrayList<>(builder.labelsQuantidadeNumeros)
                        : new ArrayList<>();

        this.qtdPrognosticosQuantidadeNumeros =
                builder.qtdPrognosticosQuantidadeNumeros != null
                        ? new ArrayList<>(builder.qtdPrognosticosQuantidadeNumeros)
                        : new ArrayList<>();

        this.exibirDialogQuantidadeNumeros =
                builder.exibirDialogQuantidadeNumeros;

        this.exibirAvisoQuantidadeNumerosMaxima =
                builder.exibirAvisoQuantidadeNumerosMaxima;

        this.avisouQuantidadeNumerosMaxima =
                builder.avisouQuantidadeNumerosMaxima;

        this.posicaoQuantidadeNumerosSelecionada =
                builder.posicaoQuantidadeNumerosSelecionada;

        this.textoBotaoPrognosticosSelecionado =
                builder.textoBotaoPrognosticosSelecionado != null
                        ? builder.textoBotaoPrognosticosSelecionado
                        : "";

        this.textoBotaoQuantidadeTrevos =
                builder.textoBotaoQuantidadeTrevos != null
                        ? builder.textoBotaoQuantidadeTrevos
                        : "";

        this.atualizarValorApostaPorQuantidadeNumeros =
                builder.atualizarValorApostaPorQuantidadeNumeros;

        this.resetarTrevosPorQuantidadeNumeros =
                builder.resetarTrevosPorQuantidadeNumeros;
        this.abrirCarrinho = builder.abrirCarrinho;
        this.exibirInfoEtapa = builder.exibirInfoEtapa;
        this.textoInfoEtapa = builder.textoInfoEtapa;

        this.textoSelecionados =
                builder.textoSelecionados != null
                        ? builder.textoSelecionados
                        : "Selecione os números:";

        this.textoCompletar =
                builder.textoCompletar != null
                        ? builder.textoCompletar
                        : "Aposta aleatória";

        this.textoEscolha =
                builder.textoEscolha != null
                        ? builder.textoEscolha
                        : "Escolha";

        this.exibirDialogQuantidadeTrevos =
                builder.exibirDialogQuantidadeTrevos;

        this.labelsQuantidadeTrevos =
                builder.labelsQuantidadeTrevos != null
                        ? new ArrayList<>(builder.labelsQuantidadeTrevos)
                        : new ArrayList<>();

        this.valoresTrevos =
                builder.valoresTrevos != null
                        ? new ArrayList<>(builder.valoresTrevos)
                        : new ArrayList<>();

        this.posicaoTrevosSelecionada =
                builder.posicaoTrevosSelecionada;

        this.subtituloTrevos =
                builder.subtituloTrevos != null
                        ? builder.subtituloTrevos
                        : "";

        this.valorTrevosSelecionado =
                builder.valorTrevosSelecionado;
        this.trevosSelecionados =
                builder.trevosSelecionados != null
                        ? new ArrayList<>(builder.trevosSelecionados)
                        : new ArrayList<>();
        this.quantidadeTrevosSelecionada = builder.quantidadeTrevosSelecionada;
        this.quantidadeSurpresinhas = builder.quantidadeSurpresinhas;
        this.quantidadeNumerosSurpresinha = builder.quantidadeNumerosSurpresinha;
        this.quantidadeTrevosSurpresinha = builder.quantidadeTrevosSurpresinha;
        this.mesSelecionado = builder.mesSelecionado;
        this.possuiAlteracoesPendentes = builder.possuiAlteracoesPendentes;
    }

    public static SimulaUiState initial() {
        return new Builder()
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
                .build();
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

    public Builder toBuilder() {
        return new Builder(this);
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
    public AnimacaoEffect getComandoAnimacao(){
        return animacaoEffect;
    }
    public int getQuantidadeTrevosSelecionada(){return quantidadeTrevosSelecionada;}
    public int getQuantidadeSurpresinhas(){return quantidadeSurpresinhas;}
    public int getQuantidadeNumerosSurpresinha(){return quantidadeNumerosSurpresinha;}
    public int getQuantidadeTrevosSurpresinha(){return quantidadeTrevosSurpresinha;}
    public ParametroMesDeSorte getMesSelecionado(){return mesSelecionado;}


    public static class Builder {

        private BarraTituloDTO barraTituloDTO;

        private boolean exibirDialogComoJogar;

        private CarrinhoEffect carrinhoEffect;
        private CompletarApostaEffect completarApostaEffect;

        private ModalidadeEnum tipoJogo;

        private List<Integer> dezenasSelecionadas;
        private ParametroEquipe equipeSelecionada;

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

        private int jogosLoteca;
        private int simplesLoteca;
        private int duplasLoteca;
        private int triplasLoteca;

        private boolean botaoCompletarVisivel;

        private boolean botaoCompletarHabilitado;
        private boolean botaoAdicionarHabilitado;
        private boolean botaoNextStepHabilitado;
        private boolean botaoLimparHabilitado;
        private BigDecimal valorCarrinho;
        private int quantidadeApostasCarrinho;
        private boolean carregandoCarrinho;
        private boolean exibirCarrinho;
        private CarrinhoDTO carrinho;

        private BigDecimal valorAposta;
        private boolean valorGrande;
        private String mensagemComoJogar;
        private String titleComoJogar;
        private boolean especial;
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

        private boolean exibirOpcaoOutrosNumeros;
        private boolean opcaoOutrosNumerosSelecionada;

        private boolean atualizarValorApostaPorQuantidadeNumeros;
        private boolean resetarTrevosPorQuantidadeNumeros;
        private String textoSelecionados = "Selecione os números:";
        private String textoCompletar = "Aposta aleatória";
        private String textoEscolha = "Escolha";
        private boolean abrirCarrinho;
        private boolean surpresinhaHabilitada;
        private ScreenSimulaEffect screenSimulaEffect;
        private EtapaAposta etapaAposta;
        private boolean exibirDialogFavoritarAposta;
        private FavoritarApostaEffect favoritarApostaEffect;
        private boolean apostaFavoritada;
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
        private List<Integer> trevosSelecionados;
        private int quantidadeTrevosSelecionada;
        private int quantidadeSurpresinhas;
        private int quantidadeNumerosSurpresinha;
        private int quantidadeTrevosSurpresinha;
        private ParametroMesDeSorte mesSelecionado;
        private boolean possuiAlteracoesPendentes;

        public Builder() {
            this.dezenasSelecionadas = new ArrayList<>();
            this.botaoCompletarVisivel = true;
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
            this.trevosSelecionados = new ArrayList<>();
            this.quantidadeTrevosSelecionada = 0;
            this.quantidadeSurpresinhas = 0;
            this.qtdTotalSelecionadosSuperSete = 0;
            this.quantidadeNumerosSurpresinha = 0;
            this.mesSelecionado = null;
            this.quantidadeTrevosSurpresinha = 0;
            this.favoritarApostaEffect = null;
        }

        public Builder(
                SimulaUiState state
        ) {
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

            this.jogosLoteca = state.jogosLoteca;
            this.simplesLoteca = state.simplesLoteca;
            this.duplasLoteca = state.duplasLoteca;
            this.triplasLoteca = state.triplasLoteca;

            this.botaoCompletarVisivel = state.botaoFinalizarVisivel;

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
            this.quantidadeTrevosSelecionada = state.quantidadeTrevosSelecionada;
            this.quantidadeSurpresinhas = state.quantidadeSurpresinhas;
            this.quantidadeNumerosSurpresinha = state.quantidadeNumerosSurpresinha;
            this.quantidadeTrevosSurpresinha = state.quantidadeTrevosSurpresinha;
            this.mesSelecionado = state.mesSelecionado;
            this.possuiAlteracoesPendentes = state.possuiAlteracoesPendentes;
        }
        public Builder setQuantidadeTrevosSelecionada(
                int quantidadeTrevosSelecionada
        ){
            this.quantidadeTrevosSelecionada = quantidadeTrevosSelecionada;
            return this;
        }
        public Builder setPossuiAlteracoesPendentes(
                boolean possuiAlteracoesPendentes
        ){
            this.possuiAlteracoesPendentes = possuiAlteracoesPendentes;
            return this;
        }

        public Builder setLabelsQuantidadeNumeros(
                List<String> labelsQuantidadeNumeros
        ) {
            this.labelsQuantidadeNumeros =
                    labelsQuantidadeNumeros != null
                            ? new ArrayList<>(labelsQuantidadeNumeros)
                            : new ArrayList<>();
            return this;
        }
        public Builder setComandoTelaSimula(
                ScreenSimulaEffect screenSimulaEffect
        ) {
            this.screenSimulaEffect = screenSimulaEffect;
            return this;
        }
        public Builder setExibirDialogQuantidadeTrevos(
                boolean exibirDialogQuantidadeTrevos
        ) {
            this.exibirDialogQuantidadeTrevos =
                    exibirDialogQuantidadeTrevos;
            return this;
        }
        public Builder setExibirAvisoTrevosMaximo(
                boolean exibirAvisoTrevosMaximo
        ) {
            this.exibirAvisoTrevosMaximo =
                    exibirAvisoTrevosMaximo;
            return this;
        }

        public Builder setAvisouTrevosMaximo(
                boolean avisouTrevosMaximo
        ) {
            this.avisouTrevosMaximo =
                    avisouTrevosMaximo;
            return this;
        }


        public Builder setLabelsQuantidadeTrevos(
                List<String> labelsQuantidadeTrevos
        ) {
            this.labelsQuantidadeTrevos =
                    labelsQuantidadeTrevos != null
                            ? new ArrayList<>(labelsQuantidadeTrevos)
                            : new ArrayList<>();
            return this;
        }

        public Builder setValoresTrevos(
                List<ParametroValorApostaDTO> valoresTrevos
        ) {
            this.valoresTrevos =
                    valoresTrevos != null
                            ? new ArrayList<>(valoresTrevos)
                            : new ArrayList<>();
            return this;
        }
        public Builder setTrevosSelecionados(
                List<Integer> trevosSelecionados
        ) {
            this.trevosSelecionados =
                    trevosSelecionados != null
                            ? new ArrayList<>(trevosSelecionados)
                            : new ArrayList<>();
            return this;
        }

        public Builder setPosicaoTrevosSelecionada(
                int posicaoTrevosSelecionada
        ) {
            this.posicaoTrevosSelecionada =
                    posicaoTrevosSelecionada;
            return this;
        }

        public Builder setSubtituloTrevos(
                String subtituloTrevos
        ) {
            this.subtituloTrevos =
                    subtituloTrevos != null
                            ? subtituloTrevos
                            : "";
            return this;
        }

        public Builder setValorTrevosSelecionado(
                ParametroValorApostaDTO valorTrevosSelecionado
        ) {
            this.valorTrevosSelecionado =
                    valorTrevosSelecionado;
            return this;
        }
        public Builder setSurpresinhaHabilitada(
                boolean surpresinhaHabilitada
        ) {
            this.surpresinhaHabilitada = surpresinhaHabilitada;
            return this;
        }

        public Builder setComandoFavoritarAposta(
                FavoritarApostaEffect favoritarApostaEffect
        ) {
            this.favoritarApostaEffect =
                    favoritarApostaEffect;
            return this;
        }
        public Builder setExibirInfoEtapa(boolean exibirInfoEtapa) {
            this.exibirInfoEtapa = exibirInfoEtapa;
            return this;
        }

        public Builder setTextoInfoEtapa(String textoInfoEtapa) {
            this.textoInfoEtapa = textoInfoEtapa;
            return this;
        }


        public Builder setAbrirCarrinho(boolean abrirCarrinho) {
            this.abrirCarrinho = abrirCarrinho;
            return this;
        }
        public Builder setExibirDialogFavoritarAposta(
                boolean exibirDialogFavoritarAposta
        ) {
            this.exibirDialogFavoritarAposta =
                    exibirDialogFavoritarAposta;
            return this;
        }
        public Builder setComandoAnalytics(
                AnalyticsEffect analyticsEffect
        ) {
            this.analyticsEffect = analyticsEffect;
            return this;
        }
        public Builder setQtdPrognosticosQuantidadeNumeros(
                List<Integer> qtdPrognosticosQuantidadeNumeros
        ) {
            this.qtdPrognosticosQuantidadeNumeros =
                    qtdPrognosticosQuantidadeNumeros != null
                            ? new ArrayList<>(qtdPrognosticosQuantidadeNumeros)
                            : new ArrayList<>();
            return this;
        }

        public Builder setExibirDialogQuantidadeNumeros(
                boolean exibirDialogQuantidadeNumeros
        ) {
            this.exibirDialogQuantidadeNumeros =
                    exibirDialogQuantidadeNumeros;
            return this;
        }
        public Builder setTextoEscolha(
                String textoEscolha
        ) {
            this.textoEscolha =
                    textoEscolha != null
                            ? textoEscolha
                            : "Escolha";
            return this;
        }
        public Builder setTextoSelecionados(
                String textoSelecionados
        ) {
            this.textoSelecionados =
                    textoSelecionados != null
                            ? textoSelecionados
                            : "Selecione os números:";
            return this;
        }
        public Builder setTextoCompletar(
                String textoCompletar
        ) {
            this.textoCompletar =
                    textoCompletar != null
                            ? textoCompletar
                            : "Aposta aleatória";
            return this;
        }
        public Builder setApostaFavoritada(boolean apostaFavoritada) {
            this.apostaFavoritada = apostaFavoritada;
            return this;
        }
        public Builder setExibirAvisoQuantidadeNumerosMaxima(
                boolean exibirAvisoQuantidadeNumerosMaxima
        ) {
            this.exibirAvisoQuantidadeNumerosMaxima =
                    exibirAvisoQuantidadeNumerosMaxima;
            return this;
        }

        public Builder setAvisouQuantidadeNumerosMaxima(
                boolean avisouQuantidadeNumerosMaxima
        ) {
            this.avisouQuantidadeNumerosMaxima =
                    avisouQuantidadeNumerosMaxima;
            return this;
        }

        public Builder setPosicaoQuantidadeNumerosSelecionada(
                int posicaoQuantidadeNumerosSelecionada
        ) {
            this.posicaoQuantidadeNumerosSelecionada =
                    posicaoQuantidadeNumerosSelecionada;
            return this;
        }

        public Builder setTextoBotaoPrognosticosSelecionado(
                String textoBotaoPrognosticosSelecionado
        ) {
            this.textoBotaoPrognosticosSelecionado =
                    textoBotaoPrognosticosSelecionado != null
                            ? textoBotaoPrognosticosSelecionado
                            : "";
            return this;
        }
        public Builder setTextoBotaoQuantidadeTrevos(
                String textoBotaoQuantidadeTrevos
        ) {
            this.textoBotaoQuantidadeTrevos =
                    textoBotaoQuantidadeTrevos != null
                            ? textoBotaoQuantidadeTrevos
                            : "2 trevos";
            return this;
        }
        public Builder setAtualizarValorApostaPorQuantidadeNumeros(
                boolean atualizarValorApostaPorQuantidadeNumeros
        ) {
            this.atualizarValorApostaPorQuantidadeNumeros =
                    atualizarValorApostaPorQuantidadeNumeros;
            return this;
        }
        public Builder setEtapaAposta(EtapaAposta etapaAposta) {
            this.etapaAposta = etapaAposta;
            return this;
        }

        public Builder setResetarTrevosPorQuantidadeNumeros(
                boolean resetarTrevosPorQuantidadeNumeros
        ) {
            this.resetarTrevosPorQuantidadeNumeros =
                    resetarTrevosPorQuantidadeNumeros;
            return this;
        }

        public Builder setMensagemComoJogar(String mensagemComoJogar) {
            this.mensagemComoJogar = mensagemComoJogar;
            return this;
        }

        public Builder setTitleComoJogar(String titleComoJogar) {
            this.titleComoJogar = titleComoJogar;
            return this;
        }

        public Builder setBarraTituloDTO(
                BarraTituloDTO barraTituloDTO
        ) {
            this.barraTituloDTO = barraTituloDTO;
            return this;
        }


        public Builder setValorCarrinho(
                BigDecimal valorCarrinho
        ) {
            this.valorCarrinho =
                    valorCarrinho != null
                            ? valorCarrinho
                            : BigDecimal.ZERO;
            return this;
        }

        public Builder setExibirDialogComoJogar(boolean exibirDialogComoJogar) {
            this.exibirDialogComoJogar = exibirDialogComoJogar;
            return this;
        }

        public Builder setQuantidadeApostasCarrinho(
                int quantidadeApostasCarrinho
        ) {
            this.quantidadeApostasCarrinho =
                    quantidadeApostasCarrinho;
            return this;
        }

        public Builder setCarregandoCarrinho(
                boolean carregandoCarrinho
        ) {
            this.carregandoCarrinho =
                    carregandoCarrinho;
            return this;
        }

        public Builder setExibirCarrinho(
                boolean exibirCarrinho
        ) {
            this.exibirCarrinho =
                    exibirCarrinho;
            return this;
        }

        public Builder setCarrinho(
                CarrinhoDTO carrinho
        ) {
            this.carrinho = carrinho;
            return this;
        }

        public Builder setEspecial(boolean especial) {
            this.especial = especial;
            return this;
        }
        public Builder setExibirOpcaoOutrosNumeros(boolean value) {
            this.exibirOpcaoOutrosNumeros = value;
            return this;
        }

        public Builder setOpcaoOutrosNumerosSelecionada(boolean value) {
            this.opcaoOutrosNumerosSelecionada = value;
            return this;
        }

        public Builder setLabelsTeimosinhas(List<String> labelsTeimosinhas) {
            this.labelsTeimosinhas = labelsTeimosinhas != null
                    ? new ArrayList<>(labelsTeimosinhas)
                    : new ArrayList<>();
            return this;
        }
        public Builder setMesSelecionado(ParametroMesDeSorte mesSelecionado) {
            this.mesSelecionado = mesSelecionado;
            return this;
        }
        public Builder setConcursosTeimosinhas(List<Integer> concursosTeimosinhas) {
            this.concursosTeimosinhas = concursosTeimosinhas != null
                    ? new ArrayList<>(concursosTeimosinhas)
                    : new ArrayList<>();
            return this;
        }

        public Builder setTextoBotaoTeimosinhas(String textoBotaoTeimosinhas) {
            this.textoBotaoTeimosinhas = textoBotaoTeimosinhas != null
                    ? textoBotaoTeimosinhas
                    : "";
            return this;
        }

        public Builder setExibirDialogTeimosinha(boolean exibirDialogTeimosinha) {
            this.exibirDialogTeimosinha = exibirDialogTeimosinha;
            return this;
        }

        public Builder setExibirDialogSemTeimosinha(boolean exibirDialogSemTeimosinha) {
            this.exibirDialogSemTeimosinha = exibirDialogSemTeimosinha;
            return this;
        }

        public Builder setAtualizarValorApostaPorTeimosinha(boolean atualizarValorApostaPorTeimosinha) {
            this.atualizarValorApostaPorTeimosinha = atualizarValorApostaPorTeimosinha;
            return this;
        }

        public Builder setComandoCarrinho(
                CarrinhoEffect carrinhoEffect
        ) {
            this.carrinhoEffect = carrinhoEffect;
            return this;
        }

        public Builder setComandoCompletarAposta(
                CompletarApostaEffect completarApostaEffect
        ) {
            this.completarApostaEffect = completarApostaEffect;
            return this;
        }

        public Builder setTipoJogo(
                ModalidadeEnum tipoJogo
        ) {
            this.tipoJogo = tipoJogo;
            return this;
        }

        public Builder setDezenasSelecionadas(
                List<Integer> dezenasSelecionadas
        ) {
            this.dezenasSelecionadas = dezenasSelecionadas != null
                    ? new ArrayList<>(dezenasSelecionadas)
                    : new ArrayList<>();
            return this;
        }

        public Builder setEquipeSelecionada(
                ParametroEquipe equipeSelecionada
        ) {
            this.equipeSelecionada = equipeSelecionada;
            return this;
        }

        public Builder setTelaSelecaoTimeAtivado(
                boolean telaSelecaoTimeAtivado
        ) {
            this.telaSelecaoTimeAtivado = telaSelecaoTimeAtivado;
            return this;
        }

        public Builder setEscolhaTimeCoracaoSurpresinha(
                boolean escolhaTimeCoracaoSurpresinha
        ) {
            this.escolhaTimeCoracaoSurpresinha = escolhaTimeCoracaoSurpresinha;
            return this;
        }

        public Builder setQtdDezenasPossiveisSelecionado(
                int qtdDezenasPossiveisSelecionado
        ) {
            this.qtdDezenasPossiveisSelecionado = qtdDezenasPossiveisSelecionado;
            return this;
        }

        public Builder setQtdConcursoSelecionado(
                int qtdConcursoSelecionado
        ) {
            this.qtdConcursoSelecionado = qtdConcursoSelecionado;
            return this;
        }

        public Builder setMostrarBotaoLimparAposta(
                boolean mostrarBotaoLimparAposta
        ) {
            this.mostrarBotaoLimparAposta = mostrarBotaoLimparAposta;
            return this;
        }
        public Builder setComandoAnimacao(
                AnimacaoEffect animacaoEffect
        ) {
            this.animacaoEffect = animacaoEffect;
            return this;
        }

        public Builder setMostrarSalvarAposta(
                boolean mostrarSalvarAposta
        ) {
            this.mostrarSalvarAposta = mostrarSalvarAposta;
            return this;
        }

        public Builder setLimparNumerosCartela(
                boolean limparNumerosCartela
        ) {
            this.limparNumerosCartela = limparNumerosCartela;
            return this;
        }

        public Builder setLimparTimeCoracao(
                boolean limparTimeCoracao
        ) {
            this.limparTimeCoracao = limparTimeCoracao;
            return this;
        }

        public Builder setLimparSuperSete(
                boolean limparSuperSete
        ) {
            this.limparSuperSete = limparSuperSete;
            return this;
        }

        public Builder setLimparLoteca(
                boolean limparLoteca
        ) {
            this.limparLoteca = limparLoteca;
            return this;
        }

        public Builder setLimparLotogol(
                boolean limparLotogol
        ) {
            this.limparLotogol = limparLotogol;
            return this;
        }

        public Builder setLimparOpcaoOutrosNumeros(
                boolean limparOpcaoOutrosNumeros
        ) {
            this.limparOpcaoOutrosNumeros = limparOpcaoOutrosNumeros;
            return this;
        }

        public Builder setQtdTotalSelecionadosSuperSete(
                int qtdTotalSelecionadosSuperSete
        ) {
            this.qtdTotalSelecionadosSuperSete = qtdTotalSelecionadosSuperSete;
            return this;
        }

        public Builder setJogosLoteca(
                int jogosLoteca
        ) {
            this.jogosLoteca = jogosLoteca;
            return this;
        }

        public Builder setSimplesLoteca(
                int simplesLoteca
        ) {
            this.simplesLoteca = simplesLoteca;
            return this;
        }

        public Builder setDuplasLoteca(
                int duplasLoteca
        ) {
            this.duplasLoteca = duplasLoteca;
            return this;
        }

        public Builder setTriplasLoteca(
                int triplasLoteca
        ) {
            this.triplasLoteca = triplasLoteca;
            return this;
        }
        public Builder setQuantidadeSurpresinhas(
                int quantidadeSurpresinhas
        ) {
            this.quantidadeSurpresinhas = quantidadeSurpresinhas;
            return this;
        }
        public Builder setQuantidadeNumerosSurpresinha(
                int quantidadeNumerosSurpresinha
        ) {
            this.quantidadeNumerosSurpresinha = quantidadeNumerosSurpresinha;
            return this;
        }
        public Builder setQuantidadeTrevosSurpresinha(
                int quantidadeTrevosSurpresinha
        ) {
            this.quantidadeTrevosSurpresinha = quantidadeTrevosSurpresinha;
            return this;
        }

        public Builder setBotaoCompletarVisivel(
                boolean botaoCompletarVisivel
        ) {
            this.botaoCompletarVisivel = botaoCompletarVisivel;
            return this;
        }


        public Builder setBotaoCompletarHabilitado(
                boolean botaoCompletarHabilitado
        ) {
            this.botaoCompletarHabilitado = botaoCompletarHabilitado;
            return this;
        }

        public Builder setBotaoAdicionarHabilitado(
                boolean botaoAdicionarHabilitado
        ) {
            this.botaoAdicionarHabilitado = botaoAdicionarHabilitado;
            return this;
        }
        public Builder setBotaoNextStepHabilitado(
                boolean botaoNextStepHabilitado
        ) {
            this.botaoNextStepHabilitado = botaoNextStepHabilitado;
            return this;
        }


        public Builder setBotaoLimparHabilitado(
                boolean botaoLimparHabilitado
        ) {
            this.botaoLimparHabilitado = botaoLimparHabilitado;
            return this;
        }

        public Builder setValorAposta(
                BigDecimal valorAposta
        ) {
            this.valorAposta = valorAposta != null
                    ? valorAposta
                    : BigDecimal.ZERO;
            return this;
        }

        public Builder setValorGrande(
                boolean valorGrande
        ) {
            this.valorGrande = valorGrande;
            return this;
        }

        public SimulaUiState build() {
            return new SimulaUiState(this);
        }
    }
}