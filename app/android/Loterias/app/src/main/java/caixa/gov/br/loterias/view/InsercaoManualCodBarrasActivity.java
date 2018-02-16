package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import caixa.gov.br.loterias.R;

public class InsercaoManualCodBarrasActivity extends AppCompatActivity {

    EditText editTextNumeroBilhete;
    Button buttonContinuarInsercaoManualBilhetes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insercao_manual_cod_barras);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonContinuarInsercaoManualBilhetes = (Button) findViewById(R.id.buttonContinuarInsercaoManualBilhetes);
        editTextNumeroBilhete = (EditText) findViewById(R.id.editTextNumeroBilhete);
        editTextNumeroBilhete.addTextChangedListener(textWatcher);
        buttonContinuarInsercaoManualBilhetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextNumeroBilhete.getText().toString().equals("")){
                    Toast.makeText(InsercaoManualCodBarrasActivity.this, "Informe o código do bilhete!", Toast.LENGTH_SHORT).show();
                }else{
                    Intent activity = new Intent(InsercaoManualCodBarrasActivity.this, ResultadoLerBilhetesActivity.class);
                    activity.putExtra("codigoBarras", editTextNumeroBilhete.getText().toString());
                    startActivity(activity);
                }
            }
        });


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

    private void abrirTermosUso(){
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
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
            if (editTextNumeroBilhete.getText().toString().equals("")){
                buttonContinuarInsercaoManualBilhetes.setEnabled(false);
                buttonContinuarInsercaoManualBilhetes.setTextColor(getResources().getColor(R.color.cinzaDisable));
                buttonContinuarInsercaoManualBilhetes.setBackgroundColor(getResources().getColor(R.color.cinzaclaro));
            }else{
                buttonContinuarInsercaoManualBilhetes.setEnabled(true);
                buttonContinuarInsercaoManualBilhetes.setTextColor(getResources().getColor(R.color.branco));
                buttonContinuarInsercaoManualBilhetes.setBackgroundColor(getResources().getColor(R.color.verdeazul));
            }

        }
    };

}
