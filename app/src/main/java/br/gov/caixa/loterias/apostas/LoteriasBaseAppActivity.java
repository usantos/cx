package br.gov.caixa.loterias.apostas;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import br.gov.caixa.loterias.apostas.utils.AppUtils;

public abstract class LoteriasBaseAppActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        AppUtils.applyInsets(this);
		//setSystenBarsColors(R.color.colorAccent);

	}

	public void setSystenBarsColors(int color){
		//getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, color));
	}
}