package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bean.SessaoUsuario;
import caixa.gov.br.loterias.bo.ApostaBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.AgrupadorDTOApostaDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeDTO;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import caixa.gov.br.loterias.bo.silce.dto.RapidaoDTO;
import caixa.gov.br.loterias.bo.silce.dto.RapidaoDTOResponse;
import caixa.gov.br.loterias.model.TipoAposta;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.ApostaFavoritaAdapter;
import caixa.gov.br.loterias.view.adapter.NumerosRapidaoAdapter;
import caixa.gov.br.loterias.view.adapter.TiposApostasAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;

public class RapidaoConfigActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {


    List<TipoAposta> tipoApostaLista = new ArrayList<TipoAposta>();
    List<ApostaFavoritaDTO> apostaFavoritaLista = new ArrayList<ApostaFavoritaDTO>();
    List<Integer> listaNumerosObrigatorios = new ArrayList<Integer>();
    List<Integer> listaNumerosProibidos = new ArrayList<Integer>();
    public RapidaoDTO rapidao;
    SeekBar seekBarValorMinimoAposta;
    SeekBar seekBarValorMaximoAposta;
    SeekBar seekBarValorPremiumMinimun;
    com.blackcat.currencyedittext.CurrencyEditText tituloValorMinimoAposta;
    com.blackcat.currencyedittext.CurrencyEditText tituloValorMaximoAposta;
    com.blackcat.currencyedittext.CurrencyEditText tituloValorPremiumMinimum;
    ExpandableHeightGridView gridViewNumerosRapidaoProibidos;
    ExpandableHeightGridView gridViewNumerosRapidaoObrigatorios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapidao_config);
        setTitle(ViewUtils.textFuturaAndFuturaBold(this, getString(R.string.title_activity_lista_rapidao)));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        callWebservice();

        Button SalvarApostaRapidaoButton = (Button)findViewById(R.id.SalvarApostaRapidao);
        SalvarApostaRapidaoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               postRapidoesWebservice();
            }
        });



    }


    private void gridViewTipoAposta(){
        //mock tipo aposta

        final ArrayList<TipoAposta> tipoApostaArrayList = new ArrayList<TipoAposta>();
        for(TipoAposta tipoAposta: tipoApostaLista){
            if(tipoAposta.getSelect() == true){
                tipoApostaArrayList.add(tipoAposta);
            }
        }
        //gridview

        ExpandableHeightGridView gridViewTiposAposta = (ExpandableHeightGridView)findViewById(R.id.gridviewTiposApostasRapidao);
        TiposApostasAdapter tiposApostasAdapter = new TiposApostasAdapter(this, tipoApostaArrayList);
        gridViewTiposAposta.setAdapter(tiposApostasAdapter);
        gridViewTiposAposta.setExpanded(true);

        gridViewTiposAposta.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == tipoApostaArrayList.size()){
                    Intent activity = new Intent(RapidaoConfigActivity.this, SelecaoModalidadesRapidaoActivity.class);
                    String arrayAsString = new Gson().toJson(tipoApostaLista);
                    activity.putExtra("tiposAposta", arrayAsString);
                    startActivityForResult(activity, 5);
                }
            }
        });
    }

    private void gridViewNumerosObrigatorios(){

        //gridview

        gridViewNumerosRapidaoObrigatorios = (ExpandableHeightGridView)findViewById(R.id.gridviewNumerosObrigatoriosRapidao);
        NumerosRapidaoAdapter numerosRapidaoAdapter = new NumerosRapidaoAdapter(this, listaNumerosObrigatorios, true);
        gridViewNumerosRapidaoObrigatorios.setAdapter(numerosRapidaoAdapter);
        gridViewNumerosRapidaoObrigatorios.setExpanded(true);

        gridViewNumerosRapidaoObrigatorios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == listaNumerosObrigatorios.size()){
                    Intent activity = new Intent(RapidaoConfigActivity.this, NumerosRapidaoActivity.class);
                    String arrayAsString = new Gson().toJson(listaNumerosObrigatorios);
                    activity.putExtra("numerosobrigatorios", arrayAsString);
                    activity.putExtra("isTipoNrObrigatorios", true);
                    startActivityForResult(activity, 6);
                }
            }
        });
    }

    private void gridViewNumerosProibidos(){

        //gridview
        gridViewNumerosRapidaoProibidos = (ExpandableHeightGridView)findViewById(R.id.gridviewNumerosProibidosRapidao);
        NumerosRapidaoAdapter numerosRapidaoAdapter2 = new NumerosRapidaoAdapter(this, listaNumerosProibidos, false);
        gridViewNumerosRapidaoProibidos.setAdapter(numerosRapidaoAdapter2);
        gridViewNumerosRapidaoProibidos.setExpanded(true);

        gridViewNumerosRapidaoProibidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == listaNumerosProibidos.size()){
                    Intent activity = new Intent(RapidaoConfigActivity.this, NumerosRapidaoActivity.class);
                    String arrayAsString = new Gson().toJson(listaNumerosProibidos);
                    activity.putExtra("numerosProibidos", arrayAsString);
                    activity.putExtra("isTipoNrObrigatorios", false);
                    startActivityForResult(activity, 7);
                }
            }
        });

    }

    private void gridViewApostaFavorita(){

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

                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                String json = gson.toJson(rapidao);
                rapidao = gson.fromJson(json, RapidaoDTO.class);


                listaNumerosObrigatorios = rapidao.getPrognosticosObrigatorios();
                listaNumerosProibidos = rapidao.getPrognosticosProibidos();
                apostaFavoritaLista = rapidao.getApostasFavoritas();


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

        SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
        int valorMinApostaInt = (int) sessaoUser.getValorMinimimoAposta().floatValue();
        int valorMaxApostaInt = (int) sessaoUser.getValorMaximoAposta().floatValue();

        // Valor Minimo Aposta
        tituloValorMinimoAposta = (com.blackcat.currencyedittext.CurrencyEditText) findViewById(R.id.tituloValorValorMinimoAposta);

        TextView tituloValorMinimoApostaValorMinimo = (TextView) findViewById(R.id.tituloValorMinimoApostaValorMinimo);
        TextView tituloValorMinimoApostaValorMaximo = (TextView) findViewById(R.id.tituloValorMinimoApostaValorMaximo);

        seekBarValorMinimoAposta = (SeekBar) findViewById(R.id.seekBarValorMinimoAposta);

        Float valueMinApostaFloat = rapidao.getValorMinimo().floatValue();

        int valueMinApostaInt = Math.round(valueMinApostaFloat);


        seekBarValorMinimoAposta.setProgress((int)Math.round(valueMinApostaInt));
        BigDecimal valorProgressValMin = new BigDecimal(Float.toString((float)valueMinApostaInt));;
        tituloValorMinimoAposta.setText(ViewUtils.getMoedaFormat(valorProgressValMin));
        tituloValorMinimoApostaValorMinimo.setText(sessaoUser.getValorMinimimoAposta() + " Reais");
        tituloValorMinimoApostaValorMaximo.setText(sessaoUser.getValorMaximoAposta() + " Reais");

        seekBarValorMinimoAposta.setOnSeekBarChangeListener(this);
        seekBarValorMinimoAposta.setMax(valorMaxApostaInt);
        seekBarValorMinimoAposta.setMin(valorMinApostaInt);


        // Valor Máximo Aposta
        tituloValorMaximoAposta = (com.blackcat.currencyedittext.CurrencyEditText) findViewById(R.id.tituloValorValorMaximoAposta);

        TextView tituloValorMaximoApostaValorMaximo = (TextView) findViewById(R.id.tituloValorMaximoApostaValorMaximo);
        TextView tituloValorMaximoApostaValorMinimo = (TextView) findViewById(R.id.tituloValorMaximoApostaValorMinimo);


        seekBarValorMaximoAposta = (SeekBar) findViewById(R.id.seekBarValorMaximoAposta);

        Float valueMaxApostaFloat = rapidao.getValorMaximo().floatValue();
        int valueMaxApostaInt = Math.round(valueMaxApostaFloat);
        seekBarValorMaximoAposta.setProgress((int)Math.round(valueMaxApostaInt));
        BigDecimal valorProgressValMax = new BigDecimal(Float.toString((float)valueMaxApostaInt));;
        tituloValorMaximoAposta.setText(ViewUtils.getMoedaFormat(valorProgressValMax));
        tituloValorMaximoApostaValorMinimo.setText(sessaoUser.getValorMinimimoAposta() + " Reais");
        tituloValorMaximoApostaValorMaximo.setText(sessaoUser.getValorMaximoAposta() + " Reais");
        seekBarValorMaximoAposta.setOnSeekBarChangeListener(this);
        seekBarValorMaximoAposta.setMax(valorMaxApostaInt);
        seekBarValorMaximoAposta.setMin(valorMinApostaInt);

        //Premium Minimum
        tituloValorPremiumMinimum = (com.blackcat.currencyedittext.CurrencyEditText)findViewById(R.id.tituloValorPremiumMinimum);

        seekBarValorPremiumMinimun = (SeekBar) findViewById(R.id.seekBarPremiumMimimun);

        Float valuePremMinFloat = rapidao.getValorMinimoPremioPrincipal().floatValue();
        int valuePremMinInt = Math.round(valuePremMinFloat);
        seekBarValorPremiumMinimun.setProgress((int)Math.round(valuePremMinInt));
        BigDecimal valorProgressPremMin = new BigDecimal(Float.toString((float)valuePremMinInt));;
        tituloValorPremiumMinimum.setText(ViewUtils.getMoedaFormat(new BigDecimal(valorProgressPremMin.floatValue()*1000000.0)));

        seekBarValorPremiumMinimun.setOnSeekBarChangeListener(this);
        seekBarValorPremiumMinimun.setMax(100);

        //fim

        montarListaEstilo();
        gridViewNumerosObrigatorios();
        gridViewNumerosProibidos();
        gridViewApostaFavorita();


        final ScrollView scrollConfigRapidao = (ScrollView)findViewById(R.id.scrollViewRapidaoConfig);
        scrollConfigRapidao.post(new Runnable()
        {
            public void run() {
                scrollConfigRapidao.fullScroll(ScrollView.FOCUS_UP);
            }
        });
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if(seekBar == seekBarValorMinimoAposta){
            BigDecimal valorProgress = new BigDecimal(Float.toString((float)progress));
            rapidao.setValorMinimo(valorProgress);
            tituloValorMinimoAposta.setText(ViewUtils.getMoedaFormat(valorProgress));
        }
        if (seekBar == seekBarValorMaximoAposta){
            BigDecimal valorProgress = new BigDecimal(Float.toString((float)progress));
            rapidao.setValorMaximo(valorProgress);
            tituloValorMaximoAposta.setText(ViewUtils.getMoedaFormat(valorProgress));
        }
        if (seekBar == seekBarValorPremiumMinimun){
            int progressFormatMilhoes = progress * 1000000;
            BigDecimal valorProgress = new BigDecimal(Float.toString((float)progressFormatMilhoes));
            rapidao.setValorMinimoPremioPrincipal(valorProgress);
            tituloValorPremiumMinimum.setText(ViewUtils.getMoedaFormat(valorProgress));
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
            switch (ModalidadeEnumSelecaoModalidades.toString(item.getDescricao())) {
                case QUINA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.quinaclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTECA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.lotecaclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOGOL:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.lotogolclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOFACIL:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.lotofacilclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case LOTOMANIA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.lotomaniaclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case MEGA_SENA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.megasenaclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case TIMEMANIA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.timemaniaclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case DUPLA_SENA:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.duplasenaclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
                case FEDERAL:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.federalclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
            }
            if (tipoAposta != null){
                tipoApostaLista.add(tipoAposta);
            }
        }
        gridViewTipoAposta();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 5) {
            String arrayAsString = data.getExtras().getString("tiposAposta");
            tipoApostaLista = Arrays.asList(new Gson().fromJson(arrayAsString, TipoAposta[].class));
            gridViewTipoAposta();
        }

        if (resultCode == RESULT_OK && requestCode == 6) {
            String arrayAsString = data.getExtras().getString("numerosRapidao");
            listaNumerosObrigatorios = Arrays.asList(new Gson().fromJson(arrayAsString, Integer[].class));
            gridViewNumerosObrigatorios();
        }

        if (resultCode == RESULT_OK && requestCode == 7) {
            String arrayAsString = data.getExtras().getString("numerosRapidao");
            listaNumerosProibidos = Arrays.asList(new Gson().fromJson(arrayAsString, Integer[].class));
            gridViewNumerosProibidos();
        }

    }


    private void postRapidoesWebservice() {

        final View loadViewProgress = LoadingViewLoterias.init(this);
        final ApostaBO apostaBO = new ApostaBO(RapidaoConfigActivity.this);


        final List<ModalidadeDTO> tipoApostaArrayList = new ArrayList<ModalidadeDTO>();
        for(TipoAposta tipoAposta: tipoApostaLista){
            if (tipoAposta.getSelect() == true){
                ModalidadeDTO modalidadeDTO = new ModalidadeDTO();
                modalidadeDTO.setDescricao(tipoAposta.getTitulo());
                modalidadeDTO.setDescricaoEspecial(tipoAposta.getDescricaoEspecial());
                modalidadeDTO.setValor(tipoAposta.getValor());
                tipoApostaArrayList.add(modalidadeDTO);
            }
        }
        rapidao.setModalidades(tipoApostaArrayList);
        rapidao.setPrognosticosObrigatorios(listaNumerosObrigatorios);
        rapidao.setPrognosticosProibidos(listaNumerosProibidos);

        apostaBO.postRapidoes(rapidao, new SilceClient.Listener<RapidaoDTOResponse>() {
            @Override
            public void onResponse(RapidaoDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);

            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(RapidaoConfigActivity.this, MensagensNetwork.setMensagem(error, RapidaoConfigActivity.this));
            }
        });
    }
}
