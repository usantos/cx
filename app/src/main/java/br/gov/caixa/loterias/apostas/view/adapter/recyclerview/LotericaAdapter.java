package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;
import br.gov.caixa.loterias.apostas.view.holder.LotericaViewHolder;
import br.gov.caixa.loterias.apostas.view.listener.OnItemClickListener;

public class LotericaAdapter extends RecyclerView.Adapter<LotericaViewHolder> {

    private static List<LotericaDTO> listLotericaDTO;
    private OnItemClickListener onItemClickListener;

    public LotericaAdapter(List<LotericaDTO> listLotericaDTO, OnItemClickListener onItemClickListener) {
        this.listLotericaDTO = listLotericaDTO;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public LotericaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loterica, parent, false);
        return new LotericaViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LotericaViewHolder holder, int position) {
        holder.bind(listLotericaDTO.get(position), position);
    }

    @Override
    public int getItemCount() {
        return listLotericaDTO.size();
    }

    public void filterList(List<LotericaDTO> listLotericaDTOFiltrada) {
        listLotericaDTO = listLotericaDTOFiltrada;
        notifyDataSetChanged();
    }

}
