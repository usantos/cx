package br.gov.caixa.loterias.apostar.interno.view;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.android.volley.VolleyError;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoConcursoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;
import static br.gov.caixa.loterias.apostar.interno.SelecaoModalidadesResultados.MODALIDADE;

public class VisualizarResultadosActivity extends AppCompatActivity {

    private String modalidade;
    private int concursoAtual;
    private int concursoAlvo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_resultados);
        getExtras();
        setViews();
    }

    private  void getExtras(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            modalidade = bundle.getString(MODALIDADE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setViews(){
        if(modalidade != null){
            concursoAtual = ViewUtils.getConcursoAtualPorModalidade(modalidade);
            concursoAlvo = concursoAtual - 1;
            getResutadoModalidade(ApostaDTO.lowerCaseFromString(ModalidadeEnum.toString(modalidade)), concursoAlvo);
            setTitlePorConcurso(concursoAlvo);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setTitlePorConcurso(int concurso){
        setTitle(getString(R.string.resultados_titulo,String.valueOf(concurso)));
    }
    private void getResutadoModalidade(String modalidade, int concurso){
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(this);
        apostaBO.getResultadoModalidadeConcurso(modalidade,concurso, new RequestListener<ResultadoConcursoDTOResponse>() {
            @Override
            public void onResponse(ResultadoConcursoDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), VisualizarResultadosActivity.this);
                }
                response.getPayload();
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, VisualizarResultadosActivity.this);
            }
        });
    }
}