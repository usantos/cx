package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.EscudoEquipeUtil;

/**
 * Created by joafilho on 27/03/2018.
 * Class PartidasLotogolApostasConfirmadasView
 */
public class PartidasLotogolApostasConfirmadasView extends LinearLayout {

    private boolean alreadyInflated = false;

    private ImageView imagemEscudoApostaEquipeLotogolUm, imagemPlacarApostaEquipeLotogolUm, imagemEscudoApostaEquipeLotogolDois, imagemPlacarApostaEquipeLotogolDois;
    private TextView textoApostaEquipeLotogolUm, textoApostaEquipeLotogolDois;

    public static PartidasLotogolApostasConfirmadasView build(Context context) {
        PartidasLotogolApostasConfirmadasView instance = new PartidasLotogolApostasConfirmadasView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.row_apostas_confirmadas_partidas_lotogol, this);
        }
        super.onFinishInflate();
        init();
    }

    private void init() {
        this.imagemEscudoApostaEquipeLotogolUm = findViewById(R.id.imagemEscudoApostaEquipeLotogolUm);
        this.imagemPlacarApostaEquipeLotogolUm = findViewById(R.id.imagemPlacarApostaEquipeLotogolUm);
        this.imagemEscudoApostaEquipeLotogolDois = findViewById(R.id.imagemEscudoApostaEquipeLotogolDois);
        this.imagemPlacarApostaEquipeLotogolDois = findViewById(R.id.imagemPlacarApostaEquipeLotogolDois);
        this.textoApostaEquipeLotogolUm = findViewById(R.id.textoApostaEquipeLotogolUm);
        this.textoApostaEquipeLotogolDois = findViewById(R.id.textoApostaEquipeLotogolDois);
    }

    public void setLayout() {

    }

    public PartidasLotogolApostasConfirmadasView(Context context) {
        super(context);
    }

    public PartidasLotogolApostasConfirmadasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartidasLotogolApostasConfirmadasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(PartidaLotogolDTO partidaLotogolDTO) {
        this.setEquipe(partidaLotogolDTO.getEquipe1(), imagemEscudoApostaEquipeLotogolUm, imagemPlacarApostaEquipeLotogolUm, textoApostaEquipeLotogolUm);
        this.setEquipe(partidaLotogolDTO.getEquipe2(), imagemEscudoApostaEquipeLotogolDois, imagemPlacarApostaEquipeLotogolDois, textoApostaEquipeLotogolDois);
    }

    private void setEquipe(EquipeDTO equipeDTO, ImageView escudo, ImageView placar, TextView descricao) {
        String paisTime = "";
        if (equipeDTO.getIndicadorSelecao() != null && equipeDTO.getIndicadorSelecao()) {
            paisTime = equipeDTO.getSiglaPais();
        }

        String descricaoString = String.format("%s%n%s", equipeDTO.getNome(), paisTime);

        EscudoEquipeUtil.setEscudo(this.getContext(), equipeDTO.getParametroEquipe(), escudo);
        placar.setImageResource(placarImagem(equipeDTO.getPlacar()));
        descricao.setText(descricaoString);
    }

    private int placarImagem(String placar) {
        switch (placar) {
            case Constantes.ZERO_STRING:
                return R.drawable.lotogol_0;
            case Constantes.UM_STRING:
                return R.drawable.lotogol_1;
            case Constantes.DOIS_STRING:
                return R.drawable.lotogol_2;
            case Constantes.TRES_STRING:
                return R.drawable.lotogol_3;
            case Constantes.PLUS_STRING:
                return R.drawable.lotogol;
            default:
                return R.drawable.lotogol_0;
        }
    }
}
