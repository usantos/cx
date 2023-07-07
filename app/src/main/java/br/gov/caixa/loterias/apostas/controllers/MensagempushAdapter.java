package br.gov.caixa.loterias.apostas.controllers;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;

public class MensagempushAdapter extends RecyclerView.Adapter<MensagempushAdapter.MessageViewHolder> {
    private List<MessagePush> mensagensList;
    private SparseBooleanArray expandedPositions;

    public MensagempushAdapter(List<MessagePush> mensagensList) {
        this.mensagensList = mensagensList;
        this.expandedPositions = new SparseBooleanArray();
    }

    public void setMensagensList(List<MessagePush> mensagensList) {
        this.mensagensList = mensagensList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_push, parent, false);
        return new MessageViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessagePush message = mensagensList.get(position);
        holder.bind(message, position);

        boolean isExpanded = expandedPositions.get(position, false);
        holder.setExpanded(isExpanded);
    }

    @Override
    public int getItemCount() {
        return mensagensList != null ? mensagensList.size() : 0;
    }

    public void mensagensFiltradas(String searchEditText) {
        List<MessagePush> mensagensFiltradas = new ArrayList<>();

        if (searchEditText.isEmpty()) {
            mensagensFiltradas.addAll(mensagensList);
        } else {
            for (MessagePush message : mensagensList) {
                if (message.getTitulo().toLowerCase().contains(searchEditText.toLowerCase()) ||
                        message.getConteudo().toLowerCase().contains(searchEditText.toLowerCase())) {
                    mensagensFiltradas.add(message);
                }
            }
            setMensagensList(mensagensFiltradas);

        }

    }

    public List<MessagePush> getUnreadMessages() {
        List<MessagePush> mensagensNaolidas = new ArrayList<>();
        for (MessagePush message : mensagensList ) {
            if (!message.isRead()) {
                mensagensNaolidas.add(message);
            }
        }
        return mensagensNaolidas;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iconImageView;
        private TextView titleTextView;
        private TextView contentTextView;
        private TextView text_preview;
        private TextView dateTextView;
        private ConstraintLayout expandedLayout;
        private Button buttonCompart;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.icon_ImageView);
            buttonCompart = itemView.findViewById(R.id.buttonCompart);
            titleTextView = itemView.findViewById(R.id.titulo_msgpush);
            contentTextView = itemView.findViewById(R.id.mensagem_texto);
            dateTextView = itemView.findViewById(R.id.data_texto);
            expandedLayout = itemView.findViewById(R.id.expandedLayout);
            text_preview = itemView.findViewById(R.id.text_preview);

            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull MessagePush mensagensList, int position) {
            iconImageView.setImageResource(mensagensList.getIcon());
            titleTextView.setText(mensagensList.getTitulo());
            TextView contentTextView = expandedLayout.findViewById(R.id.mensagem_texto);
            text_preview.setText(mensagensList.getConteudo());
            contentTextView.setText(mensagensList.getConteudo());
            dateTextView.setText(mensagensList.getData());
//            buttonCompart.setImageResource(mensagensList.getButton);

            boolean isExpanded = expandedPositions.get(position, false);

            expandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        }

        public void setExpanded(boolean expanded) {
            text_preview.setVisibility(!expanded ? View.VISIBLE : View.INVISIBLE);
            expandedLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            if(position != RecyclerView.NO_POSITION) {
                boolean isExpanded = expandedPositions.get(position, false);
                expandedPositions.put(position, !isExpanded);
                setExpanded(!isExpanded);
            }else{
                expandedPositions.delete(position);
            }

            if (v.getId() == R.id.buttonCompart) {
                MessagePush messagePush = mensagensList.get(position);
                shareMessage(v.getContext(), messagePush);
            }
            notifyDataSetChanged();
        }

        private void shareMessage(Context context, MessagePush messagePush) {
            String textToShare = messagePush.getTitulo() + messagePush.getConteudo();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            context.startActivity(Intent.createChooser(shareIntent, "Compartilhar mensagem"));

        }
    }

    public static class OnItemClickListener {
    }
}
