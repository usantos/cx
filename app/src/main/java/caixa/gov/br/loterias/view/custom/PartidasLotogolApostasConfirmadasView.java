package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.EquipeDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotecaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotogolDTO;
import caixa.gov.br.loterias.utils.Constantes;
import caixa.gov.br.loterias.utils.EscudoEquipeUtil;

/**
 * Created by joafilho on 27/03/2018.
 * Class PartidasLotogolApostasConfirmadasView
 */
@EViewGroup(R.layout.row_apostas_confirmadas_partidas_lotogol)
public class PartidasLotogolApostasConfirmadasView extends LinearLayout {

    @ViewById
    ImageView imagemEscudoApostaEquipeLotogolUm, imagemPlacarApostaEquipeLotogolUm, imagemEscudoApostaEquipeLotogolDois, imagemPlacarApostaEquipeLotogolDois;

    @ViewById
    TextView textoApostaEquipeLotogolUm, textoApostaEquipeLotogolDois;

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
