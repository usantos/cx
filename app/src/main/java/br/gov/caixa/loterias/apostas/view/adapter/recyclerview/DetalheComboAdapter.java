package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeComboDTO;
import br.gov.caixa.loterias.apostas.view.holder.DetalheComboHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class DetalheComboAdapter  extends RecyclerView.Adapter<DetalheComboHolder>{

    private final List<ModalidadeComboDTO> modalidades;
    private final Activity parentActivity;

    public DetalheComboAdapter(List<ModalidadeComboDTO> modalidades, Activity parentActivity) {
        this.modalidades = modalidades;
        this.parentActivity = parentActivity;
    }

    @NonNull
    @Override
    public @NotNull DetalheComboHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_detalhe_combo, viewGroup, false);
        return new DetalheComboHolder(view, parentActivity);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DetalheComboHolder detalheComboHolder, int position) {
        detalheComboHolder.bind(modalidades.get(position), position);

    }

    @Override
    public int getItemCount() {
        return modalidades.size();
    }
}
