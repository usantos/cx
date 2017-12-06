package caixa.gov.br.loterias.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.MunicipioDTO;
import caixa.gov.br.loterias.bo.silce.dto.MunicipioDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.UnidadeFederacaoDTO;
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
    private String municipioSelect = "";
    private List<UnidadeFederacaoDTO> listUf;

    @Extra
    List<String> listUFString, listMunicipios;

    @AfterViews
    protected void init() {
        addUFSpinner();
    }

    public void addUFSpinner() {

        listUFString = new ArrayList<String>();

        listMunicipios = new ArrayList<String>();

        configuraNavigation();
        addListenerUF();
        addListenerMunicipio();
        addListenerContinuar();

        addRightImg((EditText)findViewById(R.id.editTextNomeContent));
        addRightImg((EditText)findViewById(R.id.editTextCepContent));
        addRightImg((EditText)findViewById(R.id.editTextCpfContent));
        addRightImg((EditText)findViewById(R.id.editTextNascimentoContent));
        addRightImg((EditText)findViewById(R.id.editTextEmailContent));
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

    private void configuraNavigation(){
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient, null));
    }

    private void addRightImg (EditText textField) {
        textField.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            /* When focus is lost check that the text field
            * has valid values.
            */
                if (!hasFocus) {
                    if (v.getClass() == MaskedEditText.class) {
                        if (((MaskedEditText)v).getUnmaskedText().toString().equals(""))
                        {
                            ((MaskedEditText)v).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        } else {
                            ((MaskedEditText)v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
                        }
                    } else {
                        if (((EditText)v).getText().toString().equals(""))
                        {
                            ((EditText)v).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        } else {
                            ((EditText)v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
                        }
                    }
                }
            }
        });
    }

    private void getUFS(){
        DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(CadastrarActivity.this);

        dadosCorporativosBO.ufs(new SilceClient.Listener<UnidadeFederacaoDTOResponse>() {
            @Override
            public void onResponse(UnidadeFederacaoDTOResponse response) {
                listUf = response.getPayload();
                for (UnidadeFederacaoDTO uf : response.getPayload()) {
                    listUFString.add(uf.getSigla());
                }
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
                android.R.layout.simple_list_item_1, android.R.id.text1, listUFString);

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
                getMunicipios();
            }
        });
    }

    private void getMunicipios(){
        String idUf = "";

        if (ufSelect.isEmpty()) {
            showDialog();
        } else {
            for (UnidadeFederacaoDTO uf : listUf) {
                if (uf.getSigla().equalsIgnoreCase(ufSelect)) {
                    idUf = uf.getId().toString();
                }
            }

            DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(CadastrarActivity.this);
            dadosCorporativosBO.municipios(idUf, new SilceClient.Listener<MunicipioDTOResponse>() {
                @Override
                public void onResponse(MunicipioDTOResponse response) {
                    System.out.print("response: " + response);
                    for (MunicipioDTO municipio : response.getPayload()) {
                        listMunicipios.add(municipio.getNome());
                    }
                    modalMunicipios();
                }
            }, new SilceClient.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    ViewUtils.alert(CadastrarActivity.this, "ERRO");
                }
            });
        }
    }

    private AlertDialog myDialog;

    public void showDialog() {
        if (myDialog == null) {
            myDialog = new AlertDialog.Builder(this)
                    .setTitle("Atenção")
                    .setMessage("Selecione primeiro a UF, assim nossa precisão de municípios será incrível.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myDialog.dismiss();
                        }
                    })
                    .create();
        }
        myDialog.show();
    }

    private void modalMunicipios(){
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button

        final ListView listViewItens = (ListView) dialog.findViewById(R.id.listaItensDialog);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CadastrarActivity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listMunicipios);

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
                municipioSelect = itemValue;
            }

        });

        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = (Button) dialog.findViewById(R.id.dialogButtonCancelar);

        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextMunicipioContent.setText(municipioSelect);
                dialog.dismiss();
            }
        });

        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                municipioSelect = "";
                dialog.dismiss();
            }
        });

        dialog.show();
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
