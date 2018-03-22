package caixa.gov.br.loterias.utils;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;

/**
 * Created by brfernan on 27/10/2017.
 */

public class EstiloModalidade {
    private final ModalidadeEnum tipoModalidade;
    private final int corClara;
    private final int corMedia;
    private final int corEscura;
    private final int imagemCalendario;
    private final int imagemRelogio;
    private final int trevoBackground;
    private final int stringId;
    private final int themeId;

    public EstiloModalidade(ModalidadeEnum tipoModalidade) {
        this.tipoModalidade = tipoModalidade;

        switch (tipoModalidade) {
            case QUINA:
                this.corClara = R.color.quinaclaro;
                this.corMedia = R.color.quinamedio;
                this.corEscura = R.color.quinaescuro;
                imagemCalendario = R.drawable.icon_data_quina;
                imagemRelogio = R.drawable.icon_horario_quina;
                trevoBackground = R.drawable.background_trapeze_quina;
                themeId = R.style.AppTheme_Modalidade_Quina;
                stringId = R.string.label_quina;
                break;
            case LOTECA:
                this.corClara = R.color.lotecaclaro;
                this.corMedia = R.color.lotecaescuro;
                this.corEscura = R.color.lotecaescuro;
                imagemCalendario = R.drawable.icon_data_loteca;
                imagemRelogio = R.drawable.icon_horario_loteca;
                trevoBackground = R.drawable.background_trapeze_loteca;
                themeId = R.style.AppTheme_Modalidade_Loteca;
                stringId = R.string.label_loteca;
                break;
            case FEDERAL:
                this.corClara = R.color.federalclaro;
                this.corMedia = R.color.federalescuro;
                this.corEscura = R.color.federalescuro;
                imagemCalendario = R.drawable.icon_data_federal;
                imagemRelogio = R.drawable.icon_horario_federal;
                trevoBackground= R.drawable.background_trapeze_federal;
                themeId = R.style.AppTheme_Modalidade_Federal;
                stringId = R.string.label_federal;
                break;
            case LOTOGOL:
                this.corClara = R.color.lotogolclaro;
                this.corMedia = R.color.lotogolescuro;
                this.corEscura = R.color.lotogolescuro;
                imagemCalendario = R.drawable.icon_data_lotogol;
                imagemRelogio = R.drawable.icon_horario_lotogol;
                trevoBackground = R.drawable.background_trapeze_lotogol;
                themeId = R.style.AppTheme_Modalidade_LotoGol;
                stringId = R.string.label_lotogol;
                break;
            case MEGA_SENA:
                this.corClara = R.color.megasenaclaro;
                this.corMedia = R.color.megasenamedio;
                this.corEscura = R.color.megasenaescuro;
                imagemCalendario = R.drawable.icon_data_megasena;
                imagemRelogio = R.drawable.icon_horario_megasena;
                trevoBackground = R.drawable.background_trapeze_megasena;
                themeId = R.style.AppTheme_Modalidade_MegaSena;
                stringId = R.string.label_mega_sena;
                break;
            case DUPLA_SENA:
                this.corClara = R.color.duplasenaclaro;
                this.corMedia = R.color.duplasenamedio;
                this.corEscura = R.color.duplasenaescuro;
                imagemCalendario = R.drawable.icon_data_duplasena;
                imagemRelogio = R.drawable.icon_horario_duplasena;
                trevoBackground = R.drawable.background_trapeze_duplasena;
                themeId = R.style.AppTheme_Modalidade_DuplaSena;
                stringId = R.string.label_dupla_sena;
                break;
            case LOTOFACIL:
                this.corClara = R.color.lotofacilclaro;
                this.corMedia = R.color.lotofacilmedio;
                this.corEscura = R.color.lotofacilescuro;
                imagemCalendario = R.drawable.icon_data_lotofacil;
                imagemRelogio = R.drawable.icon_horario_lotofacil;
                trevoBackground = R.drawable.background_trapeze_lotofacil;
                themeId = R.style.AppTheme_Modalidade_Lotofacil;
                stringId = R.string.label_lotofacil;
                break;
            case LOTOMANIA:
                this.corClara = R.color.lotomaniaclaro;
                this.corMedia = R.color.lotomaniaescuro;
                this.corEscura = R.color.lotomaniaescuro;
                imagemCalendario = R.drawable.icon_data_lotomania;
                imagemRelogio = R.drawable.icon_horario_lotomania;
                trevoBackground = R.drawable.background_trapeze_lotomania;
                themeId = R.style.AppTheme_Modalidade_Lotomania;
                stringId = R.string.label_lotomania;
                break;
            case TIMEMANIA:
                this.corClara = R.color.timemaniaclaro;
                this.corMedia = R.color.timemaniaescuro;
                this.corEscura = R.color.timemaniaescuro;
                imagemCalendario = R.drawable.icon_data_timemania;
                imagemRelogio = R.drawable.icon_horario_timemania;
                trevoBackground = R.drawable.background_trapeze_timemania;
                themeId = R.style.AppTheme_Modalidade_Timemania;
                stringId = R.string.label_timemania;
                break;
            default:
                this.corClara = R.color.megasenaclaro;
                this.corMedia = R.color.megasenamedio;
                this.corEscura = R.color.megasenaescuro;
                imagemCalendario = R.drawable.icon_data_megasena;
                imagemRelogio = R.drawable.icon_horario_megasena;
                trevoBackground = R.drawable.background_trapeze_megasena;
                themeId = R.style.AppTheme_Modalidade_MegaSena;
                stringId = R.string.label_mega_sena;
                break;
        }
    }

    public ModalidadeEnum getTipoModalidade() {
        return tipoModalidade;
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

    public int getTrevoBackground() { return trevoBackground; }

    public int getStringId() {
        return stringId;
    }

    public int getThemeId() {
        return themeId;
    }
}
