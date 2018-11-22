package br.gov.caixa.loterias.apostar.view;

import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.ModalidadeDTOResponse;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostar.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostar.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostar.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.view.adapter.TiposApostasMultiplaSelecaoAdapter;
import br.gov.caixa.loterias.apostar.view.custom.ExpandableHeightGridView;

public class SelecaoModalidadesRapidaoActivity extends AppCompatActivity {

    Button salvarButtonRapidaoSelecionados;

    List<ModalidadeDTO> modalidades = new ArrayList<ModalidadeDTO>();
    List<TipoAposta> tiposAposta = new ArrayList<TipoAposta>();
    List<TipoAposta> tiposApostasSalvas = new ArrayList<TipoAposta>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_modalidades_rapidao);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_lista_rapidao)));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String arrayAsString = getIntent().getExtras().getString("tiposAposta");
        tiposApostasSalvas = Arrays.asList(new Gson().fromJson(arrayAsString, TipoAposta[].class));

        salvarButtonRapidaoSelecionados = (Button) findViewById(R.id.salvarModalidadesApostaRapidaoSelececao);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice();
        }
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

    private void abrirTermosUso() {
        Intent activity = new Intent(SelecaoModalidadesRapidaoActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void gridViewTipoAposta() {
        //mock tipo aposta
//
        //gridview


        ExpandableHeightGridView gridViewTiposAposta = (ExpandableHeightGridView) findViewById(R.id.gridviewTiposApostasRapidaoSelecionar);
        TiposApostasMultiplaSelecaoAdapter tiposApostasAdapter = new TiposApostasMultiplaSelecaoAdapter(this, this, tiposAposta);
        gridViewTiposAposta.setAdapter(tiposApostasAdapter);
        gridViewTiposAposta.setExpanded(true);
    }

    public void mudarLayoutBotaoSalvar(Boolean ativo) {
        if (ativo) {
            salvarButtonRapidaoSelecionados.setBackgroundColor(getResources().getColor(R.color.verdeazul));
            salvarButtonRapidaoSelecionados.setClickable(true);
        } else {
            salvarButtonRapidaoSelecionados.setBackgroundColor(getResources().getColor(R.color.branco));
            salvarButtonRapidaoSelecionados.setClickable(false);
        }
    }


    private void callWebservice() {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(SelecaoModalidadesRapidaoActivity.this);

        apostaBO.getModalidades(new RequestListener<ModalidadeDTOResponse>() {
            @Override
            public void onResponse(ModalidadeDTOResponse response) {
                loadViewProgress.dismiss();
                modalidades = response.getPayload();

                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), SelecaoModalidadesRapidaoActivity.this);
                }
                atualizarLayout();

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, SelecaoModalidadesRapidaoActivity.this);
            }
        });
    }

    private void atualizarLayout() {
        montarArrayTipoApostas();

    }

    private void montarArrayTipoApostas() {
        for (ModalidadeDTO item : modalidades) {
            TipoAposta tipoAposta = null;
            switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao())) {
                case QUINA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_5, R.color.quinaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTECA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_10, R.color.lotecaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOGOL:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_7, R.color.lotogolclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOFACIL:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_6, R.color.lotofacilclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOMANIA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_4, R.color.lotomaniaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case MEGA_SENA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_megasena, R.color.megasenaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case TIMEMANIA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_8, R.color.timemaniaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case DUPLA_SENA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_9, R.color.duplasenaclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case FEDERAL:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.federalclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case DIA_DE_SORTE:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_2, R.color.diasorteclaro, false, item.getValor(), item.getDescricaoEspecial());
                    break;
            }
            if (tipoAposta != null) {
                tipoAposta.setTitulo(tipoAposta.getTitulo().toLowerCase());
                tiposAposta.add(tipoAposta);
            }
        }
        if (tiposAposta != null || tiposAposta.size() > 0) {
            for (TipoAposta tipoAposta : tiposAposta) {
                for (TipoAposta tipoApostaSalva : tiposApostasSalvas) {
                    if (tipoApostaSalva.getTitulo().equals(tipoAposta.getTitulo())) {
                        tipoAposta.setSelect(tipoApostaSalva.getSelect());
                    }
                }
            }
            gridViewTipoAposta();


        }
        final ScrollView scrollModalides = (ScrollView) findViewById(R.id.scrollViewSelecaoModalidadesRapidao);
        scrollModalides.post(new Runnable() {
            public void run() {
                scrollModalides.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }

}
