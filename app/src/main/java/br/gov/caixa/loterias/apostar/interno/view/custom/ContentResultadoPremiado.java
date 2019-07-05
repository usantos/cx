package br.gov.caixa.loterias.apostar.interno.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostar.interno.R;

/**
 * Created by joafilho on 02/04/2018.
 * Class ContentResultadoPremiado
 */

@EViewGroup(R.layout.content_resultado_premiado)
public class ContentResultadoPremiado extends LinearLayout {

    @ViewById
    protected LinearLayout topoModalidadeResultadoLinearLayout, fundoBackgroundTrapezeResultadoLinearLayout, topo2ModalidadeResultadoLinearLayout, premioGanhoLinearLayout;

    @ViewById
    protected TextView tituloModalidadeTextView, aberturaApostaResultadoTextView, encerramentoResultadoTextView, proximoSorteioTextView, premioResultadoTextView;

    @ViewById
    protected ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView;

    @ViewById
    protected Button botaoAposteAgora ;

    public ContentResultadoPremiado(Context context) {
        super(context);
    }

    public ContentResultadoPremiado(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentResultadoPremiado(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    public TextView getPremioResultadoTextView() {
        return premioResultadoTextView;
    }

    public Button getBotaoAposteAgora(){ return botaoAposteAgora; }

    public ExpandableHeightRecyclerView getListaFaixaPremiacaoExpandableHeightRecyclerView() {
        return listaFaixaPremiacaoExpandableHeightRecyclerView;
    }

    public LinearLayout getPremioGanhoLinearLayout() {
        return premioGanhoLinearLayout;
    }
}
