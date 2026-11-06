package br.gov.caixa.loterias.apostas.view.custom;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;

import br.gov.caixa.loterias.apostas.view.listener.TextWatcherListener;

public class EmailMaskTextWatcher implements TextWatcher {
	private EditText editText;
	private TextWatcherListener listener;

	public EmailMaskTextWatcher(EditText editText, TextWatcherListener listener) {this.editText = editText;
		this.listener = listener;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String email = editText.getText().toString();
		if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			listener.isValid(true);
		} else {
			listener.isValid(false);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
