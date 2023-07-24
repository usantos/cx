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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class MensagempushAdapter extends RecyclerView.Adapter<MensagempushAdapter.MessageViewHolder> {
    private List<MessagePush> mensagensList;
    private SparseBooleanArray expandedPositions;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private int positionLast = RecyclerView.NO_POSITION;
    private OnItemClickListener clickListener;

    public MensagempushAdapter(List<MessagePush> mensagensList) {
        this.mensagensList = mensagensList;
        this.expandedPositions = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_push, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setMensagensList(List<MessagePush> mensagensList) {
        this.mensagensList = mensagensList;
        notifyDataSetChanged();
    }

//
////        final SwipeLayout swipeLayout = (SwipeLayout) holder.itemView;
////        swipeLayout.setClickToClose(true);
////        swipeLayout.getDragEdgeMap().clear();
////        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.btn_msg_lidas));
////        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, swipeLayout.findViewById(R.id.btn_msg_eliminar));
////        swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);

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

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iconImageView;
        private TextView titleTextView;
        private TextView contentTextView;
        private TextView categoriaTextView;
        private TextView text_preview;
        private TextView dateTextView;
        private ConstraintLayout expandedLayout, cabecalho;
        private Button buttonCompart, buttonMsgExpandida;
        private Button buttonMsgLida, buttonMsgExcluir;
        private LinearLayout swipebuttomPush;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.icon_ImageView);
            buttonCompart = itemView.findViewById(R.id.buttonCompart);
//            buttonMsgExcluir = itemView.findViewById(R.id.buttonMsgExcluir);
            buttonMsgExpandida = itemView.findViewById(R.id.buttonMsgExpandida);
            buttonMsgLida = itemView.findViewById(R.id.btn_msg_lidas);
            buttonMsgExcluir = itemView.findViewById(R.id.btn_msg_eliminar);
            swipebuttomPush = itemView.findViewById(R.id.swipebuttomPush);
            cabecalho = itemView.findViewById(R.id.container_cabecalho);
            titleTextView = itemView.findViewById(R.id.titulo_msgpush);
            contentTextView = itemView.findViewById(R.id.mensagem_texto);
            dateTextView = itemView.findViewById(R.id.data_texto);
            expandedLayout = itemView.findViewById(R.id.expandedLayout);
            text_preview = itemView.findViewById(R.id.text_preview);
            cabecalho = itemView.findViewById(R.id.container_cabecalho);
            buttonCompart.setOnClickListener(this);
            buttonMsgExpandida.setOnClickListener(this);
            cabecalho.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        setSelectedPosition(position);
                        if (clickListener != null) {
                            clickListener.onItemClick(position);
                        }
                    }
                }
            });

        }

        public void bind(@NonNull MessagePush mensagensList, int position) {
            iconImageView.setImageResource(mensagensList.getIcon());
            titleTextView.setText(mensagensList.getTitulo());
            TextView contentTextView = expandedLayout.findViewById(R.id.mensagem_texto);
            text_preview.setText(mensagensList.getConteudo());
            contentTextView.setText(mensagensList.getConteudo());
            dateTextView.setText(mensagensList.getData());


            if (getAdapterPosition() == selectedPosition) {
//                cabecalho.setBackgroundColor(R.drawable.ic_cabec_push_branco);
                itemView.setBackgroundColor(R.layout.item_msg_push_selected);
                } else {
//                cabecalho.setBackgroundColor(R.drawable.ic_cabec_push);
                itemView.setBackgroundColor(R.layout.item_msg_push);
            }

            boolean isExpanded = expandedPositions.get(position, false);

            expandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            if (mensagensList.isRead()) {
                mensagensList.setRead(true);
                mensagensList.setSelected(true);
                itemView.setBackgroundColor(R.layout.item_msg_push_selected);
//                cabecalho.setBackgroundColor(R.drawable.ic_cabec_push);
            }

            if (mensagensList.isExpanded()) {
                updateMessageViewAsRead();
                mensagensList.setRead(true);
                mensagensList.setSelected(true);
                itemView.setBackgroundColor(R.layout.item_msg_push_selected);
//                cabecalho.setBackgroundColor(R.drawable.ic_cabec_push_branco);
            } else {
                updateMessageViewAsUnread();
                mensagensList.setRead(false);
                mensagensList.setSelected(false);
                itemView.setBackgroundColor(R.layout.item_msg_push);
//                cabecalho.setBackgroundColor(R.drawable.ic_cabec_push);
            }

//            if (mensagensList.isSelected()) {
//                itemView.setBackgroundColor(R.layout.item_msg_push_selected);
////                cabecalho.setBackgroundColor(R.drawable.ic_cabec_push_branco);
//            }

            switch (mensagensList.getCategoria()) {
                case 1:
                    buttonCompart.setVisibility(View.VISIBLE);
                    buttonMsgExpandida.setVisibility(View.VISIBLE);
                    buttonMsgExpandida.setText("Pagamentos e Resgates");
                    buttonMsgExpandida.setTextColor(Color.WHITE);
//                    cabecalho.setBackgroundColor(R.drawable.ic_cabec_push_branco);
                    buttonMsgExpandida.setBackgroundColor(buttonMsgExpandida.getResources().getColor(R.color.pushbotaoaposteagora));
                    break;
                case 2:
                    buttonCompart.setVisibility(View.VISIBLE);
                    buttonMsgExpandida.setVisibility(View.VISIBLE);
                    buttonMsgExpandida.setTextColor(Color.WHITE);
                    buttonMsgExpandida.setText("Lembretes da Sorte");
                    buttonMsgExpandida.setBackgroundColor(buttonMsgExpandida.getResources().getColor(R.color.pushbotaoconferiragora));
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
//            cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.fundobrancocentralmsg));
//            itemView.setBackgroundColor(itemView.getResources().getColor(R.color.fundobrancocentralmsg));
//            cabecalho.setBackgroundColor(R.drawable.ic_cabec_push);
            titleTextView.setTextColor(Color.GRAY);
        }

        private void updateMessageViewAsRead() {
//            cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.branco));
//            itemView.setBackgroundColor(itemView.getResources().getColor(R.color.branco));
//            cabecalho.setBackgroundColor(R.drawable.ic_cabec_push_branco);
            titleTextView.setTextColor(Color.BLACK);
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
            notifyDataSetChanged();
//            if (selectedPosition == position) {
//                selectedPosition = RecyclerView.NO_POSITION;
//            } else {
//                selectedPosition = position;
//            }

        }

        public void setExpanded(boolean expanded) {
            if (itemView.isClickable()) {
                titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.branco));
                text_preview.setBackgroundColor(titleTextView.getResources().getColor(R.color.branco));
                iconImageView.setBackgroundColor(titleTextView.getResources().getColor(R.color.branco));

                //                expandedLayout.setBackgroundColor(R.drawable.ic_cabec_push_branco);
//                cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.branco));
////              cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.branco));//                cabecalho.setBackgroundColor(R.drawable.ic_cabec_push_branco);
                itemView.setBackgroundColor(R.drawable.ic_cabec_push_branco);
            } else {
//                cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.fundobrancocentralmsg));
                titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.fundobrancocentralmsg));
                text_preview.setBackgroundColor(titleTextView.getResources().getColor(R.color.fundobrancocentralmsg));
                iconImageView.setBackgroundColor(titleTextView.getResources().getColor(R.color.fundobrancocentralmsg));
//                expandedLayout.setBackgroundColor(R.drawable.ic_cabec_push);
//                cabecalho.setBackgroundColor(R.drawable.ic_cabec_push);
                itemView.setBackgroundColor(R.drawable.ic_cabec_push);
            }
            text_preview.setVisibility(!expanded ? View.VISIBLE : View.INVISIBLE);
            expandedLayout.setVisibility(expanded ? View.VISIBLE : View.GONE);
        }

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
                titleTextView.setBackgroundColor(contentTextView.getResources().getColor(R.color.branco));
                cabecalho.setBackgroundColor(R.drawable.ic_cabec_push_branco);
                notifyDataSetChanged();
            } else {
                positionLast = -1;
                expandedPositions.delete(position);
                cabecalho.setBackgroundColor(R.drawable.ic_cabec_push);
                notifyDataSetChanged();
            }

            if (v.getId() == R.id.buttonCompart) {
                MessagePush messagePush = mensagensList.get(position);
                shareMessage(v.getContext(), messagePush);
                notifyDataSetChanged();
            }

            if (v.getId() == R.id.buttonMsgExpandida) {
                ViewUtils.alert(contentTextView.getContext(), contentTextView.getResources().getString(R.string.msg_push_botaocategoria) );
                notifyDataSetChanged();
            }

//            if (v.getId() == R.id.cabecalho) {
//                if (itemView.isSelected()) {
////                    cabecalho.setBackgroundColor(R.drawable.ic_cabec_push_branco);
//                    //                    cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.branco));
//                    titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.branco));
//                } else {
////                    cabecalho.setBackgroundColor(R.drawable.ic_cabec_push);
////                    cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.fundobrancocentralmsg));
//                    titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.fundobrancocentralmsg));
//                }
//            }
        }

        private void shareMessage(Context context, MessagePush messagePush) {
            String textToShare = messagePush.getTitulo() + " <br>" + messagePush.getConteudo();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
            context.startActivity(Intent.createChooser(shareIntent, "Compartilhar mensagem"));
        }

    }

    public MessagePush getMensagem(int position) {
        return mensagensList.get(position);
    }

    public void deleteMensagem(int position) {
        mensagensList.remove(position);
        notifyItemRemoved(position);
    }

}
