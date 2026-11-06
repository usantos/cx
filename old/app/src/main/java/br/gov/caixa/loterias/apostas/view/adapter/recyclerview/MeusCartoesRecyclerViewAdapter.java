package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.RetornoCartao;
import br.gov.caixa.loterias.apostas.view.holder.MeuCartaoHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnMeusCartoesListener;

public class MeusCartoesRecyclerViewAdapter extends RecyclerView.Adapter<MeuCartaoHolder> {

    public List<RetornoCartao> lista;
    private Context context;
    private OnMeusCartoesListener listener;

    public MeusCartoesRecyclerViewAdapter(List<RetornoCartao> cartoes, Context context, OnMeusCartoesListener listener){
        this.context = context;
        this.lista = cartoes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MeuCartaoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_meu_cartao, parent, false);
        return new MeuCartaoHolder(view, context, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MeuCartaoHolder holder, int position) {
        if (lista.size() <= 1){
            holder.showButtonFav(false);
            if (lista.size() == 1){
                lista.get(0).setFav(false);
            }
        } else {
            holder.showButtonFav(false);
        }

        RetornoCartao cartao = lista.get(position);
        holder.bind(cartao, position);
    }

    @Override
    public int getItemCount() {
        return lista == null ? 0 : lista.size();
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
}
