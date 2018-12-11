package br.gov.caixa.loterias.apostar.view.adapter;

/**
 * Created by cedesbr450 on 08/02/18.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostar.R;
import br.gov.caixa.loterias.apostar.model.bo.silce.dto.CompraDTO;
import br.gov.caixa.loterias.apostar.view.DetalhesComprasActivity;


public final class ListaComprasRecyclerViewAdapter extends RecyclerView.Adapter<ListaComprasRecyclerViewAdapter.ViewHolder> {


    private final Context context;
    private final List<CompraDTO> listaCompras;

    public ListaComprasRecyclerViewAdapter(final Context context, final List<CompraDTO> listaCompras) {
        this.context = context;
        this.listaCompras = listaCompras;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.list_group_compras, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.dataCompra.setText(listaCompras.get(position).getDataFinalizacaoCompra());
        holder.valorCompra.setText(br.gov.caixa.loterias.apostar.utils.ViewUtils.getMoedaFormat(listaCompras.get(position).getValorTotal()));
        final int ordemLista = position + 1;
        holder.itemCompraOrdemTextView.setText("Compra " + ordemLista);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent activity = new Intent(context, DetalhesComprasActivity.class);
                String arrayAsString = new Gson().toJson(listaCompras.get(position));
                activity.putExtra("CompraDTO", arrayAsString);
                activity.putExtra("ordem", ordemLista);
                context.startActivity(activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCompras.size();
    }

    public List<CompraDTO> getCompras() {
        return Collections.unmodifiableList(listaCompras);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView dataCompra;
        protected TextView valorCompra;
        protected TextView itemCompraOrdemTextView;

        public ViewHolder(final View itemView) {
            super(itemView);
            dataCompra = (TextView) itemView.findViewById(R.id.dataCompra);
            valorCompra = (TextView) itemView.findViewById(R.id.valorCompra);
            itemCompraOrdemTextView = (TextView) itemView.findViewById(R.id.itemCompraOrdemTextView);
        }
    }

}