package br.gov.caixa.loterias.apostas.utils;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

public class EstiloModalidadeMKP {

    private final int corClara;
    private final int corEscura;
    private final int trapezio;
    private final int shapeBtnDetalhes;
    private final int imagemEspecialCarrossel;
    private final int imagemTriangulo;
    private final int imagemTrianguloOutline;
    private final int imagemEspecialDetalhes;
    private final int corLetraLista;
    private final int corFonteFundoClaro;
    private final int corFonteFundoEscuro;
    private final int trevoFundoClaro;
    private final int trevoFundoEscuro;
    private final int tarjaEspecialTrevoCombo;

    public EstiloModalidadeMKP(ModalidadeEnum modalidade) {
        switch (modalidade) {
            case QUINA:
                corClara = R.color.quina_claro_mkp;
                corEscura = R.color.quina_escuro_mkp;
                trapezio = R.drawable.background_trapeze_quina_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_quina_outlined;
                imagemEspecialCarrossel = R.drawable.cabecalho_quina_sao_joao_caurossel;
                imagemTriangulo = R.drawable.ic_tag_bolao_quina;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_quina;
                imagemEspecialDetalhes = R.drawable.cabecalho_quina_sao_joao_card_detalhes;
                corLetraLista = R.color.quina_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_quina_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_quina_fundo_escuro;
                tarjaEspecialTrevoCombo = R.drawable.tarja_trevo_quina_especial_combos;
                break;
            case DUPLA_SENA:
                corClara = R.color.dupla_sena_claro_mkp;
                corEscura = R.color.dupla_sena_escuro_mkp;
                trapezio = R.drawable.background_trapeze_dupla_sena_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_dupla_sena_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_dupla_sena;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_dupla_sena;
                imagemEspecialCarrossel = R.drawable.cabecalho_dupla_pascoa_carrossel;
                imagemEspecialDetalhes = R.drawable.cabecalho_dupla_pascoa_detalhes;
                corLetraLista = R.color.dupla_sena_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_dupla_sena_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_dupla_sena_fundo_escuro;
                tarjaEspecialTrevoCombo = R.drawable.tarja_trevo_dupla_especial_combos;
                break;
            case LOTOFACIL:
                corClara = R.color.lotofacil_claro_mkp;
                corEscura = R.color.lotofacil_escuro_mkp;
                trapezio = R.drawable.background_trapeze_lotofacil_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_lotofacil_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_loto_facil;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_loto_facil;
                imagemEspecialCarrossel = R.drawable.cabecalho_lotofacil_independencia_carrossel;
                imagemEspecialDetalhes = R.drawable.cabecalho_lotofacil_independencia_detalhes;
                corLetraLista = R.color.lotofacil_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_lotofacil_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_lotofacil_fundo_escuro;
                tarjaEspecialTrevoCombo = R.drawable.tarja_trevo_lotofacil_especial_combos;
                break;
            case LOTOMANIA:
                corClara = R.color.lotomania_claro_mkp;
                corEscura = R.color.lotomania_escuro_mkp;
                trapezio = R.drawable.background_trapeze_lotomanial_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_lotomania_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_lotomania;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_lotomania;
                imagemEspecialCarrossel = -1;
                imagemEspecialDetalhes = -1;
                corLetraLista = R.color.lotomania_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_lotomania_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_lotomania_fundo_escuro;
                tarjaEspecialTrevoCombo = -1;
                break;
            case MAIS_MILIONARIA:
                corClara = R.color.milionaria_claro_mkp;
                corEscura = R.color.milionaria_escuro_mkp;
                trapezio = R.drawable.background_trapeze_milionaria_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_milionaria_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_milionaria;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_milionaria;
                imagemEspecialCarrossel = -1;
                imagemEspecialDetalhes = -1;
                corLetraLista = R.color.milionaria_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_milionaria_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_milionaria_fundo_escuro;
                tarjaEspecialTrevoCombo = -1;
                break;
            case SUPER_7:
                corClara = R.color.super_sete_claro_mkp;
                corEscura = R.color.super_sete_escuro_mkp;
                trapezio = R.drawable.background_trapeze_super_sete_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_super_sete_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_super_sete;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_super_sete;
                imagemEspecialCarrossel = -1;
                imagemEspecialDetalhes = -1;
                corLetraLista = R.color.super_sete_letra_mkp;
                corFonteFundoClaro = R.color.super_sete_letra_mkp;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_super_sete_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_super_sete_fundo_escuro;
                tarjaEspecialTrevoCombo = -1;
                break;
            case LOTECA:
                corClara = R.color.loteca_claro_mkp;
                corEscura = R.color.loteca_escuro_mkp;
                trapezio = R.drawable.background_trapeze_loteca_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_loteca_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_loteca;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_loteca;
                imagemEspecialCarrossel = -1;
                imagemEspecialDetalhes = -1;
                corLetraLista = R.color.loteca_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_loteca_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_loteca_fundo_escuro;
                tarjaEspecialTrevoCombo = -1;
                break;
            case TIMEMANIA:
                corClara = R.color.timemania_claro_mkp;
                corEscura = R.color.timemania_escuro_mkp;
                trapezio = R.drawable.background_trapeze_timemania_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_timemania_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_timemania;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_timemania;
                imagemEspecialCarrossel = -1;
                imagemEspecialDetalhes = -1;
                corLetraLista = R.color.timemania_letra_mkp;
                corFonteFundoClaro = R.color.timemania_letra_mkp;
                corFonteFundoEscuro = R.color.timemania_letra_mkp;
                trevoFundoClaro = R.drawable.trevo_timemania_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_timemania_fundo_escuro;
                tarjaEspecialTrevoCombo = -1;
                break;
            case DIA_DE_SORTE:
                corClara = R.color.dia_sorte_claro_mkp;
                corEscura = R.color.dia_sorte_escuro_mkp;
                trapezio = R.drawable.background_trapeze_dia_sorte_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_dia_sorte_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao_dia_sorte;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline_dia_sorte;
                imagemEspecialCarrossel = -1;
                imagemEspecialDetalhes = -1;
                corLetraLista = R.color.dia_sorte_letra_mkp;
                corFonteFundoClaro = R.color.dia_sorte_letra_mkp;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_dia_de_sorte_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_dia_de_sorte_fundo_escuro;
                tarjaEspecialTrevoCombo = -1;
                break;
            case INSTANTANEA:
                corClara = R.color.instantanea_claro;
                corEscura = R.color.instantanea_escuro;
                trapezio = R.drawable.background_trapeze_instantanea;
                shapeBtnDetalhes = -1;
                imagemTriangulo = -1;
                imagemTrianguloOutline= -1;
                imagemEspecialCarrossel = -1;
                imagemEspecialDetalhes = -1;
                corLetraLista = R.color.instantanea_claro;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevos_instantanea_fundo_branco;
                trevoFundoEscuro = R.drawable.trevos_instantanea_fundo_escuro;
                tarjaEspecialTrevoCombo =-1;
                break;
            default:
                //MEGASENA
                corClara = R.color.mega_verde_claro_mkp;
                corEscura = R.color.mega_verde_escuro_mkp;
                trapezio = R.drawable.background_trapeze_megasena_mkp;
                shapeBtnDetalhes = R.drawable.btn_rounded_green_outlined;
                imagemTriangulo = R.drawable.ic_tag_bolao;
                imagemTrianguloOutline= R.drawable.ic_tag_bolao_outline;
                imagemEspecialCarrossel = R.drawable.cabecalho_mega_virada_carrossel;
                imagemEspecialDetalhes = R.drawable.cabecalho_mega_virada_detalhes;
                corLetraLista = R.color.mega_verde_claro_mkp;
                corFonteFundoClaro = R.color.branco;
                corFonteFundoEscuro = R.color.branco;
                trevoFundoClaro = R.drawable.trevo_megasena_fundo_branco;
                trevoFundoEscuro = R.drawable.trevo_megasena_fundo_escuro;
                tarjaEspecialTrevoCombo = R.drawable.tarja_trevo_mega_especial_combos;
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
    public int getImagemEspecialDetalhes() {
        return imagemEspecialDetalhes;
    }

    public int getCorLetraLista() {
        return corLetraLista;
    }

    public int getCorFonteFundoClaro() {
        return corFonteFundoClaro;
    }

    public int getCorFonteFundoEscuro() {
        return corFonteFundoEscuro;
    }

    public int getTrevoFundoClaro() {
        return trevoFundoClaro;
    }

    public int getTrevoFundoEscuro() {
        return trevoFundoEscuro;
    }

    public int getTarjaEspecialTrevoCombo() {
        return tarjaEspecialTrevoCombo;
    }
}

