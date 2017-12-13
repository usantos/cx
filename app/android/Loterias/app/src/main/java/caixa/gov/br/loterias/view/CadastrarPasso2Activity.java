package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.DadosCorporativosBO;
import caixa.gov.br.loterias.bo.silce.SilceClient;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumInteger;
import caixa.gov.br.loterias.bo.silce.dto.ListDTOEnumIntegerResponse;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.utils.segmentedbutton.SegmentedButton;
import caixa.gov.br.loterias.utils.segmentedbutton.SegmentedButtonGroup;

@EActivity(R.layout.activity_cadastrar_passo2)
public class CadastrarPasso2Activity extends CadastroActivity {

    @ViewById
    SegmentedButton SegmentedButtonCodigoLoterica;

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
    EditText editTextUFPasso2Content, editTextMunicipioPasso2Content, editTextBairroPasso2Content;

    @Extra
    List<String> listUFString, listMunicipios, listBairros;


    @AfterViews
    protected void init() {
        super.createToobar();
        addListenerFinalizar();
        initOnclick();
        initRightImg();

        listUFString = new ArrayList<String>();
        listMunicipios = new ArrayList<String>();
        listBairros = new ArrayList<String>();

        addListenerUF(editTextUFPasso2Content, listUFString);
        addListenerMunicipio(editTextMunicipioPasso2Content, listMunicipios, Boolean.FALSE);
        addListenerBairro(editTextBairroPasso2Content, listBairros);
        buscarLotericasHabilitadas();

    }

    private void initRightImg() {
        addRightImg((EditText) findViewById(R.id.editTextBairroPasso2Content));
        addRightImg((EditText) findViewById(R.id.editTextCEPPasso2Content));
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

    private void addListenerFinalizar() {
        buttonFinalizarCadastro.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), PrincipalActivity.class);
                startActivity(intent);
            }
        });
    }

    private void buscarLotericasHabilitadas() {
        final View loadViewProgress = LoadingViewLoterias.init(this);
        DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(CadastrarPasso2Activity.this);
        dadosCorporativosBO.buscarLotericasHabilitadas(new SilceClient.Listener<ListDTOEnumIntegerResponse>() {
            @Override
            public void onResponse(ListDTOEnumIntegerResponse response) {
                LoadingViewLoterias.stop(loadViewProgress);
                for(DTOEnumInteger dtoEnumInteger : response.getPayload()){
                }
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(CadastrarPasso2Activity.this, "ERRO");
            }
        });

    }
}
