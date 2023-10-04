package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;

public class LotericaAdapter extends RecyclerView.Adapter<LotericaAdapter.LotericaViewHolder> {

    private List<String> fruits;

    public LotericaAdapter(List<String> fruits) {
        this.fruits = fruits;
    }

    @NonNull
    @Override
    public LotericaAdapter.LotericaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loterica, parent, false);
        return new LotericaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LotericaAdapter.LotericaViewHolder holder, int position) {
        holder.fruitNameTextView.setText(fruits.get(position));
    }

    @Override
    public int getItemCount() {
        return fruits.size();
    }

    static class LotericaViewHolder extends RecyclerView.ViewHolder {
        TextView fruitNameTextView;

        LotericaViewHolder(@NonNull View itemView) {
            super(itemView);
            fruitNameTextView = itemView.findViewById(R.id.textNomeLoterica);
        }
    }

    public void filterList(List<String> filteredList) {
        fruits = filteredList;
        notifyDataSetChanged();
    }
}
