package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.swagger.annotations.ApiModel;


@ApiModel(description = "")
public class ParametrosConfiguraveisDTO {

    @SerializedName("mostrar6meses+")
    private Boolean mostrar6meses = null;
    @SerializedName("hostMicroServicoConferenciaAposta")
    private String urlBase;
    @SerializedName("habilitarConferenciaAutomaticaApostas")
    private Boolean isMicroServico;
    @SerializedName("consultaMinhasApostas")
    private Boolean habilitaMenuApostas;
    @SerializedName("recuperarMeioPagamento")
    private Boolean atualizaPublicKey;
    @SerializedName("habilitaRevendaBolaoApp")
    private Boolean bolaoHabilitado;
    @SerializedName("hostMicroServicoCarrinhoPiloto")
    private String urlBaseCarrinhoPiloto;
    @SerializedName("hostMicroServicoCarrinhoProducao")
    private String urlBaseCarrinhoProducao;
    @SerializedName("hostMicroServicoBff")
    private String urlBaseBff;
    //@SerializedName("hostMicroServicoBffPiloto")
    //private String urlBaseBffPiloto;
        @SerializedName("grupoDeCpfsQueAcessamOBffMobile")
    private String grupoDeCpfsQueAcessamOBff;
    //@SerializedName("grupoDeCpfsQueAcessamOBffPiloto")
    //private String grupoDeCpfsQueAcessamOBffPiloto;
    @SerializedName("hostNovoMicroServicoConferenciaApostaProducao")
    private String urlBaseNovaApiPRD;
    @SerializedName("hostNovoMicroServicoConferenciaApostaPiloto")
    private String urlBaseNovaApiPLT;
    @SerializedName("grupoCpfsAcessamNovoConferenciaAposta")
    private Integer grupoCpfNovaApi = 0;

    @SerializedName("rapidaoHabilitado")
    private Boolean isRapidaoHabilitado = false;
    @SerializedName("hostMicroServicoApostadorPiloto")
    private String urlBaseApostadorPiloto;
    @SerializedName("hostMicroServicoApostadorProducao")
    private String urlBaseApostador;
    @SerializedName("hostMicroServicoApostadorHabilitadoPiloto")
    private String grupoDeCpfsApostadorPiloto;
    @SerializedName("hostMicroServicoApostadorHabilitado")
    private String grupoDeCpfsApostador;

    @SerializedName("comboApostasHabilitado")
    private Boolean comboApostasHabilitado;
    @SerializedName("concursoMega30Anos")
    private Integer concursoMega30Anos;
    @SerializedName("concursoLotecaPais")
    private Integer concursoLotecaPais;
    @SerializedName("vigenciaMesOutubroRosa")
    private Boolean vigenciaMesOutubroRosa;
    @SerializedName("vigenciaOutubroRosaMegaSena")
    private Boolean vigenciaOutubroRosaMegaSena;
    @SerializedName("qtdeMinimaApostaBolaoFiltro")
    private Integer qtdeMinimaApostaBolaoFiltro;
    @SerializedName("qtdeMaximaApostaBolaoFiltro")
    private Integer qtdeMaximaApostaBolaoFiltro;

    public String getUrlBaseApostadorPiloto() {
        return urlBaseApostadorPiloto;
    }
    public void setUrlBaseApostadorPiloto(String urlBaseApostadorPiloto) {
        this.urlBaseApostadorPiloto = urlBaseApostadorPiloto;
    }

    public String getUrlBaseApostador() {
        return urlBaseApostador;
    }
    public void setUrlBaseApostador(String urlBaseApostador) {
        this.urlBaseApostador = urlBaseApostador;
    }
    public String getGrupoDeCpfsApostadorPiloto() {
        return grupoDeCpfsApostadorPiloto;
    }
    public void setGrupoDeCpfsApostadorPiloto(String grupoDeCpfsApostadorPiloto) {
        this.grupoDeCpfsApostadorPiloto = grupoDeCpfsApostadorPiloto;
    }
    public String getGrupoDeCpfsApostador() {
        return grupoDeCpfsApostador;
    }
    public void setGrupoDeCpfsApostador(String grupoDeCpfsApostador) {
        this.grupoDeCpfsApostador = grupoDeCpfsApostador;
    }

    public Boolean getMicroServico() {
        return isMicroServico;
    }

    public void setMicroServico(Boolean microServico) {
        isMicroServico = microServico;
    }

    public Boolean getMostrar6meses() {
        return mostrar6meses;
    }

    public void setMostrar6meses(Boolean mostrar6meses) {
        this.mostrar6meses = mostrar6meses;
    }

    public String getUrlBase(){
        return urlBase;
    }

    public void setUrlBase(String urlBase){
        this.urlBase = urlBase;
    }

    public Boolean getHabilitaMenuApostas() {
        return habilitaMenuApostas;
    }

    public void setHabilitaMenuApostas(Boolean habilitaMenuApostas) {
        this.habilitaMenuApostas = habilitaMenuApostas;
    }

    public Boolean isAtualizaPublicKey() {
        return atualizaPublicKey;
    }

    public void setAtualizaPublicKey(Boolean atualizaPublicKey) {
        this.atualizaPublicKey = atualizaPublicKey;
    }

    public String getUrlBaseCarrinhoPiloto() {
        return urlBaseCarrinhoPiloto;
    }

    public void setUrlBaseCarrinhoPiloto(String urlBaseCarrinhoPiloto) {
        this.urlBaseCarrinhoPiloto = urlBaseCarrinhoPiloto;
    }

    public String getUrlBaseCarrinhoProducao() {
        return urlBaseCarrinhoProducao;
    }

    public void setUrlBaseCarrinhoProducao(String urlBaseCarrinhoProducao) {
        this.urlBaseCarrinhoProducao = urlBaseCarrinhoProducao;
    }

    public Boolean getComboApostasHabilitado() {
        return comboApostasHabilitado;
    }

    public void setComboApostasHabilitado(Boolean comboApostasHabilitado) {
        this.comboApostasHabilitado = comboApostasHabilitado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ParametrosConfiguraveisDTO that = (ParametrosConfiguraveisDTO) o;
        return Objects.equals(mostrar6meses, that.mostrar6meses) &&
                Objects.equals(urlBase, that.urlBase) &&
                Objects.equals(isMicroServico, that.isMicroServico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mostrar6meses, urlBase, isMicroServico);
    }

    public Boolean getBolaoHabilitado() {
        return bolaoHabilitado;
    }

    public void setBolaoHabilitado(Boolean bolaoHabilitado) {
        this.bolaoHabilitado = bolaoHabilitado;
    }

    public String getUrlBaseNovaApiPRD() {
        return urlBaseNovaApiPRD;
    }

    public void setUrlBaseNovaApiPRD(String urlBaseNovaApiPRD) {
        this.urlBaseNovaApiPRD = urlBaseNovaApiPRD;
    }

    public String getUrlBaseNovaApiPLT() {
        return urlBaseNovaApiPLT;
    }

    public void setUrlBaseNovaApiPLT(String urlBaseNovaApiPLT) {
        this.urlBaseNovaApiPLT = urlBaseNovaApiPLT;
    }

    public Integer getGrupoCpfNovaApi() {
        return grupoCpfNovaApi;
    }

    public void setGrupoCpfNovaApi(Integer grupoCpfNovaApi) {
        this.grupoCpfNovaApi = grupoCpfNovaApi;
    }

    public Boolean isRapidaoHabilitado() {
        return isRapidaoHabilitado;
    }

    public void setRapidaoHabilitado(Boolean rapidaoHabilitado) {
        isRapidaoHabilitado = rapidaoHabilitado;
    }

    public Integer getConcursoMega30Anos() {
        return concursoMega30Anos;
    }

    public void setConcursoMega30Anos(Integer concursoMega30Anos) {
        this.concursoMega30Anos = concursoMega30Anos;
    }

    //TODO: LOTECA PAIS//
    public Integer getConcursoLotecaPais() {
        return concursoLotecaPais;
    }

    public void setConcursoLotecaPais(Integer concursoLotecaPais) {
        this.concursoLotecaPais = concursoLotecaPais;
    }

    public Boolean getVigenciaMesOutubroRosa() {
        return vigenciaMesOutubroRosa;
    }

    public Boolean getVigenciaOutubroRosaMegaSena() {
        return vigenciaOutubroRosaMegaSena;
    }

    public String getUrlBaseBff() {
        return urlBaseBff;
    }

    public void setUrlBaseBff(String urlBaseBff) {
        this.urlBaseBff = urlBaseBff;
    }

    public String getGrupoDeCpfsQueAcessamOBff() {
        return grupoDeCpfsQueAcessamOBff;
    }

    public void setGrupoDeCpfsQueAcessamOBff(String grupoDeCpfsQueAcessamOBff) {
        this.grupoDeCpfsQueAcessamOBff = grupoDeCpfsQueAcessamOBff;
    }
    public Integer getQtdeMinimaApostaBolaoFiltro() { return qtdeMinimaApostaBolaoFiltro; }
    public void setQtdeMinimaApostaBolaoFiltro(Integer valor) { this.qtdeMinimaApostaBolaoFiltro = valor; }
    public Integer getQtdeMaximaApostaBolaoFiltro() { return qtdeMaximaApostaBolaoFiltro; }
    public void setQtdeMaximaApostaBolaoFiltro(Integer valor) { this.qtdeMaximaApostaBolaoFiltro = valor; }


//    public String getUrlBaseBffPiloto() {
//        return urlBaseBffPiloto;
//    }
//
//    public void setUrlBaseBffPiloto(String urlBaseBffPiloto) {
//        this.urlBaseBffPiloto = urlBaseBffPiloto;
//    }
//
//    public String getGrupoDeCpfsQueAcessamOBffPiloto() {
//        return grupoDeCpfsQueAcessamOBffPiloto;
//    }
//
//    public void setGrupoDeCpfsQueAcessamOBffPiloto(String grupoDeCpfsQueAcessamOBffPiloto) {
//        this.grupoDeCpfsQueAcessamOBffPiloto = grupoDeCpfsQueAcessamOBffPiloto;
//    }
}
