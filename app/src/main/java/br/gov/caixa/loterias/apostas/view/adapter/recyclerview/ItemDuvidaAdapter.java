package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListSecaoDTO;
import br.gov.caixa.loterias.apostas.view.custom.ItemDuvidaView;
import br.gov.caixa.loterias.apostas.view.custom.ItemDuvidaView_;

/**
 * Created by joafilho on 15/02/2018.
 * Class adapter ItemDuvidaAdapter
 */

public class ItemDuvidaAdapter extends RecyclerView.Adapter<ItemDuvidaAdapter.ItemDuvidaViewHolder> {

    private List<ListSecaoDTO> itensDuvida;

    public ItemDuvidaAdapter(List<ListSecaoDTO> itensDuvida) {
        this.itensDuvida = itensDuvida;
    }

    @Override
    public ItemDuvidaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemDuvidaView view = ItemDuvidaView_.build(parent.getContext());
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_duvida, null);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new ItemDuvidaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemDuvidaViewHolder holder, int position) {
        holder.itemDuvidaView.setLayout(itensDuvida.get(position));
        if (position != 0) {
            holder.itemDuvidaView.getItemDuvida().setBackgroundResource(R.drawable.drawer_linha_divisao_cinza_2_top);
        }
    }

    @Override
    public int getItemCount() {
        return itensDuvida.size();
    }

    class ItemDuvidaViewHolder extends RecyclerView.ViewHolder {

        private ItemDuvidaView itemDuvidaView;

        public ItemDuvidaViewHolder(View itemView) {
            super(itemView);
            this.itemDuvidaView = (ItemDuvidaView) itemView;
        }
    }
}
