package caixa.gov.br.loterias.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

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
import caixa.gov.br.loterias.view.adapter.DetalheApostaConfirmadaLotecaAdapter;
import caixa.gov.br.loterias.view.adapter.DetalheApostaConfirmadaLotogolAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

@EFragment(R.layout.fragment_aposta_confirmada_partida)
public class ApostaConfirmadaPartidaFragment extends Fragment {

    @Bean
    protected ApostaBO apostaBO;

    @ViewById
    protected LinearLayout cabecalhoLotecaLinearLayout;

    @ViewById
    protected TextView concursoCabecalhoTextView, concursoRodaPeTextView, dataHoraTextView, statusApostaTextView;

    @ViewById
    protected ExpandableHeightRecyclerView listaPartidasApostadasExpandebleRecyclerView;

    @ViewById
    protected Button conferirResultadoButton;

    @FragmentArg
    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    private AlertDialog alertDialog;

    @FragmentArg
    protected boolean loteca;

    @AfterViews
    public void init() {

        concursoCabecalhoTextView.setText(String.format(Locale.getDefault(), "%d", aposta.getConcursoAlvo()));
        concursoRodaPeTextView.setText(String.format(Locale.getDefault(), "%d", aposta.getConcursoAlvo()));
        dataHoraTextView.setText(ViewUtils.getDateAndHour(aposta.getDataEfetivacao(), aposta.getHoraEfetivacao()));
        statusApostaTextView.setText(aposta.getSituacao().getDescricao());
        if (getActivity().getString(R.string.label_efetivada).equalsIgnoreCase(aposta.getSituacao().getDescricao())) {
            conferirResultadoButton.setVisibility(View.VISIBLE);
        }

        initList();
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

    private void initList() {

        if (loteca) {
            cabecalhoLotecaLinearLayout.setVisibility(View.VISIBLE);
            listaPartidasApostadasExpandebleRecyclerView.setAdapter(new DetalheApostaConfirmadaLotecaAdapter(aposta.getPartidasLoteca()));
        } else {
            listaPartidasApostadasExpandebleRecyclerView.setAdapter(new DetalheApostaConfirmadaLotogolAdapter(aposta.getPartidasLotogol()));
        }

        listaPartidasApostadasExpandebleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaPartidasApostadasExpandebleRecyclerView.setExpanded(Boolean.TRUE);
        listaPartidasApostadasExpandebleRecyclerView.setFocusable(Boolean.FALSE);
    }

}
