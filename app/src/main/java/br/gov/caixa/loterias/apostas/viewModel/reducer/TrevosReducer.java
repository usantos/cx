package br.gov.caixa.loterias.apostas.viewModel.reducer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.effect.ScreenSimulaEffect;
import br.gov.caixa.loterias.apostas.event.SimulaUiEvent;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroValorApostaDTO;
import br.gov.caixa.loterias.apostas.state.SimulaUiState;
import br.gov.caixa.loterias.apostas.utils.EtapaAposta;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class TrevosReducer {

    public SimulaUiState toggleTrevo(
            SimulaUiState atual,
            SimulaUiEvent.ToggleTrevo event
    ) {

        if (atual == null || event == null) {
            return atual;
        }

        ParametroValorApostaDTO valorAtual =
                atual.getValorTrevosSelecionado();

        if (valorAtual == null
                || valorAtual.getNumeroTrevos() == null) {
            return atual;
        }

        int quantidadeNecessaria =
                valorAtual.getNumeroTrevos();

        List<Integer> trevos =
                criarListaTrevosSegura(
                        atual.getTrevosSelecionados()
                );

        Integer numero =
                event.getNumeroTrevo();

        boolean jaSelecionado =
                trevos.contains(numero);

        if (jaSelecionado) {

            trevos.remove(numero);

        } else {

            if (trevos.size() >= quantidadeNecessaria) {
                return atual.toBuilder()
                        .setExibirAvisoTrevosMaximo(true)
                        .build();
            }

            trevos.add(numero);
        }

        int quantidadeSelecionada =
                trevos.size();

        boolean apostaCompleta =
                quantidadeSelecionada == quantidadeNecessaria;

        BigDecimal valorAposta =
                aplicarQuantidadeConcursos(
                        atual,
                        valorAtual.getValor()
                );

        return atual.toBuilder()
                .setEtapaAposta(EtapaAposta.TREVOS)
                .setTrevosSelecionados(trevos)
                .setValorTrevosSelecionado(valorAtual)
                .setQuantidadeTrevosSelecionada(quantidadeNecessaria)
                .setValorAposta(valorAposta)
                .setValorGrande(isValorGrande(valorAposta))
                .setBotaoAdicionarHabilitado(apostaCompleta)
                .setBotaoCompletarHabilitado(!apostaCompleta)
                .setBotaoCompletarVisivel(!apostaCompleta)
                .setBotaoLimparHabilitado(quantidadeSelecionada > 0)
                .setMostrarBotaoLimparAposta(quantidadeSelecionada > 0)
                .setMostrarSalvarAposta(apostaCompleta)
                .setTextoSelecionados(
                        montarTextoTrevosSelecionados(
                                quantidadeSelecionada,
                                quantidadeNecessaria
                        )
                )
                .build();
    }

    public SimulaUiState trevosAlterados(
            SimulaUiState atual,
            SimulaUiEvent.TrevosAlterados event
    ) {

        if (atual == null
                || event == null
                || event.getValorAposta() == null) {
            return atual;
        }

        int quantidadeSelecionada =
                event.getQuantidadeTrevosSelecionados();

        int quantidadeNecessaria =
                event.getQuantidadeTrevosNecessaria();

        boolean possuiTrevosSelecionados =
                quantidadeSelecionada > 0;

        boolean apostaCompleta =
                quantidadeSelecionada == quantidadeNecessaria;

        BigDecimal valorAposta =
                aplicarQuantidadeConcursos(
                        atual,
                        event.getValorAposta().getValor()
                );

        List<Integer> trevosSelecionados =
                criarListaTrevosSegura(
                        event.getTrevosSelecionados()
                );

        return atual.toBuilder()
                .setEtapaAposta(EtapaAposta.TREVOS)
                .setBotaoAdicionarHabilitado(apostaCompleta)
                .setBotaoCompletarHabilitado(!apostaCompleta)
                .setBotaoCompletarVisivel(!apostaCompleta)
                .setBotaoLimparHabilitado(possuiTrevosSelecionados)
                .setMostrarBotaoLimparAposta(possuiTrevosSelecionados)
                .setMostrarSalvarAposta(apostaCompleta)
                .setTrevosSelecionados(trevosSelecionados)
                .setValorAposta(valorAposta)
                .setValorGrande(isValorGrande(valorAposta))
                .setTextoSelecionados(
                        montarTextoTrevosSelecionados(
                                quantidadeSelecionada,
                                quantidadeNecessaria
                        )
                )
                .build();
    }

    public SimulaUiState quantidadeTrevosAlterada(
            SimulaUiState atual,
            SimulaUiEvent.QuantidadeTrevosAlterada event
    ) {

        if (atual == null
                || event == null
                || event.getValorSelecionado() == null) {
            return atual;
        }

        ParametroValorApostaDTO valorSelecionado =
                event.getValorSelecionado();

        BigDecimal valorAposta =
                aplicarQuantidadeConcursos(
                        atual,
                        valorSelecionado.getValor()
                );

        int quantidadeTrevos =
                obterQuantidadeTrevos(
                        valorSelecionado
                );

        return atual.toBuilder()
                .setEtapaAposta(EtapaAposta.TREVOS)
                .setValorAposta(valorAposta)
                .setValorGrande(isValorGrande(valorAposta))
                .setTextoSelecionados(
                        montarTextoTrevosSelecionados(
                                0,
                                quantidadeTrevos
                        )
                )
                .setQuantidadeTrevosSelecionada(quantidadeTrevos)
                .setTrevosSelecionados(new ArrayList<Integer>())
                .setValorTrevosSelecionado(valorSelecionado)
                .setTextoBotaoQuantidadeTrevos(
                        montarTextoBotaoTrevos(
                                quantidadeTrevos
                        )
                )
                .build();
    }

    public SimulaUiState clicarQuantidadeTrevos(
            SimulaUiState atual,
            SimulaUiEvent.ClicarQuantidadeTrevos event
    ) {

        if (atual == null
                || event == null
                || event.getParametro() == null) {
            return atual;
        }

        List<ParametroValorApostaDTO> valores =
                event.getParametro()
                        .getValoresTrevoByNumero(
                                event.getQuantidadeNumeros()
                        );

        if (valores == null || valores.isEmpty()) {
            return atual;
        }

        List<String> labels =
                montarLabelsTrevos(valores);

        String subtitulo =
                "Para aposta com "
                        + event.getQuantidadeNumeros()
                        + " numeros";

        return atual.toBuilder()
                .setValoresTrevos(valores)
                .setLabelsQuantidadeTrevos(labels)
                .setSubtituloTrevos(subtitulo)
                .setExibirDialogQuantidadeTrevos(true)
                .build();
    }

    public SimulaUiState selecionarQuantidadeTrevos(
            SimulaUiState atual,
            SimulaUiEvent.SelecionarQuantidadeTrevos event
    ) {

        if (atual == null || event == null) {
            return atual;
        }

        List<ParametroValorApostaDTO> valores =
                atual.getValoresTrevos();

        int position =
                event.getPosition();

        if (valores == null
                || position < 0
                || position >= valores.size()) {
            return atual;
        }

        boolean deveAvisar =
                position > 0
                        && !atual.isAvisouTrevosMaximo();

        return atual.toBuilder()
                .setPosicaoTrevosSelecionada(position)
                .setExibirAvisoTrevosMaximo(deveAvisar)
                .setAvisouTrevosMaximo(
                        atual.isAvisouTrevosMaximo()
                                || deveAvisar
                )
                .build();
    }

    public SimulaUiState confirmarQuantidadeTrevos(
            SimulaUiState atual,
            SimulaUiEvent.ConfirmarQuantidadeTrevos event
    ) {

        if (atual == null || event == null) {
            return atual;
        }

        List<ParametroValorApostaDTO> valores =
                atual.getValoresTrevos();

        if (valores == null || valores.isEmpty()) {
            return atual;
        }

        int position =
                event.getPosition();

        if (position < 0 || position >= valores.size()) {
            position =
                    atual.getPosicaoTrevosSelecionada();
        }

        if (position < 0 || position >= valores.size()) {
            return atual;
        }

        ParametroValorApostaDTO selecionado =
                valores.get(position);

        if (selecionado == null) {
            return atual;
        }

        BigDecimal valorAposta =
                aplicarQuantidadeConcursos(
                        atual,
                        selecionado.getValor()
                );

        int quantidadeTrevos =
                obterQuantidadeTrevos(
                        selecionado
                );

        return atual.toBuilder()
                .setPosicaoTrevosSelecionada(position)
                .setValorTrevosSelecionado(selecionado)
                .setExibirDialogQuantidadeTrevos(false)
                .setEtapaAposta(EtapaAposta.TREVOS)
                .setValorAposta(valorAposta)
                .setValorGrande(isValorGrande(valorAposta))
                .setTrevosSelecionados(new ArrayList<Integer>())
                .setQuantidadeTrevosSelecionada(quantidadeTrevos)
                .setTextoSelecionados(
                        montarTextoTrevosSelecionados(
                                0,
                                quantidadeTrevos
                        )
                )
                .setTextoBotaoQuantidadeTrevos(
                        montarTextoBotaoTrevos(
                                quantidadeTrevos
                        )
                )
                .setComandoTelaSimula(
                        new ScreenSimulaEffect.AtualizarQuantidadeTrevos(
                                selecionado
                        )
                )
                .build();
    }

    public SimulaUiState consumirDialogQuantidadeTrevos(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setExibirDialogQuantidadeTrevos(false)
                .build();
    }

    public SimulaUiState consumirAvisoTrevosMaximo(
            SimulaUiState atual
    ) {

        if (atual == null) {
            return null;
        }

        return atual.toBuilder()
                .setExibirAvisoTrevosMaximo(false)
                .build();
    }

    public ParametroValorApostaDTO obterValorTrevosAtual(
            SimulaUiState atual,
            int quantidadeTrevos
    ) {

        if (atual == null) {
            return null;
        }

        List<ParametroValorApostaDTO> valores =
                atual.getValoresTrevos();

        if (valores == null) {
            return atual.getValorTrevosSelecionado();
        }

        for (ParametroValorApostaDTO valor : valores) {

            if (valor == null
                    || valor.getNumeroTrevos() == null) {
                continue;
            }

            if (valor.getNumeroTrevos() == quantidadeTrevos) {
                return valor;
            }
        }

        return atual.getValorTrevosSelecionado();
    }

    public List<String> montarLabelsTrevos(
            List<ParametroValorApostaDTO> valores
    ) {

        List<String> labels =
                new ArrayList<String>();

        if (valores == null) {
            return labels;
        }

        for (ParametroValorApostaDTO valor : valores) {

            if (valor == null) {
                continue;
            }

            Integer numeroTrevos =
                    valor.getNumeroTrevos();

            BigDecimal valorAposta =
                    valor.getValor() != null
                            ? valor.getValor()
                            : BigDecimal.ZERO;

            labels.add(
                    numeroTrevos
                            + " trevos por "
                            + ViewUtils.getMoedaFormat(valorAposta)
            );
        }

        return labels;
    }

    public String montarTextoTrevosSelecionados(
            int quantidadeSelecionada,
            int quantidadeNecessaria
    ) {

        if (quantidadeSelecionada <= 0) {
            return "Selecione os trevos:";
        }

        return "Trevos selecionados: "
                + quantidadeSelecionada
                + "/"
                + quantidadeNecessaria;
    }

    public String montarTextoBotaoTrevos(
            int quantidadeTrevos
    ) {

        return quantidadeTrevos + " trevos";
    }

    private BigDecimal aplicarQuantidadeConcursos(
            SimulaUiState atual,
            BigDecimal valorBase
    ) {

        BigDecimal valorSeguro =
                valorBase != null
                        ? valorBase
                        : BigDecimal.ZERO;

        if (atual == null) {
            return valorSeguro;
        }

        int qtdConcursos =
                atual.getQtdConcursoSelecionado();

        if (qtdConcursos > 0) {
            return valorSeguro.multiply(
                    BigDecimal.valueOf(qtdConcursos)
            );
        }

        return valorSeguro;
    }

    private boolean isValorGrande(
            BigDecimal valor
    ) {

        return valor != null
                && valor.doubleValue() > 999.99;
    }

    private int obterQuantidadeTrevos(
            ParametroValorApostaDTO valor
    ) {

        if (valor == null
                || valor.getNumeroTrevos() == null) {
            return 0;
        }

        return valor.getNumeroTrevos();
    }

    private List<Integer> criarListaTrevosSegura(
            List<Integer> origem
    ) {

        if (origem == null) {
            return new ArrayList<Integer>();
        }

        return new ArrayList<Integer>(origem);
    }
}