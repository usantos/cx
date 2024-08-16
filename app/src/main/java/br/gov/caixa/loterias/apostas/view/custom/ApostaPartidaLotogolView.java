package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;

/**
 * Created by joafilho on 26/01/2018.
 * Class ApostaPartidaLotogolView
 */

public class ApostaPartidaLotogolView extends LinearLayout {

    private boolean alreadyInflated = false;

    private View linhaDivisao;
    private ApostaEquipeLototgolView apostaEquipeLotogolUm, apostaEquipeLotogolDois;

    public static ApostaPartidaLotogolView build(Context context) {
        ApostaPartidaLotogolView instance = new ApostaPartidaLotogolView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.row_aposta_partida_lotogol, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.linhaDivisao = findViewById(R.id.linhaDivisao);
        this.apostaEquipeLotogolUm = findViewById(R.id.aposta_equipe_lotogol_um);
        this.apostaEquipeLotogolDois = findViewById(R.id.aposta_equipe_lotogol_dois);
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
