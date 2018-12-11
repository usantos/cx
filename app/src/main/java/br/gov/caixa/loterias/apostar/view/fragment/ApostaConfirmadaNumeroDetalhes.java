package br.gov.caixa.loterias.apostar.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.DTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostar.utils.AppUtils;
import br.gov.caixa.loterias.apostar.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.utils.helper.ConferirResultadoHelper;
import br.gov.caixa.loterias.apostar.view.adapter.DezenasAdapter;

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
    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    @AfterViews
    protected void init() {
        concursoTxt.setText(String.format(Locale.getDefault(), "%d", aposta.getConcursoInicial()));
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
        DialogUtils.show(getContext());
        ApostaBO apostaBO = ApostaBO_.getInstance_(getContext());
        apostaBO.getApostaConferir(aposta.getId(), new RequestListener<DTOEnumIntegerResponse>() {
            @Override
            public void onResponse(DTOEnumIntegerResponse result) {
                DialogUtils.dismiss();
                if (Objects.equals(SituacaoResultadoBilheteEnum.PREMIADA.getValor().toString(), result.getPayload().getValor().toString())) {
                    ConferirResultadoHelper.conferirResultado(aposta.getId(), getActivity());
                } else {
                    ViewUtils.alert(getContext(), getContext().getString(R.string.MA020));
                    resultTxt.setText(result.getPayload().getDescricao());
                }
            }
//            {"codigo":"013004","mensagem":"O sistema para consulta da situação da aposta encontra-se indisponível. Tente novamente mais tarde. ","tipo":1,"redirect":"form-apostas"}
            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                try{
                    JSONObject geralJO = new JSONObject(new String(error.networkResponse.data));
                    String mensagem = geralJO.getString("mensagem");
                    ViewUtils.alert(getContext(), mensagem);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
