package br.gov.caixa.loterias.apostas.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;

public class MensagempushAdapter extends RecyclerView.Adapter<MensagempushAdapter.MessageViewHolder> implements Filterable {

    private List<String> mensagens;
    private List<String> mensagensNaolidas;
    private List<String> mensagensNaoLidas;
    private List<String> filtroMensagens;

    public MensagempushAdapter(List<String> mensagens, List<String> mensagensNaolidas) {

        this.mensagens = mensagens;
        this.mensagensNaolidas = mensagensNaolidas;
        this.filtroMensagens = new ArrayList<>(mensagens);
    }

    @NonNull
    @Override

    public MensagempushAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_push, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String message = filtroMensagens.get(position);
        holder.messageText.setText(message);
    }

    @Override
    public int getItemCount() {
        return filtroMensagens.size();
    }

    public Filter GetFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase().trim();
                List<String> filteredList = new ArrayList<>();
                if (query.isEmpty()) {
                    filteredList.addAll(mensagens);
                } else {
                    for (String mensagen : mensagens) {
                        if (mensagen.toLowerCase().contains(query)) {
                            filteredList.addAll(mensagens);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filtroMensagens.clear();
                filtroMensagens.addAll((List<String>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        MessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_texto);
        }
    }


}
