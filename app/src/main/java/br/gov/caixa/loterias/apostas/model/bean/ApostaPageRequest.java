package br.gov.caixa.loterias.apostas.model.bean;

/**
 * Created by pmotta on 19/03/2018.
 */

public class ApostaPageRequest {
    private int mes, ano, offset, size, situacao, modalidade, ordenarPor, tipoAposta;

    public ApostaPageRequest(int size){
        this.size = size;
    }

    public int getOrdenarPor() {
        return ordenarPor;
    }

    public void setOrdenarPor(int ordenarPor) {
        this.ordenarPor = ordenarPor;
    }

    public int getTipoAposta() {
        return tipoAposta;
    }

    public void setTipoAposta(int tipoAposta) {
        this.tipoAposta = tipoAposta;
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
