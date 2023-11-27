package br.gov.caixa.loterias.apostas.view.custom;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.utils.InputUtils;
import br.gov.caixa.loterias.apostas.view.listener.NomeTimeTextWatcherListener;
import br.gov.caixa.loterias.apostas.view.listener.TextWatcherListener;

public class NomeTimemaniaTextWatcher implements TextWatcher {

	private boolean isChanged = false;
	private Activity activity;
	private final List<ParametroEquipe> equipes;
	private NomeTimeTextWatcherListener listener;
	private EditText editText;

	public NomeTimemaniaTextWatcher(Activity activity, EditText editText,
									List<ParametroEquipe> parametroEquipeList,
									NomeTimeTextWatcherListener listener) {
		this.activity = activity;
		this.equipes = parametroEquipeList;
		this.listener = listener;
		this.editText = editText;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (isChanged || s.toString().contains("-")) {
			return;
		}
		if (!isChanged && s.toString().contains("\n")) {
			isChanged = true;
			String textoModificado = s.toString().replace("\n","");
			editText.setText(textoModificado);
			editText.setSelection(textoModificado.length());
			isChanged = false;
			InputUtils.closeKeyboard(activity);
			return;
		}

		isChanged = true;

		listener.limparFiltro();
		listener.isValido(false);
		if (StringUtils.isNotEmpty(s.toString())) {
			List<ParametroEquipe> filtrada = new ArrayList<>();
			for (ParametroEquipe parametroEquipe : equipes) {
				if (parametroEquipe.getNome().toUpperCase().contains(s.toString().toUpperCase())) {
					filtrada.add(parametroEquipe);
				}
			}
			listener.addFiltro(filtrada);
		} else {
			listener.addFiltro(equipes);
		}


		isChanged = false;
	}

	@Override
	public void afterTextChanged(Editable s) {}
}
