package caixa.gov.br.loterias.view.fragment;


import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ConcursoPremiadoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoConcursoDTO;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.DezenasAdapter;

@EFragment(R.layout.fragment_aposta_confirmada_numero_detalhes_premio_pago)
public class ApostaConfirmadaNumeroDetalhesPremioPago extends Fragment {


    @ViewById
    protected TextView concursoTxt, quantidadeAcertosTxt, acertosTxt, quantidadeGanhadoresTxt, ganhadoresTxt, dataApostaTxt;

    @ViewById
    protected RecyclerView dezenasResultadoRecyclerView, dezenasRecyclerView;

    @FragmentArg
    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    @FragmentArg
    protected ResultadoConcursoDTO resultadoConcursoDTO;

    private int qtdAcertos;

    @AfterViews
    protected void init() {
        concursoTxt.setText(String.format(Locale.getDefault(), "%d", resultadoConcursoDTO.getConcurso().getNumero()));
        if (StringUtils.isEmpty(aposta.getHoraEfetivacao())) {
            dataApostaTxt.setText(aposta.getDataEfetivacao());
        } else {
            dataApostaTxt.setText(ViewUtils.getDateAndHour(aposta.getDataEfetivacao(), aposta.getHoraEfetivacao()));
        }

        int ganhadores;
        ganhadores = CollectionUtils.isNotEmpty(resultadoConcursoDTO.getGanhadoresPrimeiroSorteio()) ? resultadoConcursoDTO.getGanhadoresPrimeiroSorteio().size() : 0;
        if (aposta.getQuantidadeTeimosinhas() != null && aposta.getQuantidadeTeimosinhas() > 0) {
            ganhadores = CollectionUtils.isNotEmpty(resultadoConcursoDTO.getGanhadoresSegundoSorteio()) ? resultadoConcursoDTO.getGanhadoresSegundoSorteio().size() : 0;
        }

        quantidadeGanhadoresTxt.setText(String.valueOf(ganhadores));
        if (ganhadores > 1) {
            ganhadoresTxt.setText(R.string.label_ganhador);
        } else {
            ganhadoresTxt.setText(R.string.label_ganhadores);
        }

        initResultadoRecyclerView();
        initDezenasRecyclerView();
        quantidadeAcertosTxt.setText(String.valueOf(qtdAcertos));
        if (qtdAcertos > 1) {
            acertosTxt.setText(R.string.label_acerto);
        } else {
            acertosTxt.setText(R.string.label_acertos);
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
            dezenasRecyclerView.setAdapter(new DezenasAdapter(listIntegerToListStringBold(aposta.getNumerosSelecionados(), resultadoConcursoDTO.getNumerosSorteadosPrimeiroSorteio())));
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

    private List<String> listIntegerToListStringBold(List<Integer> list, List<Integer> compareList) {

        List<String> lines = new ArrayList<>();
        StringBuilder lineSB = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                if (compareList.contains(list.get(i))) {
                    qtdAcertos = qtdAcertos + 1;
                    lineSB.append(String.format("<b>%s</b>", ViewUtils.getNumberIntegerToString(list.get(i))));
                } else {
                    lineSB.append(ViewUtils.getNumberIntegerToString(list.get(i)));
                }
            } else if (i % 5 == 0) {
                lines.add(lineSB.toString());
                lineSB = new StringBuilder();
                if (compareList.contains(list.get(i))) {
                    qtdAcertos = qtdAcertos + 1;
                    lineSB.append(String.format("<b>%s</b>", ViewUtils.getNumberIntegerToString(list.get(i))));
                } else {
                    lineSB.append(ViewUtils.getNumberIntegerToString(list.get(i)));
                }
            } else {
                if (compareList.contains(list.get(i))) {
                    qtdAcertos = qtdAcertos + 1;
                    lineSB.append(String.format("<b>%s</b>", String.format(" - %s", ViewUtils.getNumberIntegerToString(list.get(i)))));
                } else {
                    lineSB.append(String.format(" - %s", ViewUtils.getNumberIntegerToString(list.get(i))));
                }
            }
        }
        lines.add(lineSB.toString());
        return lines;
    }
}
