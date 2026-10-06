package br.gov.caixa.loterias.apostas.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaResultadoTimesAdapter;

/**
 * Created by cedesbr450 on 28/03/18.
 */


public class ModalidadeSorteioLotecaResultadoView extends LinearLayout implements DiscreteScrollView.ScrollListener<LotecaResultadoTimesAdapter.LotecaHolder>,
        DiscreteScrollView.OnItemChangedListener<LotecaResultadoTimesAdapter.LotecaHolder> {

    private boolean alreadyInflated = false;

    private DiscreteScrollView recyclerPartidasLotecas;
    private LinearLayout  item_resultado_content_view;
    private View tracoViewDivisoriaResultado;
    private PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;

    public static ModalidadeSorteioLotecaResultadoView build(Context context) {
        ModalidadeSorteioLotecaResultadoView instance = new ModalidadeSorteioLotecaResultadoView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_resultado_loteca, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.recyclerPartidasLotecas = findViewById(R.id.recyclerPartidasLotecas);
        this.item_resultado_content_view = findViewById(R.id.item_resultado_content_view);
        this.tracoViewDivisoriaResultado = findViewById(R.id.tracoViewDivisoriaResultado);
        this.partidaViewDetalhesResultadoView = findViewById(R.id.partidaViewDetalhesResultadoView);
    }

    public void setLayout(Modalidade modalidade) {

        EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade.getTipoModalidade());
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
