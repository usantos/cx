package br.gov.caixa.loterias.apostas.controllers;

import android.app.Dialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioIdDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTOResponse;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

/**
 * Created by joafilho on 06/12/2017.
 */

public class CadastroActivity extends AppCompatActivity {

    private EditText editTextUFContent, editTextMunicipioContent;
    private List<String> listUFString, listMunicipios;
    private List<MunicipioDTO> municipioDTOList;
    protected List<UnidadeFederacaoDTO> listUf;
    protected String ufSelect;
    protected String municipioSelect;
    protected String bairroSelected;
    protected static CadastrarApostadorDTO cadastrarApostadorDTO;
    private boolean isMunicipioUsuario;
    protected MunicipioIdDTO municipioIdDTOLoteria;

    protected void createToolbar() {
        setTitle(ViewUtils.textFuturaAndFuturaBoldTitle(this, getString(R.string.title_activity_toolbar)));

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
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable((R.drawable.background_gradient_verde)));
        }
        ufSelect = getResources().getString(R.string.string_vazia);
        municipioSelect = getResources().getString(R.string.string_vazia);
        bairroSelected = getResources().getString(R.string.string_vazia);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    protected void addRightImg(EditText textField) {
        textField.setOnFocusChangeListener((v, hasFocus) -> {
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
        });
    }

    protected void addListenerUF(EditText editTextUF, List<String> listUFString) {
        this.listUFString = listUFString;
        editTextUFContent = editTextUF;
        editTextUFContent.setClickable(true);
        editTextUFContent.setOnClickListener(v -> getUFS());
    }

    private void getUFS() {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(CadastroActivity.this);

        dadosCorporativosBO.ufs(new RequestListener<UnidadeFederacaoDTOResponse>() {
            @Override
            public void onResponse(UnidadeFederacaoDTOResponse response) {
                loadViewProgress.dismiss();
                listUf = response.getPayload();
                for (UnidadeFederacaoDTO uf : response.getPayload()) {
                    listUFString.add(uf.getSigla());
                }
                modalUfs();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), CadastroActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, CadastroActivity.this );
            }
        });
    }

    private void modalUfs() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button

        final ListView listViewItens = dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText(getResources().getString(R.string.uf));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(CadastroActivity.this,
                R.layout.item_text_popup, R.id.textList, listUFString);

        // Assign adapter to ListView
        listViewItens.setAdapter(adapter);
        // ListView Item Click Listener
        listViewItens.setOnItemClickListener((parent, view, position, id) -> {
            // ListView Clicked item index
            int itemPosition = position;

            // ListView Clicked item value
            String itemValue = (String) listViewItens.getItemAtPosition(position);
            ufSelect = itemValue;
        });

        Button dialogButtonOk = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = dialog.findViewById(R.id.dialogButtonCancelar);

        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(v -> {
            editTextUFContent.setText(ufSelect);
            dialog.dismiss();
        });

        dialogButtonCancelar.setOnClickListener(v -> {
            ufSelect = getResources().getString(R.string.string_vazia);
            dialog.dismiss();
        });

        dialog.show();
    }

    protected void addListenerMunicipio(EditText editTextMunicipio, List<String> listMunicipios, boolean isMunicipioUsuario) {
        editTextMunicipioContent = editTextMunicipio;
        this.listMunicipios = listMunicipios;
        this.isMunicipioUsuario = isMunicipioUsuario;
        editTextMunicipioContent.setClickable(true);
        editTextMunicipioContent.setOnClickListener(v -> getMunicipios());
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
            final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
            listMunicipios = new ArrayList<>();

            DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(CadastroActivity.this);
            dadosCorporativosBO.municipios(idUf, new RequestListener<MunicipioDTOResponse>() {
                @Override
                public void onResponse(MunicipioDTOResponse response) {
                    loadViewProgress.dismiss();
                    municipioDTOList = response.getPayload();
                    for (MunicipioDTO municipio : municipioDTOList) {
                        listMunicipios.add(municipio.getNome());
                    }
                    modalMunicipios();
                    if (response.getRedirect() != null){
                        RedirectNetwork.checkRedirectSucesso( response.getRedirect(), CadastroActivity.this);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    loadViewProgress.dismiss();
                    RedirectNetwork.checkRedirect( error, CadastroActivity.this );
                }
            });
        }
    }

    private AlertDialog myDialog;

    public void showDialog() {
        if (myDialog == null) {
            myDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.label_atencao))
                    .setMessage(getResources().getString(R.string.selecione_primeiro_uf))
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> myDialog.dismiss())
                    .create();
        }
        myDialog.show();
    }

    private void modalMunicipios() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button

        final ListView listViewItens = dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText(getString(R.string.cidade));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(CadastroActivity.this,
                R.layout.item_text_popup, R.id.textList, listMunicipios);

        // Assign adapter to ListView
        listViewItens.setAdapter(adapter);
        // ListView Item Click Listener
        listViewItens.setOnItemClickListener((parent, view, position, id) -> {
            // ListView Clicked item index
            int itemPosition = position;

            // ListView Clicked item value
            String itemValue = (String) listViewItens.getItemAtPosition(position);
            municipioSelect = itemValue;
        });

        Button dialogButtonOk = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = dialog.findViewById(R.id.dialogButtonCancelar);

        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(v -> {
            editTextMunicipioContent.setText(municipioSelect);
            if (isMunicipioUsuario) {
                for (MunicipioDTO municipio : municipioDTOList) {
                    if (municipio.getNome().equals(municipioSelect)) {
                        cadastrarApostadorDTO.setMunicipioId(municipio.getId());
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
        });

        dialogButtonCancelar.setOnClickListener(v -> {
            municipioSelect = getResources().getString(R.string.string_vazia);
            dialog.dismiss();
        });

        dialog.show();
    }
}
