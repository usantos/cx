package caixa.gov.br.loterias.utils;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;

/**
 * Created by brfernan on 27/10/2017.
 */

public class EstiloModalidade {

    private final int corClara;
    private final int corMedia;
    private final int corEscura;
    private final int imagemCalendario;
    private final int imagemRelogio;
    private final int trevoBackground;
    private final int stringId;
    private final int themeId;
    private final int menuItemColor;
    private final int iconValidadeComprovanteColor;
    private final int iconImpressoraColor;
    private final int colorTextAlpha6;

    public EstiloModalidade(ModalidadeEnum modalidade) {
        switch (modalidade) {
            case QUINA:
                corClara = R.color.quinaclaro;
                corMedia = R.color.quinamedio;
                corEscura = R.color.quinaescuro;
                imagemCalendario = R.drawable.icon_data_quina;
                imagemRelogio = R.drawable.icon_horario_quina;
                trevoBackground = R.drawable.background_trapeze_quina;
                themeId = R.style.AppTheme_Modalidade_Quina;
                stringId = R.string.label_quina;
                menuItemColor = R.color.branco;
                iconValidadeComprovanteColor = R.color.black;
                iconImpressoraColor = R.color.branco;
                colorTextAlpha6 = R.color.white_6;
                break;
            case LOTECA:
                corClara = R.color.lotecaclaro;
                corMedia = R.color.lotecaescuro;
                corEscura = R.color.lotecaescuro;
                imagemCalendario = R.drawable.icon_data_loteca;
                imagemRelogio = R.drawable.icon_horario_loteca;
                trevoBackground = R.drawable.background_trapeze_loteca;
                themeId = R.style.AppTheme_Modalidade_Loteca;
                stringId = R.string.label_loteca;
                menuItemColor = R.color.branco;
                iconValidadeComprovanteColor = R.color.black;
                iconImpressoraColor = R.color.branco;
                colorTextAlpha6 = R.color.white_6;
                break;
//            case FEDERAL:
//                corClara = R.color.federalclaro;
//                corMedia = R.color.federalescuro;
//                corEscura = R.color.federalescuro;
//                imagemCalendario = R.drawable.icon_data_federal;
//                imagemRelogio = R.drawable.icon_horario_federal;
//                trevoBackground= R.drawable.background_trapeze_federal;
//                themeId = R.style.AppTheme_Modalidade_Federal;
//                stringId = R.string.label_federal;
//                menuItemColor = R.color.branco;
//                break;
            case LOTOGOL:
                corClara = R.color.lotogolclaro;
                corMedia = R.color.lotogolescuro;
                corEscura = R.color.lotogolescuro;
                imagemCalendario = R.drawable.icon_data_lotogol;
                imagemRelogio = R.drawable.icon_horario_lotogol;
                trevoBackground = R.drawable.background_trapeze_lotogol;
                themeId = R.style.AppTheme_Modalidade_LotoGol;
                stringId = R.string.label_lotogol;
                menuItemColor = R.color.branco;
                iconValidadeComprovanteColor = R.color.black;
                iconImpressoraColor = R.color.branco;
                colorTextAlpha6 = R.color.white_6;
                break;
            case DUPLA_SENA:
                corClara = R.color.duplasenaclaro;
                corMedia = R.color.duplasenamedio;
                corEscura = R.color.duplasenaescuro;
                imagemCalendario = R.drawable.icon_data_duplasena;
                imagemRelogio = R.drawable.icon_horario_duplasena;
                trevoBackground = R.drawable.background_trapeze_duplasena;
                themeId = R.style.AppTheme_Modalidade_DuplaSena;
                stringId = R.string.label_dupla_sena;
                menuItemColor = R.color.branco;
                iconValidadeComprovanteColor = R.color.black;
                iconImpressoraColor = R.color.branco;
                colorTextAlpha6 = R.color.white_6;
                break;
            case LOTOFACIL:
                corClara = R.color.lotofacilclaro;
                corMedia = R.color.lotofacilmedio;
                corEscura = R.color.lotofacilescuro;
                imagemCalendario = R.drawable.icon_data_lotofacil;
                imagemRelogio = R.drawable.icon_horario_lotofacil;
                trevoBackground = R.drawable.background_trapeze_lotofacil;
                themeId = R.style.AppTheme_Modalidade_Lotofacil;
                stringId = R.string.label_lotofacil;
                menuItemColor = R.color.branco;
                iconValidadeComprovanteColor = R.color.black;
                iconImpressoraColor = R.color.branco;
                colorTextAlpha6 = R.color.white_6;
                break;
            case LOTOMANIA:
                corClara = R.color.lotomaniaclaro;
                corMedia = R.color.lotomaniaescuro;
                corEscura = R.color.lotomaniaescuro;
                imagemCalendario = R.drawable.icon_data_lotomania;
                imagemRelogio = R.drawable.icon_horario_lotomania;
                trevoBackground = R.drawable.background_trapeze_lotomania;
                themeId = R.style.AppTheme_Modalidade_Lotomania;
                stringId = R.string.label_lotomania;
                menuItemColor = R.color.branco;
                iconValidadeComprovanteColor = R.color.black;
                iconImpressoraColor = R.color.branco;
                colorTextAlpha6 = R.color.white_6;
                break;
            case TIMEMANIA:
                corClara = R.color.timemaniaclaro;
                corMedia = R.color.timemaniaescuro;
                corEscura = R.color.timemaniaescuro;
                imagemCalendario = R.drawable.icon_data_timemania;
                imagemRelogio = R.drawable.icon_horario_timemania;
                trevoBackground = R.drawable.background_trapeze_timemania;
                themeId = R.style.AppTheme_Modalidade_Timemania;
                stringId = R.string.label_timemania;
                menuItemColor = R.color.megasenamedio;
                iconValidadeComprovanteColor = R.color.megasenamedio;
                iconImpressoraColor = R.color.megasenamedio;
                colorTextAlpha6 = R.color.verdemediotransparente;
                break;
            case DIA_DE_SORTE:
                corClara = R.color.megasenaclaro;
                corMedia = R.color.megasenamedio;
                corEscura = R.color.megasenaescuro;
                imagemCalendario = R.drawable.icon_data_megasena;
                imagemRelogio = R.drawable.icon_horario_megasena;
                trevoBackground = R.drawable.background_trapeze_megasena;
                themeId = R.style.AppTheme_Modalidade_MegaSena;
                stringId = R.string.label_dia_de_sorte;
                menuItemColor = R.color.branco;
                iconValidadeComprovanteColor = R.color.black;
                iconImpressoraColor = R.color.branco;
                colorTextAlpha6 = R.color.white_6;
                break;
            default: {
                //MEGASENA
                corClara = R.color.megasenaclaro;
                corMedia = R.color.megasenamedio;
                corEscura = R.color.megasenaescuro;
                imagemCalendario = R.drawable.icon_data_megasena;
                imagemRelogio = R.drawable.icon_horario_megasena;
                trevoBackground = R.drawable.background_trapeze_megasena;
                themeId = R.style.AppTheme_Modalidade_MegaSena;
                stringId = R.string.label_mega_sena;
                menuItemColor = R.color.branco;
                iconValidadeComprovanteColor = R.color.black;
                iconImpressoraColor = R.color.branco;
                colorTextAlpha6 = R.color.white_6;
            }
        }

    }

    public int getMenuItemColor() {
        return menuItemColor;
    }

    public int getCorClara() {
        return corClara;
    }

    public int getCorMedia() {
        return corMedia;
    }

    public int getCorEscura() {
        return corEscura;
    }

    public int getImagemCalendario() {
        return imagemCalendario;
    }

    public int getImagemRelogio() {
        return imagemRelogio;
    }

    public int getTrevoBackground() {
        return trevoBackground;
    }

    public int getStringId() {
        return stringId;
    }

    public int getThemeId() {
        return themeId;
    }

    public int getIconValidadeComprovanteColor() {
        return iconValidadeComprovanteColor;
    }

    public int getIconImpressoraColor() {
        return iconImpressoraColor;
    }

    public int getColorTextAlpha6() {
        return colorTextAlpha6;
    }
}
