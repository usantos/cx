package br.gov.caixa.loterias.apostas.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;

public class MensagempushAdapter extends RecyclerView.Adapter<MensagempushAdapter.MessageViewHolder> {
    private final List<MessagePush> mensagensList;

    public MensagempushAdapter(List<MessagePush> mensagensList) {
        this.mensagensList = mensagensList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_push, parent, false);
        return new MessageViewHolder(view);
    }

    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessagePush message = mensagensList.get(position);
        holder.titleTextView.setText(message.getTitulo());
        holder.contentTextView.setText(message.getConteudo());

        if (message.isRead()) {
            holder.titleTextView.setTextColor(holder.itemView.getResources().getColor(R.color.black));
        } else {
            holder.titleTextView.setTextColor(holder.itemView.getResources().getColor(R.color.mpsdk_dark_font_color));
        }
    }

    @Override
    public int getItemCount() {
        return mensagensList.size();
    }


    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView contentTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titulo_msgpush);
            contentTextView = itemView.findViewById(R.id.mensagem_texto);
        }
    }
}
