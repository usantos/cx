package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.CombosDTO;
import br.gov.caixa.loterias.apostas.view.holder.ListaComboHolder;
import br.gov.caixa.loterias.apostas.view.listener.onComboClickListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListaComboAdapter extends RecyclerView.Adapter<ListaComboHolder> {

    private final List<CombosDTO> listaCombos;
    private final Activity parentActivity;
    private final onComboClickListener listener;

    public ListaComboAdapter(List<CombosDTO> listaCombos, Activity parentActivity, onComboClickListener listener) {
        this.listaCombos = new ArrayList<>(listaCombos);
        this.parentActivity = parentActivity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public @NotNull ListaComboHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lista_combos, viewGroup, false);

        return new ListaComboHolder(view, parentActivity, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ListaComboHolder listaComboHolder, int position) {
        listaComboHolder.bind(listaCombos.get(position), position);
    }

    @Override
    public int getItemCount() {
        return listaCombos.size();
    }

}
