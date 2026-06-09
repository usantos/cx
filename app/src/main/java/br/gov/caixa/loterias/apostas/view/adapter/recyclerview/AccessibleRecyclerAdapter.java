package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.holder.AccessibleRecyclerHolder;

public class AccessibleRecyclerAdapter extends RecyclerView.Adapter<AccessibleRecyclerHolder> {

    private final List<Pair<String, String>> items;
    private final int corFonteFundoEscuro;

    public AccessibleRecyclerAdapter(List<Pair<String, String>> items, int corFonteFundoEscuro){
        this.items = items;
        this.corFonteFundoEscuro = corFonteFundoEscuro;
    }

    @NonNull
    @Override
    public AccessibleRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_bolao, parent, false);
        return new AccessibleRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccessibleRecyclerHolder holder, int position) {
        holder.bind(items.get(position), position, corFonteFundoEscuro, getItemCount());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}