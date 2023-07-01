package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.view.holder.CartaoMeioPagamentoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnCartaoMeioPagamentoListener;

public class CartoesMeioPagamentoRecyclerViewAdapter extends RecyclerView.Adapter<CartaoMeioPagamentoHolder> {

    public List<RetornoCartao> lista;
    public List<RetornoCartao> listaApresentada;
    private Context context;
    private OnCartaoMeioPagamentoListener listener;
    private boolean mostrandoMais;

    public CartoesMeioPagamentoRecyclerViewAdapter(List<RetornoCartao> cartoes, Context context, OnCartaoMeioPagamentoListener listener){
        this.context = context;
        this.lista = cartoes;
        this.listener = listener;
        mostrarMenos();
    }

    @NonNull
    @Override
    public CartaoMeioPagamentoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cartao_meio_pagamento, parent, false);
        return new CartaoMeioPagamentoHolder(view, context, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartaoMeioPagamentoHolder holder, int position) {
        if (listaApresentada.size() <= 1){
            holder.showButtonFav(false);
            if (listaApresentada.size() == 1){
                listaApresentada.get(0).setFav(false);
            }
        } else {
            holder.showButtonFav(false);
        }

        RetornoCartao cartao = listaApresentada.get(position);
        holder.bind(cartao, position);
    }

    @Override
    public int getItemCount() {
        return listaApresentada == null ? 0 : listaApresentada.size();
    }

    public void atualizaCartaoFavorito(RetornoCartao retornoCartao) {
        for (RetornoCartao cartao: lista){
            if (cartao.getIdCartao() == retornoCartao.getIdCartao()){
                cartao.setFav(true);
            } else {
                cartao.setFav(false);
            }
        }
        notifyDataSetChanged();
    }

    public void mostrarTodos() {
        limparLista();

        listaApresentada.addAll(lista);

        mostrandoMais = true;
        notifyDataSetChanged();
    }

    public void mostrarMenos() {
        limparLista();

        if (lista != null && !lista.isEmpty()){
            if (lista.size() > 2){
                listaApresentada.add(lista.get(0));
                listaApresentada.add(lista.get(1));
            } else {
                for (int i = 0; i<lista.size();i++){
                    listaApresentada.add(lista.get(i));
                }
            }
        }

        mostrandoMais = false;
        notifyDataSetChanged();
    }

    private void limparLista() {
        if (listaApresentada != null) {
            listaApresentada.clear();
        } else {
            listaApresentada = new ArrayList<>();
        }
    }

    public boolean temMaisPraMostrar() {
        return lista.size() > 2;
    }

    public void atualizaLista() {
        if (mostrandoMais){
            mostrarTodos();
        } else {
            mostrarMenos();
        }
    }
}
