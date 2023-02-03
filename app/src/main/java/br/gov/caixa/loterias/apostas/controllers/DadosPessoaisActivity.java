package br.gov.caixa.loterias.apostas.controllers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.SessaoUsuario;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
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
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.utils.segmentedbutton.SegmentedButtonGroup;
public class DadosPessoaisActivity extends AppCompatActivity {

    EditText editTextNomeContent, editTextUFContent, editTextMunicipioContent, editTextEmailContent, editValMaximoCompraContent, editValMinCompraContent;
    MaskedEditText editTextCpfContent, editTextCepContent;
    SegmentedButtonGroup segmentedButtonSexo;
    Button updateDadosPesoaisButton;
    Button updateDadosPessoaisLimiteButton;

    private List<String> listUFString, listMunicipios, listBairros;
    private List<MunicipioDTO> municipioDTOList;
    protected List<UnidadeFederacaoDTO> listUf;
    protected String ufSelect;
    protected String municipioSelect;
    protected MunicipioIdDTO municipioIdDTOSelect;

    ApostadorDTO apostadorDTO;

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
        updateDadosPessoaisLimiteButton = findViewById( R.id.updateDadosPessoaisLimiteButton );

        Toolbar toolbar = findViewById( R.id.toolbar );
        TextView subtitleToolbar = findViewById( R.id.subtitleToolbar );
        TextView seekBarValor = (TextView) findViewById(R.id.seekbar_valor);

        TextView editValMinCompraContent = findViewById(R.id.seekbar_valor_min);
        TextView editValMaximoCompraContent = findViewById(R.id.seekbar_valor_max);


        SeekBar seekbarLimite = (SeekBar) findViewById(R.id.seekBar);
        seekbarLimite.setMax(945);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekbarLimite.setMin(30);
        }
        seekbarLimite.setProgress(50);

        seekbarLimite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                seekBarValor.setText ("R$ " + progress + ",00");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(activity_dados_pessoais.this, progressChangedValue, Toast.LENGTH_SHORT);
            }
        });

        ufSelect  = getResources().getString(R.string.string_vazia);
        municipioSelect = getResources().getString(R.string.string_vazia);

        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );

        setTitle( ViewUtils.textFuturaAndFuturaBoldTitle( this, getTitle().toString() ) );
        subtitleToolbar.setText( getResources().getString(R.string.seus_dados_pessoais) );
    }

    private void setaMetodos() {
        editTextMunicipioContent.setClickable(true);
        editTextMunicipioContent.setOnClickListener(v -> modalMunicipios());

        editTextUFContent.setClickable(true);
        editTextUFContent.setOnClickListener(v -> getUFS());

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
//                apostadorAlteracaoDTO.set
                SessaoUsuario sessaoUsuario = SessaoUsuario.getInstance();

                apostadorAlteracaoDTO.setLimiteDiario( sessaoUsuario.getValorMaximoAposta() );
                updateDadosCadastrais( apostadorAlteracaoDTO );
            }
        }
        );
        updateDadosPessoaisLimiteButton.setOnClickListener(view -> {
//            if () {
                apostadorDTO.setLimiteDiario(new BigDecimal(editValMaximoCompraContent.findViewById(R.id.seekbar_valor).toString()));
//            } else {
//
//            }
        }
        );
    }

    @SuppressLint("SetTextI18n")
    private void atualizaLayout() {
        editTextCpfContent.setMaskedText( apostadorDTO.getCpf() );
        editTextNomeContent.setText( apostadorDTO.getNome() );
        editTextCepContent.setMaskedText( apostadorDTO.getCep() );
        editTextEmailContent.setText( apostadorDTO.getEmail() );

        editValMaximoCompraContent.setText("R$ "+apostadorDTO.getLimiteDiario()+",00");
        editValMinCompraContent.setText("R$ "+ apostadorDTO.getLimiteMinimoDiario()+ ",00");

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
        final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_( this );

        dadosUsuarioBO.getDadosUsuario( new RequestListener<ApostadorDTOResponse>() {
            @Override
            public void onResponse(ApostadorDTOResponse response) {
                apostadorDTO = response.getPayload();
                municipioIdDTOSelect = apostadorDTO.getMunicipioId();
                getUfeMunicipio();
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

    private void getUFS() {
        if (listUf == null || listUFString == null) {
            DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(DadosPessoaisActivity.this);
            AlertDialogUtils.show(DadosPessoaisActivity.this);
            dadosCorporativosBO.ufs(new RequestListener<UnidadeFederacaoDTOResponse>() {
                @Override
                public void onResponse(UnidadeFederacaoDTOResponse response) {
                    AlertDialogUtils.dismiss();
                    listUFString = new ArrayList<>();
                    listUf = response.getPayload();
                    for (UnidadeFederacaoDTO uf : response.getPayload()) {
                        listUFString.add(uf.getSigla());
                    }
                    modalUfs();
                    if (response.getRedirect() != null) {
                        RedirectNetwork.checkRedirectSucesso(response.getRedirect(), DadosPessoaisActivity.this);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertDialogUtils.dismiss();
                    RedirectNetwork.checkRedirect(error, DadosPessoaisActivity.this);
                }
            });
        }else {
            modalUfs();
        }
    }

    private void modalUfs() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button

        final ListView listViewItens = dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText(getResources().getString(R.string.uf));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(DadosPessoaisActivity.this,
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
            if (!ufSelect.isEmpty() && !ufSelect.equals( editTextUFContent.getText().toString() )){
                editTextUFContent.setText(ufSelect);
                editTextMunicipioContent.setText( "" );
                municipioIdDTOSelect = null;
                buscaMunicipio();
            }

            dialog.dismiss();
        });

        dialogButtonCancelar.setOnClickListener(v -> {
            ufSelect = editTextUFContent.getText().toString();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void buscaMunicipio() {
        String idUF = getIdUF(ufSelect);

        if (idUF != null && !idUF.isEmpty()){
            AlertDialogUtils.show(DadosPessoaisActivity.this);

            DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(DadosPessoaisActivity.this);
            dadosCorporativosBO.municipios(idUF, new RequestListener<MunicipioDTOResponse>() {
                @Override
                public void onResponse(MunicipioDTOResponse response) {
                    AlertDialogUtils.dismiss();
                    listMunicipios = new ArrayList<>();
                    municipioDTOList = response.getPayload();
                    for (MunicipioDTO municipio : municipioDTOList) {
                        listMunicipios.add(municipio.getNome());
                    }
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
        }else {
            showDialog();
        }
    }

    private String getIdUF(String ufSelect) {
        String idUf = null;
        if (!ufSelect.isEmpty()) {
            for (UnidadeFederacaoDTO uf : listUf) {
                if (uf.getSigla().equalsIgnoreCase(ufSelect)) {
                    idUf = uf.getId().toString();
                    break;
                }
            }
        }
        return idUf;
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
        municipioSelect = "";
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button

        final ListView listViewItens = dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText(getResources().getString(R.string.cidade));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(DadosPessoaisActivity.this,
                R.layout.item_text_popup, R.id.textList, listMunicipios);

        // Assign adapter to ListView
        listViewItens.setAdapter(adapter);
        // ListView Item Click Listener
        listViewItens.setOnItemClickListener((parent, view, position, id) -> {
            // ListView Clicked item index
            // ListView Clicked item value
            String itemValue = (String) listViewItens.getItemAtPosition(position);
            municipioIdDTOSelect = municipioDTOList.get( position ).getId();
            municipioSelect = itemValue;
        });

        Button dialogButtonOk = dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = dialog.findViewById(R.id.dialogButtonCancelar);

        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(v -> {
            if (!municipioSelect.isEmpty() && !municipioSelect.equals( editTextMunicipioContent.getText().toString() ) ) {
                editTextMunicipioContent.setText(municipioSelect);
            }
            dialog.dismiss();
        });

        dialogButtonCancelar.setOnClickListener(v -> {
            municipioSelect = "";
            dialog.dismiss();
        });

        dialog.show();
    }

    private TextWatcher getTextWatcher(final EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // do what you want with your EditText

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText == editTextCepContent){
                        String cep = editable.toString().replace( getResources().getString(R.string.traco), getResources().getString(R.string.string_vazia) ).trim();
                        if (cep.length() == 8) {
                            editText.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.icon_input_ok, 0 );
                            buscarBairroPorCep( cep );
                        } else {
                            editText.setCompoundDrawablesWithIntrinsicBounds( 0, 0, 0, 0 );
                        }

                }

            }
        };
    }

    private void buscarBairroPorCep(String cep) {
        AlertDialogUtils.show(DadosPessoaisActivity.this);
        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(DadosPessoaisActivity.this);
        dadosCorporativosBO.bairros(null, null, null, cep, new RequestListener<BairroDTOResponse>() {
            @Override
            public void onResponse(BairroDTOResponse response) {
                AlertDialogUtils.dismiss();
                if (response.getPayload() != null && !response.getPayload().isEmpty()) {
                    BairroDTO bairroDTO = response.getPayload().get(0);
                    editTextUFContent.setText(bairroDTO.getMunicipio().getUf().getSigla());
                    ufSelect = bairroDTO.getMunicipio().getUf().getSigla();
                    editTextMunicipioContent.setText(bairroDTO.getMunicipio().getNome());
                    municipioIdDTOSelect = bairroDTO.getMunicipio().getId();
                }
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), DadosPessoaisActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                AlertDialogUtils.dismiss();
                ViewUtils.alertTitleButton(DadosPessoaisActivity.this,
                        R.string.label_atencao,
                        MensagensNetwork.setMensagem(error, DadosPessoaisActivity.this),
                        getApplicationContext().getResources().getString( R.string.ok ));
            }
        });
    }

    private void updateDadosCadastrais(ApostadorAlteracaoDTO apostadorAlteracaoDTO){
        final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_( this );
        AlertDialogUtils.show(DadosPessoaisActivity.this);
        dadosUsuarioBO.putDadosUsuario(apostadorAlteracaoDTO, new RequestListener<CadastrarApostadorDTOResponse>() {
            @Override
            public void onResponse(CadastrarApostadorDTOResponse response) {
                AppCenterManager.registraEvento(getResources().getString(R.string.evento_edicao_cadastro_sucesso));

                AlertDialogUtils.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), DadosPessoaisActivity.this);
                }else {
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

            ViewUtils.alertTitulo(DadosPessoaisActivity.this, R.string.label_atencao, this.getResources().getString( R.string.MA002 ) + getResources().getString(R.string.espaco_em_branco) + campos);
            return false;
        }
    }

    private void getUfeMunicipio(){

        DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(DadosPessoaisActivity.this);
        dadosCorporativosBO.ufs(new RequestListener<UnidadeFederacaoDTOResponse>() {
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
                DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(DadosPessoaisActivity.this);
                dadosCorporativosBO.municipios(apostadorDTO.getMunicipioId().getIdUF().toString(), new RequestListener<MunicipioDTOResponse>() {
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
