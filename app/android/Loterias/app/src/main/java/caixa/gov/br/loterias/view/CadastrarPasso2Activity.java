package caixa.gov.br.loterias.view;

import android.content.Intent;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import caixa.gov.br.loterias.bo.silce.dto.LoteriasHabilitadaEnum;
import caixa.gov.br.loterias.bo.silce.dto.Loterica;
import caixa.gov.br.loterias.bo.silce.dto.LotericaDTO;
import caixa.gov.br.loterias.bo.silce.dto.LotericaDTOResponse;
import caixa.gov.br.loterias.bo.silce.dto.LotericaDTOResponseManual;
import caixa.gov.br.loterias.utils.LoadingViewLoterias;
import caixa.gov.br.loterias.utils.ViewUtils;
import caixa.gov.br.loterias.utils.segmentedbutton.SegmentedButton;
import caixa.gov.br.loterias.utils.segmentedbutton.SegmentedButtonGroup;

@EActivity(R.layout.activity_cadastrar_passo2)
public class CadastrarPasso2Activity extends CadastroActivity {


    @ViewById
    SegmentedButton segmentedButtonLocalizacao, segmentedButtonCEP, segmentedButtonCodigoLoterica;

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

    @ViewById
    RadioGroup radioListLoterias;

    @Extra
    List<String> listUFString, listMunicipios, listBairros;

    private List<DTOEnumInteger> listLoteriasHabilitadas;


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
                listLoteriasHabilitadas = response.getPayload();
                for (DTOEnumInteger dtoEnumInteger : listLoteriasHabilitadas) {
                    switch (LoteriasHabilitadaEnum.getTipoFromCodigo(dtoEnumInteger.getValor())) {
                        case CEP:
                            segmentedButtonCEP.setVisibility(View.VISIBLE);
                            break;
                        case CODIGO:
                            segmentedButtonCodigoLoterica.setVisibility(View.VISIBLE);
                            break;
                        default:
                            segmentedButtonLocalizacao.setVisibility(View.VISIBLE);
                    }
                }
                buscarLoteriasAutomatico();
            }
        }, new SilceClient.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoadingViewLoterias.stop(loadViewProgress);
                ViewUtils.alert(CadastrarPasso2Activity.this, "ERRO");
            }
        });
    }

    private void buscarLoteriasAutomatico() {
        if (segmentedButtonCEP.getVisibility() == View.VISIBLE) {
            final View loadViewProgress = LoadingViewLoterias.init(this);
            DadosCorporativosBO dadosCorporativosBO = new DadosCorporativosBO(CadastrarPasso2Activity.this);
            dadosCorporativosBO.buscarLotericas(apostadorDTO.getCep().toString().replace("-", ""), null, new SilceClient.Listener<LotericaDTOResponse>() {
                @Override
                public void onResponse(LotericaDTOResponse response) {
                    LoadingViewLoterias.stop(loadViewProgress);
                    //Loterica lotericaDTO : response.getPayload().getLotericas()
                    for (int i = 0; i < 2 ; i++) {
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        RadioButton radioButton = new RadioButton(CadastrarPasso2Activity.this);
                        radioButton.setLayoutParams(params);
                        radioButton.setButtonDrawable(R.drawable.check_radio_selected);
                        radioButton.setPadding(20, 20, 0, 10);
                        radioButton.setText("Nome da lotérica lorem mono dolor\nCódigo. 2538985");
                        radioButton.setTextSize(16);
                        radioButton.setTextColor(getResources().getColor(R.color.cinza));
                        radioButton.setScaleX(0.8f);
                        radioButton.setScaleY(0.8f);
                        radioListLoterias.addView(radioButton);
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
}
