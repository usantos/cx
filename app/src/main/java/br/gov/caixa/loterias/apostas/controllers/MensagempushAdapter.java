package br.gov.caixa.loterias.apostas.controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
    private int selectedPosition = RecyclerView.NO_POSITION;
    private int positionLast;

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
        boolean isSelected = position == selectedPosition;
    }

    @Override
    public int getItemCount() {
        return mensagensList != null ? mensagensList.size() : 0;
    }

    public void setSelectedPosition(int position) {
        int previousSelectedPosition = selectedPosition;

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

    class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iconImageView;
        private TextView titleTextView;
        private TextView contentTextView;
        private TextView text_preview;
        private TextView dateTextView;
        private ConstraintLayout expandedLayout;
        private Button buttonCompart, buttonMsgExpandida, buttonMsgLida, buttonMsgExcluir;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.icon_ImageView);
            buttonCompart = itemView.findViewById(R.id.buttonCompart);
//            buttonMsgExcluir = itemView.findViewById(R.id.buttonMsgExcluir);
            buttonMsgExpandida = itemView.findViewById(R.id.buttonMsgExpandida);

            titleTextView = itemView.findViewById(R.id.titulo_msgpush);
            contentTextView = itemView.findViewById(R.id.mensagem_texto);
            dateTextView = itemView.findViewById(R.id.data_texto);
            expandedLayout = itemView.findViewById(R.id.expandedLayout);
            text_preview = itemView.findViewById(R.id.text_preview);
            buttonCompart.setOnClickListener(this);
            buttonMsgExpandida.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bind(@NonNull MessagePush mensagensList, int position) {
            iconImageView.setImageResource(mensagensList.getIcon());
            titleTextView.setText(mensagensList.getTitulo());
            TextView contentTextView = expandedLayout.findViewById(R.id.mensagem_texto);
            text_preview.setText(mensagensList.getConteudo());
            contentTextView.setText(mensagensList.getConteudo());
            dateTextView.setText(mensagensList.getData());
            //
            text_preview.setTextColor(Color.WHITE);

            boolean isExpanded = expandedPositions.get(position, false);

            expandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            if (mensagensList.isRead()) {mensagensList.setRead(true);}
            if (mensagensList.isExpanded()) {
                updateMessageViewAsRead();
                mensagensList.setRead(true);
            } else {
                updateMessageViewAsUnread();
            }

            switch (mensagensList.getCategoria()) {
                case 1:
                    buttonCompart.setVisibility(View.VISIBLE);
                    buttonMsgExpandida.setVisibility(View.VISIBLE);
                    buttonMsgExpandida.setText("Pagamentos e Resgates");
                    buttonMsgExpandida.setTextColor(Color.WHITE);
                    buttonMsgExpandida.setBackgroundColor(contentTextView.getResources().getColor(R.color.pushbotaoaposteagora));
                    break;
                case 2:
                    buttonCompart.setVisibility(View.VISIBLE);
                    buttonMsgExpandida.setVisibility(View.VISIBLE);
                    buttonMsgExpandida.setTextColor(Color.WHITE);
                    buttonMsgExpandida.setText("Lembretes da Sorte");
                    buttonMsgExpandida.setBackgroundColor(contentTextView.getResources().getColor(R.color.pushbotaoconferiragora));
                    break;
//                case 3:
//                    buttonMsgExpandida.setVisibility(View.VISIBLE);
//                    buttonMsgExpandida.setTextColor(Color.WHITE);
//                    buttonMsgExpandida.setText("Resultados Disponíveis");
//                    buttonMsgExpandida.setBackgroundColor(Color.GREEN);
//                    break;
//                case 4:
//                    buttonMsgExpandida.setVisibility(View.VISIBLE);
//                    buttonMsgExpandida.setTextColor(Color.WHITE);
//                    buttonMsgExpandida.setText("Dicas e Novidades");
//                    buttonMsgExpandida.setBackgroundColor(Color.YELLOW);
//                    break;
//                case 5:
//                    buttonMsgExpandida.setVisibility(View.VISIBLE);
//                    buttonMsgExpandida.setTextColor(Color.WHITE);
//                    buttonMsgExpandida.setText("Alertas de Segurança ou Disponibilidade de Serviços");
//                    buttonMsgExpandida.setBackgroundColor(Color.RED);
//                    break;
                default:
                    buttonMsgExpandida.setVisibility(View.GONE);
                    buttonCompart.setVisibility(View.GONE);
                    buttonMsgExpandida.setText("");
            }
        }

        private void updateMessageViewAsUnread() {
            itemView.setBackgroundColor(contentTextView.getResources().getColor(R.color.pushbotaoconferiragora));
            titleTextView.setTextColor(Color.GRAY);
        }

        private void updateMessageViewAsRead() {
            itemView.setBackgroundColor(Color.TRANSPARENT);
            itemView.setBackgroundColor(contentTextView.getResources().getColor(R.color.branco));
            titleTextView.setTextColor(Color.BLACK);
        }

        public void setExpanded(boolean expanded) {
            titleTextView.setBackgroundColor(Color.WHITE);
            text_preview.setVisibility(!expanded ? View.VISIBLE : View.INVISIBLE);
            expandedLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();

            //fecha a antiga expandida na ultima posicao
            //so pode ter uma expandidade aberta
            if (positionLast != position) {
                expandedPositions.delete(positionLast);
            }

            if (position != RecyclerView.NO_POSITION) {
                positionLast = position;
                boolean isExpanded = expandedPositions.get(position, false);
                expandedPositions.put(position, !isExpanded);
                setExpanded(!isExpanded);
                notifyDataSetChanged();
            } else {
                positionLast = -1;
                expandedPositions.delete(position);
                notifyDataSetChanged();
            }

            if (v.getId() == R.id.buttonCompart) {
                MessagePush messagePush = mensagensList.get(position);
                shareMessage(v.getContext(), messagePush);
                notifyDataSetChanged();
            }

            notifyDataSetChanged();
        }

        private void shareMessage(Context context, MessagePush messagePush) {
            String textToShare = messagePush.getTitulo() + " <br>" + messagePush.getConteudo();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            context.startActivity(Intent.createChooser(shareIntent, "Compartilhar mensagem"));
        }

    }

    public int getPositionLast() {
        return positionLast;
    }

    public MessagePush getMensagem(int position) {
        return mensagensList.get(position);
    }

    public void deleteMensagem(int position) {
        mensagensList.remove(position);
        notifyItemRemoved(position);
    }
}
