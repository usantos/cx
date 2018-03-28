package caixa.gov.br.loterias.view.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.Locale;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.DetalheApostaConfirmadaLotecaAdapter;
import caixa.gov.br.loterias.view.adapter.DetalheApostaConfirmadaLotogolAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;

@EFragment(R.layout.fragment_aposta_confirmada_partida)
public class ApostaConfirmadaPartidaFragment extends Fragment {

    @ViewById
    protected LinearLayout cabecalhoLotecaLinearLayout;

    @ViewById
    protected TextView concursoCabecalhoTextView, concursoRodaPeTextView, dataHoraTextView, statusApostaTextView;

    @ViewById
    protected ExpandableHeightRecyclerView listaPartidasApostadasExpandebleRecyclerView;

    @ViewById
    protected Button conferirResultadoButton;

    @FragmentArg
    protected ApostaDTO apostaDTO;

    @FragmentArg
    protected boolean loteca;

    @AfterViews
    public void init() {
        concursoCabecalhoTextView.setText(String.format(Locale.getDefault(), "%d", apostaDTO.getConcursoAlvo()));
        concursoRodaPeTextView.setText(String.format(Locale.getDefault(), "%d", apostaDTO.getConcursoAlvo()));
        dataHoraTextView.setText(ViewUtils.getDateAndHour(apostaDTO.getDataEfetivacao(), apostaDTO.getHoraEfetivacao()));
        statusApostaTextView.setText(apostaDTO.getSituacao().getDescricao());
        if (getActivity().getString(R.string.label_efetivada).equalsIgnoreCase(apostaDTO.getSituacao().getDescricao())) {
            conferirResultadoButton.setVisibility(View.VISIBLE);
        }

        initList();
    }

    @Click(R.id.conferirResultadoButton)
    protected void clickconferirResultadoButton() {
        //TODO implementar método
    }

    private void initList() {

        if (loteca) {
            cabecalhoLotecaLinearLayout.setVisibility(View.VISIBLE);
            listaPartidasApostadasExpandebleRecyclerView.setAdapter(new DetalheApostaConfirmadaLotecaAdapter(apostaDTO.getPartidasLoteca()));
        } else {
            listaPartidasApostadasExpandebleRecyclerView.setAdapter(new DetalheApostaConfirmadaLotogolAdapter(apostaDTO.getPartidasLotogol()));
        }

        listaPartidasApostadasExpandebleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaPartidasApostadasExpandebleRecyclerView.setExpanded(Boolean.TRUE);
        listaPartidasApostadasExpandebleRecyclerView.setFocusable(Boolean.FALSE);
    }

}
