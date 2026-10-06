package br.gov.caixa.loterias.apostas.view.features.volantenovo.presentation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaFavoritaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroMesDeSorte;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.utils.BarraTituloDTO;

public abstract class SimulaUiEvent {

    private SimulaUiEvent() {
    }

    public static final class TelaInicializada extends SimulaUiEvent {

        private final ModalidadeEnum modalidade;
        private final boolean especial;
        private final ParametroJogoDTO parametroSimulacao;

        public TelaInicializada(
                ModalidadeEnum modalidade,
                boolean especial,
                ParametroJogoDTO parametroSimulacao
        ) {
            this.modalidade = modalidade;
            this.especial = especial;
            this.parametroSimulacao = parametroSimulacao;
        }

        public ModalidadeEnum getModalidade() {
            return modalidade;
        }

        public boolean isEspecial() {
            return especial;
        }

        public ParametroJogoDTO getParametroSimulacao() {
            return parametroSimulacao;
        }
    }

    public static final class LimparApostaSolicitado extends SimulaUiEvent {
    }

    public static final class LimpezaApostaRenderizada extends SimulaUiEvent {
    }

    public static final class CompletarApostaClicado extends SimulaUiEvent {

        private final ModalidadeEnum tipoJogo;
        private final int qtdDezenasSelecionadas;
        private final int qtdDezenasPossiveisSelecionado;

        public CompletarApostaClicado(
                ModalidadeEnum tipoJogo,
                int qtdDezenasSelecionadas,
                int qtdDezenasPossiveisSelecionado
        ) {
            this.tipoJogo = tipoJogo;
            this.qtdDezenasSelecionadas = qtdDezenasSelecionadas;
            this.qtdDezenasPossiveisSelecionado = qtdDezenasPossiveisSelecionado;
        }

        public ModalidadeEnum getTipoJogo() {
            return tipoJogo;
        }

        public int getQtdDezenasSelecionadas() {
            return qtdDezenasSelecionadas;
        }

        public int getQtdDezenasPossiveisSelecionado() {
            return qtdDezenasPossiveisSelecionado;
        }

    }
    public static final class NextStepClicado
            extends SimulaUiEvent {

        private final ModalidadeEnum tipoJogo;

        public NextStepClicado(
                ModalidadeEnum tipoJogo
        ) {
            this.tipoJogo = tipoJogo;
        }

        public ModalidadeEnum getTipoJogo() {
            return tipoJogo;
        }
    }

    public static final class AdicionarCarrinhoClicado extends SimulaUiEvent {

        private final ModalidadeEnum tipoJogo;
        private final int qtdDezenasSelecionadas;
        private final int qtdDezenasPossiveisSelecionado;
        private final int qtdTotalSelecionadosSuperSete;
        private final int qtdPartidasSelecionadas;
        private final int qtdPartidasTotal;
        private final int duplosLoteca;
        private final int triplosLoteca;
        private final boolean escolhaTimeCoracaoSurpresinha;
        private final boolean equipeSelecionadaValida;
        private final boolean fragmentCartela;

        public AdicionarCarrinhoClicado(
                ModalidadeEnum tipoJogo,
                int qtdDezenasSelecionadas,
                int qtdDezenasPossiveisSelecionado,
                int qtdTotalSelecionadosSuperSete,
                int qtdPartidasSelecionadas,
                int qtdPartidasTotal,
                int duplosLoteca,
                int triplosLoteca,
                boolean escolhaTimeCoracaoSurpresinha,
                boolean equipeSelecionadaValida,
                boolean fragmentCartela
        ) {
            this.tipoJogo = tipoJogo;
            this.qtdDezenasSelecionadas = qtdDezenasSelecionadas;
            this.qtdDezenasPossiveisSelecionado = qtdDezenasPossiveisSelecionado;
            this.qtdTotalSelecionadosSuperSete = qtdTotalSelecionadosSuperSete;
            this.qtdPartidasSelecionadas = qtdPartidasSelecionadas;
            this.qtdPartidasTotal = qtdPartidasTotal;
            this.duplosLoteca = duplosLoteca;
            this.triplosLoteca = triplosLoteca;
            this.escolhaTimeCoracaoSurpresinha = escolhaTimeCoracaoSurpresinha;
            this.equipeSelecionadaValida = equipeSelecionadaValida;
            this.fragmentCartela = fragmentCartela;
        }

        public ModalidadeEnum getTipoJogo() {
            return tipoJogo;
        }

        public int getQtdDezenasSelecionadas() {
            return qtdDezenasSelecionadas;
        }

        public int getQtdDezenasPossiveisSelecionado() {
            return qtdDezenasPossiveisSelecionado;
        }

        public int getQtdTotalSelecionadosSuperSete() {
            return qtdTotalSelecionadosSuperSete;
        }

        public int getQtdPartidasSelecionadas() {
            return qtdPartidasSelecionadas;
        }

        public int getQtdPartidasTotal() {
            return qtdPartidasTotal;
        }

        public int getDuplosLoteca() {
            return duplosLoteca;
        }

        public int getTriplosLoteca() {
            return triplosLoteca;
        }

        public boolean isEscolhaTimeCoracaoSurpresinha() {
            return escolhaTimeCoracaoSurpresinha;
        }

        public boolean isEquipeSelecionadaValida() {
            return equipeSelecionadaValida;
        }

        public boolean isFragmentCartela() {
            return fragmentCartela;
        }
    }

    public static final class ComandoCompletarApostaConsumido extends SimulaUiEvent {
    }

    public static class CartelaAlterada extends SimulaUiEvent {

        private final ModalidadeEnum tipoJogo;
        private final boolean opcaoOutrosNumerosSelecionada;
        private final boolean exibirOpcaoOutrosNumeros;

        private final int qtdDezenasSelecionadas;
        private final int qtdDezenasPossiveisSelecionado;

        private final int qtdTotalSelecionadosSuperSete;

        private final int qtdPartidasSelecionadas;
        private final int qtdPartidasTotal;

        private final int duplosLoteca;
        private final int triplosLoteca;

        private final boolean possuiPlacarLotogol;
        private final boolean equipeSelecionadaValida;
        private final boolean escolhaTimeCoracaoSurpresinha;
        private final boolean etapaComplementarCompleta;
        private final List<Integer> dezenasSelecionadas;

        public CartelaAlterada(
                ModalidadeEnum tipoJogo,
                int qtdDezenasSelecionadas,
                int qtdDezenasPossiveisSelecionado,
                int qtdTotalSelecionadosSuperSete,
                int qtdPartidasSelecionadas,
                int qtdPartidasTotal,
                int duplosLoteca,
                int triplosLoteca,
                boolean possuiPlacarLotogol,
                boolean equipeSelecionadaValida,
                boolean escolhaTimeCoracaoSurpresinha,
                boolean opcaoOutrosNumerosSelecionada,
                boolean exibirOpcaoOutrosNumeros,
                boolean etapaComplementarCompleta,
                List<Integer> dezenasSelecionadas
        ) {
            this.tipoJogo = tipoJogo;
            this.qtdDezenasSelecionadas = qtdDezenasSelecionadas;
            this.qtdDezenasPossiveisSelecionado = qtdDezenasPossiveisSelecionado;
            this.qtdTotalSelecionadosSuperSete = qtdTotalSelecionadosSuperSete;
            this.qtdPartidasSelecionadas = qtdPartidasSelecionadas;
            this.qtdPartidasTotal = qtdPartidasTotal;
            this.duplosLoteca = duplosLoteca;
            this.triplosLoteca = triplosLoteca;
            this.possuiPlacarLotogol = possuiPlacarLotogol;
            this.equipeSelecionadaValida = equipeSelecionadaValida;
            this.escolhaTimeCoracaoSurpresinha = escolhaTimeCoracaoSurpresinha;
            this.opcaoOutrosNumerosSelecionada = opcaoOutrosNumerosSelecionada;
            this.exibirOpcaoOutrosNumeros = exibirOpcaoOutrosNumeros;
            this.etapaComplementarCompleta = etapaComplementarCompleta;
            this.dezenasSelecionadas = dezenasSelecionadas;
        }
        public Boolean isEtapaComplementarCompleta(){return etapaComplementarCompleta;}

        public ModalidadeEnum getTipoJogo() {
            return tipoJogo;
        }
        public List<Integer> getDezenasSelecionadas(){return dezenasSelecionadas;}
        public Boolean isExibirOpcaoOutrosNumeros(){return exibirOpcaoOutrosNumeros;}
        public Boolean isOpcaoOutrosNumerosSelecionada(){return opcaoOutrosNumerosSelecionada;}

        public int getQtdDezenasSelecionadas() {
            return qtdDezenasSelecionadas;
        }

        public int getQtdDezenasPossiveisSelecionado() {
            return qtdDezenasPossiveisSelecionado;
        }

        public int getQtdTotalSelecionadosSuperSete() {
            return qtdTotalSelecionadosSuperSete;
        }

        public int getQtdPartidasSelecionadas() {
            return qtdPartidasSelecionadas;
        }

        public int getQtdPartidasTotal() {
            return qtdPartidasTotal;
        }

        public int getDuplosLoteca() {
            return duplosLoteca;
        }

        public int getTriplosLoteca() {
            return triplosLoteca;
        }

        public boolean isPossuiPlacarLotogol() {
            return possuiPlacarLotogol;
        }

        public boolean isEquipeSelecionadaValida() {
            return equipeSelecionadaValida;
        }

        public boolean isEscolhaTimeCoracaoSurpresinha() {
            return escolhaTimeCoracaoSurpresinha;
        }
    }

    public static class ValorApostaAlterado extends SimulaUiEvent {

        private final BigDecimal valor;

        public ValorApostaAlterado(BigDecimal valor) {
            this.valor = valor;
        }

        public BigDecimal getValor() {
            return valor;
        }
    }

    public static class CarregarCarrinho extends SimulaUiEvent {
    }

    public static final class ComoJogarClicado extends SimulaUiEvent {
    }

    public static final class DialogComoJogarConsumido extends SimulaUiEvent {
    }
    public static class LotecaAtualizada extends SimulaUiEvent {

        private final int jogos;
        private final int simples;
        private final int duplas;
        private final int triplas;

        public LotecaAtualizada(
                int jogos,
                int simples,
                int duplas,
                int triplas
        ) {
            this.jogos = jogos;
            this.simples = simples;
            this.duplas = duplas;
            this.triplas = triplas;
        }

        public int getJogos() {
            return jogos;
        }

        public int getSimples() {
            return simples;
        }

        public int getDuplas() {
            return duplas;
        }

        public int getTriplas() {
            return triplas;
        }
    }

    public static final class PalpitesLotecaAtualizado extends SimulaUiEvent {

        private final int quantidadePalpites;

        public PalpitesLotecaAtualizado(
                int quantidadePalpites
        ) {
            this.quantidadePalpites = quantidadePalpites;
        }

        public int getQuantidadePalpites() {
            return quantidadePalpites;
        }
    }

    public static final class InicializarTeimosinhas extends SimulaUiEvent {
        private final List<String> qntTeimosinhas;
        private final List<Integer> qntConcursos;
        private final int qntConcursoSelecionado;
        private final boolean especial;
        private final String textoInicialTeimosinha;

        public InicializarTeimosinhas(
                List<String> qntTeimosinhas,
                List<Integer> qntConcursos,
                int qntConcursoSelecionado,
                boolean especial,
                String textoInicialTeimosinha
        ) {
            this.qntTeimosinhas = qntTeimosinhas != null ? qntTeimosinhas : new ArrayList<>();
            this.qntConcursos = qntConcursos != null ? qntConcursos : new ArrayList<>();
            this.qntConcursoSelecionado = qntConcursoSelecionado;
            this.especial = especial;
            this.textoInicialTeimosinha = textoInicialTeimosinha;
        }

        public List<String> getQntTeimosinhas() {
            return qntTeimosinhas;
        }

        public List<Integer> getQntConcursos() {
            return qntConcursos;
        }

        public int getQntConcursoSelecionado() {
            return qntConcursoSelecionado;
        }

        public boolean isEspecial() {
            return especial;
        }

        public String getTextoInicialTeimosinha() {
            return textoInicialTeimosinha;
        }
    }

    public static final class ClicarTeimosinhas extends SimulaUiEvent {
        public ClicarTeimosinhas() {
        }
    }

    public static final class ConfirmarTeimosinha extends SimulaUiEvent {
        private final int position;

        public ConfirmarTeimosinha(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    public static final class ConsumirEventoUnico extends SimulaUiEvent {
        public ConsumirEventoUnico() {
        }
    }

    public static final class ValorApostaAtualizado extends SimulaUiEvent {
        public ValorApostaAtualizado() {
        }
    }


    public static final class AdicionarCarrinhoSolicitado extends SimulaUiEvent {

        private final IdentificaoDeUmaApostaDas8Modalidades aposta;
        private final BarraTituloDTO barraTituloDTO;
        private final boolean escolhaTimeCoracaoSurpresinha;
        private final ParametroEquipe equipeSelecionada;

        public AdicionarCarrinhoSolicitado(
                IdentificaoDeUmaApostaDas8Modalidades aposta,
                BarraTituloDTO barraTituloDTO,
                boolean escolhaTimeCoracaoSurpresinha,
                ParametroEquipe equipeSelecionada
        ) {
            this.aposta = aposta;
            this.barraTituloDTO = barraTituloDTO;
            this.escolhaTimeCoracaoSurpresinha = escolhaTimeCoracaoSurpresinha;
            this.equipeSelecionada = equipeSelecionada;
        }

        public IdentificaoDeUmaApostaDas8Modalidades getAposta() {
            return aposta;
        }

        public BarraTituloDTO getBarraTituloDTO() {
            return barraTituloDTO;
        }

        public boolean isEscolhaTimeCoracaoSurpresinha() {
            return escolhaTimeCoracaoSurpresinha;
        }

        public ParametroEquipe getEquipeSelecionada() {
            return equipeSelecionada;
        }
    }

    public static final class ConfirmarAdicionarCarrinho extends SimulaUiEvent {
    }

    public static final class ComandoCarrinhoConsumido extends SimulaUiEvent {
    }

    public static class InicializarQuantidadeNumeros extends SimulaUiEvent {

        private final List<String> labelsQuantidadeNumeros;
        private final List<Integer> qtdPrognosticosQuantidadeNumeros;
        private final int qtdDezenasSelecionadaInicial;

        public InicializarQuantidadeNumeros(
                List<String> labelsQuantidadeNumeros,
                List<Integer> qtdPrognosticosQuantidadeNumeros,
                int qtdDezenasSelecionadaInicial
        ) {
            this.labelsQuantidadeNumeros = labelsQuantidadeNumeros;
            this.qtdPrognosticosQuantidadeNumeros = qtdPrognosticosQuantidadeNumeros;
            this.qtdDezenasSelecionadaInicial = qtdDezenasSelecionadaInicial;
        }

        public List<String> getLabelsQuantidadeNumeros() {
            return labelsQuantidadeNumeros;
        }

        public List<Integer> getQtdPrognosticosQuantidadeNumeros() {
            return qtdPrognosticosQuantidadeNumeros;
        }

        public int getQtdDezenasSelecionadaInicial() {
            return qtdDezenasSelecionadaInicial;
        }
    }

    public static class ClicarQuantidadeNumeros extends SimulaUiEvent {
    }

    public static class SelecionarQuantidadeNumeros extends SimulaUiEvent {

        private final int position;

        public SelecionarQuantidadeNumeros(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    public static class ConfirmarQuantidadeNumeros extends SimulaUiEvent {

        private final int position;

        public ConfirmarQuantidadeNumeros(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }
    public static class ComandoAnalyticsConsumido
            extends SimulaUiEvent {
    }
    public static class ComandoAnimacaoConsumido
            extends SimulaUiEvent {
    }


    public static class DialogQuantidadeNumerosConsumido extends SimulaUiEvent {
    }

    public static class AvisoQuantidadeNumerosConsumido extends SimulaUiEvent {
    }

    public static class ValorApostaQuantidadeNumerosAtualizado extends SimulaUiEvent {
    }

    public static final class CarrinhoClicado
            extends SimulaUiEvent {
    }
    public static final class AbrirCarrinhoConsumido
            extends SimulaUiEvent {
    }
    public static final class AlterarSurpresinha extends SimulaUiEvent {

        private final boolean habilitada;

        public AlterarSurpresinha(
                boolean habilitada
        ) {
            this.habilitada = habilitada;
        }

        public boolean isHabilitada() {
            return habilitada;
        }
    }
    public static class FavoritarApostaClicado extends SimulaUiEvent {
    }

    public static class DialogFavoritarApostaConsumido extends SimulaUiEvent {
    }
    public static class SalvarApostaFavoritaSolicitado
            extends SimulaUiEvent {

        private final ApostaFavoritaDTO apostaFavorita;
        private final String nomeAposta;
        private final Object numerosSelecionados;

        public SalvarApostaFavoritaSolicitado(
                ApostaFavoritaDTO apostaFavorita,
                String nomeAposta,
                Object numerosSelecionados
        ) {
            this.apostaFavorita = apostaFavorita;
            this.nomeAposta = nomeAposta;
            this.numerosSelecionados = numerosSelecionados;
        }

        public ApostaFavoritaDTO getApostaFavorita() {
            return apostaFavorita;
        }

        public String getNomeAposta() {
            return nomeAposta;
        }

        public Object getNumerosSelecionados() {
            return numerosSelecionados;
        }
    }
    public static class ClicarQuantidadeTrevos
            extends SimulaUiEvent {

        private final ParametroJogoDTO parametro;
        private final int quantidadeNumeros;

        public ClicarQuantidadeTrevos(
                ParametroJogoDTO parametro,
                int quantidadeNumeros
        ) {
            this.parametro = parametro;
            this.quantidadeNumeros = quantidadeNumeros;
        }

        public ParametroJogoDTO getParametro() {
            return parametro;
        }

        public int getQuantidadeNumeros() {
            return quantidadeNumeros;
        }
    }
    public static class ConfirmarQuantidadeTrevos
            extends SimulaUiEvent {

        private final int position;

        public ConfirmarQuantidadeTrevos(
                int position
        ) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }
    public static class SelecionarQuantidadeTrevos
            extends SimulaUiEvent {

        private final int position;

        public SelecionarQuantidadeTrevos(
                int position
        ) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    public static class DialogQuantidadeTrevosConsumido
            extends SimulaUiEvent {
    }
    public static class ConfirmarSalvarApostaFavorita
            extends SimulaUiEvent {

        private final ApostaFavoritaDTO apostaFavorita;

        public ConfirmarSalvarApostaFavorita(
                ApostaFavoritaDTO apostaFavorita
        ) {
            this.apostaFavorita = apostaFavorita;
        }

        public ApostaFavoritaDTO getApostaFavorita() {
            return apostaFavorita;
        }
    }
    public static class ComandoFavoritarApostaConsumido
            extends SimulaUiEvent {
    }

    public static final class ComandoTelaSimulaConsumido extends SimulaUiEvent {
    }
    public static class AvisoTrevosMaximoConsumido
            extends SimulaUiEvent {
    }

    public static final class ToggleOutrosNumeros
            extends SimulaUiEvent {
    }
    public static class TrevosAlterados
            extends SimulaUiEvent {

        private final int quantidadeTrevosSelecionados;
        private final int quantidadeTrevosNecessaria;
        private final ParametroValorApostaDTO valorAposta;
        private final List<Integer> trevosSelecionados;

        public TrevosAlterados(
                List<Integer> trevosSelecionados,
                int quantidadeTrevosNecessaria,
                ParametroValorApostaDTO valorAposta
        ) {
            this(
                    trevosSelecionados,
                    trevosSelecionados != null
                            ? trevosSelecionados.size()
                            : 0,
                    quantidadeTrevosNecessaria,
                    valorAposta
            );
        }

        private TrevosAlterados(
                List<Integer> trevosSelecionados,
                int quantidadeTrevosSelecionados,
                int quantidadeTrevosNecessaria,
                ParametroValorApostaDTO valorAposta
        ) {
            this.trevosSelecionados =
                    trevosSelecionados != null
                            ? new ArrayList<>(trevosSelecionados)
                            : new ArrayList<>();

            this.quantidadeTrevosSelecionados =
                    quantidadeTrevosSelecionados;

            this.quantidadeTrevosNecessaria =
                    quantidadeTrevosNecessaria;

            this.valorAposta =
                    valorAposta;
        }

        public int getQuantidadeTrevosSelecionados() {
            return quantidadeTrevosSelecionados;
        }

        public int getQuantidadeTrevosNecessaria() {
            return quantidadeTrevosNecessaria;
        }

        public ParametroValorApostaDTO getValorAposta() {
            return valorAposta;
        }

        public List<Integer> getTrevosSelecionados() {
            return new ArrayList<>(trevosSelecionados);
        }
    }

    public static class QuantidadeTrevosAlterada
            extends SimulaUiEvent {

        private final ParametroValorApostaDTO valorSelecionado;

        public QuantidadeTrevosAlterada(
                ParametroValorApostaDTO valorSelecionado
        ) {
            this.valorSelecionado =
                    valorSelecionado;
        }

        public ParametroValorApostaDTO getValorSelecionado() {
            return valorSelecionado;
        }
    }
    public static final class MesSorteAlterado
            extends SimulaUiEvent {

        private final boolean mesSelecionado;

        public MesSorteAlterado(
                boolean mesSelecionado
        ) {
            this.mesSelecionado = mesSelecionado;
        }

        public boolean isMesSelecionado() {
            return mesSelecionado;
        }
    }

    public static class ToggleTrevo
            extends SimulaUiEvent {

        private final int numeroTrevo;

        public ToggleTrevo(int numeroTrevo) {
            this.numeroTrevo = numeroTrevo;
        }

        public int getNumeroTrevo() {
            return numeroTrevo;
        }
    }
    public static final class AtualizarQuantidadeSurpresinhas
            extends SimulaUiEvent {

        private final int quantidade;

        public AtualizarQuantidadeSurpresinhas(
                int quantidade
        ) {
            this.quantidade = quantidade;
        }

        public int getQuantidade() {
            return quantidade;
        }
    }
    public static final class AtualizarQuantidadeNumerosSurpresinha
            extends SimulaUiEvent {

        private final int quantidade;

        public AtualizarQuantidadeNumerosSurpresinha(
                int quantidade
        ) {
            this.quantidade = quantidade;
        }

        public int getQuantidade() {
            return quantidade;
        }
    }
    public static final class AtualizarQuantidadeTrevosSurpresinha
            extends SimulaUiEvent {

        private final int quantidade;

        public AtualizarQuantidadeTrevosSurpresinha(
                int quantidade
        ) {
            this.quantidade = quantidade;
        }

        public int getQuantidade() {
            return quantidade;
        }
    }
    public static class QuantidadeNumerosAtualizadaInternamente
            extends SimulaUiEvent {

        private final int quantidade;

        public QuantidadeNumerosAtualizadaInternamente(
                int quantidade
        ) {
            this.quantidade = quantidade;
        }

        public int getQuantidade() {
            return quantidade;
        }
    }
    public static class QtdConcursoAtualizadaInternamente
            extends SimulaUiEvent {

        private final int quantidade;

        public QtdConcursoAtualizadaInternamente(
                int quantidade
        ) {
            this.quantidade = quantidade;
        }

        public int getQuantidade() {
            return quantidade;
        }
    }
    public static class InicializarParametrosAposta
            extends SimulaUiEvent {

        private final int qtdDezenas;
        private final int qtdConcurso;

        public InicializarParametrosAposta(
                int qtdDezenas,
                int qtdConcurso
        ) {
            this.qtdDezenas = qtdDezenas;
            this.qtdConcurso = qtdConcurso;
        }

        public int getQtdDezenas() {
            return qtdDezenas;
        }

        public int getQtdConcurso() {
            return qtdConcurso;
        }
    }
    public static final class VoltarParaSurpresinha
            extends SimulaUiEvent {
    }
    public static final class SelecionarMesDaSorte
            extends SimulaUiEvent {

        private final ParametroMesDeSorte mes;

        public SelecionarMesDaSorte(
                ParametroMesDeSorte mes
        ) {
            this.mes = mes;
        }

        public ParametroMesDeSorte getMes() {
            return mes;
        }
    }
    public static final class LimparMesDaSorte
            extends SimulaUiEvent {
    }
    public static final class SelecionarTimeCoracao
            extends SimulaUiEvent {

        private final ParametroEquipe equipe;

        public SelecionarTimeCoracao(
                ParametroEquipe equipe) {
            this.equipe = equipe;
        }

        public ParametroEquipe getEquipe() {
            return equipe;
        }
    }
    public static final class SolicitarSelecaoTimeCoracao
            extends SimulaUiEvent {
    }
    public static class VoltarEtapa extends SimulaUiEvent {
    }

    public static final class AdicionarSurpresinhaCarrinho
            extends SimulaUiEvent {

        private final boolean espelhoLotomania;

        public AdicionarSurpresinhaCarrinho(
                boolean espelhoLotomania
        ) {
            this.espelhoLotomania = espelhoLotomania;
        }

        public boolean isEspelhoLotomania() {
            return espelhoLotomania;
        }
    }

    public static final class ResetarAvisoPalpitesLoteca
            extends SimulaUiEvent {
    }
}
