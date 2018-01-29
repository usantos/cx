package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.PartidaLotogolDTO;

/**
 * Created by joafilho on 26/01/2018.
 * Class ApostaPartidaLotogolView
 */

@EViewGroup(R.layout.row_aposta_partida_lotogol)
public class ApostaPartidaLotogolView extends LinearLayout {

    @ViewById
    LinearLayout layoutRowApostaPartidaLotogol;

    @ViewById
    ApostaEquipeLototgolView apostaEquipeLotogolUm, apostaEquipeLotogolDois;

    @AfterViews
    protected void init() {

    }

    public ApostaPartidaLotogolView(Context context) {
        super(context);
    }

    public ApostaPartidaLotogolView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ApostaPartidaLotogolView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(PartidaLotogolDTO partidaLotogolDTO) {
        apostaEquipeLotogolUm.setLayout(partidaLotogolDTO.getEquipe1());
        apostaEquipeLotogolDois.setLayout(partidaLotogolDTO.getEquipe2());
    }

    public LinearLayout getLayoutRowApostaPartidaLotogol() {
        return layoutRowApostaPartidaLotogol;
    }
}
