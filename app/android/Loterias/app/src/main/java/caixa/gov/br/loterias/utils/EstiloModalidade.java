package caixa.gov.br.loterias.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.widget.ImageView;
import android.view.View;

import com.bumptech.glide.Glide;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.Modalidade;

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
                break;
            case LOTECA:
                this.corClara = R.color.lotecaclaro;
                this.corMedia = R.color.lotecaescuro;
                this.corEscura = R.color.lotecaescuro;
                imagemCalendario = R.drawable.icon_data_loteca;
                imagemRelogio = R.drawable.icon_horario_loteca;
                trevoBackground = R.drawable.background_trapeze_loteca;
                break;
            case FEDERAL:
                this.corClara = R.color.federalclaro;
                this.corMedia = R.color.federalescuro;
                this.corEscura = R.color.federalescuro;
                imagemCalendario = R.drawable.icon_data_federal;
                imagemRelogio = R.drawable.icon_horario_federal;
                trevoBackground= R.drawable.background_trapeze_federal;
                break;
            case LOTOGOL:
                this.corClara = R.color.lotogolclaro;
                this.corMedia = R.color.lotogolescuro;
                this.corEscura = R.color.lotogolescuro;
                imagemCalendario = R.drawable.icon_data_lotogol;
                imagemRelogio = R.drawable.icon_horario_lotogol;
                trevoBackground = R.drawable.background_trapeze_lotogol;
                break;
            case MEGA_SENA:
                this.corClara = R.color.megasenaclaro;
                this.corMedia = R.color.megasenamedio;
                this.corEscura = R.color.megasenaescuro;
                imagemCalendario = R.drawable.icon_data_megasena;
                imagemRelogio = R.drawable.icon_horario_megasena;
                trevoBackground = R.drawable.background_trapeze_megasena;
                break;
            case DUPLA_SENA:
                this.corClara = R.color.duplasenaclaro;
                this.corMedia = R.color.duplasenamedio;
                this.corEscura = R.color.duplasenaescuro;
                imagemCalendario = R.drawable.icon_data_duplasena;
                imagemRelogio = R.drawable.icon_horario_duplasena;
                trevoBackground = R.drawable.background_trapeze_duplasena;
                break;
            case LOTOFACIL:
                this.corClara = R.color.lotofacilclaro;
                this.corMedia = R.color.lotofacilmedio;
                this.corEscura = R.color.lotofacilescuro;
                imagemCalendario = R.drawable.icon_data_lotofacil;
                imagemRelogio = R.drawable.icon_horario_lotofacil;
                trevoBackground = R.drawable.background_trapeze_lotofacil;
                break;
            case LOTOMANIA:
                this.corClara = R.color.lotomaniaclaro;
                this.corMedia = R.color.lotomaniaescuro;
                this.corEscura = R.color.lotomaniaescuro;
                imagemCalendario = R.drawable.icon_data_lotomania;
                imagemRelogio = R.drawable.icon_horario_lotomania;
                trevoBackground = R.drawable.background_trapeze_lotomania;
                break;
            case TIMEMANIA:
                this.corClara = R.color.timemaniaclaro;
                this.corMedia = R.color.timemaniaescuro;
                this.corEscura = R.color.timemaniaescuro;
                imagemCalendario = R.drawable.icon_data_timemania;
                imagemRelogio = R.drawable.icon_horario_timemania;
                trevoBackground = R.drawable.background_trapeze_timemania;
                break;
            default:
                this.corClara = R.color.megasenaclaro;
                this.corMedia = R.color.megasenamedio;
                this.corEscura = R.color.megasenaescuro;
                imagemCalendario = R.drawable.icon_data_megasena;
                imagemRelogio = R.drawable.icon_horario_megasena;
                trevoBackground = R.drawable.background_trapeze_megasena;
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
}
