package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import br.gov.caixa.loterias.apostar.interno.R;

public class DetalhesApostasCarrinhoFavoritoAdapter extends RecyclerView.Adapter<DetalhesApostasCarrinhoFavoritoAdapter.DetalheViewHolder> {

    @NonNull
    @Override
    public DetalheViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_detalhes_apostas_carrinho_fav, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new DetalheViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetalheViewHolder holder, int position) {
        holder.tvMesDaSorte.setText("Julho");
        holder.tvMesDaSorte.setVisibility(View.VISIBLE);
        holder.tvModalidade.setText("Mega");
        holder.tvNumeros.setText("10 - 10 - 10 - 10 - 10 - 10 - 10 - 10 - 10 ");
        holder.ivTeimosinha.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return 60;
    }

    class DetalheViewHolder extends RecyclerView.ViewHolder {

        private TextView tvModalidade, tvMesDaSorte, tvNumeros;
        private ImageButton btnExcluir;
        private ImageView ivTeimosinha;

        public DetalheViewHolder(View view) {
            super(view);
            tvModalidade = view.findViewById(R.id.tv_modalidade);
            tvMesDaSorte = view.findViewById(R.id.tv_mes_da_sorte);
            tvNumeros = view.findViewById(R.id.tv_numeros);
            btnExcluir = view.findViewById(R.id.ib_excluir);
            ivTeimosinha = view.findViewById(R.id.iv_teimosinha);
        }
    }
}
