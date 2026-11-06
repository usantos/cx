package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraDTO;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class SugestoesPremioMinimoAdapter extends RecyclerView.Adapter<SugestoesPremioMinimoAdapter.ViewHolder> {

	private final Context mContext;

	private OnModalidadeSelecionadaListener listener;
	private ArrayList<String> listaSugestoes;

	private OnItemClickListener itemClickListener;

	private int selectedPosition = -1;

	public SugestoesPremioMinimoAdapter(Context context, ArrayList<String> listaSugestoes, OnItemClickListener listener) {
		this.mContext = context;
		this.listaSugestoes = listaSugestoes;
		this.itemClickListener = listener;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_opcao_premio_minimo, null);
		return new ViewHolder(view, itemClickListener);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
		holder.btnSugestao.setText(listaSugestoes.get(position));
		holder.btnSugestao.setSelected(selectedPosition == position ? true:false);
	}

	@Override
	public int getItemCount() {
		return listaSugestoes.size();
	}

	public interface OnModalidadeSelecionadaListener {
		void onItemClick(TipoAposta item);
	}

	public class ViewHolder extends RecyclerView.ViewHolder{
		protected Button btnSugestao;

		public ViewHolder(final View itemView, OnItemClickListener itemClickListener) {
			super(itemView);
			btnSugestao = itemView.findViewById(R.id.btn_sugestao);
			ViewGroup.LayoutParams lp = btnSugestao.getLayoutParams();
			if (lp instanceof FlexboxLayoutManager.LayoutParams) {
				FlexboxLayoutManager.LayoutParams flexboxLp = (FlexboxLayoutManager.LayoutParams)lp;
				flexboxLp.setFlexGrow(1.0f);
			}

			itemView.setOnClickListener(v -> {
				itemClickListener.itemClick(this,getAbsoluteAdapterPosition());
			});
		}
	}

	public interface OnFavoritarClickListener {
		void onItemClick(CompraDTO compra);
	}

	public int getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(int selectedPosition) {
		this.selectedPosition = selectedPosition;
	}
}
