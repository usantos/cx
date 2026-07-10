package br.gov.caixa.loterias.apostas.event;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CarrinhoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroJogoDTO;
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
        private final int qtdTotalSelecionadosSuperSete;
        private final int qtdPartidasSelecionadas;
        private final int qtdPartidasTotal;
        private final boolean escolhaTimeCoracaoSurpresinha;
        private final boolean escolhaTimeBtnAtivado;
        private final boolean telaSelecaoTimeAtivado;
        private final boolean equipeSelecionadaValida;
        private final boolean fragmentCartela;

        public CompletarApostaClicado(
                ModalidadeEnum tipoJogo,
                int qtdDezenasSelecionadas,
                int qtdDezenasPossiveisSelecionado,
                int qtdTotalSelecionadosSuperSete,
                int qtdPartidasSelecionadas,
                int qtdPartidasTotal,
                boolean escolhaTimeCoracaoSurpresinha,
                boolean escolhaTimeBtnAtivado,
                boolean telaSelecaoTimeAtivado,
                boolean equipeSelecionadaValida,
                boolean fragmentCartela
        ) {
            this.tipoJogo = tipoJogo;
            this.qtdDezenasSelecionadas = qtdDezenasSelecionadas;
            this.qtdDezenasPossiveisSelecionado = qtdDezenasPossiveisSelecionado;
            this.qtdTotalSelecionadosSuperSete = qtdTotalSelecionadosSuperSete;
            this.qtdPartidasSelecionadas = qtdPartidasSelecionadas;
            this.qtdPartidasTotal = qtdPartidasTotal;
            this.escolhaTimeCoracaoSurpresinha = escolhaTimeCoracaoSurpresinha;
            this.escolhaTimeBtnAtivado = escolhaTimeBtnAtivado;
            this.telaSelecaoTimeAtivado = telaSelecaoTimeAtivado;
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

        public boolean isEscolhaTimeCoracaoSurpresinha() {
            return escolhaTimeCoracaoSurpresinha;
        }

        public boolean isEscolhaTimeBtnAtivado() {
            return escolhaTimeBtnAtivado;
        }

        public boolean isTelaSelecaoTimeAtivado() {
            return telaSelecaoTimeAtivado;
        }

        public boolean isEquipeSelecionadaValida() {
            return equipeSelecionadaValida;
        }

        public boolean isFragmentCartela() {
            return fragmentCartela;
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
        private final boolean telaSelecaoTimeAtivado;
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
                boolean telaSelecaoTimeAtivado,
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
            this.telaSelecaoTimeAtivado = telaSelecaoTimeAtivado;
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

        public boolean isTelaSelecaoTimeAtivado() {
            return telaSelecaoTimeAtivado;
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

        private final int qtdDezenasSelecionadas;
        private final int qtdDezenasPossiveisSelecionado;

        private final int qtdTotalSelecionadosSuperSete;

        private final int qtdPartidasSelecionadas;
        private final int qtdPartidasTotal;

        private final int duplosLoteca;
        private final int triplosLoteca;

        private final boolean possuiPlacarLotogol;
        private final boolean telaSelecaoTimeAtivado;
        private final boolean equipeSelecionadaValida;
        private final boolean escolhaTimeCoracaoSurpresinha;

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
                boolean telaSelecaoTimeAtivado,
                boolean equipeSelecionadaValida,
                boolean escolhaTimeCoracaoSurpresinha
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
            this.telaSelecaoTimeAtivado = telaSelecaoTimeAtivado;
            this.equipeSelecionadaValida = equipeSelecionadaValida;
            this.escolhaTimeCoracaoSurpresinha = escolhaTimeCoracaoSurpresinha;
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

        public boolean isPossuiPlacarLotogol() {
            return possuiPlacarLotogol;
        }

        public boolean isTelaSelecaoTimeAtivado() {
            return telaSelecaoTimeAtivado;
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

    public static class CarrinhoCarregado extends SimulaUiEvent {

        private final CarrinhoDTO carrinho;

        public CarrinhoCarregado(CarrinhoDTO carrinho) {
            this.carrinho = carrinho;
        }

        public CarrinhoDTO getCarrinho() {
            return carrinho;
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
}