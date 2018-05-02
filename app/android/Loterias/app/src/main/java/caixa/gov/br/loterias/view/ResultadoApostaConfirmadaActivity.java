package caixa.gov.br.loterias.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.CodigoResgateDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.DetalhesPremioDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroSimulacao;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.FaixaPremiacaoAdapter;
import caixa.gov.br.loterias.view.custom.ContenteResultadoPremiado;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

@EActivity(R.layout.activity_resultado_aposta_confirmada)
public class ResultadoApostaConfirmadaActivity extends SettingToolbarActivity {

    @Bean
    protected ApostaBO apostaBO;

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected ContenteResultadoPremiado contenteResultadoPremiado;

    @ViewById
    protected RelativeLayout lotericaLayout, agenciaCaixaLayout, mercadoPagoLayout;

    @Extra
    protected DetalhesPremioDTO detalhesPremioDTO;

    private AlertDialog alertDialog;

    @AfterViews
    protected void init() {

        createToolBar();
        ModalidadeEnum modalidadeEnum = detalhesPremioDTO.getAposta().getModalidade();
        EstiloModalidade estiloModalidade = new EstiloModalidade(modalidadeEnum);
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidadeEnum);

        contenteResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorClara()));
        contenteResultadoPremiado.getTopo2ModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
        contenteResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidade.getTrevoBackground()));
        contenteResultadoPremiado.getTituloModalidadeTextView().setText(ModalidadeEnum.fromString(detalhesPremioDTO.getAposta().getModalidade()));
        contenteResultadoPremiado.getLimiteDiarioResultadoTextView().setText(ViewUtils.getMoedaFormat(sessaoUsuario.getParametrosSimulacao().getValorLimiteDiario()));
        contenteResultadoPremiado.getPremioResultadoTextView().setText(ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido()));

        if (parametroSimulacao != null) {
            if (parametroSimulacao.getProximoConcurso() != null) {
                contenteResultadoPremiado.getProximoSorteioTextView().setText(parametroSimulacao.getProximoConcurso().getConcurso().getDataAbertura());
                contenteResultadoPremiado.getAberturaApostaResultadoTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getParametroJogo().getConcurso().getDataAbertura()));
                contenteResultadoPremiado.getEncerramentoResultadoTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getParametroJogo().getConcurso().getDataFechamento()));
            }
        }
        createListPremiacao();
    }

    @Click(R.id.lotericaLayout)
    protected void clickLotericaLayout() {
        alertDialog = LoadingViewLoterias.show(getContext());
        apostaBO.getApostaGerarCodigoResgate(detalhesPremioDTO.getAposta().getId(), new RequestListener<CodigoResgateDTOResponse>() {
            @Override
            public void onResponse(CodigoResgateDTOResponse result) {
                alertDialog.dismiss();
                ResgatePremioLotericaAgenciaActivity_.intent(getContext()).codigoResgateDTO(result.getPayload()).start();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                alertDialog.dismiss();
                String msgError = MensagensNetwork.setMensagem(error, ResultadoApostaConfirmadaActivity.this);
                ViewUtils.alert(getContext(), msgError);
            }
        });

    }

    @Click(R.id.agenciaCaixaLayout)
    protected void clickAgenciaCaixaLayout() {
        ResgatePremioLotericaAgenciaActivity_.intent(this).nsbi(detalhesPremioDTO.getNsbi()).start();
    }

    @Click(R.id.mercadoPagoLayout)
    protected void clickMercadoPagoLayout() {
        ResgatePremioLotericaAgenciaActivity_.intent(this).nsbi(detalhesPremioDTO.getNsbi()).start();
        startActivity( new Intent( ResultadoApostaConfirmadaActivity.this, JogosConfirmadosPremioMercadoPagoActivity.class) );
    }

    private void createListPremiacao() {
        ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView = contenteResultadoPremiado.getListaFaixaPremiacaoExpandableHeightRecyclerView();

        listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(new FaixaPremiacaoAdapter(ViewUtils.getFaixaPremiacaoDTO(detalhesPremioDTO.getPremio())));
        listaFaixaPremiacaoExpandableHeightRecyclerView.setExpanded(Boolean.TRUE);
        listaFaixaPremiacaoExpandableHeightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createToolBar() {
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_resultado_ler_bilhetes)));
    }

    private Context getContext() {
        return ResultadoApostaConfirmadaActivity.this;
    }
}
