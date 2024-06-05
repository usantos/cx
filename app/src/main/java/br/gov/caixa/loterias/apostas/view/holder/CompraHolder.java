package br.gov.caixa.loterias.apostas.view.holder;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.activity.DetalhesBolaoActivity;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.DetalhesApostaLotogolDetalhesComprasAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.LotecaComprasAdapter;
import br.gov.caixa.loterias.apostas.view.adapter.recyclerview.PartidasLotecaRecyclerViewAdapter;
import br.gov.caixa.loterias.apostas.view.custom.ComprasDezenasView;
import br.gov.caixa.loterias.apostas.view.custom.ComprasDezenasView_;
import br.gov.caixa.loterias.apostas.view.custom.ExpandableHeightRecyclerView;

public class CompraHolder extends LoteriasHolder<ApostaDTO> {

	private TextView tipoPremioListaComprasTextView;
	private TextView dataConcursoListaComprasTextView;
	private TextView concursoListaComprasTextView;
	private TextView cotaListaComprasTextView;
	private TextView statusResultadoApostaListaComprasTextView;
	private LinearLayout topListItemComprasLinearLayout;
	private LinearLayout bottomListItemComprasLinearLayout;
	private TextView seusNumerosItemListaComprasTextView;
	private LinearLayout itemComprasContentModalideLinearLayout;
	private TextView tvTeimosinhas;
	private DetalhesComprasActivity activity;
	private ImageView tagBolao;

	public CompraHolder(View itemView, DetalhesComprasActivity activity) {
		super(itemView);
		tipoPremioListaComprasTextView = itemView.findViewById(R.id.TipoPremioListaComprasTextView);
		dataConcursoListaComprasTextView = itemView.findViewById(R.id.dataConcursoListaComprasTextView);
		concursoListaComprasTextView = itemView.findViewById(R.id.concursoListaComprasTextView);
		cotaListaComprasTextView = itemView.findViewById(R.id.cotaListaComprasTextView);
		statusResultadoApostaListaComprasTextView = itemView.findViewById(R.id.statusResultadoApostaListaComprasTextView);
		topListItemComprasLinearLayout = itemView.findViewById(R.id.topListItemComprasLinearLayout);
		bottomListItemComprasLinearLayout = itemView.findViewById(R.id.bottomListItemComprasLinearLayout);
		seusNumerosItemListaComprasTextView = itemView.findViewById(R.id.seusNumerosItemListaComprasTextView);
		itemComprasContentModalideLinearLayout = itemView.findViewById(R.id.itemComprasContentModalideLinearLayout);
		tvTeimosinhas = itemView.findViewById(R.id.tv_teimosinhas);
		tagBolao = itemView.findViewById(R.id.image_tag_bolao);
		this.activity = activity;
	}

	@Override
	public void bind(ApostaDTO aposta, int position) {
		EstiloModalidadeMKP estiloMKP = new EstiloModalidadeMKP(aposta.getModalidade());
		int colorText = itemView.getContext().getResources().getColor(estiloMKP.getCorFonteFundoClaro());

		int qtdTeimosinhas = aposta.getQuantidadeTeimosinhas();
		String teimosinhasTxt = qtdTeimosinhas + (qtdTeimosinhas > 1 ? itemView.getContext().getResources().getString(R.string.espaco_teimosinhas) : itemView.getContext().getResources().getString(R.string.espaco_teimosinha));

		this.tipoPremioListaComprasTextView.setTextColor(colorText);
		String nomeAposta =  ViewUtils.getNomeModalidadePorAposta(aposta).toLowerCase();
		this.tipoPremioListaComprasTextView.setText(ViewUtils.textFuturaAndFuturaBold(itemView.getContext(), itemView.getContext().getString(R.string.texto_futura_bold, nomeAposta)));

		if (aposta.getIndicadorCotaBolao()) {
			tagBolao.setVisibility(View.VISIBLE);
			tagBolao.setImageResource(estiloMKP.getImagemTrianguloOutline());

			this.dataConcursoListaComprasTextView.setText(aposta.getReservaCotaBolao().getDataRegistroBolao());
			this.concursoListaComprasTextView.setText(ViewUtils.textFuturaAndFuturaBold(itemView.getContext(),
							itemView.getContext().getString(R.string.label_aposta_concurso_bold,
							aposta.getConcursoInicial())));
			this.cotaListaComprasTextView.setVisibility(View.VISIBLE);
			this.cotaListaComprasTextView.setTextColor(colorText);
			this.cotaListaComprasTextView.setText(itemView.getContext().getString(R.string.label_cota_concurso_bold) + String.format("%02d/%02d",
									aposta.getReservaCotaBolao().getNumeroCotaReservada(),
									aposta.getReservaCotaBolao().getQtdCotaTotalBolao()));
		} else {
			this.dataConcursoListaComprasTextView.setText(aposta.getDataEfetivacao());
			this.concursoListaComprasTextView.setText(ViewUtils.textFuturaAndFuturaBold(itemView.getContext(),
							itemView.getContext().getString(R.string.label_aposta_concurso_bold,
									aposta.getConcursoInicial())));
		}
		this.dataConcursoListaComprasTextView.setTextColor(colorText);
		this.concursoListaComprasTextView.setTextColor(colorText);

		this.statusResultadoApostaListaComprasTextView.setText(aposta.getSituacao().getDescricao());
		this.statusResultadoApostaListaComprasTextView.setTextColor(itemView.getContext().getResources().getColor(estiloMKP.getCorFonteFundoClaro()));

		this.topListItemComprasLinearLayout.setBackgroundColor(itemView.getContext().getResources().getColor(estiloMKP.getCorClara()));

		this.bottomListItemComprasLinearLayout.setBackgroundColor(itemView.getContext().getResources().getColor(estiloMKP.getCorEscura()));

		this.seusNumerosItemListaComprasTextView.setTextColor(colorText);
		if(qtdTeimosinhas > 0){
			this.tvTeimosinhas.setText(teimosinhasTxt);
			this.tvTeimosinhas.setTextColor(colorText);
		}

		View child = null;
		switch (aposta.getModalidade()) {
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
				if (aposta.getModalidade() == ModalidadeEnum.LOTECA) {
					this.seusNumerosItemListaComprasTextView.setText(R.string.label_sua_aposta);
				} else {
					this.seusNumerosItemListaComprasTextView.setText(R.string.seus_numeros);
				}
				child = ComprasDezenasView_.build(this.activity);
				((ComprasDezenasView)child).setLayout(aposta, estiloMKP.getCorFonteFundoClaro());
				break;
		}

		this.itemComprasContentModalideLinearLayout.addView(child);
	}

}
