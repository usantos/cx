package caixa.gov.br.loterias.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
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
import java.util.Collections;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.BairroDTO;
import caixa.gov.br.loterias.bo.silce.dto.BairroDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.bo.silce.dto.ListDTOEnumIntegerResponse;
import caixa.gov.br.loterias.bo.silce.dto.LoteriasHabilitadaEnum;
import caixa.gov.br.loterias.bo.silce.dto.Loterica;
import caixa.gov.br.loterias.bo.silce.dto.LotericaCodigoWrapperResponse;
import caixa.gov.br.loterias.bo.silce.dto.LotericaDTO;
import caixa.gov.br.loterias.bo.silce.dto.LotericaDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.UsuarioLogadoResponse;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.utils.segmentedbutton.SegmentedButton;
import caixa.gov.br.loterias.utils.segmentedbutton.SegmentedButtonGroup;

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
        addListenerFinalizar();
        initOnclick();
        initRightImg();

        listUFString = new ArrayList<String>();
        listMunicipios = new ArrayList<String>();
        listBairros = new ArrayList<String>();

        addListenerUF(editTextUFPasso2Content, listUFString);
        addListenerMunicipio(editTextMunicipioPasso2Content, listMunicipios, Boolean.FALSE);
        addListenerBairro();
        buscarLotericasHabilitadas();
    }

    private void initRightImg() {
        // addRightImg((EditText) findViewById(R.id.editTextBairroPasso2Content));
        addRightImg((EditText) findViewById(R.id.editTextCEPPasso2Content));
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
                if ( isChanged ) {
                    return;
                }

                isChanged = true;

                String texto = textField.getText().toString();
                if (!TextUtils.isEmpty( texto )
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
        this.termoPolitica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), TermosUsoActivity.class);
                startActivity(intent);
            }
        });

        this.segmentedButtonTipoBuscaLoterica.setOnClickedButtonListener(new SegmentedButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(int position) {
                if (position == 0) {
                    focusButtonLocalizacao();
                } else if (position == 1) {
                    focusButtonCEP();
                } else {
                    focusButtonCodigoLoterica();
                }
            }
        });

        this.editTextCEPPasso2Content.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                String cep = textView.getText().toString().replace("-", "").trim();

                if (StringUtils.isNotEmpty(cep) && (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    buscarLoteriasLocalizacaoCep(cep, null);
                }
                return false;
            }
        });

        this.editTextCodigoLotericaPasso2Content.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    buscarLoteriasCodigoCompleto();
                }
                return false;
            }
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
        final View loadViewProgress = LoadingViewLoterias.init(this);
        DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(CadastrarPasso2Activity.this);
        dadosCorporativosBO.buscarLotericasHabilitadas(new SilceClient.Listener<ListDTOEnumIntegerResponse>() {
            @Override
            public void onResponse(ListDTOEnumIntegerResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
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
                    buscarLoteriasAutomatico();
                }
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(CadastrarPasso2Activity.this, MensagensNetwork.setMensagem(error, CadastrarPasso2Activity.this));
            }
        });
    }

    private void buscarLoteriasAutomatico() {

        final String cep = apostadorDTO.getCep().replace("-", "");
        final View loadViewProgress = LoadingViewLoterias.init(this);
        final DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(CadastrarPasso2Activity.this);
        dadosCorporativosBO.buscarLotericasCepOrBairroDTO(apostadorDTO.getCep().replace("-", ""), null, new SilceClient.Listener<LotericaDTOResponse>() {
            @Override
            public void onResponse(LotericaDTOResponse response) {

                if (response.getPayload() == null || response.getPayload().getLotericas() == null) {
                    dadosCorporativosBO.bairros(null, null, null, cep, new SilceClient.Listener<BairroDTOResponse>() {
                        @Override
                        public void onResponse(BairroDTOResponse response) {
                            LoadingViewLoterias.stop(loadViewProgress);
                            if (response.getPayload() != null && !response.getPayload().isEmpty()) {
                                buscarLoteriasLocalizacaoCep(null, response.getPayload().get(0));
                            }
                        }
                    }, new SilceClient.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            LoadingViewLoterias.stop(loadViewProgress);
                            ViewUtils.alert(CadastrarPasso2Activity.this, MensagensNetwork.setMensagem(error, CadastrarPasso2Activity.this));
                        }
                    });
                } else {
                    LoadingViewLoterias.stop(loadViewProgress);
                    popularRadio(response.getPayload().getLotericas());
                }


            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(CadastrarPasso2Activity.this, MensagensNetwork.setMensagem(error, CadastrarPasso2Activity.this));
            }
        });
    }

    private void buscarLoteriasLocalizacaoCep(String cep, BairroDTO bairroDTO) {

        final View loadViewProgress = LoadingViewLoterias.init(this);
        DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(CadastrarPasso2Activity.this);
        dadosCorporativosBO.buscarLotericasCepOrBairroDTO(cep, bairroDTO, new SilceClient.Listener<LotericaDTOResponse>() {
            @Override
            public void onResponse(LotericaDTOResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                if (response.getPayload() != null && response.getPayload().getLotericas() != null) {
                    popularRadio(response.getPayload().getLotericas());
                } else {
                    ViewUtils.alert(CadastrarPasso2Activity.this, response.getMensagem());
                }

            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(CadastrarPasso2Activity.this, MensagensNetwork.setMensagem(error, CadastrarPasso2Activity.this));
            }
        });
    }

    private void buscarLoteriasCodigoCompleto() {
        final View loadViewProgress = LoadingViewLoterias.init(this);

        DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(CadastrarPasso2Activity.this);
        dadosCorporativosBO.buscarLoteriasCodigoCompleto(editTextCodigoLotericaPasso2Content.getText().toString(), new SilceClient.Listener<LotericaCodigoWrapperResponse>() {
            @Override
            public void onResponse(LotericaCodigoWrapperResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                List<LotericaDTO> lotericaDTOList = new ArrayList<LotericaDTO>();
                lotericaDTOList.add(response.getPayload());
                popularRadio(lotericaDTOList);

            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(CadastrarPasso2Activity.this, MensagensNetwork.setMensagem(error, CadastrarPasso2Activity.this));
            }
        });
    }

    private void addListenerBairro() {
        this.listBairros = listBairros;
        editTextBairroPasso2Content.setClickable(true);
        editTextBairroPasso2Content.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getBairros();
            }
        });
    }

    private void getBairros() {

        if (StringUtils.isEmpty(municipioSelect)) {
            showDialogBairro();
        } else {
            listBairros = new ArrayList<String>();
            final View loadViewProgress = LoadingViewLoterias.init(this);

            DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(CadastrarPasso2Activity.this);
            dadosCorporativosBO.bairros(municipioIdDTOLoteria.getIdUF().toString(), municipioIdDTOLoteria.getNumero().toString(),
                    municipioIdDTOLoteria.getDigitoVerificador().toString(), null, new SilceClient.Listener<BairroDTOResponse>() {
                        @Override
                        public void onResponse(BairroDTOResponse response) {
                            LoadingViewLoterias.stop(loadViewProgress);
                            listBairroDTO = response.getPayload();
                            for (BairroDTO bairro : listBairroDTO) {
                                listBairros.add(bairro.getNome());
                            }
                            modalBairros();
                        }
                    }, new SilceClient.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            LoadingViewLoterias.stop(loadViewProgress);
                            ViewUtils.alert(CadastrarPasso2Activity.this, MensagensNetwork.setMensagem(error, CadastrarPasso2Activity.this));
                        }
                    });
        }
    }

    public void showDialogBairro() {
        if (myDialog == null) {
            myDialog = new AlertDialog.Builder(this)
                    .setTitle("Atenção")
                    .setMessage("Selecione primeiro o Município, assim nossa precisão de Bairros será incrível.")
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

    protected void modalBairros() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button

        final ListView listViewItens = (ListView) dialog.findViewById(R.id.listaItensDialog);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CadastrarPasso2Activity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, listBairros);

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
                bairroSelected = itemValue;
            }

        });

        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = (Button) dialog.findViewById(R.id.dialogButtonCancelar);

        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextBairroPasso2Content.setText(bairroSelected);
                for (BairroDTO bairroDTO : listBairroDTO) {
                    if (bairroDTO.getNome().equalsIgnoreCase(bairroSelected)) {
                        buscarLoteriasLocalizacaoCep(null, bairroDTO);
                    }
                }
                dialog.dismiss();
            }
        });

        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bairroSelected = "";
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void popularRadio(List<LotericaDTO> listLotericas) {
        radioListLoterias.removeAllViews();
        apostadorDTO.setLotericaId(null);
        if (listLotericas != null) {

            for (LotericaDTO lotericaDTO : listLotericas) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                RadioButton radioButton = new RadioButton(CadastrarPasso2Activity.this);
                radioButton.setLayoutParams(params);
                radioButton.setButtonDrawable(R.drawable.check_radio_selected);
                radioButton.setPadding(20, 20, 0, 10);
                radioButton.setText(lotericaDTO.getNomeFantasia() + "\nCódigo. " + lotericaDTO.getCodigo());
                radioButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                radioButton.setTextColor(getResources().getColor(R.color.cinza));
                radioButton.setScaleX(0.8f);
                radioButton.setScaleY(0.8f);
                radioButton.setTag(String.valueOf(lotericaDTO.getId()));
                radioButton.setTypeface(ViewUtils.getFontFutura(this));
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        apostadorDTO.setLotericaId(Long.parseLong(view.getTag().toString()));
                    }
                });
                radioListLoterias.addView(radioButton);
            }
        }
    }

    private void addListenerFinalizar() {
        buttonFinalizarCadastro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validarCampos()) {

                    final View loadViewProgress = LoadingViewLoterias.init(CadastrarPasso2Activity.this);
                    DadosUsuarioBO dadosUsuarioBO = new DadosUsuarioBO(CadastrarPasso2Activity.this);
                    dadosUsuarioBO.gravarUsuario(apostadorDTO, new SilceClient.Listener<UsuarioLogadoResponse>() {
                        @Override
                        public void onResponse(UsuarioLogadoResponse response) {
                            LoadingViewLoterias.stop(loadViewProgress);
                            Intent intent = new Intent(getBaseContext(), PrincipalActivity.class);
                            startActivity(intent);
                        }
                    }, new SilceClient.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            LoadingViewLoterias.stop(loadViewProgress);
                            ViewUtils.alert(CadastrarPasso2Activity.this, MensagensNetwork.setMensagem(error, CadastrarPasso2Activity.this));
                        }
                    });

                }
            }
        });
    }

    private boolean validarCampos() {

        String campos = "";

        if (apostadorDTO.getLotericaId() == null) {
            campos += "\nLotérica";
        }
        if (aceiteTermo.isChecked()) {
            apostadorDTO.setAceitaTermosUso(Boolean.TRUE);
        } else {
            campos += "\nTermo de Uso";
        }

        if (StringUtils.isEmpty(campos)) {
            return true;
        } else {

            ViewUtils.alertTitulo(CadastrarPasso2Activity.this, R.string.MA002, campos);
            return false;
        }
    }
}
