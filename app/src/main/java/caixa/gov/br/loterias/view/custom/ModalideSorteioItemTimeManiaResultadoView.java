package caixa.gov.br.loterias.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.Modalidade;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.PremiacaoConcursoDTO;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.NumerosResultadosAdapter;


/**
 * Created by cedesbr450 on 28/03/18.
 */


@EViewGroup(R.layout.item_resultado_timemania)
public class ModalideSorteioItemTimeManiaResultadoView extends LinearLayout {

    @ViewById
    protected RecyclerView recyclerNumerosResultado;

    @ViewById
    protected LinearLayout linearlayoutPremiadosListaText, item_resultado_content_view;

    @ViewById
    protected View tracoViewDivisoriaResultado;

    @ViewById
    protected TextView labelTimeCoracaoText, textTimeCoracao;

    @ViewById
    protected Button vejaDetalhesButton;

    @ViewById
    protected PartidaViewDetalhesResultado partidaViewDetalhesResultadoView;

    @ViewById
    protected ImageView timeCoracaoImage;

    @AfterViews
    protected void init() {

    }

    public void setLayout(Modalidade modalidade) {

        EstiloModalidade estilo = new EstiloModalidade(modalidade.getTipoModalidade());
        if (modalidade.getTipoModalidade() == ModalidadeEnum.DIA_DE_SORTE) {
            timeCoracaoImage.setVisibility(View.GONE);
            labelTimeCoracaoText.setText(R.string.label_mes_desejado);
            if (modalidade.getResultadoConcursoDTO().getMesDeSorte() != null) {
                this.textTimeCoracao.setText(modalidade.getResultadoConcursoDTO().getMesDeSorte().getNome());
            }else{
                //TODO quando retornar mes de sorte retirar mock
                this.textTimeCoracao.setText(R.string.label_tres_interrogacoes);
            }
        } else {
            this.textTimeCoracao.setText(modalidade.getResultadoConcursoDTO().getPremiacaoTimeDoCoracao().getEquipe().getNome());
        }
        this.tracoViewDivisoriaResultado.setBackground(getContext().getResources().getDrawable(estilo.getCorClara()));
        adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio());
        NumerosResultadosAdapter numerosResultadosAdapter;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        int numberOfColumns = 3;
        if (modalidade.getResultadoConcursoDTO().getNumerosSorteadosPrimeiroSorteio().size() > 6) {
            numberOfColumns = 4;
        }
        this.recyclerNumerosResultado.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));
        numerosResultadosAdapter = new NumerosResultadosAdapter(getContext(), modalidade.getResultadoConcursoDTO().getNumerosSorteadosPrimeiroSorteio(), modalidade.getTipoModalidade());
        this.recyclerNumerosResultado.setAdapter(numerosResultadosAdapter);

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

    public ModalideSorteioItemTimeManiaResultadoView(Context context) {
        super(context);
    }

    public ModalideSorteioItemTimeManiaResultadoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ModalideSorteioItemTimeManiaResultadoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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

    private void adicionarPremiados(List<PremiacaoConcursoDTO> listaPremiados) {
        for (PremiacaoConcursoDTO premiacaoConcurso : listaPremiados) {
            TextView title1 = new TextView(getContext());
            title1.setText(premiacaoConcurso.getDescricao());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(16, 18, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(getContext(), R.style.fontForTitle1Premiados);
            this.linearlayoutPremiadosListaText.addView(title1);

            TextView title2 = new TextView(getContext());
            if (premiacaoConcurso.getQuantidadeGanhadores() > 1) {
                title2.setText(getResources().getString(R.string.label_numero_ganhadores, premiacaoConcurso.getQuantidadeGanhadores()));
            } else {
                title2.setText(getResources().getString(R.string.label_numero_ganhador, premiacaoConcurso.getQuantidadeGanhadores()));
            }

            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams2.setMargins(16, 0, 0, 0);
            title2.setLayoutParams(layoutParams2);
            title2.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
            this.linearlayoutPremiadosListaText.addView(title2);
        }
    }

}
