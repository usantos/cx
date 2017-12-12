package caixa.gov.br.loterias.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;


@EActivity(R.layout.activity_cadastrar)

public class CadastrarActivity extends CadastroActivity {
    @ViewById
    EditText editTextCpfContent, editTextNomeContent, editTextCepContent, editTextNascimentoContent,
            editTextEmailContent, editTextMunicipioContent, editTextUFContent;
    @ViewById
    Button buttonContinuarIntroducao;

    @Extra
    List<String> listUFString, listMunicipios;

    @AfterViews
    protected void init() {
        super.createToobar();
        addUFSpinner();
    }

    public void addUFSpinner() {

        listUFString = new ArrayList<String>();
        listMunicipios = new ArrayList<String>();

        // configuraNavigation();
        addListenerUF(editTextUFContent, listUFString);
        addListenerMunicipio(editTextMunicipioContent, listMunicipios);
        addListenerContinuar();

        addRightImg((EditText) findViewById(R.id.editTextNomeContent));
        addRightImg((EditText) findViewById(R.id.editTextCepContent));
        addRightImg((EditText) findViewById(R.id.editTextCpfContent));
        addRightImg((EditText) findViewById(R.id.editTextNascimentoContent));
        addRightImg((EditText) findViewById(R.id.editTextEmailContent));
    }


    private void configuraNavigation() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient, null));
    }

    private void addListenerContinuar() {
        buttonContinuarIntroducao.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), CadastrarPasso2Activity_.class);
                startActivity(intent);
            }
        });
    }

    public Dialog onCreateDialogSingleChoice(List<String> listString, String TituloAlert) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(TituloAlert)
                .setSingleChoiceItems(listString.toArray(new String[listString.size()]), 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub

                    }
                })

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }
}
