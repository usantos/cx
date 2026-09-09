package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;

public class ListStringAdapter extends RecyclerView.Adapter<ListStringAdapter.ViewHolder> {

    public interface OnItemClickListener { void onItemClick(int position); }

    private static final Object PAYLOAD_SELECTION = new Object();

    private final List<String> itens;
    private final OnItemClickListener clickListener;

    private int selectedPosition = RecyclerView.NO_POSITION;

    public ListStringAdapter(@NonNull List<String> itens,
                             @NonNull OnItemClickListener clickListener) {
        this.itens = itens;
        this.clickListener = clickListener;
        setHasStableIds(true);
    }


    @Override public long getItemId(int position) {
        // evita crash se algum dia vier posição fora (defensivo)
        if (position < 0 || position >= itens.size()) return RecyclerView.NO_ID;
        String v = itens.get(position);
        return v != null ? v.hashCode() : position;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_text_popup_itens_novo, parent, false);
        return new ViewHolder(view, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindText(itens.get(position));
        holder.bindSelected(position == selectedPosition);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.contains(PAYLOAD_SELECTION)) {
            holder.bindSelected(position == selectedPosition);
            return;
        }
        onBindViewHolder(holder, position);
    }

    @Override public int getItemCount() { return itens.size(); }

    public int getSelectedPosition() { return selectedPosition; }

    public void setSelectedPosition(int position) {
        if (getItemCount() <= 0) return;

        // clamp evita NPE/IOB
        int newPos = Math.min(Math.max(position, 0), getItemCount() - 1);
        if (newPos == selectedPosition) return;

        int old = selectedPosition;
        selectedPosition = newPos;

        if (old != RecyclerView.NO_POSITION) notifyItemChanged(old, PAYLOAD_SELECTION);
        notifyItemChanged(selectedPosition, PAYLOAD_SELECTION);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView text;
        private final OnItemClickListener clickListener;

        ViewHolder(@NonNull View itemView, @NonNull OnItemClickListener clickListener) {
            super(itemView);
            this.text = itemView.findViewById(R.id.text_list_itens);
            this.clickListener = clickListener;

            itemView.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;
                this.clickListener.onItemClick(pos);
            });
        }

        void bindText(String value) {
            if (text != null) text.setText(value == null ? "" : value);
        }

        void bindSelected(boolean selected) {
            itemView.setActivated(selected);
            itemView.setSelected(selected);
        }
    }
}