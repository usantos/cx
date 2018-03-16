package caixa.gov.br.loterias.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO_;
import caixa.gov.br.loterias.model.bo.silce.SilceClient;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostadorDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.BairroDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.BairroDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.MunicipioIdDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.SexoAbrvEnum;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.DateUtils;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.utils.segmentedbutton.SegmentedButtonGroup;


@EActivity(R.layout.activity_cadastrar)

public class CadastrarActivity extends CadastroActivity {

    private SexoAbrvEnum sexo;
    private static final String FORMATO_YYYYMMDD = "yyyyMMdd";

    @ViewById
    EditText editTextCpfContent, editTextNomeContent, editTextCepContent, editTextNascimentoContent,
            editTextEmailContent, editTextMunicipioContent, editTextUFContent, editTextNomeMaeContent;

    @ViewById
    Button buttonContinuarIntroducao;

    @ViewById
    SegmentedButtonGroup segmentedButtonSexo;

    @ViewById
    TextView textViewTituloCadastrar2;

    @Extra
    List<String> listUFString, listMunicipios;

    @AfterViews
    protected void init() {
        super.createToolbar();
        loadFields();
        loadFields();
        addUFSpinner();
        if (apostadorDTO == null) {
            apostadorDTO = new ApostadorDTO();
            apostadorDTO.setMunicipioId(new MunicipioIdDTO());
        }
    }

    private void loadFields() {
        editTextNascimentoContent = (EditText) findViewById(R.id.editTextNascimentoContent);
        textViewTituloCadastrar2.setText(ViewUtils.textFuturaAndFuturaBold(this,"Que bom ver _você por aqui_ :)"));
    }

    public void addUFSpinner() {

        listUFString = new ArrayList<String>();
        listMunicipios = new ArrayList<String>();

        if (sexo == null) {
            sexo = SexoAbrvEnum.M;
        }
        initOnclick();
        addListenerUF(editTextUFContent, listUFString);
        addListenerMunicipio(editTextMunicipioContent, listMunicipios, Boolean.TRUE);

        addRightImg((EditText) findViewById(R.id.editTextNomeContent));
        addRightImg((EditText) findViewById(R.id.editTextCepContent));
        addRightImg((EditText) findViewById(R.id.editTextCpfContent));
        addRightImg((EditText) findViewById(R.id.editTextNomeMaeContent));
        addRightImg((EditText) findViewById(R.id.editTextNascimentoContent));
        addRightImg((EditText) findViewById(R.id.editTextEmailContent));
        addListenerContinuar();
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

                if (validarCamposObrigatorios()) {
                    if (validate()) {
                        carregaApostadorDTO();
                        Intent intent = new Intent(getBaseContext(), CadastrarPasso2Activity_.class);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    private boolean validate() {
        String textNascimento = AppUtils.getEditTextValue(editTextNascimentoContent);
        if (textNascimento != null) {

            if (DateUtils.isLegalDate(textNascimento, DateUtils.PATTERN_DDMM_YYYY)) {
                Calendar calendar = Calendar.getInstance();
                Date dateBorn = DateUtils.stringToDate(textNascimento, DateUtils.PATTERN_DDMM_YYYY);
                int age = DateUtils.getBetweenAges(calendar, DateUtils.DateToCalendar(dateBorn));
                if (age < 18) {
                    ViewUtils.alertTitulo(this, R.string.label_atencao, getString(R.string.MN002));
                    return false;
                }
                return true;
            }
            ViewUtils.alertTitulo(this,R.string.label_atencao, getString(R.string.msg_erro_data_invalida));
            return false;
        }
        return true;
    }

    private void initOnclick() {

        this.segmentedButtonSexo.setOnClickedButtonListener(new SegmentedButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(int position) {
                if (position == 0) {
                    sexo = SexoAbrvEnum.M;
                } else {
                    sexo = SexoAbrvEnum.F;
                }
            }
        });

        this.editTextCepContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String cep = editable.toString().replace("-", "").trim();
                if (cep.length() == 8) {
                    buscarBairroPorCep(cep);
                }
            }
        });

//        this.editTextCepContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//
//                String cep = textView.getText().toString().replace("-", "").trim();
//
//                if (StringUtils.isNotEmpty(cep) && (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_NAVIGATE_NEXT)) || (i == EditorInfo.IME_ACTION_NEXT)) {
//                    buscarBairroPorCep(cep);
//                }
//                return false;
//            }
//        });
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

    private boolean validarCamposObrigatorios() {
        String campos = "";

        if (StringUtils.isEmpty(AppUtils.removeMasckCPF(editTextCpfContent.getText().toString()))) {
            campos += "\nCPF";
        }
        if (StringUtils.isEmpty(editTextNomeContent.getText().toString())) {
            campos += "\nNome";
        }
        if (StringUtils.isEmpty(editTextNomeMaeContent.getText().toString())) {
            campos += "\nNome da Mãe";
        }
        if (StringUtils.isEmpty(editTextCepContent.getText().toString().replace("-", "").trim())) {
            campos += "\n" + getResources().getString(R.string.CEP);
        }
        if (StringUtils.isEmpty(editTextUFContent.getText().toString())) {
            campos += "\nUF";
        }
        if (StringUtils.isEmpty(editTextMunicipioContent.getText().toString())) {
            campos += "\nMunicípio";
        }
        if (StringUtils.isEmpty(editTextNascimentoContent.getText().toString().replace("/", "").trim())) {
            campos += "\nNascimento";
        }
        if (StringUtils.isEmpty(editTextEmailContent.getText().toString())) {
            campos += "\nE-mail";
        }

        if (StringUtils.isEmpty(campos)) {
            return true;
        } else {

            ViewUtils.alertTitulo(CadastrarActivity.this, R.string.MA002, campos);
            return false;
        }
    }

    private void carregaApostadorDTO() {
        apostadorDTO.setCpf(AppUtils.removeMasckCPF(editTextCpfContent.getText().toString()));
        apostadorDTO.setEmail(editTextEmailContent.getText().toString());
        apostadorDTO.setNome(editTextNomeContent.getText().toString());
        apostadorDTO.setNomeMae(editTextNomeMaeContent.getText().toString());
        apostadorDTO.setDataNascimento(editTextNascimentoContent.getText().toString());
        apostadorDTO.setCep(AppUtils.removeMasckCPF(editTextCepContent.getText().toString()));
        apostadorDTO.setAceitaReceberNoticias(Boolean.TRUE);
        apostadorDTO.setSexo(sexo);
        apostadorDTO.setMercadoPagoCustomerId("0");
    }

    private void buscarBairroPorCep(String cep) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(CadastrarActivity.this);
        dadosCorporativosBO.bairros(null, null, null, cep, new SilceClient.Listener<BairroDTOResponse>() {
            @Override
            public void onResponse(BairroDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getPayload() != null && !response.getPayload().isEmpty()) {
                    BairroDTO bairroDTO = response.getPayload().get(0);
                    editTextUFContent.setText(bairroDTO.getMunicipio().getUf().getSigla());
                    editTextMunicipioContent.setText(bairroDTO.getMunicipio().getNome());
                    apostadorDTO.setMunicipioId(bairroDTO.getMunicipio().getId());
                } else {
                    limparBuscaPorCep();
                }
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                limparBuscaPorCep();
                ViewUtils.alert(CadastrarActivity.this, MensagensNetwork.setMensagem(error, CadastrarActivity.this));
            }
        });
    }

    private void limparBuscaPorCep() {
        editTextUFContent.setText("");
        editTextMunicipioContent.setText("");
        apostadorDTO.setMunicipioId(null);
    }
}
