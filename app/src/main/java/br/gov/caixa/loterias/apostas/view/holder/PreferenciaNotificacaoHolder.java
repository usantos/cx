package br.gov.caixa.loterias.apostas.view.holder;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.PreferenciaNotificacao;

public class PreferenciaNotificacaoHolder extends LoteriasHolder<PreferenciaNotificacao> {
	private TextView titulo, descricao;
	private Switch isAtivo;

	public PreferenciaNotificacaoHolder(View itemView) {
		super(itemView);
		titulo = itemView.findViewById(R.id.titulo_notificacao);
		descricao = itemView.findViewById(R.id.descricao_notificacao);
		isAtivo = itemView.findViewById(R.id.notificacao_switch);
	}

	@Override
	public void bind(PreferenciaNotificacao item, int position) {
		titulo.setText(item.getTitulo());
		if (item.getDescricao() != null && !item.getDescricao().isEmpty()){
			descricao.setText(item.getDescricao());
		} else {
			descricao.setVisibility(View.GONE);
		}

		isAtivo.setChecked(item.getAtivo());
	}
}
