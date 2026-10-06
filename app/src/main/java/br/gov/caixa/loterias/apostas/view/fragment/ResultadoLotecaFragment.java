package br.gov.caixa.loterias.apostas.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.EscudoBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaResultadoTimesAdapter;
import br.gov.caixa.loterias.apostas.view.listener.OnEscudoListener;

import static br.gov.caixa.loterias.apostas.view.fragment.ResultadoGenericoFragment.RESULTADO_DTO;

public class ResultadoLotecaFragment extends Fragment implements DiscreteScrollView.ScrollListener<LotecaResultadoTimesAdapter.LotecaHolder>,
        DiscreteScrollView.OnItemChangedListener<LotecaResultadoTimesAdapter.LotecaHolder> {

    private DiscreteScrollView svLoteca;
    private ResultadoConcursoDTO resultado;

    public ResultadoLotecaFragment() {
    }

    public static ResultadoLotecaFragment newInstance(ResultadoConcursoDTO resultado) {
        ResultadoLotecaFragment fragment = new ResultadoLotecaFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESULTADO_DTO, resultado);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resultado_loteca, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        svLoteca = view.findViewById(R.id.sv_loteca);

        if (getArguments() != null) {
            resultado = (ResultadoConcursoDTO) getArguments().getSerializable(RESULTADO_DTO);
            if(resultado != null){
                //configuraResultadoLoteca(resultado);
                EscudoBO.getInstance().carregaEscudos(getContext(), new OnEscudoListener() {
                    @Override
                    public void aposCarregarEscudos() {
                        configuraResultadoLoteca(resultado);
                    }
                });
            }
        }
    }

    private void configuraResultadoLoteca(ResultadoConcursoDTO resultado){
        LotecaResultadoTimesAdapter lotecaResultadoTimesAdapter;
        lotecaResultadoTimesAdapter = new LotecaResultadoTimesAdapter(getContext(), resultado.getPartidasLoteca(),resultado.getConcurso().getModalidade());

        this.svLoteca.setAdapter(lotecaResultadoTimesAdapter);
        this.svLoteca.addScrollListener(this);
        this.svLoteca.addOnItemChangedListener(this);
        this.svLoteca.scrollToPosition(0);
        this.svLoteca.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        this.svLoteca.addOnItemTouchListener(mScrollTouchListener);

    }

    RecyclerView.OnItemTouchListener mScrollTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            int action = e.getAction();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                 //   ((PrincipalActivity) getContext()).homeCarousel.requestDisallowInterceptTouchEvent(true);
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

    @Override
    public void onCurrentItemChanged(@Nullable LotecaResultadoTimesAdapter.LotecaHolder viewHolder, int adapterPosition) {

    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable LotecaResultadoTimesAdapter.LotecaHolder currentHolder, @Nullable LotecaResultadoTimesAdapter.LotecaHolder newCurrent) {

    }
}
