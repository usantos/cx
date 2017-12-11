package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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

    @AfterViews
    protected void init() {
        super.createToobar();
        addListenerFinalizar();

    }

    private void addListenerFinalizar(){
        buttonFinalizarCadastro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PrincipalActivity.class);
                startActivity(intent);
            }
        });
    }

}
