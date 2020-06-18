package br.gov.caixa.loterias.apostar.interno.view.fragment;

import android.support.constraint.ConstraintLayout;
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

import java.math.BigDecimal;
import java.util.Locale;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.DetalheApostaConfirmadaLotecaAdapter;
import br.gov.caixa.loterias.apostar.interno.view.adapter.DetalheApostaConfirmadaLotogolAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightRecyclerView;

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
    protected TextView tv_val_premio, tv_val_premio_tit;

    @ViewById
    protected ConstraintLayout cl_premio_pago;

    @ViewById
    protected ExpandableHeightRecyclerView listaPartidasApostadasExpandebleRecyclerView;

    @FragmentArg
    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    @FragmentArg
    protected boolean loteca;

    @FragmentArg
    protected BigDecimal valorPremio;

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

        if (valorPremio != null) {
            tv_val_premio.setVisibility(View.VISIBLE);
            tv_val_premio_tit.setVisibility(View.VISIBLE);
            cl_premio_pago.setVisibility(View.VISIBLE);
            statusApostaTextView.setVisibility(View.GONE);
            tv_val_premio.setText(ViewUtils.getMoedaFormat(valorPremio));
        } else {
            statusApostaTextView.setText(aposta.getSituacao().getDescricao());
        }
        
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
