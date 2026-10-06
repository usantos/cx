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
 * Created by joafilho on 26/01/2018.
 * Class ApostaEquipeLototgolView
 */

public class ApostaEquipeLototgolView extends LinearLayout {

    private boolean alreadyInflated = false;

    private ImageView imagemEscudoApostaPartidaLotogol, imagemPlacarApostaPartidaLotogol;

    private TextView textoApostaPartidaLotogol;

    public static ApostaEquipeLototgolView build(Context context) {
        ApostaEquipeLototgolView instance = new ApostaEquipeLototgolView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.row_aposta_equipe_lotogol, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.imagemEscudoApostaPartidaLotogol = findViewById(R.id.imagem_escudo_aposta_partida_lotogol);
        this.imagemPlacarApostaPartidaLotogol = findViewById(R.id.imagem_placar_aposta_partida_lotogol);
        this.textoApostaPartidaLotogol = findViewById(R.id.texto_aposta_partida_lotogol);
    }

    public ApostaEquipeLototgolView(Context context) {
        super(context);
    }

    public ApostaEquipeLototgolView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ApostaEquipeLototgolView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(EquipeDTO equipeDTO) {

        String paisTime = "";
        if (equipeDTO != null && equipeDTO.getIndicadorSelecao() != null && equipeDTO.getIndicadorSelecao()) {
            paisTime = equipeDTO.getSiglaPais();
        }

        if (equipeDTO != null) {
            String descricao = "";
            if (equipeDTO.getNome() != null) {
                descricao = String.format("%s%n%s", equipeDTO.getNome(), paisTime);
            }
            if (equipeDTO.getParametroEquipe() != null){
                EscudoEquipeUtil.setEscudo(this.getContext(), equipeDTO.getParametroEquipe(), imagemEscudoApostaPartidaLotogol);
            }
            if (equipeDTO.getPlacar() != null){
                this.imagemPlacarApostaPartidaLotogol.setImageResource(placarImagem(equipeDTO.getPlacar()));
            }
            this.textoApostaPartidaLotogol.setText(descricao);
        }
    }

    private int placarImagem(String placar) {
        switch (placar) {
            case Constantes.ZERO_STRING:
                return R.drawable.lotogol_selecionado_0;
            case Constantes.UM_STRING:
                return R.drawable.lotogol_selecionado_1;
            case Constantes.DOIS_STRING:
                return R.drawable.lotogol_selecionado_2;
            case Constantes.TRES_STRING:
                return R.drawable.lotogol_selecionado_3;
            case Constantes.PLUS_STRING:
                return R.drawable.lotogol_selecionado;
            default:
                return R.drawable.lotogol_0;
        }
    }
}
