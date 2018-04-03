package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.Modalidade;
import caixa.gov.br.loterias.model.bo.silce.dto.PremiacaoConcursoDTO;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.NumerosResultadosAdapter;

/**
 * Created by cedesbr450 on 28/03/18.
 */


@EViewGroup(R.layout.item_resultado_dois_concursos)
public class ModalidadeSorteioItemDoisConcursosResultadoView extends LinearLayout {


    private Modalidade modalidade;



    @ViewById
    protected RecyclerView recyclerNumerosResultado, recyclerNumerosResultadoSegundoSorteio;


    @ViewById
    protected LinearLayout linearlayoutPremiadosListaText;

    @ViewById
    protected View tracoViewDivisoriaResultado;

    @ViewById
    protected Button vejaDetalhesButton;



    @AfterViews
    protected void init() {

    }

    public void setLayout(Modalidade modalidade) {

        this.modalidade = modalidade;
        EstiloModalidade estilo = new EstiloModalidade(modalidade.getTipoModalidade());
        tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));
        NumerosResultadosAdapter numerosResultadosAdapter;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        int numberOfColumns = 3;
        if (modalidade.getResultadoConcursoDTO().getNumerosSorteadosPrimeiroSorteio().size()>6){
            numberOfColumns = 4;
        }
        this.recyclerNumerosResultado.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getNumerosSorteadosPrimeiroSorteio(), modalidade.getTipoModalidade());
        this.recyclerNumerosResultado.setAdapter(numerosResultadosAdapter);

        int numberOfColumnsConcursodois = 3;
        if (modalidade.getResultadoConcursoDTO().getNumerosSorteadosSegundoSorteio().size()>6){
            numberOfColumnsConcursodois = 4;
        }
        this.recyclerNumerosResultadoSegundoSorteio.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getNumerosSorteadosSegundoSorteio(), modalidade.getTipoModalidade());
        this.recyclerNumerosResultadoSegundoSorteio.setAdapter(numerosResultadosAdapter);

        adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio(), modalidade.getResultadoConcursoDTO().getPremiacoesSegundoSorteio());

        this.vejaDetalhesButton.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), "Veja mais _detalhes_"));

    }

    public ModalidadeSorteioItemDoisConcursosResultadoView(Context context) {
        super(context);
    }

    public ModalidadeSorteioItemDoisConcursosResultadoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ModalidadeSorteioItemDoisConcursosResultadoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RecyclerView getRecyclerNumerosResultado() {
        return recyclerNumerosResultado;
    }

    public void setRecyclerNumerosResultado(RecyclerView recyclerNumerosResultado) {
        this.recyclerNumerosResultado = recyclerNumerosResultado;
    }

    public LinearLayout getLinearlayoutPremiadosListaText() {
        return linearlayoutPremiadosListaText;
    }

    public void setLinearlayoutPremiadosListaText(LinearLayout premiadosListaText) {
        this.linearlayoutPremiadosListaText = premiadosListaText;
    }

    private void adicionarPremiados(List<PremiacaoConcursoDTO> listaPremiados, List<PremiacaoConcursoDTO> listaPremiadosSegundoConcurso){
        TextView premiadosTextview1 = new TextView(getContext());
        premiadosTextview1.setText("Premiados - 1º sorteio");
        LinearLayout.LayoutParams layoutParamsPremiadosTextview1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParamsPremiadosTextview1.setMargins(16, 20, 0, 0);
        premiadosTextview1.setLayoutParams(layoutParamsPremiadosTextview1);
        premiadosTextview1.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
        this.linearlayoutPremiadosListaText.addView(premiadosTextview1);


        for (PremiacaoConcursoDTO premiacaoConcurso :  listaPremiados){
            TextView title1 = new TextView(getContext());
            LinearLayout.LayoutParams layoutParamstitle1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            title1.setText(premiacaoConcurso.getDescricao());
            layoutParamstitle1.setMargins(16, 18, 0, 0);
            title1.setLayoutParams(layoutParamstitle1);
            title1.setTextAppearance(getContext(), R.style.fontForTitle1Premiados);
            this.linearlayoutPremiadosListaText.addView(title1);

            TextView title2 = new TextView(getContext());
            LinearLayout.LayoutParams layoutParamstitle2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (premiacaoConcurso.getQuantidadeGanhadores()>1){
                title2.setText(premiacaoConcurso.getQuantidadeGanhadores() + " ganhadores");
            }else{
                title2.setText(premiacaoConcurso.getQuantidadeGanhadores() + " ganhador");
            }
            layoutParamstitle2.setMargins(16, 0, 0, 0);
            title2.setLayoutParams(layoutParamstitle2);
            title2.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
            this.linearlayoutPremiadosListaText.addView(title2);
        }


        TextView premiadosTextview2= new TextView(getContext());
        premiadosTextview2.setText("Premiados - 2º sorteio");
        LinearLayout.LayoutParams layoutParamspremiadosTextview2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamspremiadosTextview2.setMargins(16, 20, 0, 0);
        premiadosTextview2.setLayoutParams(layoutParamspremiadosTextview2);
        premiadosTextview2.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
        this.linearlayoutPremiadosListaText.addView(premiadosTextview2);


        for (PremiacaoConcursoDTO premiacaoConcurso :  listaPremiadosSegundoConcurso){
            TextView title1Segundo = new TextView(getContext());
            title1Segundo.setText(premiacaoConcurso.getDescricao());
            LinearLayout.LayoutParams layoutParamtitle1Segundo = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamtitle1Segundo.setMargins(16, 18, 0, 0);
            title1Segundo.setLayoutParams(layoutParamtitle1Segundo);
            title1Segundo.setTextAppearance(getContext(), R.style.fontForTitle1Premiados);
            this.linearlayoutPremiadosListaText.addView(title1Segundo);

            TextView title2Segundo = new TextView(getContext());
            if (premiacaoConcurso.getQuantidadeGanhadores()>1){
                title2Segundo.setText(premiacaoConcurso.getQuantidadeGanhadores() + " ganhadores");
            }else{
                title2Segundo.setText(premiacaoConcurso.getQuantidadeGanhadores() + " ganhador");
            }
            LinearLayout.LayoutParams layoutParamtitle2Segundo = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamtitle2Segundo.setMargins(16, 0, 0, 0);
            title2Segundo.setLayoutParams(layoutParamtitle2Segundo);
            title2Segundo.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
            this.linearlayoutPremiadosListaText.addView(title2Segundo);
        }
    }

}
