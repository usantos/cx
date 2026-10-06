package br.gov.caixa.loterias.apostas.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyTextWatcher implements TextWatcher {
	private final WeakReference<EditText> editTextWeakReference;
	private final Locale locale = Locale.getDefault();

	public MoneyTextWatcher(EditText editText) {
		this.editTextWeakReference = new WeakReference<>(editText);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable editable) {
		EditText editText = editTextWeakReference.get();
		if (editText == null) return;
		editText.removeTextChangedListener(this);

		BigDecimal parsed    = parseToBigDecimal(editable.toString());
		String     formatted = NumberFormat.getCurrencyInstance(locale).format(parsed);

		String replaceable = String.format("[%s\\s]", getCurrencySymbol());
		String cleanString = formatted.replaceAll(replaceable, "");

		editText.setText(formatted);
		editText.setSelection(formatted.length());
		editText.addTextChangedListener(this);
	}

	private BigDecimal parseToBigDecimal(String value) {
		String replaceable = String.format("[%s,.\\s]", getCurrencySymbol());

		String cleanString = value.replaceAll(replaceable, "");

		try {
			return new BigDecimal(cleanString).setScale(
					2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
		} catch (NumberFormatException e) {
			return new BigDecimal(0);
		}
	}

	public static String formatPrice(String price) {
		DecimalFormat df = new DecimalFormat("0.00");
		return String.valueOf(df.format(Double.valueOf(price)));

	}

	public static String formatTextPrice(String price) {
		BigDecimal bD = new BigDecimal(formatPriceSave(formatPrice(price)));
		String newFormat = String.valueOf(NumberFormat.getCurrencyInstance(Locale.getDefault()).format(bD));
		String replaceable = String.format("[%s]", getCurrencySymbol());
		return newFormat.replaceAll(replaceable, "");

	}

	public static String formatPriceSave(String price) {
		String replaceable = String.format("[%s,.\\s]", getCurrencySymbol());
		String cleanString = price.replaceAll(replaceable, "");
		StringBuilder stringBuilder = new StringBuilder(cleanString.replaceAll(" ", ""));

		return String.valueOf(stringBuilder.insert(cleanString.length() - 2, '.'));

	}

	public static String getCurrencySymbol() {
		return NumberFormat.getCurrencyInstance(Locale.getDefault()).getCurrency().getSymbol();

	}
}