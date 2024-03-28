package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

/**
 * Created by brfernan on 27/10/2017.
 */

public class EstiloModalidadeMKP {

    private final int corClara;
    private final int corEscura;
    private final int trapezio;
    private final int shapeBtnDetalhes;
    private final int imagemEspecialCarrossel;
    private final int imagemTriangulo;
    private final int imagemTrianguloOutline;

    public EstiloModalidadeMKP(ModalidadeEnum modalidade) {
        switch (modalidade) {
            case QUINA:
                corClara = R.color.quina_claro_mkp;
                corEscura = R.color.quina_claro_mkp;
                trapezio = R.drawable.background_trapeze_quina_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_quina_outlined;
                imagemEspecialCarrossel = R.drawable.cabecalho_quina_sao_joao_caurossel;
                imagemTriangulo = R.drawable.ic_tag_bolao_quina;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_quina;
                break;
            case DUPLA_SENA:
                corClara = R.color.dupla_sena_claro_mkp;
                corEscura = R.color.dupla_sena_escuro_mkp;
                trapezio = R.drawable.background_trapeze_dupla_sena_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_dupla_sena_outlined;
                imagemEspecialCarrossel = -1;
                imagemTriangulo = R.drawable.ic_tag_bolao_dupla_sena;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_dupla_sena;
                break;
            case LOTOFACIL:
                corClara = R.color.lotofacil_claro_mkp;
                corEscura = R.color.lotofacil_escuro_mkp;
                trapezio = R.drawable.background_trapeze_lotofacil_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_lotofacil_outlined;
                imagemEspecialCarrossel = -1;
                imagemTriangulo = R.drawable.ic_tag_bolao_loto_facil;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_loto_facil;
                break;
            default: {
                //MEGASENA
                corClara = R.color.mega_verde_claro_mkp;
                corEscura = R.color.mega_verde_escuro_mkp;
                trapezio = R.drawable.background_trapeze_megasena_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_green_outlined;
                imagemEspecialCarrossel = -1;
                imagemTriangulo = R.drawable.ic_tag_bolao;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline;
            }
        }

    }

    public int getCorClara() {
        return corClara;
    }

    public int getCorEscura() {
        return corEscura;
    }

    public int getTrapezio() {
        return trapezio;
    }

    public int getShapeBtnDetalhes() {
        return shapeBtnDetalhes;
    }

    public int getImagemEspecialCarrossel() {
        return imagemEspecialCarrossel;
    }

    public int getImagemTriangulo() {
        return imagemTriangulo;
    }

    public int getImagemTrianguloOutline() {
        return imagemTrianguloOutline;
    }
}
