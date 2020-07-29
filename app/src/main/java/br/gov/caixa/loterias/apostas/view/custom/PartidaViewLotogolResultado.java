package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PartidaLotogolDTO;
import br.gov.caixa.loterias.apostas.utils.EscudoEquipeUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

/**
 * Created by cedesbr450 on 02/04/18.
 */

@EViewGroup(R.layout.row_partida_lotogol_resultado)
public class PartidaViewLotogolResultado extends LinearLayout {

    @ViewById
    LinearLayout rowPartidaLotogol;

    @ViewById
    ItemEquipeViewLotogolResultado lotogolTimeUm, lotogolTimeDois;

    @ViewById
    TextView paginaResultadoLotogolText, diaSemanaDataPartidaText;

    @AfterViews
    protected void init() {

    }

    public PartidaViewLotogolResultado(Context context) {
        super(context);
    }

    public PartidaViewLotogolResultado(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartidaViewLotogolResultado(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(PartidaLotogolDTO parametroPartida) {

        lotogolTimeUm.setParametroEquipe(parametroPartida.getEquipe1().getParametroEquipe());
        lotogolTimeDois.setParametroEquipe(parametroPartida.getEquipe2().getParametroEquipe());

        String paisTime1 = "";
        if (parametroPartida.getEquipe1().getIndicadorSelecao() != null && parametroPartida.getEquipe1().getIndicadorSelecao()) {
            paisTime1 = " / " + parametroPartida.getEquipe1().getSiglaPais();
        } else {
            paisTime1 = " / " + parametroPartida.getEquipe1().getUf();
        }

        String paisTime2 = "";
        if (parametroPartida.getEquipe2().getIndicadorSelecao() != null && parametroPartida.getEquipe2().getIndicadorSelecao()) {
            paisTime2 = " / " + parametroPartida.getEquipe2().getSiglaPais();
        } else {
            paisTime2 = " / " + parametroPartida.getEquipe2().getUf();
        }

        lotogolTimeUm.getItemImage().setImageResource(R.drawable.time);
        EscudoEquipeUtil.setEscudo(this.getContext(), parametroPartida.getEquipe1().getParametroEquipe(), lotogolTimeUm.getItemImage());

        lotogolTimeUm.getItemTitle().setText(ViewUtils.fromHtml("<strong>" + parametroPartida.getEquipe1().getNome() + "</strong>" + paisTime1));
        lotogolTimeUm.getPlacarResultadoIremLotogolText().setText(parametroPartida.getEquipe1().getPlacar());

        lotogolTimeDois.getItemImage().setImageResource(R.drawable.time);
        EscudoEquipeUtil.setEscudo(this.getContext(), parametroPartida.getEquipe2().getParametroEquipe(), lotogolTimeDois.getItemImage());
        lotogolTimeDois.getItemTitle().setText(ViewUtils.fromHtml("<strong>" + parametroPartida.getEquipe2().getNome() + "</strong>" + paisTime2));
        lotogolTimeDois.getPlacarResultadoIremLotogolText().setText(parametroPartida.getEquipe2().getPlacar());

        diaSemanaDataPartidaText.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.texto_futura_bold, getResources().getString(R.string.label_domingo))));
    }

    public TextView getPaginaResultadoLotogolText() {
        return paginaResultadoLotogolText;
    }

    public void setPaginaResultadoLotogolText(TextView paginaResultadoLotogolText) {
        this.paginaResultadoLotogolText = paginaResultadoLotogolText;
    }
}
