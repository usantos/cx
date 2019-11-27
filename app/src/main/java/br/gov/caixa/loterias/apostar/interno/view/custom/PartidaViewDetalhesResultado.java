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
import java.util.LinkedHashMap;
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

            adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio(), modalidade);

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

            adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio(), modalidade);

            TextView title2 = new TextView(getContext());
            title2.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_premiacao_segundo_sorteio_bold)));
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams2.setMargins(0, 18, 0, 0);
            title2.setLayoutParams(layoutParams2);
            title2.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title2);

            if(modalidade.getResultadoConcursoDTO().getPremiacoesSegundoSorteio() != null){
                adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesSegundoSorteio(),modalidade);
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

    public static LinkedHashMap<String,String> obterMapFaixa(Modalidade  modalidade){
        LinkedHashMap<String,String> mapFaixa = new LinkedHashMap<>();
        String modalidadeTratada = modalidade.getNome()
                                             .toUpperCase()
                                             .replace(" ", "")
                                             .replace("_", "")
                                             .replace("-", "");
        switch (modalidadeTratada ){
            case "DIADESORTE":
                mapFaixa.put("0001","7 números acertados");
                mapFaixa.put("0002","6 números acertados");
                mapFaixa.put("0003","5 números acertados");
                mapFaixa.put("0004","4 números acertados");
                break;
            case "DUPLASENA":
                mapFaixa.put("0001","Sena - 6 números acertados");
                mapFaixa.put("0002","Quina - 5 números acertados");
                mapFaixa.put("0003","Quadra - 4 números acertados");
                mapFaixa.put("0004","Terno - 3 números acertados");
                break;
            case "LOTECA":
                mapFaixa.put("0001","1º (14 acertos)");
                mapFaixa.put("0002","2º (13 acertos)");
                break;
            case "LOTOFACIL":
            case "LOTOFÁCIL":
                mapFaixa.put("0001","15 acertos");
                mapFaixa.put("0002","14 acertos");
                mapFaixa.put("0003","13 acertos");
                mapFaixa.put("0004","12 acertos");
                mapFaixa.put("0005","11 acertos");
                break;
            case "LOTOGOL":
                mapFaixa.put("0001","1º (5 acertos)");
                mapFaixa.put("0002","2º (4 acertos)");
                mapFaixa.put("0003","3º (3 acertos)");
                break;
            case "LOTOMANIA":
                mapFaixa.put("0001","20 números acertados");
                mapFaixa.put("0002","19 números acertados");
                mapFaixa.put("0003","18 números acertados");
                mapFaixa.put("0004","17 números acertados");
                mapFaixa.put("0005","16 números acertados");
                mapFaixa.put("0006","15 números acertados");
                break;
            case "MEGASENA":
                mapFaixa.put("0001","Sena - 6 números acertados");
                mapFaixa.put("0002","Quina - 5 números acertados");
                mapFaixa.put("0003","Quadra - 4 números acertados");
                break;
            case "QUINA":
                mapFaixa.put("0001","Quina - 5 números acertados");
                mapFaixa.put("0002","Quadra - 4 números acertados");
                mapFaixa.put("0003","Terno - 3 números acertados");
                mapFaixa.put("0004","Duque - 2 números acertados");
                break;
            case "TIMEMANIA":
                mapFaixa.put("0001","7 números acertados");
                mapFaixa.put("0002","6 números acertados");
                mapFaixa.put("0003","5 números acertados");
                mapFaixa.put("0004","4 números acertados");
                mapFaixa.put("0003","3 números acertados");
                break;
        }
        return mapFaixa;
    }

    private void adicionarPremiados(List<PremiacaoConcursoDTO> listaPremiados, Modalidade modalidade) {
        for (PremiacaoConcursoDTO premiacaoConcurso : listaPremiados) {

            TextView title1 = new TextView(getContext());
            TextView title2 = new TextView(getContext());
            TextView title3 = new TextView(getContext());

            String faixa  = modalidade.getDescricao().isEmpty() ?
                    premiacaoConcurso.getDescricao() :
                    obterMapFaixa(modalidade).get(premiacaoConcurso.getDescricao());
            title1.setText(faixa);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 18, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(getContext(), R.style.fontForTitle1Premiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title1);


            if (premiacaoConcurso.getQuantidadeGanhadores() > 1) {
                title2.setText(getResources().getString(R.string.label_apostas_ganhadoras, premiacaoConcurso.getQuantidadeGanhadores()));
            } else if (premiacaoConcurso.getQuantidadeGanhadores() == 1) {
                title2.setText(getResources().getString(R.string.label_aposta_ganhadora, premiacaoConcurso.getQuantidadeGanhadores()));
            } else {
                title2.setText(R.string.label_nao_houve_acertador);
                title3.setVisibility(View.GONE);
            }

            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            title2.setLayoutParams(layoutParams2);
            title2.setTextAppearance(getContext(), R.style.fontForTitle2Premiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title2);


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