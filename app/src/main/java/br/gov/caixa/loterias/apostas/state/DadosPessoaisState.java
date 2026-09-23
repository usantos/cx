package br.gov.caixa.loterias.apostas.state;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.effect.DadosPessoaisEffect;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioIdDTO;

/**
 * Estado da tela de dados pessoais.
 *
 * Alem dos dados da tela, carrega os slots de comando (rede, dialog,
 * navegacao e analytics), que substituem o SingleLiveEvent. Cada slot e
 * limpo pela ViewModel quando a Activity envia o evento ...Consumido.
 */
public class DadosPessoaisState {

    private ApostadorDTO apostador;
    private MunicipioIdDTO municipioSelecionado;
    private String uf;
    private String municipio;
    private String cep;
    private String erroCep;
    private BigDecimal limiteDigitado;
    private int posicaoSexo;
    private boolean aceitaNoticias;
    private boolean carregando;
    private boolean dadosCarregados;
    private boolean limiteMenorQueMinimo;
    private boolean limiteMaiorQueMaximo;

    private DadosPessoaisEffect comandoRede;
    private DadosPessoaisEffect comandoDialog;
    private DadosPessoaisEffect comandoNavegacao;
    private DadosPessoaisEffect comandoAnalytics;

    /**
     * Derivado: o CEP so e valido quando a consulta nao retornou erro
     * e resolveu um municipio.
     */
    public boolean isCepValido() {
        return erroCep == null && municipioSelecionado != null;
    }

    public boolean isLimiteValido() {
        return !limiteMenorQueMinimo && !limiteMaiorQueMaximo;
    }

    public ApostadorDTO getApostador() {
        return apostador;
    }

    public void setApostador(ApostadorDTO apostador) {
        this.apostador = apostador;
    }

    public MunicipioIdDTO getMunicipioSelecionado() {
        return municipioSelecionado;
    }

    public void setMunicipioSelecionado(MunicipioIdDTO municipioSelecionado) {
        this.municipioSelecionado = municipioSelecionado;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getErroCep() {
        return erroCep;
    }

    public void setErroCep(String erroCep) {
        this.erroCep = erroCep;
    }

    public BigDecimal getLimiteDigitado() {
        return limiteDigitado;
    }

    public void setLimiteDigitado(BigDecimal limiteDigitado) {
        this.limiteDigitado = limiteDigitado;
    }

    public int getPosicaoSexo() {
        return posicaoSexo;
    }

    public void setPosicaoSexo(int posicaoSexo) {
        this.posicaoSexo = posicaoSexo;
    }

    public boolean isAceitaNoticias() {
        return aceitaNoticias;
    }

    public void setAceitaNoticias(boolean aceitaNoticias) {
        this.aceitaNoticias = aceitaNoticias;
    }

    public boolean isCarregando() {
        return carregando;
    }

    public void setCarregando(boolean carregando) {
        this.carregando = carregando;
    }

    public boolean isDadosCarregados() {
        return dadosCarregados;
    }

    public void setDadosCarregados(boolean dadosCarregados) {
        this.dadosCarregados = dadosCarregados;
    }

    public boolean isLimiteMenorQueMinimo() {
        return limiteMenorQueMinimo;
    }

    public void setLimiteMenorQueMinimo(boolean limiteMenorQueMinimo) {
        this.limiteMenorQueMinimo = limiteMenorQueMinimo;
    }

    public boolean isLimiteMaiorQueMaximo() {
        return limiteMaiorQueMaximo;
    }

    public void setLimiteMaiorQueMaximo(boolean limiteMaiorQueMaximo) {
        this.limiteMaiorQueMaximo = limiteMaiorQueMaximo;
    }

    public DadosPessoaisEffect getComandoRede() {
        return comandoRede;
    }

    public void setComandoRede(DadosPessoaisEffect comandoRede) {
        this.comandoRede = comandoRede;
    }

    public DadosPessoaisEffect getComandoDialog() {
        return comandoDialog;
    }

    public void setComandoDialog(DadosPessoaisEffect comandoDialog) {
        this.comandoDialog = comandoDialog;
    }

    public DadosPessoaisEffect getComandoNavegacao() {
        return comandoNavegacao;
    }

    public void setComandoNavegacao(DadosPessoaisEffect comandoNavegacao) {
        this.comandoNavegacao = comandoNavegacao;
    }

    public DadosPessoaisEffect getComandoAnalytics() {
        return comandoAnalytics;
    }

    public void setComandoAnalytics(DadosPessoaisEffect comandoAnalytics) {
        this.comandoAnalytics = comandoAnalytics;
    }
}
