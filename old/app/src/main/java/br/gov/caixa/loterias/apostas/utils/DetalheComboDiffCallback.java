package br.gov.caixa.loterias.apostas.utils;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeComboDTO;
import org.jetbrains.annotations.NotNull;

public class DetalheComboDiffCallback extends DiffUtil.ItemCallback<ModalidadeComboDTO> {

    @Override
    public boolean areItemsTheSame(@NonNull @NotNull ModalidadeComboDTO oldItem, @NonNull @NotNull ModalidadeComboDTO newItem) {
        return oldItem.getIdentificadorUnico().equals(newItem.getIdentificadorUnico());
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull ModalidadeComboDTO oldItem, @NonNull @NotNull ModalidadeComboDTO newItem) {
        return oldItem.equals(newItem);
    }
}
