package br.gov.caixa.loterias.apostas.view.listener;

import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;

public interface TipoApostaListener {
	void onItemClicked(TipoAposta tipoAposta, int position);

}
