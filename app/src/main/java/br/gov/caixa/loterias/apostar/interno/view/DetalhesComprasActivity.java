package br.gov.caixa.loterias.apostar.interno.view;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.Gson;

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
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.adapter.ListaComprasDetalhesAdapter;
import br.gov.caixa.loterias.apostar.interno.view.custom.LoadingViewLoterias;

public class DetalhesComprasActivity extends AppCompatActivity {

    private CompraDTO compraDTO;
    private List<ApostaDTO> listaAposta = new ArrayList<>();
    private ArrayList<String> tiposApostas = new ArrayList<>();
    private Dialog dialogFavoritar;

    private Boolean mantemSurpresinhas = true;
    int ordemCompra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_compras);
        setaViews();
    }

    private void setaViews(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.texto_futura_bold, getString(R.string.title_activity_detalhes_compras))));


        String compraDTOJson = getIntent().getExtras().getString("CompraDTO");
        ordemCompra = getIntent().getExtras().getInt("ordem");
        compraDTO = new Gson().fromJson(compraDTOJson, CompraDTO.class);

        criarCabecalho();
        callWebService();
        setaDialogFavoritar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebService();
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



    private void criarCabecalho() {
        FrameLayout setaIndicador = findViewById(R.id.setaComprasGroup);
        setaIndicador.setVisibility(View.INVISIBLE);

        TextView dataCompra = findViewById(R.id.dataCompraTextView);
        dataCompra.setText("Data: " + compraDTO.getDataEfetivacaoDebito());
        TextView valorCompra = findViewById(R.id.valorCompra);
        valorCompra.setText(ViewUtils.textFuturaAndFuturaBold(this, this.getString(R.string.label_compra_lista_valor,
                ViewUtils.getMoedaFormat(compraDTO.getValorTotal()))));

        TextView situacaoCompraTextView = findViewById(R.id.situacaoCompraTextView);
        situacaoCompraTextView.setText(ViewUtils.textFuturaAndFuturaBold(this, this.getString(R.string.label_compra_lista_situacao,
                compraDTO.getSituacao().getDescricao())));
        ImageButton ibFavoritar = findViewById(R.id.ib_favoritar);
        ibFavoritar.setOnClickListener(v -> {
                    configuraVisibilidadeMantSurpresinha(listaAposta);
                });
    }

    private void abrirTermosUso() {
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }


    private void montarLayout() {
        RecyclerView listView = findViewById(R.id.gridviewDetalhesCompras);
        listView.setLayoutManager(new LinearLayoutManager(this));
        ListaComprasDetalhesAdapter listaAdapter = new ListaComprasDetalhesAdapter(this, this, listaAposta);
        listView.setAdapter(listaAdapter);

    }

    private void callWebService() {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final ApostaBO apostaBO = ApostaBO_.getInstance_(DetalhesComprasActivity.this);

        apostaBO.getDetalhesCompras(compraDTO.getId().toString(), new RequestListener<AgrupadorApostaCompraDTOResponse>() {
            @Override
            public void onResponse(AgrupadorApostaCompraDTOResponse response) {
                loadViewProgress.dismiss();
                FirebaseManager.registraEvento(DetalhesComprasActivity.this ,"ENTROU_COMPRAS_DETALHE");

                listaAposta = response.getPayload().getApostas();

                tiposApostas = new ArrayList<>();
                for(ApostaDTO aposta : listaAposta){
                    tiposApostas.add(aposta.getModalidade().toString());
                }

                montarLayout();
                if (response.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(response.getRedirect(), DetalhesComprasActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                montarLayout();
                RedirectNetwork.checkRedirect(error, DetalhesComprasActivity.this);
            }
        });
    }

    private void configuraVisibilidadeMantSurpresinha(List<ApostaDTO> apostas){
        dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas).setVisibility(View.GONE);
        dialogFavoritar.findViewById(R.id.tv_manter_surpresinhas).setVisibility(View.GONE);
        for (ApostaDTO aposta: apostas) {
            if(aposta.getIndicadorSurpresinha().getValor() == 2){
                dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas).setVisibility(View.VISIBLE);
                dialogFavoritar.findViewById(R.id.tv_manter_surpresinhas).setVisibility(View.VISIBLE);
                break;
            }
        }
        dialogFavoritar.show();
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
            if(text != null && !text.isEmpty() && compraDTO != null && compraDTO.getId() != null){
                favoritarCarrinho(text, compraDTO.getId());
            }
        });
        btnCancelar.setOnClickListener(v -> dialogFavoritar.dismiss());
        dialogFavoritar.setOnDismissListener(dialogInterface -> {
            etCarrinho.setText("");
            dialogFavoritar.findViewById(R.id.ck_manter_surpresinhas).setVisibility(View.GONE);
            dialogFavoritar.findViewById(R.id.tv_manter_surpresinhas).setVisibility(View.GONE);
        });
    }

    private void favoritarCarrinho(String nome, Long idFavorito) {
        DialogUtils.show(DetalhesComprasActivity.this);
        ApostaBO_.getInstance_(DetalhesComprasActivity.this).salvarCarrinhoFavorito(nome,idFavorito.toString(),mantemSurpresinhas, new RequestListener<CarrinhoFavoritoDTOResponse>() {
            @Override
            public void onResponse(CarrinhoFavoritoDTOResponse result) {
                DialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso( result.getRedirect(), DetalhesComprasActivity.this);
                }
                ViewUtils.alertTitleButton(DetalhesComprasActivity.this,
                        R.string.label_atencao, getString(R.string.carrinho_fav_lbl_msg),
                        getString(R.string.ok), dialogInterface -> {
                            dialogFavoritar.dismiss();
                            startActivity(new Intent(DetalhesComprasActivity.this,CarrinhosFavoritosActivity.class));
                        }
                );
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, DetalhesComprasActivity.this);
            }
        });
    }

}
