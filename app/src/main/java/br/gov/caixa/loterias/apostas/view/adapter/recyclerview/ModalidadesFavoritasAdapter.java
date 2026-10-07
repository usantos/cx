package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;

public final class ModalidadesFavoritasAdapter extends RecyclerView.Adapter<ModalidadesFavoritasAdapter.Holder> {
    public interface OnRemoveListener {
        void onRemove(ModalidadeEnum modalidade);
    }
    public interface OnSelectListener {
        void onSelect(ModalidadeEnum modalidade);
    }

    private final List<ModalidadeEnum> favorites = new ArrayList<>();
    private final OnRemoveListener listener;
    private final OnSelectListener onSelect;

    public ModalidadesFavoritasAdapter(OnRemoveListener listener, OnSelectListener onSelect) {
        this.listener = listener;
        this.onSelect = onSelect;
    }

    public void setFavorites(List<ModalidadeEnum> items) {
        favorites.clear();
        favorites.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_modalidade_favorita, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ModalidadeEnum modalidade = favorites.get(position);
        String name = ModalidadeEnum.getDescricao(modalidade);
        holder.name.setText(name);
        int logo = new EstiloModalidadeMKP(modalidade).getTrevoFundoClaro();
        holder.logo.setImageResource(logo > 0 ? logo : R.drawable.trevo);
        holder.itemView.setOnClickListener(view -> onSelect.onSelect(modalidade));
        holder.remove.setContentDescription(holder.itemView.getContext().getString(R.string.modalidade_favorita_excluir, name));
        holder.remove.setOnClickListener(view -> listener.onRemove(modalidade));
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView logo;
        final ImageButton remove;

        Holder(View view) {
            super(view);
            name = view.findViewById(R.id.nomeModalidadeFavorita);
            logo = view.findViewById(R.id.logoModalidadeFavorita);
            remove = view.findViewById(R.id.excluirModalidadeFavorita);
            ViewCompat.setAccessibilityHeading(name, true);
        }
    }
}
