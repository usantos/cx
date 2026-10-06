package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaComposition;
import br.gov.caixa.loterias.apostas.view.holder.BuscaLotericaViewHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class BuscaLotericaAdapter extends RecyclerView.Adapter<BuscaLotericaViewHolder> {

    private static List<LotericaComposition> listLotericaDTO;
    private OnItemClickListener onItemClickListener;

    public BuscaLotericaAdapter(List<LotericaComposition> listLotericaDTO, OnItemClickListener onItemClickListener) {
        this.listLotericaDTO = listLotericaDTO;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public BuscaLotericaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buscar_favoritas, parent, false);
        return new BuscaLotericaViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull BuscaLotericaViewHolder holder, int position) {
        holder.bind(listLotericaDTO.get(position), position);
    }

    @Override
    public int getItemCount() {
        return listLotericaDTO.size();
    }

    public void submitList(List<LotericaComposition> listLotericaDTOFiltrada) {
        listLotericaDTO = listLotericaDTOFiltrada;
        notifyDataSetChanged();
    }

}
