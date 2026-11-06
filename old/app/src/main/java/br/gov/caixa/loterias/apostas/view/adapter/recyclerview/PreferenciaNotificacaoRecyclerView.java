package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.PreferenciaNotificacao;
import br.gov.caixa.loterias.apostas.view.holder.PreferenciaNotificacaoHolder;

public class PreferenciaNotificacaoRecyclerView extends RecyclerView.Adapter<PreferenciaNotificacaoHolder>{

	private ArrayList<PreferenciaNotificacao> preferencias;

	public PreferenciaNotificacaoRecyclerView(ArrayList<PreferenciaNotificacao> preferencias) {
		this.preferencias = preferencias;
	}

	@NonNull
	@Override
	public PreferenciaNotificacaoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preferencia_notificacao,
																		parent, false);
		return new PreferenciaNotificacaoHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull PreferenciaNotificacaoHolder holder, int position) {
		PreferenciaNotificacao preferencia = preferencias.get(position);
		holder.bind(preferencia, position);
	}

	@Override
	public int getItemCount() {
		return preferencias.size();
	}
}
