package caixa.gov.br.loterias.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.ColorEnum;
import caixa.gov.br.loterias.utils.TipoApostaEnum;
import caixa.gov.br.loterias.utils.ViewUtils;

import static caixa.gov.br.loterias.utils.ColorEnum.DUPLASENACLARO;
import static caixa.gov.br.loterias.utils.ColorEnum.DUPLASENAESCURO;
import static caixa.gov.br.loterias.utils.ColorEnum.LOTOFACILCLARO;
import static caixa.gov.br.loterias.utils.ColorEnum.LOTOFACILESCURO;
import static caixa.gov.br.loterias.utils.ColorEnum.MEGASENACLARO;
import static caixa.gov.br.loterias.utils.ColorEnum.MEGASENAESCURO;
import static caixa.gov.br.loterias.utils.ColorEnum.QUINACLARO;
import static caixa.gov.br.loterias.utils.ColorEnum.QUINAESCURO;

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
        TipoApostaEnum tipoJogo = (TipoApostaEnum) getIntent().getSerializableExtra("tipoAposta");

        switch (tipoJogo){
            case MEGASENA:
                configuraLayoutTela("Mega Sena", MEGASENACLARO, MEGASENAESCURO);
                break;
            case QUINA:
                configuraLayoutTela("Quina", QUINACLARO, QUINAESCURO);
                break;
            case LOTOFACIL:
                configuraLayoutTela("Lotofácil", LOTOFACILCLARO, LOTOFACILESCURO);
                break;
            case DUPLASENA:
                configuraLayoutTela("Duplasena", DUPLASENACLARO, DUPLASENAESCURO);
        }

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        findViewById(R.id.botaoNovaAposta).setOnClickListener(this);
        findViewById(R.id.botaoVerCarrinhoAposta).setOnClickListener(this);
    }

    private void configuraLayoutTela(String titleActivity, ColorEnum lightColor, ColorEnum darkColor){
        setTitle(titleActivity);

        getWindow().setStatusBarColor(Color.parseColor(darkColor.toString()));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(lightColor.toString())));
        getWindow().getDecorView().setBackground(new ColorDrawable(Color.parseColor(lightColor.toString())));

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(lightColor.toString()));
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
//                startActivity(activity);
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
