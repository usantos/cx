package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bean.TipoAposta;
import br.gov.caixa.loterias.apostas.view.holder.TipoApostaHolder;
import br.gov.caixa.loterias.apostas.view.listener.TipoApostaListener;

public class TipoApostaMultiplaSelecaoAdapter extends RecyclerView.Adapter<TipoApostaHolder> {
    private List<TipoAposta> tiposAposta;
    private TipoApostaListener listener;

    public TipoApostaMultiplaSelecaoAdapter(List<TipoAposta> tiposAposta, TipoApostaListener listener){
        this.tiposAposta = tiposAposta;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TipoApostaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.linearlayout_tipos_apostas, parent, false);
        return new TipoApostaHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TipoApostaHolder holder, int position) {
        holder.bind(tiposAposta.get(position), position);
    }

    @Override
    public int getItemCount() {
        return tiposAposta.size();
    }

}
