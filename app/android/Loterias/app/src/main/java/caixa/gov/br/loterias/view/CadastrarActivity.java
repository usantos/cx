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
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.ApostadorDTO;
import caixa.gov.br.loterias.bo.silce.dto.MunicipioIdDTO;
import caixa.gov.br.loterias.bo.silce.dto.SexoAbrvEnum;
import caixa.gov.br.loterias.utils.AppUtils;
import caixa.gov.br.loterias.utils.CustomAlert;
import caixa.gov.br.loterias.utils.DateUtils;
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

    @Extra
    List<String> listUFString, listMunicipios;

    @AfterViews
    protected void init() {
        super.createToobar();
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
                    CustomAlert.showAlertMessage(this, R.string.label_atencao, R.string.idade_menor_18);
                    return false;
                }
                return true;
            }
            CustomAlert.showAlertMessage(this, R.string.label_atencao, R.string.erro_data_invalida);
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
}
