package br.gov.caixa.loterias.apostar.interno.view.fragment;


import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.DezenasAdapter;
import br.gov.caixa.loterias.apostar.interno.view.adapter.DezenasResultadoAdapter;

@EFragment(R.layout.fragment_aposta_confirmada_numero_detalhes_premio_pago)
public class ApostaConfirmadaNumeroDetalhesPremioPago extends Fragment {


    @ViewById
    protected TextView concursoTxt, quantidadeAcertosTxt, acertosTxt, quantidadeGanhadoresTxt, ganhadoresTxt, dataApostaTxt,
            informacaoAdicionalResultadoLabelText, informacaoAdicionalResultadoText, informacaoAdicionalDezenasText;

    @ViewById
    protected RecyclerView dezenasResultadoRecyclerView, dezenasRecyclerView;

    @ViewById
    protected RelativeLayout informacaoAdicionalResultadoLayout;

    @FragmentArg
    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    @FragmentArg
    protected ResultadoConcursoDTO resultadoConcursoDTO;

    private int qtdAcertos;
    private EstiloModalidade estilo;

    @AfterViews
    protected void init() {
        concursoTxt.setText(String.format(Locale.getDefault(), "%d", resultadoConcursoDTO.getConcurso().getNumero()));
        if (StringUtils.isEmpty(aposta.getHoraEfetivacao())) {
            dataApostaTxt.setText(aposta.getDataEfetivacao());
        } else {
            dataApostaTxt.setText(aposta.getDataEfetivacao() + " - " + aposta.getHoraEfetivacao());
        }

        estilo = new EstiloModalidade(aposta.getModalidade());

        int ganhadores;
        ganhadores = CollectionUtils.isNotEmpty(resultadoConcursoDTO.getGanhadoresPrimeiroSorteio()) ? resultadoConcursoDTO.getGanhadoresPrimeiroSorteio().size() : 0;
        if (aposta.getQuantidadeTeimosinhas() != null && aposta.getQuantidadeTeimosinhas() > 0) {
            ganhadores = CollectionUtils.isNotEmpty(resultadoConcursoDTO.getGanhadoresSegundoSorteio()) ? resultadoConcursoDTO.getGanhadoresSegundoSorteio().size() : 0;
        }

        quantidadeGanhadoresTxt.setText(String.valueOf(ganhadores));
        if (ganhadores > 1) {
            ganhadoresTxt.setText(R.string.label_ganhadores);
        } else {
            ganhadoresTxt.setText(R.string.label_ganhador);
        }

        initResultadoRecyclerView();
        initDezenasRecyclerView();

        quantidadeAcertosTxt.setText(String.valueOf(qtdAcertos));
        if (qtdAcertos > 1) {
            acertosTxt.setText(R.string.label_acertos);
        } else {
            acertosTxt.setText(R.string.label_acerto);
        }

        if (aposta.getModalidade() == ModalidadeEnum.TIMEMANIA) {
            setValorAdicionalResultadoAposta(resultadoConcursoDTO.getPremiacaoTimeDoCoracao().getEquipe().getNome(),
                    getString(R.string.label_time_coracao_dois_pontos_time, aposta.getTimeDoCoracao().getNome()),
                    resultadoConcursoDTO.getPremiacaoTimeDoCoracao().getEquipe().getNome().equals(aposta.getTimeDoCoracao().getNome()));
        } else if (aposta.getModalidade() == ModalidadeEnum.DIA_DE_SORTE) {
            informacaoAdicionalResultadoLabelText.setText(R.string.label_mes_sorteado);
            setValorAdicionalResultadoAposta(resultadoConcursoDTO.getPremiacaoMesDeSorte().getMesDeSorte().getNome(),
                    getString(R.string.label_mes_sorte_dois_pontos_mes, aposta.getMesDeSorte().getNome()),
                    resultadoConcursoDTO.getPremiacaoMesDeSorte().getMesDeSorte().getNumero().equals(aposta.getMesDeSorte().getNumero()));
        }
    }

    private void setValorAdicionalResultadoAposta(String resultado, String aposta, boolean isIgual) {
        informacaoAdicionalResultadoLayout.setVisibility(View.VISIBLE);
        informacaoAdicionalDezenasText.setVisibility(View.VISIBLE);
        informacaoAdicionalResultadoText.setText(resultado);
        informacaoAdicionalDezenasText.setText(aposta);
        if (isIgual) {
            informacaoAdicionalDezenasText.setTextColor(getContext().getResources().getColor(estilo.getIconImpressoraColor()));
            informacaoAdicionalDezenasText.setTypeface(ViewUtils.getFontArialMT(getContext()), Typeface.BOLD);
        } else {
            informacaoAdicionalDezenasText.setTextColor(getContext().getResources().getColor(estilo.getColorTextAlpha6()));
        }
    }

    private void initResultadoRecyclerView() {

        if (!resultadoConcursoDTO.getNumerosSorteadosPrimeiroSorteio().isEmpty()) {
            dezenasResultadoRecyclerView.setAdapter(new DezenasAdapter(listIntegerToListString(resultadoConcursoDTO.getNumerosSorteadosPrimeiroSorteio())));
            dezenasResultadoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private void initDezenasRecyclerView() {

        if (!aposta.getNumerosSelecionados().isEmpty()) {
            dezenasRecyclerView.setAdapter(new DezenasResultadoAdapter(listIntegerToListViewBold(aposta.getNumerosSelecionados(), resultadoConcursoDTO.getNumerosSorteadosPrimeiroSorteio())));
            dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private List<String> listIntegerToListString(List<Integer> list) {

        List<String> lines = new ArrayList<>();
        StringBuilder lineSB = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                lineSB.append(ViewUtils.getNumberIntegerToString(list.get(i)));
            } else if (i % 5 == 0) {
                lines.add(lineSB.toString());
                lineSB = new StringBuilder();
                lineSB.append(ViewUtils.getNumberIntegerToString(list.get(i)));
            } else {
                lineSB.append(String.format(" - %s", ViewUtils.getNumberIntegerToString(list.get(i))));
            }
        }
        lines.add(lineSB.toString());
        return lines;
    }

    private List<View> listIntegerToListViewBold(List<Integer> list, List<Integer> compareList) {

        List<View> lines = new ArrayList<>();
        LinearLayout linha = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linha.setOrientation(LinearLayout.HORIZONTAL);
        linha.setLayoutParams(params);

        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                if (compareList.contains(list.get(i))) {
                    qtdAcertos = qtdAcertos + 1;
                    setTextInLayout(list, linha, i, estilo.getIconImpressoraColor(), true, false);
                } else {
                    setTextInLayout(list, linha, i, estilo.getColorTextAlpha6(), false, false);
                }
            } else if (i % 5 == 0) {
                lines.add(linha);
                linha = new LinearLayout(getContext());
                linha.setOrientation(LinearLayout.HORIZONTAL);
                linha.setLayoutParams(params);
                if (compareList.contains(list.get(i))) {
                    qtdAcertos = qtdAcertos + 1;
                    setTextInLayout(list, linha, i, estilo.getIconImpressoraColor(), true, false);
                } else {
                    setTextInLayout(list, linha, i, estilo.getColorTextAlpha6(), false, false);
                }
            } else {
                if (compareList.contains(list.get(i))) {
                    qtdAcertos = qtdAcertos + 1;
                    setTextInLayout(list, linha, i, estilo.getIconImpressoraColor(), true, true);
                } else {
                    setTextInLayout(list, linha, i, estilo.getColorTextAlpha6(), false, true);
                }
            }
        }
        lines.add(linha);
        return lines;
    }

    private void setTextInLayout(List<Integer> list, LinearLayout linha, int i, int colorText, boolean bold, boolean separate) {
        TextView textView = new TextView(getContext());
        textView.setTextColor(getContext().getResources().getColor(colorText));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        if (bold) {
            textView.setTypeface(ViewUtils.getFontArialMT(getContext()), Typeface.BOLD);
        } else {
            textView.setTypeface(ViewUtils.getFontArialMT(getContext()));
        }
        if (separate) {
            textView.setText(String.format(" - %s", ViewUtils.getNumberIntegerToString(list.get(i))));
        } else {
            textView.setText(ViewUtils.getNumberIntegerToString(list.get(i)));
        }
        linha.addView(textView);
    }
}
