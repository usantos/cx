package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;

/**
 * Created by joafilho on 02/04/2018.
 * Class ContenteResultadoPremiado
 */

@EViewGroup(R.layout.contente_resultado_premiado)
public class ContenteResultadoPremiado extends LinearLayout {

    @ViewById
    protected LinearLayout topoModalidadeResultadoLinearLayout, fundoBackgroundTrapezeResultadoLinearLayout, topo2ModalidadeResultadoLinearLayout;

    @ViewById
    protected TextView tituloModalidadeTextView, aberturaApostaResultadoTextView, encerramentoResultadoTextView, proximoSorteioTextView, limiteDiarioResultadoTextView, premioResultadoTextView;

    @ViewById
    protected ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView;

    public ContenteResultadoPremiado(Context context) {
        super(context);
    }

    public ContenteResultadoPremiado(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContenteResultadoPremiado(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearLayout getTopoModalidadeResultadoLinearLayout() {
        return topoModalidadeResultadoLinearLayout;
    }

    public LinearLayout getFundoBackgroundTrapezeResultadoLinearLayout() {
        return fundoBackgroundTrapezeResultadoLinearLayout;
    }

    public LinearLayout getTopo2ModalidadeResultadoLinearLayout() {
        return topo2ModalidadeResultadoLinearLayout;
    }

    public TextView getTituloModalidadeTextView() {
        return tituloModalidadeTextView;
    }

    public TextView getAberturaApostaResultadoTextView() {
        return aberturaApostaResultadoTextView;
    }

    public TextView getEncerramentoResultadoTextView() {
        return encerramentoResultadoTextView;
    }

    public TextView getProximoSorteioTextView() {
        return proximoSorteioTextView;
    }

    public TextView getLimiteDiarioResultadoTextView() {
        return limiteDiarioResultadoTextView;
    }

    public TextView getPremioResultadoTextView() {
        return premioResultadoTextView;
    }

    public ExpandableHeightRecyclerView getListaFaixaPremiacaoExpandableHeightRecyclerView() {
        return listaFaixaPremiacaoExpandableHeightRecyclerView;
    }
}
