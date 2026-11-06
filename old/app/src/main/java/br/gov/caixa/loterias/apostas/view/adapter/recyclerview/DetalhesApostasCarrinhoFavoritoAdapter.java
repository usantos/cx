package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostas.view.holder.DetalhesCarrinhoFavoritoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnDetalheCarrinhoFavoritoClickListener;

public class DetalhesApostasCarrinhoFavoritoAdapter extends RecyclerView.Adapter<DetalhesCarrinhoFavoritoHolder> {
	//region Variables
	private List<ApostaCarrinhoFavoritoDTO> listApostas;
	private OnDetalheCarrinhoFavoritoClickListener listener;
	private FragmentManager fm;
	//endregion

	//region Contructor
	public DetalhesApostasCarrinhoFavoritoAdapter(List<ApostaCarrinhoFavoritoDTO> listApostas,
												  OnDetalheCarrinhoFavoritoClickListener listener,
												  FragmentManager fm) {
		this.listApostas = listApostas;
		this.listener = listener;
		this.fm = fm;
	}
	//endregion

	//region Overrides
	@NonNull
	@Override
	public DetalhesCarrinhoFavoritoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View                      view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_detalhes_apostas_carrinho_fav, null);
		RecyclerView.LayoutParams lp   = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		view.setLayoutParams(lp);

		return new DetalhesCarrinhoFavoritoHolder(view, listener, fm);
	}

	@Override
	public void onBindViewHolder(@NonNull DetalhesCarrinhoFavoritoHolder holder, int position) {
		ApostaCarrinhoFavoritoDTO apostaAtual = listApostas.get(position);
		holder.bind(apostaAtual, position);
	}

	@Override
	public int getItemCount() {
		return listApostas.size();
	}
	//endregion
}
