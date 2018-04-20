package caixa.gov.br.loterias.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.utils.helper.ConferirResultadoHelper;
import caixa.gov.br.loterias.view.adapter.DetalheApostaConfirmadaLotecaAdapter;
import caixa.gov.br.loterias.view.adapter.DetalheApostaConfirmadaLotogolAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;

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

    @FragmentArg
    protected boolean loteca;

    @AfterViews
    public void init() {

        concursoCabecalhoTextView.setText(String.format(Locale.getDefault(), "%d", aposta.getConcursoAlvo()));
        concursoRodaPeTextView.setText(String.format(Locale.getDefault(), "%d", aposta.getConcursoAlvo()));
        if (StringUtils.isEmpty(aposta.getHoraEfetivacao())) {
            dataHoraTextView.setText(aposta.getDataEfetivacao());
        } else {
            dataHoraTextView.setText(ViewUtils.getDateAndHour(aposta.getDataEfetivacao(), aposta.getHoraEfetivacao()));
        }
        statusApostaTextView.setText(aposta.getSituacao().getDescricao());
        if (getActivity().getString(R.string.label_efetivada).equalsIgnoreCase(aposta.getSituacao().getDescricao())) {
            conferirResultadoButton.setVisibility(View.VISIBLE);
        }
        initList();
    }

    @Click(R.id.conferirResultadoButton)
    protected void conferirResultadoClick() {
        ConferirResultadoHelper.conferirResultado(aposta.getId(), getActivity());
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
