package br.gov.caixa.loterias.apostar.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostar.R;

public class NovaFormaPagamentoBandeiraAdapter extends RecyclerView.Adapter<NovaFormaPagamentoBandeiraAdapter.ApostaHolder> {

    private int selected_position = -1;

    private Context context;
    private ArrayList<String> valoresList;

    public NovaFormaPagamentoBandeiraAdapter(ArrayList<String> valoresList, Context context){
        this.context = context;
        this.valoresList = valoresList;
    }

    public class ApostaHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        final ImageView bandeiraImageView;

        ApostaHolder(View view){
            super(view);

            bandeiraImageView = itemView.findViewById(R.id.imageViewbandeiraCartao);

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
    public NovaFormaPagamentoBandeiraAdapter.ApostaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_nova_forma_pagamento_bandeira_cartao, parent, false);

        return new NovaFormaPagamentoBandeiraAdapter.ApostaHolder(view);
    }

    @Override
    public void onBindViewHolder(ApostaHolder holder, int position) {
        if (selected_position == position){
            holder.bandeiraImageView.setBackgroundResource(R.drawable.image_view_with_border_green);

            float pxHeigth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, Resources.getSystem().getDisplayMetrics());
            float pxWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 41, Resources.getSystem().getDisplayMetrics());

            holder.bandeiraImageView.getLayoutParams().height = (int)pxHeigth;
            holder.bandeiraImageView.getLayoutParams().width = (int)pxWidth;
            holder.bandeiraImageView.requestLayout();

        }else{
            holder.bandeiraImageView.setBackgroundResource(0);
            holder.bandeiraImageView.setPadding(0,0,0,0);

            float pxHeigth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26, Resources.getSystem().getDisplayMetrics());
            float pxWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 37, Resources.getSystem().getDisplayMetrics());

            holder.bandeiraImageView.getLayoutParams().height = (int)pxHeigth;
            holder.bandeiraImageView.getLayoutParams().width = (int)pxWidth;
            holder.bandeiraImageView.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return valoresList.size();
    }
}
