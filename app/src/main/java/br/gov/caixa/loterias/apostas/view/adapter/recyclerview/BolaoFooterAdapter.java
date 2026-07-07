package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.gov.caixa.loterias.apostas.R;

public class BolaoFooterAdapter extends RecyclerView.Adapter<BolaoFooterAdapter.FooterViewHolder> {

    private boolean mostrarLoading;
    private boolean mostrarFimLista;

    public void mostrarLoading(boolean mostrar) {
        this.mostrarLoading = mostrar;
        notifyItemChanged(0);
    }

    public void mostrarFimLista(boolean mostrar) {
        this.mostrarFimLista = mostrar;
        notifyItemChanged(0);
    }

    @NonNull
    @Override
    public FooterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_footer_boloes, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FooterViewHolder holder, int position) {
        holder.progressBar.setVisibility(mostrarLoading ? View.VISIBLE : View.GONE);
        holder.txtFimLista.setVisibility(mostrarFimLista ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        TextView txtFimLista;

        FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_loading);
            txtFimLista = itemView.findViewById(R.id.id_fim_lista);
        }
    }
}
