package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.listener.OnSilceListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import br.gov.caixa.loterias.apostas.model.model.ModalidadeModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightGridView;

public class SelecaoModalidadesRapidaoActivity extends LoteriasBaseAppActivity {

    Button salvarButtonRapidaoSelecionados;

    List<ModalidadeDTO> modalidades = new ArrayList<>();
    List<TipoAposta> tiposAposta = new ArrayList<>();
    List<TipoAposta> tiposApostasSalvas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_modalidades_rapidao);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_lista_rapidao)));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String arrayAsString = getIntent().getExtras().getString(getResources().getString(R.string.extra_tipos_aposta));
        tiposApostasSalvas = Arrays.asList(new Gson().fromJson(arrayAsString, TipoAposta[].class));

        salvarButtonRapidaoSelecionados = findViewById(R.id.salvarModalidadesApostaRapidaoSelececao);

        salvarButtonRapidaoSelecionados.setOnClickListener(view -> {
            Intent intent = new Intent();

            String arrayAsString1 = new Gson().toJson(tiposAposta);
            intent.putExtra(getResources().getString(R.string.extra_tipos_aposta), arrayAsString1);
            setResult(RESULT_OK, intent);
            finish();
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


        ExpandableHeightGridView gridViewTiposAposta = findViewById(R.id.gridviewTiposApostasRapidaoSelecionar);
//        TiposApostasMultiplaSelecaoAdapter tiposApostasAdapter = new TiposApostasMultiplaSelecaoAdapter(this, this, tiposAposta);
//        gridViewTiposAposta.setAdapter(tiposApostasAdapter);
//        gridViewTiposAposta.setExpanded(true);
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
        AlertDialogUtils.show(this);
        new ModalidadeModel(this).buscaModalidades(new OnSilceListener<List<ModalidadeDTO>>() {
            @Override
            public void success(List<ModalidadeDTO> payload) {
                AlertDialogUtils.dismiss();
                modalidades = payload;

                atualizarLayout();
            }

            @Override
            public void error(VolleyError error) {
                AlertDialogUtils.dismiss();
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
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_5, R.color.quina_claro_mkp, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOFACIL:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_6, R.color.lotofacil_claro_mkp, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOMANIA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_4, R.color.lotomania_claro_mkp, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case MEGA_SENA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_megasena, R.color.mega_verde_claro_mkp, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case TIMEMANIA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_8, R.color.timemania_claro_mkp, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case DUPLA_SENA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_9, R.color.dupla_sena_claro_mkp, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                case DIA_DE_SORTE:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo_2, R.color.dia_sorte_claro_mkp, false, item.getValor(), item.getDescricaoEspecial());
                    break;
                    default:
                        break;
            }
            if (tipoAposta != null) {
                tipoAposta.setTitulo(tipoAposta.getTitulo().toLowerCase(new Locale(getResources().getString(R.string.pt), getResources().getString(R.string.br))));
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
        final ScrollView scrollModalides = findViewById(R.id.scrollViewSelecaoModalidadesRapidao);
        scrollModalides.post(() -> scrollModalides.fullScroll(ScrollView.FOCUS_UP));
    }

}
