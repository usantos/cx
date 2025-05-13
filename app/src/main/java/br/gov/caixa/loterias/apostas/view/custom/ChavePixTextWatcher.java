package br.gov.caixa.loterias.apostas.view.custom;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;

import java.util.regex.Pattern;

import br.gov.caixa.loterias.apostas.view.listener.TextWatcherListener;

public class ChavePixTextWatcher implements TextWatcher {
	private static final String REGEX_PHONE = "^\\d{11}$";
	private static final Pattern RANDOM_KEY_PATTERN =
			Pattern.compile("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$");
	private TextWatcherListener listener;

	public ChavePixTextWatcher(TextWatcherListener listener) {
		this.listener = listener;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (s == null || s.toString().isEmpty()){
			return;
		}

		String input = s.toString();

		if (isPhoneKey(input)){
			listener.isValid(true);
		} else if (isEmailKey(input)){
			listener.isValid(true);
		} else if (isRandomKey(input)){
			listener.isValid(true);
		} else {
			listener.isValid(false);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {}

	private boolean isPhoneKey(String chavePix){
		return chavePix.matches(REGEX_PHONE);
	}

	private boolean isEmailKey(String chavePix){
		return Patterns.EMAIL_ADDRESS.matcher(chavePix).matches();
	}

	private boolean isRandomKey(String chavePIx){
		return RANDOM_KEY_PATTERN.matcher(chavePIx).matches();
	}

}
