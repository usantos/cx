package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.OpcaoResgatePremio;
import br.gov.caixa.loterias.apostas.view.holder.OpcaoResgatePremioHolder;


public class OpcaoResgatePremioAdapter extends RecyclerView.Adapter<OpcaoResgatePremioHolder> {
    private List<OpcaoResgatePremio> list;

    public OpcaoResgatePremioAdapter(List<OpcaoResgatePremio> list) {
        this.list = list;
    }

    @Override
    public OpcaoResgatePremioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_opcao_resgate_premio,
                    parent,
                    false);

        return new OpcaoResgatePremioHolder(view);
    }

    @Override
    public void onBindViewHolder(OpcaoResgatePremioHolder holder, int position) {
        holder.bind(list.get(position), position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
