package br.gov.caixa.loterias.apostar.interno.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.utils.AppUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.DezenasAdapter;

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
    protected RelativeLayout informacaoAdicionalLayout;

    @ViewById
    protected RecyclerView dezenasRecyclerView;

    @FragmentArg
    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    @AfterViews
    protected void init() {
        concursoTxt.setText(String.format(Locale.getDefault(), "%d", aposta.getConcursoInicial()));
        if(StringUtils.isEmpty(aposta.getHoraEfetivacao())){
            dataApostaTxt.setText(aposta.getDataEfetivacao() + " - " + aposta.getHoraEfetivacao());
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

        initdezenasRecyclerView();
    }

    private void initdezenasRecyclerView() {

        if (!aposta.getNumerosSelecionados().isEmpty()) {
            dezenasRecyclerView.setAdapter(new DezenasAdapter(AppUtils.listIntegerToListString(aposta.getNumerosSelecionados())));
            dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }
}
