package br.gov.caixa.loterias.apostar.interno.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ScrollView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostar.interno.view.adapter.TiposApostasConferirApostaAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.ExpandableHeightGridView;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class LerBilhetesEscolhaJogoActivity extends AppCompatActivity {

    List<ModalidadeDTO> modalidades = new ArrayList<>();
    List<TipoAposta> tiposAposta = new ArrayList<>();

    Button selecionarModalidadesConferirApostaButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ler_bilhetes_escolha_jogo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selecionarModalidadesConferirApostaButton = findViewById(R.id.selecionarModalidadesConferirApostaButton);

        selecionarModalidadesConferirApostaButton.setOnClickListener(view -> {

        });

        callWebservice();

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice();
        }
    }

    private void abrirTermosUso(){
        Intent activity = new Intent(LerBilhetesEscolhaJogoActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void gridViewTipoAposta(){
        //mock tipo aposta
//
        //gridview



        ExpandableHeightGridView gridViewTiposAposta = findViewById(R.id.gridviewTiposApostasConferirAposta);
        TiposApostasConferirApostaAdapter tiposApostasAdapter = new TiposApostasConferirApostaAdapter(LerBilhetesEscolhaJogoActivity.this, this, tiposAposta);
        gridViewTiposAposta.setAdapter(tiposApostasAdapter);
        gridViewTiposAposta.setExpanded(true);
    }

    public void mudarLayoutBotaoSalvar(Boolean ativo) {
        if (ativo) {
            selecionarModalidadesConferirApostaButton.setBackgroundColor(getResources().getColor(R.color.verdeazul));
            selecionarModalidadesConferirApostaButton.setClickable(true);
        }else {
            selecionarModalidadesConferirApostaButton.setBackgroundColor(getResources().getColor(R.color.branco));
            selecionarModalidadesConferirApostaButton.setClickable(false);
        }
    }


    private void callWebservice() {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(this);

        apostaBO.getModalidades(new RequestListener<ModalidadeDTOResponse>() {
            @Override
            public void onResponse(ModalidadeDTOResponse response) {
                loadViewProgress.dismiss();
                modalidades = response.getPayload();
                Log.d("Response", response.getPayload().toString());

                atualizarLayout();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), LerBilhetesEscolhaJogoActivity.this);
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, LerBilhetesEscolhaJogoActivity.this );
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
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.quinaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTECA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.lotecaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOGOL:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.lotogolclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOFACIL:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.lotofacilclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOMANIA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.lotomaniaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case MEGA_SENA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.megasenaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case TIMEMANIA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.timemaniaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case DUPLA_SENA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.duplasenaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case FEDERAL:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.federalclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
            }
            if (tipoAposta != null){
                tiposAposta.add(tipoAposta);
            }
        }
        gridViewTipoAposta();

        final ScrollView scrollModalides = findViewById(R.id.scrollViewSelecaoModalidadesRapidao);
        scrollModalides.post(() -> scrollModalides.fullScroll(ScrollView.FOCUS_UP));
    }

}
