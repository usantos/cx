package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;

/**
 * Created by cedesbr450 on 20/03/18.
 */


public class ApostaPartidaComprasLotogolView extends LinearLayout {

    private boolean alreadyInflated = false;

    private LinearLayout layoutRowApostaPartidaLotogol;
    private ApostaEquipeComprasLotogolView apostaEquipeLotogolUm, apostaEquipeLotogolDois;

    public static ApostaPartidaComprasLotogolView build(Context context) {
        ApostaPartidaComprasLotogolView instance = new ApostaPartidaComprasLotogolView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.row_aposta_compras_partida_lotogol, this);
        }
        super.onFinishInflate();
        init();
    }

    //@AfterViews
    protected void init() {
        this.layoutRowApostaPartidaLotogol = findViewById(R.id.layout_row_aposta_partida_lotogol);
        this.apostaEquipeLotogolUm = findViewById(R.id.aposta_equipe_lotogol_um);
        this.apostaEquipeLotogolDois = findViewById(R.id.aposta_equipe_lotogol_dois);
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
