package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;

public class TeimosinhasAdapter extends RecyclerView.Adapter<TeimosinhasAdapter.ViewHolder> {

    private final List<String> itens;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private final Consumer<Integer> onSelect;

    public TeimosinhasAdapter(List<String> itens, Consumer<Integer> onSelect) {
        this.itens = itens;
        this.onSelect = onSelect;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_text_popup_itens_novo, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {
        holder.text.setText(itens.get(position));
        holder.itemView.setSelected(position == selectedPosition);

        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();

            if (adapterPosition == RecyclerView.NO_POSITION) {
                return;
            }

            int oldPosition = selectedPosition;
            selectedPosition = adapterPosition;

            if (oldPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(oldPosition);
            }
            notifyItemChanged(selectedPosition);

            onSelect.accept(adapterPosition);
        });
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public void setSelectedPosition(int position) {
        int old = selectedPosition;
        selectedPosition = position;

        if (old != RecyclerView.NO_POSITION) {
            notifyItemChanged(old);
        }
        notifyItemChanged(selectedPosition);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text_list_itens);
        }
    }
}