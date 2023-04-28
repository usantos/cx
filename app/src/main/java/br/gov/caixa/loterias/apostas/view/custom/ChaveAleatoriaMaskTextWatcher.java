package br.gov.caixa.loterias.apostas.view.custom;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.gov.caixa.loterias.apostas.view.listener.TextWatcherListener;

public class ChaveAleatoriaMaskTextWatcher implements TextWatcher {
	private EditText editText;
	private TextWatcherListener listener;
	private static final String PIX_REGEX = "^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}$";
	public static boolean isPixValid(String pix)  {
		Pattern pattern = Pattern.compile(PIX_REGEX);
		Matcher matcher = pattern.matcher(pix);
		return matcher.matches();
	};

	public ChaveAleatoriaMaskTextWatcher(EditText editText, TextWatcherListener listener) {this.editText = editText;
		this.listener = listener;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (editText.getText().toString().isEmpty()){
			listener.isValid(false);
		} else {
//			8e2c2171-0d58-41bb-9101-6934e87dea25
			if (isPixValid(editText.getText().toString())){
				listener.isValid(true);
			}
			else {
				listener.isValid(false);
			}
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
