package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.DetalhesComprasActivity;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ApostaDTO;
import br.gov.caixa.loterias.apostas.view.holder.CompraHolder;

/**
 * Created by cedesbr450 on 30/01/18.
 */


public class ListaComprasDetalhesAdapter extends RecyclerView.Adapter<CompraHolder> {

    private final Context mContext;
    private final DetalhesComprasActivity activity;
    private List<ApostaDTO> listaAposta;

    public ListaComprasDetalhesAdapter(DetalhesComprasActivity activity, Context context, List<ApostaDTO> listaAposta) {
        this.mContext = context;
        this.activity = activity;
        this.listaAposta = listaAposta;
    }

    @Override
    public CompraHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutInflater = LayoutInflater.from(mContext).inflate(R.layout.list_item_compras, null);
        layoutInflater.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new CompraHolder(layoutInflater, activity);
    }

    @Override
    public void onBindViewHolder(CompraHolder holder, int position) {
        ApostaDTO        apostaDTO = listaAposta.get(position);
        holder.bind(apostaDTO, position);
    }

    @Override
    public int getItemCount() {
        return listaAposta.size();
    }
}