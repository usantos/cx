package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.FaixaPremiadaDTO;
import br.gov.caixa.loterias.apostas.view.custom.ItemListaFaixaPremiacao;
import br.gov.caixa.loterias.apostas.view.custom.ItemListaFaixaPremiacao_;

/**
 * Created by joafilho on 16/03/2018.
 * Class FaixaPremiacaoAdapter
 */

public class FaixaPremiacaoAdapter extends RecyclerView.Adapter<FaixaPremiacaoAdapter.FaixaPremiacaoHolder> {

    private List<FaixaPremiadaDTO> itemListaFaixaPremiacaoList;
    private int color;

    public FaixaPremiacaoAdapter(List<FaixaPremiadaDTO> itemListaFaixaPremiacaoList, int color) {
        this.itemListaFaixaPremiacaoList = itemListaFaixaPremiacaoList;
        this.color = color;
    }

    @Override
    public FaixaPremiacaoHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemListaFaixaPremiacao view = ItemListaFaixaPremiacao_.build(parent.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new FaixaPremiacaoHolder(view);
    }

    @Override
    public void onBindViewHolder(FaixaPremiacaoHolder holder, int position) {
        if (position % 2 == 0){
            holder.itemListaFaixaPremiacao.setLayout(itemListaFaixaPremiacaoList.get(position), color);
        } else {
            holder.itemListaFaixaPremiacao.setLayout(itemListaFaixaPremiacaoList.get(position), -1);
        }
    }

    @Override
    public int getItemCount() {
        return itemListaFaixaPremiacaoList.size();
    }

    class FaixaPremiacaoHolder extends RecyclerView.ViewHolder {

        private ItemListaFaixaPremiacao itemListaFaixaPremiacao;

        public FaixaPremiacaoHolder(View itemView) {
            super(itemView);
            itemListaFaixaPremiacao = (ItemListaFaixaPremiacao) itemView;
        }
    }
}
