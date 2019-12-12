package br.gov.caixa.loterias.apostar.interno.model.bean;

/**
 * Created by pmotta on 19/03/2018.
 */

public class ApostaPageRequest {
    private int mes, ano, offset, size, situacao,modalidade;

    public ApostaPageRequest(int size){
        this.size = size;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSituacao() { return situacao; }

    public void setSituacao(int situacao) {
        this.situacao = situacao;
    }

    public int getModalidade() {
        return modalidade;
    }

    public void setModalidade(int modalidade) {
        this.modalidade = modalidade;
    }
}
