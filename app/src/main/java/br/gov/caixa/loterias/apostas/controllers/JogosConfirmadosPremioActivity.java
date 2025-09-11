package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;

public class JogosConfirmadosPremioActivity extends LoteriasBaseAppActivity {

    Boolean tipoCaixa = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogos_confirmados_premio);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tipoCaixa = extras.getBoolean(getResources().getString(R.string.tipo_caixa));
        }
        montarLayout();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);

        return true;
    }

    // Mudando o texto dos MenuItem de acordo com os dados
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            abrirTermosUso();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void abrirTermosUso(){
        Intent activity = new Intent(JogosConfirmadosPremioActivity.this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private void montarLayout(){
        if(tipoCaixa == false){
            Bitmap bitmap;
            ImageView qrCodeImage = findViewById(R.id.imagemQrCode);
            LinearLayout lltQrCode = findViewById(R.id.lltQrCode);
            // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
            QRGEncoder qrgEncoder = new QRGEncoder(getResources().getString(R.string.teste_xxx), null, QRGContents.Type.TEXT, 105);
            try {
                // Getting QR-Code as Bitmap
                bitmap = qrgEncoder.encodeAsBitmap();
                // Setting Bitmap to ImageView
                qrCodeImage.setImageBitmap(bitmap);
                qrCodeImage.setVisibility(View.VISIBLE);
                lltQrCode.setVisibility(View.VISIBLE);
            } catch (WriterException e) {
            }
        }

    }
}
