package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.utils.segmentedbutton.SegmentedButton;

@EActivity(R.layout.activity_cadastrar_passo2)
public class CadastrarPasso2Activity extends CadastroActivity {

    @ViewById
    SegmentedButton SegmentedButtonCodigoLoterica;

    @ViewById
    Button buttonFinalizarCadastro;

    @ViewById
    TextView termoPolitica;

    @ViewById
    AppCompatCheckBox aceiteTermo;

    @AfterViews
    protected void init() {
        super.createToobar();
        addListenerFinalizar();
        this.termoPolitica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void addListenerFinalizar() {
        buttonFinalizarCadastro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PrincipalActivity.class);
                startActivity(intent);
            }
        });
    }

}
