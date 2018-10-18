package caixa.gov.br.loterias.view;

import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bean.SessaoUsuario;
import caixa.gov.br.loterias.model.bo.ApostaBO;
import caixa.gov.br.loterias.model.bo.ApostaBO_;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostaFavoritaDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.ModalidadeEnumSelecaoModalidades;
import caixa.gov.br.loterias.model.bo.silce.dto.RapidaoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.RapidaoDTOResponse;
import caixa.gov.br.loterias.model.bean.TipoAposta;
import caixa.gov.br.loterias.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse;
import caixa.gov.br.loterias.model.sp.RapidaoSP;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.view.adapter.ApostaFavoritaAdapter;
import caixa.gov.br.loterias.view.adapter.NumerosRapidaoAdapter;
import caixa.gov.br.loterias.view.adapter.TiposApostasAdapter;
import caixa.gov.br.loterias.view.custom.ExpandableHeightGridView;

public class RapidaoConfigActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    AlertDialog loadViewProgress;
    List<ApostaFavoritaDTO> favoritosSelecionados;
    List<ApostaFavoritaDTO> favoritosSalvos;
    List<TipoAposta> tipoApostaLista = new ArrayList<TipoAposta>();
    List<ApostaFavoritaDTO> apostaFavoritaConfiguradasLista = new ArrayList<ApostaFavoritaDTO>();
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

    int valueMinApostaInt;
    int valorMinApostaCarrinho;
    int valorMaxApostaCarrinho;
    private boolean primeiroCarregamento = true;

    TextView tituloValorMinimoApostaValorMaximo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rapidao_config);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_lista_rapidao)));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {//Caso ocorra o erro para buscar informações dos valores na sessão do usuário
            SessaoUsuario sessaoUser = SessaoUsuario.getInstance();
            valorMinApostaCarrinho = (int) sessaoUser.getValorMinimimoAposta().floatValue();
            valorMaxApostaCarrinho = (int) sessaoUser.getValorMaximoAposta().floatValue();
        }catch (Exception e){

        }

        callWebservice();

        Button SalvarApostaRapidaoButton = (Button)findViewById(R.id.SalvarApostaRapidao);
        SalvarApostaRapidaoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               postRapidoesWebservice();
            }
        });


        final ScrollView scrollConfigRapidao = (ScrollView)findViewById(R.id.scrollViewRapidaoConfig);
        scrollConfigRapidao.setFocusableInTouchMode(true);
        scrollConfigRapidao.setDescendantFocusability( ViewGroup.FOCUS_BEFORE_DESCENDANTS);
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
                    arrayAsString = new Gson().toJson(listaNumerosProibidos);
                    activity.putExtra("numerosContrarios", arrayAsString);
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
                    arrayAsString = new Gson().toJson(listaNumerosObrigatorios);
                    activity.putExtra("numerosContrarios", arrayAsString);
                    activity.putExtra("isTipoNrObrigatorios", false);
                    startActivityForResult(activity, 7);
                }
            }
        });

    }

    private void gridViewApostaFavorita(){
        //gridview
        ExpandableHeightGridView gridViewApostaFavorita = (ExpandableHeightGridView)findViewById(R.id.gridViewApostaFavorita);
        ApostaFavoritaAdapter apostaFavoritaAdapter = new ApostaFavoritaAdapter(this, apostaFavoritaConfiguradasLista, favoritosSalvos);
        gridViewApostaFavorita.setAdapter(apostaFavoritaAdapter);
        gridViewApostaFavorita.setExpanded(true);

        favoritosSelecionados = new ArrayList<>();
        apostaFavoritaAdapter.setOnFavorito( new ApostaFavoritaAdapter.Favorito() {
            @Override
            public void onRemover(ApostaFavoritaDTO aposta) {
                if(favoritosSelecionados.contains(aposta)){
                    favoritosSelecionados.remove(aposta);
                }
            }

            @Override
            public void onIncluso(ApostaFavoritaDTO aposta) {
                if(!favoritosSelecionados.contains(aposta)){
                    favoritosSelecionados.add(aposta);
                }
            }
        });
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

        loadViewProgress = LoadingViewLoterias.show(this);
        ApostaBO apostaBO = ApostaBO_.getInstance_(RapidaoConfigActivity.this);

        apostaBO.getRapidoes(new RequestListener<RapidaoDTOResponse>() {
            @Override
            public void onResponse(final RapidaoDTOResponse response) {
                rapidao = response.getPayload();
                carregaApostaFavoritasServer(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, RapidaoConfigActivity.this );
            }
        });
    }

    private void carregaApostaFavoritasServer(final RapidaoDTOResponse response) {
        ApostaBO apostaBO = ApostaBO_.getInstance_(RapidaoConfigActivity.this);
        apostaBO.getApostasFavoritas(0, new RequestListener<ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse>() {

            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOApostaFavoritaDTOResponse result) {
                favoritosSalvos = result.getPayload().getLista();
                loadViewProgress.dismiss();
                processarRegistro(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("s", "");
                favoritosSalvos = new ArrayList<>();
                loadViewProgress.dismiss();
                processarRegistro(response);
            }
        });
    }

    private void processarRegistro(RapidaoDTOResponse response){
        //                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//                String json = gson.toJson(rapidao);
//                rapidao = gson.fromJson(json, RapidaoDTO.class);
        if (rapidao.getValorMaximo() == null){
            rapidao.setValorMaximo( SessaoUsuario.getInstance().parametrosSimulacao.getValorLimiteDiario() );
        }
        if (rapidao.getValorMinimo() == null){
            rapidao.setValorMinimo( SessaoUsuario.getInstance().parametrosSimulacao.getValorMinimoCarrinho() );
        }
        if (rapidao.getValorMinimoPremioPrincipal() == null){
            rapidao.setValorMinimoPremioPrincipal( new BigDecimal("0.0") );
        }
        if (rapidao.getApostasFavoritas()==null){
            rapidao.setApostasFavoritas( new ArrayList<ApostaFavoritaDTO>(  ) );
        }

        if (rapidao.getModalidades()==null){
            rapidao.setModalidades( new ArrayList<ModalidadeDTO>(  ) );
        }


        if (rapidao.getPrognosticosObrigatorios() != null){
            listaNumerosObrigatorios = rapidao.getPrognosticosObrigatorios();
        }
        if(rapidao.getPrognosticosProibidos() != null){
            listaNumerosProibidos = rapidao.getPrognosticosProibidos();
        }
        apostaFavoritaConfiguradasLista = rapidao.getApostasFavoritas();

        atualizaLayout();
        if (response.getRedirect() != null){
            RedirectNetwork.checkRedirectSucesso( response.getRedirect(), RapidaoConfigActivity.this);
        }
    }

    private void atualizaLayout(){

        tituloValorMinimoAposta = (com.blackcat.currencyedittext.CurrencyEditText) findViewById(R.id.tituloValorValorMinimoAposta);
        tituloValorMinimoAposta.addTextChangedListener(textWatcher);

        tituloValorMaximoAposta = (com.blackcat.currencyedittext.CurrencyEditText) findViewById(R.id.tituloValorValorMaximoAposta);
        tituloValorMaximoAposta.addTextChangedListener(textWatcher);

        tituloValorPremiumMinimum = (com.blackcat.currencyedittext.CurrencyEditText)findViewById(R.id.tituloValorPremiumMinimum);
        tituloValorPremiumMinimum.addTextChangedListener(textWatcher);

        // Valor Minimo Aposta

        float valor = 1000000;
        if(rapidao.getValorMaximo() != null){
            valor = rapidao.getValorMaximo().floatValue();
        }
        int valueMaxApostaInt = Math.round(valor);

        TextView tituloValorMinimoApostaValorMinimo = (TextView) findViewById(R.id.tituloValorMinimoApostaValorMinimo);
        tituloValorMinimoApostaValorMaximo = (TextView) findViewById(R.id.tituloValorMinimoApostaValorMaximo);

        seekBarValorMinimoAposta = (SeekBar) findViewById(R.id.seekBarValorMinimoAposta);


        valueMinApostaInt = Math.round(rapidao.getValorMinimo().floatValue());


        seekBarValorMinimoAposta.setProgress((int)Math.round(valueMinApostaInt));
        BigDecimal valorProgressValMin = new BigDecimal(Float.toString((float)valueMinApostaInt));;
        tituloValorMinimoAposta.setText(ViewUtils.getMoedaFormat(valorProgressValMin));
        tituloValorMinimoApostaValorMinimo.setText(ViewUtils.getMoedaFormat(new BigDecimal(Float.toString((float)valorMinApostaCarrinho))));
        tituloValorMinimoApostaValorMaximo.setText(ViewUtils.getMoedaFormat(new BigDecimal(Float.toString((float)valueMaxApostaInt))));

        seekBarValorMinimoAposta.setOnSeekBarChangeListener(this);
        seekBarValorMinimoAposta.setMax(valueMaxApostaInt);

        Button limparValorMinimoAposta = (Button) findViewById(R.id.limparValorMinimoAposta);
        limparValorMinimoAposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBarValorMinimoAposta.setProgress(0);
            }
        });


        // Valor Máximo Aposta


        TextView tituloValorMaximoApostaValorMaximo = (TextView) findViewById(R.id.tituloValorMaximoApostaValorMaximo);
        TextView tituloValorMaximoApostaValorMinimo = (TextView) findViewById(R.id.tituloValorMaximoApostaValorMinimo);


        seekBarValorMaximoAposta = (SeekBar) findViewById(R.id.seekBarValorMaximoAposta);


        seekBarValorMaximoAposta.setProgress((int)Math.round(valueMaxApostaInt));
        BigDecimal valorProgressValMax = new BigDecimal(Float.toString((float)valueMaxApostaInt));;
        tituloValorMaximoAposta.setText(ViewUtils.getMoedaFormat(valorProgressValMax));
        tituloValorMaximoApostaValorMinimo.setText(ViewUtils.getMoedaFormat(new BigDecimal(Float.toString((float)valorMinApostaCarrinho))));
        tituloValorMaximoApostaValorMaximo.setText(ViewUtils.getMoedaFormat(new BigDecimal(Float.toString((float)valorMaxApostaCarrinho))));
        seekBarValorMaximoAposta.setOnSeekBarChangeListener(this);
        seekBarValorMaximoAposta.setMax(valorMaxApostaCarrinho);
        seekBarValorMaximoAposta.setProgress((int)Math.round(valueMaxApostaInt));


        Button limparValorMaximoAposta = (Button) findViewById(R.id.limparValorMaximoAposta);
        limparValorMaximoAposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBarValorMaximoAposta.setProgress(0);
            }
        });

        //Premium Minimum

        seekBarValorPremiumMinimun = (SeekBar) findViewById(R.id.seekBarPremiumMimimun);

        int valuePremMinInt = Math.round(rapidao.getValorMinimoPremioPrincipal().floatValue());
        int valorSalvo = RapidaoSP.obterValorPremioMinimoSalvo((int)Math.round(valuePremMinInt));
        seekBarValorPremiumMinimun.setProgress(valorSalvo);
        BigDecimal valorProgressPremMin = new BigDecimal(Float.toString((float)valuePremMinInt));
        tituloValorPremiumMinimum.setText(ViewUtils.getMoedaFormat(new BigDecimal(valorSalvo*1000000.0)));

        seekBarValorPremiumMinimun.setOnSeekBarChangeListener(this);
        seekBarValorPremiumMinimun.setMax(100);

        Button limparPremioMinimoAposta = (Button) findViewById(R.id.limparPremioMinimoAposta);
        limparPremioMinimoAposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBarValorPremiumMinimun.setProgress(1);
            }
        });

        //fim

        montarListaEstilo();
        gridViewNumerosObrigatorios();
        gridViewNumerosProibidos();
        gridViewApostaFavorita();



    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        int progressSet = progress;
        if(seekBar == seekBarValorMinimoAposta){
            if (progressSet < valorMinApostaCarrinho){
                progressSet = valorMinApostaCarrinho;
            }
            BigDecimal valorProgress = new BigDecimal(Float.toString((float)progressSet));
            rapidao.setValorMinimo(valorProgress);
            tituloValorMinimoAposta.setText(ViewUtils.getMoedaFormat(valorProgress));

        }
         if (seekBar == seekBarValorMaximoAposta){
             if (progressSet < valorMinApostaCarrinho){
                 progressSet = valorMinApostaCarrinho;
             }
             BigDecimal valorProgress = new BigDecimal(Float.toString((float)progressSet));
             rapidao.setValorMaximo(valorProgress);
             tituloValorMaximoAposta.setText(ViewUtils.getMoedaFormat(valorProgress));
        }
        if (seekBar == seekBarValorPremiumMinimun){
            if (progressSet < 1 ){
                progressSet = 1;
            }
            int progressFormatMilhoes = progressSet * 1000000;
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
            switch (ModalidadeEnum.fromInteger( item.getValor() )) {
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
                case DIA_DE_SORTE:
                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.diasorteclaro, true, item.getValor(), item.getDescricaoEspecial());
                    break;
//                case FEDERAL:
//                    tipoAposta = new TipoAposta(item.getDescricao(), R.drawable.trevo, R.color.federalclaro, true, item.getValor(), item.getDescricaoEspecial());
//                    break;
            }
            if (tipoAposta != null){
                tipoAposta.setTitulo(tipoAposta.getTitulo().toLowerCase());
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

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(RapidaoConfigActivity.this);


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
        rapidao.setApostasFavoritas(favoritosSelecionados);

        apostaBO.postRapidoes(rapidao, new RequestListener<RapidaoDTOResponse>() {
            @Override
            public void onResponse(RapidaoDTOResponse response) {
                loadViewProgress.dismiss();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, RapidaoConfigActivity.this );
            }
        });
    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //after text changed
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            String valorSetado = s.toString();
            valorSetado= valorSetado.replace("R$", "");
            valorSetado= valorSetado.replace("$", "");
            valorSetado= valorSetado.replace(".", "");
            valorSetado = valorSetado.replace(",",".");

            if (tituloValorMinimoAposta.getText().hashCode() == s.hashCode())
            {
                seekBarValorMinimoAposta.setProgress((int)Math.round(Float.parseFloat(valorSetado)));

            }
            else if (tituloValorMaximoAposta.getText().hashCode() == s.hashCode())
            {
                seekBarValorMaximoAposta.setProgress((int)Math.round(Float.parseFloat(valorSetado)));
                seekBarValorMinimoAposta.setMax((int)Math.round(Float.parseFloat(valorSetado)));
                tituloValorMinimoApostaValorMaximo.setText(ViewUtils.getMoedaFormat(new BigDecimal(Float.toString((float)(int)Math.round(Float.parseFloat(valorSetado))))));

            }else if (tituloValorPremiumMinimum.getText().hashCode() == s.hashCode()) {
                if(!primeiroCarregamento) {
                    int valor = (int) Math.round(Float.parseFloat(valorSetado) / 1000000);
                    Log.i("caixa", "aaa => " + valor);
                    RapidaoSP.salvarValorPremioMinimo(valor);
                    seekBarValorPremiumMinimun.setProgress(valor);
                }
                primeiroCarregamento = false;
            }
        }
    };
}
