package br.gov.caixa.loterias.apostas.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.*;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DetalheComboViewModel extends ViewModel {

    private final MutableLiveData<List<ModalidadeComboDTO>> modalidadesComboLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> qtdTotalApostasLiveData = new MutableLiveData<>(0);
    private final MutableLiveData<BigDecimal> valorComboLiveData = new MutableLiveData<>(BigDecimal.ZERO);
    private CombosDTO combo;
    private int qtdInicialApostas = 0;
    private int index = 0;
    private SessaoUsuario sessaoUsuario;

    public void carregarCombo(CombosDTO combo) {
        this.combo = combo;
        sessaoUsuario = SessaoUsuario.getInstance();
        if (combo != null && combo.getModalidadesCombo() != null) {
            List<ModalidadeComboDTO> modalidadesCombo = new ArrayList<>(combo.getModalidadesCombo());
            modalidadesComboLiveData.setValue(modalidadesCombo);
            qtdInicialApostas = calcularQtdApostas(modalidadesCombo);
            qtdTotalApostasLiveData.setValue(qtdInicialApostas);
            valorComboLiveData.setValue(combo.getValor() != null ? combo.getValor() : BigDecimal.ZERO);
        }
    }

    public CombosDTO getCombo() {
        return combo;
    }

    public LiveData<List<ModalidadeComboDTO>> getModalidadesComboLiveData() {
        return modalidadesComboLiveData;
    }

    public LiveData<Integer> getQtdTotalApostasLiveData() {
        return qtdTotalApostasLiveData;
    }

    public LiveData<BigDecimal> getValorComboLiveData() {
        return valorComboLiveData;
    }

    public void incrementarApostas() {

        if(modalidadesComboLiveData.getValue() == null || qtdTotalApostasLiveData.getValue() == null) {
            return;
        }

        if (index >= 0 && index < modalidadesComboLiveData.getValue().size()) {
            if(modalidadesComboLiveData.getValue().get(index).getModalidade().getValor() == 16){
                qtdTotalApostasLiveData.setValue(qtdTotalApostasLiveData.getValue() + 1);
                atualizarQtdJogosItem( index, true);
            }

            qtdTotalApostasLiveData.setValue(qtdTotalApostasLiveData.getValue() + 1);
            atualizarQtdJogosItem( index, true);
            index = (index + 1) % modalidadesComboLiveData.getValue().size();

            atualizarValorCombo();
        }
    }


    public void decrementarApostas() {
        if (modalidadesComboLiveData.getValue() == null || qtdTotalApostasLiveData.getValue() == null) {
            return;
        }

        if(qtdTotalApostasLiveData.getValue() > qtdInicialApostas){
            qtdTotalApostasLiveData.setValue(qtdTotalApostasLiveData.getValue() - 1);
            index = index == 0 ? modalidadesComboLiveData.getValue().size() - 1 : index - 1;
            atualizarQtdJogosItem( index, false);
            if(modalidadesComboLiveData.getValue().get(index).getModalidade().getValor() == 16){
                qtdTotalApostasLiveData.setValue(qtdTotalApostasLiveData.getValue() - 1);
                atualizarQtdJogosItem( index, false);
            }
        }else{
            qtdTotalApostasLiveData.setValue(qtdInicialApostas);
        }
        atualizarValorCombo();
    }

    private static @NotNull ModalidadeComboDTO getModalidadeComboDTO(ModalidadeComboDTO itemOriginal, int novaQuantidade) {
        ModalidadeComboDTO itemQtdApostasAlteradas = new ModalidadeComboDTO();
        itemQtdApostasAlteradas.setModalidade(itemOriginal.getModalidade());
        itemQtdApostasAlteradas.setTipoConcurso(itemOriginal.getTipoConcurso());
        itemQtdApostasAlteradas.setQuantidadeApostas(novaQuantidade);
        itemQtdApostasAlteradas.setQuantidadeTeimosinhas(itemOriginal.getQuantidadeTeimosinhas());
        itemQtdApostasAlteradas.setNumero(itemOriginal.getNumero());
        itemQtdApostasAlteradas.setQuantidadeDezenas(itemOriginal.getQuantidadeDezenas());
        itemQtdApostasAlteradas.setQuantidadeDezenasTrevos(itemOriginal.getQuantidadeDezenasTrevos());
        return itemQtdApostasAlteradas;
    }

    private int calcularQtdApostas(List<ModalidadeComboDTO> modalidadesCombo) {
        int quantidadeApostas = 0;
        for (ModalidadeComboDTO modalidade : modalidadesCombo) {
            quantidadeApostas += modalidade.getQuantidadeApostas();
        }
        return quantidadeApostas;
    }

    private void atualizarValorCombo() {
        valorComboLiveData.setValue(BigDecimal.ZERO);
        BigDecimal valorCombo = BigDecimal.ZERO;

        if(modalidadesComboLiveData.getValue() == null) {
            return;
        }

        for (ModalidadeComboDTO modalidadeCombo : modalidadesComboLiveData.getValue()) {
            ModalidadeEnum modalidade = ModalidadeEnum.fromInteger(modalidadeCombo.getModalidade().getValor());
            ParametroSimulacao parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidade);

            if (parametroSimulacao != null) {
                List<ParametroValorApostaDTO> valores = parametroSimulacao.getParametroJogo().getValoresAposta();
                BigDecimal valorAposta = valores.get(0).getValor(); // Valor padrão
                int qtdApostas = modalidadeCombo.getQuantidadeApostas();
                int qtdTeimosinhas = Math.max(modalidadeCombo.getQuantidadeTeimosinhas(), 1);

                for (ParametroValorApostaDTO valor : valores) {
                    if (modalidadeCombo.getModalidade().getValor() != 9) {
                        if (Objects.equals(modalidadeCombo.getQuantidadeDezenas(), valor.getNumeroPrognosticos())) {
                            valorAposta = valor.getValor();
                            break;
                        }
                    } else {
                        // +milionária
                        if (Objects.equals(modalidadeCombo.getQuantidadeDezenas(), valor.getNumeroPrognosticos()) &&
                                Objects.equals(modalidadeCombo.getQuantidadeDezenasTrevos(), valor.getNumeroTrevos())) {
                            valorAposta = valor.getValor();
                            break;
                        }
                    }
                }

                BigDecimal soma = valorAposta
                        .multiply(BigDecimal.valueOf(qtdApostas))
                        .multiply(BigDecimal.valueOf(qtdTeimosinhas));

                valorCombo = valorCombo.add(soma);
            }
        }
        valorComboLiveData.setValue(valorCombo);
    }

    public void atualizarQtdJogosItem(int indexItem, boolean incrementar){

        if(modalidadesComboLiveData.getValue() == null){
            return;
        }

        ModalidadeComboDTO itemOriginal = modalidadesComboLiveData.getValue().get(indexItem);
        int novaQuantidade = incrementar ? itemOriginal.getQuantidadeApostas() + 1 : itemOriginal.getQuantidadeApostas() - 1;

        ModalidadeComboDTO itemAtualizado = getModalidadeComboDTO(itemOriginal, novaQuantidade);

        List<ModalidadeComboDTO> novaLista = new ArrayList<>(modalidadesComboLiveData.getValue());
        novaLista.set(indexItem, itemAtualizado);

        modalidadesComboLiveData.setValue(novaLista);

    }
}