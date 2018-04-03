package caixa.gov.br.loterias.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.DTOEnumIntegerResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.DetalhesPremioDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.ResultadoApostaConfirmadaActivity_;
import caixa.gov.br.loterias.view.adapter.DezenasAdapter;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

/**
 * Created by pmotta on 23/03/2018.
 * Class ApostaConfirmadaNumeroDetalhes
 */

@EFragment(R.layout.fragment_aposta_confirmada_numero_detalhes)
public class ApostaConfirmadaNumeroDetalhes extends Fragment {

    @Bean
    protected ApostaBO apostaBO;

    @ViewById
    protected TextView concursoTxt, quantidadeApostadasTxt, dataApostaTxt, resultTxt, modalidadeTxt, apostaTrocaTxt, timeCoracaoTxt;

    @ViewById
    protected Button conferirResultadoButton;

    @ViewById
    protected RelativeLayout timeCoracaoLayout;

    @ViewById
    protected RecyclerView dezenasRecyclerView;

    @FragmentArg
    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    private AlertDialog alertDialog;

    @AfterViews
    protected void init() {

        concursoTxt.setText(String.format(Locale.getDefault(), "%d", aposta.getConcursoAlvo()));
        dataApostaTxt.setText(ViewUtils.getDateAndHour(aposta.getDataEfetivacao(), aposta.getHoraEfetivacao()));
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
            timeCoracaoLayout.setVisibility(View.VISIBLE);
            timeCoracaoTxt.setText(aposta.getTimeDoCoracao().getNome());
        }
        if (getActivity().getString(R.string.label_efetivada).equalsIgnoreCase(aposta.getSituacao().getDescricao())) {
            conferirResultadoButton.setVisibility(View.VISIBLE);
        }
        initdezenasRecyclerView();
    }

    private void initdezenasRecyclerView() {

        if (!aposta.getNumerosSelecionados().isEmpty()) {
            dezenasRecyclerView.setAdapter(new DezenasAdapter(listIntegerToListString()));
            dezenasRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    private List<String> listIntegerToListString() {

        List<String> lines = new ArrayList<>();
        StringBuilder lineSB = new StringBuilder();

        for (int i = 0; i < aposta.getNumerosSelecionados().size(); i++) {
            if (i == 0) {
                lineSB.append(ViewUtils.getNumberIntegerToString(aposta.getNumerosSelecionados().get(i)));
            } else if (i % 5 == 0) {
                lines.add(lineSB.toString());
                lineSB = new StringBuilder();
                lineSB.append(ViewUtils.getNumberIntegerToString(aposta.getNumerosSelecionados().get(i)));
            } else {
                lineSB.append(String.format(" - %s", ViewUtils.getNumberIntegerToString(aposta.getNumerosSelecionados().get(i))));
            }
        }
        lines.add(lineSB.toString());
        return lines;
    }

    @Click(R.id.conferirResultadoButton)
    protected void conferirResultadoClick() {
        alertDialog = LoadingViewLoterias.show(getActivity());
        apostaBO.getApostaConferir(aposta.getId(), new RequestListener<DTOEnumIntegerResponse>() {
            @Override
            public void onResponse(DTOEnumIntegerResponse result) {

                if (Objects.equals(SituacaoResultadoBilheteEnum.PREMIADA.getValor().toString(), result.getPayload().getValor().toString())) {
                    getApostaDetalhePremio();
                } else {
                    alertDialog.dismiss();
                    ViewUtils.alert(getActivity(), getString(R.string.MA020));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                errroVolley(error);
            }
        });
    }

    private void getApostaDetalhePremio() {
        apostaBO.getApostaDetalhePremio(aposta.getId(), new RequestListener<DetalhesPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesPremioDTOResponse result) {
                alertDialog.dismiss();
                ResultadoApostaConfirmadaActivity_.intent(getActivity()).detalhesPremioDTO(result.getPayload()).start();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                errroVolley(error);
            }
        });
    }

    private void errroVolley(VolleyError error) {
        alertDialog.dismiss();
        String msgError = MensagensNetwork.setMensagem(error, getActivity());
        ViewUtils.alert(getActivity(), msgError);
    }
}
