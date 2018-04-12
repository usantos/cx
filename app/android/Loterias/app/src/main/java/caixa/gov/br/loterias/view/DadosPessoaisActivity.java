package caixa.gov.br.loterias.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.model.bo.DadosCorporativosBO_;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO;
import caixa.gov.br.loterias.model.bo.DadosUsuarioBO_;
import caixa.gov.br.loterias.model.bo.MensagensNetwork;
import caixa.gov.br.loterias.model.bo.RedirectNetwork;
import caixa.gov.br.loterias.model.bo.RequestListener;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostadorAlteracaoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostadorDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.ApostadorDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.BairroDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.BairroDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.CadastrarApostadorDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.MunicipioDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.MunicipioDTOResponse;
import caixa.gov.br.loterias.model.bo.silce.dto.MunicipioIdDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.SexoEnum;
import caixa.gov.br.loterias.model.bo.silce.dto.UnidadeFederacaoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.UnidadeFederacaoDTOResponse;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.utils.segmentedbutton.SegmentedButtonGroup;
import caixa.gov.br.loterias.view.custom.LoadingViewLoterias;

public class DadosPessoaisActivity extends AppCompatActivity {

    EditText editTextNomeContent, editTextUFContent, editTextMunicipioContent, editTextEmailContent;
    MaskedEditText editTextCpfContent, editTextCepContent;
    SegmentedButtonGroup segmentedButtonSexo;
    Button updateDadosPesoaisButton;
    private List<String> listUFString, listMunicipios, listBairros;
    private List<MunicipioDTO> municipioDTOList;
    protected List<UnidadeFederacaoDTO> listUf;
    protected String ufSelect = "";
    protected String municipioSelect = "";

    protected MunicipioIdDTO municipioIdDTOSelect;

    ApostadorDTO apostadorDTO;
    AlertDialog loadViewProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_dados_pessoais );
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        TextView subtitleToolbar = (TextView) findViewById( R.id.subtitleToolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        setTitle( ViewUtils.textFuturaAndFuturaBold( this, getTitle().toString() ) );
        subtitleToolbar.setText( "Estes são seus dados pessoais.\nPor favor, mantenha-os sempre atualizados." );
        montarLayout();
        callWebservice();
    }

    private void montarLayout() {
        editTextCpfContent = (MaskedEditText) findViewById( R.id.editTextCpfContent );
        editTextNomeContent = (EditText) findViewById( R.id.editTextNomeContent );
        editTextCepContent = (MaskedEditText) findViewById( R.id.editTextCepContent );
        editTextUFContent = (EditText) findViewById( R.id.editTextUFContent );
        editTextMunicipioContent = (EditText) findViewById( R.id.editTextMunicipioContent );
        editTextEmailContent = (EditText) findViewById( R.id.editTextEmailContent );
        segmentedButtonSexo = (SegmentedButtonGroup) findViewById( R.id.segmentedButtonSexo );
        updateDadosPesoaisButton = (Button) findViewById( R.id.updateDadosPesoaisButton );

        addRightImg((EditText) findViewById(R.id.editTextNomeContent));
        addRightImg((MaskedEditText) findViewById(R.id.editTextCepContent));
        addRightImg((MaskedEditText) findViewById(R.id.editTextCpfContent));
        addRightImg((EditText) findViewById(R.id.editTextEmailContent));

        editTextMunicipioContent.setClickable(true);
        editTextMunicipioContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getMunicipios();
            }
        });

        editTextUFContent.setClickable(true);
        editTextUFContent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getUFS();
            }
        });

        editTextCepContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    buscarBairroPorCep( editTextCepContent.getUnmaskedText() );
                }
            }
        });

        updateDadosPesoaisButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextUFContent.getText().toString().equals( "" ) &&
                        !editTextMunicipioContent.getText().toString().equals( "" ) ||
                        !editTextCepContent.getText().toString().equals( "" )
                        ){
                    ApostadorAlteracaoDTO apostadorAlteracaoDTO = new ApostadorAlteracaoDTO();
                    apostadorAlteracaoDTO.setCep( editTextCepContent.getUnmaskedText() );
                    SexoEnum sexoEnum;
                    if (segmentedButtonSexo.getPosition() == 0){
                        sexoEnum = SexoEnum.MASCULINO;
                    }else {
                        sexoEnum = SexoEnum.FEMININO;
                    }
                    apostadorAlteracaoDTO.setSexo( sexoEnum);
                    apostadorAlteracaoDTO.setMunicipioId( municipioIdDTOSelect );
                    updateDadosCadastrais( apostadorAlteracaoDTO );
                }else{
                    ViewUtils.alertTitulo( DadosPessoaisActivity.this, R.string.label_atencao, "Por favor, preencha todos os campos." );
                }

            }
        } );


    }

    private void atualizaLayout() {
        editTextCpfContent.setMaskedText( apostadorDTO.getCpf() );
        editTextNomeContent.setText( apostadorDTO.getNome() );
        editTextCepContent.setMaskedText( apostadorDTO.getCep() );
        editTextEmailContent.setText( apostadorDTO.getEmail() );
        editTextCpfContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
        editTextNomeContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
        editTextCepContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
        editTextEmailContent.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
        if (apostadorDTO.getSexo().equals( ApostadorDTO.SexoEnum.F )){
            segmentedButtonSexo.setPosition( 1 );
        }else{
            segmentedButtonSexo.setPosition( 0 );
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }


    private void callWebservice() {

        loadViewProgress = LoadingViewLoterias.show( this );
        final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_( this );

        dadosUsuarioBO.getDadosUsuario( new RequestListener<ApostadorDTOResponse>() {
            @Override
            public void onResponse(ApostadorDTOResponse response) {
                apostadorDTO = response.getPayload();
                buscarBairroPorCep( response.getPayload().getCep() );
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect().toString(), DadosPessoaisActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, DadosPessoaisActivity.this );
            }
        } );
    }

    private void getUFS() {
        DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(DadosPessoaisActivity.this);
        loadViewProgress.show();
        dadosCorporativosBO.ufs(new RequestListener<UnidadeFederacaoDTOResponse>() {
            @Override
            public void onResponse(UnidadeFederacaoDTOResponse response) {
                response.getRedirect().toString();
                loadViewProgress.dismiss();
                listUFString = new ArrayList<String>();
                listUf = response.getPayload();
                for (UnidadeFederacaoDTO uf : response.getPayload()) {
                    listUFString.add(uf.getSigla());
                }
                modalUfs();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect().toString(), DadosPessoaisActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, DadosPessoaisActivity.this );
            }
        });
    }

    private void getMunicipios() {
        String idUf = "";

        if (ufSelect.isEmpty()) {
            showDialog();
        } else {
            for (UnidadeFederacaoDTO uf : listUf) {
                if (uf.getSigla().equalsIgnoreCase(ufSelect)) {
                    idUf = uf.getId().toString();
                }
            }
            loadViewProgress.show();
            listMunicipios = new ArrayList<String>();

            DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(DadosPessoaisActivity.this);
            dadosCorporativosBO.municipios(idUf, new RequestListener<MunicipioDTOResponse>() {
                @Override
                public void onResponse(MunicipioDTOResponse response) {
                    loadViewProgress.dismiss();
                    listMunicipios = new ArrayList<String>();
                    municipioDTOList = response.getPayload();
                    for (MunicipioDTO municipio : municipioDTOList) {
                        listMunicipios.add(municipio.getNome());
                    }
                    modalMunicipios();
                    if (response.getRedirect() != null){
                        RedirectNetwork.checkRedirectSucesso( response.getRedirect().toString(), DadosPessoaisActivity.this);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    loadViewProgress.dismiss();
                    RedirectNetwork.checkRedirect( error, DadosPessoaisActivity.this );
                }
            });
        }
    }

    private void modalUfs() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button

        final ListView listViewItens = (ListView) dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = (TextView) dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText("UF");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DadosPessoaisActivity.this,
                R.layout.item_text_popup, R.id.textList, listUFString);

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
                ufSelect = itemValue;
            }

        });

        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = (Button) dialog.findViewById(R.id.dialogButtonCancelar);

        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ufSelect.equals( editTextUFContent.getText().toString() )){
                    editTextMunicipioContent.setText( "" );
                }
                editTextUFContent.setText(ufSelect);
                dialog.dismiss();
            }
        });

        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ufSelect = "";
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private AlertDialog myDialog;

    public void showDialog() {
        if (myDialog == null) {
            myDialog = new AlertDialog.Builder(this)
                    .setTitle("Atenção")
                    .setMessage("Selecione primeiro a UF, assim nossa precisão de municípios será incrível.")
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

    private void modalMunicipios() {
        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_list_view);

        // set the custom dialog components - listview and button

        final ListView listViewItens = (ListView) dialog.findViewById(R.id.listaItensDialog);
        TextView tituloDialog = (TextView) dialog.findViewById(R.id.tituloDialogCustom);
        tituloDialog.setText("Cidade");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DadosPessoaisActivity.this,
                R.layout.item_text_popup, R.id.textList, listMunicipios);

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
                municipioIdDTOSelect = municipioDTOList.get( position ).getId();
            }

        });

        Button dialogButtonOk = (Button) dialog.findViewById(R.id.dialogButtonOK);
        Button dialogButtonCancelar = (Button) dialog.findViewById(R.id.dialogButtonCancelar);

        // if button is clicked, close the custom dialog
        dialogButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextMunicipioContent.setText(municipioSelect);
                dialog.dismiss();
            }
        });

        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                municipioSelect = "";
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    protected void addRightImg(EditText textField) {
        textField.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {


            @Override
            public void onFocusChange(View v, boolean hasFocus) {
            /* When focus is lost check that the text field
            * has valid values.
            */
                if (!hasFocus) {
                    if (v.getClass() == MaskedEditText.class) {
                        if (((MaskedEditText) v).getUnmaskedText().toString().equals("")) {
                            ((MaskedEditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        } else {
                            ((MaskedEditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
                        }
                    } else {
                        if (((EditText) v).getText().toString().equals("")) {
                            ((EditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        } else {
                            ((EditText) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
                        }
                    }
                }
            }
        });
    }

    private void buscarBairroPorCep(String cep) {
        final DadosCorporativosBO dadosCorporativosBO = DadosCorporativosBO_.getInstance_(DadosPessoaisActivity.this);
        dadosCorporativosBO.bairros(null, null, null, cep, new RequestListener<BairroDTOResponse>() {
            @Override
            public void onResponse(BairroDTOResponse response) {
                loadViewProgress.dismiss();
                if (response.getPayload() != null && !response.getPayload().isEmpty()) {
                    BairroDTO bairroDTO = response.getPayload().get(0);
                    editTextUFContent.setText(bairroDTO.getMunicipio().getUf().getSigla());
                    editTextMunicipioContent.setText(bairroDTO.getMunicipio().getNome());
                    municipioIdDTOSelect = bairroDTO.getMunicipio().getId();
                }
                atualizaLayout();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect().toString(), DadosPessoaisActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                ViewUtils.alert(DadosPessoaisActivity.this, MensagensNetwork.setMensagem(error, DadosPessoaisActivity.this));
            }
        });
    }


    private void updateDadosCadastrais(ApostadorAlteracaoDTO apostadorAlteracaoDTO){
        final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_( this );
        loadViewProgress.show();
        dadosUsuarioBO.putDadosUsuario(apostadorAlteracaoDTO, new RequestListener<CadastrarApostadorDTOResponse>() {
            @Override
            public void onResponse(CadastrarApostadorDTOResponse response) {

                loadViewProgress.dismiss();
                if (response.getRedirect() != null){
                    RedirectNetwork.checkRedirectSucesso( response.getRedirect().toString(), DadosPessoaisActivity.this);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                loadViewProgress.dismiss();
                RedirectNetwork.checkRedirect( error, DadosPessoaisActivity.this );
            }
        });
    }
}
