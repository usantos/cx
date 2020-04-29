package br.gov.caixa.loterias.apostas.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DuvidaDTO;
import br.gov.caixa.loterias.apostas.view.custom.ItemDuvidaDetalheView;
import br.gov.caixa.loterias.apostas.view.custom.ItemDuvidaDetalheView_;


/**
 * Created by joafilho on 16/02/2018.
 * Class DetalheDuvidaAdapter
 */

public class DetalheDuvidaAdapter extends RecyclerView.Adapter<DetalheDuvidaAdapter.DetalheDuvidaHolder> {

    private List<DuvidaDTO> duvidasDetalhe;

    public DetalheDuvidaAdapter(List<DuvidaDTO> duvidasDetalhe) {
        this.duvidasDetalhe = duvidasDetalhe;
    }

    @Override
    public DetalheDuvidaHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ItemDuvidaDetalheView view = ItemDuvidaDetalheView_.build(parent.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new DetalheDuvidaHolder(view);
    }

    @Override
    public void onBindViewHolder(DetalheDuvidaHolder holder, int position) {
        holder.itemDuvidaDetalheView.setLayout(duvidasDetalhe.get(position));
        if (position != 0) {
            holder.itemDuvidaDetalheView.getLayoutItemDetalheDuvida().setBackgroundResource(R.drawable.drawer_linha_divisao_cinza_2_top);
        }

    }

    @Override
    public int getItemCount() {
        return duvidasDetalhe.size();
    }

    class DetalheDuvidaHolder extends HomeAdapter.ViewHolder {

        private ItemDuvidaDetalheView itemDuvidaDetalheView;

        DetalheDuvidaHolder(View itemView) {
            super(itemView);
            this.itemDuvidaDetalheView = (ItemDuvidaDetalheView) itemView;
        }
    }
}