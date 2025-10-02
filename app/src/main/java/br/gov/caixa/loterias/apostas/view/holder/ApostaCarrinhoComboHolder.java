package br.gov.caixa.loterias.apostas.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ComboApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.IdentificaoDeUmaApostaDas8Modalidades;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.model.enums.TipoApostaLinhaEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.fragment.TipoApostaLinhaView;
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
	private TipoApostaLinhaView tipoApostaLinhaView;


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
		tipoApostaLinhaView = view.findViewById(R.id.tipo_aposta_linha);
	}

	@Override
	public void bind(ComboApostaDTO comboAposta, int position) {

		this.tvDescricaoCombo.setText(comboAposta.getTipoCombo().getNome());

		boolean isSurpresinha = false;
		boolean isEspelho = false;
		int qtdTeimosinha = 0;
		if (comboAposta.getApostas() != null && comboAposta.getApostas().size() > 0) {
			BigDecimal valAposta = BigDecimal.ZERO;
			for (IdentificaoDeUmaApostaDas8Modalidades umaAposta : comboAposta.getApostas()) {
				valAposta = valAposta.add(umaAposta.getValor());
				isSurpresinha = umaAposta.getSurpresinha();
				if (!isEspelho) {
					isEspelho = umaAposta.getEspelho();
				}
				qtdTeimosinha += umaAposta.getQuantidadeTeimosinhas();
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

		EstiloModalidadeMKP estiloMKP = new EstiloModalidadeMKP(ModalidadeEnum.COMBO);
		List<TipoApostaLinhaEnum> listTipoApostaLinha = new ArrayList<>();

//		if (aposta.getTroca()) {
//			listTipoApostaLinha.add(TipoApostaLinhaEnum.TROCA);
//		}
		listTipoApostaLinha.add(TipoApostaLinhaEnum.COMBO);
		if (isEspelho) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.ESPELHO);
		}
		if (isSurpresinha) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.SURPRESINHA);
		}
		if (qtdTeimosinha > 0) {
			listTipoApostaLinha.add(TipoApostaLinhaEnum.TEIMOSINHA);
		}

		if (listTipoApostaLinha.size() > 0) {
			tipoApostaLinhaView.setVisibility(View.VISIBLE);
			tipoApostaLinhaView.setupView(listTipoApostaLinha, R.color.bolao_linha_clara, R.color.cinzaescuro, estiloMKP.getCorLetraLista(), TipoApostaLinhaView.TriangleDirection.UP);
		}
	}

}
