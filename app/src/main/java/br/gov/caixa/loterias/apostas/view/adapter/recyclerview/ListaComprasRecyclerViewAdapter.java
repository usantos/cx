package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

/**
 * Created by cedesbr450 on 08/02/18.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CompraDTO;
import br.gov.caixa.loterias.apostas.view.holder.CompraViewHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnFavoritarClickListener;


public final class ListaComprasRecyclerViewAdapter extends RecyclerView.Adapter<CompraViewHolder> {

    private final Context context;
    private final List<CompraDTO> listaCompras;
    private String dataHoraServidor;
    private final OnFavoritarClickListener listener;

    public ListaComprasRecyclerViewAdapter(final Context context, final List<CompraDTO> listaCompras, String dataHoraServidor, OnFavoritarClickListener listener) {
        this.context = context;
        this.listaCompras = listaCompras;
        this.dataHoraServidor = dataHoraServidor;
        this.listener = listener;
    }

    @Override
    public CompraViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        final Context context = parent.getContext();
        View view;
        if (listaCompras.size() == 0) {
            view = LayoutInflater.from(context).inflate(R.layout.row_compras_empty,
                    parent,
                    false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_group_compras,
                    parent,
                    false);
        }

        final CompraViewHolder viewHolder = new CompraViewHolder(view, context, dataHoraServidor, listener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CompraViewHolder holder, int position) {
        if (listaCompras.size() != 0) {
            CompraDTO compra = listaCompras.get(position);
            holder.bind(compra, position);
        }
    }

    @Override
    public int getItemCount() {
        return listaCompras.size() == 0 ? 1 : listaCompras.size();
    }

    public List<CompraDTO> getCompras() {
        return Collections.unmodifiableList(listaCompras);
    }
}