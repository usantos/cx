package br.gov.caixa.loterias.apostas.controllers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.swipe.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;

public class MensagempushAdapter extends RecyclerView.Adapter<MensagempushAdapter.MessageViewHolder> {
    private List<MessagePush> mensagensList;
    private SparseBooleanArray expandedPositions;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private int positionLast = RecyclerView.NO_POSITION;
    private int row_index = -1;
    private int colunaTab = 1;
    private OnItemClickListener clickListener;
    private List<MessagePush> readMensagensList;
    private List<MessagePush> unreadMensagensList;

    public MensagempushAdapter(List<MessagePush> mensagensList) {
        this.mensagensList = mensagensList;
        this.expandedPositions = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_push_swipebuttons, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        MessagePush message = mensagensList.get(position);
        holder.bind(message, position);

        boolean isExpanded = expandedPositions.get(position, false);
        holder.setExpanded(isExpanded);
        holder.setSelectedPosition(position);

//        holder.cabecalho.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                positionLast = row_index;
//                row_index = position;
//                notifyDataSetChanged();
//            }
//        });
//        if (row_index == position) {
//            holder.setExpanded(true);
//        } else {
//            holder.setExpanded(false);
//        }

    }

    @Override
    public int getItemCount() {
        return mensagensList != null ? mensagensList.size() : 0;
    }

    private List<MessagePush> getAllMensagens() {
        return mensagensList;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setMensagensList(List<MessagePush> MensagensList) {
        this.mensagensList = mensagensList;
//        this.unreadMensagensList = unreadMensagensList;
//        updateDisplayedMensagensList();
//        notifyDataSetChanged();
    }



//    private void updateDisplayedMensagensList() {
//
//        if (radioGroupMensagens.isChecked()) {
//            displayedMessages = readMensagensList;
//        } else {
//            displayedMessages = unreadMensagensList;
//        }
//        notifyDataSetChanged();
//    }

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
        private LinearLayout expandedLayout, cabecalho;
        private Button buttonCompart, buttonMsgExpandida;
        private ImageButton buttonMsgLidaItem;
        private ImageButton buttonMsgExcluirItem;
        private LinearLayout swipebuttomPush, surfacePush;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.icon_ImageView);
            buttonCompart = itemView.findViewById(R.id.buttonCompart);
            buttonMsgExpandida = itemView.findViewById(R.id.buttonMsgExpandida);
            buttonMsgLidaItem = itemView.findViewById(R.id.btn_msg_lidas);
            buttonMsgExcluirItem = itemView.findViewById(R.id.btn_msg_eliminar);
            swipebuttomPush = itemView.findViewById(R.id.swipeLayoutButtonPush);
            cabecalho = itemView.findViewById(R.id.container_cabecalho);
            titleTextView = itemView.findViewById(R.id.titulo_msgpush);
            contentTextView = itemView.findViewById(R.id.mensagem_texto);
            dateTextView = itemView.findViewById(R.id.data_texto);
            expandedLayout = itemView.findViewById(R.id.expandedLayout);
            text_preview = itemView.findViewById(R.id.text_preview);
            surfacePush = itemView.findViewById(R.id.surface_push);

            buttonCompart.setOnClickListener(this);
            buttonMsgExpandida.setOnClickListener(this);
            cabecalho.setOnClickListener(this);
            buttonMsgExcluirItem.setOnClickListener(this);
            buttonMsgLidaItem.setOnClickListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    int positionLast = selectedPosition;
//                    setExpanded(false);

                    if (position != RecyclerView.NO_POSITION) {
//                        setSelectedPosition(position);
                        MessagePush messagePush = mensagensList.get(position);
                        messagePush.setRead(true);
                        messagePush.setSelected(true);

                        if (clickListener != null) {
                            clickListener.onItemClick(position);
                            notifyItemChanged(position);
                            notifyItemChanged(selectedPosition);
                        }
                    }
                }
            });
        }

        public void bind(@NonNull MessagePush mensagensList, int position) {
            iconImageView.setImageResource(mensagensList.getIcon());
            titleTextView.setText(mensagensList.getTitulo());
            TextView contentTextView = expandedLayout.findViewById(R.id.mensagem_texto);
            contentTextView.setText(mensagensList.getConteudo());
            text_preview.setText(mensagensList.getConteudo());
            dateTextView.setText(mensagensList.getData());

            final SwipeLayout swipeLayout = (SwipeLayout) this.itemView;
            swipeLayout.setClickToClose(true);
            swipeLayout.getDragEdgeMap().clear();
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, swipeLayout.findViewById(R.id.swipeLayoutButtonPush));
            swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
//            swipebuttomPush.setAnimation();

            swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                @Override
                public void onClose(SwipeLayout layout) {
                    //when the SurfaceView totally cover the BottomView.
                }

                @Override
                public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                    //you are swiping.
                }

                @Override
                public void onStartOpen(SwipeLayout layout) {

                }

                @Override
                public void onOpen(SwipeLayout layout) {
                    //when the BottomView totally show.
                }

                @Override
                public void onStartClose(SwipeLayout layout) {

                }

                @Override
                public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                    //when user's hand released.
                }
            });

//            if (mensagensList.isExpanded()) {
//                cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.branco));
//                titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.black));
//                cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.branco));
//                iconImageView.setBackgroundColor(iconImageView.getResources().getColor(R.color.branco));
//                text_preview.setBackgroundColor(text_preview.getResources().getColor(R.color.branco));
//                dateTextView.setBackgroundColor(dateTextView.getResources().getColor(R.color.branco));
//            } else {
//                cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.pesquisa_msgpush));
//                titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.cinza));
//                cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.pesquisa_msgpush));
//                titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.pesquisa_msgpush));
//                iconImageView.setBackgroundColor(iconImageView.getResources().getColor(R.color.pesquisa_msgpush));
//                text_preview.setBackgroundColor(text_preview.getResources().getColor(R.color.pesquisa_msgpush));
//                dateTextView.setBackgroundColor(dateTextView.getResources().getColor(R.color.pesquisa_msgpush));
//            }

            boolean isExpanded = expandedPositions.get(position, false);

            expandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            if (mensagensList.isExpanded()) {
//                mensagensList.setRead(true);
                updateMessageViewAsRead();
                mensagensList.setSelected(true);
            } else {
                updateMessageViewAsUnread();
//                mensagensList.setRead(false);
                mensagensList.setSelected(false);
            }

            if (mensagensList.isSelected()) {
                titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.black));
                titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.branco));
                cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.branco));
                surfacePush.setBackgroundColor(surfacePush.getResources().getColor(R.color.branco));
                iconImageView.setBackgroundColor(surfacePush.getResources().getColor(R.color.branco));
                text_preview.setBackgroundColor(text_preview.getResources().getColor(R.color.branco));
                dateTextView.setBackgroundColor(dateTextView.getResources().getColor(R.color.branco));
            } else {
                cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.pesquisa_msgpush));
                surfacePush.setBackgroundColor(surfacePush.getResources().getColor(R.color.pesquisa_msgpush));
                titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.pesquisa_msgpush));
                titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.cinza));
                iconImageView.setBackgroundColor(iconImageView.getResources().getColor(R.color.pesquisa_msgpush));
                text_preview.setBackgroundColor(text_preview.getResources().getColor(R.color.pesquisa_msgpush));
                dateTextView.setBackgroundColor(dateTextView.getResources().getColor(R.color.pesquisa_msgpush));
            }

            switch (mensagensList.getCategoria()) {
//                case 1:
//                    buttonCompart.setVisibility(View.VISIBLE);
//                    buttonMsgExpandida.setVisibility(View.VISIBLE);
//                    buttonMsgExpandida.setText("Pagamentos e Resgates");
//                    buttonMsgExpandida.setTextColor(Color.WHITE);
//                    buttonMsgExpandida.setBackgroundColor(buttonMsgExpandida.getResources().getColor(R.color.pushbotaoaposteagora));
//                    break;
                case 2:
                    buttonCompart.setVisibility(View.VISIBLE);
                    buttonMsgExpandida.setVisibility(View.VISIBLE);
                    buttonMsgExpandida.setTextColor(Color.WHITE);
                    buttonMsgExpandida.setText("Apostar agora");
                    buttonMsgExpandida.setBackgroundColor(buttonMsgExpandida.getResources().getColor(R.color.pushbotaoaposteagora));
                    break;
                case 3:
//                    buttonCompart.setVisibility(View.VISIBLE);
                    buttonMsgExpandida.setVisibility(View.VISIBLE);
                    buttonMsgExpandida.setTextColor(Color.WHITE);
                    buttonMsgExpandida.setText("Conferir agora");
                    buttonMsgExpandida.setBackgroundColor(buttonMsgExpandida.getResources().getColor(R.color.pushbotaoconferiragora));
                    break;
                case 4:
                    buttonCompart.setVisibility(View.VISIBLE);
//                    buttonMsgExpandida.setVisibility(View.VISIBLE);
//                    buttonMsgExpandida.setTextColor(Color.WHITE);
//                    buttonMsgExpandida.setText("Dicas e Novidades");
//                    buttonMsgExpandida.setBackgroundColor(Color.YELLOW);
                    break;
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
                    break;
            }

        }

        private void updateMessageViewAsUnread() {
            cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.pesquisa_msgpush));
            surfacePush.setBackgroundColor(surfacePush.getResources().getColor(R.color.pesquisa_msgpush));
            titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.pesquisa_msgpush));
            titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.cinza));
            iconImageView.setBackgroundColor(iconImageView.getResources().getColor(R.color.pesquisa_msgpush));
            text_preview.setBackgroundColor(text_preview.getResources().getColor(R.color.pesquisa_msgpush));
            dateTextView.setBackgroundColor(dateTextView.getResources().getColor(R.color.pesquisa_msgpush));
        }

        private void updateMessageViewAsRead() {
            cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.branco));
            surfacePush.setBackgroundColor(surfacePush.getResources().getColor(R.color.branco));
            text_preview.setBackgroundColor(text_preview.getResources().getColor(R.color.branco));
            titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.branco));
            titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.black));
            iconImageView.setBackgroundColor(iconImageView.getResources().getColor(R.color.branco));
            dateTextView.setBackgroundColor(dateTextView.getResources().getColor(R.color.branco));
        }

        public void setSelectedPosition(int position) {
            positionLast = position;
            if (selectedPosition == position) {
                selectedPosition = RecyclerView.NO_POSITION;
            } else {
                selectedPosition = position;
                itemView.setSelected(true);
            }
        }

        public void onClick(View v) {
            int position = getAdapterPosition();
            //fecha a antiga expandida na ultima posicao
            //so pode ter uma expandidade aberta
            if (positionLast != position) {
                if (positionLast != -1) {
                    updateMessageViewAsRead();
                    setExpanded(false);
//                    expandedPositions.delete(positionLast);
                    notifyItemChanged(positionLast);
                    notifyDataSetChanged();
                }
            }

//            setSelectedPosition(position);
            if (position != RecyclerView.NO_POSITION) {
                positionLast = position;
                boolean isExpanded = expandedPositions.get(position, false);
                expandedPositions.put(position, !isExpanded);
                itemView.setSelected(true);
                setExpanded(!isExpanded);
                updateMessageViewAsRead();
                notifyItemChanged(position);
                notifyDataSetChanged();
            } else {
                positionLast = -1;
                setExpanded(false);
                expandedPositions.delete(position);
                notifyItemChanged(position);
                notifyDataSetChanged();
            }

            if (v.getId() == R.id.buttonCompart) {
                MessagePush messagePush = mensagensList.get(position);
                shareMessage(v.getContext(), messagePush);
                notifyItemChanged(position);
                notifyDataSetChanged();
            }

            if (v.getId() == R.id.buttonMsgExpandida) {
                ViewUtils.alert(contentTextView.getContext(), contentTextView.getResources().getString(R.string.msg_push_botaocategoria));
                notifyItemChanged(position);
                notifyDataSetChanged();
            }
            if (v.getId() == R.id.btn_msg_eliminar) {
                ViewUtils.alertTitleButtonYesOrNotListener(
                        v.getContext(),
                        itemView.getResources().getString(R.string.msg_push_excluir_item),
                        (dialog, which) -> {
                            deleteMensagem(position);
                            notifyItemChanged(position);
                            notifyDataSetChanged();
                        },
                        (dialog, which) -> {
                            dialog.dismiss();
                        });
            }

            if (v.getId() == R.id.btn_msg_lidas) {
                ViewUtils.alertTitleButtonYesOrNotListener(
                        v.getContext(),
                        itemView.getResources().getString(R.string.msg_push_marcar_lida_item),
                        (dialog, which) -> {
                            marcaMensagemLida(position);
                            notifyItemChanged(position);
                            notifyDataSetChanged();
                        },
                        (dialog, which) -> {
                            dialog.dismiss();
                        });
            }

        }

        public void setExpanded(boolean isExpanded) {
            if (isExpanded) {
                titleTextView.setTextColor(Color.BLACK);
                titleTextView.setBackgroundColor(titleTextView.getResources().getColor(R.color.branco));
                cabecalho.setBackgroundColor(cabecalho.getResources().getColor(R.color.branco));
                surfacePush.setBackgroundColor(surfacePush.getResources().getColor(R.color.branco));
                titleTextView.setTextColor(titleTextView.getResources().getColor(R.color.black));
                iconImageView.setBackgroundColor(iconImageView.getResources().getColor(R.color.branco));
                text_preview.setBackgroundColor(text_preview.getResources().getColor(R.color.branco));
                dateTextView.setBackgroundColor(dateTextView.getResources().getColor(R.color.branco));
            }
//
            text_preview.setVisibility(!isExpanded ? View.VISIBLE : View.INVISIBLE);
            expandedLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
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
        getMensagem(position).setApagada(true);
        mensagensList.remove(position);
        notifyItemRemoved(position);
    }

    public void setColunaTab (int dadoCol) {
        colunaTab = dadoCol;
    }

    public void marcaMensagemLida(int position) {
        getMensagem(position).setRead(true);
        if (colunaTab == 2) mensagensList.remove(position);
        notifyItemRemoved(position);
    }

}


