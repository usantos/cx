package br.gov.caixa.loterias.apostas.view.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheApostaConfirmadaLotecaAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalheApostaConfirmadaLotogolAdapter;
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
    protected TextView tv_val_premio, tv_val_premio_tit;

    @ViewById
    protected ConstraintLayout cl_premio_pago;

    @ViewById
    protected ExpandableHeightRecyclerView listaPartidasApostadasExpandebleRecyclerView;

    @FragmentArg
    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    @FragmentArg
    protected ResultadoConcursoDTO resultadoConcurso;

    @FragmentArg
    protected boolean loteca;

    @FragmentArg
    protected BigDecimal valorPremio;

    @AfterViews
    public void init() {

        concursoCabecalhoTextView.setText(String.format(Locale.getDefault(), getResources().getString(R.string.percent_d), aposta.getConcursoInicial()));
        concursoRodaPeTextView.setText(String.format(Locale.getDefault(), getResources().getString(R.string.percent_d), aposta.getConcursoInicial()));
        if (StringUtils.isEmpty(aposta.getHoraEfetivacao())) {
            dataHoraTextView.setText(aposta.getDataEfetivacao());
        } else {
            dataHoraTextView.setText(aposta.getDataEfetivacao() + getResources().getString(R.string.barra_n) + aposta.getHoraEfetivacao());
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

    private void initList() {

        if (loteca) {
            cabecalhoLotecaLinearLayout.setVisibility(View.VISIBLE);
            concursoRodapeLinearLayout.setVisibility(View.INVISIBLE);
            listaPartidasApostadasExpandebleRecyclerView.setAdapter(new DetalheApostaConfirmadaLotecaAdapter(aposta.getPartidasLoteca(),resultadoConcurso));
        } else {
            concursoRodapeLinearLayout.setVisibility(View.INVISIBLE);
            listaPartidasApostadasExpandebleRecyclerView.setAdapter(new DetalheApostaConfirmadaLotogolAdapter(aposta.getPartidasLotogol()));
        }

        listaPartidasApostadasExpandebleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaPartidasApostadasExpandebleRecyclerView.setExpanded(Boolean.TRUE);
        listaPartidasApostadasExpandebleRecyclerView.setFocusable(Boolean.FALSE);
    }

    public void atualizaValorPremio(BigDecimal valor){
        if (valor != null){
            valorPremio = valor;
            tv_val_premio.setText(ViewUtils.getMoedaFormat(valorPremio));
        }

    }

}