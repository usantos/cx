package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;

/**
 * Created by joafilho on 26/01/2018.
 * Class ApostaPartidaLotogolView
 */

@EViewGroup(R.layout.row_aposta_partida_lotogol)
public class ApostaPartidaLotogolView extends LinearLayout {

    @ViewById
    View linhaDivisao;

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

    public View getLinhaDivisao() {
        return linhaDivisao;
    }
}
