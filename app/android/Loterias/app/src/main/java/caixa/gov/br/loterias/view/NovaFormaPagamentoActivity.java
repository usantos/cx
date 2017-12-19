package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.pinball83.maskededittext.MaskedEditText;

import java.util.ArrayList;
import java.util.Collections;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.view.adapter.NovaFormaPagamentoBandeiraAdapter;
import caixa.gov.br.loterias.utils.ViewUtils;

public class NovaFormaPagamentoActivity extends SettingToolbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ViewUtils.configuraStatusBarGradientLayout(this, getWindow());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nova_forma_pagamento);

        String[] values = new String[]{"01", "02", "02", "02", "02", "02", "02", "02", "02"};

        RecyclerView recyclerView = findViewById(R.id.novaFormaPagamentoRecyclerView);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);


        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(new NovaFormaPagamentoBandeiraAdapter(montaCarrinhoApostaValores(values), this));

        configuraBotoesListener();

        addRightImg((EditText) findViewById(R.id.editTextNumeroCartaoContent));
        addRightImg((EditText) findViewById(R.id.editTextNomeClienteCartaoContent));
        addRightImg((EditText) findViewById(R.id.editTextCodigoSegurancaContent));
    }

    private void addRightImg(EditText textField) {
        textField.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            /* When focus is lost check that the text field
            * has valid values.
            */
                if (!hasFocus) {
                    if (v.getClass() == MaskedEditText.class) {
                        if (((MaskedEditText) v).getUnmaskedText().toString().equals("")) {
                            ((MaskedEditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        } else {
                            ((MaskedEditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
                        }
                    } else {
                        if (((EditText) v).getText().toString().equals("")) {
                            ((EditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        } else {
                            ((EditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
                        }
                    }
                }
            }
        });
    }

    private ArrayList<String> montaCarrinhoApostaValores(String valores[]) {
        ArrayList<String> valoresTextoDialog = new ArrayList<>();

        Collections.addAll(valoresTextoDialog, valores);

        return valoresTextoDialog;
    }

    private void configuraBotoesListener() {
        ((Button) findViewById(R.id.botaoAvancarPagamento)).setText(ViewUtils.configuraButtonText(this, getString(R.string.apostarAutorizarCobranca), 7));

        findViewById(R.id.botaoAvancarPagamento).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(NovaFormaPagamentoActivity.this, ConfirmacaoPagamentoActivity.class);
                startActivity(activity);
            }
        });
    }

}
