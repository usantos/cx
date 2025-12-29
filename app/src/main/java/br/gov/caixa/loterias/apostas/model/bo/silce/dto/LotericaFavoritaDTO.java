
package br.gov.caixa.loterias.apostas.model.bo.silce.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Objects;

@ApiModel(description = "")
public class LotericaFavoritaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Polo de Atuação da Unidade Lotérica ")
    private Integer polo;

    @ApiModelProperty(value = "Código da Unidade Lotérica")
    private Long codigo;

    @ApiModelProperty(value = "Digito Verificador do Polo de Atuação da Unidade Lotérica ")
    private Integer dv;

    @ApiModelProperty(value = "Nome Fantasia da Unidade Lotérica")
    private String nomeFantasia;

    @ApiModelProperty(value = "Município em que a Unidade Lotérica está localizada")
    private String nomeMunicipio;

    @ApiModelProperty(value = "Sigla da UF da Unidade Lotérica")
    private String siglaUf;

    // === equals, hashCode e toString ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                 // mesma referência
        if (o == null || getClass() != o.getClass()) return false;  // null ou classe diferente
        LotericaFavoritaDTO that = (LotericaFavoritaDTO) o;
        return Objects.equals(polo, that.polo)
                && Objects.equals(codigo, that.codigo)
                && Objects.equals(dv, that.dv)
                && Objects.equals(nomeFantasia, that.nomeFantasia)
                && Objects.equals(nomeMunicipio, that.nomeMunicipio)
                && Objects.equals(siglaUf, that.siglaUf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(polo, codigo, dv, nomeFantasia, nomeMunicipio, siglaUf);
    }

    @Override
    public String toString() {
        return "LotericaFavoritaDTO{" +
                "polo=" + polo +
                ", codigo=" + codigo +
                ", dv=" + dv +
                ", nomeFantasia='" + nomeFantasia + '\'' +
                ", nomeMunicipio='" + nomeMunicipio + '\'' +
                ", siglaUf='" + siglaUf + '\'' +
                '}';
    }

    // (Opcional) Getters/Setters, se você precisar
    public Integer getPolo() { return polo; }
    public void setPolo(Integer polo) { this.polo = polo; }
    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public Integer getDv() { return dv; }
    public void setDv(Integer dv) { this.dv = dv; }
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
    public String getNomeMunicipio() { return nomeMunicipio; }
    public void setNomeMunicipio(String nomeMunicipio) { this.nomeMunicipio = nomeMunicipio; }
    public String getSiglaUf() { return siglaUf; }
    public void setSiglaUf(String siglaUf) { this.siglaUf = siglaUf; }
}

