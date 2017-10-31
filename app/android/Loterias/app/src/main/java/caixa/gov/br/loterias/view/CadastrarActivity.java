package caixa.gov.br.loterias.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class CadastrarActivity extends AppCompatActivity {
    @ViewById
    EditText editTextCpfContent, editTextNomeContent, editTextCepContent, editTextNascimentoContent,
            editTextEmailContent, editTextMunicipioContent, editTextUFContent;
    @ViewById
    Button buttonContinuarIntroducao;



    @Extra
    List<String> listUF, listMunicipios;

    @AfterViews
    protected void init() {
        addUFSpinner();
    }

    public void addUFSpinner() {

        listUF = new ArrayList<String>();
        listUF.add("DF");
        listUF.add("RJ");
        listUF.add("GO");

        listMunicipios = new ArrayList<String>();
        listMunicipios.add("Rio de janeiro");
        listMunicipios.add("Goiania");
        listMunicipios.add("São Paulo");
        listMunicipios.add("Brasília");
        addListenerUF();
        addListenerMunicipio();
        addListenerContinuar();


    }

    private void addListenerUF(){
        editTextUFContent.setClickable(true);
        editTextUFContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // showDialog
                Dialog dialog = onCreateDialogSingleChoice(listUF, "UF");
                dialog.show();
            }
        });
    }

    private void addListenerMunicipio(){
        editTextMunicipioContent.setClickable(true);
        editTextMunicipioContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // showDialog
                Dialog dialog = onCreateDialogSingleChoice(listMunicipios, "Município");
                dialog.show();            }
        });
    }

    private void addListenerContinuar(){
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
