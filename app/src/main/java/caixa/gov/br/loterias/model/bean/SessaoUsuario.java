package caixa.gov.br.loterias.model.bean;

import java.math.BigDecimal;

import caixa.gov.br.loterias.model.bo.silce.dto.ParametrosSimulacao;

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


    public ParametrosSimulacao getParametrosSimulacao() {
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


}
