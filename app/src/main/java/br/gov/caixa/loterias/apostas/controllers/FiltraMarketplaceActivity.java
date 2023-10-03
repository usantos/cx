package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class FiltraMarketplaceActivity extends AppCompatActivity {

    private RangeSlider sliderValCotas;
    private RangeSlider sliderQtdCotas;
    //private Slider sliderQtdDezenas;
    //private AppCompatImageView accordionQueroButton;
    //private LinearLayout accordionContentQuero;
    //private AppCompatImageView accordionNaoQueroButton;
    //private LinearLayout accordionContentNaoQuero;
    private Button btnLimpaFiltros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_filtra_marketplace );

        setaViews();
        setaRangeComponentes();
        limpaFiltros();
        setaMetodos();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            //callWebservice();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //callWebservice();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setaViews() {

        //accordionContentQuero = findViewById(R.id.accordionContentQuero);
        //accordionQueroButton = findViewById(R.id.img_accordion_quero);
        //accordionContentNaoQuero = findViewById(R.id.accordionContentNaoQuero);
        //accordionNaoQueroButton = findViewById(R.id.img_accordion_nao_quero);
        //sliderQtdDezenas = findViewById(R.id.sliderQuantidadeDezena);

        sliderValCotas = findViewById(R.id.sliderValorCotas);
        sliderQtdCotas = findViewById(R.id.sliderQuantidadeCotas);
        btnLimpaFiltros = findViewById(R.id.btn_limpar_filtros);

        sliderValCotas.setLabelFormatter(new LabelFormatter() {
            @NonNull
            @Override
            public String getFormattedValue(float value) {
                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                format.getMaximumFractionDigits();
                format.setCurrency(Currency.getInstance("BRL"));
                return format.format(value);
            }
        });

        //Toolbar
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        setTitle( ViewUtils.textFuturaAndFuturaBoldTitle( this, getResources().getString(R.string.filtrar)));
    }

    private void setaMetodos() {
//        accordionQueroButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (accordionContentQuero.getVisibility() == View.VISIBLE) {
//                    accordionContentQuero.setVisibility(View.GONE);
//                    accordionQueroButton.setBackground(getDrawable(R.drawable.ic_accordion_plus));
//                } else {
//                    accordionContentQuero.setVisibility(View.VISIBLE);
//                    accordionQueroButton.setBackground(getDrawable(R.drawable.ic_accordion_minus));
//                }
//            }
//        });

//        accordionNaoQueroButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (accordionContentNaoQuero.getVisibility() == View.VISIBLE) {
//                    accordionContentNaoQuero.setVisibility(View.GONE);
//                    accordionNaoQueroButton.setBackground(getDrawable(R.drawable.ic_accordion_plus));
//                } else {
//                    accordionContentNaoQuero.setVisibility(View.VISIBLE);
//                    accordionNaoQueroButton.setBackground(getDrawable(R.drawable.ic_accordion_minus));
//                }
//            }
//        });

        btnLimpaFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpaFiltros();
            }
        });
    }

    private void setaRangeComponentes() {
        sliderValCotas.setValueFrom(0);
        sliderValCotas.setValueTo(300);
        sliderQtdCotas.setValueFrom(0);
        sliderQtdCotas.setValueTo(100);
    }

    private void limpaFiltros() {
        sliderValCotas.setValues(0F,300F);
        sliderQtdCotas.setValues(0F,100F);
    }

//    private void callWebservice() {
//        AlertDialogUtils.show(FiltraMarketplaceActivity.this);
//        final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_( this );
//
//        dadosUsuarioBO.getDadosUsuario( new RequestListener<ApostadorDTOResponse>() {
//            @Override
//            public void onResponse(ApostadorDTOResponse response) {
//                apostadorDTO = response.getPayload();
//                municipioIdDTOSelect = apostadorDTO.getMunicipioId();
//
//
//                seekbarLimite.setMax(apostadorDTO.getLimiteDiarioAutorizado().intValue());
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    seekbarLimite.setMin(apostadorDTO.getLimiteMinimoDiario().intValue());
//                }
//                seekbarLimite.setProgress(apostadorDTO.getLimiteDiario().intValue());
//
//
//                if (response.getRedirect() != null){
//                    RedirectNetwork.checkRedirectSucesso( response.getRedirect(), FiltraMarketplaceActivity.this);
//                }
//                AppCenterManager.registraEvento(getResources().getString(R.string.evento_entrou_editar_cadastro));
//                AlertDialogUtils.dismiss();
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                AlertDialogUtils.dismiss();
//                RedirectNetwork.checkRedirect( error, FiltraMarketplaceActivity.this );
//            }
//        } );
//    }

}
