package br.gov.caixa.loterias.apostas.utils;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import br.gov.caixa.loterias.apostas.view.fragment.CardPixFragment;

public class InputUtils {

	public static void closeKeyboard(Activity activity){
		View view = activity.getCurrentFocus();
		if (view != null) {

			InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

}
