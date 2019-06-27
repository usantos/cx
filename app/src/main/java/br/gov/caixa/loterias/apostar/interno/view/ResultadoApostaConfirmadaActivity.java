package br.gov.caixa.loterias.apostar.interno.view;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CodigoResgateDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.FaixaPremiacaoAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ContentResultadoPremiado;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

@EActivity(R.layout.activity_resultado_aposta_confirmada)
public class ResultadoApostaConfirmadaActivity extends SettingToolbarActivity {

    @Bean
    protected ApostaBO apostaBO;

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected ContentResultadoPremiado contenteResultadoPremiado;

    @ViewById
    protected RelativeLayout lotericaLayout, agenciaCaixaLayout, mercadoPagoLayout;

    @Extra
    protected DetalhesPremioDTO detalhesPremioDTO;

    private AlertDialog alertDialog;

    ModalidadeEnum modalidadeEnum;

    @AfterViews
    protected void init() {
        createToolBar();

        modalidadeEnum = detalhesPremioDTO.getAposta().getModalidade();
        EstiloModalidade estiloModalidade = new EstiloModalidade(modalidadeEnum);
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidadeEnum);

        contenteResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorClara()));
        contenteResultadoPremiado.getTopo2ModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
        contenteResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidade.getTrevoBackground()));
        contenteResultadoPremiado.getTituloModalidadeTextView().setText(ModalidadeEnum.fromString(detalhesPremioDTO.getAposta().getModalidade()));
        contenteResultadoPremiado.getPremioResultadoTextView().setText(ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido()));
        contenteResultadoPremiado.getBotaoAposteAgora().setText(ViewUtils.textFuturaAndFuturaBold(this ,getString(R.string.botao_aposte_agora)));

        if (parametroSimulacao != null) {
            contenteResultadoPremiado.getAberturaApostaResultadoTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getParametroJogo().getConcurso().getDataAbertura()));
            contenteResultadoPremiado.getEncerramentoResultadoTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getParametroJogo().getConcurso().getDataFechamento()));

            for (ParametroSimulacao parametro : sessaoUsuario.getParametrosSimulacao().getParametros()) {
                if (parametro.getParametroJogo().getConcurso().getModalidade() == modalidadeEnum && parametro.getProximoConcurso() != null) {
                    parametroSimulacao.setProximoConcurso(parametro.getProximoConcurso());
                }
            }

            if (parametroSimulacao.getProximoConcurso() != null) {
                contenteResultadoPremiado.getProximoSorteioTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getProximoConcurso().getConcurso().getDataFechamento()));
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
                ViewUtils.alertTitleButton(getContext(), R.string.label_atencao, msgError, getContext().getResources().getString( R.string.ok ));
            }
        });

    }

    @Click(R.id.botaoAposteAgora)
    protected void clickBotaoAposteAgora() {

        Intent it = new Intent(ResultadoApostaConfirmadaActivity.this, PrincipalActivity_.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
        it.putExtra("MODALIDADE_APOSTAR_AGORA", modalidadeEnum);
        startActivity(it);
    }

    @Click(R.id.agenciaCaixaLayout)
    protected void clickAgenciaCaixaLayout() {
        ResgatePremioLotericaAgenciaActivity_.intent(this).nsbi(detalhesPremioDTO.getNsbi()).start();
    }

    @Click(R.id.mercadoPagoLayout)
    protected void clickMercadoPagoLayout() {
        Intent intent = new Intent( ResultadoApostaConfirmadaActivity.this, JogosConfirmadosPremioMercadoPagoActivity.class);
        intent.putExtra( "valorPremio", ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido()) );
        intent.putExtra( "idAposta", detalhesPremioDTO.getAposta().getId() );
        startActivity( intent );
    }

    private void createListPremiacao() {
        ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView = contenteResultadoPremiado.getListaFaixaPremiacaoExpandableHeightRecyclerView();

        listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(new FaixaPremiacaoAdapter(ViewUtils.getFaixaPremiacaoDTO(detalhesPremioDTO.getPremio())));
        listaFaixaPremiacaoExpandableHeightRecyclerView.setExpanded(Boolean.TRUE);
        listaFaixaPremiacaoExpandableHeightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void createToolBar() {
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resultado_ler_bilhetes)));
    }

    private Context getContext() {
        return ResultadoApostaConfirmadaActivity.this;
    }
}
