package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.model.ItemLoteria;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;

public class PremiacaoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_TITULO = 0;
    private static final int TYPE_CORPO = 1;
    private static final int TYPE_DIVIDER = 2;

    private static EstiloModalidadeMKP estiloModalidadeMKP;

    private List<ItemLoteria> itens = new ArrayList<>();

    public void submitList(List<ItemLoteria> nList) {
        this.itens = nList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (itens.get(position) instanceof ItemLoteria.Titulo) return TYPE_TITULO;
        if (itens.get(position) instanceof ItemLoteria.Divider) return TYPE_DIVIDER;
        return TYPE_CORPO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_TITULO) {
            View view = inflater.inflate(R.layout.layout_item_header, parent, false);
            return new TituloViewHolder(view);
        } else if (viewType == TYPE_DIVIDER) {
            View view = inflater.inflate(R.layout.layout_item_divider, parent, false);
            view.setBackgroundColor(ContextCompat.getColor(parent.getContext(), estiloModalidadeMKP.getCorFonteFundoClaro()));
            return new DividerViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.layout_item_corpo, parent, false);
            return new CorpoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TituloViewHolder) {
            ((TituloViewHolder) holder).bind((ItemLoteria.Titulo) itens.get(position));
        } else if (holder instanceof CorpoViewHolder) {
            ((CorpoViewHolder) holder).bind((ItemLoteria.Corpo) itens.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public void submitColor(EstiloModalidadeMKP estiloModalidadeMKP) {
        PremiacaoAdapter.estiloModalidadeMKP = estiloModalidadeMKP;
    }

    static class DividerViewHolder extends RecyclerView.ViewHolder {
        public DividerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class TituloViewHolder extends RecyclerView.ViewHolder {
        TextView tvConcurso;

        public TituloViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvConcurso = itemView.findViewById(R.id.tvContestTitle);
            tvConcurso.setTextColor(ContextCompat.getColor(itemView.getContext(), estiloModalidadeMKP.getCorFonteFundoClaro()));
        }

        void bind(ItemLoteria.Titulo item) {
            tvConcurso.setText(item.getConcurso());
        }
    }

    static class CorpoViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvConcurrency;

        public CorpoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvConcurrency = itemView.findViewById(R.id.tvConcurrency);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvConcurrency.setTextColor(ContextCompat.getColor(itemView.getContext(), estiloModalidadeMKP.getCorFonteFundoClaro()));
            tvDescription.setTextColor(ContextCompat.getColor(itemView.getContext(), estiloModalidadeMKP.getCorFonteFundoClaro()));
        }

        void bind(ItemLoteria.Corpo item) {
            tvDescription.setText(item.getDescricao());
            tvConcurrency.setText(item.getValor());
        }

    }
}
