package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.Tupla;
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
        Tupla<String, String> tuplaUfs = Utils.getSiglasUf(partidaLotecaDTO);
        this.setValueApostaEquipeLotecaView(imageApostaPartidaLotecaUm, textApostaPartidaLotecaUm, partidaLotecaDTO.getEquipe1(),partidaLotecaDTO.getEquipe1().getNome() + tuplaUfs.x);
        this.setValueApostaEquipeLotecaView(imageApostaPartidaLotecaDois, textApostaPartidaLotecaDois, partidaLotecaDTO.getEquipe2(),partidaLotecaDTO.getEquipe2().getNome() + tuplaUfs.y);

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
