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
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotogolResultadoTimesAdapter;

/**
 * Created by cedesbr450 on 28/03/18.
 */


public class ModalidadeSorteioLotogolResultadoView extends LinearLayout implements DiscreteScrollView.ScrollListener<LotogolResultadoTimesAdapter.LotogolHolder>,
        DiscreteScrollView.OnItemChangedListener<LotogolResultadoTimesAdapter.LotogolHolder> {

    private boolean alreadyInflated = false;

    private DiscreteScrollView recyclerPartidasLotogol;
    private LinearLayout  item_resultado_content_view;
    private View tracoViewDivisoriaResultado;
    private PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;

    public static ModalidadeSorteioLotogolResultadoView build(Context context) {
        ModalidadeSorteioLotogolResultadoView instance = new ModalidadeSorteioLotogolResultadoView(context);
        instance.onFinishInflate();
        return instance;
    }

    @Override
    public void onFinishInflate() {
        if (!alreadyInflated) {
            alreadyInflated = true;
            inflate(getContext(), R.layout.item_resultado_lotogol, this);
        }
        super.onFinishInflate();
        init();
    }

    protected void init() {
        this.recyclerPartidasLotogol = findViewById(R.id.recyclerPartidasLotogol);
        this.item_resultado_content_view = findViewById(R.id.item_resultado_content_view);
        this.tracoViewDivisoriaResultado = findViewById(R.id.tracoViewDivisoriaResultado);
        this.partidaViewDetalhesResultadoView = findViewById(R.id.partidaViewDetalhesResultadoView);
    }

    public void setLayout(Modalidade modalidade) {

        EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade.getTipoModalidade());
        tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));
        LotogolResultadoTimesAdapter lotogolResultadoTimesAdapter;
        lotogolResultadoTimesAdapter = new LotogolResultadoTimesAdapter(getContext(), modalidade.getResultadoConcursoDTO().getPartidasLotogol(), modalidade.getTipoModalidade());

        this.recyclerPartidasLotogol.setAdapter(lotogolResultadoTimesAdapter);
        this.recyclerPartidasLotogol.addScrollListener(this);
        this.recyclerPartidasLotogol.addOnItemChangedListener(this);
        this.recyclerPartidasLotogol.scrollToPosition(0);
        this.recyclerPartidasLotogol.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        this.recyclerPartidasLotogol.addOnItemTouchListener(mScrollTouchListener);


        this.partidaViewDetalhesResultadoView.setLayout(modalidade);

    }

    public ModalidadeSorteioLotogolResultadoView(Context context) {
        super(context);
    }

    public ModalidadeSorteioLotogolResultadoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ModalidadeSorteioLotogolResultadoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onCurrentItemChanged(@Nullable LotogolResultadoTimesAdapter.LotogolHolder viewHolder, int adapterPosition) {

    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable LotogolResultadoTimesAdapter.LotogolHolder currentHolder, @Nullable LotogolResultadoTimesAdapter.LotogolHolder newCurrent) {

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