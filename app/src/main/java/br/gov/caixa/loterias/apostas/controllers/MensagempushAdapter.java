package br.gov.caixa.loterias.apostas.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.enums.FiltroMessagePushEnum;
import br.gov.caixa.loterias.apostas.view.holder.MensagemPushViewHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnMensagemPushListener;

public class MensagempushAdapter extends RecyclerView.Adapter<MensagemPushViewHolder> {
    private List<MessagePush> mensagensList;
    private List<MessagePush> listaApresentada;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private int positionLast = RecyclerView.NO_POSITION;
    private FiltroMessagePushEnum filtro;
    private OnMensagemPushListener listener;

    public MensagempushAdapter(List<MessagePush> mensagensList, OnMensagemPushListener listener) {
        this.mensagensList = mensagensList;
        this.listener = listener;
        this.listaApresentada = new ArrayList<>();
        this.filtro = FiltroMessagePushEnum.TODAS;
        showAllMessages();
    }

    @NonNull
    @Override
    public MensagemPushViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg_push_swipebuttons, parent, false);
        return new MensagemPushViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MensagemPushViewHolder holder, int position) {
        MessagePush message = listaApresentada.get(position);
        holder.bind(message, position);
    }

    @Override
    public int getItemCount() {
        return listaApresentada != null ? listaApresentada.size() : 0;
    }

    public void recarregaLista() {
        if (isMensagemNaoLidas()){
            showNotReadMessages();
        } else {
            showAllMessages();
        }
    }

    private void showNotReadMessages() {
        listaApresentada.clear();
        for (MessagePush message : mensagensList){
            if (!message.isRead()){
                listaApresentada.add(message);
            }
        }
        notifyDataSetChanged();
    }

    private void showAllMessages() {
        listaApresentada.clear();
        listaApresentada.addAll(mensagensList);
        notifyDataSetChanged();
    }

    public void apagarTodas() {
        mensagensList.clear();
        listaApresentada.clear();
        notifyDataSetChanged();
    }

    public void mensagensFiltradas(String searchEditText) {

        if (searchEditText.isEmpty()) {
            recarregaLista();
        } else {
            List<MessagePush> mensagensFiltradas = new ArrayList<>();
            for (MessagePush message : listaApresentada) {
                if (message.getTitulo().toLowerCase().contains(searchEditText.toLowerCase()) ||
                        message.getConteudo().toLowerCase().contains(searchEditText.toLowerCase())) {
                    mensagensFiltradas.add(message);
                }
            }

            listaApresentada.clear();
            listaApresentada.addAll(mensagensFiltradas);
            notifyDataSetChanged();
        }

    }

    public MessagePush getMensagem(int position) {
        return mensagensList.get(position);
    }

    public void deleteMensagem(int position) {
        listaApresentada.remove(position);
        notifyItemRemoved(position);
    }

    public void setFiltro(FiltroMessagePushEnum filtro) {
        this.filtro = filtro;
    }

    public void marcaMensagemLida(int position) {
        listaApresentada.get(position).setRead(true);

        if (isMensagemNaoLidas()){
            listaApresentada.remove(position);
            notifyItemRemoved(position);
        } else {
            notifyItemChanged(position);
        }
    }

    private boolean isMensagemNaoLidas() {
        return filtro == FiltroMessagePushEnum.NAO_LIDAS;
    }

    public MessagePush getItemByPosition(int position) {
        return listaApresentada.get(position);
    }

    public void mudarVisibilidade(int position) {
        listaApresentada.get(position).setExpanded(!listaApresentada.get(position).isExpanded());
        if (listaApresentada.get(position).isExpanded()){
            listaApresentada.get(position).setRead(true);
        }

        notifyItemChanged(position);
    }

    public void marcarTodasComoLidas() {
        for (MessagePush messagePush : mensagensList){
            messagePush.setRead(true);
        }

        recarregaLista();

        notifyDataSetChanged();
    }

    public void closeAll() {
        for (int position = 0; position < listaApresentada.size(); position++){
            if (listaApresentada.get(position).isExpanded()){
                listaApresentada.get(position).setExpanded(false);
                notifyItemChanged(position);
            }
        }
    }

    public void fecharTodosExceto(int item) {
        for (int position = 0; position < listaApresentada.size(); position++){
            if (listaApresentada.get(position).isExpanded() && position != item){
                listaApresentada.get(position).setExpanded(false);
                notifyItemChanged(position);
            }
        }
    }

    public FiltroMessagePushEnum getFiltro() {
        return this.filtro;
    }

    public boolean isListaVazia() {
        return listaApresentada != null && listaApresentada.size() == 0;
    }
}


