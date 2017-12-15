package caixa.gov.br.loterias.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.ApostadorDTO;
import caixa.gov.br.loterias.bo.silce.dto.BairroDTO;
import caixa.gov.br.loterias.bo.silce.dto.BairroDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.Municipio;
import caixa.gov.br.loterias.bo.silce.dto.MunicipioDTO;
import caixa.gov.br.loterias.bo.silce.dto.MunicipioDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.MunicipioIdDTO;
import caixa.gov.br.loterias.bo.silce.dto.UnidadeFederacaoDTO;
import caixa.gov.br.loterias.bo.silce.dto.UnidadeFederacaoDTOResponse;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.ViewUtils;

/**
 * Created by joafilho on 06/12/2017.
 */

public class CadastroActivity extends AppCompatActivity {

    private EditText editTextUFContent, editTextMunicipioContent, editTextBairroContent;
    private List<String> listUFString, listMunicipios, listBairros;
    private List<MunicipioDTO> municipioDTOList;
    protected List<UnidadeFederacaoDTO> listUf;
    protected String ufSelect = "";
    protected String municipioSelect = "";
    protected String bairroSelected = "";
    protected static ApostadorDTO apostadorDTO;
    private boolean isMunicipioUsuario;
    protected MunicipioIdDTO municipioIdDTOLoteria;

    protected void createToobar() {
        setTitle(R.string.title_activity_toolbar);


        if (getSupportActionBar() != null) {

            ImageView imageView = new ImageView(getSupportActionBar().getThemedContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            ActionBar.LayoutParams paramsImage = new ActionBar.LayoutParams(
                    48,
                    48, Gravity.RIGHT
                    | Gravity.CENTER_VERTICAL);
            paramsImage.rightMargin = 40;
            imageView.setLayoutParams(paramsImage);
            imageView.setBackgroundResource(R.drawable.icon_loteria);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setCustomView(imageView);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable((R.drawable.background_dialogtitle_gradient_color)));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    protected void addRightImg(EditText textField) {
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

    protected void addListenerUF(EditText editTextUF, List<String> listUFString) {
        this.listUFString = listUFString;
        editTextUFContent = editTextUF;
        editTextUFContent.setClickable(true);
        editTextUFContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getUFS();
            }
        });
    }

    private void getUFS() {
        final View loadViewProgress = LoadingViewLoterias.init(this);
        DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(CadastroActivity.this);

        dadosCorporativosBO.ufs(new SilceClient.Listener<UnidadeFederacaoDTOResponse>() {
            @Override
            public void onResponse(UnidadeFederacaoDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                listUf = response.getPayload();
                for (UnidadeFederacaoDTO uf : response.getPayload()) {
                    listUFString.add(uf.getSigla());
                }
                modalUfs();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(CadastroActivity.this, "ERRO");
            }
        });
    }

    private void modalUfs() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);
        dialog.setTitle("UF");

        // set the custom dialog components - listview and button

        final ListView listViewItens = (ListView) dialog.findViewById(R.id.listaItensDialog);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CadastroActivity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listUFString);

        // Assign adapter to ListView
        listViewItens.setAdapter(adapter);
        // ListView Item Click Listener
        listViewItens.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listViewItens.getItemAtPosition(position);
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

    protected void addListenerMunicipio(EditText editTextMunicipio, List<String> listMunicipios, boolean isMunicipioUsuario) {
        editTextMunicipioContent = editTextMunicipio;
        this.listMunicipios = listMunicipios;
        this.isMunicipioUsuario = isMunicipioUsuario;
        editTextMunicipioContent.setClickable(true);
        editTextMunicipioContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getMunicipios();
            }
        });
    }

    private void getMunicipios() {
        String idUf = "";

        if (ufSelect.isEmpty()) {
            showDialog();
        } else {
            for (UnidadeFederacaoDTO uf : listUf) {
                if (uf.getSigla().equalsIgnoreCase(ufSelect)) {
                    idUf = uf.getId().toString();
                }
            }
            final View loadViewProgress = LoadingViewLoterias.init(this);
            listMunicipios = new ArrayList<String>();

            DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(CadastroActivity.this);
            dadosCorporativosBO.municipios(idUf, new SilceClient.Listener<MunicipioDTOResponse>() {
                @Override
                public void onResponse(MunicipioDTOResponse response) {
                    LoadingViewLoterias.stop(loadViewProgress);
                    municipioDTOList = response.getPayload();
                    for (MunicipioDTO municipio : municipioDTOList) {
                        listMunicipios.add(municipio.getNome());
                    }
                    modalMunicipios();
                }
            }, new SilceClient.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    LoadingViewLoterias.stop(loadViewProgress);
                    ViewUtils.alert(CadastroActivity.this, "ERRO");
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

    private void modalMunicipios() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button

        final ListView listViewItens = (ListView) dialog.findViewById(R.id.listaItensDialog);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CadastroActivity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listMunicipios);

        // Assign adapter to ListView
        listViewItens.setAdapter(adapter);
        // ListView Item Click Listener
        listViewItens.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) listViewItens.getItemAtPosition(position);
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
                if (isMunicipioUsuario) {
                    for (MunicipioDTO municipio : municipioDTOList) {
                        if (municipio.getNome().equals(municipioSelect)) {
                            apostadorDTO.setMunicipioId(municipio.getId());
                            break;
                        }
                    }
                } else {
                    for (MunicipioDTO municipio : municipioDTOList) {
                        if (municipio.getNome().equals(municipioSelect)) {
                            municipioIdDTOLoteria = municipio.getId();
                            break;
                        }
                    }
                }
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
}
