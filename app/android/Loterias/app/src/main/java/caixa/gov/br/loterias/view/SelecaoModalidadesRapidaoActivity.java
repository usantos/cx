package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeDTO;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import caixa.gov.br.loterias.model.TipoAposta;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.TiposApostasMultiplaSelecaoAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;

public class SelecaoModalidadesRapidaoActivity extends AppCompatActivity{

    Button salvarButtonRapidaoSelecionados;

    List<ModalidadeDTO> modalidades = new ArrayList<ModalidadeDTO>();
    List<TipoAposta> tiposAposta = new ArrayList<TipoAposta>();
    List<TipoAposta> tiposApostasSalvas = new ArrayList<TipoAposta>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_modalidades_rapidao);
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_lista_rapidao)));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String arrayAsString =  getIntent().getExtras().getString("tiposAposta");
        tiposApostasSalvas = Arrays.asList(new Gson().fromJson(arrayAsString, TipoAposta[].class));

        salvarButtonRapidaoSelecionados = (Button)findViewById(R.id.salvarModalidadesApostaRapidaoSelececao);

        salvarButtonRapidaoSelecionados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                String arrayAsString = new Gson().toJson(tiposAposta);
                intent.putExtra("tiposAposta", arrayAsString);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        callWebservice();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            abrirTermosUso();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirTermosUso(){
        Intent activity = new Intent(SelecaoModalidadesRapidaoActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void gridViewTipoAposta(){
        //mock tipo aposta
//
        //gridview



        ExpandableHeightGridView gridViewTiposAposta = (ExpandableHeightGridView)findViewById(R.id.gridviewTiposApostasRapidaoSelecionar);
        TiposApostasMultiplaSelecaoAdapter tiposApostasAdapter = new TiposApostasMultiplaSelecaoAdapter(this, this, tiposAposta);
        gridViewTiposAposta.setAdapter(tiposApostasAdapter);
        gridViewTiposAposta.setExpanded(true);
    }

    public void mudarLayoutBotaoSalvar(Boolean ativo) {
        if (ativo) {
            salvarButtonRapidaoSelecionados.setBackgroundColor(getResources().getColor(R.color.verdeazul));
            salvarButtonRapidaoSelecionados.setClickable(true);
        }else {
            salvarButtonRapidaoSelecionados.setBackgroundColor(getResources().getColor(R.color.branco));
            salvarButtonRapidaoSelecionados.setClickable(false);
        }
    }


    private void callWebservice() {

        final View loadViewProgress = LoadingViewLoterias.init(this);
        final ApostaBO apostaBO = new ApostaBO(SelecaoModalidadesRapidaoActivity.this);

        apostaBO.getModalidades(new SilceClient.Listener<ModalidadeDTOResponse>() {
            @Override
            public void onResponse(ModalidadeDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                modalidades = response.getPayload();


                atualizarLayout();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(SelecaoModalidadesRapidaoActivity .this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private void atualizarLayout(){
        montarArrayTipoApostas();

    }

    private void montarArrayTipoApostas(){
        for (ModalidadeDTO item : modalidades){
            TipoAposta tipoAposta = null;
            switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao())) {
                case QUINA:
                    tipoAposta = new TipoAposta("Quina", R.drawable.trevo, R.color.quinaclaro, false);
                    break;
                case LOTECA:
                    tipoAposta = new TipoAposta("Loteca", R.drawable.trevo, R.color.lotecaclaro, false);
                    break;
                case LOTOGOL:
                    tipoAposta = new TipoAposta("Lotogol", R.drawable.trevo, R.color.lotogolclaro, false);
                    break;
                case LOTOFACIL:
                    tipoAposta = new TipoAposta("Lotofácil", R.drawable.trevo, R.color.lotofacilclaro, false);
                    break;
                case LOTOMANIA:
                    tipoAposta = new TipoAposta("Lotomania", R.drawable.trevo, R.color.lotomaniaclaro, false);
                    break;
                case MEGA_SENA:
                    tipoAposta = new TipoAposta("Mega-Sena", R.drawable.trevo, R.color.megasenaclaro, false);
                    break;
                case TIMEMANIA:
                    tipoAposta = new TipoAposta("Timemania", R.drawable.trevo, R.color.timemaniaclaro, false);
                    break;
                case DUPLA_SENA:
                    tipoAposta = new TipoAposta("Dupla-Sena", R.drawable.trevo, R.color.duplasenaclaro, false);
                    break;
                case FEDERAL:
                    tipoAposta = new TipoAposta("Federal", R.drawable.trevo, R.color.federalclaro, false);
                    break;
            }
            if (tipoAposta != null){
                tiposAposta.add(tipoAposta);
            }
        }
        if (tiposAposta != null || tiposAposta.size()>0){
            for(TipoAposta tipoAposta : tiposAposta){
               for(TipoAposta tipoApostaSalva : tiposApostasSalvas){
                   if(tipoApostaSalva.getTitulo().equals(tipoAposta.getTitulo())){
                       tipoAposta.setSelect(tipoApostaSalva.getSelect());
                   }
               }
            }
            gridViewTipoAposta();
        }
    }

}
