package br.gov.caixa.loterias.apostas.controllers;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DTOEnumInteger;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListDTOEnumIntegerResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LoteriasHabilitadaEnum;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaCodigoWrapperResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTOResponse;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButton;
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButtonGroup;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;

@EActivity(R.layout.activity_cadastrar_passo2)
public class CadastrarPasso2Activity extends CadastroActivity {


    @ViewById
    SegmentedButton segmentedButtonLocalizacao, segmentedButtonCEP, segmentedButtonCodigoLoterica;

    @ViewById
    Button buttonFinalizarCadastro;

    @ViewById
    TextView termoPolitica;

    @ViewById
    AppCompatCheckBox aceiteTermo;

    @ViewById
    LinearLayout localizacaoLoterica, cepLoterica, codigoLoterica;

    @ViewById
    SegmentedButtonGroup segmentedButtonTipoBuscaLoterica;

    @ViewById
    EditText editTextUFPasso2Content, editTextMunicipioPasso2Content, editTextBairroPasso2Content, editTextCodigoLotericaPasso2Content;

    @ViewById
    RadioGroup radioListLoterias;

    @ViewById
    MaskedEditText editTextCEPPasso2Content;

    @Extra
    List<String> listUFString, listMunicipios, listBairros;

    private List<DTOEnumInteger> listLoteriasHabilitadas;
    private List<BairroDTO> listBairroDTO;
    private AlertDialog myDialog;


    @AfterViews
    protected void init() {
        super.createToolbar();
//        addListenerFinalizar();
        initOnclick();
        initRightImg();

        listUFString = new ArrayList<>();
        listMunicipios = new ArrayList<>();
        listBairros = new ArrayList<>();

        addListenerUF(editTextUFPasso2Content, listUFString);
        addListenerMunicipio(editTextMunicipioPasso2Content, listMunicipios, Boolean.FALSE);
        addListenerBairro();
        buscarLotericasHabilitadas();
    }

    private void initRightImg() {
        // addRightImg((EditText) findViewById(R.id.editTextBairroPasso2Content));
        addRightImg(findViewById(R.id.editTextCEPPasso2Content));
    }


    @Override
    protected void addRightImg(final EditText textField) {
        textField.addTextChangedListener(new TextWatcher() {

            private boolean isChanged = false;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (isChanged) {
                    return;
                }

                isChanged = true;

                String texto = textField.getText().toString();
                if (!TextUtils.isEmpty(texto)
                        && texto.trim().length() == 9) {
                    textField.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
                } else {
                    textField.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }

                isChanged = false;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void initOnclick() {
        this.termoPolitica.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
            startActivity(intent);
        });

        this.segmentedButtonTipoBuscaLoterica.setOnClickedButtonListener(position -> {
            if (position == 0) {
                focusButtonLocalizacao();
            } else if (position == 1) {
                focusButtonCEP();
            } else {
                focusButtonCodigoLoterica();
            }
        });

        this.editTextCEPPasso2Content.setOnEditorActionListener((textView, i, keyEvent) -> {

            String cep = textView.getText().toString().replace(getResources().getString(R.string.traco), getResources().getString(R.string.string_vazia)).trim();

            if (StringUtils.isNotEmpty(cep) && (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                buscarLoteriasLocalizacaoCep(cep, null);
            }
            return false;
        });

        this.editTextCodigoLotericaPasso2Content.setOnEditorActionListener((textView, i, keyEvent) -> {
            if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                buscarLoteriasCodigoCompleto();
            }
            return false;
        });
    }

    private void focusButtonLocalizacao() {
        localizacaoLoterica.setVisibility(View.VISIBLE);
        cepLoterica.setVisibility(View.GONE);
        codigoLoterica.setVisibility(View.GONE);
    }

    private void focusButtonCEP() {
        cepLoterica.setVisibility(View.VISIBLE);
        localizacaoLoterica.setVisibility(View.GONE);
        codigoLoterica.setVisibility(View.GONE);
    }

    private void focusButtonCodigoLoterica() {
        codigoLoterica.setVisibility(View.VISIBLE);
        localizacaoLoterica.setVisibility(View.GONE);
        cepLoterica.setVisibility(View.GONE);
    }

    private void buscarLotericasHabilitadas() {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(CadastrarPasso2Activity.this);
        dadosCorporativosBO.buscarLotericasHabilitadas(new RequestListener<ListDTOEnumIntegerResponse>() {
            @Override
            public void onResponse(ListDTOEnumIntegerResponse response) {
                loadViewProgress.dismiss();
                listLoteriasHabilitadas = response.getPayload();
                for (DTOEnumInteger dtoEnumInteger : listLoteriasHabilitadas) {
                    switch (LoteriasHabilitadaEnum.getTipoFromCodigo(dtoEnumInteger.getValor())) {
                        case CEP:
                            segmentedButtonCEP.setVisibility(View.VISIBLE);
                            break;
                        case CODIGO:
                            segmentedButtonCodigoLoterica.setVisibility(View.VISIBLE);
                            break;
                        default:
                            segmentedButtonLocalizacao.setVisibility(View.VISIBLE);
                    }
                }
                if (segmentedButtonCEP.getVisibility() == View.VISIBLE) {
//                    buscarLoteriasAutomatico();
                }
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), CadastrarPasso2Activity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, CadastrarPasso2Activity.this );
            }
        });
    }

    private void buscarLoteriasLocalizacaoCep(String cep, BairroDTO bairroDTO) {

        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(CadastrarPasso2Activity.this);
        dadosCorporativosBO.buscarLotericasCepOrBairroDTO(cep, bairroDTO, new RequestListener<LotericaDTOResponse>() {
            @Override
            public void onResponse(LotericaDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getPayload() != null && response.getPayload().getLotericas() != null) {
//                    popularRadio(response.getPayload().getLotericas());
                } else {
                    ViewUtils.alert(CadastrarPasso2Activity.this, response.getMensagem());
                }
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), CadastrarPasso2Activity.this);
                }

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, CadastrarPasso2Activity.this );
            }
        });
    }

    private void buscarLoteriasCodigoCompleto() {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);

        DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(CadastrarPasso2Activity.this);
        dadosCorporativosBO.buscarLoteriasCodigoCompleto(editTextCodigoLotericaPasso2Content.getText().toString(), new RequestListener<LotericaCodigoWrapperResponse>() {
            @Override
            public void onResponse(LotericaCodigoWrapperResponse response) {
                loadViewProgress.dismiss();
                List<LotericaDTO> lotericaDTOList = new ArrayList<>();
                lotericaDTOList.add(response.getPayload());
//                popularRadio(lotericaDTOList);

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, CadastrarPasso2Activity.this );
            }
        });
    }

    private void addListenerBairro() {
        this.listBairros = listBairros;
        editTextBairroPasso2Content.setClickable(true);
        editTextBairroPasso2Content.setOnClickListener(v -> getBairros());
    }

    private void getBairros() {

        if (StringUtils.isEmpty(municipioSelect)) {
            showDialogBairro();
        } else {
            listBairros = new ArrayList<>();
            final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);

            DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(CadastrarPasso2Activity.this);
            dadosCorporativosBO.bairros(municipioIdDTOLoteria.getIdUF().toString(), municipioIdDTOLoteria.getNumero().toString(),
                    municipioIdDTOLoteria.getDigitoVerificador().toString(), null, new RequestListener<BairroDTOResponse>() {
                        @Override
                        public void onResponse(BairroDTOResponse response) {
                            loadViewProgress.dismiss();
                            listBairroDTO = response.getPayload();
                            for (BairroDTO bairro : listBairroDTO) {
                                listBairros.add(bairro.getNome());
                            }
                            modalBairros();
                            if (response.getRedirect() != null){
                                RedirectNetwork.checkRedirectSucesso( response.getRedirect(), CadastrarPasso2Activity.this);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loadViewProgress.dismiss();
                            RedirectNetwork.checkRedirect( error, CadastrarPasso2Activity.this );
                        }
                    });
        }
    }

    public void showDialogBairro() {
        if (myDialog == null) {
            myDialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.label_atencao))
                    .setMessage(getResources().getString(R.string.selecione_municipio_primeiro))
                    .setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> myDialog.dismiss())
                    .create();
        }
        myDialog.show();
    }

    protected void modalBairros() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button

        final ListView listViewItens = dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText("Bairro");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(CadastrarPasso2Activity.this,
                R.layout.item_text_popup, R.id.textList, listBairros);

        // Assign adapter to ListView
        listViewItens.setAdapter(adapter);
        // ListView Item Click Listener
        listViewItens.setOnItemClickListener((parent, view, position, id) -> {
            // ListView Clicked item index
            int itemPosition = position;

            // ListView Clicked item value
            String itemValue = (String) listViewItens.getItemAtPosition(position);
            bairroSelected = itemValue;
        });

        Button dialogButtonOk = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = dialog.findViewById(R.id.dialogButtonCancelar);

        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(v -> {
            editTextBairroPasso2Content.setText(bairroSelected);
            for (BairroDTO bairroDTO : listBairroDTO) {
                if (bairroDTO.getNome().equalsIgnoreCase(bairroSelected)) {
                    buscarLoteriasLocalizacaoCep(null, bairroDTO);
                }
            }
            dialog.dismiss();
        });

        dialogButtonCancelar.setOnClickListener(v -> {
            bairroSelected = "";
            dialog.dismiss();
        });

        dialog.show();
    }
}
