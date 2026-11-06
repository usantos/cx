package br.gov.caixa.loterias.apostas.model.bean;

import br.gov.caixa.loterias.apostas.model.enums.TipoConsultaBolaoEnum;

public class PaginacaoFiltro {
    public static final int QTD_POR_PAGINA = 30;
    private static final int PAG_INICIAL = 1;

    private TipoConsultaBolaoEnum tipoConsulta;
    private int idMunicipio;
    private int idUf;
    private int qtdPorPagina;
    private int pagina;
    private int qtdTotalPaginas;
    private boolean semRegistros = false;
    private Integer idModalidade;
    private Integer idTipoConcurso;

    public PaginacaoFiltro(TipoConsultaBolaoEnum tipoConsulta, int idMunicipio, int idUf, Integer idModalidade, Integer idTipoConcurso){
        this.tipoConsulta = tipoConsulta;
        this.idMunicipio = idMunicipio;
        this.idUf = idUf;
        this.pagina = PAG_INICIAL;
        this.qtdPorPagina = QTD_POR_PAGINA;
        this.qtdTotalPaginas = -1;
        this.idModalidade = idModalidade;
        this.idTipoConcurso = idTipoConcurso;
    }

    public TipoConsultaBolaoEnum getTipoConsulta() {
        return tipoConsulta;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }

    public int getIdUf() {
        return idUf;
    }

    public int getQtdPorPagina() {
        return qtdPorPagina;
    }

    public int getPagina() {
        return pagina;
    }

    public int getQtdTotalPaginas() {
        return qtdTotalPaginas;
    }

    public void setQtdTotalPaginas(int qtdTotalPaginas) {
        this.qtdTotalPaginas = qtdTotalPaginas;
    }

    public Integer getIdModalidade() {
        return idModalidade;
    }

    public void setIdModalidade(Integer idModalidade) {
        this.idModalidade = idModalidade;
    }

    public void proxPagina() {
        if (pagina < qtdTotalPaginas){
            pagina++;
        }
    }

    public void proxArea() {
        if (tipoConsulta != TipoConsultaBolaoEnum.LOTERICO){
            switch (tipoConsulta){
                case MUNICIPIO:
                    tipoConsulta = TipoConsultaBolaoEnum.UF;
                    pagina = 1;
                    qtdTotalPaginas = -1;
                    break;
                case UF:
                    tipoConsulta = TipoConsultaBolaoEnum.NACIONAL;
                    pagina = 1;
                    qtdTotalPaginas = -1;
                    break;
                case NACIONAL:
                    semRegistros = true;
                    break;
            }
        }
    }

    public boolean acabouRegistros(){
        return semRegistros;
    }

    public void setAcabouRegistros(boolean acabou) {
        this.semRegistros = acabou;
    }

    public Integer getIdTipoConcurso() {
        return idTipoConcurso;
    }

    public void setIdTipoConcurso(Integer idTipoConcurso) {
        this.idTipoConcurso = idTipoConcurso;
    }
}
