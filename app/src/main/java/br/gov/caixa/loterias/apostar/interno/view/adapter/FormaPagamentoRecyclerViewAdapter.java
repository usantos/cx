package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.MensagensNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.FirebaseManager;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.listener.FormaDePagamentoListener;


public class FormaPagamentoRecyclerViewAdapter  extends RecyclerView.Adapter<FormaPagamentoRecyclerViewAdapter.ApostaHolder> {
    public int selected_position = -1;


    public FormaDePagamentoListener formaDePagamentoListener;
    private Context context;
    public List<RetornoCartao> valoresList;

    public FormaPagamentoRecyclerViewAdapter(List<RetornoCartao> cartoes, Context context){
        this.context = context;
        this.valoresList = cartoes;
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
            buttonRemoverCartao.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.buttonRemoverFormaPagamento: {
                    ViewUtils.alertTitleButtonYesOrNotListener(context, context.getString(R.string.exclusao_cartao), (dialog, which) -> {
                        DialogUtils.show(context);
                        DadosUsuarioBO_.getInstance_(context).deleteCartao(valoresList.get(getAdapterPosition()).getIdCartao(), new RequestListener<ListCartaoDTOResponse>() {
                            @Override
                            public void onResponse(ListCartaoDTOResponse result) {
                                FirebaseManager.registraEvento(context, "EFETUOU_EXCLUSAO_FORMA_DE_PAGAMENTO_SUCESSO");
                                DialogUtils.dismiss();
                                valoresList.remove(getAdapterPosition());
                                notifyDataSetChanged();
                                formaDePagamentoListener.itemRemoved();
                                selected_position = -1;
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                onError(error);
                            }
                        });
                    });
                }
                break;
                default:{
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

                    notifyItemChanged(selected_position);
                    selected_position = getAdapterPosition();
                    notifyItemChanged(selected_position);
                }
            }
        }
    }

    @Override
    public FormaPagamentoRecyclerViewAdapter.ApostaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_forma_pagamento, parent, false);

        return new FormaPagamentoRecyclerViewAdapter.ApostaHolder(view);
    }

    @Override
    public void onBindViewHolder(FormaPagamentoRecyclerViewAdapter.ApostaHolder holder, int position) {
        RetornoCartao cartao = valoresList.get(position);

        holder.numeroCartao.setText("**** **** **** " + cartao.getUltimosDigitos());
        holder.textBandeiraCartao.setText(ViewUtils.capitalized(cartao.getNomeMetodoPagamento()));
        Glide.with(context).load(cartao.getSecureThumbnail()).into( holder.imageViewBandeiraCartao);
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

    private void onError(VolleyError error) {
        DialogUtils.dismiss();
        String msgError = MensagensNetwork.setMensagem(error, context);
        ViewUtils.alert(context, msgError);
    }
}
