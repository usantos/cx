package caixa.gov.br.loterias.view.custom;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
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
import caixa.gov.br.loterias.model.bo.silce.dto.GanhadoresPorRegiaoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.PartidaLotecaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.PremiacaoConcursoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoConcursoDTO;
import caixa.gov.br.loterias.utils.ViewUtils;

/**
 * Created by cedesbr450 on 04/04/18.
 */

@EViewGroup(R.layout.item_partida_view_detalhes_resultado)
public class PartidaViewDetalhesResultado extends LinearLayout {

    @ViewById
    protected Button voltarButton;

    @ViewById LinearLayout contentResuldadoDetalhesPremiacaoLinearLayout;

    @AfterViews
    protected void init() {

    }

    public void setLayout(Modalidade modalidade) {
        if (modalidade.getResultadoConcursoDTO().getPremiacoesSegundoSorteio() == null){
            TextView title1 = new TextView(getContext());
            title1.setText("Premiação");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(16, 18, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title1);

            adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio());

            TextView titleGanhadores = new TextView(getContext());
            titleGanhadores.setText("Ganhadores por região");
            LinearLayout.LayoutParams layoutParamsGanhadores = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParamsGanhadores.setMargins(16, 18, 0, 0);
            titleGanhadores.setLayoutParams(layoutParamsGanhadores);
            titleGanhadores.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(titleGanhadores);

            adicionarGanhadoresRegiao(modalidade.getResultadoConcursoDTO().getGanhadoresPrimeiroSorteio());

        }else{
            TextView title1 = new TextView(getContext());
            title1.setText("Premiação - 1º sorteio");
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(16, 18, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title1);

            adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio());

            TextView title2 = new TextView(getContext());
            title2.setText("Premiação - 2º sorteio");
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(16, 18, 0, 0);
            title2.setLayoutParams(layoutParams2);
            title2.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title2);

            adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesSegundoSorteio());



            TextView title3 = new TextView(getContext());
            title3.setText("Ganhadores por região - 1º sorteio");
            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(16, 18, 0, 0);
            title3.setLayoutParams(layoutParams3);
            title3.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title3);

            adicionarGanhadoresRegiao(modalidade.getResultadoConcursoDTO().getGanhadoresPrimeiroSorteio());


            TextView title4 = new TextView(getContext());
            title4.setText("Ganhadores por região - 2º sorteio");
            LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(16, 18, 0, 0);
            title4.setLayoutParams(layoutParams4);
            title4.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title4);

            adicionarGanhadoresRegiao(modalidade.getResultadoConcursoDTO().getGanhadoresSegundoSorteio());
        }
    }

    public PartidaViewDetalhesResultado(Context context) {
        super(context);
    }

    public PartidaViewDetalhesResultado(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PartidaViewDetalhesResultado(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void mostrarDetalhes(boolean mostrarDetalhes, LinearLayout item_resultado_content_view, PartidaViewDetalhesResultado partidaViewDetalhesResultadoView){
        if (mostrarDetalhes == true){
            animacaoFadeOutResultado(item_resultado_content_view, partidaViewDetalhesResultadoView);
        }else {
            animacaoFadeOutDetalhes(partidaViewDetalhesResultadoView, item_resultado_content_view);
        }
    }

    public Button getVoltarButton() {
        return voltarButton;
    }

    public void setVoltarButton(Button voltarButton) {
        this.voltarButton = voltarButton;
    }

    private void animacaoFadeInDetalhes(View view){
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }




    private void animacaoFadeOutDetalhes(final View view, final View view2){
        view.animate().alpha(0.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                animacaoFadeInResultado(view2);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }


    private void animacaoFadeInResultado(View view){
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
    }




    private void animacaoFadeOutResultado(final View view, final View view2){
        view.animate().alpha(0.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                animacaoFadeInDetalhes(view2);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
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
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title1);

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
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title2);

            TextView title3 = new TextView(getContext());
            title3.setText(ViewUtils.getMoedaFormat(premiacaoConcurso.getValor()));
            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams3.setMargins(16, 0, 0, 0);
            title3.setLayoutParams(layoutParams3);
            title3.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title3);

        }
    }


    private void adicionarGanhadoresRegiao(List<GanhadoresPorRegiaoDTO> listaGanhadores){
        for (GanhadoresPorRegiaoDTO ganhadoresPorRegiaoDTO :  listaGanhadores){
            TextView title1 = new TextView(getContext());
            title1.setText(ganhadoresPorRegiaoDTO.getMunicipioSorteio().getNome() + " / " + ganhadoresPorRegiaoDTO.getMunicipioSorteio().getUf());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(16, 18, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(getContext(), R.style.fontForTitle1Premiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title1);

            TextView title2 = new TextView(getContext());
            title2.setText(ganhadoresPorRegiaoDTO.getDescricao());

            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams2.setMargins(16, 0, 0, 0);
            title2.setLayoutParams(layoutParams2);
            title2.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title2);


        }
    }

}