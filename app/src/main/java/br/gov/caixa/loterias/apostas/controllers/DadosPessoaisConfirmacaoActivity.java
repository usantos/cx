package br.gov.caixa.loterias.apostas.controllers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;

import android.view.Gravity;
import android.view.Menu;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class DadosPessoaisConfirmacaoActivity extends LoteriasBaseAppActivity {

    private ImageButton customButton;
    private Toolbar toolbar;
    ImageView iconLoterias;
    ConstraintLayout containerTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dados_pessoais_confirmacao );
        toolbar = findViewById( R.id.toolbar );
        configurarToolbar();

        TextView tituloConfirmacaoCadastroTextView = findViewById( R.id.tituloConfirmacaoCadastroTextView );
        TextView subtituloConfirmaCadastroTextView = findViewById( R.id.subtituloConfirmaCadastroTextView );
        tituloConfirmacaoCadastroTextView.setText( ViewUtils.textFuturaAndFuturaBold( this, getResources().getString(R.string.cadastro_atualizado) ) );
        subtituloConfirmaCadastroTextView.setText( getResources().getString(R.string.aguarde_redirecionamento_tela_inicial) );

        Utils.delay(3, () -> finish());
    }

    private void configurarToolbar() {
        iconLoterias = findViewById(R.id.icon_loterias);
        iconLoterias.setVisibility(VISIBLE);

        customButton = findViewById(R.id.customButton);
        customButton.setVisibility(GONE);

        TextView textCustom = findViewById(R.id.textCustom);

        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) textCustom.getLayoutParams();

        // Centraliza horizontalmente na Toolbar
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;

        // Mantém centralizado verticalmente
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;

        // Remove constraints antigas
        params.startToEnd = ConstraintLayout.LayoutParams.UNSET;
        params.endToStart = ConstraintLayout.LayoutParams.UNSET;

        textCustom.setLayoutParams(params);
        textCustom.setGravity(Gravity.CENTER);
        textCustom.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.dados_pessoais, menu);
        return true;
    }

}
