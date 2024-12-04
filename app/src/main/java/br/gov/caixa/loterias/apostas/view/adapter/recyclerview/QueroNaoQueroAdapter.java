package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.view.holder.QueroNaoQueroViewHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;


public class QueroNaoQueroAdapter extends RecyclerView.Adapter<QueroNaoQueroViewHolder>{
    private List<Integer> dezenasSelecionadas;
    private final OnItemClickListener listener;

    public QueroNaoQueroAdapter(List<Integer> dezenasSelecionadas, OnItemClickListener listener){
        this.dezenasSelecionadas = dezenasSelecionadas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QueroNaoQueroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dezena_quero_nao_quero,
                parent, false);
        return new QueroNaoQueroViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull QueroNaoQueroViewHolder holder, int position) {

        Integer dezena = dezenasSelecionadas.get(position);
        holder.bind(dezena, position);
    }

    @Override
    public int getItemCount() {
        return dezenasSelecionadas.size();
    }

    public void atualizaSelecionados(List<Integer> selecionados) {
        this.dezenasSelecionadas = selecionados;
        notifyDataSetChanged();
    }

}

