package br.gov.caixa.loterias.apostas.model.bean;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;

/**
 * Created by cedesbr450 on 08/01/18.
 */

public class SessaoUsuario {
    private static  SessaoUsuario ourInstance = new SessaoUsuario();

    private BigDecimal valorMinimimoAposta;
    private BigDecimal valorMaximoAposta;
    private String token;
    private String nome;
    private String email;
    private boolean precisaMontarCarrossel = true;
    private boolean passouPelaQueueIT = false;

    private Boolean suspensaoTemporariaApostador;
    private Boolean responderAutoavaliacao;

    private Date dataUltimaRequisicao;
    public static int SECONDS = 180;


    public ParametrosSimulacao getParametrosSimulacao() {
        if(parametrosSimulacao == null){
            parametrosSimulacao = new ParametrosSimulacao();
        }
        return parametrosSimulacao;
    }

    public void setParametrosSimulacao(ParametrosSimulacao parametrosSimulacao) {
        this.parametrosSimulacao = parametrosSimulacao;
    }

    public ParametrosSimulacao parametrosSimulacao;


    public static SessaoUsuario getInstance() {
        if (ourInstance == null)
        {
            ourInstance = new SessaoUsuario();
        }
        return ourInstance;
    }

    private SessaoUsuario() {}


    public BigDecimal getValorMinimimoAposta() {
        return valorMinimimoAposta;
    }

    public void setValorMinimimoAposta(BigDecimal valorMinimimoAposta) {
        this.valorMinimimoAposta = valorMinimimoAposta;
    }

    public BigDecimal getValorMaximoAposta() {
        return valorMaximoAposta;
    }

    public void setValorMaximoAposta(BigDecimal valorMaximoAposta) {
        this.valorMaximoAposta = valorMaximoAposta;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDataUltimaRequisicao() {
        return dataUltimaRequisicao;
    }

    public void setDataUltimaRequisicao(Date dataUltimaRequisicao) {
        this.dataUltimaRequisicao = dataUltimaRequisicao;
    }

    public boolean isPrecisaMontarCarrossel() {
        return precisaMontarCarrossel;
    }

    public void setPrecisaMontarCarrossel(boolean precisaMontarCarrossel) {
        this.precisaMontarCarrossel = precisaMontarCarrossel;
    }

    public boolean isPassouPelaQueueIT() { return passouPelaQueueIT; }

    public void setPassouPelaQueueIT(boolean passouPelaQueueIT) {
        this.passouPelaQueueIT = passouPelaQueueIT;
    }

    public Boolean getSuspensaoTemporariaApostador() {
        if (suspensaoTemporariaApostador == null) {
            return false;
        }
        return suspensaoTemporariaApostador;
    }

    public void setSuspensaoTemporariaApostador(Boolean suspensaoTemporariaApostador) {
        this.suspensaoTemporariaApostador = suspensaoTemporariaApostador;
    }

    public Boolean getResponderAutoavaliacao() {
        if (responderAutoavaliacao == null) {
            return false;
        }
        return responderAutoavaliacao;
    }

    public void setResponderAutoavaliacao(Boolean responderAutoavaliacao) {
        this.responderAutoavaliacao = responderAutoavaliacao;
    }

}
