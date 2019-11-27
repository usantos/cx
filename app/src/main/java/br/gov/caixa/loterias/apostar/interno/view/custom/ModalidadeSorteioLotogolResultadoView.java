package br.gov.caixa.loterias.apostar.interno.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.Modalidade;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PremiacaoConcursoDTO;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.controllers.PrincipalActivity;
import br.gov.caixa.loterias.apostar.interno.view.adapter.LotogolResultadoTimesAdapter;

/**
 * Created by cedesbr450 on 28/03/18.
 */


@EViewGroup(R.layout.item_resultado_lotogol)
public class ModalidadeSorteioLotogolResultadoView extends LinearLayout implements DiscreteScrollView.ScrollListener<LotogolResultadoTimesAdapter.LotogolHolder>,
        DiscreteScrollView.OnItemChangedListener<LotogolResultadoTimesAdapter.LotogolHolder> {

    @ViewById
    protected DiscreteScrollView recyclerPartidasLotogol;

    @ViewById
    protected LinearLayout linearlayoutPremiadosListaText, item_resultado_content_view;

    @ViewById
    protected View tracoViewDivisoriaResultado;

    @ViewById
    protected Button vejaDetalhesButton;

    @ViewById
    protected PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;

    @AfterViews
    protected void init() {

    }

    public void setLayout(Modalidade modalidade) {

        EstiloModalidade estilo = new EstiloModalidade(modalidade.getTipoModalidade());
        tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));
        adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio(), modalidade);

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

        this.vejaDetalhesButton.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), "Veja mais _detalhes_"));

        this.partidaViewDetalhesResultadoView.setLayout(modalidade);
        this.vejaDetalhesButton.setOnClickListener(view -> partidaViewDetalhesResultadoView.mostrarDetalhes(true, item_resultado_content_view, partidaViewDetalhesResultadoView));
        this.partidaViewDetalhesResultadoView.getVoltarButton().setOnClickListener(view -> partidaViewDetalhesResultadoView.mostrarDetalhes(false, item_resultado_content_view, partidaViewDetalhesResultadoView));

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


    public LinearLayout getLinearlayoutPremiadosListaText() {
        return linearlayoutPremiadosListaText;
    }

    public void setLinearlayoutPremiadosListaText(LinearLayout premiadosListaText) {
        this.linearlayoutPremiadosListaText = premiadosListaText;
    }

    private void adicionarPremiados(List<PremiacaoConcursoDTO> listaPremiados, Modalidade modalidade){
        for (PremiacaoConcursoDTO premiacaoConcurso :  listaPremiados){
            TextView title1 = new TextView(getContext());
            String faixa  = modalidade.getNome().isEmpty() ?
                    premiacaoConcurso.getDescricao() :
                    PartidaViewDetalhesResultado.obterMapFaixa(modalidade).get(premiacaoConcurso.getDescricao());
            title1.setText(faixa);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 18, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(getContext(), R.style.fontForTitle1Premiados);
            this.linearlayoutPremiadosListaText.addView(title1);

            TextView title2 = new TextView(getContext());
            if (premiacaoConcurso.getQuantidadeGanhadores()>1){
                title2.setText(getResources().getString(R.string.label_numero_ganhadores, premiacaoConcurso.getQuantidadeGanhadores()));
            }else{
                title2.setText(getResources().getString(R.string.label_numero_ganhador, premiacaoConcurso.getQuantidadeGanhadores()));
            }

            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            title2.setLayoutParams(layoutParams2);
            title2.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
            this.linearlayoutPremiadosListaText.addView(title2);
        }
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