package caixa.gov.br.loterias.view.fragment;


import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import caixa.gov.br.loterias.R;
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

    @AfterViews
    protected void init() {
        concursoTxt.setText(String.format(Locale.getDefault(), "%d",resultadoConcursoDTO.getConcurso().getNumero()));
        if(StringUtils.isEmpty(aposta.getHoraEfetivacao())){
            dataApostaTxt.setText(aposta.getDataEfetivacao());
        }else{
            dataApostaTxt.setText(ViewUtils.getDateAndHour(aposta.getDataEfetivacao(), aposta.getHoraEfetivacao()));
        }
        initResultadoRecyclerView();
        initDezenasRecyclerView();
    }

    private void initResultadoRecyclerView() {

        if (!resultadoConcursoDTO.getNumerosSorteadosPrimeiroSorteio().isEmpty()) {
            dezenasResultadoRecyclerView.setAdapter(new DezenasAdapter(listIntegerToListString(resultadoConcursoDTO.getNumerosSorteadosPrimeiroSorteio())));
            dezenasResultadoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private void initDezenasRecyclerView() {

        if (!aposta.getNumerosSelecionados().isEmpty()) {
            dezenasRecyclerView.setAdapter(new DezenasAdapter(listIntegerToListString(aposta.getNumerosSelecionados())));
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

}
