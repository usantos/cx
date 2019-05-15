package br.gov.caixa.loterias.apostar.interno.view.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import br.gov.caixa.loterias.apostar.interno.R;
import br.gov.caixa.loterias.apostar.interno.model.bo.silce.dto.ModalidadeEnum;
import br.gov.caixa.loterias.apostar.interno.utils.EstiloModalidade;
import br.gov.caixa.loterias.apostar.interno.utils.ViewUtils;

/**
 * Created by cedesbr450 on 27/03/18.
 */

public final class NumerosResultadosAdapter extends RecyclerView.Adapter<NumerosResultadosAdapter.ViewHolder> {


    private final Context context;
    private final List<Integer> numeroslista;
    private final ModalidadeEnum modalidade;

    public NumerosResultadosAdapter(final Context context, final List<Integer> numeroslista, final ModalidadeEnum modalidade) {
        this.context = context;
        this.numeroslista = numeroslista;
        this.modalidade = modalidade;
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
        EstiloModalidade estilo = new EstiloModalidade(modalidade);
        int numeroResultado = numeroslista.get(position);
        holder.textViewTituloNumerosResultados.setText(numeroResultado < 10 ? "0" + numeroResultado : "" + numeroResultado);
        holder.textViewTituloNumerosResultados.setTextColor(context.getResources().getColor(estilo.getMenuItemColor()));
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(context.getResources().getColor(estilo.getCorClara()));
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setSize(ViewUtils.setDisplayMetric(40, context), ViewUtils.setDisplayMetric(40, context));
        holder.linearLayoutContentNumerosResultados.setBackground(drawable);
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
            textViewTituloNumerosResultados = (TextView) itemView.findViewById(R.id.textViewTituloNumerosResultados);
            linearLayoutContentNumerosResultados = (LinearLayout) itemView.findViewById(R.id.linearLayoutContentNumerosResultados);
        }
    }

}