package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.DuvidaDTO;
import br.gov.caixa.loterias.apostas.view.holder.DetalheDuvidaHolder;


/**
 * Created by joafilho on 16/02/2018.
 * Class DetalheDuvidaAdapter
 */

public class DetalheDuvidaAdapter extends RecyclerView.Adapter<DetalheDuvidaHolder> {

    private List<DuvidaDTO> duvidasDetalhe;

    public DetalheDuvidaAdapter(List<DuvidaDTO> duvidasDetalhe) {
        this.duvidasDetalhe = duvidasDetalhe;
    }

    @Override
    public DetalheDuvidaHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //ItemDuvidaDetalheView view = ItemDuvidaDetalheView_.build(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_duvida_detalhe, parent, false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new DetalheDuvidaHolder(view);
    }

    @Override
    public void onBindViewHolder(DetalheDuvidaHolder holder, int position) {
        holder.bind(duvidasDetalhe.get(position), position);
    }

    @Override
    public int getItemCount() {
        return duvidasDetalhe.size();
    }

}