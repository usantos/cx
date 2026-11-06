package br.gov.caixa.loterias.apostas.controllers;

import android.content.Intent;
import android.os.Bundle;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import br.gov.caixa.loterias.apostas.LoteriasBaseAppActivity;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.internals.Crypto;
import br.gov.caixa.loterias.apostas.model.enums.LeituraBilheteEnum;
import br.gov.caixa.loterias.apostas.utils.BilheteUtils;
import br.gov.caixa.loterias.apostas.utils.IntentUtil;
import br.gov.caixa.loterias.apostas.utils.MaskEditUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class InsercaoManualCodBarrasActivity extends LoteriasBaseAppActivity {

    EditText editTextNumeroBilhete, editTextLoterico;
    Button buttonContinuarInsercaoManualBilhetes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insercao_manual_cod_barras);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle( ViewUtils.textCaixaSTDBold(this, getString(R.string.label_ler_bilhestes_bold)));

        InputFilter inputFilter = (charSequence, start, end, spanned, dstart, dend) -> {
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = start; i < end; i++){
                Character character = charSequence.charAt(i);
                character = Character.toUpperCase(character);
                stringBuilder.append(character);
            }
            return stringBuilder.toString();
        };

        buttonContinuarInsercaoManualBilhetes = findViewById(R.id.buttonContinuarInsercaoManualBilhetes);

        editTextLoterico = findViewById(R.id.editTextLoterico);
        //editTextLoterico.addTextChangedListener(textWatcher2);
        editTextLoterico.setFilters(new InputFilter[]{inputFilter});


        editTextNumeroBilhete = findViewById(R.id.editTextNumeroBilhete);
        editTextNumeroBilhete.addTextChangedListener(textWatcher);
        editTextNumeroBilhete.setFilters(new InputFilter[]{inputFilter});
        buttonContinuarInsercaoManualBilhetes.setOnClickListener(v -> {
            if(editTextNumeroBilhete.getText().toString().trim().length() == 0){
                Toast.makeText(InsercaoManualCodBarrasActivity.this, getResources().getString(R.string.informe_codigo_bilhete), Toast.LENGTH_SHORT).show();
            }else{
                try {
                    String loterico = removerMascaraLoterico(editTextLoterico).replace(".","");

                    String code;
                    LeituraBilheteEnum tipo;
                    if (loterico != null && !loterico.isEmpty()){
                        String nsbSemMascara = removerMascara(editTextNumeroBilhete);
                        code = Crypto.cripfyBarcode(nsbSemMascara+"=="+loterico);
                        tipo = LeituraBilheteEnum.QR_CODE;
                    } else {
                        code = Crypto.cripfyBarcode(removerMascara(editTextNumeroBilhete));
                        tipo = LeituraBilheteEnum.CODIGO_BARRAS;
                    }
                    String nsbFormatado = BilheteUtils.getNSBFormatado(editTextNumeroBilhete.getText().toString());
                    //ResultadoLerBilhetesActivity_.intent(InsercaoManualCodBarrasActivity.this)
                    //                             .codBarras(code).nsb(nsbFormatado)
                    //                             .tipoLeitura(tipo).start();
                    Intent intent = IntentUtil.getIntentOrigemDestino(this, ResultadoLerBilhetesActivity.class);
                    intent.putExtra(ResultadoLerBilhetesActivity.COD_BARRAS_EXTRA, code);
                    intent.putExtra(ResultadoLerBilhetesActivity.NSB_EXTRA, nsbFormatado);
                    intent.putExtra(ResultadoLerBilhetesActivity.TIPO_LEITURA_EXTRA, tipo);
                    startActivity(intent);

                    //ResultadoLerBilhetesActivity_.intent(InsercaoManualCodBarrasActivity.this).codBarras(code).start();
                } catch (NoSuchAlgorithmException e) {
                } catch (InvalidKeySpecException e) {
                } catch (NoSuchPaddingException e) {
                } catch (BadPaddingException e) {
                } catch (IllegalBlockSizeException e) {
                } catch (InvalidKeyException e) {
                }
             }
        });

        editTextLoterico.addTextChangedListener(MaskEditUtil.mask(editTextLoterico, MaskEditUtil.FORMAT_LOT, null));
        editTextNumeroBilhete.addTextChangedListener(MaskEditUtil.mask(editTextNumeroBilhete, MaskEditUtil.FORMAT_NSBI, null));
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

    private void abrirTermosUso() {
        Intent activity = new Intent(this, TermosUsoActivity.class);
        startActivity(activity);
    }

    private String removerMascara(EditText maskedEditText){
        return maskedEditText.getText().toString().replaceAll(getResources().getString(R.string.traco), getResources().getString(R.string.string_vazia));
    }
    private String removerMascaraLoterico(EditText maskedEditText){
        return maskedEditText.getText().toString().replaceAll(getResources().getString(R.string.traco), getResources().getString(R.string.string_vazia).replace(getResources().getString(R.string.ponto), getResources().getString(R.string.string_vazia)));
    }
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (editTextNumeroBilhete.getText().toString().equals("") || editTextNumeroBilhete.getText().toString().length() < 23) {
                buttonContinuarInsercaoManualBilhetes.setEnabled(false);
                buttonContinuarInsercaoManualBilhetes.setTextColor(getResources().getColor(R.color.cinzaDisable));
                buttonContinuarInsercaoManualBilhetes.setBackgroundColor(getResources().getColor(R.color.cinzaclaro));
                editTextNumeroBilhete.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                buttonContinuarInsercaoManualBilhetes.setEnabled(true);
                buttonContinuarInsercaoManualBilhetes.setTextColor(getResources().getColor(R.color.branco));
                buttonContinuarInsercaoManualBilhetes.setBackgroundColor(getResources().getColor(R.color.verdeazul));

                editTextNumeroBilhete.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
            }
        }
    };

    private TextWatcher textWatcher2 = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (editTextLoterico.getText().toString().equals("")) {
                buttonContinuarInsercaoManualBilhetes.setEnabled(false);
                buttonContinuarInsercaoManualBilhetes.setTextColor(getResources().getColor(R.color.cinzaDisable));
                buttonContinuarInsercaoManualBilhetes.setBackgroundColor(getResources().getColor(R.color.cinzaclaro));
                editTextLoterico.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            } else {
                if(!editTextLoterico.getText().toString().equals("") && !editTextNumeroBilhete.getText().toString().equals("")){
                    buttonContinuarInsercaoManualBilhetes.setEnabled(true);
                    buttonContinuarInsercaoManualBilhetes.setTextColor(getResources().getColor(R.color.branco));
                    buttonContinuarInsercaoManualBilhetes.setBackgroundColor(getResources().getColor(R.color.verdeazul));
                }
                editTextLoterico.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_input_ok, 0);
            }
        }
    };
}
