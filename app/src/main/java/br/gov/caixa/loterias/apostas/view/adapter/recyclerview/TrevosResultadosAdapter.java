package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostas.R;
import br.gov.caixa.loterias.apostas.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostas.utils.EstiloModalidadeMKP;

/**
 * Created by cedesbr450 on 27/03/18.
 */

public final class TrevosResultadosAdapter extends RecyclerView.Adapter<TrevosResultadosAdapter.ViewHolder> {


    private final Context context;
    private final List<Integer> numeroslista;
    private final ModalidadeEnum modalidade;

    public TrevosResultadosAdapter(final Context context, final List<Integer> numeroslista, final ModalidadeEnum modalidade) {
        this.context = context;
        this.numeroslista = numeroslista;
        this.modalidade = modalidade;
        if(numeroslista != null){
            Collections.sort(this.numeroslista);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.linearlayout_numeros_resultados, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        EstiloModalidadeMKP estilo = new EstiloModalidadeMKP(modalidade);
        int numeroResultado = numeroslista.get(position);
        holder.textViewTituloNumerosResultados.setText(numeroResultado < 10 ? context.getResources().getString(R.string.zero) + numeroResultado : context.getResources().getString(R.string.string_vazia) + numeroResultado);
        holder.textViewTituloNumerosResultados.setTextColor(context.getResources().getColor(estilo.getCorClara()));

        Drawable background = context.getResources().getDrawable(R.drawable.ic_item_trevo_selecionado);

        holder.linearLayoutContentNumerosResultados.setBackground(background);
    }

    @Override
    public int getItemCount() {
        return numeroslista != null ? numeroslista.size() : 0;
    }

    public List<Integer> getNumeros() {
        return Collections.unmodifiableList(numeroslista);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView textViewTituloNumerosResultados;
        protected LinearLayout linearLayoutContentNumerosResultados;

        public ViewHolder(final View itemView) {
            super(itemView);
            textViewTituloNumerosResultados = itemView.findViewById(R.id.textViewTituloNumerosResultados);
            linearLayoutContentNumerosResultados = itemView.findViewById(R.id.linearLayoutContentNumerosResultados);
        }
    }
}