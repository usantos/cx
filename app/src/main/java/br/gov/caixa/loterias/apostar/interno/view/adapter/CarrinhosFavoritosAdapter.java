package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
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

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.CarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.view.CarrinhosFavoritosActivity;
import br.gov.caixa.loterias.apostar.interno.view.DetalhesCarrinhoFavoritoActivity;

public class CarrinhosFavoritosAdapter extends RecyclerView.Adapter<CarrinhosFavoritosAdapter.CarrinhosFavoritosHolder> {

    private Long idCarrinhoAtual;
    private List<CarrinhoFavoritoDTO> listaCarrinhosFavoritos;
    private Context context;
    private CarrinhosFavoritosActivity activity;
    private int posicaoAtual;

    public CarrinhosFavoritosAdapter(List<CarrinhoFavoritoDTO> listaCarrinhosFavoritos, CarrinhosFavoritosActivity activity){
        this.activity = activity;
        this.context = activity;
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
    }

    @Override
    public int getItemCount() {
        return listaCarrinhosFavoritos.size();
    }

    private void configViewsAposta(List<ApostaCarrinhoFavoritoDTO> listaApostas, LinearLayout layout, ConstraintLayout card){
        if(layout.findViewById(R.id.tv_nome_modalidade) == null){
            for (ApostaCarrinhoFavoritoDTO aposta:listaApostas) {
                layout.addView(configViewAposta(aposta));

            }
        }
        configuraAnimacao(layout,card);
    }

    private void configuraAnimacao(LinearLayout layoutDetalhes,ConstraintLayout clCard){
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
    }

    private View configViewAposta(ApostaCarrinhoFavoritoDTO aposta){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.row_aposta_carrinho_favorito,null);
            ((TextView)v.findViewById(R.id.tv_nome_modalidade)).setText(aposta.getModalidade().getDescricao());
            ((TextView)v.findViewById(R.id.tv_qtd_numeros)).setText(aposta.getQuantidadeNumeros()+ " números");
            //((TextView)v.findViewById(R.id.tv_valor_total_carrinho)).setText("R$10.00");
        if(aposta.getQuantidadeTeimosinhas() > 0){
            (v.findViewById(R.id.iv_teimosinha)).setVisibility(View.VISIBLE);
        }

        return v;
    }

    private void transformaCarrinho(){

    }

    private void detalhesCarrinho(){
        activity.startActivity(new Intent(activity, DetalhesCarrinhoFavoritoActivity.class));
    }

    private void buscaApostas(LinearLayout layout, ConstraintLayout card){
        DialogUtils.show(context);
        ApostaBO_.getInstance_(context).buscaApostasCarrinhoFavorito(idCarrinhoAtual ,new RequestListener<ListApostaCarrinhoFavoritoDTO>() {
            @Override
            public void onResponse(ListApostaCarrinhoFavoritoDTO result) {
                DialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(result.getRedirect(), activity);
                }
                configViewsAposta(result.getPayload(), layout, card);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, activity);
            }
        });
    }

    private void excluirCarrinho(Long idCarrinhoAtual){
        ViewUtils.alertTitleButtonYesOrNotListener(context, context.getResources().getString(R.string.carrinho_fav_deseja_excluir), (dialogInterface, i) -> {
            DialogUtils.show(context);
            ApostaBO_.getInstance_(context).deletaCarrinhoFavorito(idCarrinhoAtual.toString(), new RequestListener<ListCarrinhoFavoritoDTO>() {
                @Override
                public void onResponse(ListCarrinhoFavoritoDTO result) {
                    DialogUtils.dismiss();
                    if (result.getRedirect() != null) {
                        RedirectNetwork.checkRedirectSucesso(result.getRedirect(), activity);
                    }
                    listaCarrinhosFavoritos = result.getPayload();
                    activity.configuraRecyclerView(result);
                    //notifyDataSetChanged();
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    DialogUtils.dismiss();
                    RedirectNetwork.checkRedirect(error, activity);
                }
            });
        });
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
            ibExcluir.setOnClickListener(this);
            ibDetalhes.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            idCarrinhoAtual = listaCarrinhosFavoritos.get(getAdapterPosition()).getId();
            posicaoAtual = getAdapterPosition();
            switch (view.getId()){
                case R.id.tv_nome_carrinho:
                    break;
                case R.id.tv_valor_total_carrinho:
                    break;
                case R.id.ib_excluir_carrinho:
                    excluirCarrinho(idCarrinhoAtual);
                    break;
                case R.id.ib_lupa_detalhes:
                    detalhesCarrinho();
                    break;
                case R.id.ib_transformar_carrinho:
                    transformaCarrinho();
                    break;
                case R.id.cl_card_carrinho_favorito:
                    buscaApostas(layoutDetalhes,clCard);
                    break;
                    default:
                        break;

            }
        }
    }
}
