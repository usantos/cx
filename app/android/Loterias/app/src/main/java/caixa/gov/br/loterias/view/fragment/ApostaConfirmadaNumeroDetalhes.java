package caixa.gov.br.loterias.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ComprovanteApostaDTO;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.DezenasAdapter;

/**
 * Created by pmotta on 23/03/2018.
 * Class ApostaConfirmadaNumeroDetalhes
 */

@EFragment(R.layout.fragment_aposta_confirmada_numero_detalhes)
public class ApostaConfirmadaNumeroDetalhes extends Fragment {

    @ViewById
    protected TextView concursoTxt, quantidadeApostadasTxt, dataApostaTxt, resultTxt, modalidadeTxt, apostaTrocaTxt, timeCoracaoTxt;

    @ViewById
    protected Button conferirResultadoButton;

    @ViewById
    protected RelativeLayout timeCoracaoLayout;

    @ViewById
    protected RecyclerView dezenasRecyclerView;

    @FragmentArg
    protected ComprovanteApostaDTO comprovanteApostaDTO;

    @AfterViews
    protected void init() {
        concursoTxt.setText(String.format(Locale.getDefault(), "%d", comprovanteApostaDTO.getAposta().getConcursoAlvo()));
        dataApostaTxt.setText(ViewUtils.getDateAndHour(comprovanteApostaDTO.getAposta().getDataEfetivacao(), comprovanteApostaDTO.getAposta().getHoraEfetivacao()));
        resultTxt.setText(comprovanteApostaDTO.getAposta().getSituacao().getDescricao());

        if (comprovanteApostaDTO.getAposta().getTroca()) {
            apostaTrocaTxt.setVisibility(View.VISIBLE);
        }
        if (comprovanteApostaDTO.getAposta().getQuantidadeTeimosinhas() > 0) {
            modalidadeTxt.setVisibility(View.VISIBLE);
        }
        if (comprovanteApostaDTO.getAposta().getTimeDoCoracao() != null) {
            timeCoracaoLayout.setVisibility(View.VISIBLE);
            timeCoracaoTxt.setText(comprovanteApostaDTO.getAposta().getTimeDoCoracao().getNome());
        }
        if (getActivity().getString(R.string.label_efetivada).equalsIgnoreCase(comprovanteApostaDTO.getAposta().getSituacao().getDescricao())) {
            conferirResultadoButton.setVisibility(View.VISIBLE);
        }
        initDezenasGridView();
    }

    private void initDezenasGridView() {

        if (!comprovanteApostaDTO.getAposta().getNumerosSelecionados().isEmpty()) {
            dezenasRecyclerView.setAdapter(new DezenasAdapter(listIntegerToListString()));
            dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private List<String> listIntegerToListString() {

        List<String> lines = new ArrayList<>();
        StringBuilder lineSB = new StringBuilder();

        for (int i = 0; i < comprovanteApostaDTO.getAposta().getNumerosSelecionados().size(); i++) {
            if (i == 0) {
                lineSB.append(ViewUtils.getNumberIntegerToString(comprovanteApostaDTO.getAposta().getNumerosSelecionados().get(i)));
            } else if (i % 5 == 0) {
                lines.add(lineSB.toString());
                lineSB = new StringBuilder();
                lineSB.append(ViewUtils.getNumberIntegerToString(comprovanteApostaDTO.getAposta().getNumerosSelecionados().get(i)));
            } else {
                lineSB.append(String.format("  -  %s", ViewUtils.getNumberIntegerToString(comprovanteApostaDTO.getAposta().getNumerosSelecionados().get(i))));
            }
        }
        lines.add(lineSB.toString());
        return lines;
    }
}
