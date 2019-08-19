package br.gov.caixa.loterias.apostar.interno.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.AgrupadorApostaCompraDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoFavoritoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CompraDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.MesAnoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.MesAnoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ResultadoPesquisaPaginadaDTOCompraDTOResponse;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.ListaComprasRecyclerViewAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class ListaComprasActivity extends AppCompatActivity {



    private List<MesAnoDTO> listaMesAnoDTO;
    private List<CompraDTO> listaComprasDto;
    private List<ApostaDTO> listaAposta = new ArrayList<>();
    private CompraDTO compraSelecionada;
    private Boolean isMaisCompras = true, bloquearRequestScroll = false, mantemSurpresinhas = true;
    int offsetItem = 0, mes, ano;
    private Dialog dialogFavoritar;

    private ListaComprasRecyclerViewAdapter listaComprasRecyclerViewAdapter;
    private ListaComprasRecyclerViewAdapter.OnFavoritarClickListener listenerFavoritar;

    private LinearLayoutManager layoutManager;
    private  RecyclerView recyclerViewCompras;
    private ProgressBar progressLoadingMore;
    private NestedScrollView scrollViewContentViewCompras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_compras);
        setaViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        callWebServiceMesCompras();
    }

    private void setaViews(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView textBottomInformacaoTextView = findViewById(R.id.textBottomInformacaoTextView);
        textBottomInformacaoTextView.setText("Compras mais antigas\npodem ser consultadas no site");
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.texto_futura_bold, getString(R.string.title_activity_lista_compras))));
        scrollViewContentViewCompras = findViewById(R.id.scrollViewContentViewCompras);

        progressLoadingMore = findViewById(R.id.compras_loading_more);
        recyclerViewCompras = findViewById(R.id.recyclerCompras);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseManager.registraEvento(this ,"ENTROU_LISTA_COMPRAS");
        setaDialogFavoritar();
    }

    private void setaDialogFavoritar() {
        dialogFavoritar = new Dialog(this);
        dialogFavoritar.setContentView(R.layout.custom_dialog_edit_text);

        TextView tituloDialog = dialogFavoritar.findViewById(R.id.tv_titulo);
        tituloDialog.setText("Carrinho Favorito");

        EditText etCarrinho = dialogFavoritar.findViewById(R.id.et_carrinho);

        Button btnOk = dialogFavoritar.findViewById(R.id.btn_ok);
        Button btnCancelar = dialogFavoritar.findViewById(R.id.btn_cancelar);

        CheckBox ckManterSurpresinha = dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas);
        ckManterSurpresinha.setOnCheckedChangeListener((compoundButton, b) -> mantemSurpresinhas = b);

        btnOk.setOnClickListener(v -> {
            String text  = etCarrinho.getText().toString();
            if(text != null && !text.isEmpty() && compraSelecionada != null && compraSelecionada.getId() != null){
                favoritarCarrinho(text, compraSelecionada.getId());
            }
        });
        btnCancelar.setOnClickListener(v -> dialogFavoritar.dismiss());
        dialogFavoritar.setOnDismissListener(dialogInterface -> {
            dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas).setVisibility(View.GONE);
            dialogFavoritar.findViewById(R.id.tv_manter_surpresinhas).setVisibility(View.GONE);
        });
    }

    private void favoritarCarrinho(String nome, Long idFavorito) {
        DialogUtils.show(ListaComprasActivity.this);
        ApostaBO_.getInstance_(ListaComprasActivity.this).salvarCarrinhoFavorito(nome, idFavorito.toString(), mantemSurpresinhas, new RequestListener<CarrinhoFavoritoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoFavoritoDTOResponse result) {
                DialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), ListaComprasActivity.this);
                }
                ViewUtils.alertTitleButton(ListaComprasActivity.this,
                        R.string.label_atencao, getString(R.string.carrinho_fav_lbl_msg),
                        getString(R.string.ok), dialogInterface -> {
                            dialogFavoritar.dismiss();
                            startActivity(new Intent(ListaComprasActivity.this,CarrinhosFavoritosActivity.class));
                        }
                );
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, ListaComprasActivity.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebServiceMesCompras();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
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

    private void abrirTermosUso(){
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void callWebServiceCompras(){
        offsetItem = 0;
        isMaisCompras = true;
        bloquearRequestScroll = false;
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(ListaComprasActivity.this);

        apostaBO.getCompras(mes, ano, offsetItem ,new RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOCompraDTOResponse response) {
                loadViewProgress.dismiss();
                listaComprasDto = response.getPayload().getLista();
                offsetItem = 5;
                initRecyclerView();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaComprasActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );
            }
        });
    }


    private void callWebServiceMesCompras(){

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(ListaComprasActivity.this);

        apostaBO.getMesesCompras(new RequestListener<MesAnoDTOResponse>() {
            @Override
            public void onResponse(MesAnoDTOResponse response) {
                loadViewProgress.dismiss();
                listaMesAnoDTO = response.getPayload();

                addSpinner();
                mes = listaMesAnoDTO.get(0).getMes().getValor().intValue();
                ano = listaMesAnoDTO.get(0).getAno().intValue();

                callWebServiceCompras();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );

            }
        });
    }

    private void addSpinner(){
        Spinner selecaoMesSpinner;
        selecaoMesSpinner = findViewById(R.id.SelecaoMesSpinner);
        List<String> list = new ArrayList<>();
        for(MesAnoDTO item : listaMesAnoDTO){
            list.add(item.getMes().getDescricao() + " de " + item.getAno());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selecaoMesSpinner.setAdapter(dataAdapter);
        selecaoMesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Your code here
                mes = listaMesAnoDTO.get(i).getMes().getValor().intValue();
                ano = listaMesAnoDTO.get(i).getAno().intValue();
                callWebServiceCompras();

            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });
    }

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        recyclerViewCompras.setHasFixedSize(true);
        ListaComprasRecyclerViewAdapter.OnFavoritarClickListener listener = item -> {
            compraSelecionada = item;
            pegaApostasDaCompra();
        };
        listaComprasRecyclerViewAdapter = new ListaComprasRecyclerViewAdapter(this, listaComprasDto,listener);
        recyclerViewCompras.setAdapter(listaComprasRecyclerViewAdapter);
        recyclerViewCompras.setLayoutManager(layoutManager);


        scrollViewContentViewCompras.setOnScrollChangeListener((NestedScrollView v,int scrollX,int scrollY,int oldScrollX,int oldScrollY) -> {
            View view =  v.getChildAt(v.getChildCount() - 1);
            int diff = (view.getBottom() - (v.getHeight() + v.getScrollY()));

            // if diff is zero, then the bottom has been reached
            if (diff == 0 && !bloquearRequestScroll) {
                // do stuff
                getComprasScroll();
                bloquearRequestScroll = true;
            }
        });

    }

    private void configuraVisibilidadeMantSurpresinha(List<ApostaDTO> apostas){
        for (ApostaDTO aposta: apostas) {
            if(aposta.getIndicadorSurpresinha().getValor() == 2){
                dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas).setVisibility(View.VISIBLE);
                dialogFavoritar.findViewById(R.id.tv_manter_surpresinhas).setVisibility(View.VISIBLE);
                break;
            }
        }
        dialogFavoritar.show();
    }

    private void pegaApostasDaCompra() {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(ListaComprasActivity.this);

        apostaBO.getDetalhesCompras(compraSelecionada.getId().toString(), new RequestListener<AgrupadorApostaCompraDTOResponse>() {
            @Override
            public void onResponse(AgrupadorApostaCompraDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaComprasActivity.this);
                }
                if(response.getPayload().getApostas() != null){
                    configuraVisibilidadeMantSurpresinha(response.getPayload().getApostas());
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect(error, ListaComprasActivity.this);
            }
        });
    }

    private void getComprasScroll(){
        if (!isMaisCompras) {
            return;
        }
        final ApostaBO apostaBO = ApostaBO_.getInstance_(ListaComprasActivity.this);
        progressLoadingMore.setVisibility(View.VISIBLE);

        apostaBO.getCompras(mes, ano, offsetItem, new RequestListener<ResultadoPesquisaPaginadaDTOCompraDTOResponse>() {
            @Override
            public void onResponse(ResultadoPesquisaPaginadaDTOCompraDTOResponse response) {
                progressLoadingMore.setVisibility(View.GONE);
                if(response.getPayload().getLista().isEmpty()){
                    isMaisCompras = false;
                }else{
                    listaComprasDto.addAll(response.getPayload().getLista());
                    listaComprasRecyclerViewAdapter.notifyDataSetChanged();
                    offsetItem = offsetItem +5;
                }
                bloquearRequestScroll = false;
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), ListaComprasActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                progressLoadingMore.setVisibility(View.GONE);
                bloquearRequestScroll = false;
                RedirectNetwork.checkRedirect( error, ListaComprasActivity.this );
            }
        });
    }
}