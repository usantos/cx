package br.gov.caixa.loterias.apostar.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bean.DadosUsuarioToken;
import br.gov.caixa.loterias.apostar.model.bo.DadosCorporativosBO;
import br.gov.caixa.loterias.apostar.model.bo.DadosCorporativosBO_;
import br.gov.caixa.loterias.apostar.model.bo.DadosUsuarioBO;
import br.gov.caixa.loterias.apostar.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostar.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.model.bo.keycloak.KeycloakBO;
import br.gov.caixa.loterias.apostar.model.bo.keycloak.KeycloakBO_;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.ApostadorDTOResponse;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.BairroDTO;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.BairroDTOResponse;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.CadastrarApostadorDTO;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.MunicipioIdDTO;
import br.gov.caixa.loterias.apostar.utils.AppUtils;
import br.gov.caixa.loterias.apostar.utils.Utils;
import br.gov.caixa.loterias.apostar.view.custom.LoadingViewLoterias;
import br.gov.caixa.loterias.apostar.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.utils.segmentedbutton.SegmentedButtonGroup;


@EActivity(R.layout.activity_cadastrar)

public class CadastrarActivity extends CadastroActivity {

    private static final String FORMATO_YYYYMMDD = "yyyyMMdd";

    @ViewById
    EditText editTextNomeContent, editTextEmailContent, editTextMunicipioContent, editTextUFContent;

    @ViewById
    MaskedEditText editTextCpfContent, editTextCepContent;

    @ViewById
    Button buttonFinalizarCadastro;

    @ViewById
    SegmentedButtonGroup segmentedButtonSexo;

    @ViewById
    TextView textViewTituloCadastrar2;

    @Extra
    List<String> listUFString, listMunicipios;

    @ViewById
    AppCompatCheckBox aceiteTermo;

    @AfterViews
    protected void init() {
        super.createToolbar();
        loadFields();
        loadFields();
        addUFSpinner();
        if (cadastrarApostadorDTO == null) {
            cadastrarApostadorDTO = new CadastrarApostadorDTO();
            cadastrarApostadorDTO.setSexo( CadastrarApostadorDTO.SexoEnum.M );
            cadastrarApostadorDTO.setMunicipioId(new MunicipioIdDTO());
            cadastrarApostadorDTO.setAceitaReceberNoticias( false );
            List<ApostaDTO> apostas = new ArrayList<ApostaDTO>();
            cadastrarApostadorDTO.setApostas(apostas);
        }

        KeycloakBO keycloakBO = KeycloakBO_.getInstance_( CadastrarActivity.this );
        String acessToken = keycloakBO.getAccessToken();
        try {
            atualizaLayout(Utils.decodedToken( acessToken ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFields() {
        textViewTituloCadastrar2.setText(ViewUtils.textFuturaAndFuturaBold(this,"Que bom ver _você por aqui_ :)"));
    }

    public void addUFSpinner() {

        listUFString = new ArrayList<String>();
        listMunicipios = new ArrayList<String>();

        initOnclick();
        addListenerUF(editTextUFContent, listUFString);
        addListenerMunicipio(editTextMunicipioContent, listMunicipios, Boolean.TRUE);

        addRightImg((EditText) findViewById(R.id.editTextNomeContent));
        addRightImg((EditText) findViewById(R.id.editTextCepContent));
        addRightImg((EditText) findViewById(R.id.editTextCpfContent));
        addRightImg((EditText) findViewById(R.id.editTextEmailContent));
        addListenerFinalizar();

    }


    private void configuraNavigation() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_gradient, null));
    }

    private void addListenerFinalizar() {
        buttonFinalizarCadastro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (validarCamposObrigatorios()) {

                    final AlertDialog loadViewProgress = LoadingViewLoterias.show(CadastrarActivity.this);
                    DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_(CadastrarActivity.this);
                    dadosUsuarioBO.gravarUsuario(cadastrarApostadorDTO, new RequestListener<ApostadorDTOResponse>() {
                        @Override
                        public void onResponse(ApostadorDTOResponse response) {
                            loadViewProgress.dismiss();
                             startActivity( new Intent(CadastrarActivity.this, CadastrarConfirmacao.class ));
                             CadastrarActivity.this.finish();
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            loadViewProgress.dismiss();
                            ViewUtils.alert(CadastrarActivity.this, MensagensNetwork.setMensagem(error, CadastrarActivity.this));
                        }
                    });

                }
            }
        });
    }


    private void initOnclick() {

        this.segmentedButtonSexo.setOnClickedButtonListener(new SegmentedButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(int position) {
                CadastrarApostadorDTO.SexoEnum sexoEnum;
                if (position == 0) {
                    sexoEnum = CadastrarApostadorDTO.SexoEnum.M;
                } else {
                    sexoEnum = CadastrarApostadorDTO.SexoEnum.F;
                }
                cadastrarApostadorDTO.setSexo( sexoEnum );
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
        if (aceiteTermo.isChecked()) {
            cadastrarApostadorDTO.setAceitaTermosUso(Boolean.TRUE);
        } else {
            ViewUtils.alertTitulo(CadastrarActivity.this, R.string.label_atencao, this.getResources().getString( R.string.termos_nao_aceitos ));
            return false;
        }
        String campos = "";

        if (StringUtils.isEmpty(AppUtils.removeMasckCPF(editTextCpfContent.getText().toString()))) {
            campos += "\nCPF";
        }
        if (StringUtils.isEmpty(editTextNomeContent.getText().toString())) {
            campos += "\nNome";
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
        if (StringUtils.isEmpty(editTextEmailContent.getText().toString())) {
            campos += "\nE-mail";
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

            ViewUtils.alertTitulo(CadastrarActivity.this, R.string.label_atencao, this.getResources().getString( R.string.MA002 ) + " " + campos);
            return false;
        }
    }


    private void buscarBairroPorCep(String cep) {
        final AlertDialog loadViewProgress = LoadingViewLoterias.show(this);
        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(CadastrarActivity.this);
        dadosCorporativosBO.bairros(null, null, null, cep, new RequestListener<BairroDTOResponse>() {
            @Override
            public void onResponse(BairroDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getPayload() != null && !response.getPayload().isEmpty()) {
                    BairroDTO bairroDTO = response.getPayload().get(0);
                    editTextUFContent.setText(bairroDTO.getMunicipio().getUf().getSigla());
                    editTextMunicipioContent.setText(bairroDTO.getMunicipio().getNome());
                    cadastrarApostadorDTO.setMunicipioId(bairroDTO.getMunicipio().getId());
                } else {
                    limparBuscaPorCep();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                limparBuscaPorCep();
                RedirectNetwork.checkRedirect( error, CadastrarActivity.this );

            }
        });
    }

    private void limparBuscaPorCep() {
        editTextUFContent.setText("");
        editTextMunicipioContent.setText("");
        cadastrarApostadorDTO.setMunicipioId(null);
    }




    private void atualizaLayout(DadosUsuarioToken dadosUsuarioToken) {
        editTextCpfContent.setMaskedText( dadosUsuarioToken.getCpf() );
        editTextNomeContent.setText( dadosUsuarioToken.getName() );
        editTextEmailContent.setText( dadosUsuarioToken.getEmail() );
        editTextCpfContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
        editTextNomeContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
        editTextEmailContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);

    }
}
