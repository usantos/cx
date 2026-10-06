package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeComboDTO;
import br.gov.caixa.loterias.apostas.utils.DetalheComboDiffCallback;
import br.gov.caixa.loterias.apostas.view.holder.DetalheComboHolder;
import org.jetbrains.annotations.NotNull;

public class DetalheComboAdapter extends ListAdapter<ModalidadeComboDTO, DetalheComboHolder> {

    public DetalheComboAdapter() {
        super(new DetalheComboDiffCallback());
    }

    @NonNull
    @Override
    public @NotNull DetalheComboHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_detalhe_combo, viewGroup, false);
        return new DetalheComboHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DetalheComboHolder detalheComboHolder, int position) {
        ModalidadeComboDTO item = getItem(position);
        detalheComboHolder.bind(item);
    }

}
