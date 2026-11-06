package br.gov.caixa.loterias.apostas.controllers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButton;
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButtonGroup;
import br.gov.caixa.loterias.apostas.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;


public class CadastrarActivity extends CadastroActivity {

    public final static String LIST_UF_STRING_EXTRA = "listUFString";
    public final static String LIST_MUNICIPIOS_EXTRA = "listMunicipios";

    private TextInputLayout editTextCEPLayout;
    private EditText editTextNomeContent, editTextEmailContent, editTextMunicipioContent, editTextUFContent;
    private MaskedEditText editTextCpfContent;
    private br.gov.caixa.loterias.apostas.view.components.MaskedEditText editTextCepContent;
    private Button naoSeiCepButton, buttonFinalizarCadastro;
    private SegmentedButtonGroup segmentedButtonSexo;
    private TextView txtValidaCep, labelCep;
    private ArrayList<String> listUFString, listMunicipios;
    private CheckBox aceiteTermo, aceiteNotificacoes;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);
        init();
    }

    protected void init() {
        getExtras();
        setViews();
        super.createToolbar();
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
        this.editTextCEPLayout = findViewById( R.id.editTextCEP );
        this.editTextCepContent = findViewById(R.id.editTextCepContent);
        this.buttonFinalizarCadastro = findViewById(R.id.buttonFinalizarCadastro);
        this.segmentedButtonSexo = findViewById(R.id.segmentedButtonSexo);
        this.labelCep = findViewById(R.id.labelCep);
        this.txtValidaCep = findViewById(R.id.txt_valida_cep);
        this.aceiteTermo = findViewById(R.id.aceiteTermo);
        this.aceiteNotificacoes = findViewById(R.id.aceiteNotificacoes);
        this.naoSeiCepButton = findViewById(R.id.naoSeiCep);
    }

    public void addUFSpinner() {

        listUFString = new ArrayList<>();
        listMunicipios = new ArrayList<>();

        initOnclick();
        addListenerUF(editTextUFContent, listUFString);
        addListenerMunicipio(editTextMunicipioContent, listMunicipios, Boolean.TRUE);
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
        MaterialTextView termo = findViewById(R.id.textViewAceiteTermo);

        String texto = getString(R.string.declaracao_termo_uso_politica_privacidade_termos);

        SpannableString spannable = new SpannableString(texto);

        String termoUso = "termos de uso";

        int inicioTermo = texto.indexOf(termoUso);
        int fimTermo = inicioTermo + termoUso.length();

        ClickableSpan termoClick = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent = new Intent(CadastrarActivity.this, TermosUsoActivity.class);
                startActivity(intent.putExtra("IS_CADASTRO",true));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(true);
                ds.setColor(ContextCompat.getColor(getApplicationContext(), R.color.cinza));
            }
        };

        spannable.setSpan(termoClick, inicioTermo, fimTermo, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        termo.setText(spannable);
        termo.setMovementMethod(LinkMovementMethod.getInstance());
        termo.setHighlightColor(Color.TRANSPARENT);

        segmentedButtonSexo.setOnPositionChangedListener(position -> {

            SegmentedButton masculino = findViewById(R.id.btMasculino);
            SegmentedButton feminino = findViewById(R.id.btFeminino);

            CadastrarApostadorDTO.SexoEnum sexoEnum;
            if (position == 0) {
                sexoEnum = CadastrarApostadorDTO.SexoEnum.M;
                masculino.setDrawable(R.drawable.ic_check_circle);
                feminino.setDrawable(R.drawable.ic_circle_outline);
            } else {
                sexoEnum = CadastrarApostadorDTO.SexoEnum.F;
                masculino.setDrawable(R.drawable.ic_circle_outline);
                feminino.setDrawable(R.drawable.ic_check_circle);
            }
            cadastrarApostadorDTO.setSexo( sexoEnum );

        });

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

        aceiteTermo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                findViewById(R.id.buttonFinalizarCadastro).setEnabled(b);
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
                String cep = editable.toString().replace( getResources().getString(R.string.traco), getResources().getString(R.string.string_vazia) ).trim();
                if (cep.length() == 8) {
                    buscarBairroPorCep( cep );
                } else {
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
        cadastrarApostadorDTO.setCep( editTextCepContent.getRawText() );

        if (StringUtils.isEmpty(campos)) {
            return true;
        } else {
            DialogUtils.dialogEntendi(CadastrarActivity.this, this.getResources().getString( R.string.MA002 ) + " " + campos);
            return false;
        }
    }

    private void buscarBairroPorCep(String cep) {
        AlertDialogUtils.show(CadastrarActivity.this);
        DadosCorporativosSilceBO.getInstance().bairros(null, null, null, cep, new RequestListener<BairroDTOResponse>() {
            @Override
            public void onResponse(BairroDTOResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getPayload() != null && !response.getPayload().isEmpty()) {
                    BairroDTO bairroDTO = response.getPayload().get(0);
                    editTextUFContent.setText(bairroDTO.getMunicipio().getUf().getSigla());
                    editTextMunicipioContent.setText(bairroDTO.getMunicipio().getNome());
                    cadastrarApostadorDTO.setMunicipioId(bairroDTO.getMunicipio().getId());
                    editTextCEPLayout.setBoxStrokeColor(getColor(R.color.cinza_item_desabilitado));
                    editTextCEPLayout.setHelperTextEnabled(false);
                    labelCep.setTextColor(getColor(R.color.cinza));
                    txtValidaCep.setVisibility(GONE);
                    addLeftImg(findViewById(R.id.editTextUFContent));
                    addLeftImg(findViewById(R.id.editTextMunicipioContent));
                } else {
                    cepInvalido(getString(R.string.cep_invalido));
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if(MensagensNetwork.getError(error) != null && !Objects.requireNonNull(MensagensNetwork.getError(error)).getCodigo().isEmpty()){
                    String code = Objects.requireNonNull(MensagensNetwork.getError(error)).getCodigo();
                    if(Objects.equals(code, "002021")){
                        cepInvalido(getString(R.string.cep_invalido));
                    }else{
                        cepInvalido(Objects.requireNonNull(MensagensNetwork.getError(error)).getMensagem());
                    }
                }
            }
        });
    }

    private void cepInvalido(String msg) {
        AlertDialogUtils.dismiss();
        labelCep.setTextColor(getColor(R.color.vermelho_erro));
        editTextCEPLayout.setBoxStrokeColor(getColor(R.color.vermelho_erro));
        txtValidaCep.setVisibility(VISIBLE);
        txtValidaCep.setText(msg);
        editTextUFContent.setText("");
        editTextMunicipioContent.setText("");
        editTextUFContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        editTextMunicipioContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    private void atualizaLayout(DadosUsuarioToken dadosUsuarioToken) {
        editTextCpfContent.setMaskedText( dadosUsuarioToken.getCpf() );
        editTextNomeContent.setText( dadosUsuarioToken.getName() );
        editTextEmailContent.setText( dadosUsuarioToken.getEmail() );
    }

    @SuppressLint("GestureBackNavigation")
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