package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoFavoritoDTO;

public class CarrinhosFavoritosAdapter extends RecyclerView.Adapter<CarrinhosFavoritosAdapter.CarrinhosFavoritosHolder> {

    private int posicaoSelecionada = -1;
    private List<CarrinhoFavoritoDTO> listaCarrinhosFavoritos;
    private Context context;

    public CarrinhosFavoritosAdapter(List<CarrinhoFavoritoDTO> listaCarrinhosFavoritos, Context context){
        this.context = context;
        this.listaCarrinhosFavoritos = listaCarrinhosFavoritos;
    }

    private List<CarrinhoFavoritoDTO> getMock(){
        return new ArrayList<>();
    }
    @NonNull
    @Override
    public CarrinhosFavoritosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_carrinho_favorito, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new CarrinhosFavoritosHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull CarrinhosFavoritosHolder holder, int position) {
        CarrinhoFavoritoDTO carrinhoAtual = listaCarrinhosFavoritos.get(position);
        holder.tvNomeCarrinho.setText(carrinhoAtual.getNome());
        if(holder.layoutDetalhes.findViewById(R.id.tv_nome_modalidade) == null){
            for (int x = 1; x <= 4; x++){
                holder.layoutDetalhes.addView(configViewAposta());
            }
        }
    }

    @Override
    public int getItemCount() {
        return listaCarrinhosFavoritos.size();
    }

    private void configViewsAposta(){

    }

    private View configViewAposta(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.row_aposta_carrinho_favorito,null);
        ((TextView)v.findViewById(R.id.tv_nome_modalidade)).setText("modalidade");
        (v.findViewById(R.id.ib_excluir_carrinho)).setVisibility(View.VISIBLE);
        ((TextView)v.findViewById(R.id.tv_qtd_numeros)).setText("9 números");
        ((TextView)v.findViewById(R.id.tv_valor_total_carrinho)).setText("R$ 250.00");
        return v;
    }

    class CarrinhosFavoritosHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tvNomeCarrinho, tvValorTotalCarrinho;
        private ImageButton ibExcluir, ibDetalhes, ibTransformar;
        private ConstraintLayout clCard;
        private LinearLayout layoutDetalhes;

        public CarrinhosFavoritosHolder(View itemView) {
            super(itemView);
            tvNomeCarrinho = itemView.findViewById(R.id.tv_nome_carrinho);
            tvValorTotalCarrinho = itemView.findViewById(R.id.tv_valor_total_carrinho);
            ibExcluir = itemView.findViewById(R.id.ib_excluir_carrinho);
            ibDetalhes = itemView.findViewById(R.id.ib_lupa_detalhes);
            ibTransformar = itemView.findViewById(R.id.ib_transformar_carrinho);
            clCard = itemView.findViewById(R.id.cl_card_carrinho_favorito);
            layoutDetalhes = itemView.findViewById(R.id.ll_apostas_carrinho);
            clCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.tv_nome_carrinho:
                    break;
                case R.id.tv_valor_total_carrinho:
                    break;
                case R.id.ib_excluir_carrinho:
                    break;
                case R.id.ib_lupa_detalhes:
                    break;
                case R.id.ib_transformar_carrinho:
                    break;
                case R.id.cl_card_carrinho_favorito:
                    if(layoutDetalhes.getVisibility() == View.GONE){
                        layoutDetalhes.animate()
                                .setDuration(100)
                                .alpha(1)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        layoutDetalhes.setVisibility(View.VISIBLE);
                                        clCard.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.azul_card));

                                    }
                                });
                    }else {
                        layoutDetalhes.animate()
                                .alpha(0)
                                .setDuration(100)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        layoutDetalhes.setVisibility(View.GONE);
                                        clCard.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.verde_card));

                                    }
                                });
                    }
                    break;
                    default:
                        break;

            }
        }
    }
}
