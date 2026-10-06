package br.gov.caixa.loterias.apostas.model.bean;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametrosSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RedirectEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoApostador;

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
    private boolean passouPelaFilaBRA = false;

    private Boolean suspensaoTemporariaApostador;
    private Boolean responderAutoavaliacao;
    private SituacaoApostador situacaoApostador;
    private RedirectEnum redirectEnum;

    private Date dataUltimaRequisicao;
    public static int SECONDS = 180;
    public ParametrosSimulacao parametrosSimulacao;


    public ParametrosSimulacao getParametrosSimulacao() {
        if(parametrosSimulacao == null){
            parametrosSimulacao = new ParametrosSimulacao();
        }
        return parametrosSimulacao;
    }

    public void setParametrosSimulacao(ParametrosSimulacao parametrosSimulacao) {
        this.parametrosSimulacao = parametrosSimulacao;
    }


    public List<CombosDTO> listCombosDTO;


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

    public boolean isPassouPelaFilaBRA() { return passouPelaFilaBRA; }

    public void setPassouPelaFilaBRA(boolean passouPelaFilaBRA) {
        this.passouPelaFilaBRA = passouPelaFilaBRA;
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

    public SituacaoApostador getSituacaoApostador() {
        return situacaoApostador;
    }

    public void setSituacaoApostador(SituacaoApostador situacaoApostador) {
        this.situacaoApostador = situacaoApostador;
    }

    public List<CombosDTO> getListCombosDTO() {
        return listCombosDTO;
    }

    public void setListCombosDTO(List<CombosDTO> listCombosDTO) {
        this.listCombosDTO = listCombosDTO;
    }

    public RedirectEnum getRedirectEnum() {
        return redirectEnum;
    }

    public void setRedirectEnum(RedirectEnum redirectEnum) {
        this.redirectEnum = redirectEnum;
    }
}
