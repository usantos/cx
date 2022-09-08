package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
import java.util.Objects;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroSimulacao;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConsultaBilheteDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.StatusBilhete;
import br.gov.caixa.loterias.apostas.model.enums.LeituraBilheteEnum;
import br.gov.caixa.loterias.apostas.model.model.ConferirBilheteModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.RateUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.FaixaPremiacaoAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ContentResultadoPremiado;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;

@EActivity(R.layout.activity_resultado_ler_bilhetes)
public class ResultadoLerBilhetesActivity extends AppCompatActivity {

    @Bean
    protected ApostaBO apostaBO;

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected ScrollView resultadoApostaScrollView;

    @ViewById
    protected LinearLayout corpoBilhetePremiadoLinearLayout, corpoBilheteNaoPremiadoLinearLayout, premioGanhoLinearLayout, mensagemNaoPremiadoLinearLayout,
            informacoesPremioZeradoLayout, informacoesPremioLayout, backgroundTopoNaoPremiadoLayout;

    @ViewById
    protected TextView mensagemNaoPremiadoTextView, statusResultadoNaoPremiadTextView;

    @ViewById
    protected ContentResultadoPremiado contentResultadoPremiado;

    @Extra
    protected String codBarras;

    @Extra
    protected LeituraBilheteEnum tipoLeitura;

    @Extra
    protected String nsb;

    private ResultadoConsultaBilheteDTO resultadoConsultaBilheteDTO;
    private ModalidadeEnum modalidadeEnum;

    private ConferirBilheteModel model;
    private EstiloModalidade estiloModalidade;

    @AfterViews
    public void init() {
        contentResultadoPremiado.getPremioGanhoLinearLayout().setVisibility(View.GONE);
        contentResultadoPremiado.getButtonVerDetalheAposta().setVisibility(View.GONE);
        createToolBar();
        model = new ConferirBilheteModel(ResultadoLerBilhetesActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        postLerBilhetes();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    // Mudando o texto dos MenuItem de acordo com os dados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            abrirTermosUso();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirTermosUso() {
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void postLerBilhetes() {
        AlertDialogUtils.show(this);
        switch (tipoLeitura){
            case CODIGO_BARRAS:
                conferirCodBarras();
                break;
            case QR_CODE:
                conferirQrCode();
                break;
        }
    }

    private void conferirQrCode() {
        model.conferirBilheteQrCode(codBarras, new OnSilceListener<ResultadoConsultaBilheteDTO>() {
            @Override
            public void success(ResultadoConsultaBilheteDTO payload) {
                resultadoConsultaBilheteDTO = payload;
                if (payload != null && payload.getSituacao().getValor() == 100) {
                    atualizarLayoutPremiado();
                } else {
                    atualizarLayoutNaoPremiado();
                }
                AlertDialogUtils.dismiss();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    private void conferirCodBarras() {
        model.conferirBilhete(codBarras, new OnSilceListener<ResultadoConsultaBilheteDTO>() {
            @Override
            public void success(ResultadoConsultaBilheteDTO payload) {
                AlertDialogUtils.dismiss();
                resultadoConsultaBilheteDTO = payload;
                if (Objects.equals(resultadoConsultaBilheteDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.PREMIADA.getValor())) {
                    atualizarLayoutPremiado();
                    RateUtils.avaliarAplicativo(ResultadoLerBilhetesActivity.this, true);
                } else {
                    atualizarLayoutNaoPremiado();
                }
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
            }
        });
    }

    private void atualizarLayoutPremiado() {
        corpoBilhetePremiadoLinearLayout.setVisibility(View.VISIBLE);
        corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.GONE);

        modalidadeEnum = ModalidadeEnum.fromInteger( resultadoConsultaBilheteDTO.getModalidadeDTO().getValor());
        estiloModalidade = new EstiloModalidade(modalidadeEnum);
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametroSimulacao = ViewUtils.getParametroSimulacao(sessaoUsuario, modalidadeEnum);

        if (modalidadeEnum == ModalidadeEnum.MAIS_MILIONARIA){
            contentResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackground(getDrawable(R.drawable.header_interno__milionaria));
            contentResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setVisibility(View.GONE);
            contentResultadoPremiado.getTituloModalidadeTextView().setVisibility(View.GONE);
            contentResultadoPremiado.getPremioGanhoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, R.color.mais_milionaria_clara));
            contentResultadoPremiado.getTopoResultadoPremioLinearLayout().setBackgroundColor(ContextCompat.getColor(this, R.color.mais_milionaria_clara));
        } else {
            contentResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorClara()));
            contentResultadoPremiado.getPremioGanhoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
            contentResultadoPremiado.getTopoResultadoPremioLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
            contentResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidade.getTrevoBackground()));
        }

        if (modalidadeEnum == ModalidadeEnum.MAIS_MILIONARIA){
            contentResultadoPremiado.getTituloModalidadeTextView().setText(getResources().getString(R.string.label_mais_milionaria_acentuado));
        } else {
            contentResultadoPremiado.getTituloModalidadeTextView().setText(resultadoConsultaBilheteDTO.getModalidadeDTO().getDescricao().toLowerCase());
        }

        contentResultadoPremiado.getBotaoAposteAgora().setText(ViewUtils.textFuturaAndFuturaBold(this ,getString(R.string.botao_aposte_agora)));

        if (resultadoConsultaBilheteDTO.getPremioDTO().getValorLiquido() != null && !(BigDecimal.ZERO.compareTo(resultadoConsultaBilheteDTO.getPremioDTO().getValorLiquido()) == 0)) {
            informacoesPremioZeradoLayout.setVisibility(View.GONE);
            informacoesPremioLayout.setVisibility(View.VISIBLE);

            String label = getResources().getString(R.string.label_voce_foi_premiado).replace(getResources().getString(R.string.chave_valor_premio_chave), ViewUtils.getMoedaFormat(resultadoConsultaBilheteDTO.getPremioDTO().getValorLiquido()));
            contentResultadoPremiado.getValorPremioTituloTextView().setText(label);

            contentResultadoPremiado.getPremioGanhoLinearLayout().setVisibility(View.VISIBLE);
            createListPremiacao();
        }
        else {
            String label = getResources().getString(R.string.label_voce_foi_premiado_exclamacao);
            contentResultadoPremiado.getValorPremioTituloTextView().setText(label);
        }

        checkNsb();

        if (parametroSimulacao != null) {
            if (parametroSimulacao.getProximoConcurso() != null) {
                contentResultadoPremiado.getProximoSorteioTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getProximoConcurso().getConcurso().getDataAbertura()));
                contentResultadoPremiado.getValorEstimativaConcursoAtualTextView().setText(ViewUtils.getMoedaFormat(parametroSimulacao.getParametroJogo().getConcurso().getEstimativa() ));
            }
        }
    }

    private void checkNsb() {
        if (nsb != null && !nsb.isEmpty()){
            contentResultadoPremiado.getNSBTextView().setVisibility(View.GONE);
            contentResultadoPremiado.getNSBTextView().setText("\nNSB\n" + nsb);
        } else {
            contentResultadoPremiado.getNSBTextView().setVisibility(View.GONE);
        }
    }

    @Click(R.id.botaoAposteAgora)
    protected void clickBotaoAposteAgora() {
        Intent it = new Intent(ResultadoLerBilhetesActivity.this, PrincipalActivity_.class);
        it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
        it.putExtra(getResources().getString(R.string.extra_modalidade_apostar_agora), modalidadeEnum);
        startActivity(it);
    }

    private void atualizarLayoutNaoPremiado() {
        corpoBilhetePremiadoLinearLayout.setVisibility(View.GONE);
        corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.VISIBLE);
        int status = -1;
        if(resultadoConsultaBilheteDTO != null &&
                resultadoConsultaBilheteDTO.getSituacao() != null &&
                resultadoConsultaBilheteDTO.getSituacao().getValor() != null) {
            status  = resultadoConsultaBilheteDTO.getSituacao().getValor().intValue();
        }
        switch (status){
                case StatusBilhete.PRESCRITO:
                    statusResultadoNaoPremiadTextView.setText(R.string.label_concurso_prescrito);
                    break;
                case StatusBilhete.PREMIO_PAGO:
                    statusResultadoNaoPremiadTextView.setText(R.string.label_premio_pago);
                    break;
                case StatusBilhete.NAO_PREMIADO:
                case StatusBilhete.NAO_APURADO:
                    statusResultadoNaoPremiadTextView.setText(R.string.label_nao_foi_dessa_vez);
                    break;
                    default:
                        statusResultadoNaoPremiadTextView.setText(R.string.string_vazia);
        }
    }

    private void createToolBar() {
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resultado_ler_bilhetes)));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void createListPremiacao() {
        ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView = contentResultadoPremiado.getListaFaixaPremiacaoExpandableHeightRecyclerView();

        listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(new FaixaPremiacaoAdapter(ViewUtils.getFaixaPremiacaoDTO(resultadoConsultaBilheteDTO.getPremioDTO()),
                                                                                             ContextCompat.getColor(this, estiloModalidade.getCorClara()),
                                                                                             ContextCompat.getColor(this, estiloModalidade.getCorEscura())));
        listaFaixaPremiacaoExpandableHeightRecyclerView.setExpanded(Boolean.TRUE);
        listaFaixaPremiacaoExpandableHeightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
