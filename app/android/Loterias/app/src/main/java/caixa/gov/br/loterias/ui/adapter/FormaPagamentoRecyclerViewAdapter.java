package caixa.gov.br.loterias.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import caixa.gov.br.loterias.R;

public class FormaPagamentoRecyclerViewAdapter  extends RecyclerView.Adapter<FormaPagamentoRecyclerViewAdapter.ApostaHolder> {
    private int selected_position = -1;

    private Context context;
    private ArrayList<String> valoresList;

    public FormaPagamentoRecyclerViewAdapter(ArrayList<String> valoresList, Context context){
        this.context = context;
        this.valoresList = valoresList;
    }

    public class ApostaHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView imageViewBandeiraCartao;
        final ImageView imageViewSelecionado;
        final TextView textBandeiraCartao;
        final TextView numeroCartao;
        final Button buttonRemoverCartao;

        ApostaHolder(View view){
            super(view);

            imageViewBandeiraCartao = view.findViewById(R.id.imageViewCartao);
            imageViewSelecionado = view.findViewById(R.id.imageViewSelecionado);
            textBandeiraCartao = view.findViewById(R.id.textBandeiraCartao);
            numeroCartao = view.findViewById(R.id.textNumeroCartaoMask);
            buttonRemoverCartao = view.findViewById(R.id.buttonRemoverFormaPagamento);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

            notifyItemChanged(selected_position);
            selected_position = getAdapterPosition();
            notifyItemChanged(selected_position);

        }
    }

    @Override
    public FormaPagamentoRecyclerViewAdapter.ApostaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_forma_pagamento, parent, false);

        return new FormaPagamentoRecyclerViewAdapter.ApostaHolder(view);
    }

    @Override
    public void onBindViewHolder(FormaPagamentoRecyclerViewAdapter.ApostaHolder holder, int position) {
        if (selected_position == position){
            holder.itemView.setBackgroundResource(R.color.cinzaclaro);
            holder.imageViewSelecionado.setVisibility(View.VISIBLE);
        }else{
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.imageViewSelecionado.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return valoresList.size();
    }
}
