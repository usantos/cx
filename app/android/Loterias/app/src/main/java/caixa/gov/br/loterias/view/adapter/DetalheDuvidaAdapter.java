package caixa.gov.br.loterias.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import caixa.gov.br.loterias.R;
import caixa.gov.br.loterias.bo.silce.dto.DTOEnumCharacter;
import caixa.gov.br.loterias.view.custom.ItemDividaDetalheView;
import caixa.gov.br.loterias.view.custom.ItemDividaDetalheView_;


/**
 * Created by joafilho on 16/02/2018.
 * Class DetalheDuvidaAdapter
 */

public class DetalheDuvidaAdapter extends RecyclerView.Adapter<DetalheDuvidaAdapter.DetalheDuvidaHolder> {

    private List<DTOEnumCharacter> listDetalhes;

    public DetalheDuvidaAdapter(List<DTOEnumCharacter> listDetalhes) {
        this.listDetalhes = listDetalhes;
    }

    @Override
    public DetalheDuvidaHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemDividaDetalheView view = ItemDividaDetalheView_.build(parent.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new DetalheDuvidaHolder(view);
    }

    @Override
    public void onBindViewHolder(DetalheDuvidaHolder holder, int position) {
        holder.itemDividaDetalheView.setLayout(listDetalhes.get(position));
        if (position != 0) {
            holder.itemDividaDetalheView.getLayoutItemDetalheDuvida().setBackgroundResource(R.drawable.drawer_linha_divisao_cinza_2_top);
        }

    }

    @Override
    public int getItemCount() {
        return listDetalhes.size();
    }

    class DetalheDuvidaHolder extends HomeAdapter.ViewHolder {

        private ItemDividaDetalheView itemDividaDetalheView;

        public DetalheDuvidaHolder(View itemView) {
            super(itemView);
            this.itemDividaDetalheView = (ItemDividaDetalheView) itemView;
        }
    }
}


