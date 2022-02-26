package br.gov.caixa.loterias.apostas.controllers;

import android.content.Context;
import android.content.Intent;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CodigoResgateDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComprovanteApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DetalhesPremioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.TipoPagamentoDTO;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.FaixaPremiacaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ContentResultadoPremiado;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

@EActivity(R.layout.activity_resultado_aposta_confirmada)
public class ResultadoApostaConfirmadaActivity extends SettingToolbarActivity {

    public static String DETALHES = "DETALHES";
    public static String COMPROVANTE = "COMPROVANTE";
    public static String CODIGO_RESGATE = "CODIGO_RESGATE";
    public static int RESGATOU_MERCADO_PAGO = 200;
    @Bean
    protected ApostaBO apostaBO;

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected ContentResultadoPremiado contentResultadoPremiado;

    @ViewById
    protected RelativeLayout lotericaLayout, agenciaCaixaLayout, mercadoPagoLayout;

    @Extra
    protected DetalhesPremioDTO detalhesPremioDTO;

    private AlertDialog alertDialog;

    ModalidadeEnum modalidadeEnum;

    @Extra
    protected ComprovanteApostaDTO comprovante;

    @AfterViews
    protected void init() {
        createToolBar();

        modalidadeEnum = detalhesPremioDTO.getAposta().getModalidade();
        EstiloModalidade estiloModalidade = new EstiloModalidade(modalidadeEnum);
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidadeEnum);

        contentResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorClara()));

        contentResultadoPremiado.getTopoResultadoPremioLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));

        contentResultadoPremiado.getPremioInfoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));

        contentResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidade.getTrevoBackground()));

        contentResultadoPremiado.getTituloModalidadeTextView().setText(ModalidadeEnum.fromString(detalhesPremioDTO.getAposta().getModalidade()).toLowerCase());

        if (detalhesPremioDTO.getPremio().getValorLiquido() != null && !(BigDecimal.ZERO.compareTo(detalhesPremioDTO.getPremio().getValorLiquido()) == 0)) {
            contentResultadoPremiado.getValorPremioTituloTextView().setText( getResources().getString(R.string.label_voce_foi_premiado).replace(getResources().getString(R.string.chave_valor_premio_chave), ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido())));
        }
        else {
            contentResultadoPremiado.getValorPremioTituloTextView().setText(R.string.label_voce_foi_premiado_exclamacao);
        }

        contentResultadoPremiado.getBotaoAposteAgora().setText(ViewUtils.textFuturaAndFuturaBold(this ,getString(R.string.botao_aposte_agora)));

        if (parametroSimulacao != null) {

            for (ParametroSimulacao parametro : sessaoUsuario.getParametrosSimulacao().getParametros()) {

                if (parametro.getParametroJogo().getConcurso().getModalidade() == modalidadeEnum && parametro.getProximoConcurso() != null) {

                    parametroSimulacao.setProximoConcurso(parametro.getProximoConcurso());
                }
            }

            if (parametroSimulacao.getProximoConcurso() != null) {

                contentResultadoPremiado.getProximoSorteioTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getParametroJogo().getConcurso().getDataFechamento()));

                contentResultadoPremiado.getValorEstimativaConcursoAtualTextView().setText(ViewUtils.getMoedaFormat(parametroSimulacao.getParametroJogo().getConcurso().getEstimativa() ));
            }

        }
        createListPremiacao();
        trataFormasDeResgate();
        RateUtils.avaliarAplicativo(this, true);
        setResult(RESULT_FIRST_USER);
    }

    @Click(R.id.lotericaLayout)
    protected void clickLotericaLayout() {
        apresentaOrientacoesResgate(true);
    }

    protected void apresentaOrientacoesResgate(Boolean isLoterica) {
        alertDialog = LoadingViewLoterias.show(getContext());
        apostaBO.getApostaGerarCodigoResgate(detalhesPremioDTO.getAposta().getId(), new RequestListener<CodigoResgateDTOResponse>() {
            @Override
            public void onResponse(CodigoResgateDTOResponse result) {
                alertDialog.dismiss();
                if(isLoterica){
                    Intent it = new Intent(ResultadoApostaConfirmadaActivity.this, ResgatePremioLotericaActivity.class);
                    it.putExtra(DETALHES, detalhesPremioDTO);
                    it.putExtra(COMPROVANTE, comprovante);
                    it.putExtra(CODIGO_RESGATE, result.getPayload());
                    startActivity(new Intent(it));
                }else {
                    ResgatePremioLotericaAgenciaActivity_.intent(getContext()).codigoResgateDTO(result.getPayload()).isLoterica(isLoterica).detalhesPremioDTO(detalhesPremioDTO).comprovante(comprovante).start();
                }

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
        it.putExtra(getResources().getString(R.string.extra_modalidade_apostar_agora), modalidadeEnum);
        startActivity(it);
    }

    @Click(R.id.agenciaCaixaLayout)
    protected void clickAgenciaCaixaLayout() {
        apresentaOrientacoesResgate(false);
    }

    @Click(R.id.mercadoPagoLayout)
    protected void clickMercadoPagoLayout() {
        Intent intent = new Intent( ResultadoApostaConfirmadaActivity.this, JogosConfirmadosPremioMercadoPagoActivity.class);
        intent.putExtra( getResources().getString(R.string.extra_valor_premio), ViewUtils.getMoedaFormat(detalhesPremioDTO.getPremio().getValorLiquido()) );
        intent.putExtra( getResources().getString(R.string.extra_id_aposta), detalhesPremioDTO.getAposta().getId() );
        startActivityForResult(intent,RESGATOU_MERCADO_PAGO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESGATOU_MERCADO_PAGO && resultCode == RESULT_OK){
            setResult(RESULT_OK);
            finish();
        }
    }

    private void trataFormasDeResgate() {
        for (TipoPagamentoDTO meioPagamento: detalhesPremioDTO.getMeiosResgate()) {
            switch (meioPagamento.getCanal()) {
                case SIGEL:
                    agenciaCaixaLayout.setVisibility(View.VISIBLE);
                    break;
                case SILCE:
                    mercadoPagoLayout.setVisibility(View.VISIBLE);
                    break;
                case SISPL:
                    lotericaLayout.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private void createListPremiacao() {
        ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView = contentResultadoPremiado.getListaFaixaPremiacaoExpandableHeightRecyclerView();

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
