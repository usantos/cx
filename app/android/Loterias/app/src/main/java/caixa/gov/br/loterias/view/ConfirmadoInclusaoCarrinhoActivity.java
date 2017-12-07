package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.TipoModalidadeEnum;
import caixa.gov.br.loterias.utils.ViewUtils;

public class ConfirmadoInclusaoCarrinhoActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmado_inclusao_carrinho);

        invalidateOptionsMenu();
        configuraActivityIncluidoApostaCarrinho();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.itens_simular_actionbar, menu);

        return true;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();

        return true;
    }

    private void configuraActivityIncluidoApostaCarrinho(){
        TipoModalidadeEnum tipoJogo = (TipoModalidadeEnum) getIntent().getSerializableExtra("tipoAposta");

        switch (tipoJogo){
            case MEGASENA:
                configuraLayoutTela("Mega Sena", R.color.megasenaclaro, R.color.megasenaescuro);
                break;
            case QUINA:
                configuraLayoutTela("Quina", R.color.quinaclaro, R.color.quinaescuro);
                break;
            case LOTOFACIL:
                configuraLayoutTela("Lotofácil", R.color.lotofacilclaro, R.color.lotofacilescuro);
                break;
            case DUPLASENA:
                configuraLayoutTela("Duplasena", R.color.duplasenaclaro, R.color.duplasenaescuro);
        }

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.botaoNovaAposta).setOnClickListener(this);
        findViewById(R.id.botaoVerCarrinhoAposta).setOnClickListener(this);
        findViewById(R.id.carrinhoApostas).setOnClickListener(this);

    }

    private void configuraLayoutTela(String titleActivity, int lightColor, int darkColor){
        setTitle(titleActivity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, darkColor));
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, lightColor)));
        getWindow().getDecorView().setBackground(new ColorDrawable(ContextCompat.getColor(this, lightColor)));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(ContextCompat.getColor(this, lightColor));
        gd.setCornerRadius(5);
        gd.setStroke(2, Color.WHITE);

        findViewById(R.id.botaoNovaAposta).setBackground(gd);

        ((Button) findViewById(R.id.botaoVerCarrinhoAposta)).setText(ViewUtils.configuraButtonText(this, getString(R.string.verCarrinho), 12));
        ((Button) findViewById(R.id.botaoAdicionarCompletarCartela)).setText(ViewUtils.configuraButtonText(this, getString(R.string.adicionarAoCarrinho), 9));

    }

    // Mudando o texto dos MenuItem de acordo com os dados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        configuraMenuItem(R.id.concursoAtual);
        configuraMenuItem(R.id.sorteioAtual);

        return true;
    }

    private void configuraMenuItem(int id){
        View menuItem = findViewById(id);

        if (menuItem != null && menuItem instanceof TextView) {
            ((TextView) menuItem).setTextSize(10);
            menuItem.setPadding(5, 5, 5, 5);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.botaoNovaAposta:{
                finish();
            }

            break;

            case R.id.botaoVerCarrinhoAposta:{
                Intent activity = new Intent(this, CarrinhoActivity.class);

                startActivityForResult(activity, 0);
            }

            case R.id.carrinhoApostas: {
                Intent activity = new Intent(this, CarrinhoActivity.class);

                startActivityForResult(activity, 0);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2){
            finish();
        }
    }
}
