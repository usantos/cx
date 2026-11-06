package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.util.List;

import br.gov.caixa.loterias.apostas.model.bo.silce.dto.FaixaPremiadaDTO;
import br.gov.caixa.loterias.apostas.view.custom.ItemListaFaixaPremiacaoBolao;

public class FaixaPremiacaoBolaoAdapter extends RecyclerView.Adapter<FaixaPremiacaoBolaoAdapter.FaixaPremiacaoHolder> {
    private List<FaixaPremiadaDTO> itemListaFaixaPremiacaoList;
    private int color;
    private int darkColor;
    private BigDecimal valorLiquidoPremio;

    public FaixaPremiacaoBolaoAdapter(List<FaixaPremiadaDTO> itemListaFaixaPremiacaoList, int color, int darkColor, BigDecimal valorLiquidoPremio) {
        this.itemListaFaixaPremiacaoList = itemListaFaixaPremiacaoList;
        this.color = color;
        this.darkColor = darkColor;
        this.valorLiquidoPremio = valorLiquidoPremio;
    }

    @Override
    public FaixaPremiacaoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemListaFaixaPremiacaoBolao view = ItemListaFaixaPremiacaoBolao.build(parent.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new FaixaPremiacaoHolder(view);
    }

    @Override
    public void onBindViewHolder(FaixaPremiacaoHolder holder, int position) {
        //if (position % 2 == 0){
        //    holder.itemListaFaixaPremiacao.setLayout(itemListaFaixaPremiacaoList.get(position), darkColor);
        //} else {
            holder.itemListaFaixaPremiacao.setLayout(itemListaFaixaPremiacaoList.get(position), color, darkColor, position, valorLiquidoPremio);
        //}
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
