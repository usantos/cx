package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import br.gov.caixa.loterias.apostas.R;

public class FiltraMarketplaceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_filtra_marketplace );
        //setContentView( R.layout.activity_dados_pessoais );
        //setContentView( R.layout.activity_forma_pagamento );
        //setContentView( R.layout.activity_carrinho );

//        // Cria um slider
//        Slider slider = findViewById(R.id.sliderQuantidadeDezena);
//        slider.setValueFrom(1);
//        slider.setValueTo(100);
//        slider.setValue(50);
//        // Adiciona um ouvinte para o evento de mudança de valor
//        slider.addOnChangeListener((slider1, value, fromUser) -> {
//            // Faz algo quando o valor do slider mudar
//        });

        setaViews();
        //setaMetodos();
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

//    @Override
//    public boolean onSupportNavigateUp() {
//        finish();
//        return true;
//    }

    private void setaViews() {

        TextView accordionTitle = findViewById(R.id.accordion_quero);
        LinearLayout accordionContentQuero = findViewById(R.id.accordionContentQuero);
        AppCompatImageView accordionQueroButton = findViewById(R.id.img_accordion_quero);

        accordionQueroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (accordionContentQuero.getVisibility() == View.VISIBLE) {
                    accordionContentQuero.setVisibility(View.GONE);
                } else {
                    accordionContentQuero.setVisibility(View.VISIBLE);
                }
            }
        });


//        editTextCpfContent = findViewById( R.id.editTextCpfContent );
//        editTextNomeContent = findViewById( R.id.editTextNomeContent );
//        editTextCepContent = findViewById( R.id.editTextCepContent );
//        editTextUFContent = findViewById( R.id.editTextUFContent );
//        editTextMunicipioContent = findViewById( R.id.editTextMunicipioContent );
//        editTextEmailContent = findViewById( R.id.editTextEmailContent );
//        segmentedButtonSexo = findViewById( R.id.segmentedButtonSexo );
//        updateDadosPesoaisButton = findViewById( R.id.updateDadosPesoaisButton );
//        updateDadosPessoaisLimiteButton = findViewById( R.id.updateDadosPessoaisLimiteButton );
//
//        Toolbar toolbar = findViewById( R.id.toolbar );
//        TextView subtitleToolbar = findViewById( R.id.subtitleToolbar );
//
//        seekBarValor = (TextView) findViewById(R.id.seekbar_valor);
//
//        editValMinCompraContent =  findViewById(R.id.seekbar_valor_min);
//        editValMaximoCompraContent = findViewById(R.id.seekbar_valor_max);
//
//        seekbarLimite = (SeekBar) findViewById(R.id.seekBar);
//
//        seekbarLimite.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(apostadorDTO != null && apostadorDTO.getLimiteMinimoDiario() != null && progress <= apostadorDTO.getLimiteMinimoDiario().intValue()) {
//                    progress = apostadorDTO.getLimiteMinimoDiario().intValue();
//                }
//                progressChangedValue = progress;
//                seekBarValor.setText(NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(progress));
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
//        ufSelect  = getResources().getString(R.string.string_vazia);
//        municipioSelect = getResources().getString(R.string.string_vazia);
//
//        setSupportActionBar( toolbar );
//        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
//
//        setTitle( ViewUtils.textFuturaAndFuturaBoldTitle( this, getTitle().toString() ) );
//        subtitleToolbar.setText( getResources().getString(R.string.seus_dados_pessoais) );
    }

//    private void setaMetodos() {
//        editTextMunicipioContent.setClickable(true);
//        editTextUFContent.setClickable(true);
//        updateDadosPesoaisButton.setOnClickListener(view -> {
//
//        }
//        );
//
//        updateDadosPessoaisLimiteButton.setOnClickListener(view -> {
//
//            AlertDialogUtils.show(FiltraMarketplaceActivity.this);
//            final DadosUsuarioBO dadosUsuarioBO = DadosUsuarioBO_.getInstance_( this );
//
//            dadosUsuarioBO.recalcularLimite( new RequestListener<ApostadorDTOResponse>() {
//                @Override
//                public void onResponse(ApostadorDTOResponse response) {
//                    apostadorDTO = response.getPayload();
//
//                    editValMaximoCompraContent.setText(NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteDiarioAutorizado()));
//                    updateDadosPessoaisLimiteButton.setText(getString(R.string.dados_limite_compra) +
//                            NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(apostadorDTO.getLimiteDiarioAutorizado())
//                    );
//
//                    AppCenterManager.registraEvento("ENTROU_RECALCULAR_LIMITE_AUTORIZADO");
//                    AlertDialogUtils.dismiss();
//                }
//
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    AlertDialogUtils.dismiss();
//                    RedirectNetwork.checkRedirect( error, FiltraMarketplaceActivity.this );
//                }
//            });
//        });
//    }




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
