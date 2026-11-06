package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.core.content.res.ResourcesCompat;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;

/**
 * Created by joafilho on 19/12/2017.
 */

public class SettingToolbarActivity extends LoteriasBaseAppActivity {
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
        configuraActivityBack();
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
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }

    protected void configuraActivityBack() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setElevation(0);
        }

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient, null));
    }
}
