package br.gov.caixa.loterias.apostas.view.custom;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.gov.caixa.loterias.apostas.utils.StringUtils;
import br.gov.caixa.loterias.apostas.view.listener.TextWatcherListener;

public class MoedaMaskTextWatcher implements TextWatcher {
	private EditText editText;
	private String current = "";
	public MoedaMaskTextWatcher(EditText editText) {
		this.editText = editText;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
		if (charSequence.toString().length() == 0) {
			current = charSequence.toString();
			return;
		}
		if (!charSequence.toString().equals(current)) {
			editText.removeTextChangedListener(this);
			String cleanString = charSequence.toString().replaceAll("[,.]", "");
			double parsed = Double.parseDouble(cleanString);
			String formated = StringUtils.formatToCurrency(parsed/100);
			current = formated;
			editText.setText(formated);
			editText.setSelection(formated.length());
			editText.addTextChangedListener(this);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
	}
}
