package br.gov.caixa.loterias.apostar.interno.view.custom;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.Modalidade;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.GanhadoresPorRegiaoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.PremiacaoConcursoDTO;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

/**
 * Created by cedesbr450 on 04/04/18.
 */

@EViewGroup(R.layout.item_partida_view_detalhes_resultado)
public class PartidaViewDetalhesResultado extends LinearLayout {

    @ViewById
    protected Button voltarButton;

    @ViewById
    LinearLayout contentResuldadoDetalhesPremiacaoLinearLayout;

    @AfterViews
    protected void init() {

    }

    public void setLayout(Modalidade modalidade) {
        if (modalidade.getTipoModalidade() != ModalidadeEnum.DUPLA_SENA) {
            TextView title1 = new TextView(getContext());
            title1.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_premiacao_bold)));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 18, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title1);

            adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio());

            TextView titleGanhadores = new TextView(getContext());
            titleGanhadores.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_ganhadores_regiao_bold)));
            LinearLayout.LayoutParams layoutParamsGanhadores = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParamsGanhadores.setMargins(0, 18, 0, 0);
            titleGanhadores.setLayoutParams(layoutParamsGanhadores);
            titleGanhadores.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(titleGanhadores);

            adicionarGanhadoresRegiao(modalidade.getResultadoConcursoDTO().getGanhadoresPrimeiroSorteio());

        } else {
            TextView title1 = new TextView(getContext());
            title1.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_premiacao_primeiro_sorteio_bold)));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 18, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title1);

            adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio());

            TextView title2 = new TextView(getContext());
            title2.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_premiacao_segundo_sorteio_bold)));
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams2.setMargins(0, 18, 0, 0);
            title2.setLayoutParams(layoutParams2);
            title2.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title2);

            if(modalidade.getResultadoConcursoDTO().getPremiacoesSegundoSorteio() != null){
                adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesSegundoSorteio());
            }

            TextView title3 = new TextView(getContext());
            title3.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_ganhadores_regiao_primeiro_sorteio_bold)));
            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams3.setMargins(0, 18, 0, 0);
            title3.setLayoutParams(layoutParams3);
            title3.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title3);

            adicionarGanhadoresRegiao(modalidade.getResultadoConcursoDTO().getGanhadoresPrimeiroSorteio());


            TextView title4 = new TextView(getContext());
            title4.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_ganhadores_regiao_segundo_sorteio_bold)));
            LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams4.setMargins(0, 18, 0, 0);
            title4.setLayoutParams(layoutParams4);
            title4.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title4);
            if(modalidade.getResultadoConcursoDTO().getGanhadoresSegundoSorteio() != null){
                adicionarGanhadoresRegiao(modalidade.getResultadoConcursoDTO().getGanhadoresSegundoSorteio());
            }

        }
        adicionarArrecadacaoTotal(modalidade.getResultadoConcursoDTO().getArrecadacaoTotal());

        voltarButton.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_voltar_bold)));
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

    public void mostrarDetalhes(boolean mostrarDetalhes, LinearLayout item_resultado_content_view, PartidaViewDetalhesResultado partidaViewDetalhesResultadoView) {
        if (mostrarDetalhes) {
            animacaoFadeOutResultado(item_resultado_content_view, partidaViewDetalhesResultadoView);
        } else {
            animacaoFadeOutDetalhes(partidaViewDetalhesResultadoView, item_resultado_content_view);
        }
    }

    public Button getVoltarButton() {
        return voltarButton;
    }

    public void setVoltarButton(Button voltarButton) {
        this.voltarButton = voltarButton;
    }

    private void animacaoFadeInDetalhes(View view) {
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }


    private void animacaoFadeOutDetalhes(final View view, final View view2) {
        view.animate().alpha(0.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                animacaoFadeInResultado(view2);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }


    private void animacaoFadeInResultado(View view) {
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }


    private void animacaoFadeOutResultado(final View view, final View view2) {
        view.animate().alpha(0.0f).setDuration(300).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                animacaoFadeInDetalhes(view2);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private void adicionarPremiados(List<PremiacaoConcursoDTO> listaPremiados) {
        for (PremiacaoConcursoDTO premiacaoConcurso : listaPremiados) {
            TextView title1 = new TextView(getContext());
            title1.setText(premiacaoConcurso.getDescricao());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 18, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(getContext(), R.style.fontForTitle1Premiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title1);

            TextView title2 = new TextView(getContext());
            if (premiacaoConcurso.getQuantidadeGanhadores() > 1) {
                title2.setText(getResources().getString(R.string.label_apostas_ganhadoras, premiacaoConcurso.getQuantidadeGanhadores()));
            } else if (premiacaoConcurso.getQuantidadeGanhadores() == 1) {
                title2.setText(getResources().getString(R.string.label_aposta_ganhadora, premiacaoConcurso.getQuantidadeGanhadores()));
            } else {
                title2.setText(R.string.label_nao_houve_acertador);
            }

            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            title2.setLayoutParams(layoutParams2);
            title2.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title2);

            TextView title3 = new TextView(getContext());
            title3.setText(ViewUtils.getMoedaFormat(premiacaoConcurso.getValor()));
            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            title3.setLayoutParams(layoutParams3);
            title3.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title3);

        }
    }


    private void adicionarGanhadoresRegiao(List<GanhadoresPorRegiaoDTO> listaGanhadores) {
        if (listaGanhadores != null) {
            for (GanhadoresPorRegiaoDTO ganhadoresPorRegiaoDTO : listaGanhadores) {
                TextView title1 = new TextView(getContext());
                title1.setText(getResources().getString(R.string.label_texto_barra_texto, ganhadoresPorRegiaoDTO.getMunicipioSorteio().getNome(), ganhadoresPorRegiaoDTO.getUfSorteio().getSigla()));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(0, 18, 0, 0);
                title1.setLayoutParams(layoutParams);
                title1.setTextAppearance(getContext(), R.style.fontForTitle1Premiados);
                this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title1);

                TextView title2 = new TextView(getContext());
                title2.setText(ganhadoresPorRegiaoDTO.getDescricao());

                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                title2.setLayoutParams(layoutParams2);
                title2.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
                this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title2);
            }
        }

    }

    private void adicionarArrecadacaoTotal(BigDecimal valor) {
        TextView title = new TextView(getContext());
        title.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_arrecadacao_total_bold)));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(0, 18, 0, 0);
        title.setLayoutParams(layoutParams);
        title.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
        this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title);

        TextView text = new TextView(getContext());
        text.setText(ViewUtils.getMoedaFormat(valor));

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams2.setMargins(0, 18, 0, 0);
        text.setLayoutParams(layoutParams2);
        text.setTextAppearance(getContext(), R.style.fontForTitle1Premiados);
        this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(text);
    }
}