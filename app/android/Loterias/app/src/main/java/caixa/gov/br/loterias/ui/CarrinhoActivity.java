package caixa.gov.br.loterias.ui;

import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.ui.adapter.CarrinhoApostasListViewAdapter;

public class CarrinhoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        configuraActivityCarrinho();

        String[] values = new String[] { "01", "02", "02", "02", "02", "02", "02", "02", "02", "10" };

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.apostasListaRecycleView);
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

    @Override
    public boolean onSupportNavigateUp(){
        finish();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        return true;
    }

    // Mudando o texto dos MenuItem de acordo com os dados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    private void configuraActivityCarrinho(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        getWindow().setStatusBarColor(getResources().get);
        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient, null));
//        getWindow().getDecorView().setBackground(new ColorDrawable(Color.parseColor(lightColor.toString())));

//        GradientDrawable gd = new GradientDrawable();
//        gd.setColor(Color.parseColor(lightColor.toString()));
//        gd.setCornerRadius(5);
//        gd.setStroke(2, Color.WHITE);
//
//        findViewById(R.id.botaoNovaAposta).setBackground(gd);
    }

}
