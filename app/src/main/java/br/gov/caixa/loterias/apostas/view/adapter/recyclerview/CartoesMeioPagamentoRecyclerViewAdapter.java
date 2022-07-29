package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.view.holder.CartaoMeioPagamentoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnCartaoMeioPagamentoListener;

public class CartoesMeioPagamentoRecyclerViewAdapter extends RecyclerView.Adapter<CartaoMeioPagamentoHolder> {

    public List<RetornoCartao> lista;
    private Context context;
    private OnCartaoMeioPagamentoListener listener;

    public CartoesMeioPagamentoRecyclerViewAdapter(List<RetornoCartao> cartoes, Context context, OnCartaoMeioPagamentoListener listener){
        this.context = context;
        this.lista = cartoes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartaoMeioPagamentoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cartao_meio_pagamento, parent, false);
        return new CartaoMeioPagamentoHolder(view, context, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartaoMeioPagamentoHolder cartaoMeioPagamentoHolder, int position) {
        RetornoCartao cartao = lista.get(position);
        cartaoMeioPagamentoHolder.bind(cartao, position);
    }

    @Override
    public int getItemCount() {
        return lista == null ? 0 : lista.size();
    }

}
