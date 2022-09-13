package br.gov.caixa.loterias.apostas.view.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.SituacaoCompraUtil;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalhesApostaLotogolDetalhesComprasAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaComprasAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ComprasDezenasView;
import br.gov.caixa.loterias.apostas.view.custom.ComprasDezenasView_;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;

public class CompraHolder extends LoteriasHolder<ApostaDTO> {

	private TextView tipoPremioListaComprasTextView;
	private TextView dataConcursoListaComprasTextView;
	private TextView concursoListaComprasTextView;
	private TextView statusResultadoApostaListaComprasTextView;
	private LinearLayout topListItemComprasLinearLayout;
	private LinearLayout bottomListItemComprasLinearLayout;
	private TextView seusNumerosItemListaComprasTextView;
	private LinearLayout itemComprasContentModalideLinearLayout;
	private TextView tvTeimosinhas;
	private DetalhesComprasActivity activity;

	public CompraHolder(View itemView, DetalhesComprasActivity activity) {
		super(itemView);
		tipoPremioListaComprasTextView = itemView.findViewById(R.id.TipoPremioListaComprasTextView);
		dataConcursoListaComprasTextView = itemView.findViewById(R.id.dataConcursoListaComprasTextView);
		concursoListaComprasTextView = itemView.findViewById(R.id.concursoListaComprasTextView);
		statusResultadoApostaListaComprasTextView = itemView.findViewById(R.id.statusResultadoApostaListaComprasTextView);
		topListItemComprasLinearLayout = itemView.findViewById(R.id.topListItemComprasLinearLayout);
		bottomListItemComprasLinearLayout = itemView.findViewById(R.id.bottomListItemComprasLinearLayout);
		seusNumerosItemListaComprasTextView = itemView.findViewById(R.id.seusNumerosItemListaComprasTextView);
		itemComprasContentModalideLinearLayout = itemView.findViewById(R.id.itemComprasContentModalideLinearLayout);
		tvTeimosinhas = itemView.findViewById(R.id.tv_teimosinhas);
		this.activity = activity;
	}

	@Override
	public void bind(ApostaDTO aposta, int position) {
		EstiloModalidade estilo = new EstiloModalidade(aposta.getModalidade());
		itemView.getContext().setTheme(estilo.getThemeId());
		int colorText = itemView.getContext().getResources().getColor(estilo.getMenuItemColor());
		int qtdTeimosinhas = aposta.getQuantidadeTeimosinhas();
		String teimosinhasTxt = qtdTeimosinhas + (qtdTeimosinhas > 1 ? itemView.getContext().getResources().getString(R.string.espaco_teimosinhas) : itemView.getContext().getResources().getString(R.string.espaco_teimosinha));

		String nomeAposta =  ViewUtils.getNomeModalidadePorAposta(aposta);

		this.tipoPremioListaComprasTextView
				.setText(ViewUtils.textFuturaAndFuturaBold(
						itemView.getContext(),
						itemView.getContext().getString(R.string.texto_futura_bold, nomeAposta)
														  )
						);

		this.tipoPremioListaComprasTextView.setTextColor(colorText);

		this.dataConcursoListaComprasTextView.setText(aposta.getDataEfetivacao());
		this.dataConcursoListaComprasTextView.setTextColor(colorText);

		this.concursoListaComprasTextView
				.setText(ViewUtils.textFuturaAndFuturaBold(itemView.getContext(),
														   itemView.getContext().getString(R.string.label_aposta_concurso_bold,
																			  aposta.getConcursoInicial())));
		this.concursoListaComprasTextView.setTextColor(colorText);

		this.statusResultadoApostaListaComprasTextView.setText(aposta.getSituacao().getDescricao());
		this.statusResultadoApostaListaComprasTextView.setTextColor(colorText);

		this.topListItemComprasLinearLayout.setBackgroundColor(itemView.getContext().getResources().getColor(estilo.getCorClara()));

		aplicaBackgroundPorSituacao(aposta, estilo);

		this.seusNumerosItemListaComprasTextView.setTextColor(colorText);
		if(qtdTeimosinhas > 0){
			this.tvTeimosinhas.setText(teimosinhasTxt);
			this.tvTeimosinhas.setTextColor(colorText);
		}

		View child = null;
		switch (aposta.getModalidade()) {
			case LOTECA:
				child = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_compras_loteca_layout, null);
				this.seusNumerosItemListaComprasTextView.setText(R.string.label_sua_aposta);
				ExpandableHeightRecyclerView lotecaRecyclerViewItemCompra = child.findViewById(R.id.lotecaRecyclerViewItemCompra);
				LotecaComprasAdapter lotecaAdapter = new LotecaComprasAdapter(aposta.getPartidasLoteca());
				lotecaRecyclerViewItemCompra.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
				lotecaRecyclerViewItemCompra.setAdapter(lotecaAdapter);
				lotecaRecyclerViewItemCompra.setExpanded(true);
				break;
			case LOTOGOL:
				child = LayoutInflater.from(itemView.getContext()).inflate(R.layout.item_compras_lotogol_layout, null);
				this.seusNumerosItemListaComprasTextView.setText(R.string.label_sua_aposta);
				ExpandableHeightRecyclerView lotogolRecyclerViewItemCompra = child.findViewById(R.id.lotecaRecyclerViewItemCompra);
				DetalhesApostaLotogolDetalhesComprasAdapter lotogolAdapter = new DetalhesApostaLotogolDetalhesComprasAdapter(aposta.getPartidasLotogol());
				lotogolRecyclerViewItemCompra.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
				lotogolRecyclerViewItemCompra.setAdapter(lotogolAdapter);
				lotogolRecyclerViewItemCompra.setExpanded(true);
				break;
			default:
				this.seusNumerosItemListaComprasTextView.setText(R.string.seus_numeros);
				child = ComprasDezenasView_.build(this.activity);
				((ComprasDezenasView)child).setLayout(aposta);
				break;
		}

		this.itemComprasContentModalideLinearLayout.addView(child);
	}

	private void aplicaBackgroundPorSituacao(ApostaDTO aposta, EstiloModalidade estilo) {
//		if (SituacaoCompraUtil.isEfetivada(aposta.getSituacao())){
//			this.bottomListItemComprasLinearLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.azul_efetivada));
//		} else if (SituacaoCompraUtil.isCancelada(aposta.getSituacao())){
//			this.bottomListItemComprasLinearLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.vermelho_cancelada));
//		} else if (SituacaoCompraUtil.isPremioPago(aposta.getSituacao())){
//			this.bottomListItemComprasLinearLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.verde_premio_pago));
//		} else {
			this.bottomListItemComprasLinearLayout.setBackgroundColor(itemView.getContext().getResources().getColor(estilo.getCorEscura()));
//		}
	}
}
