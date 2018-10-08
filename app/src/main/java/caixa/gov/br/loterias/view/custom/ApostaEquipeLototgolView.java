package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.EquipeDTO;
import caixa.gov.br.loterias.utils.Constantes;
import caixa.gov.br.loterias.utils.EscudoEquipeUtil;

/**
 * Created by joafilho on 26/01/2018.
 * Class ApostaEquipeLototgolView
 */

@EViewGroup(R.layout.row_aposta_equipe_lotogol)
public class ApostaEquipeLototgolView extends LinearLayout {

    @ViewById
    ImageView imagemEscudoApostaPartidaLotogol, imagemPlacarApostaPartidaLotogol;

    @ViewById
    TextView textoApostaPartidaLotogol;

    @AfterViews
    protected void init() {

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
        if (equipeDTO.getIndicadorSelecao() != null && equipeDTO.getIndicadorSelecao()) {
            paisTime = equipeDTO.getSiglaPais();
        }

        String descricao = String.format("%s%n%s", equipeDTO.getNome(), paisTime);
        EscudoEquipeUtil.setEscudo(this.getContext(), equipeDTO.getParametroEquipe(), imagemEscudoApostaPartidaLotogol);
        this.imagemPlacarApostaPartidaLotogol.setImageResource(placarImagem(equipeDTO.getPlacar()));
        this.textoApostaPartidaLotogol.setText(descricao);
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
