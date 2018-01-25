package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.EquipeDTO;
import caixa.gov.br.loterias.bo.silce.dto.PartidaLotecaDTO;
import caixa.gov.br.loterias.utils.Constantes;
import caixa.gov.br.loterias.utils.ViewUtils;

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
        apostaEquipeLotecaView.getImageApostaPartidaLoteca().setSelected(equipeDTO.getVitoria());
        apostaEquipeLotecaView.getTextApostaPartidaLoteca().setText(StringUtils.isEmpty(equipeDTO.getNome()) ? "Equipe" : equipeDTO.getNome());
        if (equipeDTO.getVitoria()) {
            apostaEquipeLotecaView.getTextApostaPartidaLoteca().setTypeface(ViewUtils.getFontFuturaBold(getContext()), Typeface.BOLD);
        }
    }
}
