package caixa.gov.br.loterias.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.UnidadeFederacaoDTOResponse;
import caixa.gov.br.loterias.utils.ViewUtils;


@EActivity(R.layout.activity_cadastrar)

public class CadastrarActivity extends AppCompatActivity {
    @ViewById
    EditText editTextCpfContent, editTextNomeContent, editTextCepContent, editTextNascimentoContent,
            editTextEmailContent, editTextMunicipioContent, editTextUFContent;
    @ViewById
    Button buttonContinuarIntroducao;


    final Context context = this;

    private String ufSelect = "";

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

                getUFS();
            }
        });
    }

    private void getUFS(){
        DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(CadastrarActivity.this);

        dadosCorporativosBO.ufs(new SilceClient.Listener<UnidadeFederacaoDTOResponse>() {
            @Override
            public void onResponse(UnidadeFederacaoDTOResponse response) {
                System.out.print("response: " + response);
                modalUfs();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                ViewUtils.alert(CadastrarActivity.this, "ERRO");
            }
        });
    }

    private void modalUfs(){
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button


        final ListView listViewItens = (ListView) dialog.findViewById(R.id.listaItensDialog);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CadastrarActivity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listUF);


        // Assign adapter to ListView
        listViewItens.setAdapter(adapter);
        // ListView Item Click Listener
        listViewItens.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listViewItens.getItemAtPosition(position);
                ufSelect = itemValue;

            }

        });


        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = (Button) dialog.findViewById(R.id.dialogButtonCancelar);

        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextUFContent.setText(ufSelect);
                dialog.dismiss();
            }
        });

        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ufSelect = "";
                dialog.dismiss();
            }
        });

        dialog.show();
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
