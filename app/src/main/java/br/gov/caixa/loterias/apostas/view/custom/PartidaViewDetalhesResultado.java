package br.gov.caixa.loterias.apostas.view.custom;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.Modalidade;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.GanhadoresPorRegiaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.PremiacaoConcursoDTO;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

/**
 * Created by cedesbr450 on 04/04/18.
 */

@EViewGroup(R.layout.item_partida_view_detalhes_resultado)
public class PartidaViewDetalhesResultado extends LinearLayout {

    @ViewById
    LinearLayout contentResuldadoDetalhesPremiacaoLinearLayout;

    @AfterViews
    protected void init() {}

    public void setLayout(Modalidade modalidade) {
        if (modalidade.getTipoModalidade() != ModalidadeEnum.DUPLA_SENA) {
            TextView title1 = new TextView(getContext());
            title1.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_premiacao_bold)));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams.setMargins(0, 20, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title1);

            adicionarPremiados(modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio(), modalidade);

            TextView titleGanhadores = new TextView(getContext());
            titleGanhadores.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_ganhadores_regiao_bold)));
            LinearLayout.LayoutParams layoutParamsGanhadores = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParamsGanhadores.setMargins(0, 20, 0, 0);
            titleGanhadores.setLayoutParams(layoutParamsGanhadores);
            titleGanhadores.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            if(modalidade!= null &&
                    modalidade.getResultadoConcursoDTO() != null &&
                    modalidade.getResultadoConcursoDTO().getGanhadoresPrimeiroSorteio() != null &&
                    modalidade.getResultadoConcursoDTO().getGanhadoresPrimeiroSorteio().size() > 0){
                this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(titleGanhadores);
                adicionarGanhadoresRegiao(modalidade.getResultadoConcursoDTO().getGanhadoresPrimeiroSorteio());
            }
        } else {
            TextView title1 = new TextView(getContext());
            title1.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_premiacao_primeiro_sorteio_bold)));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            TextView title4 = new TextView(getContext());
            title4.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_ganhadores_regiao_segundo_sorteio_bold)));
            LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams4.setMargins(0, 20, 0, 0);
            title4.setLayoutParams(layoutParams4);
            title4.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);


            layoutParams.setMargins(0, 20, 0, 0);
            title1.setLayoutParams(layoutParams);
            title1.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);
            this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title1);
            //List<PremiacaoConcursoDTO> premiadosPrimeiro = modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio();
            //List<PremiacaoConcursoDTO> premiadosSegundo = modalidade.getResultadoConcursoDTO().getPremiacoesSegundoSorteio();
            List<PremiacaoConcursoDTO> listaPremiados = modalidade.getResultadoConcursoDTO().getPremiacoesPrimeiroSorteio();
            int n = listaPremiados.size();

            List<PremiacaoConcursoDTO> premiadosPrimeiro  = listaPremiados.subList(0,(n + 1)/2);
            List<PremiacaoConcursoDTO> premiadosSegundo = listaPremiados.subList(((n + 1)/2), n);

            adicionarPremiados(premiadosPrimeiro, modalidade);

            TextView title2 = new TextView(getContext());
            title2.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_premiacao_segundo_sorteio_bold)));
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams2.setMargins(0, 20, 0, 0);
            title2.setLayoutParams(layoutParams2);
            title2.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);

            if(premiadosSegundo != null){
                this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title2);
                adicionarPremiados(premiadosSegundo ,modalidade);
            }

            TextView title3 = new TextView(getContext());
            title3.setText(ViewUtils.textFuturaAndFuturaBold(getContext(), getResources().getString(R.string.label_ganhadores_regiao_primeiro_sorteio_bold)));
            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams3.setMargins(0, 20, 0, 0);
            title3.setLayoutParams(layoutParams3);
            title3.setTextAppearance(getContext(), R.style.fontForTitleTopoPremiados);

            if(modalidade!= null &&
                    modalidade.getResultadoConcursoDTO() != null &&
                    modalidade.getResultadoConcursoDTO().getGanhadoresPrimeiroSorteio() != null &&
                    modalidade.getResultadoConcursoDTO().getGanhadoresPrimeiroSorteio().size() > 0){
                this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title3);
                adicionarGanhadoresRegiao(modalidade.getResultadoConcursoDTO().getGanhadoresPrimeiroSorteio());
            }

            if(modalidade!= null &&
                    modalidade.getResultadoConcursoDTO() != null &&
                    modalidade.getResultadoConcursoDTO().getGanhadoresSegundoSorteio() != null &&
                    modalidade.getResultadoConcursoDTO().getGanhadoresSegundoSorteio().size() > 0){
                this.contentResuldadoDetalhesPremiacaoLinearLayout.addView(title4);
                adicionarGanhadoresRegiao(modalidade.getResultadoConcursoDTO().getGanhadoresSegundoSorteio());
            }
        }
        adicionarArrecadacaoTotal(modalidade.getResultadoConcursoDTO().getArrecadacaoTotal());
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
                mapFaixa.put("0005","Mês da Sorte\n"+
                        (modalidade.getResultadoConcursoDTO() == null ? "":
                                modalidade.getResultadoConcursoDTO().getPremiacaoMesDeSorte().getMesDeSorte().getNome()));
                break;
            case "DUPLASENA":
                mapFaixa.put("0001","Sena - 6 números acertados");
                mapFaixa.put("0002","Quina - 5 números acertados");
                mapFaixa.put("0003","Quadra - 4 números acertados");
                mapFaixa.put("0004","Terno - 3 números acertados");
                mapFaixa.put("0005","Sena - 6 números acertados");
                mapFaixa.put("0006","Quina - 5 números acertados");
                mapFaixa.put("0007","Quadra - 4 números acertados");
                mapFaixa.put("0008","Terno - 3 números acertados");
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
                mapFaixa.put("0007","0 acertos");
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
                mapFaixa.put("0005","3 números acertados");
                mapFaixa.put("0006","Time do Coração\n"+
                        (modalidade.getResultadoConcursoDTO() == null ? "":
                                modalidade.getResultadoConcursoDTO().getPremiacaoTimeDoCoracao().getEquipe().getNome()));
                break;
            case "SUPERSETE":
            case "SUPER7":
                mapFaixa.put("0001","7 acertos");
                mapFaixa.put("0002","6 acertos");
                mapFaixa.put("0003","5 acertos");
                mapFaixa.put("0004","4 acertos");
                mapFaixa.put("0005","3 acertos");
                mapFaixa.put("1","7 acertos");
                mapFaixa.put("2","6 acertos");
                mapFaixa.put("3","5 acertos");
                mapFaixa.put("4","4 acertos");
                mapFaixa.put("5","3 acertos");
                break;
            case "MAISMILIONARIA":
            case "MAISMILIONÁRIA":
                mapFaixa.put("1","6 números +2 trevos acertados");
                mapFaixa.put("2","6 números +1 ou 6 +0 trevos acertados");
                mapFaixa.put("3","5 números +2 trevos acertados");
                mapFaixa.put("4","5 números e 1 ou 0 trevos acertados");
                mapFaixa.put("5","4 números e 2 trevos acertados");
                mapFaixa.put("6","4 números e 1 ou 0 trevos acertados");
                mapFaixa.put("7","3 números e 2 trevos acertados");
                mapFaixa.put("8","3 números e 1 trevo acertados");
                mapFaixa.put("9","2 números e 2 trevos acertados");
                mapFaixa.put("10","2 números e 1 trevo acertado");
                mapFaixa.put("0001","6 números +2 trevos acertados");
                mapFaixa.put("0002","6 números +1 ou 6 +0 trevos acertados");
                mapFaixa.put("0003","5 números +2 trevos acertados");
                mapFaixa.put("0004","5 números e 1 ou 0 trevos acertados");
                mapFaixa.put("0005","4 números e 2 trevos acertados");
                mapFaixa.put("0006","4 números e 1 ou 0 trevos acertados");
                mapFaixa.put("0007","3 números e 2 trevos acertados");
                mapFaixa.put("0008","3 números e 1 trevo acertado");
                mapFaixa.put("0009","2 números e 2 trevos acertados");
                mapFaixa.put("00010","2 números e 1 trevo acertado");
                break;
        }
        return mapFaixa;
    }

    private void adicionarPremiados(List<PremiacaoConcursoDTO> listaPremiados, Modalidade modalidade) {
        for (PremiacaoConcursoDTO premiacaoConcurso : listaPremiados) {

            TextView title1 = new TextView(getContext());
            TextView title2 = new TextView(getContext());
            TextView title3 = new TextView(getContext());

            String faixa  = obterMapFaixa(modalidade).get(premiacaoConcurso.getDescricao());

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


            title3.setText("Prêmio: "+ViewUtils.getMoedaFormat(premiacaoConcurso.getValor()));
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
                title1.setText(getResources().getString(R.string.label_texto_barra_texto,
                        ganhadoresPorRegiaoDTO.getMunicipioSorteio().getNome(),
                        ganhadoresPorRegiaoDTO.getUfSorteio().getSigla())
                );
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