package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.ApostaBO_;
import br.gov.caixa.loterias.apostar.interno.model.bo.RedirectNetwork;
import br.gov.caixa.loterias.apostar.interno.model.bo.RequestListener;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListApostaCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ListCarrinhoFavoritoDTO;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.utils.DialogUtils;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;
import br.gov.caixa.loterias.apostar.interno.controllers.DetalhesCarrinhoFavoritoActivity;

import static br.gov.caixa.loterias.apostar.interno.controllers.CarrinhosFavoritosActivity.RESPOSTA_EXCLUIU_CARRINHO;

public class DetalhesApostasCarrinhoFavoritoAdapter extends RecyclerView.Adapter<DetalhesApostasCarrinhoFavoritoAdapter.DetalheViewHolder> {

    private List<ApostaCarrinhoFavoritoDTO> listApostas;
    private DetalhesCarrinhoFavoritoActivity activity;
    private Long idCarrinho;

    public DetalhesApostasCarrinhoFavoritoAdapter(Long idCarrinho, ListApostaCarrinhoFavoritoDTO response, DetalhesCarrinhoFavoritoActivity activity){
        this.listApostas = response.getPayload();
        this.activity = activity;
        this.idCarrinho = idCarrinho;
    }

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
        ApostaCarrinhoFavoritoDTO apostaAtual = listApostas.get(position);
        EstiloModalidade estilo = new EstiloModalidade(ModalidadeEnum.fromModalidadeDTO(apostaAtual.getModalidade()));

        holder.tvModalidade.setText(apostaAtual.getModalidade().getDescricao());
        holder.tvModalidade.setTextColor(ContextCompat.getColor(activity, estilo.getCorClara()));
        holder.tvNumeros.setText(configuraNumeros(apostaAtual));

        if(apostaAtual.getQuantidadeTeimosinhas() > 0){
            holder.ivTeimosinha.setVisibility(View.VISIBLE);

        }

        if(apostaAtual.getModalidade().getDescricao().equalsIgnoreCase(activity.getResources().getString(R.string.dia_de_sorte))) {
            holder.tvMesDaSorte.setText(apostaAtual.getMesDeSorte().getNome());
            holder.tvMesDaSorte.setVisibility(View.VISIBLE);
            holder.tvMesDaSorte.setTextColor(ContextCompat.getColor(activity, estilo.getCorClara()));
        }
    }

    private String configuraNumeros(ApostaCarrinhoFavoritoDTO aposta){
        StringBuilder numeroStr = new StringBuilder();
        //Surpresinha
        if (aposta.getNumerosSelecionados().size() == 0) {
            for (int i = 0; i < aposta.getQuantidadeNumeros(); i++) {
                if (i == aposta.getQuantidadeNumeros() - 1) {
                    numeroStr.append(activity.getResources().getString(R.string.x_x));
                } else {
                    numeroStr.append(activity.getResources().getString(R.string.x_x_traco));
                }
            }
        } else {
            for (Integer num : aposta.getNumerosSelecionados()) {
                if (num.equals(aposta.getNumerosSelecionados().get(aposta.getNumerosSelecionados().size() - 1))) {
                    numeroStr.append(String.format(activity.getResources().getString(R.string.percent_zero_dois_d), num));
                } else {
                    numeroStr.append(String.format(activity.getResources().getString(R.string.percent_zero_dois_d_traco), num));
                }
            }
        }
        return numeroStr.toString();
    }

    @Override
    public int getItemCount() {
        return listApostas.size();
    }

    private void excluirAposta(Long idCarrinho, Long idAposta){
        ViewUtils.alertTitleButtonYesOrNotListener(activity, activity.getResources().getString(R.string.carrinho_fav_deseja_excluir_aposta), (dialogInterface, i) -> {
            if(listApostas.size() > 1){
                DialogUtils.show(activity);
                ApostaBO_.getInstance_(activity).deletaApostaCarrinhoFavorito(idCarrinho.toString(), idAposta.toString(), new RequestListener<ListApostaCarrinhoFavoritoDTO>() {
                    @Override
                    public void onResponse(ListApostaCarrinhoFavoritoDTO result) {
                        DialogUtils.dismiss();
                        if (result.getRedirect() != null) {
                            RedirectNetwork.checkRedirectSucesso(result.getRedirect(), activity);
                        }
                        listApostas = result.getPayload();
                        activity.setListApostaResponse(result);
                        activity.configuraRecyclerView(result);
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DialogUtils.dismiss();
                        RedirectNetwork.checkRedirect(error, activity);
                    }
                });
            }else {
                excluirCarrinho(idCarrinho);
            }
        });
    }

    private void excluirCarrinho(Long idCarrinhoAtual){
        DialogUtils.show(activity);
        ApostaBO_.getInstance_(activity).deletaCarrinhoFavorito(idCarrinhoAtual.toString(), new RequestListener<ListCarrinhoFavoritoDTO>() {
            @Override
            public void onResponse(ListCarrinhoFavoritoDTO result) {
                DialogUtils.dismiss();
                if (result.getRedirect() != null) {
                    RedirectNetwork.checkRedirectSucesso(result.getRedirect(), activity);
                }
                activity.setResult(RESPOSTA_EXCLUIU_CARRINHO);
                activity.finish();
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                DialogUtils.dismiss();
                RedirectNetwork.checkRedirect(error, activity);
            }
        });
    }


    class DetalheViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvModalidade, tvMesDaSorte, tvNumeros;
        private ImageButton btnExcluir;
        private ImageView ivTeimosinha;
        private Long idAposta;

        public DetalheViewHolder(View view) {
            super(view);
            tvModalidade = view.findViewById(R.id.tv_modalidade);
            tvMesDaSorte = view.findViewById(R.id.tv_mes_da_sorte);
            tvNumeros = view.findViewById(R.id.tv_numeros);
            btnExcluir = view.findViewById(R.id.ib_excluir);
            ivTeimosinha = view.findViewById(R.id.iv_teimosinha);
            btnExcluir.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            idAposta = listApostas.get(getAdapterPosition()).getId();
            switch (view.getId()){
                case R.id.ib_excluir:
                    excluirAposta(idCarrinho,idAposta);
                    break;
                default:
                    break;

            }
        }
    }
}
