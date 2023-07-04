package br.gov.caixa.loterias.apostas.controllers;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

    class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iconImageView;
        private TextView titleTextView;
        private TextView contentTextView;
        private TextView dateTextView;
        private LinearLayout expandedLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.icon_ImageView);
            titleTextView = itemView.findViewById(R.id.titulo_msgpush);
            contentTextView = itemView.findViewById(R.id.mensagem_texto);
            dateTextView = itemView.findViewById(R.id.data_texto);
            expandedLayout = itemView.findViewById(R.id.expandedLayout);

            expandedLayout.setOnClickListener(this);
        }

        public void bind(@NonNull MessagePush mensagensList, int position) {
            iconImageView.setImageResource(mensagensList.getIcon());
            titleTextView.setText(mensagensList.getTitulo());
            contentTextView.setText(mensagensList.getConteudo());
            dateTextView.setText(mensagensList.getData());

            boolean isExpanded = expandedPositions.get(position, false);
            expandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }

        public void setExpanded(boolean expanded) {
            expandedLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            if(position != RecyclerView.NO_POSITION) {
                boolean isExpanded = expandedPositions.get(position, false);
                expandedPositions.put(position, !isExpanded);
                setExpanded(!isExpanded);
            }
        }
    }
}
