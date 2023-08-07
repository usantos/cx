package br.gov.caixa.loterias.apostas.view.custom;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import br.gov.caixa.loterias.apostas.utils.MascaraUtil;
import br.gov.caixa.loterias.apostas.view.listener.TextWatcherListener;

public class CelularMaskTextWatcher implements TextWatcher {
	private Activity activity;
	private EditText editText;
	private int maxLength;
	private TextWatcherListener listener;

	private boolean isDeleting = false;

	public CelularMaskTextWatcher(Activity activity, EditText editText, int maxLength, TextWatcherListener listener) {
		this.activity = activity;
		this.editText = editText;
		this.maxLength = maxLength;
		this.listener = listener;

		this.editText.setOnKeyListener(onKeyListener());
	}

	private View.OnKeyListener onKeyListener() {
		return (v, keyCode, event) -> {

			if (keyCode == KeyEvent.KEYCODE_DEL){
				isDeleting = true;
			} else {
				isDeleting = false;
			}

			return false;
		};
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		editText.removeTextChangedListener(this);
		String celular = MascaraUtil.removeMascaraCelular(s.toString());
		switch (celular.length()){
			case 2:
				if (isDeleting){
					if (!s.toString().contains(")")){
						editText.setText(celular.substring(0,1));
					} else {
						editText.setText("(" + celular + ")");
					}
				} else {
					editText.setText("(" + celular + ")");
				}
				break;
			case 3:
				editText.setText("(" + celular.subSequence(0, 2) + ") " + celular.subSequence(2, 3));
				break;
			case 4:
				editText.setText("(" + celular.subSequence(0, 2) + ") " + celular.subSequence(2, 3) + " " + celular.substring(3, 4));
				break;
			case 7:
				if (isDeleting){
					editText.setText("(" + celular.subSequence(0, 2) + ") " + celular.subSequence(2, 3) + " " + celular.substring(3, 7));
				}
				break;
			case 8:
				editText.setText("(" + celular.subSequence(0, 2) + ") " + celular.subSequence(2, 3) + " " + celular.substring(3, 7) + "-" + celular.substring(7, 8));
				break;
		}
		editText.setSelection(editText.getText().length());
		editText.addTextChangedListener(this);

		if (editText.getText().toString().isEmpty() || editText.getText().toString().length() < maxLength){
			listener.isValid(false);
		} else {
			closeKeyboard();
			listener.isValid(true);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	private void closeKeyboard()
	{
		View view = activity.getCurrentFocus();
		if (view != null) {

			InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
}
