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
    private final int imagemEspecialDetalhes;

    public EstiloModalidadeMKP(ModalidadeEnum modalidade) {
        switch (modalidade) {
            case QUINA:
                corClara = R.color.quina_claro_mkp;
                corEscura = R.color.quina_claro_mkp;
                trapezio = R.drawable.background_trapeze_quina_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_quina_outlined;
                imagemEspecialCarrossel = R.drawable.cabecalho_quina_sao_joao_caurossel;
                imagemEspecialDetalhes = R.drawable.cabecalho_quina_sao_joao_card_detalhes;
                break;
            case DUPLA_SENA:
                corClara = R.color.dupla_sena_claro_mkp;
                corEscura = R.color.dupla_sena_escuro_mkp;
                trapezio = R.drawable.background_trapeze_dupla_sena_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_dupla_sena_outlined;
                imagemEspecialCarrossel = R.drawable.cabecalho_dupla_pascoa_carrossel;
                imagemEspecialDetalhes = R.drawable.cabecalho_dupla_pascoa_detalhes;
                break;
            case LOTOFACIL:
                corClara = R.color.lotofacil_claro_mkp;
                corEscura = R.color.lotofacil_escuro_mkp;
                trapezio = R.drawable.background_trapeze_lotofacil_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_lotofacil_outlined;
                imagemEspecialCarrossel = R.drawable.cabecalho_lotofacil_independencia_carrossel;
                imagemEspecialDetalhes = R.drawable.cabecalho_lotofacil_independencia_detalhes;
                break;
            default:
                //MEGASENA
                corClara = R.color.mega_verde_claro_mkp;
                corEscura = R.color.mega_verde_escuro_mkp;
                trapezio = R.drawable.background_trapeze_megasena_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_green_outlined;
                imagemEspecialCarrossel = R.drawable.cabecalho_mega_virada_carrossel;
                imagemEspecialDetalhes = R.drawable.cabecalho_mega_virada_detalhes;

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

    public int getImagemEspecialDetalhes() {
        return imagemEspecialDetalhes;
    }
}
