package caixa.gov.br.loterias.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ParametroPartida;
import caixa.gov.br.loterias.view.adapter.LotogolAdapter;

/**
 * Created by joafilho on 09/01/2018.
 */
@EViewGroup(R.layout.row_partida_lotogol)
public class PartidaViewLotogol extends LinearLayout {

    @ViewById
    ItemEquipeViewLotogol lotogolTimeUm, lotogolTimeDois;

    private ParametroPartida parametroPartida;
    private LotogolAdapter.LotogolAdapterListener listener;

    @AfterViews
    protected void init() {

    }

    public PartidaViewLotogol(Context context) {
        super(context);
    }

    public PartidaViewLotogol(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartidaViewLotogol(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setLayout(ParametroPartida parametroPartida) {

        this.parametroPartida = parametroPartida;

        String paisTime1 = parametroPartida.getEquipe1().getIndicadorSelecao() ? "" : " / " + parametroPartida.getEquipe1().getPais();
        String paisTime2 = parametroPartida.getEquipe2().getIndicadorSelecao() ? "" : " / " + parametroPartida.getEquipe2().getPais();

        lotogolTimeUm.getItemImage().setImageResource(R.drawable.time);
        lotogolTimeUm.getItemTitle().setText(ViewUtils.fromHtml("<strong>" + parametroPartida.getEquipe1().getNome() + "</strong>" + paisTime1));

        lotogolTimeDois.getItemImage().setImageResource(R.drawable.time);
        lotogolTimeDois.getItemTitle().setText(ViewUtils.fromHtml("<strong>" + parametroPartida.getEquipe2().getNome() + "</strong>" + paisTime2));
    }

    public void setListener(LotogolAdapter.LotogolAdapterListener listener) {
        this.listener = listener;
    }
}
