package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.ApostaComboCarrinhoListener;
import br.gov.caixa.loterias.apostas.view.listener.ComboCarrinhoListener;

public class ApostaCarrinhoComboHolder extends LoteriasHolder<ComboApostaDTO> {
	private TextView tvDescricaoCombo;
	private TextView tvQtdApostasCombo;
	private TextView valorAposta;
	private ImageView imageDetalheCombos;

	private Context context;
	private AppCompatActivity activity;
	private ApostaComboCarrinhoListener listener;
	private ComboCarrinhoListener listenerCombo;


	public ApostaCarrinhoComboHolder(View view, AppCompatActivity activity, ApostaComboCarrinhoListener listener, ComboCarrinhoListener listenerCombo) {
		super(view);

		this.context = view.getContext();
		this.activity = activity;
		this.listener = listener;
		this.listenerCombo = listenerCombo;

		tvDescricaoCombo = view.findViewById(R.id.descricaoCombo);
		valorAposta = view.findViewById(R.id.valorAposta);
		tvQtdApostasCombo = view.findViewById(R.id.tvQtdApostasCombo);
		imageDetalheCombos = view.findViewById(R.id.imageViewDetalheCombos);
	}

	@Override
	public void bind(ComboApostaDTO comboAposta, int position) {

		this.tvDescricaoCombo.setText(comboAposta.getTipoCombo().getNome());

		if (comboAposta.getApostas() != null && comboAposta.getApostas().size() > 0) {
			BigDecimal valAposta = BigDecimal.ZERO;
			for (IdentificaoDeUmaApostaDas8Modalidades umaAposta : comboAposta.getApostas()) {
				valAposta = valAposta.add(umaAposta.getValor());
			}
			ViewUtils.setMoedaFormatHtmlCombo(valAposta, valorAposta);

			if (comboAposta.getApostas().size() == 1)
				this.tvQtdApostasCombo.setText(comboAposta.getApostas().size() + " aposta");
			else
				this.tvQtdApostasCombo.setText(comboAposta.getApostas().size() + " apostas");
		}

		this.itemView.findViewById(R.id.excluirApostaCarrinho)
				.setOnClickListener(view -> listener.onDeletaComboAposta(comboAposta));

		this.imageDetalheCombos.setOnClickListener(view -> listenerCombo.onComboClick(comboAposta));
	}

}
