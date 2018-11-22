package br.gov.caixa.loterias.apostar.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.PartidaLotogolDTO;

/**
 * Created by cedesbr450 on 20/03/18.
 */


@EViewGroup(R.layout.row_aposta_compras_partida_lotogol)
public class ApostaPartidaComprasLotogolView extends LinearLayout {

    @ViewById
    LinearLayout layoutRowApostaPartidaLotogol;

    @ViewById
    ApostaEquipeComprasLotogolView apostaEquipeLotogolUm, apostaEquipeLotogolDois;

    @AfterViews
    protected void init() {

    }

    public ApostaPartidaComprasLotogolView(Context context) {
        super(context);
    }

    public ApostaPartidaComprasLotogolView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ApostaPartidaComprasLotogolView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
