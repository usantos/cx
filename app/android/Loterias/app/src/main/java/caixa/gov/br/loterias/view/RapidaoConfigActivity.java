package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeDTO;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.bo.silce.dto.RapidaoDTO;
import caixa.gov.br.loterias.bo.silce.dto.RapidaoDTOResponse;
import caixa.gov.br.loterias.model.ApostaFavorita;
import caixa.gov.br.loterias.model.TipoAposta;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.ApostaFavoritaAdapter;
import caixa.gov.br.loterias.view.adapter.NumerosRapidaoAdapter;
import caixa.gov.br.loterias.view.adapter.TiposApostasAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;

public class RapidaoConfigActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    ArrayList<TipoAposta> tipoApostaLista = new ArrayList<TipoAposta>();
    ApostaFavorita[] apostaFavoritaLista;
    int[] listaNumerosProibidos = new int[7];
    int[] listaNumerosObrigatorios = new int[12];
    public RapidaoDTO rapidao;
    SeekBar seekBarValorMinimoAposta;
    SeekBar seekBarValorMaximoAposta;
    TextView tituloValorMinimoAposta;
    TextView tituloValorMaximoAposta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapidao_config);
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_lista_rapidao)));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        callWebservice();

    }

    private void gridViewTipoAposta(){
        //mock tipo aposta

//        TipoAposta tipoAposta = new TipoAposta("Mega Sena", R.drawable.trevo, R.color.megasenaclaro, true);
//        TipoAposta tipoAposta2 = new TipoAposta("Quina", R.drawable.trevo, R.color.quinaclaro, true);
//        TipoAposta tipoAposta3 = new TipoAposta("Loteca", R.drawable.trevo, R.color.lotecaclaro, true);
//        tipoApostaLista = new TipoAposta[4];
//        tipoApostaLista[0] = tipoAposta;
//        tipoApostaLista[1] = tipoAposta2;
//        tipoApostaLista[2] = tipoAposta3;
//        tipoApostaLista[3] = tipoAposta4;

        //gridview

        ExpandableHeightGridView gridViewTiposAposta = (ExpandableHeightGridView)findViewById(R.id.gridviewTiposApostasRapidao);
        TiposApostasAdapter tiposApostasAdapter = new TiposApostasAdapter(this, tipoApostaLista);
        gridViewTiposAposta.setAdapter(tiposApostasAdapter);
        gridViewTiposAposta.setExpanded(true);

        gridViewTiposAposta.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == tipoApostaLista.size()){
                    Intent activity = new Intent(RapidaoConfigActivity.this, SelecaoModalidadesRapidaoActivity.class);
                    startActivity(activity);
                }
            }
        });
    }

    private void gridViewNumerosObrigatorios(){
        //mock numeros obrigatorios

        listaNumerosObrigatorios[0] = 1;
        listaNumerosObrigatorios[1] = 2;
        listaNumerosObrigatorios[2] = 3;
        listaNumerosObrigatorios[3] = 4;
        listaNumerosObrigatorios[4] = 10;
        listaNumerosObrigatorios[5] = 11;
        listaNumerosObrigatorios[6] = 12;
        listaNumerosObrigatorios[7] = 13;
        listaNumerosObrigatorios[8] = 14;
        listaNumerosObrigatorios[9] = 30;
        listaNumerosObrigatorios[10] = 34;
        listaNumerosObrigatorios[11] = 100;

        //gridview

        ExpandableHeightGridView gridViewNumerosRapidaoObrigatorios = (ExpandableHeightGridView)findViewById(R.id.gridviewNumerosObrigatoriosRapidao);
        NumerosRapidaoAdapter numerosRapidaoAdapter = new NumerosRapidaoAdapter(this, listaNumerosObrigatorios, true);
        gridViewNumerosRapidaoObrigatorios.setAdapter(numerosRapidaoAdapter);
        gridViewNumerosRapidaoObrigatorios.setExpanded(true);

        gridViewNumerosRapidaoObrigatorios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == listaNumerosObrigatorios.length){
                    Intent activity = new Intent(RapidaoConfigActivity.this, NumerosRapidaoActivity.class);
                    startActivity(activity);
                }
            }
        });
    }

    private void gridViewNumerosProibidos(){

        //mock numeros proibidos
        listaNumerosProibidos[0] = 13;
        listaNumerosProibidos[1] = 66;
        listaNumerosProibidos[2] = 77;
        listaNumerosProibidos[3] = 99;
        listaNumerosProibidos[4] = 23;
        listaNumerosProibidos[5] = 66;
        listaNumerosProibidos[6] = 44;

        //gridview
        ExpandableHeightGridView gridViewNumerosRapidaoProibidos = (ExpandableHeightGridView)findViewById(R.id.gridviewNumerosProibidosRapidao);
        NumerosRapidaoAdapter numerosRapidaoAdapter2 = new NumerosRapidaoAdapter(this, listaNumerosProibidos, false);
        gridViewNumerosRapidaoProibidos.setAdapter(numerosRapidaoAdapter2);
        gridViewNumerosRapidaoProibidos.setExpanded(true);

        gridViewNumerosRapidaoProibidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == listaNumerosProibidos.length){
                    Intent activity = new Intent(RapidaoConfigActivity.this, NumerosRapidaoActivity.class);
                    startActivity(activity);
                }
            }
        });

    }

    private void gridViewApostaFavorita(){

        //mock aposta favorita
        ApostaFavorita apostaFavorita = new ApostaFavorita("Mega Sena", listaNumerosObrigatorios);
        ApostaFavorita apostaFavorita2 = new ApostaFavorita("Sena", listaNumerosProibidos);

        apostaFavoritaLista = new ApostaFavorita[2];
        apostaFavoritaLista[0] = apostaFavorita;
        apostaFavoritaLista[1] = apostaFavorita2;

        //gridview
        ExpandableHeightGridView gridViewApostaFavorita = (ExpandableHeightGridView)findViewById(R.id.gridViewApostaFavorita);
        ApostaFavoritaAdapter apostaFavoritaAdapter = new ApostaFavoritaAdapter(this, apostaFavoritaLista);
        gridViewApostaFavorita.setAdapter(apostaFavoritaAdapter);
        gridViewApostaFavorita.setExpanded(true);
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
        Intent activity = new Intent(RapidaoConfigActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void callWebservice() {

        final View loadViewProgress = LoadingViewLoterias.init(this);
        final ApostaBO apostaBO = new ApostaBO(RapidaoConfigActivity.this);

        apostaBO.getRapidoes(new SilceClient.Listener<RapidaoDTOResponse>() {
            @Override
            public void onResponse(RapidaoDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                rapidao = response.getPayload();


                atualizaLayout();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(RapidaoConfigActivity.this, MensagensNetwork.setMensagem(error, getApplicationContext()));
            }
        });
    }

    private void atualizaLayout(){
        seekBarValorMinimoAposta = (SeekBar) findViewById(R.id.seekBarValorMinimoAposta);
        seekBarValorMinimoAposta.setMax(200);
        seekBarValorMinimoAposta.setOnSeekBarChangeListener(this);

        seekBarValorMaximoAposta = (SeekBar) findViewById(R.id.seekBarValorMaximoAposta);
        seekBarValorMaximoAposta.setMax(200);
        seekBarValorMaximoAposta.setOnSeekBarChangeListener(this);

        tituloValorMinimoAposta = (TextView)findViewById(R.id.tituloValorValorMinimoAPosta);
        tituloValorMaximoAposta = (TextView)findViewById(R.id.tituloValorValorMaximoAposta);

        montarListaEstilo();

        gridViewNumerosObrigatorios();
        gridViewNumerosProibidos();
        gridViewApostaFavorita();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        BigDecimal valorProgress = new BigDecimal(Float.toString((float)progress/2));
        if(seekBar == seekBarValorMinimoAposta){
            tituloValorMinimoAposta.setText(ViewUtils.getMoedaFormat(valorProgress));
        }
        if (seekBar == seekBarValorMaximoAposta){
            tituloValorMaximoAposta.setText(ViewUtils.getMoedaFormat(valorProgress));
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if(seekBar == seekBarValorMinimoAposta){

        }
    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if(seekBar == seekBarValorMinimoAposta) {

        }
    }


    private void montarListaEstilo(){
        List<ModalidadeDTO> modalides = rapidao.getModalidades();
        for (ModalidadeDTO item : modalides) {
            TipoAposta tipoAposta = null;
            switch (ModalidadeEnum.toString(item.getDescricao())) {
                case QUINA:
                    tipoAposta = new TipoAposta("Quina", R.drawable.trevo, R.color.quinaclaro, true);
                    break;
                case LOTECA:
                    tipoAposta = new TipoAposta("Loteca", R.drawable.trevo, R.color.lotecaclaro, true);
                    break;
                case LOTOGOL:
                    tipoAposta = new TipoAposta("Lotogol", R.drawable.trevo, R.color.lotogolclaro, true);
                    break;
                case LOTOFACIL:
                    tipoAposta = new TipoAposta("Lotofácil", R.drawable.trevo, R.color.lotofacilclaro, true);
                    break;
                case LOTOMANIA:
                    tipoAposta = new TipoAposta("Lotomania", R.drawable.trevo, R.color.lotomaniaclaro, true);
                    break;
                case MEGA_SENA:
                    tipoAposta = new TipoAposta("Mega-Sena", R.drawable.trevo, R.color.megasenaclaro, true);
                    break;
                case TIMEMANIA:
                    tipoAposta = new TipoAposta("Timemania", R.drawable.trevo, R.color.timemaniaclaro, true);
                    break;
                case DUPLA_SENA:
                    tipoAposta = new TipoAposta("Dupla-Sena", R.drawable.trevo, R.color.duplasenaclaro, true);
                    break;
                case FEDERAL:
                    tipoAposta = new TipoAposta("Federal", R.drawable.trevo, R.color.federalclaro, true);
                    break;
            }
            if (tipoAposta != null){
                tipoApostaLista.add(tipoAposta);
            }
        }
        gridViewTipoAposta();

    }
}
