package br.gov.caixa.loterias.apostas.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatCheckBox;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.DadosUsuarioToken;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioIdDTO;
import br.gov.caixa.loterias.apostas.model.model.LoginSSOModel;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppUtils;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.RedirectUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButtonGroup;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;


public class CadastrarActivity extends CadastroActivity {

    public final static String LIST_UF_STRING_EXTRA = "listUFString";
    public final static String LIST_MUNICIPIOS_EXTRA = "listMunicipios";

    private static final String FORMATO_YYYYMMDD = "yyyyMMdd";

    private EditText editTextNomeContent, editTextEmailContent, editTextMunicipioContent, editTextUFContent;
    private MaskedEditText editTextCpfContent, editTextCepContent;
    private Button buttonFinalizarCadastro, naoSeiCepButton;
    private SegmentedButtonGroup segmentedButtonSexo;
    private TextView textViewTituloCadastrar2, termoPolitica;
    private ArrayList<String> listUFString, listMunicipios;
    private AppCompatCheckBox aceiteTermo;
    private AppCompatCheckBox aceiteNotificacoes;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);
        init();
    }

    protected void init() {
        getExtras();
        setViews();
        super.createToolbar();
        loadFields();
        loadFields();
        addUFSpinner();
        if (cadastrarApostadorDTO == null) {
            cadastrarApostadorDTO = new CadastrarApostadorDTO();
            cadastrarApostadorDTO.setSexo( CadastrarApostadorDTO.SexoEnum.M );
            cadastrarApostadorDTO.setMunicipioId(new MunicipioIdDTO());
            cadastrarApostadorDTO.setAceitaReceberNoticias( false );
            List<ApostaDTO> apostas = new ArrayList<>();
            cadastrarApostadorDTO.setApostas(apostas);
        }

        KeycloakBO keycloakBO = KeycloakBO.getInstance();
        String acessToken = keycloakBO.getAccessToken();
        try {
            atualizaLayout(Utils.decodedToken( acessToken ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getExtras() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(LIST_UF_STRING_EXTRA)) {
                this.listUFString = extras_.getStringArrayList(LIST_UF_STRING_EXTRA);
            }
            if (extras_.containsKey(LIST_MUNICIPIOS_EXTRA)) {
                this.listMunicipios = extras_.getStringArrayList(LIST_MUNICIPIOS_EXTRA);
            }
        }
    }

    private void setViews() {
        this.editTextNomeContent = findViewById(R.id.editTextNomeContent);
        this.editTextEmailContent = findViewById(R.id.editTextEmailContent);
        this.editTextMunicipioContent = findViewById(R.id.editTextMunicipioContent);
        this.editTextUFContent = findViewById(R.id.editTextUFContent);
        this.editTextCpfContent = findViewById(R.id.editTextCpfContent);
        this.editTextCepContent = findViewById(R.id.editTextCepContent);
        this.buttonFinalizarCadastro = findViewById(R.id.buttonFinalizarCadastro);
        this.segmentedButtonSexo = findViewById(R.id.segmentedButtonSexo);
        this.textViewTituloCadastrar2 = findViewById(R.id.textViewTituloCadastrar2);
        this.termoPolitica = findViewById(R.id.termoPolitica);
        this.aceiteTermo = findViewById(R.id.aceiteTermo);
        this.aceiteNotificacoes = findViewById(R.id.aceiteNotificacoes);
        this.naoSeiCepButton = findViewById(R.id.naoSeiCep);
    }

    private void loadFields() {
        textViewTituloCadastrar2.setText(ViewUtils.textFuturaAndFuturaBold(this,getResources().getString(R.string.bom_ver_voce)));
    }

    public void addUFSpinner() {

        listUFString = new ArrayList<>();
        listMunicipios = new ArrayList<>();

        initOnclick();
        addListenerUF(editTextUFContent, listUFString);
        addListenerMunicipio(editTextMunicipioContent, listMunicipios, Boolean.TRUE);

        addRightImg(findViewById(R.id.editTextNomeContent));
        addRightImg(findViewById(R.id.editTextCepContent));
        addRightImg(findViewById(R.id.editTextCpfContent));
        addRightImg(findViewById(R.id.editTextEmailContent));
        addListenerFinalizar();
    }

    private void addListenerFinalizar() {
        buttonFinalizarCadastro.setOnClickListener(v -> {
            if (validarCamposObrigatorios()) {

                final AlertDialog loadViewProgress = LoadingViewLoterias.show(CadastrarActivity.this);
                DadosUsuarioBO.getInstance().gravarUsuario(cadastrarApostadorDTO, new RequestListener<ApostadorDTOResponse>() {
                    @Override
                    public void onResponse(ApostadorDTOResponse response) {
                        SessaoUsuario.getInstance().setRedirectEnum(null);
                        loadViewProgress.dismiss();
                         startActivity( new Intent(CadastrarActivity.this, CadastrarConfirmacaoActivity.class ));
                         CadastrarActivity.this.finish();
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadViewProgress.dismiss();
                        DialogUtils.dialogEntendi(CadastrarActivity.this,  MensagensNetwork.setMensagem(error, CadastrarActivity.this));
                    }
                });
            }
        });
    }


    private void initOnclick() {
        naoSeiCepButton.setOnClickListener(view ->{
            DialogUtils.dialogTituloConfirmar(CadastrarActivity.this,
                    getString(R.string.nao_sei_cep),
                    getString(R.string.voce_sera_redirecionado_para_o_site_dos_correios),

                    new OnDialogBotaoListener() {
                        @Override
                        public void onButtonClick(DialogInterface dialog, int which) {
                            Utils.abreUrl(CadastrarActivity.this, R.string.Url_Correios);
                        }
                    }
            );
        });
        this.termoPolitica.setOnClickListener(v -> {
            Intent intent = new Intent(CadastrarActivity.this, TermosUsoActivity.class);
            startActivity(intent.putExtra("IS_CADASTRO",true));
        });
        this.segmentedButtonSexo.setOnClickedButtonListener(position -> {
            CadastrarApostadorDTO.SexoEnum sexoEnum;
            if (position == 0) {
                sexoEnum = CadastrarApostadorDTO.SexoEnum.M;
            } else {
                sexoEnum = CadastrarApostadorDTO.SexoEnum.F;
            }
            cadastrarApostadorDTO.setSexo( sexoEnum );
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
                String cep = editable.toString().replace( getResources().getString(R.string.traco), getResources().getString(R.string.string_vazia) ).trim();
                if (cep.length() == 8) {
                    editTextCepContent.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.icon_input_ok, 0 );
                    buscarBairroPorCep( cep );
                } else {
                    editTextCepContent.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0 );
                    //habilitaBotao(true);
                }
            }
        });
    }

    private boolean validarCamposObrigatorios() {
        if (aceiteTermo.isChecked()) {
            cadastrarApostadorDTO.setAceitaTermosUso(Boolean.TRUE);
        } else {
            DialogUtils.dialogEntendi(CadastrarActivity.this, this.getResources().getString( R.string.termos_nao_aceitos ));
            return false;
        }
        if(aceiteNotificacoes.isChecked()){
            cadastrarApostadorDTO.setAceitaReceberNoticias(Boolean.TRUE);
        } else{
            cadastrarApostadorDTO.setAceitaReceberNoticias(Boolean.FALSE);
        }

        String campos = getResources().getString(R.string.string_vazia);

        if (StringUtils.isEmpty(AppUtils.removeMasckCPF(editTextCpfContent.getText().toString()))) {
            campos += getResources().getString(R.string.barra_n) + getResources().getString(R.string.cpf);
        }
        if (StringUtils.isEmpty(editTextNomeContent.getText().toString())) {
            campos += getResources().getString(R.string.barra_n) + getResources().getString(R.string.nome);
        }

        if (StringUtils.isEmpty(editTextCepContent.getText().toString().replace(getResources().getString(R.string.traco), getResources().getString(R.string.string_vazia)).trim())) {
            campos += getResources().getString(R.string.barra_n) + getResources().getString(R.string.CEP);
        }
        if (StringUtils.isEmpty(editTextUFContent.getText().toString())) {
            campos += getResources().getString(R.string.barra_n) + getResources().getString(R.string.uf);
        }
        if (StringUtils.isEmpty(editTextMunicipioContent.getText().toString())) {
            campos += getResources().getString(R.string.barra_n) + getResources().getString(R.string.municipio);
        }
        if (StringUtils.isEmpty(editTextEmailContent.getText().toString())) {
            campos += getResources().getString(R.string.barra_n) + getResources().getString(R.string.email);
        }

        CadastrarApostadorDTO.SexoEnum sexoEnum;
        if (segmentedButtonSexo.getPosition() == 0){
            sexoEnum = CadastrarApostadorDTO.SexoEnum.M;
        }else{
            sexoEnum = CadastrarApostadorDTO.SexoEnum.F;
        }
        cadastrarApostadorDTO.setSexo( sexoEnum );
        cadastrarApostadorDTO.setCep( editTextCepContent.getText().toString() );

        if (StringUtils.isEmpty(campos)) {
            return true;
        } else {

            DialogUtils.dialogEntendi(CadastrarActivity.this, this.getResources().getString( R.string.MA002 ) + " " + campos);
            return false;
        }
    }

    private void buscarBairroPorCep(String cep) {
        AlertDialogUtils.show(this);
        DadosCorporativosSilceBO.getInstance().bairros(null, null, null, cep, new RequestListener<BairroDTOResponse>() {
            @Override
            public void onResponse(BairroDTOResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getPayload() != null && !response.getPayload().isEmpty()) {
                    BairroDTO bairroDTO = response.getPayload().get(0);
                    editTextUFContent.setText(bairroDTO.getMunicipio().getUf().getSigla());
                    editTextMunicipioContent.setText(bairroDTO.getMunicipio().getNome());
                    cadastrarApostadorDTO.setMunicipioId(bairroDTO.getMunicipio().getId());
                } else {
                    cepInvalido(getString(R.string.cep_invalido));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                cepInvalido(MensagensNetwork.setMensagem(error, CadastrarActivity.this));

            }
        });
    }

    private void cepInvalido(String msg) {
        DialogUtils.dialogEntendi(CadastrarActivity.this, msg);
        editTextUFContent.setText("");
        editTextMunicipioContent.setText("");
        editTextCepContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    private void atualizaLayout(DadosUsuarioToken dadosUsuarioToken) {
        editTextCpfContent.setMaskedText( dadosUsuarioToken.getCpf() );
        editTextNomeContent.setText( dadosUsuarioToken.getName() );
        editTextEmailContent.setText( dadosUsuarioToken.getEmail() );
        editTextCpfContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
        editTextNomeContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
        editTextEmailContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
    }

    @Override
    public void onBackPressed() {
        if (RedirectUtils.temRedirectCadastrarApostador()){
            executaVoltar();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        executaVoltar();
        return true;
    }

    private void executaVoltar(){
        AlertDialogUtils.show(this);
        new LoginSSOModel(CadastrarActivity.this).sair(LoginActivity.class, () -> {
            AlertDialogUtils.dismiss();
            startActivity(IntentUtil.getIntentLimpandoPilhaActivities(CadastrarActivity.this, LoginActivity.class));
        });

    }
}
