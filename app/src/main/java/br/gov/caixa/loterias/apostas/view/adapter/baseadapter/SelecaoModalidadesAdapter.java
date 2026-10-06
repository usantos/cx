package br.gov.caixa.loterias.apostas.view.adapter.baseadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeDTO;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

public class SelecaoModalidadesAdapter extends BaseAdapter {

	private final Context mContext;
	private final List<TipoAposta> tiposAposta;
	private Boolean isUnicaSelecao;

	public SelecaoModalidadesAdapter(Context context, List<TipoAposta> tiposAposta, Boolean isUnicaSelecao) {
		this.mContext = context;
		this.tiposAposta = tiposAposta;
		this.isUnicaSelecao = isUnicaSelecao;
	}

	@Override
	public int getCount() {
		return tiposAposta.size();
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
			convertView = layoutInflater.inflate(R.layout.linearlayout_tipos_apostas, null);
		}

		final TipoAposta   tipoAposta             = tiposAposta.get(position);
		final ImageView    imageViewTipoAPosta    = convertView.findViewById(R.id.imageviewTrevoTipoAposta);
		final LinearLayout linearLayoutTipoAposta = convertView.findViewById(R.id.LinearLayoutContentTiposAposta);
		final TextView     tituloTipoAposta       = convertView.findViewById(R.id.tituloTipoApostaTextView);

		tituloTipoAposta.setText(tipoAposta.getTitulo());

		if (tipoAposta.getSelect()) {
			imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevoFundoCor());
			linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, tipoAposta.getCorBackground()));
			if (tipoAposta.getTextSelectColor() != null && tipoAposta.getTextSelectColor() > 0){
				tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, tipoAposta.getTextSelectColor()));
			} else {
				tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.branco));
			}
		} else {
			imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevo());
			linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, R.color.branco));
			linearLayoutTipoAposta.setBackground(ContextCompat.getDrawable(mContext, R.drawable.custom_border_cinza));
			if (tipoAposta.getTextColor() != null && tipoAposta.getTextColor() > 0){
				tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, tipoAposta.getTextColor()));
			} else {
				tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.cinzanaoselecionado));
			}
		}

		convertView.setOnClickListener(v -> {

			if (tiposAposta.get(position).getSelect()) {
				tiposAposta.get(position).setSelect(false);
				imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevo());
				linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, R.color.branco));
				linearLayoutTipoAposta.setBackground(ContextCompat.getDrawable(mContext, R.drawable.custom_border_cinza));
				tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.cinzanaoselecionado));
			} else {
				if (isUnicaSelecao) {
					limpaSelecaoModalidades();
				}
				tiposAposta.get(position).setSelect(true);
				imageViewTipoAPosta.setImageResource(tipoAposta.getImagemTrevoFundoCor());
				linearLayoutTipoAposta.setBackgroundColor(ContextCompat.getColor(mContext, tipoAposta.getCorBackground()));
				tituloTipoAposta.setTextColor(ContextCompat.getColor(mContext, R.color.branco));

			}
		});

		return convertView;
	}

	public List<ModalidadeEnum> getModalidadesEnumSelecionadas() {
		List<ModalidadeEnum> modalidadesEnum = new ArrayList<>();
		List<ModalidadeDTO>  modalidades     = getModalidadesSelecionadas();
		for (ModalidadeDTO modalidade : modalidades) {
			modalidadesEnum.add(ModalidadeEnum.fromModalidadeDTO(modalidade));
		}
		return modalidadesEnum;
	}

	public List<ModalidadeDTO> getModalidadesSelecionadas() {
		List<ModalidadeDTO> modalidades = new ArrayList<>();
		for (TipoAposta tipoAposta : tiposAposta) {
			if (tipoAposta.getSelect()) {
				ModalidadeDTO modalidade = new ModalidadeDTO();
				modalidade.setDescricao(tipoAposta.getTitulo());
				modalidade.setDescricaoEspecial(tipoAposta.getDescricaoEspecial());
				modalidade.setValor(tipoAposta.getValor());
				modalidades.add(modalidade);
			}
		}
		return modalidades;
	}

	public void limpaSelecaoModalidades() {
		if (tiposAposta != null) {
			for (TipoAposta aposta : tiposAposta) {
				aposta.setSelect(false);
			}
		}
		notifyDataSetChanged();
	}
}