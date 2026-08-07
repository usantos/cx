package br.gov.caixa.loterias.apostas.controllers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.TextViewCompat;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosSilceBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorAlteracaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.BairroDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CadastrarApostadorDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.MunicipioIdDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.UnidadeFederacaoDTOResponse;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.DialogUtils;
import br.gov.caixa.loterias.apostas.utils.Utils;
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButton;
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButtonGroup;
import br.gov.caixa.loterias.apostas.view.listener.OnDialogBotaoListener;

public class DadosPessoaisActivity extends LoteriasBaseAppActivity {
    private TextInputLayout editTextLimiteLayout, editTextCEPLayout;
    private EditText editTextNomeContent, editTextUFContent, editTextMunicipioContent, editTextEmailContent, editTextLimiteContent;
    private MaterialTextView limiteTitulo, txtValidaCep, validaLimiteMinContent, validaLimiteMaxContent, labelCep;
    private MaskedEditText editTextCpfContent;
    private br.gov.caixa.loterias.apostas.view.components.MaskedEditText editTextCepContent;
    private SegmentedButtonGroup segmentedButtonSexo;
    private Button updateDadosPessoaisButton, naoSeiCepButton, updateDadosPessoaisLimiteButton;

    private List<String> listUFString, listMunicipios, listBairros;
    private List<MunicipioDTO> municipioDTOList;
    protected List<UnidadeFederacaoDTO> listUf;
    protected String ufSelect;
    protected MunicipioIdDTO municipioIdDTOSelect;
    private ApostadorDTO apostadorDTO;
    private CheckBox notificacaoSwitch;
    private LinearLayout containerNotificacoes;
    private Boolean telaRenderizada = false;
    private ImageButton customButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dados_pessoais );
        setaViews();
        View footer = findViewById(R.id.footerDadosPessoais);
        View scroll = findViewById(R.id.scrollViewDadosPessoais);
        ViewCompat.setOnApplyWindowInsetsListener(scroll, (v, insets) -> {
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int imeOnly = ime.bottom - sys.bottom;
            int needed = Math.max(0, imeOnly - footer.getHeight());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), needed);
            return WindowInsetsCompat.CONSUMED;
        });
        setaMetodos();
    }

    @Override
    protected boolean baseConsumeInsets() {
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            callWebservice();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        callWebservice();
    }

    private void setaViews(){
        editTextCpfContent = findViewById( R.id.editTextCpfContent );
        editTextNomeContent = findViewById( R.id.editTextNomeContent );
        editTextCepContent = findViewById( R.id.editTextCepContent );
        editTextCEPLayout = findViewById( R.id.editTextCEP );
        editTextUFContent = findViewById( R.id.editTextUFContent );
        editTextMunicipioContent = findViewById( R.id.editTextMunicipioContent );
        editTextEmailContent = findViewById( R.id.editTextEmailContent );
        editTextLimiteContent = findViewById( R.id.editTextLimiteContent );
        editTextLimiteLayout = findViewById( R.id.editTextLimite );
        limiteTitulo = findViewById( R.id.limite_titulo );
        labelCep = findViewById( R.id.labelCep );
        txtValidaCep = findViewById( R.id.txt_valida_cep );
        validaLimiteMinContent = findViewById( R.id.txt_valida_limite_min );
        validaLimiteMaxContent = findViewById( R.id.txt_valida_limite_max );
        segmentedButtonSexo = findViewById( R.id.segmentedButtonSexo );
        updateDadosPessoaisButton = findViewById( R.id.updateDadosPesoaisButton );
        naoSeiCepButton = findViewById(R.id.naoSeiCep);
        updateDadosPessoaisLimiteButton = findViewById( R.id.updateDadosPessoaisLimiteButton );
        notificacaoSwitch = findViewById(R.id.notificacao_switch);
        containerNotificacoes = findViewById(R.id.containerNotificacoes);

        configurarToolbar();

        atualizarDescricaoNotificacoes(containerNotificacoes, notificacaoSwitch.isChecked());
    }

    private void configurarToolbar(){
        TextView textTitulo = findViewById(R.id.textCustom);
        textTitulo.setHint(getString(R.string.titulo));
        textTitulo.setText(getString(R.string.title_dados_pessoais));
        customButton = findViewById(R.id.customButton);
        customButton.setContentDescription(getString(R.string.voltar));
        customButton.setOnClickListener(v -> onBackPressed());
        customButton.setFocusable(true);
        customButton.setFocusableInTouchMode(true);
        customButton.requestFocus();
    }

    private void setaMetodos() {
        editTextMunicipioContent.setClickable(false);
        editTextUFContent.setClickable(false);
        updateDadosPessoaisButton.setOnClickListener(view -> {
            if (validarCamposObrigatorios()){
                ApostadorAlteracaoDTO apostadorAlteracaoDTO = new ApostadorAlteracaoDTO();
                apostadorAlteracaoDTO.setCep( editTextCepContent.getRawText() );
                ApostadorAlteracaoDTO.SexoEnum sexoEnum;
                if (segmentedButtonSexo.getPosition() == 0){
                    sexoEnum = ApostadorAlteracaoDTO.SexoEnum.M;
                }else {
                    sexoEnum = ApostadorAlteracaoDTO.SexoEnum.F;
                }
                apostadorAlteracaoDTO.setSexo(sexoEnum);
                apostadorAlteracaoDTO.setAlterandoLoterica(false);
                apostadorAlteracaoDTO.setMunicipioId( municipioIdDTOSelect );
                apostadorAlteracaoDTO.setAceitaReceberNoticias(notificacaoSwitch.isChecked());

                apostadorAlteracaoDTO.setLimiteDiario(getLimiteDefinido());
                updateDadosCadastrais( apostadorAlteracaoDTO );
                SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
            }
        }
        );

        segmentedButtonSexo.setOnPositionChangedListener(position -> {

            SegmentedButton masculino = findViewById(R.id.btMasculino);
            SegmentedButton feminino = findViewById(R.id.btFeminino);

            ApostadorAlteracaoDTO apostadorAlteracaoDTO = new ApostadorAlteracaoDTO();
            ApostadorAlteracaoDTO.SexoEnum sexoEnum;

            if (position == 0) {
                sexoEnum = ApostadorAlteracaoDTO.SexoEnum.M;
                masculino.setDrawable(R.drawable.ic_check_circle);
                feminino.setDrawable(R.drawable.ic_circle_outline);
            } else {
                sexoEnum = ApostadorAlteracaoDTO.SexoEnum.F;
                masculino.setDrawable(R.drawable.ic_circle_outline);
                feminino.setDrawable(R.drawable.ic_check_circle);
            }
            apostadorAlteracaoDTO.setSexo(sexoEnum);

        });

        notificacaoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!telaRenderizada) return;
            atualizarDescricaoNotificacoes(containerNotificacoes,isChecked);
            if (isChecked) {
                DialogUtils.dialogEntendi(
                        DadosPessoaisActivity.this,
                        "Ao ativar esta opção, você receberá notificações importantes de loterias se estiverem habilitadas no seu aparelho. A funcionalidade pode ser ativada ou desativada a qualquer momento. Para salvar, clique em atualizar."
                );
            } else {
                DialogUtils.dialogEntendi(
                        DadosPessoaisActivity.this,
                        "Ao desativar esta opção, você não receberá mais notificações de loterias. Para salvar, clique em atualizar."
                );
            }
        });

        containerNotificacoes.setOnClickListener(view -> {notificacaoSwitch.toggle();});

        naoSeiCepButton.setOnClickListener(view ->{
            DialogUtils.dialogTituloConfirmar(DadosPessoaisActivity.this,
                    getString(R.string.nao_sei_cep),
                    getString(R.string.voce_sera_redirecionado_para_o_site_dos_correios),

                    new OnDialogBotaoListener() {
                        @Override
                        public void onButtonClick(DialogInterface dialog, int which) {
                            Utils.abreUrl(DadosPessoaisActivity.this, R.string.Url_Correios);
                        }
                    }
            );
        });

        updateDadosPessoaisLimiteButton.setOnClickListener(view -> {
            AlertDialogUtils.show(DadosPessoaisActivity.this);
            DadosUsuarioBO.getInstance().recalcularLimite( new RequestListener<ApostadorDTOResponse>() {
                @Override
                public void onResponse(ApostadorDTOResponse response) {
                    apostadorDTO = response.getPayload();

                    String valor = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteDiarioAutorizado());
                    updateDadosPessoaisLimiteButton.setText(getString(R.string.dados_limite_compra, valor));

                    AppCenterManager.registraEvento("ENTROU_RECALCULAR_LIMITE_AUTORIZADO");
                    AlertDialogUtils.dismiss();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialogUtils.dismiss();
                    RedirectNetwork.checkRedirect( error, DadosPessoaisActivity.this );
                }
            });
        });
    }


    private void atualizarDescricaoNotificacoes(View rowNotificacoes, boolean isChecked) {
        String descricao = isChecked ? "Ativado: Interruptor" : "Desativado: Interruptor";
        String textoFinal = getString(R.string.label_notificacoes) + ": " + descricao;

        rowNotificacoes.setContentDescription(textoFinal);

        rowNotificacoes.post(() -> {
            AccessibilityManager accessibilityManager =
                    (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

            if (accessibilityManager != null && accessibilityManager.isEnabled()) {
                rowNotificacoes.announceForAccessibility(textoFinal);
            }
        });
    }

    private void atualizaLayout() {
        editTextCpfContent.setMaskedText( apostadorDTO.getCpf() );
        editTextNomeContent.setText( apostadorDTO.getNome() );
        editTextCepContent.setMask( apostadorDTO.getCep() );
        editTextEmailContent.setText( apostadorDTO.getEmail() );
        editTextLimiteContent.setText( NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteDiario()) );
        notificacaoSwitch.setChecked(apostadorDTO.getAceitaReceberNoticias());
        String valorLimiteMin = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteMinimoDiario());
        String valorLimiteMax = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteDiarioAutorizado());
        editTextLimiteLayout.setHelperText(valorLimiteMin + " até " + valorLimiteMax);
        validaLimiteMinContent.setText(getString(R.string.restringe_limite_ao_minimo, valorLimiteMin));
        validaLimiteMaxContent.setText(getString(R.string.restringe_limite_ao_maximo, valorLimiteMax));

        updateDadosPessoaisLimiteButton.setText(getString(R.string.dados_limite_compra, valorLimiteMax));

        if (apostadorDTO.getSexo().equals( ApostadorDTO.SexoEnum.F )){
            segmentedButtonSexo.setPosition( 1 );
        }else{
            segmentedButtonSexo.setPosition( 0 );
        }
        editTextCepContent.addTextChangedListener(getTextWatcher(editTextCepContent));
        editTextLimiteContent.addTextChangedListener(getTextWatcher(editTextLimiteContent));

        telaRenderizada = true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void callWebservice() {
        AlertDialogUtils.show(DadosPessoaisActivity.this);
        DadosUsuarioBO.getInstance().getDadosUsuario( new RequestListener<ApostadorDTOResponse>() {
            @Override
            public void onResponse(ApostadorDTOResponse response) {
                if(response != null){
                    apostadorDTO = response.getPayload();
                    municipioIdDTOSelect = apostadorDTO.getMunicipioId();
                    getUfeMunicipio();

                    if (response.getRedirect() != null){
                        RedirectNetwork.checkRedirectSucesso( response.getRedirect(), DadosPessoaisActivity.this);
                    }
                    AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_editar_cadastro));
                    AlertDialogUtils.dismiss();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect( error, DadosPessoaisActivity.this );
            }
        } );
    }

    private BigDecimal getLimiteDefinido() {
        String clean = editTextLimiteContent.getText().toString().replaceAll("[\\sR$,.]", "");
        return new BigDecimal(clean).movePointLeft(2);
    }

    private TextWatcher getTextWatcher(final EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            private String current = "";
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText == editTextLimiteContent && !charSequence.toString().equals(current)) {
                    editText.removeTextChangedListener(this);
                    String formatted = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format((getLimiteDefinido()));
                    current = formatted;
                    editText.setText(formatted);
                    editText.setSelection(formatted.length());
                    editText.addTextChangedListener(this);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (editText == editTextCepContent){
                    String cep = editable.toString().replace( getResources().getString(R.string.traco), getResources().getString(R.string.string_vazia) ).trim();
                    if (cep.length() == 8) {
                        buscarBairroPorCep( cep );
                    }
                }
                else if(editText == editTextLimiteContent){
                    BigDecimal limite = getLimiteDefinido();
                    boolean menorQueMin = limite.compareTo(apostadorDTO.getLimiteMinimoDiario()) < 0;
                    boolean maiorQueMax = limite.compareTo(apostadorDTO.getLimiteDiarioAutorizado()) > 0;
                    validaLimiteMinContent.setVisibility(menorQueMin ? VISIBLE : GONE);
                    validaLimiteMaxContent.setVisibility(maiorQueMax ? VISIBLE : GONE);
                    limiteTitulo.setTextColor(getColor(R.color.vermelho_erro));
                    editTextLimiteLayout.setBoxStrokeColor(getColor(R.color.vermelho_erro));
                    editTextLimiteLayout.setHelperTextEnabled(false);
                    boolean valido = !menorQueMin && !maiorQueMax;

                    if(valido){
                        editTextLimiteLayout.setBoxStrokeColor(getColor(R.color.cinza_item_desabilitado));
                        limiteTitulo.setTextColor(getColor(R.color.cinza));
                        editTextLimiteLayout.setHelperTextEnabled(true);
                        String valorLimiteMin = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteMinimoDiario());
                        String valorLimiteMax = NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteDiarioAutorizado());
                        editTextLimiteLayout.setHelperText(valorLimiteMin + " até " + valorLimiteMax);
                    }
                    //editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, valido ? R.drawable.icon_input_ok : 0, 0);
                    /*View target = maiorQueMax ? validaLimiteMaxContent : (menorQueMin ? validaLimiteMinContent : null);
                    if (target != null) {
                        target.post(() -> {
                            Rect r = new Rect(0, 0, target.getWidth(), target.getHeight());
                            target.requestRectangleOnScreen(r, true);
                        });
                    }*/
                }
            }
        };
    }

    private void buscarBairroPorCep(String cep) {
        AlertDialogUtils.show(DadosPessoaisActivity.this);
        DadosCorporativosSilceBO.getInstance().bairros(null, null, null, cep, new RequestListener<BairroDTOResponse>() {
            @Override
            public void onResponse(BairroDTOResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getPayload() != null && !response.getPayload().isEmpty()) {
                    BairroDTO bairroDTO = response.getPayload().get(0);
                    editTextUFContent.setText(bairroDTO.getMunicipio().getUf().getSigla());
                    ufSelect = bairroDTO.getMunicipio().getUf().getSigla();
                    editTextMunicipioContent.setText(bairroDTO.getMunicipio().getNome());
                    municipioIdDTOSelect = bairroDTO.getMunicipio().getId();
                    editTextCEPLayout.setBoxStrokeColor(getColor(R.color.cinza_item_desabilitado));
                    editTextCEPLayout.setHelperTextEnabled(false);
                    labelCep.setTextColor(getColor(R.color.cinza));
                    txtValidaCep.setVisibility(GONE);
                    addLeftImg(findViewById(R.id.editTextUFContent));
                    addLeftImg(findViewById(R.id.editTextMunicipioContent));
                } else {
                    cepInvalido(getString(R.string.cep_invalido));
                }
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), DadosPessoaisActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                cepInvalido(MensagensNetwork.setMensagem(error, DadosPessoaisActivity.this));
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

    private void habilitaBotao(boolean isHabilita){
        if(isHabilita){
            updateDadosPessoaisButton.setEnabled(true);
            updateDadosPessoaisButton.setBackgroundColor(getResources().getColor(R.color.verdeazul));

        }else{
            updateDadosPessoaisButton.setEnabled(false);
            updateDadosPessoaisButton.setBackgroundColor(getResources().getColor(R.color.cinza_desativado));
        }
    }

    private void updateDadosCadastrais(ApostadorAlteracaoDTO apostadorAlteracaoDTO){
        AlertDialogUtils.show(DadosPessoaisActivity.this);
        DadosUsuarioBO.getInstance().putDadosUsuario(apostadorAlteracaoDTO, new RequestListener<CadastrarApostadorDTOResponse>() {
            @Override
            public void onResponse(CadastrarApostadorDTOResponse response) {
                AppCenterManager.registraEvento(getResources().getString(R.string.evento_edicao_cadastro_sucesso));

                AlertDialogUtils.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), DadosPessoaisActivity.this);
                }else {
                    SessaoUsuario.getInstance().getParametrosSimulacao().setValorLimiteDiario(getLimiteDefinido());
                    startActivity(new Intent(DadosPessoaisActivity.this, DadosPessoaisConfirmacaoActivity.class));
                    finish();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect( error, DadosPessoaisActivity.this );
            }
        });
    }

    private boolean validarCamposObrigatorios() {
        String campos = "";

        if (editTextCepContent.getText().toString().replace("-", "").trim().length() < 8) {
            campos += getResources().getString(R.string.barra_n) + getResources().getString(R.string.CEP);
        }
        if (StringUtils.isEmpty(editTextUFContent.getText().toString())) {
            campos += getResources().getString(R.string.barra_n) + getResources().getString(R.string.uf);
        }
        if (StringUtils.isEmpty(editTextMunicipioContent.getText().toString())) {
            campos += getResources().getString(R.string.barra_n) + getResources().getString(R.string.municipio);
        }
        if (StringUtils.isEmpty(campos)) {
            return true;
        } else {
            DialogUtils.dialogEntendi(DadosPessoaisActivity.this,
                    this.getResources().getString( R.string.MA002 ) + getResources().getString(R.string.espaco_em_branco) + campos
                    );
            return false;
        }
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

    private void getUfeMunicipio(){
        DadosCorporativosSilceBO.getInstance().ufs(new RequestListener<UnidadeFederacaoDTOResponse>() {
            @Override
            public void onResponse(UnidadeFederacaoDTOResponse response) {
                listUf = response.getPayload();
                listUFString = new ArrayList<>();
                for (UnidadeFederacaoDTO item : listUf){
                    if (item.getId().equals(apostadorDTO.getMunicipioId().getIdUF())){
                        editTextUFContent.setText( item.getSigla() );
                    }
                    listUFString.add(item.getSigla());
                }
                DadosCorporativosSilceBO.getInstance().municipios(apostadorDTO.getMunicipioId().getIdUF().toString(), new RequestListener<MunicipioDTOResponse>() {
                    @Override
                    public void onResponse(MunicipioDTOResponse response) {
                        AlertDialogUtils.dismiss();
                        municipioDTOList = response.getPayload();

                        listMunicipios = new ArrayList<>();
                        for (MunicipioDTO item : municipioDTOList){
                            if (item.getId().getNumero().equals(apostadorDTO.getMunicipioId().getNumero())){
                                editTextMunicipioContent.setText( item.getNome() );
                            }
                            listMunicipios.add(item.getNome());
                        }
                        atualizaLayout();

                        if (response.getRedirect() != null){
                            RedirectNetwork.checkRedirectSucesso( response.getRedirect(), DadosPessoaisActivity.this);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialogUtils.dismiss();
                        RedirectNetwork.checkRedirect( error, DadosPessoaisActivity.this );
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect( error, DadosPessoaisActivity.this );
            }
        });

    }

}
