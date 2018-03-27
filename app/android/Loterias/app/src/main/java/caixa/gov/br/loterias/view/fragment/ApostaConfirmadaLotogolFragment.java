package caixa.gov.br.loterias.view.fragment;


import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Button;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import java.util.Locale;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaDTO;
import caixa.gov.br.loterias.view.adapter.DetalheApostaConfirmadaLotogolAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;

@EFragment(R.layout.fragment_aposta_confirmada_lotogol)
public class ApostaConfirmadaLotogolFragment extends Fragment {

    @ViewById
    protected TextView concursoCabecalhoTextView;

    @ViewById
    protected ExpandableHeightRecyclerView listaPartidasApostadasExpandebleRecyclerView;

    @ViewById
    protected Button conferirResultadoButton;

    @FragmentArg
    protected ApostaDTO apostaDTO;

    @AfterViews
    protected void init() {
        concursoCabecalhoTextView.setText(String.format(Locale.getDefault(), "%d", apostaDTO.getConcursoAlvo()));
        initList();
    }

    @Click(R.id.conferirResultadoButton)
    protected void clickconferirResultadoButton() {
        //TODO implementar método
    }

    private void initList() {

        listaPartidasApostadasExpandebleRecyclerView.setAdapter(new DetalheApostaConfirmadaLotogolAdapter(apostaDTO.getPartidasLotogol()));
        listaPartidasApostadasExpandebleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaPartidasApostadasExpandebleRecyclerView.setExpanded(Boolean.TRUE);
        listaPartidasApostadasExpandebleRecyclerView.setFocusable(Boolean.FALSE);
    }
}
