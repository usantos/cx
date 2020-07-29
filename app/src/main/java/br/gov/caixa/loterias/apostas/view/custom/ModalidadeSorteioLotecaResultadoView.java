package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.view.adapter.LotecaResultadoTimesAdapter;

/**
 * Created by cedesbr450 on 28/03/18.
 */



@EViewGroup(R.layout.item_resultado_loteca)
public class ModalidadeSorteioLotecaResultadoView extends LinearLayout implements DiscreteScrollView.ScrollListener<LotecaResultadoTimesAdapter.LotecaHolder>,
        DiscreteScrollView.OnItemChangedListener<LotecaResultadoTimesAdapter.LotecaHolder> {

    @ViewById
    protected DiscreteScrollView recyclerPartidasLotecas;

    @ViewById
    protected LinearLayout  item_resultado_content_view;

    @ViewById
    protected View tracoViewDivisoriaResultado;

    @ViewById
    protected PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;

    @AfterViews
    protected void init() {
    }

    public void setLayout(Modalidade modalidade) {

        EstiloModalidade estilo = new EstiloModalidade(modalidade.getTipoModalidade());
        tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));
        LotecaResultadoTimesAdapter lotecaResultadoTimesAdapter;
        lotecaResultadoTimesAdapter = new LotecaResultadoTimesAdapter(getContext(), modalidade.getResultadoConcursoDTO().getPartidasLoteca(), modalidade.getTipoModalidade());

        this.recyclerPartidasLotecas.setAdapter(lotecaResultadoTimesAdapter);
        this.recyclerPartidasLotecas.addScrollListener(this);
        this.recyclerPartidasLotecas.addOnItemChangedListener(this);
        this.recyclerPartidasLotecas.scrollToPosition(0);
        this.recyclerPartidasLotecas.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        this.recyclerPartidasLotecas.addOnItemTouchListener(mScrollTouchListener);

        this.partidaViewDetalhesResultadoView.setLayout(modalidade);

    }

    public ModalidadeSorteioLotecaResultadoView(Context context) {
        super(context);
    }

    public ModalidadeSorteioLotecaResultadoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ModalidadeSorteioLotecaResultadoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onCurrentItemChanged(@Nullable LotecaResultadoTimesAdapter.LotecaHolder viewHolder, int adapterPosition) {

    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable LotecaResultadoTimesAdapter.LotecaHolder currentHolder, @Nullable LotecaResultadoTimesAdapter.LotecaHolder newCurrent) {

    }

    RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            int action = e.getAction();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    ((PrincipalActivity) getContext()).homeCarousel.requestDisallowInterceptTouchEvent(true);
//                    rv.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    };
}
