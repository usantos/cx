package br.gov.caixa.loterias.apostas.view.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.ResultadoApostaConfirmadaActivity_;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConcursoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoAposta;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
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
    protected ConstraintLayout cl_premio_pago, clResgatePremio;

    @ViewById
    protected ExpandableHeightRecyclerView listaPartidasApostadasExpandebleRecyclerView;

    @FragmentArg
    protected IdentificaoDeUmaApostaDas8Modalidades aposta;

    @FragmentArg
    protected ResultadoConcursoDTO resultadoConcurso;

    @FragmentArg
    protected boolean loteca;

    @FragmentArg
    protected ComprovanteApostaDTO comprovanteApostaDTO;

    @FragmentArg
    protected BigDecimal valorPremio;

    private DetalhesPremioDTO detalhesPremio;

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

        if (aposta.getSituacao().getValor() == SituacaoAposta.PREMIO_PAGO || apostaPremiada()) {

            tv_val_premio.setVisibility(View.VISIBLE);
            tv_val_premio_tit.setVisibility(View.VISIBLE);
            if (valorPremio != null) {
                tv_val_premio.setText(ViewUtils.getMoedaFormat(valorPremio));
            } else {
                getValorPremio(aposta.getId());
            }
            if (apostaPremiada()){
                statusApostaTextView.setText(aposta.getSituacao().getDescricao());
                clResgatePremio.setVisibility(View.VISIBLE);
                cl_premio_pago.setVisibility(View.GONE);
                statusApostaTextView.setVisibility(View.VISIBLE);
            } else {
                cl_premio_pago.setVisibility(View.VISIBLE);
                clResgatePremio.setVisibility(View.GONE);
                statusApostaTextView.setVisibility(View.GONE);
            }
        } else {
            statusApostaTextView.setText(aposta.getSituacao().getDescricao());
        }

        initList();
    }

    private void getValorPremio(Long apostaId) {
        ApostaBO apostaBO = ApostaBO_.getInstance_(getContext());
        apostaBO.getApostaDetalhePremio(apostaId, new RequestListener<DetalhesPremioDTOResponse>() {
            @Override
            public void onResponse(DetalhesPremioDTOResponse result) {
                AlertDialogUtils.dismiss();
                atualizaValorPremio(result.getPayload().getPremio().getValorLiquido());
            }

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }

    private boolean apostaPremiada() {
        return aposta.getSituacao().getValor() == SituacaoAposta.PREMIADA;
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
            tv_val_premio.setVisibility(View.VISIBLE);
            tv_val_premio_tit.setVisibility(View.VISIBLE);
        }

    }

    @Click(R.id.clResgatePremio)
    protected void resgatarPremio(){
        getApostaDetalhePremio(aposta.getId());
    }

    private void getApostaDetalhePremio(Long apostaId) {
        if (detalhesPremio != null){
            startResgateActivity();
        } else {
            ApostaBO apostaBO = ApostaBO_.getInstance_(getContext());
            apostaBO.getApostaDetalhePremio(apostaId, new RequestListener<DetalhesPremioDTOResponse>() {
                @Override
                public void onResponse(DetalhesPremioDTOResponse result) {
                    AlertDialogUtils.dismiss();

                    detalhesPremio = result.getPayload();
                    startResgateActivity();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialogUtils.dismiss();
                    RedirectNetwork.checkRedirect(error, getActivity());
                }
            });
        }
    }

    private void startResgateActivity() {
        if (detalhesPremio != null && comprovanteApostaDTO != null){
            ResultadoApostaConfirmadaActivity_.intent(getContext()).detalhesPremioDTO(detalhesPremio).comprovante(comprovanteApostaDTO).start();
        }
    }

}