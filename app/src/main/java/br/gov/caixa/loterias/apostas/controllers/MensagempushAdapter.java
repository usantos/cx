package br.gov.caixa.loterias.apostas.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;

public class MensagempushAdapter extends RecyclerView.Adapter<MensagempushAdapter.MessageViewHolder> {
    private List<MessagePush> mensagensList;


    public MensagempushAdapter(List<MessagePush> mensagensList) {
        this.mensagensList = mensagensList;
    }

    public void setMensagensList(List<MessagePush> mensagensList) {
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
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return mensagensList != null ? mensagensList.size() : 0;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iconImageView;
        private final TextView titleTextView;
        private final TextView contentTextView;
        private final TextView dateTextView;


        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.icon_ImageView);
            titleTextView = itemView.findViewById(R.id.titulo_msgpush);
            contentTextView = itemView.findViewById(R.id.mensagem_texto);
            dateTextView = itemView.findViewById(R.id.data_texto);
        }

        public void bind(MessagePush mensagensList) {
            iconImageView.setImageResource(mensagensList.getIcon());
            titleTextView.setText(mensagensList.getTitulo());
            contentTextView.setText(mensagensList.getConteudo());
            dateTextView.setText(mensagensList.getData());
        }
    }
}
