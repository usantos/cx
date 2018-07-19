package caixa.gov.br.loterias.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import caixa.gov.br.loterias.mock.ItemListaFaixaPremiacaoMock;
import caixa.gov.br.loterias.model.bo.silce.dto.ConcursoPremiadoDTO;
import caixa.gov.br.loterias.model.bo.silce.dto.FaixaPremiadaDTO;
import caixa.gov.br.loterias.view.custom.ItemListaFaixaPremiacao;
import caixa.gov.br.loterias.view.custom.ItemListaFaixaPremiacao_;

/**
 * Created by joafilho on 16/03/2018.
 * Class FaixaPremiacaoAdapter
 */

public class FaixaPremiacaoAdapter extends RecyclerView.Adapter<FaixaPremiacaoAdapter.FaixaPremiacaoHolder> {

    private List<FaixaPremiadaDTO> itemListaFaixaPremiacaoList;

    public FaixaPremiacaoAdapter(List<FaixaPremiadaDTO> itemListaFaixaPremiacaoList) {
        this.itemListaFaixaPremiacaoList = itemListaFaixaPremiacaoList;
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
        holder.itemListaFaixaPremiacao.setLayout(itemListaFaixaPremiacaoList.get(position));
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
