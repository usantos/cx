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
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotogolResultadoTimesAdapter;

import static br.gov.caixa.loterias.apostas.view.fragment.ResultadoGenericoFragment.RESULTADO_DTO;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultadoLotogolFragment extends Fragment implements DiscreteScrollView.ScrollListener<LotogolResultadoTimesAdapter.LotogolHolder>,
        DiscreteScrollView.OnItemChangedListener<LotogolResultadoTimesAdapter.LotogolHolder> {

    private DiscreteScrollView svLotogol;
    private ResultadoConcursoDTO resultado;

    public ResultadoLotogolFragment() {
    }

    public static ResultadoLotogolFragment newInstance(ResultadoConcursoDTO resultado) {
        ResultadoLotogolFragment fragment = new ResultadoLotogolFragment();
        Bundle args = new Bundle();
        args.putSerializable(RESULTADO_DTO, resultado);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resultado_lotogol, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        svLotogol = view.findViewById(R.id.sv_lotogol);

        if (getArguments() != null) {
            resultado = (ResultadoConcursoDTO) getArguments().getSerializable(RESULTADO_DTO);
            if(resultado != null){
                configuraResultadoLoteca(resultado);
            }
        }
    }

    private void configuraResultadoLoteca(ResultadoConcursoDTO resultado){

        LotogolResultadoTimesAdapter lotogolResultadoTimesAdapter;
        lotogolResultadoTimesAdapter = new LotogolResultadoTimesAdapter(getContext(), resultado.getPartidasLotogol(),resultado.getConcurso().getModalidade());

        this.svLotogol.setAdapter(lotogolResultadoTimesAdapter);
        this.svLotogol.addScrollListener(this);
        this.svLotogol.addOnItemChangedListener(this);
        this.svLotogol.scrollToPosition(0);
        this.svLotogol.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        this.svLotogol.addOnItemTouchListener(mScrollTouchListener);

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
                 //   ((PrincipalActivity) getContext()).homeCarousel.requestDisallowInterceptTouchEvent(true);
//                //    rv.getParent().requestDisallowInterceptTouchEvent(true);
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
