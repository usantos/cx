package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostas.R;

/**
 * Created by joafilho on 02/04/2018.
 * Class ContentResultadoPremiado
 */

@EViewGroup(R.layout.content_resultado_premiado)
public class ContentResultadoPremiado extends LinearLayout {

    @ViewById
    protected LinearLayout premioInfoLinearLayout,topoModalidadeResultadoLinearLayout, fundoBackgroundTrapezeResultadoLinearLayout, topoResultadoPremioLinearLayout, premioGanhoLinearLayout;

    @ViewById
    protected TextView valorEstimativaConcursoAtualTextView,valorPremioTituloTextView,tituloModalidadeTextView, proximoSorteioTextView, nsbBilhete;

    @ViewById
    protected ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView;

    @ViewById
    protected ConstraintLayout clVerDetalheAposta;

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

    public LinearLayout getTopoResultadoPremioLinearLayout() {
        return topoResultadoPremioLinearLayout;
    }

    public LinearLayout getPremioInfoLinearLayout() {
        return premioInfoLinearLayout;
    }

    public TextView getTituloModalidadeTextView() {
        return tituloModalidadeTextView;
    }

    public TextView getValorPremioTituloTextView() { return valorPremioTituloTextView; }

    public TextView getNSBTextView() { return nsbBilhete; }

    public TextView getValorEstimativaConcursoAtualTextView() { return valorEstimativaConcursoAtualTextView; }

    public TextView getProximoSorteioTextView() {
        return proximoSorteioTextView;
    }

    public Button getBotaoAposteAgora(){ return botaoAposteAgora; }

    public ExpandableHeightRecyclerView getListaFaixaPremiacaoExpandableHeightRecyclerView() {
        return listaFaixaPremiacaoExpandableHeightRecyclerView;
    }

    public LinearLayout getPremioGanhoLinearLayout() {
        return premioGanhoLinearLayout;
    }

    public ConstraintLayout getButtonVerDetalheAposta() {
        return clVerDetalheAposta;
    }
}
