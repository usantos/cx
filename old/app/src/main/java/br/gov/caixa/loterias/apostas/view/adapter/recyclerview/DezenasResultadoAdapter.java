package br.gov.caixa.loterias.apostas.view.adapter.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.gov.caixa.loterias.apostas.R;

/**
 * Created by joafilho on 28/03/2018.
 * Class DezenasAdapter
 */

public class DezenasResultadoAdapter extends RecyclerView.Adapter<DezenasResultadoAdapter.ViewHolder> {

    private List<View> dezenas;

    public DezenasResultadoAdapter(List<View> dezenas) {
        this.dezenas = dezenas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_resultado_dezenas, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position == 0) {
            holder.dezenaView.findViewById(R.id.lineDivisiveView).setVisibility(View.GONE);
        }
        ((LinearLayout) holder.dezenaView.findViewById(R.id.lineDezenasLienarLayout)).addView(dezenas.get(position));
    }

    @Override
    public int getItemCount() {
        return dezenas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private View dezenaView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.dezenaView = itemView;
        }
    }
}
