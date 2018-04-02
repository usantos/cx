package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yarolegovich.discretescrollview.DiscreteScrollView;
import com.yarolegovich.discretescrollview.transform.ScaleTransformer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.Modalidade;
import caixa.gov.br.loterias.model.bo.silce.dto.PremiacaoConcursoDTO;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.view.adapter.HomeAdapter;
import caixa.gov.br.loterias.view.adapter.LotecaResultadoTimesAdapter;
import caixa.gov.br.loterias.view.adapter.NumerosResultadosAdapter;

/**
 * Created by cedesbr450 on 28/03/18.
 */



@EViewGroup(R.layout.item_resultado_loteca)
public class ModalidadeSorteioLotecaResultadoView extends LinearLayout implements DiscreteScrollView.ScrollListener<LotecaResultadoTimesAdapter.LotecaHolder>,
        DiscreteScrollView.OnItemChangedListener<LotecaResultadoTimesAdapter.LotecaHolder> {


    private Modalidade modalidade;



    @ViewById
    protected DiscreteScrollView recyclerPartidasLotecas;


    @ViewById
    protected LinearLayout linearlayoutPremiadosListaText;

    @ViewById
    protected View tracoViewDivisoriaResultado;

    @AfterViews
    protected void init() {

    }

    public void setLayout(Modalidade modalidade) {

        this.modalidade = modalidade;
        EstiloModalidade estilo = new EstiloModalidade(modalidade.getTipoModalidade());
        tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));
        adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio());

        LotecaResultadoTimesAdapter lotecaResultadoTimesAdapter;
        lotecaResultadoTimesAdapter = new LotecaResultadoTimesAdapter(getContext(), modalidade.getResultadoConcursoDTO().getPartidasLoteca(), modalidade.getTipoModalidade());

        this.recyclerPartidasLotecas.setAdapter(lotecaResultadoTimesAdapter);
        this.recyclerPartidasLotecas.addScrollListener(this);
        this.recyclerPartidasLotecas.addOnItemChangedListener(this);
        this.recyclerPartidasLotecas.scrollToPosition(0);
        this.recyclerPartidasLotecas.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

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

    public LinearLayout getLinearlayoutPremiadosListaText() {
        return linearlayoutPremiadosListaText;
    }

    public void setLinearlayoutPremiadosListaText(LinearLayout premiadosListaText) {
        this.linearlayoutPremiadosListaText = premiadosListaText;
    }

    private void adicionarPremiados(List<PremiacaoConcursoDTO> listaPremiados){
        for (PremiacaoConcursoDTO premiacaoConcurso :  listaPremiados){
            TextView title1 = new TextView(getContext());
            title1.setText(premiacaoConcurso.getDescricao());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(16, 18, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(getContext(), R.style.fontForTitle1Premiados);
            this.linearlayoutPremiadosListaText.addView(title1);

            TextView title2 = new TextView(getContext());
            if (premiacaoConcurso.getQuantidadeGanhadores()>1){
                title2.setText(premiacaoConcurso.getQuantidadeGanhadores() + " ganhadores");
            }else{
                title2.setText(premiacaoConcurso.getQuantidadeGanhadores() + " ganhador");
            }

            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams2.setMargins(16, 0, 0, 0);
            title2.setLayoutParams(layoutParams2);
            title2.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
            this.linearlayoutPremiadosListaText.addView(title2);
        }
    }

    @Override
    public void onCurrentItemChanged(@Nullable LotecaResultadoTimesAdapter.LotecaHolder viewHolder, int adapterPosition) {

    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable LotecaResultadoTimesAdapter.LotecaHolder currentHolder, @Nullable LotecaResultadoTimesAdapter.LotecaHolder newCurrent) {

    }


}
