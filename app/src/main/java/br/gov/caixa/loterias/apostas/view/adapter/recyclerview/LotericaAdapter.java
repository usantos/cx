package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.LotericaDTO;

public class LotericaAdapter extends RecyclerView.Adapter<LotericaAdapter.LotericaViewHolder> {

    private static List<LotericaDTO> listLotericaDTO;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(LotericaDTO lotericaDTO);
    }

    public LotericaAdapter(List<LotericaDTO> listLotericaDTO, OnItemClickListener onItemClickListener) {
        this.listLotericaDTO = listLotericaDTO;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public LotericaAdapter.LotericaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loterica, parent, false);
        return new LotericaViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LotericaAdapter.LotericaViewHolder holder, int position) {
        holder.textView.setText(listLotericaDTO.get(position).getNome().trim());
    }

    @Override
    public int getItemCount() {
        return listLotericaDTO.size();
    }

    static class LotericaViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        LotericaViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener) {
            super(itemView);
            textView = itemView.findViewById(R.id.textNomeLoterica);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(listLotericaDTO.get(position));
                        }
                    }
                }
            });
        }
    }

    public void filterList(List<LotericaDTO> listLotericaDTOFiltrada) {
        listLotericaDTO = listLotericaDTOFiltrada;
        notifyDataSetChanged();
    }

}
