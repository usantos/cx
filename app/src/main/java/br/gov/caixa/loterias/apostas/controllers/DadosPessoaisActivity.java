package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButtonGroup;

public class DadosPessoaisActivity extends AppCompatActivity {
    int progressChangedValue = 0;
    private EditText editTextNomeContent, editTextUFContent, editTextMunicipioContent, editTextEmailContent;
    private TextView editValMaximoCompraContent, editValMinCompraContent, seekBarValor;
    private MaskedEditText editTextCpfContent, editTextCepContent;
    private SegmentedButtonGroup segmentedButtonSexo;
    private Button updateDadosPesoaisButton, naoSeiCepButton;
    private Button updateDadosPessoaisLimiteButton;

    private List<String> listUFString, listMunicipios, listBairros;
    private List<MunicipioDTO> municipioDTOList;
    protected List<UnidadeFederacaoDTO> listUf;
    protected String ufSelect;
    protected String municipioSelect;
    protected MunicipioIdDTO municipioIdDTOSelect;
    private SeekBar seekbarLimite;
    private ApostadorDTO apostadorDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dados_pessoais );
        setaViews();
        setaMetodos();
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
        editTextUFContent = findViewById( R.id.editTextUFContent );
        editTextMunicipioContent = findViewById( R.id.editTextMunicipioContent );
        editTextEmailContent = findViewById( R.id.editTextEmailContent );
        segmentedButtonSexo = findViewById( R.id.segmentedButtonSexo );
        updateDadosPesoaisButton = findViewById( R.id.updateDadosPesoaisButton );
        naoSeiCepButton = findViewById(R.id.naoSeiCep);
        updateDadosPessoaisLimiteButton = findViewById( R.id.updateDadosPessoaisLimiteButton );

        Toolbar toolbar = findViewById( R.id.toolbar );
        TextView subtitleToolbar = findViewById( R.id.subtitleToolbar );

        seekBarValor = (TextView) findViewById(R.id.seekbar_valor);

        editValMinCompraContent =  findViewById(R.id.seekbar_valor_min);
        editValMaximoCompraContent = findViewById(R.id.seekbar_valor_max);

        seekbarLimite = findViewById(R.id.seekBar);

        seekbarLimite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(apostadorDTO != null && apostadorDTO.getLimiteMinimoDiario() != null && progress <= apostadorDTO.getLimiteMinimoDiario().intValue()) {
                    progress = apostadorDTO.getLimiteMinimoDiario().intValue();
                }
                progressChangedValue = progress;
                seekBarValor.setText(NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ufSelect  = getResources().getString(R.string.string_vazia);
        municipioSelect = getResources().getString(R.string.string_vazia);

        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        //setTitle( ViewUtils.textFuturaAndFuturaBoldTitle( this, getTitle().toString() ) );
        setTitle( ViewUtils.textCaixaSTDBoldTitle( this, getTitle().toString() ) );
        subtitleToolbar.setText( getResources().getString(R.string.seus_dados_pessoais) );
    }

    private void setaMetodos() {
        editTextMunicipioContent.setClickable(false);
        //editTextMunicipioContent.setOnClickListener(v -> modalMunicipios());

        editTextUFContent.setClickable(false);
        //editTextUFContent.setOnClickListener(v -> getUFS());

        updateDadosPesoaisButton.setOnClickListener(view -> {
            if (validarCamposObrigatorios()){
                ApostadorAlteracaoDTO apostadorAlteracaoDTO = new ApostadorAlteracaoDTO();
                apostadorAlteracaoDTO.setCep( editTextCepContent.getUnmaskedText() );
                ApostadorAlteracaoDTO.SexoEnum sexoEnum;
                if (segmentedButtonSexo.getPosition() == 0){
                    sexoEnum = ApostadorAlteracaoDTO.SexoEnum.M;
                }else {
                    sexoEnum = ApostadorAlteracaoDTO.SexoEnum.F;
                }
                apostadorAlteracaoDTO.setSexo( sexoEnum);
                apostadorAlteracaoDTO.setAlterandoLoterica( false );
                apostadorAlteracaoDTO.setMunicipioId( municipioIdDTOSelect );


//                SessaoUsuario sessaoUsuario = (SessaoUsuario) seekBarValor.getText();
                apostadorAlteracaoDTO.setLimiteDiario(new BigDecimal(progressChangedValue));
                updateDadosCadastrais( apostadorAlteracaoDTO );
                SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();
            }
        }
        );

        naoSeiCepButton.setOnClickListener(view ->{
            DialogUtils.modalNaoSeiCep(DadosPessoaisActivity.this);
        });

        updateDadosPessoaisLimiteButton.setOnClickListener(view -> {
            AlertDialogUtils.show(DadosPessoaisActivity.this);
            DadosUsuarioBO.getInstance().recalcularLimite( new RequestListener<ApostadorDTOResponse>() {
                @Override
                public void onResponse(ApostadorDTOResponse response) {
                    apostadorDTO = response.getPayload();

                    editValMaximoCompraContent.setText(NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteDiarioAutorizado()));
                    updateDadosPessoaisLimiteButton.setText(getString(R.string.dados_limite_compra) +
                            NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteDiarioAutorizado())
                    );

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

//    @SuppressLint("SetTextI18n")
    private void atualizaLayout() {
        editTextCpfContent.setMaskedText( apostadorDTO.getCpf() );
        editTextNomeContent.setText( apostadorDTO.getNome() );
        editTextCepContent.setMaskedText( apostadorDTO.getCep() );
        editTextEmailContent.setText( apostadorDTO.getEmail() );

        seekBarValor.setText(NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteDiario()));
        editValMinCompraContent.setText(NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteMinimoDiario()));
        editValMaximoCompraContent.setText(NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteDiarioAutorizado()));

        updateDadosPessoaisLimiteButton.setText(getString(R.string.dados_limite_compra) +
                NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteDiarioAutorizado())
        );

        editTextCpfContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
        editTextNomeContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
        editTextCepContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
        editTextEmailContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);


        if (apostadorDTO.getSexo().equals( ApostadorDTO.SexoEnum.F )){
            segmentedButtonSexo.setPosition( 1 );
        }else{
            segmentedButtonSexo.setPosition( 0 );
        }
        editTextCepContent.addTextChangedListener(getTextWatcher(editTextCepContent));

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
                apostadorDTO = response.getPayload();
                municipioIdDTOSelect = apostadorDTO.getMunicipioId();
                getUfeMunicipio();

                seekbarLimite.setMax(apostadorDTO.getLimiteDiarioAutorizado().intValue());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    seekbarLimite.setMin(apostadorDTO.getLimiteMinimoDiario().intValue());
                }
                seekbarLimite.setProgress(apostadorDTO.getLimiteDiario().intValue());

                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), DadosPessoaisActivity.this);
                }
                AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_editar_cadastro));
                AlertDialogUtils.dismiss();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                RedirectNetwork.checkRedirect( error, DadosPessoaisActivity.this );
            }
        } );
    }

    private TextWatcher getTextWatcher(final EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText == editTextCepContent){
                    String cep = editable.toString().replace( getResources().getString(R.string.traco), getResources().getString(R.string.string_vazia) ).trim();
                    if (cep.length() == 8) {
                        editText.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.icon_input_ok, 0 );
                        buscarBairroPorCep( cep );
                    } else {
                        editText.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0 );
                        //habilitaBotao(true);
                    }
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
                } else {
                    cepInvalido(getString(R.string.cep_invalido));
                }
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), DadosPessoaisActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                cepInvalido(MensagensNetwork.setMensagem(error, DadosPessoaisActivity.this));
                //habilitaBotao(false);
            }
        });
    }

    private void cepInvalido(String msg) {
        ViewUtils.alertTitleButton(DadosPessoaisActivity.this,
                R.string.label_atencao,
                msg,
                getApplicationContext().getResources().getString(R.string.ok));
        editTextUFContent.setText("");
        editTextMunicipioContent.setText("");
        editTextCepContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    private void habilitaBotao(boolean isHabilita){
        if(isHabilita){
            updateDadosPesoaisButton.setEnabled(true);
            updateDadosPesoaisButton.setBackgroundColor(getResources().getColor(R.color.verdeazul));

        }else{
            updateDadosPesoaisButton.setEnabled(false);
            updateDadosPesoaisButton.setBackgroundColor(getResources().getColor(R.color.cinza_desativado));
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
                    SessaoUsuario.getInstance().getParametrosSimulacao().setValorLimiteDiario(new BigDecimal(progressChangedValue));
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
            ViewUtils.alertTitulo(DadosPessoaisActivity.this, R.string.label_atencao,
                    this.getResources().getString( R.string.MA002 ) + getResources().getString(R.string.espaco_em_branco) + campos);
            return false;
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
