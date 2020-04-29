package br.gov.caixa.loterias.apostas.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.view.adapter.DetalheApostaConfirmadaLotecaAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.DetalheApostaConfirmadaLotogolAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;

@EFragment(R.layout.fragment_aposta_confirmada_partida)
public class ApostaConfirmadaPartidaFragment extends Fragment {

    @Bean
    protected ApostaBO apostaBO;

    @ViewById
    protected LinearLayout cabecalhoLotecaLinearLayout;

    @ViewById
    protected LinearLayout concursoRodapeLinearLayout;

    @ViewById
    protected TextView concursoCabecalhoTextView, concursoRodaPeTextView, dataHoraTextView, statusApostaTextView;

    @ViewById
    protected ExpandableHeightRecyclerView listaPartidasApostadasExpandebleRecyclerView;

    @FragmentArg
    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    @FragmentArg
    protected boolean loteca;

    @AfterViews
    public void init() {

        concursoCabecalhoTextView.setText(String.format(Locale.getDefault(), "%d", aposta.getConcursoInicial()));
        concursoRodaPeTextView.setText(String.format(Locale.getDefault(), "%d", aposta.getConcursoInicial()));
        if (StringUtils.isEmpty(aposta.getHoraEfetivacao())) {
            dataHoraTextView.setText(aposta.getDataEfetivacao());
        } else {
            dataHoraTextView.setText(aposta.getDataEfetivacao() + " - " + aposta.getHoraEfetivacao());
        }
        statusApostaTextView.setText(aposta.getSituacao().getDescricao());

        initList();
    }

//    @Click(R.id.conferirResultadoButton)
//    protected void conferirResultadoClick() {
//        ConferirResultadoHelper.conferirResultado(aposta.getId(), getActivity());
//    }

    private void initList() {

        if (loteca) {
            cabecalhoLotecaLinearLayout.setVisibility(View.VISIBLE);
            concursoRodapeLinearLayout.setVisibility(View.INVISIBLE);
            listaPartidasApostadasExpandebleRecyclerView.setAdapter(new DetalheApostaConfirmadaLotecaAdapter(aposta.getPartidasLoteca()));
        } else {
            concursoRodapeLinearLayout.setVisibility(View.INVISIBLE);
            listaPartidasApostadasExpandebleRecyclerView.setAdapter(new DetalheApostaConfirmadaLotogolAdapter(aposta.getPartidasLotogol()));
        }

        listaPartidasApostadasExpandebleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaPartidasApostadasExpandebleRecyclerView.setExpanded(Boolean.TRUE);
        listaPartidasApostadasExpandebleRecyclerView.setFocusable(Boolean.FALSE);
    }

}
