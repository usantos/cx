package caixa.gov.br.loterias.view;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.ConcursoPremiadoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.FaixaPremiadaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.ParametroSimulacao;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoConsultaBilheteDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoConsultaBilheteDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.SituacaoEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.SituacaoResultadoBilheteEnum;
import caixa.gov.br.loterias.utils.EstiloModalidade;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.FaixaPremiacaoAdapter;
import caixa.gov.br.loterias.view.adapter.RetirarPremioDescricaoAdapter;
import caixa.gov.br.loterias.view.custom.ContenteResultadoPremiado;
import caixa.gov.br.loterias.view.custom.ExpandableHeightRecyclerView;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

@EActivity(R.layout.activity_resultado_ler_bilhetes)
public class ResultadoLerBilhetesActivity extends AppCompatActivity {

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected ScrollView resultadoApostaScrollView;

    @ViewById
    protected LinearLayout corpoBilhetePremiadoLinearLayout, corpoBilheteNaoPremiadoLinearLayout, premioGanhoLinearLayout, mensagemNaoPremiadoLinearLayout;

    @ViewById
    protected TextView mensagemNaoPremiadoTextView, statusResultadoNaoPremiadTextView;

    @ViewById
    protected ExpandableHeightRecyclerView listaComoRetirarPremio;

    @ViewById
    protected ContenteResultadoPremiado contenteResultadoPremiado;

    @Extra
    String codBarras;

    private ResultadoConsultaBilheteDTO resultadoConsultaBilheteDTO;

    @AfterViews
    public void init() {
        resultadoApostaScrollView.fullScroll(View.FOCUS_UP);
        createToolBar();
        postLerBilhetes(ApostaBO_.getInstance_(this), this, codBarras);
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


    private void postLerBilhetes(ApostaBO apostaBO, Activity activity, String CodBarra) {
        final AlertDialog alert = LoadingViewLoterias.show(activity);

        apostaBO.postConferirBilhetes(CodBarra, new RequestListener<ResultadoConsultaBilheteDTOResponse>() {
            @Override
            public void onResponse(ResultadoConsultaBilheteDTOResponse response) {
                alert.dismiss();
                resultadoConsultaBilheteDTO = response.getPayload();
                if (Objects.equals(resultadoConsultaBilheteDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.PREMIADA.getValor())) {
                    atualizarLayoutPremiado();
                } else {
                    atualizarLayoutNaoPremiado();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                alert.dismiss();
                ViewUtils.alert(ResultadoLerBilhetesActivity.this, MensagensNetwork.setMensagem(error, ResultadoLerBilhetesActivity.this));
            }
        });
    }

    private void atualizarLayoutPremiado() {

        corpoBilhetePremiadoLinearLayout.setVisibility(View.VISIBLE);
        corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.GONE);

        ModalidadeEnum modalidadeEnum = ModalidadeEnum.toString(resultadoConsultaBilheteDTO.getModalidadeDTO().getDescricao().toLowerCase());
        EstiloModalidade estiloModalidade = new EstiloModalidade(modalidadeEnum);
        SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
        ParametroSimulacao parametroSimulacao = getParametroJogo(sessaoUsuario, modalidadeEnum);

        contenteResultadoPremiado.getTopoModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorClara()));
        contenteResultadoPremiado.getTopo2ModalidadeResultadoLinearLayout().setBackgroundColor(ContextCompat.getColor(this, estiloModalidade.getCorEscura()));
        contenteResultadoPremiado.getFundoBackgroundTrapezeResultadoLinearLayout().setBackground(ContextCompat.getDrawable(this, estiloModalidade.getTrevoBackground()));
        contenteResultadoPremiado.getTituloModalidadeTextView().setText(resultadoConsultaBilheteDTO.getModalidadeDTO().getDescricao());
        contenteResultadoPremiado.getLimiteDiarioResultadoTextView().setText(ViewUtils.getMoedaFormat(sessaoUsuario.getParametrosSimulacao().getValorLimiteDiario()));
        contenteResultadoPremiado.getPremioResultadoTextView().setText(ViewUtils.getMoedaFormat(resultadoConsultaBilheteDTO.getValorPremio()));

        if (parametroSimulacao != null) {
            if (parametroSimulacao.getProximoConcurso() != null) {
                contenteResultadoPremiado.getProximoSorteioTextView().setText(parametroSimulacao.getProximoConcurso().getConcurso().getDataAbertura());
                contenteResultadoPremiado.getAberturaApostaResultadoTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getParametroJogo().getConcurso().getDataAbertura()));
                contenteResultadoPremiado.getEncerramentoResultadoTextView().setText(ViewUtils.getDateAndHour(parametroSimulacao.getParametroJogo().getConcurso().getDataFechamento()));
            }
        }
        createListPremiacao();
        listaMockDadosComoRetirarPremio();
    }

    private void atualizarLayoutNaoPremiado() {
        corpoBilhetePremiadoLinearLayout.setVisibility(View.GONE);
        corpoBilheteNaoPremiadoLinearLayout.setVisibility(View.VISIBLE);

        if (Objects.equals(resultadoConsultaBilheteDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.NAO_APURADO.getValor())) {
            statusResultadoNaoPremiadTextView.setText(R.string.label_nao_apurado);
        } else if (Objects.equals(resultadoConsultaBilheteDTO.getSituacao().getValor(), SituacaoResultadoBilheteEnum.PRESCRITA.getValor())) {
            statusResultadoNaoPremiadTextView.setText(R.string.label_concurso_prescrito);
        } else {
            statusResultadoNaoPremiadTextView.setText(R.string.label_nao_foi_dessa_vez);
        }
        mensagemNaoPremiadoTextView.setText(R.string.label_mock_texto_nao_premiado);
    }

    private void createToolBar() {
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_resultado_ler_bilhetes)));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void createListPremiacao() {
        ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView = contenteResultadoPremiado.getListaFaixaPremiacaoExpandableHeightRecyclerView();

        listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(new FaixaPremiacaoAdapter(getFaixaPremiacaoDTO()));
        listaFaixaPremiacaoExpandableHeightRecyclerView.setExpanded(Boolean.TRUE);
        listaFaixaPremiacaoExpandableHeightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private List<FaixaPremiadaDTO> getFaixaPremiacaoDTO() {
        List<FaixaPremiadaDTO> faixaPremiadaDTOList = new ArrayList<>();

        if (resultadoConsultaBilheteDTO.getPremioDTO() != null && resultadoConsultaBilheteDTO.getPremioDTO().getConcursos() != null) {
            for (ConcursoPremiadoDTO concurso : resultadoConsultaBilheteDTO.getPremioDTO().getConcursos()) {

                if (concurso.getConcurso() != null && concurso.getFaixas() != null) {
                    for (FaixaPremiadaDTO faixa : concurso.getFaixas()) {
                        faixa.setId(Long.parseLong(concurso.getConcurso().getNumero().toString()));
                        faixaPremiadaDTOList.add(faixa);
                    }
                }
            }
        }
        return faixaPremiadaDTOList;
    }

    //TODO mock de dados, ainda não determinado texto
    private void listaMockDadosComoRetirarPremio() {
        List<String> mockList = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            mockList.add("");
        }

        listaComoRetirarPremio.setAdapter(new RetirarPremioDescricaoAdapter(mockList));
        listaComoRetirarPremio.setExpanded(Boolean.TRUE);
        listaComoRetirarPremio.setLayoutManager(new LinearLayoutManager(this));
    }

    private ParametroSimulacao getParametroJogo(SessaoUsuario sessaoUsuario, ModalidadeEnum modalidadeEnum) {
        if (sessaoUsuario.getParametrosSimulacao() != null && sessaoUsuario.getParametrosSimulacao().getParametros() != null) {
            for (ParametroSimulacao parametroSimulacao : sessaoUsuario.getParametrosSimulacao().getParametros()) {
                if (parametroSimulacao.getParametroJogo().getConcurso().getModalidade() == modalidadeEnum) {
                    return parametroSimulacao;
                }
            }
        }
        return null;
    }
}
