package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ParametroEquipe;
import br.gov.caixa.loterias.apostas.view.holder.TimeCoracaoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class TimeCoracaoAdapter extends RecyclerView.Adapter<TimeCoracaoHolder> {
    private Context context;
    private List<ParametroEquipe> times;
    private OnItemClickListener listener;

    public TimeCoracaoAdapter(Context context, List<ParametroEquipe> times, OnItemClickListener listener) {
        this.context = context;
        this.times = times;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeCoracaoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_coracao, parent, false);
        return new TimeCoracaoHolder(view, context, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeCoracaoHolder holder, int position) {
        holder.bind(times.get(position), position);
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

}