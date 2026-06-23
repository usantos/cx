package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.OpcaoResgatePremio;
import br.gov.caixa.loterias.apostas.view.holder.LoteriasHolder;
import br.gov.caixa.loterias.apostas.view.holder.OpcaoResgateHeaderViewHolder;
import br.gov.caixa.loterias.apostas.view.holder.OpcaoResgatePremioHolder;


public class OpcaoResgatePremioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<OpcaoResgatePremio> list;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_BODY = 1;

    public OpcaoResgatePremioAdapter(List<OpcaoResgatePremio> list) {
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        OpcaoResgatePremio item = list.get(position);
        if (item.isHeader()) return TYPE_HEADER; else return TYPE_BODY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case TYPE_HEADER:
                return new OpcaoResgateHeaderViewHolder(inflater.inflate(R.layout.layout_item_header_opcao_resgate, parent, false));
            case TYPE_BODY:
            default:
                return new OpcaoResgatePremioHolder(inflater.inflate(R.layout.layout_bet_reward, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof OpcaoResgatePremioHolder) {
            ((OpcaoResgatePremioHolder) holder).bind(list.get(position), position);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
