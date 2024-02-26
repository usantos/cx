package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.FaixaPremiadaDTO;
import br.gov.caixa.loterias.apostas.view.custom.ItemListaFaixaPremiacao;
import br.gov.caixa.loterias.apostas.view.custom.ItemListaFaixaPremiacaoBolao;
import br.gov.caixa.loterias.apostas.view.custom.ItemListaFaixaPremiacaoBolao_;
import br.gov.caixa.loterias.apostas.view.custom.ItemListaFaixaPremiacao_;

public class FaixaPremiacaoBolaoAdapter extends RecyclerView.Adapter<FaixaPremiacaoBolaoAdapter.FaixaPremiacaoHolder> {
    private List<FaixaPremiadaDTO> itemListaFaixaPremiacaoList;
    private int color;
    private int darkColor;
    public FaixaPremiacaoBolaoAdapter(List<FaixaPremiadaDTO> itemListaFaixaPremiacaoList, int color, int darkColor) {
        this.itemListaFaixaPremiacaoList = itemListaFaixaPremiacaoList;
        this.color = color;
        this.darkColor = darkColor;
    }

    @Override
    public FaixaPremiacaoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemListaFaixaPremiacaoBolao view = ItemListaFaixaPremiacaoBolao_.build(parent.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new FaixaPremiacaoHolder(view);
    }

    @Override
    public void onBindViewHolder(FaixaPremiacaoHolder holder, int position) {
        if (position % 2 == 0){
            holder.itemListaFaixaPremiacao.setLayout(itemListaFaixaPremiacaoList.get(position), darkColor);
        } else {
            holder.itemListaFaixaPremiacao.setLayout(itemListaFaixaPremiacaoList.get(position), color);
        }
    }

    @Override
    public int getItemCount() {
        return itemListaFaixaPremiacaoList.size();
    }

    class FaixaPremiacaoHolder extends RecyclerView.ViewHolder {
        private ItemListaFaixaPremiacaoBolao itemListaFaixaPremiacao;

        public FaixaPremiacaoHolder(View itemView) {
            super(itemView);
            itemListaFaixaPremiacao = (ItemListaFaixaPremiacaoBolao) itemView;
        }
    }
}
