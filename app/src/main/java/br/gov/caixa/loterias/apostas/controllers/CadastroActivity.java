package br.gov.caixa.loterias.apostas.controllers;


import android.app.Dialog;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioIdDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTOResponse;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogListener;

/**
 * Created by joafilho on 06/12/2017.
 */

public class CadastroActivity extends LoteriasBaseAppActivity {

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
            getSupportActionBar().setBackgroundDrawable(getResources().getDrawable((R.drawable.background_app_bar_azul)));
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

    protected void addLeftImg(EditText textField) {
        if (!textField.getText().toString().trim().isEmpty()) {

            textField.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_check, 0, 0, 0);

            TextViewCompat.setCompoundDrawableTintList(
                    textField,
                    ContextCompat.getColorStateList(
                            textField.getContext(),
                            R.color.cinza_item_desabilitado
                    )
            );

            textField.setHintTextColor(
                    ContextCompat.getColor(
                            textField.getContext(),
                            R.color.cinza_item_desabilitado
                    )
            );

        } else {
            textField.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

            TextViewCompat.setCompoundDrawableTintList(
                    textField,
                    null
            );
        }
    }

    protected void addListenerUF(EditText editTextUF, List<String> listUFString) {
        this.listUFString = listUFString;
        editTextUFContent = editTextUF;
        editTextUFContent.setClickable(true);
        editTextUFContent.setOnClickListener(v -> getUFS());
    }

    private void getUFS() {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        DadosCorporativosSilceBO.getInstance().ufs(new RequestListener<UnidadeFederacaoDTOResponse>() {
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
            DialogUtils.dialogEntendi(
                    CadastroActivity.this,
                    getResources().getString(R.string.selecione_primeiro_uf)
            );
        } else {
            for (UnidadeFederacaoDTO uf : listUf) {
                if (uf.getSigla().equalsIgnoreCase(ufSelect)) {
                    idUf = uf.getId().toString();
                }
            }
            final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
            listMunicipios = new ArrayList<>();

            DadosCorporativosSilceBO.getInstance().municipios(idUf, new RequestListener<MunicipioDTOResponse>() {
                @Override
                public void onResponse(MunicipioDTOResponse response) {
                    loadViewProgress.dismiss();
                    municipioDTOList = response.getPayload();
                    for (MunicipioDTO municipio : municipioDTOList) {
                        listMunicipios.add(municipio.getNome());
                    }
                    showDialogMunicipios();
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



    private void showDialogMunicipios(){

        DialogUtils.showDialogListItens(
                CadastroActivity.this,
                getString(R.string.cidade),
                null,
                listMunicipios,
                getString(R.string.confirmar),
                getString(R.string.cancelar),
                new OnDialogListener() {
                    @Override
                    public void itemSelecionado(int position) {

                        // ListView Clicked item value
                        String itemValue = listMunicipios.get(position);
                        municipioSelect = itemValue;
                    }

                    @Override
                    public void ok(int position) {
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
                    }

                    @Override
                    public void cancelar() {
                        municipioSelect = getResources().getString(R.string.string_vazia);
                    }
                }

        );
    }
}
