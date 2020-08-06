package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import javax.annotation.Nullable;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.EquipeDTO;
import br.gov.caixa.loterias.apostas.utils.Constantes;
import br.gov.caixa.loterias.apostas.utils.EscudoEquipeUtil;

/**
 * Created by cedesbr450 on 20/03/18.
 */


@EViewGroup(R.layout.row_aposta_compras_equipe_lotogol)
public class ApostaEquipeComprasLotogolView extends LinearLayout {

    @ViewById
    ImageView imagemEscudoApostaPartidaLotogol;
//            , imagemPlacarApostaPartidaLotogol;

    @ViewById
    TextView textoApostaPartidaLotogol, placar_aposta_partida_lotogol_TextView;

    @AfterViews
    protected void init() {

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


//        this.imagemEscudoApostaPartidaLotogol.setImageResource(R.drawable.time);
        EscudoEquipeUtil.setEscudo(this.getContext(), equipeDTO.getParametroEquipe(), imagemEscudoApostaPartidaLotogol);
//        this.imagemPlacarApostaPartidaLotogol.setImageResource(placarImagem(equipeDTO.getPlacar()));
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
