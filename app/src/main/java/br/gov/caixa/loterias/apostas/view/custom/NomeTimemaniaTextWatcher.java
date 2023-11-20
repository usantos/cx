package br.gov.caixa.loterias.apostas.view.custom;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.EditText;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.view.listener.NomeTimeTextWatcherListener;
import br.gov.caixa.loterias.apostas.view.listener.TextWatcherListener;

public class NomeTimemaniaTextWatcher implements TextWatcher {

	private boolean isChanged = false;
	private List<ParametroEquipe> equipes;
	private NomeTimeTextWatcherListener listener;

	public NomeTimemaniaTextWatcher(List<ParametroEquipe> parametroEquipeList,
									NomeTimeTextWatcherListener listener) {
		this.equipes = parametroEquipeList;
		this.listener = listener;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (isChanged || s.toString().contains("-")) {
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
