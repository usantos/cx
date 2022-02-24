package br.gov.caixa.loterias.apostas.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.controllers.CartoesActivity;
import br.gov.caixa.loterias.apostas.model.bo.DadosUsuarioBO_;
import br.gov.caixa.loterias.apostas.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostas.model.bo.RequestListener;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ListCartaoDTOResponse;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.utils.AppCenterManager;
import br.gov.caixa.loterias.apostas.utils.AlertDialogUtils;
import br.gov.caixa.loterias.apostas.utils.ViewUtils;
import br.gov.caixa.loterias.apostas.view.listener.FormaDePagamentoListener;

public class CartoesRecyclerViewAdapter extends RecyclerView.Adapter<CartoesRecyclerViewAdapter.CartaoHolder> {

    public int selected_position = 0;
    public FormaDePagamentoListener formaDePagamentoListener;
    public List<RetornoCartao> valoresList;
    private Context context;
    private Boolean flagVeioMenu;

    public CartoesRecyclerViewAdapter(List<RetornoCartao> cartoes, Context context, Boolean flagVeioMenu){
        this.context = context;
        this.valoresList = cartoes;
        this.flagVeioMenu = flagVeioMenu;
    }

    @NonNull
    @Override
    public CartaoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_cartao, parent, false);
        return new CartoesRecyclerViewAdapter.CartaoHolder(view);
    }

    @Override
    public void onBindViewHolder(CartoesRecyclerViewAdapter.CartaoHolder holder, int position) {
        RetornoCartao cartao = valoresList.get(position);

        holder.tvNumCartao.setText(context.getResources().getString(R.string.mascara_cartao_sem_ultimos_digitos) + cartao.getUltimosDigitos());
        holder.tvDataVenc.setText(context.getResources().getString(R.string.venc_ponto_dois_pontos) + cartao.getMesValidade() + context.getResources().getString(R.string.barra)  + cartao.getAnoValidade());
        holder.ivBandeira.setImageDrawable(context.getDrawable(getBandeira(cartao.getIdMetodoPagamento())));
        if(!flagVeioMenu){
            if (selected_position == position){
                holder.itemView.setBackgroundResource(R.color.cinzaclaro);
                holder.ivSelecionado.setVisibility(View.VISIBLE);
            }else{
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
                holder.ivSelecionado.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return valoresList == null? 0: valoresList.size();
    }

    public class CartaoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView ivBandeira,ivSelecionado;//, ivSelecionado
        final TextView tvNumCartao, tvDataVenc;
        final ImageButton ibRemoverCartao;
        final ConstraintLayout clCard;

        public CartaoHolder(View view) {
            super(view);
            //imageViewSelecionado = view.findViewById(R.id.imageViewSelecionado);
            //textBandeiraCartao = view.findViewById(R.id.textBandeiraCartao);
            clCard = view.findViewById(R.id.cl_card_cartao);
            ivBandeira = view.findViewById(R.id.iv_bandeira_cartao);
            tvNumCartao = view.findViewById(R.id.tv_numero_cartao);
            tvDataVenc = view.findViewById(R.id.tv_data_vencimento);
            ibRemoverCartao = view.findViewById(R.id.ib_excluir_cartao);
            ivSelecionado = view.findViewById(R.id.iv_selecionado);

            clCard.setOnClickListener(this);
            ibRemoverCartao.setOnClickListener(this);


        }
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.ib_excluir_cartao: {
                    ViewUtils.alertTitleButtonYesOrNotListener(context, context.getString(R.string.exclusao_cartao), (dialog, which) -> {
                        AlertDialogUtils.show(context);
                        DadosUsuarioBO_.getInstance_(context).deleteCartao(valoresList.get(getAdapterPosition()).getIdCartao(), new RequestListener<ListCartaoDTOResponse>() {
                            @Override
                            public void onResponse(ListCartaoDTOResponse result) {
                                AppCenterManager.registraEvento(context.getResources().getString(R.string.evento_efetuou_exclusao_forma_pagamento_sucesso));
                                AlertDialogUtils.dismiss();
                                if(result != null && result.getRedirect() != null){
                                    RedirectNetwork.checkRedirectSucesso(result.getRedirect(), ((CartoesActivity)context));
                                }
                                valoresList.remove(getAdapterPosition());
                                notifyDataSetChanged();
                                formaDePagamentoListener.itemRemoved(getItemCount() == 0);
                                selected_position = -1;
                            }

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                AlertDialogUtils.dismiss();
                                RedirectNetwork.checkRedirect(error, ((CartoesActivity)context));
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

    private int getBandeira(String nomeMetodo){
        switch (nomeMetodo){
            case "visa":
                return R.drawable.ic_visa;
            case "master":
                return R.drawable.ic_master_card;
            case "hipercard":
                return R.drawable.ic_hipercard;
            case "diners":
                return R.drawable.ic_diners;
            case "elo":
                return R.drawable.ic_elo;
            case "amex":
                return R.drawable.ic_american;
            default:
                return R.drawable.cartao_cvc;

        }
    }
}
