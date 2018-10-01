package caixa.gov.br.loterias.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.utils.helper.ConferirResultadoHelper;
import caixa.gov.br.loterias.view.adapter.DezenasAdapter;

/**
 * Created by pmotta on 23/03/2018.
 * Class ApostaConfirmadaNumeroDetalhes
 */

@EFragment(R.layout.fragment_aposta_confirmada_numero_detalhes)
public class ApostaConfirmadaNumeroDetalhes extends Fragment {

    @Bean
    protected ApostaBO apostaBO;

    @ViewById
    protected TextView concursoTxt, quantidadeApostadasTxt, dataApostaTxt, resultTxt, modalidadeTxt, apostaTrocaTxt, informacaoAdicionalTxt, labelInformacaoAdicionalTxt;

    @ViewById
    protected Button conferirResultadoButton;

    @ViewById
    protected RelativeLayout informacaoAdicionalLayout;

    @ViewById
    protected RecyclerView dezenasRecyclerView;

    @FragmentArg
    protected ApostaDTO aposta;

    @AfterViews
    protected void init() {

        concursoTxt.setText(String.format(Locale.getDefault(), "%d", aposta.getConcursoAlvo()));
        if(StringUtils.isEmpty(aposta.getHoraEfetivacao())){
            dataApostaTxt.setText(ViewUtils.getDateAndHour(aposta.getDataEfetivacao(), "21:20"));
        }else{
            dataApostaTxt.setText(ViewUtils.getDateAndHour(aposta.getDataEfetivacao(), aposta.getHoraEfetivacao()));
        }
        resultTxt.setText(aposta.getSituacao().getDescricao());

        if (aposta.getTroca()) {
            apostaTrocaTxt.setVisibility(View.VISIBLE);
        }
        if (aposta.getQuantidadeTeimosinhas() > 0) {
            modalidadeTxt.setVisibility(View.VISIBLE);
            quantidadeApostadasTxt.setVisibility(View.VISIBLE);

            Integer quantidade = aposta.getQuantidadeTeimosinhas();
            String textoTeimosinha = "";
            if (quantidade == 1) {
                textoTeimosinha = String.format(Locale.getDefault(), "%d %s", quantidade, getActivity().getString(R.string.label_aposta));
            } else {
                textoTeimosinha = String.format(Locale.getDefault(), "%d %s", quantidade, getActivity().getString(R.string.label_apostas));
            }
            quantidadeApostadasTxt.setText(textoTeimosinha);
        }
        if (aposta.getTimeDoCoracao() != null) {
            informacaoAdicionalLayout.setVisibility(View.VISIBLE);
            informacaoAdicionalTxt.setText(aposta.getTimeDoCoracao().getNome());
        }else if(aposta.getMesDeSorte() != null && aposta.getMesDeSorte().getNumero() != null){
            informacaoAdicionalLayout.setVisibility(View.VISIBLE);
            labelInformacaoAdicionalTxt.setText(R.string.label_mes_sorte);
            informacaoAdicionalTxt.setText(aposta.getMesDeSorte().getNome());
        }
        if (getActivity().getString(R.string.label_efetivada).equalsIgnoreCase(aposta.getSituacao().getDescricao())) {
            conferirResultadoButton.setVisibility(View.VISIBLE);
        }
        initdezenasRecyclerView();
    }

    private void initdezenasRecyclerView() {

        if (!aposta.getNumerosSelecionados().isEmpty()) {
            dezenasRecyclerView.setAdapter(new DezenasAdapter(AppUtils.listIntegerToListString(aposta.getNumerosSelecionados())));
            dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    @Click(R.id.conferirResultadoButton)
    protected void conferirResultadoClick() {
            ConferirResultadoHelper.conferirResultado(aposta.getId(), getActivity());
    }
}
