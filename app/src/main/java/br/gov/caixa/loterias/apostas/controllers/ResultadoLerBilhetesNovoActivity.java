package br.gov.caixa.loterias.apostas.controllers;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ConcursosNovoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.FaixasDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConsultaBilheteDTONovoResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ResultadoConsultaBilheteDTOResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

@EActivity(R.layout.activity_resultado_ler_bilhetes_novo)
public class ResultadoLerBilhetesNovoActivity extends AppCompatActivity {

    private ApostaBO apostaBO = ApostaBO.getInstance();

    @ViewById
    protected Toolbar toolbar;

    @ViewById
    protected ScrollView resultadoApostaNovoScrollView;

    @ViewById
    protected TableLayout tableLayout;

    @ViewById
    protected TextView valorTotalPremiacao, nsbValor;

    @ViewById
    protected ConstraintLayout contentResultadosBilheteResult, contentResultadosBilheteNaoApurado;

    @Extra
    String codBarras;

    @Extra
    String nsb;


    @AfterViews
    public void init() {
        contentResultadosBilheteResult.setVisibility(View.GONE);
        contentResultadosBilheteNaoApurado.setVisibility(View.GONE);
        createToolBar();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        postLerBilhetes();
    }

    private void postLerBilhetes() {
        AlertDialogUtils.show(this);

        //Mockado no ResultadoLerBilheteMock.MOCK
        //ResultadoConsultaBilheteDTONovoResponse response = new Gson().fromJson(ResultadoLerBilheteMock.MOCK, ResultadoConsultaBilheteDTONovoResponse.class);
        //atualizarLayoutPremiado(response);

        apostaBO.postConferirBilhetesNovo(codBarras, new RequestListener<ResultadoConsultaBilheteDTOResponse>() {
            @Override
            public void onResponse(ResultadoConsultaBilheteDTOResponse response) {
                if (response != null && response.getPayload() != null && response.getPayload().getSituacao().getValor() == 100) {
                    //atualizarLayoutPremiado(response);
                } else {
                    //atualizarLayoutNaoPremiado(response);
                }
                AlertDialogUtils.dismiss();
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                RedirectNetwork.checkRedirect( error, ResultadoLerBilhetesNovoActivity.this );
                AlertDialogUtils.dismiss();
            }
        });
    }

    private void atualizarLayoutPremiado(ResultadoConsultaBilheteDTONovoResponse response) {
        contentResultadosBilheteNaoApurado.setVisibility(View.GONE);
        contentResultadosBilheteResult.setVisibility(View.VISIBLE);

        int j = 0;

        j = getListConcursos(response.getPayload().getPremio().getConcursos(), j, response.getPayload().getModalidade().getValor());
        j = getListConcursosNaoPremiados(response.getPayload().getPremio().getConcursosNaoPremiado(), j);

        nsbValor.setText(nsb);
        valorTotalPremiacao.setText(valorTotalPremiacao.getText() + " R$ " + response.getPayload().getPremio().getValorLiquido());
    }

    private void atualizarLayoutNaoPremiado(ResultadoConsultaBilheteDTONovoResponse response){
        contentResultadosBilheteResult.setVisibility(View.GONE);
        contentResultadosBilheteNaoApurado.setVisibility(View.VISIBLE);
        if (response != null && response.getPayload() != null && response.getPayload().getSituacao() != null){
            TextView situacaoTxt = contentResultadosBilheteNaoApurado.findViewById(R.id.situacao_bilhete);
            situacaoTxt.setText(response.getPayload().getSituacao().getDescricao());
        }
    }

    private int getListConcursosNaoPremiados(List<ConcursosNovoDTO> concursosNaoPremiado, int j) {
        TableRow.LayoutParams param = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.MATCH_PARENT,
                0.2f
        );

        TableRow.LayoutParams param2 = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.MATCH_PARENT,
                0.2f
        );


        for (int i = 0; i < concursosNaoPremiado.size(); i++) {
            ConcursosNovoDTO r = concursosNaoPremiado.get(i);

            View tableRow = LayoutInflater.from(this).inflate(R.layout.table_item,null,false);

            TextView concurso_bilhete  = (TextView) tableRow.findViewById(R.id.concurso_bilhete);
            TextView situacao_bilhete  = (TextView) tableRow.findViewById(R.id.situacao_bilhete);
            TextView acertos_bilhete  = (TextView) tableRow.findViewById(R.id.acertos_bilhete);
            TextView qtde_bilhete  = (TextView) tableRow.findViewById(R.id.qtde_bilhete);
            TextView valor_bilhete  = (TextView) tableRow.findViewById(R.id.valor_bilhete);


            concurso_bilhete.setLayoutParams(param2);
            situacao_bilhete.setLayoutParams(param2);

            acertos_bilhete.setLayoutParams(param);
            qtde_bilhete.setLayoutParams(param);
            valor_bilhete.setLayoutParams(param);

            concurso_bilhete.setText(r.getConcurso().getNumero().toString());
            situacao_bilhete.setText(r.getSituacao());
            acertos_bilhete.setText("-");
            qtde_bilhete.setText("-");
            valor_bilhete.setText("-");

            if(j % 2 == 0){
                tableRow.setBackgroundColor(Color.rgb(1, 100, 168));
            }
            j++;

            tableLayout.addView(tableRow);
        }

        return j;
    }

    private int getListConcursos(List<ConcursosNovoDTO> concursos, int j, Integer numeroModalidade) {
        TableRow.LayoutParams param = new TableRow.LayoutParams(
                0,
                120,
                0.2f
        );

        TableRow.LayoutParams param2 = new TableRow.LayoutParams(
                0,
                120,
                0.2f
        );

        for (int i = 0; i < concursos.size(); i++) {
            ConcursosNovoDTO r = concursos.get(i);

            List<FaixasDTO> faixasTemp = new ArrayList<>();

            for(int f = 0; f < r.getFaixas().size(); f++){
                FaixasDTO faixasDTO = r.getFaixas().get(f);

                if(verificaSeFaixaExiste(faixasTemp, faixasDTO)){
                    View tableRow = LayoutInflater.from(this).inflate(R.layout.table_item,null,false);

                    TextView concurso_bilhete  = (TextView) tableRow.findViewById(R.id.concurso_bilhete);
                    TextView situacao_bilhete  = (TextView) tableRow.findViewById(R.id.situacao_bilhete);
                    TextView acertos_bilhete  = (TextView) tableRow.findViewById(R.id.acertos_bilhete);
                    TextView qtde_bilhete  = (TextView) tableRow.findViewById(R.id.qtde_bilhete);
                    TextView valor_bilhete  = (TextView) tableRow.findViewById(R.id.valor_bilhete);

                    concurso_bilhete.setLayoutParams(param2);
                    situacao_bilhete.setLayoutParams(param2);

                    acertos_bilhete.setLayoutParams(param);
                    qtde_bilhete.setLayoutParams(param);
                    valor_bilhete.setLayoutParams(param);

                    concurso_bilhete.setText(r.getConcurso().getNumero().toString());
                    situacao_bilhete.setText(r.getSituacao());

                    if(numeroModalidade != 9){
                        acertos_bilhete.setText("FX " + faixasDTO.getNumero());
                    }else {
                        acertos_bilhete.setText(faixasDTO.getNome() + " -  FX " + faixasDTO.getNumero());
                    }

                    qtde_bilhete.setText(String.valueOf(confereQuantasFaixasIguais(faixasTemp, r.getFaixas(), faixasDTO)));
                    valor_bilhete.setText("R$ " + faixasDTO.getValorLiquido().toString());

                    if(j % 2 == 0){
                        tableRow.setBackgroundColor(Color.rgb(1, 100, 168));
                        concurso_bilhete.setTextColor(getResources().getColor(R.color.branco));
                        situacao_bilhete.setTextColor(getResources().getColor(R.color.branco));
                        acertos_bilhete.setTextColor(getResources().getColor(R.color.branco));
                        qtde_bilhete.setTextColor(getResources().getColor(R.color.branco));
                        valor_bilhete.setTextColor(getResources().getColor(R.color.branco));
                    }
                    j++;

                    tableLayout.addView(tableRow);
                }
            }
        }

        return j;
    }

    private boolean verificaSeFaixaExiste(List<FaixasDTO> faixasTemp, FaixasDTO faixasDTO) {
        if(faixasTemp.isEmpty()){
            return true;
        }

        for (FaixasDTO f :faixasTemp) {
            if(f.getNumero() == faixasDTO.getNumero() && f.getNome().equalsIgnoreCase(faixasDTO.getNome())){
                return false;
            }
        }
        return true;
    }

    private int confereQuantasFaixasIguais(List<FaixasDTO> faixasTemp, List<FaixasDTO> faixas, FaixasDTO faixasDTO) {
        int i = 0;
        for (FaixasDTO f: faixas) {
            if(f.getNumero() == faixasDTO.getNumero() && f.getNome().equalsIgnoreCase(faixasDTO.getNome())){
                faixasTemp.add(f);
                i++;
            }
        }

        return i;
    }

    private void createToolBar() {
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_resultado_ler_bilhetes)));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /*private void createListPremiacao() {
        ExpandableHeightRecyclerView listaFaixaPremiacaoExpandableHeightRecyclerView = contentResultadoPremiado.getListaFaixaPremiacaoExpandableHeightRecyclerView();

        listaFaixaPremiacaoExpandableHeightRecyclerView.setAdapter(new FaixaPremiacaoAdapter(ViewUtils.getFaixaPremiacaoDTO(resultadoConsultaBilheteDTO.getPremioDTO())));
        listaFaixaPremiacaoExpandableHeightRecyclerView.setExpanded(Boolean.TRUE);
        listaFaixaPremiacaoExpandableHeightRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }*/
}
