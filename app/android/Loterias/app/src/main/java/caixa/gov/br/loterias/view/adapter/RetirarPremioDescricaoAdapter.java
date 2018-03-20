package caixa.gov.br.loterias.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import caixa.gov.br.loterias.view.custom.ItemRetirarPremioDescricao;
import caixa.gov.br.loterias.view.custom.ItemRetirarPremioDescricao_;

/**
 * Created by joafilho on 20/03/2018.
 * Class RetirarPremioDescricaoAdapter
 */

public class RetirarPremioDescricaoAdapter extends RecyclerView.Adapter<RetirarPremioDescricaoAdapter.RetirarPremioDescricaoHolder> {

    private List<String> listItens;

    public RetirarPremioDescricaoAdapter(List<String> listItens) {
        this.listItens = listItens;
    }

    @Override
    public RetirarPremioDescricaoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemRetirarPremioDescricao view = ItemRetirarPremioDescricao_.build(parent.getContext());

        return new RetirarPremioDescricaoHolder(view);
    }

    @Override
    public void onBindViewHolder(RetirarPremioDescricaoHolder holder, int position) {
        holder.itemRetirarPremioDescricao.setLayout();
    }

    @Override
    public int getItemCount() {
        return listItens.size();
    }

    class RetirarPremioDescricaoHolder extends RecyclerView.ViewHolder {
        private ItemRetirarPremioDescricao itemRetirarPremioDescricao;

        public RetirarPremioDescricaoHolder(View itemView) {
            super(itemView);

            itemRetirarPremioDescricao = (ItemRetirarPremioDescricao) itemView;
        }
    }
}
