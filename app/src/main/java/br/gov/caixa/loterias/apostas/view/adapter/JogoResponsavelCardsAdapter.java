package br.gov.caixa.loterias.apostas.view.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import br.gov.caixa.loterias.apostas.R;

public class JogoResponsavelCardsAdapter extends RecyclerView.Adapter<JogoResponsavelCardsAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<String> descricoes, titulos;
    private ArrayList<Integer> idsImagens;

    public JogoResponsavelCardsAdapter(Activity activity, ArrayList<String> titulos, ArrayList<String> descricoes, ArrayList<Integer> idsImagens) {
        this.titulos = titulos;
        this.descricoes = descricoes;
        this.idsImagens = idsImagens;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.card_jogo_responsavel, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.descricao.setText(descricoes.get(position));
        holder.titulo.setText(titulos.get(position));
        if  (idsImagens == null){
            holder.icone.setVisibility(View.GONE);
        } else {
            holder.icone.setImageDrawable(activity.getDrawable(idsImagens.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return titulos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titulo;
        private TextView descricao;
        private ImageView icone;

        public ViewHolder (View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tv_titulo);
            descricao = itemView.findViewById(R.id.tv_descricao);
            icone = itemView.findViewById(R.id.iv_icone);
        }
    }
}
