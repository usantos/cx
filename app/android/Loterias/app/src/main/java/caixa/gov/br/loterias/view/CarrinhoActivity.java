package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.view.adapter.CarrinhoApostasListViewAdapter;
import caixa.gov.br.loterias.utils.ViewUtils;

public class CarrinhoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        montarLayout();
        configuraActivityCarrinho();
        configuraRecyclerViewCarrinhoAposta();
        configuraListeners();

        ViewUtils.organizaTabLayout(this, R.id.carrinhoMainLayout, R.id.apostasListaRecycleView, R.id.carrinhoScrollView);
    }

    private void montarLayout(){
        TextView infoValorMinimoTextView = (TextView) findViewById(R.id.infoValorMinimo);
        infoValorMinimoTextView.setText(ViewUtils.fromHtml("Lembre-se que o valor mínimo do total de compras nesse aplicativo é <strong>R$24,00</strong>"));
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
            abrirTermos();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirTermos(){
        Intent activity = new Intent(CarrinhoActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void configuraActivityCarrinho(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient, null));
    }

    private void configuraListeners(){
        ((Button) findViewById(R.id.botaoAvancarPagamento)).setText(ViewUtils.configuraButtonText(this, getString(R.string.avancarFormaPagamento), 7));


        findViewById(R.id.botaoFazerNovaAposta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(2);

                finish();
            }
        });

        findViewById(R.id.imagemLixeiraCarrinho).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpaCarrinho();
            }
        });

        findViewById(R.id.textoLimparCarrinho).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpaCarrinho();
            }
        });

        findViewById(R.id.botaoAvancarPagamento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(CarrinhoActivity.this, FormaPagamentoActivity.class);

                startActivity(activity);
            }
        });
    }

    private void configuraRecyclerViewCarrinhoAposta(){
        String[] values = new String[] { "01", "02"};

        RecyclerView recyclerView = findViewById(R.id.apostasListaRecycleView);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        recyclerView.setAdapter(new CarrinhoApostasListViewAdapter(montaCarrinhoApostaValores(values), this));
        recyclerView.setLayoutManager(layout);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private ArrayList<String> montaCarrinhoApostaValores(String valores[]){
        ArrayList<String> valoresTextoDialog = new ArrayList<>();

        Collections.addAll(valoresTextoDialog, valores);

        return valoresTextoDialog;
    }

    private void limpaCarrinho(){
        ViewUtils.alert(this, "LIMPAR CARRINHO");
    }

}
