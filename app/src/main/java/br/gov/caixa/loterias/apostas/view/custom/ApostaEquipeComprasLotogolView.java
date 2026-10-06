package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.EscudoEquipeUtil;

/**
 * Created by cedesbr450 on 20/03/18.
 */

public class ApostaEquipeComprasLotogolView extends LinearLayout {

    private boolean alreadyInflated = false;

    private ImageView imagemEscudoApostaPartidaLotogol;
    private TextView textoApostaPartidaLotogol, placar_aposta_partida_lotogol_TextView;

    public static ApostaEquipeComprasLotogolView build(Context context) {
        ApostaEquipeComprasLotogolView instance = new ApostaEquipeComprasLotogolView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.row_aposta_compras_equipe_lotogol, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.imagemEscudoApostaPartidaLotogol = findViewById(R.id.imagem_escudo_aposta_partida_lotogol);
        this.textoApostaPartidaLotogol = findViewById(R.id.texto_aposta_partida_lotogol);
        this.placar_aposta_partida_lotogol_TextView = findViewById(R.id.placar_aposta_partida_lotogol_TextView);
    }

    public ApostaEquipeComprasLotogolView(Context context) {
        super(context);
    }

    public ApostaEquipeComprasLotogolView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ApostaEquipeComprasLotogolView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(EquipeDTO equipeDTO) {

        String paisTime = "";
        if (equipeDTO.getIndicadorSelecao() != null && equipeDTO.getIndicadorSelecao()) {
            paisTime = equipeDTO.getSiglaPais();
        }

        String descricao = String.format("%s%n%s", equipeDTO.getNome(), paisTime);
        EscudoEquipeUtil escudoEquipeUtil = new EscudoEquipeUtil();

        EscudoEquipeUtil.setEscudo(this.getContext(), equipeDTO.getParametroEquipe(), imagemEscudoApostaPartidaLotogol);
        this.textoApostaPartidaLotogol.setText(descricao);
        this.placar_aposta_partida_lotogol_TextView.setText(placarString(equipeDTO.getPlacar()));
    }

    private String placarString(String placar) {
        switch (placar) {
            case Constantes.ZERO_STRING:
                return "0";
            case Constantes.UM_STRING:
                return "1";
            case Constantes.DOIS_STRING:
                return "2";
            case Constantes.TRES_STRING:
                return "3";
            case Constantes.PLUS_STRING:
                return "+";
            default:
                return "";
        }
    }
}
