package br.gov.caixa.loterias.apostar.interno.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
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

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.Modalidade;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PremiacaoConcursoDTO;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.NumerosResultadosAdapter;

/**
 * Created by cedesbr450 on 28/03/18.
 */


@EViewGroup(R.layout.item_resultado_dois_concursos)
public class ModalidadeSorteioItemDoisConcursosResultadoView extends LinearLayout {

    @ViewById
    protected RecyclerView recyclerNumerosResultado, recyclerNumerosResultadoSegundoSorteio;

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
        NumerosResultadosAdapter numerosResultadosAdapter;
        int numberOfColumns = 3;
        if (modalidade.getResultadoConcursoDTO().getNumerosSorteadosPrimeiroSorteio().size()>6){
            numberOfColumns = 4;
        }
        this.recyclerNumerosResultado.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getNumerosSorteadosPrimeiroSorteio(), modalidade.getTipoModalidade());
        this.recyclerNumerosResultado.setAdapter(numerosResultadosAdapter);

        this.recyclerNumerosResultadoSegundoSorteio.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getNumerosSorteadosSegundoSorteio(), modalidade.getTipoModalidade());
        this.recyclerNumerosResultadoSegundoSorteio.setAdapter(numerosResultadosAdapter);

        adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio(), modalidade.getResultadoConcursoDTO().getPremiacoesSegundoSorteio());

        this.vejaDetalhesButton.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), "Veja mais _detalhes_"));


        this.partidaViewDetalhesResultadoView.setLayout(modalidade);
        this.vejaDetalhesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                partidaViewDetalhesResultadoView.mostrarDetalhes(true, item_resultado_content_view, partidaViewDetalhesResultadoView);
            }
        });
        this.partidaViewDetalhesResultadoView.getVoltarButton().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                partidaViewDetalhesResultadoView.mostrarDetalhes(false, item_resultado_content_view, partidaViewDetalhesResultadoView);
            }
        });
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
        premiadosTextview1.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_premiados_primeiro_sorteio_bold)));
        LinearLayout.LayoutParams layoutParamsPremiadosTextview1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        premiadosTextview1.setLayoutParams(layoutParamsPremiadosTextview1);
        premiadosTextview1.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
        this.linearlayoutPremiadosListaText.addView(premiadosTextview1);


        for (PremiacaoConcursoDTO premiacaoConcurso :  listaPremiados){
            TextView title1 = new TextView(getContext());
            LinearLayout.LayoutParams layoutParamstitle1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            title1.setText(premiacaoConcurso.getDescricao());
            layoutParamstitle1.setMargins(0, 18, 0, 0);
            title1.setLayoutParams(layoutParamstitle1);
            title1.setTextAppearance(getContext(), R.style.fontForTitle1Premiados);
            this.linearlayoutPremiadosListaText.addView(title1);

            TextView title2 = new TextView(getContext());
            LinearLayout.LayoutParams layoutParamstitle2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (premiacaoConcurso.getQuantidadeGanhadores()>1){
                title2.setText(getResources().getString(R.string.label_numero_ganhadores, premiacaoConcurso.getQuantidadeGanhadores()));
            }else{
                title2.setText(getResources().getString(R.string.label_numero_ganhador, premiacaoConcurso.getQuantidadeGanhadores()));
            }
            title2.setLayoutParams(layoutParamstitle2);
            title2.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
            this.linearlayoutPremiadosListaText.addView(title2);
        }


        TextView premiadosTextview2= new TextView(getContext());
        premiadosTextview2.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_premiados_segundo_sorteio_bold)));
        LinearLayout.LayoutParams layoutParamspremiadosTextview2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamspremiadosTextview2.setMargins(0, 20, 0, 0);
        premiadosTextview2.setLayoutParams(layoutParamspremiadosTextview2);
        premiadosTextview2.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
        this.linearlayoutPremiadosListaText.addView(premiadosTextview2);

        if (listaPremiadosSegundoConcurso != null) {
            for (PremiacaoConcursoDTO premiacaoConcurso :  listaPremiadosSegundoConcurso){
                TextView title1Segundo = new TextView(getContext());
                title1Segundo.setText(premiacaoConcurso.getDescricao());
                LinearLayout.LayoutParams layoutParamtitle1Segundo = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParamtitle1Segundo.setMargins(0, 18, 0, 0);
                title1Segundo.setLayoutParams(layoutParamtitle1Segundo);
                title1Segundo.setTextAppearance(getContext(), R.style.fontForTitle1Premiados);
                this.linearlayoutPremiadosListaText.addView(title1Segundo);

                TextView title2Segundo = new TextView(getContext());
                if (premiacaoConcurso.getQuantidadeGanhadores()>1){
                    title2Segundo.setText(getResources().getString(R.string.label_numero_ganhadores, premiacaoConcurso.getQuantidadeGanhadores()));
                }else{
                    title2Segundo.setText(getResources().getString(R.string.label_numero_ganhador, premiacaoConcurso.getQuantidadeGanhadores()));
                }
                LinearLayout.LayoutParams layoutParamtitle2Segundo = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                title2Segundo.setLayoutParams(layoutParamtitle2Segundo);
                title2Segundo.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
                this.linearlayoutPremiadosListaText.addView(title2Segundo);
            }
        }
    }
}
