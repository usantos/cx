package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

/**
 * Created by joafilho on 14/12/2017.
 */
public enum LoteriasHabilitadaEnum {

    CEP(1),
    NOME(2),
    CODIGO(3),
    UF_MUNICIPIO_BAIRRO(4);

    private Integer tipo;

    LoteriasHabilitadaEnum(Integer tipo) {
        this.tipo = tipo;
    }

    public Integer getTipo() {
        return tipo;
    }

    public static LoteriasHabilitadaEnum getTipoFromCodigo(Integer codigo) {
        if (CEP.getTipo().equals(codigo)) {
            return CEP;
        } else if (NOME.getTipo().equals(codigo)) {
            return NOME;
        } else if (CODIGO.getTipo().equals(codigo)) {
            return CODIGO;
        } else if (UF_MUNICIPIO_BAIRRO.getTipo().equals(codigo)) {
            return UF_MUNICIPIO_BAIRRO;
        }
        return null;
    }
}
