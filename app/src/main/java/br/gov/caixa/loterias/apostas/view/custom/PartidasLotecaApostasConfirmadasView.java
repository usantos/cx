package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

/**
 * Created by joafilho on 26/03/2018.
 * Class PartidasLotecaApostasConfirmadasView
 */

@EViewGroup(R.layout.row_apostas_confirmadas_partidas_loteca)
public class PartidasLotecaApostasConfirmadasView extends LinearLayout {

    @ViewById
    TextView textApostaPartidaLotecaUm, textApostaPartidaLotecaDois, textApostaPartidaLotecaEmpate;

    @ViewById
    ImageView imageApostaPartidaLotecaUm, imageApostaPartidaLotecaDois, imageApostaPartidaLotecaEmpate;

    public PartidasLotecaApostasConfirmadasView(Context context) {
        super(context);
    }

    public PartidasLotecaApostasConfirmadasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartidasLotecaApostasConfirmadasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(PartidaLotecaDTO partidaLotecaDTO) {
        this.setValueApostaEquipeLotecaView(imageApostaPartidaLotecaUm, textApostaPartidaLotecaUm, partidaLotecaDTO.getEquipe1(), Utils.getEquipeComUf(partidaLotecaDTO.getEquipe1()));
        this.setValueApostaEquipeLotecaView(imageApostaPartidaLotecaDois, textApostaPartidaLotecaDois, partidaLotecaDTO.getEquipe2(), Utils.getEquipeComUf(partidaLotecaDTO.getEquipe2()));

        EquipeDTO equipeDTOEmpate = new EquipeDTO();
        equipeDTOEmpate.setVitoria(partidaLotecaDTO.getEmpate());
        equipeDTOEmpate.setNome(Constantes.X_STRING);
        this.setValueApostaEquipeLotecaView(imageApostaPartidaLotecaEmpate, textApostaPartidaLotecaEmpate, equipeDTOEmpate, equipeDTOEmpate.getNome());
    }

    private void setValueApostaEquipeLotecaView(ImageView imageView, TextView apostaEquipeLotecaView, EquipeDTO equipeDTO, String equipeNome) {
        imageView.setSelected(equipeDTO.getVitoria());
        apostaEquipeLotecaView.setText(equipeNome);
        if (equipeDTO.getVitoria()) {
            apostaEquipeLotecaView.setTypeface(ViewUtils.getFontArialMT(getContext()), Typeface.BOLD);
        }
    }
}
