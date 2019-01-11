package br.gov.caixa.loterias.apostar.interno.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PartidaLotecaDTO;
import br.gov.caixa.loterias.apostar.interno.utils.Constantes;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

/**
 * Created by joafilho on 25/01/2018.
 * Class ApostaPartidaLotecaView
 */
@EViewGroup(R.layout.row_apostas_partidas_loteca)
public class ApostaPartidaLotecaView extends LinearLayout {

    @ViewById
    ApostaEquipeLotecaView apostasPartidaLotecaViewUm, apostasPartidaLotecaViewEmpate, apostasPartidaLotecaViewDois;

    @AfterViews
    protected void init() {

    }

    public ApostaPartidaLotecaView(Context context) {
        super(context);
    }

    public ApostaPartidaLotecaView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ApostaPartidaLotecaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ApostaPartidaLotecaView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setLayout(PartidaLotecaDTO partidaLotecaDTO) {
        this.setValueApostaEquipeLotecaView(apostasPartidaLotecaViewUm, partidaLotecaDTO.getEquipe1());
        this.setValueApostaEquipeLotecaView(apostasPartidaLotecaViewDois, partidaLotecaDTO.getEquipe2());

        EquipeDTO equipeDTOEmpate = new EquipeDTO();
        equipeDTOEmpate.setVitoria(partidaLotecaDTO.getEmpate());
        equipeDTOEmpate.setNome(Constantes.X_STRING);
        this.setValueApostaEquipeLotecaView(apostasPartidaLotecaViewEmpate, equipeDTOEmpate);
    }

    private void setValueApostaEquipeLotecaView(ApostaEquipeLotecaView apostaEquipeLotecaView, EquipeDTO equipeDTO) {

        String equipeNome = "";

        apostaEquipeLotecaView.getImageApostaPartidaLoteca().setSelected(equipeDTO.getVitoria());

        if (equipeDTO.getIndicadorSelecao() != null && !equipeDTO.getIndicadorSelecao()) {
            equipeNome = String.format("%s/%s", equipeDTO.getNome(), equipeDTO.getSiglaPais());
        } else {
            equipeNome = equipeDTO.getNome();
        }
        apostaEquipeLotecaView.getTextApostaPartidaLoteca().setText(equipeNome);
        if (equipeDTO.getVitoria()) {
            apostaEquipeLotecaView.getTextApostaPartidaLoteca().setTypeface(ViewUtils.getFontArialMT(getContext()), Typeface.BOLD);
        }
    }
}
